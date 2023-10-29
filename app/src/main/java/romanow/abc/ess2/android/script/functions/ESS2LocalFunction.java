package romanow.abc.ess2.android.script.functions;

import romanow.abc.core.Pair;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.entity.subject2area.ESS2Equipment;
import romanow.abc.core.entity.subject2area.ESS2LogUnit;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.ScriptException;
import romanow.abc.core.script.functions.FunctionCall;

public class ESS2LocalFunction extends FunctionCall {
    public ESS2LocalFunction(String name0, String comment0) {
        super(name0, comment0);}
    @Override
    public int getResultType() {return 0;}
    @Override
    public int[] getParamTypes() {return new int[0];}
    @Override
    public void call(CallContext context) throws ScriptException {}
    public Pair<ESS2Device,Integer> findDevice(ESS2Architecture architecture, String devName, int unit) throws ScriptException{
        ESS2Equipment equipment = architecture.getEquipments().getByName(devName);
        if (equipment==null){
            throw new ScriptException(ValuesBase.SEConfiguration, "Не найдено ед.оборудования: "+devName);
        }
        if (unit >= equipment.getLogUnits().size()){
            throw new ScriptException(ValuesBase.SEConfiguration, "Недопустимый лог.номер устройства: "+unit+">="+equipment.getLogUnits().size());
        }
        ESS2LogUnit logUnit = equipment.getLogUnits().get((int)unit);
        ESS2Device device = logUnit.getDevice().getRef();
        if (!device.getErrors().valid()){
            throw new ScriptException(ValuesBase.SEConfiguration, "Ошибки оборудования "+devName+
                    ": "+device.getErrors().toString());
            }
        return new Pair<>(device,logUnit.getUnit());
    }
}
