package romanow.abc.ess2.android.service;

import static romanow.abc.core.Utils.httpError;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import romanow.abc.core.DBRequest;
import romanow.abc.core.Utils;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.artifacts.Artifact;
import romanow.abc.core.entity.metadata.Meta2Equipment;
import romanow.abc.core.entity.metadata.Meta2GUIView;
import romanow.abc.core.entity.metadata.Meta2XML;
import romanow.abc.core.entity.metadata.Meta2XStream;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.entity.subject2area.ESS2Equipment;
import romanow.abc.core.entity.subject2area.ESS2MetaFile;
import romanow.abc.core.entity.subject2area.ESS2ScriptFile;
import romanow.abc.core.entity.subject2area.ESS2View;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.CompileError;
import romanow.abc.core.script.FunctionCode;
import romanow.abc.core.script.Scaner;
import romanow.abc.core.script.Syntax;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.I_DownLoadString;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.ModBusClientProxyDriver;
import romanow.abc.ess2.android.R;

public class ArchitectureData {
    public final static int archStateIcons[]={
            R.drawable.settings_gray,
            R.drawable.settings_red,
            R.drawable.settings_yellow,
            R.drawable.settings_green,
            R.drawable.settings_green,
            R.drawable.settings_green
            };
    public final static int connStateIcons[]={
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_red,
            R.drawable.status_green,
            };
    private MainActivity base;
    private ImageView deployState;
    private ImageView connectState;
    private ImageView renderState;
    private TextView deployStateText;
    private TextView connectStateText;
    private TextView renderStateText;
    private Button formMenuButton;
    private AppData ctx;
    private ESS2Architecture deployed=null;
    private ESS2View currentView=null;                  // Текущий вид
    private String debugToken;
    public ArchitectureData(MainActivity main0){
        base = main0;
        ctx = AppData.ctx();
        deployState = (ImageView) base.findViewById(R.id.headerDeployState);
        connectState = (ImageView) base.findViewById(R.id.headerConnectState);
        renderState = (ImageView) base.findViewById(R.id.headerRenderState);
        deployStateText = (TextView) base.findViewById(R.id.headerDeployStateText);
        connectStateText = (TextView) base.findViewById(R.id.headerConnectStateText);
        renderStateText = (TextView) base.findViewById(R.id.headerRenderStateText);
        formMenuButton = (Button) base.findViewById(R.id.headerRenderMenu);
        renderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOffActionPerformed();
                }
            });
        }
    //------------------------------------------------------------------------------------------------
    public void refreshArchtectureState(){
        if (ctx.cState()!=AppData.CStateGreen){
            deployState.setImageResource(archStateIcons[0]);
            connectState.setImageResource(connStateIcons[0]);
            return;
            }
        new NetCall<Pair<Integer,Long>>().call(base,ctx.getService2().getArchitectureState(ctx.loginSettings().getSessionToken()), new NetBackDefault(){
            @Override
            public void onSuccess(Object vv) {
                Pair<Integer,Long> val = (Pair<Integer,Long>)vv;
                int state = val.o1;
                deployState.setImageResource(archStateIcons[state]);
                connectState.setImageResource(connStateIcons[state]);
                base.addToLog("Состояние "+ Values.constMap().getGroupMapByValue("ArchState").get(state).title());
                if (state==Values.ASNotDeployed)
                    return;
                long oid = val.o2;
                int idx=-1;
                /*
                for(int i=0;i<architectures.size();i++)
                    if (architectures.get(i).getOid()==oid){
                        idx=i;
                        break;
                    }
                if (idx==-1){
                    popup("Не найдена развернутая архитектура, oid="+oid);
                    return;
                    }
                */
                loadDeployedArchitecture(oid, state);
                deployed.setArchitectureState(state);
                }
            });
        }
    //-------------------------------------------------------------------------------------------------------------------
    private void loadDeployedArchitecture(long oid, int state) {
        deployed = loadFullArchitecture(oid);
        deployed.setArchitectureState(state);
        deployed.testFullArchitecture();
        ModBusClientProxyDriver driver = new ModBusClientProxyDriver();
        Object oo[]={base};
        try {
            driver.openConnection(oo,null);
            for (ESS2Equipment equipment : deployed.getEquipments()) {
                equipment.createFullEquipmentPath();
                }
            for(ESS2Device device : deployed.getDevices()){         // НАстроить прокси
                device.setDriver(driver);
                }
        } catch (Exception ee){
            base.addToLog("Ошибка драйвера БД: "+ee.toString());
            }
        refreshDeployedMetaData();
        deployed.createStreamRegisterList();
        //refreshSelectedArchitecture();
        }
    //-----------------------------------------------------------------------------------------------------------
    public ESS2Architecture loadFullArchitecture(long oid){
        ESS2Architecture arch = new ESS2Architecture();
        try {
            Response<DBRequest> res = ctx.service.getEntity(debugToken,"ESS2Architecture",oid,4).execute();
            if (!res.isSuccessful()){
                if (res.code()== ValuesBase.HTTPAuthorization)
                    arch.addErrorData("Сеанс закрыт " + Utils.httpError(res));
                else
                    arch.addErrorData("Ошибка " + res.message()+" ("+res.code()+") "+res.errorBody().string());
                return arch;
            }
            DBRequest request = res.body();
            arch = (ESS2Architecture)  request.get(new Gson());
            Artifact artifact;
            for(ESS2Equipment equipment : arch.getEquipments()){
                ESS2MetaFile metaFile = equipment.getMetaFile().getRef();
                artifact = metaFile.getFile().getRef();
                Meta2XML xml = loadXMLArtifact(artifact);
                arch.addErrorData(xml);
                equipment.setEquipment((Meta2Equipment) xml);
            }
            for(ESS2View view : arch.getViews()){
                ESS2MetaFile metaFile = view.getMetaFile().getRef();
                artifact = metaFile.getFile().getRef();
                Meta2XML xml = loadXMLArtifact(artifact);
                arch.addErrorData(xml);
                view.setView((Meta2GUIView)xml);
            }
            return arch;
        } catch (Exception ex) {
            arch.addErrorData(Utils.createFatalMessage(ex));
            return arch;
            }
        }
    //------------------------------------------------------------------------------------------------------------
    public Meta2XML loadXMLArtifact(Artifact art){
        Meta2XML entity = new Meta2XML();
        Pair<String,String> vv = loadFileAsStringSync(art);
        if (vv.o1!=null) {
            entity.addErrorData(vv.o1);
        }
        else{
            entity = (Meta2XML) new Meta2XStream().fromXML(vv.o2);
            System.out.println(entity.getTitle());
            entity.setHigh(null);
            entity.createMap();
            entity.testLocalConfiguration();
            }
        return entity;
        }
    //----------------------------------------------------------------------------------------------------------
    public Pair<String,String> loadFileAsStringSync(Artifact art){
        Call<ResponseBody> call2 = ctx.service.downLoad(debugToken,art.getOid());
        try {
            Response<ResponseBody> bbody = call2.execute();
            if (!bbody.isSuccessful()) {
                String mes = httpError(bbody);
                return new Pair<>(mes, null);
            }
            ResponseBody body = bbody.body();
            long fileSize = body.contentLength();
            InputStream in = body.byteStream();
            InputStreamReader reader = new InputStreamReader(in,"UTF8");
            StringBuffer buffer = new StringBuffer();
            int cc;
            while ((cc=reader.read())!=-1){
                buffer.append((char) cc);
                }
            reader.close();
            return new Pair<>(null,buffer.toString());
            } catch (IOException ee) {
                String mes = Utils.createFatalMessage(ee);
                return new Pair<>(mes,null);
                }
            }
    //---------------------------------------------------------------------------------------------------------------------
    private void refreshDeployedMetaData(){
        if (deployed==null)
            return;
        int state = deployed.getArchitectureState();
        deployStateText.setText(deployed.getFullTitle());
        deployState.setImageResource(archStateIcons[state]);
        connectState.setImageResource(connStateIcons[state]);
        if (!deployed.isDeployed())
            return;
        setRenderingOff();
        }
    //---------------------------------------------------------------------------------------------------------------------------
    private void clearDeployedMetaData(){
        deployStateText.setText("Архитектура не выбрана");
        deployState.setImageResource(archStateIcons[0]);
        connectState.setImageResource(connStateIcons[0]);
        deployed = null;
        //main2.currentView = null;
        }
    //---------------------------------------------------------------------------------------------------------------------------
    public void setRenderingOff(){
        currentView=null;
        renderState.setImageResource(R.drawable.connect_off);
        //main.sendEvent(EventPLMOff,0);
         }
    public void setRenderingOn() {
        currentView = null;
        for(ESS2View view : deployed.getViews()){
            if (view.getMetaFile().getRef().getMetaType()==Values.MTViewAndroid){
                currentView = view;
                break;
                }
            }
        if (currentView==null){
            base.errorMes("Не найден ЧМИ для Android");
            return;
            }
        renderState.setImageResource(R.drawable.connect_on);
        //main.sendEvent(EventPLMOn,0);
        preCompileLocalScripts();
        }
    private void OnOffActionPerformed() {//GEN-FIRST:event_OnOffActionPerformed
        if (currentView!=null){
            setRenderingOff();
            }
        else{
            if (!deployed.isConnected())
                return;
            setRenderingOn();
            }
        }
    //------------------------------------------------------------------------------------------------------------------------
    private void preCompileLocalScripts(){
        for(ESS2ScriptFile scriptFile : deployed.getScripts()){
            if (!scriptFile.isServerScript() && scriptFile.isPreCompiled() && scriptFile.getScriptType()==Values.STCalcClient){
                loadFileAsString(scriptFile.getFile().getRef(), new I_DownLoadString() {
                    @Override
                    public void onSuccess(String ss) {
                        Syntax SS = compileScriptLocal(scriptFile, ss);
                        boolean res = SS.getErrorList().size()==0;
                        if (res)
                            scriptFile.setScriptCode(new CallContext(SS, deployed));
                        scriptFile.setValid(res);
                        base.errorMes("Скрипт " + scriptFile.getShortName() + " " + scriptFile.getTitle() + (res ? "" : " не")+" скомпилировался");
                        }
                    @Override
                    public void onError(String mes) {
                        base.errorMes("Скрипт " + scriptFile.getShortName() + " " + scriptFile.getTitle() + " не загрузился");
                        base.errorMes(mes);
                    }
                });
            }
            else{
                scriptFile.setScriptCode(null);
                scriptFile.setValid(false);
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void loadFileAsString(Artifact art,final I_DownLoadString back){
        Call<ResponseBody> call2 = ctx.service.downLoad(debugToken,art.getOid());
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    long fileSize = body.contentLength();
                    InputStream in = body.byteStream();
                    try {
                        InputStreamReader reader = new InputStreamReader(in,"UTF8");
                        StringBuffer buffer = new StringBuffer();
                        int cc;
                        while ((cc=reader.read())!=-1){
                            buffer.append((char) cc);
                            }
                        reader.close();
                        if (back!=null)
                            back.onSuccess(buffer.toString());
                        else
                            base.addToLog("Текст загружен\n"+buffer.toString());
                        } catch (IOException ee) {
                            String mes = Utils.createFatalMessage(ee);
                            if (back!=null)
                                back.onError(mes);
                            else
                                base.errorMes(mes);
                                }
                        }
                else{
                    String mes = httpError(response);
                    if (back!=null)
                        back.onError(mes);
                    else
                        base.errorMes(mes);
                        }
                    }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String mes = Utils.createFatalMessage(t);
                if (back!=null)
                    back.onError(mes);
                else
                    base.errorMes(mes);
                }
            });
        }
    //------------------------------------------------------------------------------------------------------------------------
    public Syntax compileScriptLocal(ESS2ScriptFile scriptFile,String src){
        ArrayList<String> lines = new ArrayList<>();
        while(true){
            int idx=src.indexOf('\n');
            if (idx==-1){
                lines.add(src);
                lines.add("#");
                break;
                }
            lines.add(src.substring(0,idx-1));
            src = src.substring(idx+1);
            }
        Scaner lex = new Scaner();
        lex.open(lines);
        Syntax SS = new Syntax(lex) {
            @Override
            public void createFunctionMap() {
                createFunctionMap(Values.constMap().getGroupList("ScriptFun"));
                }
            };
        FunctionCode ff = SS.compile();
        if (SS.getErrorList().size()!=0)
            base.errorMes("errors: "+SS.getErrorList().size());
        for(CompileError error : SS.getErrorList())
            base.errorMes(error.toString());
        return SS;
        }
}
