package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2State;
import romanow.abc.core.entity.metadata.Meta2StateRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIStateSet;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIStateSet extends View2BaseDesktop {
    private BorderedTextView textField;
    public DesktopGUIStateSet(){
        type = Values.GUIStateSet;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUIStateSet element = (Meta2GUIStateSet) getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+DefaultSpace),
                context.y(element.getY()),
                context.x(element.getW2()),
                context.y(hh));
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
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
        }

    @Override
    public void putValue(long vv) throws UniException {
        Meta2StateRegister reg = (Meta2StateRegister)  getRegister();
        Meta2State state = reg.getStates().getByCode((int)vv);
        if (state==null)
            textField.setText("Недопустимое состояние");
        else
            textField.setText(""+state.getTitle());
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register =  getRegister();
        if (!(register instanceof Meta2StateRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
    @Override
    public void showInfoMessage() {
        }
}