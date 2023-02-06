package romanow.abc.ess2.android.rendering.view2;

import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUILabel extends View2BaseDesktop {
        public DesktopGUILabel(){
            type = Values.GUILabel;
            }
        @Override
        public void addToPanel(RelativeLayout panel) {
            TextView label = setLabel(panel);          // Power==1 - с цветом
            setInfoClick(label);
            }
        @Override
        public void showInfoMessage() {}
        @Override
        public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
            super.setParams(context0,meta0, element0,onEvent0);
            return null;
            }
        @Override
        public void putValue(long vv) throws UniException {}
}
