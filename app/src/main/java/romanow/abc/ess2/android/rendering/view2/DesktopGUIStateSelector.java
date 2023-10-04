package romanow.abc.ess2.android.rendering.view2;

import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2State;
import romanow.abc.core.entity.metadata.Meta2StateRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIStateSelector;
import romanow.abc.core.entity.metadata.view.Meta2GUIStateSet;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIStateSelector extends DesktopGUIStateSet {
    private BorderedTextView textField;
    public DesktopGUIStateSelector(){
        type = Values.GUIStateSelector;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUIStateSelector element = (Meta2GUIStateSelector) getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+DefaultSpace),
                context.y(element.getY()),
                context.dx(element.getW2()),
                context.dy(hh));
        setTextFieldParams(textField);
        textField.setClickable(false);
        panel.addView(textField);
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