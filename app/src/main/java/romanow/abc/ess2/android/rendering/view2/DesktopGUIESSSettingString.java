package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;

import retrofit2.Call;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.baseentityes.JString;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIESSSettingString;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class DesktopGUIESSSettingString extends View2BaseDesktop {
    private BorderedTextView textField;
    private String setValue="";
    public DesktopGUIESSSettingString(){
        type = Values.GUIESSSettingString; }
    @Override
    public void addToPanel(RelativeLayout panel) {
        FormContext2 context= getContext();
        Meta2GUIESSSettingString element = (Meta2GUIESSSettingString) getElement();
        setLabel(panel);
        int w2=element.getW2();
        if (w2==0) w2=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        setBounds(textField,
                context.x(element.getX()+element.getDx()+5),
                context.y(element.getY()),
                context.dx(w2),
                context.dy(25));
        textField.setEnabled(context.isActionEnable());
        setTextFieldParams(textField);
        //textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        panel.addView(textField);
        setInfoClick(textField);
        if (!context.isActionEnable())
            textField.setBackgroundColor(Values.AccessDisableColor | 0xFF000000);
        getSettings();
        textField.setText(setValue);
        boolean enabled= element.isEditDisable();
        textField.setEnabled(enabled);
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
                getSettings();
                /*
                KeyBoardPanel digitPanel = new KeyBoardPanel(false, setValue, new I_Value<String>() {
                    @Override
                    public void onEnter(String value) {
                        new NetCall<JEmpty>().call(context.getMain().main(), context.getService().updateWorkSettings(context.getToken(), element.getFieldName(), value),
                                new NetBackDefault() {
                                    @Override
                                    public void onSuccess(Object val) {}
                                });
                        getSettings();
                        }
                    });
                 */
                }
            });
        }
    @Override
    public void showInfoMessage() {
        Meta2GUIESSSettingString element = (Meta2GUIESSSettingString) getElement();
        String ss = "Параметр сервера (int) "+element.getFieldName();
        context.getMain().main().popupInfo(ss);
        }
    private void getSettings() {
        final FormContext2 context = getContext();
        final Meta2GUIESSSettingString element = (Meta2GUIESSSettingString) getElement();
        new NetCall<JString>().call(context.getMain().main(), context.getService().getWorkSettingsString(context.getToken(), element.getFieldName()),
                new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        setValue = ((JString) val).getValue();
                    }
                });
        textField.setText(""+setValue);
        }
    @Override
    public void putValue(long vv) throws UniException {}
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        return null;
        }
}
