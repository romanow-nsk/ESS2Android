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
import romanow.abc.core.entity.metadata.view.Meta2GUILevelMultiIndicator;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_Success;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUILevelMultiIndicator extends View2BaseDesktop {
    private final static int alarmMax=0;
    private final static int alarmMin=3;
    private final static int warnMax=1;
    private final static int warnMin=2;
    private final double limits[]={0,0,0,0};
    private double vv[] = new double[]{0,0,0};
    private  int hh[] = new int[]{0,0,0};
    private double vMin,vMax;
    private BorderedTextView textField;
    private BorderedTextView textField2;
    private BorderedTextView textField3;
    private BorderedTextView high;
    public DesktopGUILevelMultiIndicator(){
        type = Values.GUILevelMultiIndicator;
        }
    private I_Success back=null;
    private int h=0,w2=0;
    private final static int proc=25;
    private int backColor;
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUILevelMultiIndicator element = (Meta2GUILevelMultiIndicator) getElement();
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        w2=element.getW2();
        if (w2==0) w2=100;
        h = element.getH();
        if (h==0) h=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        textField2 = new BorderedTextView(context.getMain().main(),textColor);
        textField3 = new BorderedTextView(context.getMain().main(),textColor);
        high = new BorderedTextView(context.getMain().main(),textColor);
        int dxOffset = getDxOffset();
        int dyOffset = getDyOffset();
        setBounds(high,
                context.x(element.getX()+dxOffset+5),
                context.y(element.getY()+dyOffset),
                context.x(w2),
                context.x(h));
        backColor = element.getColor() | 0xFF000000;
        textField.setBackgroundColor(backColor);
        textField2.setBackgroundColor(backColor);
        textField3.setBackgroundColor(backColor);
        high.setBackgroundColor(backColor);
        setInfoClick(textField);
        setInfoClick(high);
        setBounds(high,
                context.x(element.getX()+dxOffset+5),
                context.y(element.getY()+dyOffset),
                context.x(w2),
                context.y(h));
        setBounds(textField,
                context.x(element.getX()+dxOffset+5),
                context.y(element.getY()+dyOffset+h),
                context.x(w2),
                context.y(0));
        setBounds(textField2,
                context.x(element.getX()+dxOffset+5),
                context.y(element.getY()+dyOffset+h),
                context.x(w2),
                context.y(0));
        setBounds(textField3,
                context.x(element.getX()+dxOffset+5),
                context.y(element.getY()+dyOffset+h),
                context.x(w2),
                context.y(0));
        panel.addView(textField);
        panel.addView(textField2);
        panel.addView(textField3);
        }
    @Override
    public void putValue(Meta2Register register, long xx, int idx) {
        if (register instanceof Meta2SettingRegister)
            limits[idx] = register.regValueToFloat(getUnitIdx(),(int)xx);
        else{
            if (idx==0)
                vv[1] = register.regValueToFloat(getUnitIdx(),(int)xx);
            else
                vv[2] = register.regValueToFloat(getUnitIdx(),(int)xx);
            }
        }
    @Override
    public void putValue(long xx) throws UniException {
        Meta2DataRegister register = (Meta2DataRegister) getRegister();
        vv[0] = register.regValueToFloat(getUnitIdx(),(int)xx);
        }
    private int getValueColor(double vv){
        int color = Color.GRAY;
        //if (settingsValues==null)
        //    return color;
        color = Color.GREEN;
        if (vv >= limits[alarmMax] || vv <= limits[alarmMin])
            color = Color.RED;
        else if (vv >= limits[warnMax] || vv <= limits[warnMin])
            color = Color.YELLOW;
        return color;
        }
    @Override
    public void repaintValues(){
        int dxOffset = getDxOffset();
        int dyOffset = getDyOffset();
        FormContext2 context= getContext();
        Meta2GUILevelMultiIndicator element = (Meta2GUILevelMultiIndicator) getElement();
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        vMin = limits[alarmMin] - (limits[alarmMax]-limits[alarmMin])*proc/100;
        vMax = limits[alarmMax] + (limits[alarmMax]-limits[alarmMin])*proc/100;
        /*
        if (settingsValues==null){
            high.setBounds(
                    context.x(element.getX()+dxOffset+5),
                    context.y(element.getY()+dyOffset),
                    context.x(w2),
                    context.y(h));
            return;
            }
         */
        for(int i=0;i<3;i++){
            if (vv[i]<vMin) vv[i]=vMin*1.03;
            if (vv[i]>vMax) vv[i]=vMax*0.98;
            }
        for(int i=0;i<3;i++)
            hh[i] = (int)((vv[i]-vMin)/(vMax-vMin)*h);
        //high.setBounds(element.getX()+dxOffset+5,element.getY()+dyOffset,w2,h-hh+2);
        int xx = element.getX()+dxOffset+5;
        int yy = element.getY()+dyOffset+h;
        setBounds(textField,
                context.x(xx),
                context.y(yy-hh[0]),
                context.x(w2),
                context.y(hh[0]-hh[1]));
        textField.setBackgroundColor(getValueColor(vv[0]));
        setBounds(textField3,
                context.x(xx),
                context.y(yy-hh[1]),
                context.x(w2),
                context.y(5));
        textField3.setBackgroundColor(getValueColor(vv[1]));
        setBounds(textField2,
                context.x(xx),
                context.y(yy-hh[1]+5),
                context.x(w2),
                context.y(hh[1]-hh[2]));
        textField2.setBackgroundColor(getValueColor(vv[2]));
    }

    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        Meta2GUILevelMultiIndicator element = (Meta2GUILevelMultiIndicator) getElement();
        if (!(register instanceof Meta2DataRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        Meta2RegLink[] links = getSettingsLinks();
        for(Meta2RegLink link : links){
            register = link.getRegister();
            if (!(register instanceof Meta2SettingRegister))
                return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
            }
        links = getDataLinks();
        for(Meta2RegLink link : links){
            register = link.getRegister();
            if (!(register instanceof Meta2DataRegister))
                return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
            }
        return null;
        }
    @Override
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister)  getRegister();
        String ss = "Индикатор уровня(3) для "+(set.getRegNum()+getRegOffset())+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+="Регистры уровней: ";
        for(Meta2RegLink link : getElement().getSettingsLinks())
            ss+=link.getRegister().getRegNum()+" ";
        ss+="Доп.регистры: ";
        for(Meta2RegLink link2 : getElement().getDataLinks())
            ss+=link2.getRegister().getRegNum()+" ";
        context.getMain().main().popupInfo(ss);
    }
}
