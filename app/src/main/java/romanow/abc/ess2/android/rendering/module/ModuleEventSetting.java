package romanow.abc.ess2.android.rendering.module;

import romanow.abc.core.constants.Values;

public class ModuleEventSetting extends ModuleEventAll {
    public ModuleEventSetting(){}
    @Override
    public String getTitle(){ return "Изменение уставок"; }
    public boolean typeFilter(int type) {
        return type== Values.EventSetting;
        }
}
