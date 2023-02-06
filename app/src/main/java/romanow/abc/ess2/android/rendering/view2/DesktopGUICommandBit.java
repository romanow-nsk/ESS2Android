package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUICommandBit;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUICommandBit extends View2BaseDesktop {
    private TextView textField;
    private Meta2Bit bit;
    public DesktopGUICommandBit(){
        type =Values.GUICommandBit;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        Meta2GUICommandBit element = (Meta2GUICommandBit) getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        setBounds(textField,
                context.x(element.getX()),
                context.y(element.getY()),
                context.x(element.getDx()),
                context.y(25));
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,textSize);
        textField.setClickable(false);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        textField.setBackgroundColor(getBackColor());
        textField.setTextColor(textColor);
        setInfoClick(textField);
        Meta2BitRegister register = (Meta2BitRegister) getRegister();
        bit = register.getBits().getByCode(element.getBitNum());
        if (bit==null){
            context.getMain().main().popupInfo("Не найден бит "+element.getBitNum()+" регистра "+register.getTitle());
            return;
            }
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser() && !bit.isRemoteEnable();
        int color=remoteDisable || !context.isActionEnable() ? Values.AccessDisableColor : getBackColor();
        textField.setBackgroundColor(color | 0xFF000000);
        textField.setTextColor(textColor);
        textField.setText(bit.getTitle());
        setLongClickInfo(textField);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteDisable){
                    context.getMain().main().popupInfo("Запрет удаленного управления");
                    return;
                    }
                if (!context.isActionEnable()){
                    context.getMain().main().popupInfo("Недостаточен уровень доступа");
                    return;
                    }
                new OKDialog(context.getMain().main(), bit.getTitle(), new I_EventListener() {
                    @Override
                    public void onEvent(String zz) {
                        writeMainRegister(1<<element.getBitNum());
                        }
                    });
                }
            });
        }
    @Override
    public void showInfoMessage(){
        Meta2BitRegister register = (Meta2BitRegister)  getRegister();
        String ss = "Разряд "+getElement().getTitle()+" "+toHex(register.getRegNum())+":="+bit.getBitNum()+"$"+ bit.getTitle();
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {}
    @Override
    public String setParams(FormContext2 context, ESS2Architecture meta, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context,meta, element0,onEvent0);
        if (!(getRegister() instanceof Meta2BitRegister))
            return "Недопустимый "+getRegister().getTypeName()+" для "+getElement().getFullTitle();
        return null;
        }
}
