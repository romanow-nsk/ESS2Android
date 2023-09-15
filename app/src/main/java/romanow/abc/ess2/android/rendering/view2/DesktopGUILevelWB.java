package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUILevelWB;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_Success;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUILevelWB extends View2BaseDesktop {
    private BorderedTextView textField;
    private Meta2GUILevelWB element;
    public DesktopGUILevelWB(){
        type = Values.GUILevelWB;
        }
    private I_Success back=null;
    private double value=0;
    private int h=0,w2=0;
    private final static int proc=25;
    private Color backColor;
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        element = (Meta2GUILevelWB) getElement();
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        w2=element.getW2();
        if (w2==0) w2=100;
        h = element.getH();
        if (h==0) h=100;
        textField = new BorderedTextView(context.getMain().main().getBaseContext(),Color.BLACK);
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+5),
                context.y(element.getY()+getDyOffset()),
                context.dx(w2-1),
                context.dy(5));
        panel.addView(textField);
        setTextFieldParams(textField);
        textField.setText("");
        textField.setBackgroundColor(Color.LTGRAY);
        setInfoClick(textField);
        }
    public void showInfoMessage() {
        Meta2Register set =  getRegister();
        String ss = "Регистр данных "+toHex(set.getRegNum()+getRegOffset())
                +" ["+toHex(set.getRegNum())+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        if (set instanceof Meta2DataRegister){
            Meta2DataRegister set2 = (Meta2DataRegister)set;
            ss+="Потоковый  - "+(set2.getStreamType()!=Values.DataStreamNone ? "да":"нет")+",";
            ss+=" Ед.изм. "+ set2.getUnit();
            }
        else{
            ss+=" Ед.изм. "+ ((Meta2SettingRegister)set).getUnit();
            }
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        Meta2Register register = getRegister();
        double val = register.regValueToFloat(getUnitIdx(),(int)vv);
        double lowLevel = element.getLowLevel();
        double highLevel = element.getHighLevel();
        if (val<lowLevel)
            val=lowLevel;
        if (val>highLevel)
            val=highLevel;
        FormContext2 context= getContext();
        int hh = (int)(h*(val-lowLevel)/(highLevel-lowLevel));
        textField.setBackgroundColor(hh==0 ? Color.LTGRAY : Color.GRAY);
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+5),
                context.y(element.getY()+getDyOffset()+h-hh),
                context.dx(w2-1),
                context.dy(hh+10));
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
