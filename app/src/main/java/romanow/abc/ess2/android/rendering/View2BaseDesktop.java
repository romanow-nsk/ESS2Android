package romanow.abc.ess2.android.rendering;

import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.types.TypeFace;
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
                    context.dx(element.getDx()),
                    context.dy(hh));
            int size = element.getStringSize();
            label.setText(element.isLabelBold() ? Html.fromHtml("<b>" + text + "</b>") : text);
            label.setTextColor(context.getView().getTextColor() | 0xFF000000);
            label.setTextAlignment(element.isLabelOnCenter() ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_TEXT_START);
            label.setBackgroundColor(getBackColor());
            int fontSize = element.getFontSize();
            if (fontSize==0) fontSize=DefaultTextSize;
            setTextSize(label,context.dy(fontSize));
            setLongClickInfo(label);
            panel.addView(label);
            if (element.isBold())
                label.setTypeface(null, Typeface.BOLD);
            return label;
            }
        public void setTextSize(TextView textView, int size){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);// (float) (size*AppData.ScreenMas+5));
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
                return context.getView().getCommonBackColor()  | 0xFF000000;
            else
                return element.getColor() | 0xFF000000;
            }
    public void setTextFieldParams(TextView textField) {
        textField.setBackgroundColor(getElemBackColor());
        // border = true
        textField.setText(element.isBold() ? Html.fromHtml("<b>" + element.getTitle() + "</b>") : element.getTitle());
        textField.setTextAlignment(element.isOnCenter() ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_VIEW_START);
        textField.setTextColor(context.getView().getTextColor() | 0xFF000000);
        int fontSize = element.getFontSize();
        if (fontSize == 0) fontSize = 12;
        setTextSize(textField,context.dy(fontSize));
        }
    public void setButtonParams(Button textField) {
        setButtonParams(textField, false);
        }
    public void setButtonParams(Button textField, boolean noOneString) {
        textField.setBackgroundColor(getElemBackColor());
        String ss = element.getTitle();
        textField.setText(!noOneString ? ss : "<html>"+(element.isOnCenter() ? "<center>" : "") + ss.replaceAll(" ", "<br>") + "</html>");
        textField.setTextAlignment(element.isOnCenter() ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_VIEW_START);
        textField.setTextColor(context.getView().getTextColor() | 0xFF000000);
        int fontSize = element.getFontSize();
        if (fontSize == 0) fontSize = 12;
        setTextSize(textField,context.dy(fontSize));
        }
    public int getElemBackColor(){
        if (element.isBackColor()){
            return  context.getView().getBackColor();
        }
        else{
            if (element.getColor()==0 || element.isCommonColor()){
                return context.getView().getCommonBackColor();
            }
            else{
                return element.getColor();
                }
            }
        }
    public int getLabelColor(){
        if (element.isLabelBackColor()){
            return  context.getView().getBackColor();
        }
        else{
            if (element.getLabelColor()==0 || element.isLabelCommonColor()){
                return context.getView().getCommonBackColor();
            }
            else{
                return element.getLabelColor();
                }
            }
        }
    }

