package romanow.abc.ess2.android.rendering.module;

import romanow.abc.core.constants.Values;

public class ModuleEventExternal extends ModuleEventAll {
    public ModuleEventExternal(){}
    @Override
    public String getTitle(){ return "Внешние события"; }
    public boolean typeFilter(int type) {
        return type== Values.EventExternal;
    }
}
