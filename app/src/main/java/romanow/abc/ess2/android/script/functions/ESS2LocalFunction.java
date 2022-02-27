package romanow.abc.ess2.android.script.functions;

import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
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
    public ESS2Device findDevice(ESS2Architecture architecture, String devName){
        for(ESS2Device device : architecture.getDevices())
            if (device.getShortName().equals(devName))
                return device;
        return null;
    }
}
