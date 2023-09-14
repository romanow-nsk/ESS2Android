package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;

import retrofit2.Call;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIESSSettingInt;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class DesktopGUIESSSettingInt extends View2BaseDesktop {
    private BorderedTextView textField;
    private int setValue=0;
    public DesktopGUIESSSettingInt(){
        type = Values.GUIESSSettingInt;
        }

    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUIESSSettingInt element = (Meta2GUIESSSettingInt) getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element.getW2();
        if (dd==0) dd=DefaultW2;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+5),
                context.y(element.getY()),
                context.dx(dd),
                context.dy(hh));
        setTextFieldParams(textField);
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        setInfoClick(textField);
        boolean disable = !context.isActionEnable() || element.isEditDisable();
        textField.setEnabled(!disable);
        if (disable)
            textField.setBackgroundColor(Values.AccessDisableColor | 0xFF000000);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (element.isEditDisable()){
                    context.getMain().main().popupInfo("Настройка не редактируется");
                    return;
                    }
                if (!context.isActionEnable()){
                    context.getMain().main().popupInfo("Недостаточен уровень доступа");
                    return;
                    }
                /*
                DigitPanel digitPanel = new DigitPanel(new I_RealValue() {
                    @Override
                    public void onEvent(double value) {
                        try {
                            new APICallC<JEmpty>(){
                                @Override
                                public Call<JEmpty> apiFun() {
                                    return context.getService().updateWorkSettings(context.getToken(),element.getFieldName(),(int)value);
                                }
                            }.call();
                            getSettings();
                        } catch (UniException ex) {
                            new Message(300,300,"Ошибка обновления настроек",Values.PopupMessageDelay);
                        }
                    }
                });
                digitPanel.setValue(setValue);
                digitPanel.setNoFloat();
                */
                }
            });
        getSettings();
        }
    @Override
    public void showInfoMessage() {
        final Meta2GUIESSSettingInt element = (Meta2GUIESSSettingInt) getElement();
        String ss = "Параметр сервера (int) "+element.getFieldName();
        context.getMain().main().popupInfo(ss);
        }
    private void getSettings(){
        final FormContext2 context= getContext();
        final Meta2GUIESSSettingInt element = (Meta2GUIESSSettingInt) getElement();
        new NetCall<JInt>().call(context.getMain().main(), context.getService().getWorkSettingsInt(context.getToken(), element.getFieldName()),
             new NetBackDefault() {
                 @Override
                 public void onSuccess(Object val) {
                    textField.setText(""+setValue);
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

