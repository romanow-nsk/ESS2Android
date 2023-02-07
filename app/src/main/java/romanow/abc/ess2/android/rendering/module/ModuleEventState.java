package romanow.abc.ess2.android.rendering.module;

import romanow.abc.core.constants.Values;

public class ModuleEventState extends ModuleEventAll {
    public ModuleEventState(){}
    @Override
    public String getTitle(){ return "Изменение состояний"; }
    public boolean typeFilter(int type) {
        return type== Values.EventState || type==Values.EventDEStateReg || type==Values.EventDEBitReg;
        }
}
