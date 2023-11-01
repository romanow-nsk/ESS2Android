package romanow.abc.ess2.android.rendering.module;

import android.widget.RelativeLayout;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;

public class Module implements I_Module{
    protected RelativeLayout panel;
    protected RestAPIBase service;
    protected RestAPIESS2 service2;
    protected Meta2GUIForm form;
    protected String token;
    protected FormContext2 context;
    protected ESS2ArchitectureData client;
    public Module(){}
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        client = client0;
        this.form = form;
        this.panel = panel;
        this.service = service;
        this.service2 = service2;
        this.token = token;
        context = formContext;
        }
    @Override
    public String getTitle(){ return ""; }
    @Override
    public void repaintView() {
    }
    @Override
    public void repaintValues() {
    }

}