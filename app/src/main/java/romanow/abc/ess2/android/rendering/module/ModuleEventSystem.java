package romanow.abc.ess2.android.rendering.module;

import android.widget.RelativeLayout;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;

public class ModuleEventSystem extends ModuleEventAll {
    public ModuleEventSystem(){}
    @Override
    public String getTitle(){ return "Системные (сервер)"; }
    public boolean typeFilter(int type) {
        return type== Values.EventSystem || type == Values.EventFile;
        }
    private int[] eTypes = {Values.EventSystem, Values.EventFile};
    public int[] eventTypes(){ return eTypes; }
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client, panel, service, service2,token, form, formContext);
        repaintValues();
        }
}
