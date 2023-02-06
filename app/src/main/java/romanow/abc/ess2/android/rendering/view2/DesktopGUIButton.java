package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Command;
import romanow.abc.core.entity.metadata.Meta2CommandRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIButton;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.service.NetBack;

public class DesktopGUIButton extends View2BaseDesktop {
    private Button textField;
    public DesktopGUIButton(){
        type = Values.GUIButton;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        //LinearLayout button = (LinearLayout) context.getMain().main().getLayoutInflater().inflate(R.layout.form_button, null);
        //textField = (Button) button.findViewById(R.id.form_button);
        textField = new Button(context.getMain().main());
        FormContext2 context = getContext();
        Meta2GUIButton element = (Meta2GUIButton) getElement();
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()),
                context.y(element.getY()),
                context.x(element.getDx()),
                context.y(hh));
        textField.setText(element.getTitle());
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,textSize);
        Meta2CommandRegister register = (Meta2CommandRegister)getRegister();
        final Meta2Command cmd = register.getCommands().getByCode(element.getCmdCode());
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser() && !cmd.isRemoteEnable();
        int color = remoteDisable || !context.isActionEnable() ? Values.AccessDisableColor : getBackColor();
        textField.setBackgroundColor(color | 0xFF000000);
        setInfoClick(textField);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (remoteDisable) {
                context.getMain().main().popupAndLog("Запрет удаленного управления");
                return;
                }
            if  (!context.isActionEnable()) {
                    context.getMain().main().popupAndLog("Недостаточен уровень доступа");
                    return;
                    }
                new OKDialog(context.getMain().main(), cmd.getTitle(), new I_EventListener() {
                    @Override
                    public void onEvent(String zz) {
                        if (zz==null) return;
                        writeMainRegister(cmd.getCode());
                        }
                    });
                }
            });
        panel.addView(textField);
        }
    @Override
    public void showInfoMessage(){
        int cmdCode = ((Meta2GUIButton)getElement()).getCode();
        Meta2CommandRegister register = (Meta2CommandRegister)  getRegister();
        String ss = register.getCommands().getByCode(cmdCode).getTitle();
        context.getMain().main().popupAndLog("Команда "+register.getRegNum()+":="+cmdCode+"\n"+ ss);
        }
    @Override
    public void putValue(long vv) throws UniException {}
    @Override
    public String setParams(FormContext2 context, ESS2Architecture meta, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context,meta, element0,onEvent0);
        if (!(getRegister() instanceof Meta2CommandRegister))
            return "Недопустимый "+getRegister().getTypeName()+" для "+getElement().getFullTitle();
        return null;
        }
}