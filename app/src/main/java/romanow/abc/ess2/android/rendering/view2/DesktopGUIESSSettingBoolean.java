package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import retrofit2.Call;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JBoolean;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIESSSettingBoolean;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class DesktopGUIESSSettingBoolean extends View2BaseDesktop {
    private ImageView textField;
    private boolean setValue=false;
    public DesktopGUIESSSettingBoolean(){ 
        type = Values.GUIESSSettingBoolean; 
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        FormContext2 context = getContext();
        Meta2GUIESSSettingBoolean element = (Meta2GUIESSSettingBoolean) getElement();
        int hh = element.getH();
        if (hh == 0) hh = DefaultH;
        setLabel(panel);
        int sz = 24;
        int offset = (25 - sz) / 2;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new ImageView(context.getMain().main());
        setBounds(textField,
                context.x(element.getX() + element.getDx() + getDxOffset() + 5 + offset),
                context.y(element.getY() + getDyOffset() + (hh - 15) / 2 - 5 + offset),
                context.dx(sz),
                context.dy(sz));
        panel.addView(textField);
        setInfoClick(textField);
        final boolean remoteDisable = !context.isSuperUser() && !context.isLocalUser();
        int color = remoteDisable || !context.isActionEnable() ? Values.AccessDisableColor : element.getColor();
        textField.setBackgroundColor(color | 0xFF000000);
        getSettings();
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Meta2GUIESSSettingBoolean) element).isEditDisable()) {
                    context.getMain().main().popupInfo("Настройка не редактируется");
                    return;
                    }
                if (remoteDisable) {
                    context.getMain().main().popupInfo("Запрет удаленного управления");
                    return;
                    }
                if (!context.isActionEnable()) {
                    context.getMain().main().popupInfo("Недостаточен уровень доступа");
                    return;
                    }
                new OKDialog(context.getMain().main(), element.getTitle() + (setValue ? ": выключить" : ": включить"), new I_EventListener() {
                    @Override
                    public void onEvent(String zz) {
                        setValue = !setValue;
                        new NetCall<JEmpty>().call(context.getMain().main(), context.getService().updateWorkSettings(context.getToken(),
                                element.getFieldName(), setValue), new NetBackDefault() {
                            @Override
                            public void onSuccess(Object val) {
                                getSettings();
                                }
                            });
                        }
                    });
                }
            });
        }
    @Override
    public void showInfoMessage() {
        Meta2GUIESSSettingBoolean element = (Meta2GUIESSSettingBoolean) getElement();
        String ss = "Параметр сервера (boolean) "+element.getFieldName();
        context.getMain().main().popupInfo(ss);
        }
    private void getSettings(){
        final FormContext2 context= getContext();
        final Meta2GUI element = getElement();
        new NetCall<JBoolean>().call(context.getMain().main(),
             context.getService().getWorkSettingsBoolean(context.getToken(), ((Meta2GUIESSSettingBoolean) getElement()).getFieldName()), new NetBackDefault() {
                 @Override
                 public void onSuccess(Object val) {
                     setValue = ((JBoolean)val).value();
                     textField.setImageResource(setValue ? R.drawable.ballgreen : R.drawable.ballred);
                     }
                 });
        }
    @Override
    public void putValue(long vv) throws UniException {}
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        return null;
        }
}
