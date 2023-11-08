package romanow.abc.ess2.android.rendering;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import romanow.abc.core.entity.artifacts.Artifact;
import romanow.abc.core.types.TypeFace;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.R;
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
            if (element.isLabelOnCenter())
                label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                label.setTextAlignment(element.isLabelOnRight() ? View.TEXT_ALIGNMENT_TEXT_END: View.TEXT_ALIGNMENT_TEXT_START);
            label.setBackgroundColor(getLabelColor() | 0xFF000000);
            int fontSize = element.getFontSize();
            if (fontSize==0) fontSize=DefaultTextSize;
            setTextSize(label,context.dy(fontSize));
            setLongClickInfo(label);
            setInfoClick(label);
            panel.addView(label);
            if (element.isLabelBold())
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
        textField.setBackgroundColor(getElemBackColor() | 0xFF000000);
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
        textField.setBackgroundColor(getElemBackColor() | 0xFF000000);
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
        //------------------------------------------------------------------------------------------------------------------------
        public static void loadImage(MainActivity main, Artifact art, I_Value<Bitmap> back){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Call<ResponseBody> call2 = AppData.ctx().getService().downLoad(AppData.ctx().loginSettings().getSessionToken(),art.getOid());
                        Response<ResponseBody> response = call2.execute();
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            final Bitmap bitmap0 = BitmapFactory.decodeStream(body.byteStream());
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    back.onEnter(bitmap0);
                                    }
                                });
                            }
                        else{
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        main.popupAndLog(response.message()+response.errorBody().string());
                                        back.onEnter(null);
                                    } catch (IOException e) {}
                                }
                            });
                        }
                    } catch (Exception ee){
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.popupAndLog("Ошибка загрузки: "+art.getTitle()+"\n"+ee.toString());
                                back.onEnter(null);
                                }
                            });
                        }
                    }
                }).start();
            }
    }

