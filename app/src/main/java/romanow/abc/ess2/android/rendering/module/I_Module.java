package romanow.abc.ess2.android.rendering.module;

import android.widget.RelativeLayout;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;

public interface I_Module {
    public void init(ESS2ArchitectureData client, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext);
    public void repaintView();
    public void repaintValues();
    public String getTitle();
}
