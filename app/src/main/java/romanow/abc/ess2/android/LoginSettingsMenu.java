package romanow.abc.ess2.android;

import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.subjectarea.AccessManager;
import romanow.abc.core.entity.subjectarea.WorkSettings;
import romanow.abc.core.entity.users.Account;
import romanow.abc.core.entity.users.User;
import romanow.abc.ess2.android.service.AppData;
import romanow.abc.ess2.android.service.NetBack;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class LoginSettingsMenu extends SettingsMenuBase {
    public LoginSettingsMenu(MainActivity base0){
        super(base0);
        }
    private AppData ctx;
    @Override
    public void settingsSave() {
        ctx.fileService().saveJSON(ctx.loginSettings());
        }

    @Override
    public void createDialog(LinearLayout trmain) {
        ctx = AppData.ctx();
        try {
            final LoginSettings set = AppData.ctx().loginSettings();
            String ipList[]={"217.71.138.9","217.71.138.13","192.168.0.225","10.32.0.2","10.30.0.2"};
            LinearLayout layout = createItem("IP", set.getDataSetverIP(), true,true,ipList,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setDataSetverIP(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            String portList[]={"4567","4569","5001"};
            layout = createItem("Порт", ""+set.getDataServerPort(), false,false,portList,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setDataServerPort(Integer.parseInt(ss));
                        settingsChanged();
                        } catch (Exception ee){
                           base.popupInfo("Формат числа");}
                            }
                    });
            trmain.addView(layout);
            layout = createItem("Телефон", set.getUserPhone(),true,false,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPhone(ss);
                    settingsChanged();
                    }});
            trmain.addView(layout);
            layout = createItem("Пароль", "******", true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPass(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            final boolean isLogin = ctx.cState()== AppData.CStateGreen;
            layout = createItem((!isLogin ? "Войти" : "Выйти"), "",true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    final SettingsMenuBase pp = LoginSettingsMenu.this;
                    if (!isLogin){
                        base.retrofitConnect();
                        Account acc = new Account("",set.getUserPhone(), set.getUserPass());
                        new NetCall<User>().call(base,ctx.getService().login(acc), new NetBack(){
                            @Override
                            public void onError(int code, String mes) {
                                if (code == Values.HTTPAuthorization)
                                    ctx.toLog(false,"Ошибка авторизации: "+mes+"");
                                else if (code==Values.HTTPNotFound)
                                    ctx.toLog(false,"Ошибка соединения: "+mes+"");
                                else
                                    ctx.toLog(false,mes);
                                }
                            @Override
                            public void onError(UniException ee) {
                                ctx.popupToastFatal(ee);
                                }
                            @Override
                            public void onSuccess(Object val) {
                                base.sessionOn();
                                User user =(User)val;
                                final LoginSettings set = ctx.loginSettings();
                                set.setUserId(user.getOid());
                                set.setSessionToken(user.getSessionToken());
                                base.putHeaderInfo(set.getDataSetverIP()+"\n"+user.shortUserName()+"\n"+user.typeName());
                                new NetCall<DBRequest>().call(base,ctx.getService().workSettings(ctx.loginSettings().getSessionToken()), new NetBackDefault() {
                                    @Override
                                    public void onSuccess(Object val) {
                                        try {
                                            ctx.workSettings((WorkSettings)((DBRequest)val).get(new Gson()));
                                            ctx.setRegisteredOnServer(true);
                                            base.getArchitectureData().refreshArchtectureState(new AccessManager(user));
                                            } catch (UniException e) {
                                                base.errorMes("Загрузка параметров сервера:\n"+e.toString());
                                                }
                                            }
                                        });
                                /*---------------------------- Регистрация на сервере не нужна -----------------
                                String serverSim = user.getSimCardICC();
                                String regCode = base.createRegistrationCode();
                                if (serverSim.length()==0){
                                    user.setSimCardICC(regCode);
                                    //--------------------------------------------------------------
                                    new NetCall<JEmpty>().call(base,ctx.getService().updateEntityField(set.getSessionToken(),"simCardICC",
                                            new DBRequest(user,new Gson())), new NetBack(){
                                        @Override
                                        public void onError(int code, String mes) {
                                            ctx.toLog(true,""+code+": "+mes);
                                            ctx.toLog(true,"Не зарегистрирован на сервере");
                                            ctx.setRegisteredOnServer(false);
                                            }
                                        @Override
                                        public void onError(UniException ee) {
                                            ctx.popupToastFatal(ee);
                                            ctx.toLog(true,"Не зарегистрирован на сервере");
                                            ctx.setRegisteredOnServer(false);
                                            }
                                        @Override
                                        public void onSuccess(Object val) {
                                            ctx.toLog(true,"Зарегистрирован на сервере");
                                            ctx.setRegisteredOnServer(true);
                                            }
                                        });
                                    //--------------------------------------------------------------
                                    }
                                else{
                                    if (serverSim.equals(regCode)){
                                        ctx.toLog(true,"Зарегистрирован на сервере");
                                        ctx.setRegisteredOnServer(true);
                                        }
                                    else{
                                        ctx.toLog(true,"Другой код регистрации на сервере");
                                        ctx.setRegisteredOnServer(false);
                                        }
                                    }
                                    */
                               }
                            });
                        pp.cancel();
                        }
                    else{
                        base.getArchitectureData().clearDeployedMetaData();
                        new NetCall<JEmpty>().call(base,ctx.getService().logoff(ctx.loginSettings().getSessionToken()), new NetBackDefault(){
                            @Override
                            public void onSuccess(Object val) {
                                base.sessionOff();
                                ctx.cState(AppData.CStateGray);
                                }
                            });
                        }
                    pp.cancel();
                    }
                });
            trmain.addView(layout);
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
}

