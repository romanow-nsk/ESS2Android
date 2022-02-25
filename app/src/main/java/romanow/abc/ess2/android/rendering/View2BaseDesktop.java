package romanow.abc.ess2.android.rendering;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class View2BaseDesktop extends View2Base implements I_View2Desktop {
        public static void setBounds(View view, int xx, int yy, int dx, int dy){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dx,dy);
            params.setMargins(xx, yy, -1, -1);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            view.setLayoutParams(params);
            }
        public TextView setLabel(RelativeLayout panel){
            String text = element.getTitle();
            //------------- TODO ------------------- подпись индекса --------------------------------
            //if (getType()== Values.GUILabel && inGroup)               // НЕ ООП !!!!!!!!!!!!!!!!!!!
            //    text += " "+(groupIndex+1);
            int hh = element.getH();
            if (hh==0) hh=25;
            TextView label = new TextView(null);
            setBounds(
                    label,
                    context.x(element.getX()+dxOffset),
                    context.y(element.getY()+dyOffset),
                    context.x(element.getDx()),
                    context.y(hh));
            int size = element.getStringSize();
            //if (size==0)
                label.setText("  "+text);
            //else
            //    UtilsDesktop.setLabelText(label,text,size);
            label.setTextColor(context.getView().getTextColor());
            if (element.getColor()==0 || element.isCommonColor()){
                label.setBackgroundColor(context.getView().getLabelBackColor());
                }
            else{
                label.setBackgroundColor(element.getColor());
                }
            int fontSize = element.getFontSize();
            if (fontSize==0) fontSize=12;
            label.setTextSize(context.y(fontSize));
            panel.addView(label);
            return label;
            }
        }

