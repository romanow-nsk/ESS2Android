package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2DateTime;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIData;
import romanow.abc.core.entity.metadata.view.Meta2GUIDateTime;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIDateTime  extends View2BaseDesktop {
    private TextView textField;
    public DesktopGUIDateTime(){ type = Values.GUIDateTime; }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        Meta2GUIDateTime element2 = (Meta2GUIDateTime) element;
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
        //int textSize = element2.getFontSize();
        //if (textSize==0) textSize = DefaultTextSize;
        //setTextSize(textField,textSize);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        //textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textField.setBackgroundColor(getBackColor());
        //textField.setTextColor(textColor);
        textField.setClickable(false);
        panel.addView(textField);
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
        int type = register.getFormat();
        String ss = String.format("%2d",vv & 0x0FF);
        vv >>=8;
        ss = String.format("%2d:",vv & 0x0FF)+ss;
        textField.setText(ss);
        vv >>=8;
        ss = String.format("%2d:",vv & 0x0FF)+ss;
        vv >>=8;
        String ss2 = String.format("%2d-",vv & 0x0FF);
        vv >>=8;
        ss2 = ss2+String.format("%2d-",vv & 0x0FF);
        vv >>=8;
        ss2 = ss2+String.format("%2d",(vv & 0x0FF)+2000);
        textField.setText(" "+ss2+" "+ss);
        }

    @Override
    public String setParams(FormContext2 context, ESS2Architecture meta, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context,meta, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2DateTime))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }

}
