package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIData;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.service.AppData;

public class DesktopGUIData extends View2BaseDesktop {
    private TextView textField;
    public DesktopGUIData(){
        type = Values.GUIData;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        Meta2GUIData element2 = (Meta2GUIData)element;
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element2.getW2();
        if (dd==0) dd=DefaultW2;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+dxOffset+element.getDx()+DefaultSpace),
                context.y(element.getY()+dyOffset),
                context.x(dd),
                context.y(hh));
        int textSize = element2.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        textField.setTextSize(textSize);
        textField.setClickable(false);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        textField.setBackgroundColor(getBackColor());
        textField.setTextColor(textColor);
        setInfoClick(textField);
    }
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister)  getRegister();
        String ss = "?????????????? ???????????? "+(set.getRegNum()+regOffset)+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+="??????????????????  - "+(set.getStreamType()!=Values.DataStreamNone ? "????":"??????")+",";
        ss+=" ????.??????. "+ set.getUnit();
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(int vv) throws UniException {
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        int type = register.getFormat();
        if (type==Values.FloatValue)
            textField.setText(""+Float.intBitsToFloat(vv));
        else
            textField.setText(register.valueWithPower(vv));
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2DataRegister || register instanceof Meta2SettingRegister))
            return "???????????????????????? "+register.getTypeName()+" ?????? "+getTypeName();
        return null;
        }
}