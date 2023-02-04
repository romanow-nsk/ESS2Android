package romanow.abc.ess2.android.service;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.UniException;
import romanow.abc.core.drivers.I_ModbusGroupDriver;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.entity.metadata.CallResult;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.rendering.I_Value;
import romanow.abc.ess2.android.service.APICall2;
import romanow.abc.ess2.android.service.AppData;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class ModBusClientAndroidDriver implements I_ModbusGroupAsyncDriver {
    class Result{
        UniException ee=null;
        String mes = null;
        int value=0;
        }
    public final static int StateOff=0;
    public final static int StateOn=1;
    public final static int StateBusy=2;
    public final static int StateError=3;
    public final static String[] states = {"Выключено","Готово","Занято","Ошибка"};
    private RestAPIBase service;
    private RestAPIESS2 service2;
    private boolean ready=false;
    private long oid=0;  // =0, не включает соединение с ПЛК, иначе включает и выбирает мета-данные
    private String token;
    private MainActivity base;
    @Override
    public void openConnection(Object needed[], HashMap<String, String> paramList) throws UniException {
        ready=false;
        int ii=0;
        AppData ctx = AppData.ctx();
        service = ctx.getService();
        service2 = ctx.getService2();
        token = ctx.loginSettings().getSessionToken();
        base = (MainActivity) needed[0];
        ready = true;
        try {
            } catch (Exception ee){
                throw UniException.bug("Недопустимый класс драйвера "+needed[ii].getClass().getSimpleName());
                }
        }
    @Override
    public void closeConnection() throws UniException{}

    @Override
    public void reopenConnection() {}

    @Override
    public int getState() {
        return ready ? StateOn : StateOff;
        }

    @Override
    public void readRegister(String devName, int unit, int regNum, NetBack back) {
        if (!ready) {
            back.onError(UniException.user("Устройство не готово"));
            return;
            }
        new NetCall<JInt>().call(base, AppData.ctx().getService2().readESS2RegisterValue(token, devName, unit, regNum), back);
        }
    @Override
    public void writeRegister(String devName, int unit, int regNum, int value, NetBack back){
        if (!ready) {
            back.onError(UniException.user("Устройство не готово"));
            return;
            }
        new NetCall<JEmpty>().call(base, AppData.ctx().getService2().writeESS2RegisterValue(token, devName, unit, regNum,value), back);
        }
    @Override
    public boolean isReady() {
        return ready;
        }
    private volatile boolean busy;
    @Override
    public int readRegister(final String devName,final int unit, final int regNum) throws UniException {
        if (!ready)
            throw UniException.user("Устройство не готово");
        JInt xx = new APICall2<JInt>(){
            @Override
            public Call apiFun() {
                return service2.readESS2RegisterValue(token,devName,unit,regNum);
                }
            }.call(base);
        return xx.getValue();
        /*
        final Result result = new Result();
        if (!ready)
            throw UniException.user("Устройство не готово");
        new NetCall<JInt>().call(base, service2.readESS2RegisterValue(token, devName, unit, regNum), new NetBack() {
            @Override
            public void onError(int code, String mes) {
                result.mes = mes;
                result.value = code;
                busy=false;
                }
            @Override
                public void onError(UniException ee) {
                result.ee = ee;
                busy=false;
                }
            @Override
                public void onSuccess(Object val) {
                result.value = ((JInt)val).getValue();
                busy=false;
                }
            });
        if (result.ee!=null)
            throw result.ee;
        if (result.mes!=null)
            throw UniException.io(""+result.value+": "+result.mes);
        return result.value;
         */
        }
    @Override
    public void writeRegister(final String devName,final int unit, final int regNum, final int value) throws UniException {
        if (!ready)
            throw UniException.user("Устройство не готово");
        new APICall2<JEmpty>(){
            @Override
            public Call apiFun() {
                return service2.writeESS2RegisterValue(token,devName,unit,regNum,value);
                }
            }.call(base);
        /*
        final Result result = new Result();
        if (!ready)
            throw UniException.user("Устройство не готово");
        busy = true;
        new NetCall<JEmpty>().call(base, AppData.ctx().getService2().writeESS2RegisterValue(token, devName, unit, regNum,value), new NetBack() {
             @Override
             public void onError(int code, String mes) {
                 result.mes = mes;
                 result.value = code;
                 busy=false;
                 }
             @Override
             public void onError(UniException ee) {
                 result.ee = ee;
                 busy=false;
                 }
             @Override
                 public void onSuccess(Object val) {
                 busy=false;
                 }
             });
        while (busy) {
            try {
                Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        if (result.ee!=null)
            throw result.ee;
        if (result.mes!=null)
            throw UniException.io(""+result.value+": "+result.mes);
         */
        }
    @Override
    public ArrayList<Integer> readRegisters(String devName, int unit, int regNum, int size) throws UniException {
        throw UniException.bug("Функция readRegisters(int unit, int regNum, int size) не поддерживанися в API");
        }
    @Override
    public void writeRegisters(String devName,int unit, int regNum, ArrayList<Integer> values) throws UniException {
        throw UniException.bug("Функция writeRegisters(int unit, int regNum, ArrayList<Integer> values) не поддерживанися в API");
        }
}

