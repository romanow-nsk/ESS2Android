package romanow.abc.ess2.android.rendering.view2;

import android.graphics.Color;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2RegLink;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUILevelIndicator;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_Success;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUILevelIndicator extends View2BaseDesktop {
    private BorderedTextView textField;
    public DesktopGUILevelIndicator(){
        type = Values.GUILevelIndicator;
        }
    private I_Success back=null;
    private final static int alarmMax=0;
    private final static int alarmMin=3;
    private final static int warnMax=1;
    private final static int warnMin=2;
    private final double limits[]={0,0,0,0};
    private double value=0;
    private double vMin,vMax;
    private int h=0,w2=0;
    private final static int proc=25;
    private int backColor;
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUILevelIndicator element = (Meta2GUILevelIndicator) getElement();
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        w2=element.getW2();
        if (w2==0) w2=100;
        h = element.getH();
        if (h==0) h=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+5),
                context.y(element.getY()+getDyOffset()),
                context.x(w2-1),
                context.y(h));
        panel.addView(textField);
        backColor = element.getColor() | 0xFF000000;
        setInfoClick(textField);
        }
    @Override
    public void putValue(Meta2Register register, long xx, int idx) {
        limits[idx] = register.regValueToFloat(getUnitIdx(),(int)xx);
        }
    @Override
    public void putValue(long xx) throws UniException {
        Meta2DataRegister register = (Meta2DataRegister) getRegister();
        if (((Meta2GUILevelIndicator)getElement()).isByteSize()){
            value = (byte)xx;
            }
        else
            value = register.regValueToFloat(getUnitIdx(),(int)xx);
        }
    @Override
    public void repaintValues(){
        vMin = limits[alarmMin] - (limits[alarmMax]-limits[alarmMin])*proc/100;
        vMax = limits[alarmMax] + (limits[alarmMax]-limits[alarmMin])*proc/100;
        int color = Color.GREEN;
        if (value>=limits[alarmMax] || value<=limits[alarmMin])
            color=Color.RED;
        else
        if (value>=limits[warnMax] || value<=limits[warnMin])
            color=Color.YELLOW;
        int hh = (int)((value-vMin)/(vMax-vMin)*h);
        textField.setBackgroundColor(color);
        FormContext2 context= getContext();
        Meta2GUILevelIndicator element = (Meta2GUILevelIndicator) getElement();
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+5),
                context.y(element.getY()+getDyOffset()+h-hh),
                context.x(w2-1),
                context.y(hh));
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2DataRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        Meta2RegLink[] links = getSettingsLinks();
        for(Meta2RegLink link : links){
            register = link.getRegister();
            if (!(register instanceof Meta2SettingRegister))
                return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
            }
        return null;
        }
    @Override
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister)  getRegister();
        String ss = "Индикатор уровня для "+(set.getRegNum()+getRegOffset())+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+="Регистры уровней: ";
        for(Meta2RegLink link : getElement().getSettingsLinks()){
            ss+=link.getRegister().getRegNum()+" ";
            }
        context.getMain().main().popupAndLog(ss);
    }
}

