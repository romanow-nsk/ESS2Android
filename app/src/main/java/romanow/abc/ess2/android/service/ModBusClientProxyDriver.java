package romanow.abc.ess2.android.service;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.UniException;
import romanow.abc.core.drivers.I_ModbusGroupDriver;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.entity.metadata.CallResult;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.service.APICall2;
import romanow.abc.ess2.android.service.AppData;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class ModBusClientProxyDriver implements I_ModbusGroupDriver {
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
        try {
            } catch (Exception ee){
                throw UniException.bug("Недопустимый класс драйвера "+needed[ii].getClass().getSimpleName());
                }
        /*
        String id = paramList.get("id");
        if (id==null){
            oid=0;          // Нет id - использование текущего состояния соединения сервер-ПЛК
            new NetCall<JBoolean>().call(base, service2.isPLMReady(token), new NetBackDefault() {
                @Override
                public void onSuccess(Object val) {
                    ready = ((JBoolean)val).value();
                    }
                });
            }
        ready=true;
        */
        }
    @Override
    public void closeConnection() throws UniException{
        new NetCall<CallResult>().call(base, service2.disconnectFromEquipment(token), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                ready = false;
                }
            });
        }

    @Override
    public void reopenConnection() {
    }

    @Override
    public boolean isReady() {
        return ready;
    }
    @Override
    public int readRegister(String devName,int unit, int regNum) throws UniException {
        if (!ready)
            throw UniException.user("Устройство не готово");
            JInt xx = new APICall2<JInt>(){
            @Override
            public Call apiFun() {
                return service2.readESS2RegisterValue(token,devName,unit,regNum);
                }
            }.call(base);
        return xx.getValue();
        }
    @Override
    public void writeRegister(String devName,int unit, int regNum, int value) throws UniException {
        if (!ready)
            throw UniException.user("Устройство не готово");
        JInt xx = new APICall2<JInt>(){
            @Override
            public Call apiFun() {
                return service2.writeESS2RegisterValue(token, devName, unit, regNum, value);
                }
            }.call(base);
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

