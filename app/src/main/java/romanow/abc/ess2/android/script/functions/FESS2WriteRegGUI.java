package romanow.abc.ess2.android.script.functions;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.OperationStack;
import romanow.abc.core.script.ScriptException;
import romanow.abc.core.types.TypeFace;
import romanow.abc.core.types.TypeLong;
import romanow.abc.core.types.TypeString;
import romanow.abc.ess2.android.service.AppData;
import romanow.abc.ess2.android.service.I_ModbusGroupAsyncDriver;
import romanow.abc.ess2.android.service.NetBack;

public class FESS2WriteRegGUI extends ESS2LocalFunction {
    public FESS2WriteRegGUI() {
        super("writeRegGUI", "ess2:запись регистра");
    }
    @Override
    public int getResultType() {
        return ValuesBase.DTVoid;
    }
    @Override
    public int[] getParamTypes() {
        return new int[]{ ValuesBase.DTString,ValuesBase.DTLong,ValuesBase.DTLong,ValuesBase.DTLong  };
    }
    @Override
    public void call(CallContext context) throws ScriptException {
        Object env = context.getCallEnvironment();
        if (env==null)
            throw new ScriptException(ValuesBase.SEIllegalFunEnv,"Объект окружения = null");
        if (!(env instanceof ESS2Architecture))
            throw new ScriptException(ValuesBase.SEIllegalFunEnv,"Объект окружения не ESS2Architecture");
        ESS2Architecture architecture = (ESS2Architecture) env;
        OperationStack stack = context.getStack();
        TypeString devName;
        TypeLong unit;
        TypeLong regNum;
        TypeLong regValue;
        try {
            TypeFace par3 = stack.pop();
            TypeFace par2 = stack.pop();
            TypeFace par1 = stack.pop();
            TypeFace par0 = stack.pop();
            devName = (TypeString) par0;
            unit = (TypeLong) par1;
            regNum = (TypeLong) par2;
            regValue = (TypeLong) par3;
            } catch (Exception ee){
                throw new ScriptException(ValuesBase.SEBug,"Исключение: "+ee.toString());
                }
        ESS2Device device = findDevice(architecture,devName.formatTo());
        if (device==null){
            throw new ScriptException(ValuesBase.SEConfiguration, "Не найдено ед.оборудования: "+devName.formatTo());
            }
        if (!device.getErrors().valid()){
            throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования "+devName.formatTo()+
                    ": "+device.getErrors().getInfo());
            }
        final int regNum2 = (int) regNum.toLong();
        final String devName2 = devName.formatTo();
        final I_ModbusGroupAsyncDriver driver = (I_ModbusGroupAsyncDriver) device.getDriver();
        try {
            driver.writeRegister(device.getShortName(), (int) unit.toLong(), regNum2, (int) regValue.toLong());
            } catch (UniException ee){
            String ss = "Ошибка чтения регистра Modbus: "+regNum.toLong()+ "\n"+ee.toString();
            throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования "+devName.formatTo()+ ": "+ss);
            }
        /*
        driver.writeRegister(device.getShortName(), (int) unit.toLong(), regNum2, (int) regValue.toLong(), new NetBack() {
            @Override
            public void onError(int code, String mes) {
                String ss = "Ошибка записи регистра Modbus: "+regNum2+ "\n"+code + ": "+ mes;
                ss =  "Ошибки оборудования "+ devName2+ ": "+ss;
                AppData.ctx().errorMes(ss);
                }
            @Override
            public void onError(UniException ee) {
                String ss = "Ошибка записи регистра Modbus: "+regNum2+ "\n"+ee.toString();
                ss =  "Ошибки оборудования "+ devName2+ ": "+ss;
                AppData.ctx().errorMes(ss);
                }
            @Override
            public void onSuccess(Object val) {}
            });
          */
    }
}
