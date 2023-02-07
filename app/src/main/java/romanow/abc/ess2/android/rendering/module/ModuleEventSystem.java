package romanow.abc.ess2.android.rendering.module;

import romanow.abc.core.constants.Values;

public class ModuleEventSystem extends ModuleEventAll {
    public ModuleEventSystem(){}
    @Override
    public String getTitle(){ return "Системные (сервер)"; }
    public boolean typeFilter(int type) {
        return type== Values.EventSystem || type == Values.EventFile;
        }
}
