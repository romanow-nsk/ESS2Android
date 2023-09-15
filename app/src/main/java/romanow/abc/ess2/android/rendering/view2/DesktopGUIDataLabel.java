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

public class DesktopGUIDataLabel extends View2BaseDesktop {
    private TextView label;
    public DesktopGUIDataLabel(){
        type = Values.GUIDataLabel;
    }

    @Override
    public void addToPanel(RelativeLayout panel) {
        label = setLabel(panel);
        label.setTextColor(getElemBackColor() | 0xFF000000);
        setInfoClick(label);
        }
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister) getRegister();
        String ss = "Регистр данных "+(set.getRegNum()+getRegOffset()+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$");
        ss+="Потоковый  - "+(set.getStreamType()!=Values.DataStreamNone ? "да":"нет")+",";
        ss+=" Ед.изм. "+ set.getUnit();
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        Meta2Register register = (Meta2Register) getRegister();
        int type = register.getFormat();
        Meta2GUIRegW2 metaGUI = (Meta2GUIRegW2)getElement();
        String ss = register.regValueToString(getUnitIdx(),(int)vv,metaGUI);
        label.setText("" + getElement().getTitle() + "=" + ss + " " + register.getUnit());
        /* ------------ Старая версия
        Meta2Register register = getRegister();
        if (((Meta2GUIData)getElement()).isByteSize()){
            label.setText(""+(byte)vv);
            return;
            }
        if (type==Values.FloatValue)
            label.setText(""+Float.intBitsToFloat((int)vv));
        else{
            if (register instanceof Meta2DataRegister){
                if (((Meta2GUIData) getElement()).isIntValue())
                    label.setText(((Meta2DataRegister)register).regValueToIntString(getUnitIdx(),(int)vv));
                else
                    label.setText(((Meta2DataRegister)register).regValueToString(getUnitIdx(),(int)vv));
            }
            else{
                if (((Meta2GUIData) getElement()).isIntValue())
                    label.setText(((Meta2SettingRegister)register).regValueToIntString(getUnitIdx(),(int)vv));
                else
                    label.setText(((Meta2SettingRegister)register).regValueToString(getUnitIdx(),(int)vv));
            }
        }
         */
    }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register =  getRegister();
        if (!(register instanceof Meta2DataRegister || register instanceof Meta2SettingRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
    }
}
