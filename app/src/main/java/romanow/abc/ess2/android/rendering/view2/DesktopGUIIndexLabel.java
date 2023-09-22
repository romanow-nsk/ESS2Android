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
import romanow.abc.core.entity.metadata.view.Meta2GUIIndexLabel;
import romanow.abc.core.entity.metadata.view.Meta2GUIRegW2;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIIndexLabel extends View2BaseDesktop {
    public DesktopGUIIndexLabel(){
        type = Values.GUIIndexLabel;
        }
    private TextView label;
    @Override
    public void addToPanel(RelativeLayout panel) {
        label = setLabel(panel);
        setInfoClick(label);
        int level = ((Meta2GUIIndexLabel)getElement()).getStackLevel();
        label.setText("" + getElement().getTitle() + " " + (getContext().getIndex(level)+1));        }

    @Override
    public void putValue(long vv) throws UniException {}

    public void showInfoMessage() {
        String ss = "Индекс элемента формы уровня "+ ((Meta2GUIIndexLabel)getElement()).getStackLevel() ;
        context.getMain().main().popupInfo(ss);
        }
}