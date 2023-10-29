package romanow.abc.ess2.android.script.functions;


import romanow.abc.core.Pair;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.OperationStack;
import romanow.abc.core.script.ScriptException;
import romanow.abc.core.types.*;

public class FESS2ReadRegFloatGUI extends ESS2LocalFunction {
    public FESS2ReadRegFloatGUI() {
        super("readRegFloatGUI", "ess2:чтение регистра");
    }

    @Override
    public int getResultType() {
        return ValuesBase.DTFloat;
    }

    @Override
    public int[] getParamTypes() {
        return new int[]{ValuesBase.DTString, ValuesBase.DTLong, ValuesBase.DTLong};
    }

    @Override
    public void call(CallContext context) throws ScriptException {
        Object env = context.getCallEnvironment();
        if (env == null)
            throw new ScriptException(ValuesBase.SEIllegalFunEnv, "Объект окружения = null");
        if (!(env instanceof ESS2Architecture))
            throw new ScriptException(ValuesBase.SEIllegalFunEnv, "Объект окружения не ESS2Architecture");
        ESS2Architecture architecture = (ESS2Architecture) env;
        OperationStack stack = context.getStack();
        TypeString devName;
        TypeLong unit;
        TypeLong regNum;
        try {
            TypeFace par2 = stack.pop();
            TypeFace par1 = stack.pop();
            TypeFace par0 = stack.pop();
            devName = (TypeString) par0;
            unit = (TypeLong) par1;
            regNum = (TypeLong) par2;
        } catch (Exception ee) {
            throw new ScriptException(ValuesBase.SEBug, "Исключение: " + ee.toString());
            }
        Pair<ESS2Device, Integer> res = null;
        try {
            res = findDevice(architecture, devName.formatTo(), (int) unit.toLong());
            int vv = res.o1.getDriver().readRegister(res.o1.getShortName(), res.o2, (int) regNum.toLong()) & 0x0FFFF;
            int vv2 = res.o1.getDriver().readRegister(res.o1.getShortName(), res.o2, (int) regNum.toLong() + 1) & 0x0FFFF;
            vv |= vv2 << 16;
            stack.push(new TypeFloat(Float.intBitsToFloat(vv)));
        } catch (UniException ee) {
            String ss = "Ошибка чтения регистра Modbus: " + regNum.toLong() + "\n" + ee.toString();
            throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования " + devName.formatTo() + ": " + ss);
        }
    }
    /*
    ESS2Device device = findDevice(architecture,devName.formatTo());
        if (device==null){
            throw new ScriptException(ValuesBase.SEConfiguration, "Не найдено ед.оборудования: "+devName.formatTo());
            }
        if (!device.getErrors().valid()){
            throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования "+devName.formatTo()+
                    ": "+device.getErrors().toString());
                }
        try {
            int regNum2 = (int)regNum.toLong();
            int vv = device.getDriver().readRegister(device.getShortName(),(int)unit.toLong(),regNum2) & 0x0FFFF;
            int vv2 = device.getDriver().readRegister(device.getShortName(),(int)unit.toLong(),regNum2+1) & 0x0FFFF;
            vv |= vv2<<16;
            float  ff = Float.intBitsToFloat(vv);
            stack.push(new TypeFloat(ff));
            } catch (UniException ee){
                String ss = "Ошибка чтения регистра Modbus: "+regNum.toLong()+ "\n"+ee.toString();
                throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования "+devName.formatTo()+ ": "+ss);
                }
            }
        }
     */
}