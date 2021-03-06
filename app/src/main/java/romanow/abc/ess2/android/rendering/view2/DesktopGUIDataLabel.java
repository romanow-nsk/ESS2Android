package romanow.abc.ess2.android.rendering.view2;

import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
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
        setInfoClick(label);
        }
    public void showInfoMessage() {
        Meta2DataRegister set = (Meta2DataRegister) getRegister();
        String ss = "Регистр данных "+(set.getRegNum()+getRegOffset()+" ["+set.getRegNum()+"] "+set.getShortName()+"$"+set.getTitle()+"$");
        ss+="Потоковый  - "+(set.getStreamType()!=Values.DataStreamNone ? "да":"нет")+",";
        ss+=" Ед.изм. "+ set.getUnit();
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(int vv) throws UniException {
        Meta2DataRegister register = (Meta2DataRegister) getRegister();
        int type = register.getFormat();
        if (type==Values.FloatValue)
            label.setText(getElement().getTitle()+" "+Float.intBitsToFloat(vv));
        else
            label.setText(getElement().getTitle()+" "+register.valueWithPower(vv));
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
