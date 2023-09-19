package romanow.abc.ess2.android.rendering.view2;

import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIData;
import romanow.abc.core.entity.metadata.view.Meta2GUIRegW2;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUITwoBytes extends View2BaseDesktop {
    private TextView textField;
    public DesktopGUITwoBytes(){
        type = Values.GUITwoBytes;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        Meta2GUIData element2 = (Meta2GUIData)element;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element2.getW2();
        if (dd==0) dd=DefaultW2;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+dxOffset+element.getDx()+DefaultSpace),
                context.y(element.getY()+dyOffset),
                context.dx(dd),
                context.dy(hh));
        setTextFieldParams(textField);
        textField.setClickable(false);
        panel.addView(textField);
        setInfoClick(textField);
        }
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister)  getRegister();
        String ss = "Регистр данных (2 байта) ["+getUnitIdx()+"] "+(set.getRegNum()+regOffset)+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+="Потоковый  - "+(set.getStreamType()!=Values.DataStreamNone ? "да":"нет")+",";
        ss+=" Ед.изм. "+ set.getUnit();
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        Meta2Register register = getRegister();
        Meta2GUIRegW2 metaGUI = (Meta2GUIRegW2)getElement();
        textField.setText("["+((vv>>8) & 0x0FF) +"][" + (vv & 0x0FF)+"]");
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2DataRegister || register instanceof Meta2SettingRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}