package romanow.abc.ess2.android.rendering;

import android.graphics.Typeface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.ess2.android.service.AppData;

public abstract class View2BaseDesktop extends View2Base implements I_View2Desktop {
        public final static int DefaultTextSize=14;
        public final static int DefaultH=20;
        public final static int DefaultSpace=5;
        public final static int DefaultW2=50;
        public static void setBounds(View view, int xx, int yy, int dx, int dy){
            //AppData.ctx().popupAndLog(false,""+xx+" "+yy+" "+dx+" "+dy);
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
            if (hh==0) hh=DefaultH;
            TextView label = new TextView(context.getMain().main());
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
            label.setTextColor(context.getView().getTextColor() | 0xFF000000);
            label.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            label.setBackgroundColor(getBackColor());
            int fontSize = element.getFontSize();
            if (fontSize==0) fontSize=DefaultTextSize;
            setTextSize(label,fontSize);
            setLongClickInfo(label);
            panel.addView(label);
            if (element.isBold())
                label.setTypeface(null, Typeface.BOLD);
            return label;
            }
        public void setTextSize(TextView textView, int size){
            textView.setTextSize((float) (size*AppData.ScreenMas+5));
            }
        public void setLongClickInfo(View view){
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showInfoMessage();
                    return false;
                    }
                });
            }
        public int getBackColor(){
            if (element.getColor()==0 || element.isCommonColor())
                return context.getView().getLabelBackColor()  | 0xFF000000;
            else
                return element.getColor() | 0xFF000000;
            }
        }

