package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.Meta2String;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIString;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIString extends View2BaseDesktop {
    private TextView textField;
    public DesktopGUIString(){
        type = Values.GUIString;;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUIString element = (Meta2GUIString) getElement();
        int w2=element.getW2();
        if (w2==0) w2=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int hh = element.getH();
        if (hh==0) hh=25;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+DefaultSpace),
                context.y(element.getY()),
                context.x(w2),
                context.y(hh));
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,textSize);
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        setInfoClick(textField);
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser(); // && !(getRegister()).isRemoteEnable();
        int color=remoteDisable || !context.isActionEnable() ? Values.AccessDisableColor : getBackColor();
        textField.setBackgroundColor(color | 0xFF000000);
        textField.setTextColor(textColor);
        setLongClickInfo(textField);
        }
    public void showInfoMessage() {
        Meta2Register set =  getRegister();
        String ss = "Строка "+toHex(set.getRegNum()+getRegOffset())
                +" ["+toHex(set.getRegNum())+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(int data[]) throws UniException {
        Meta2String register = (Meta2String)getRegister();
        String ss ="";
        for(int i=0;i<data.length && i<register.getSize();i++)
            ss += ""+(char)(data[i] & 0x0FF)+(char)((data[i]>>8) & 0x0FF);
        textField.setText(" "+ss);
        }
    @Override
    public void putValue(int vv) throws UniException {
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2String))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}
