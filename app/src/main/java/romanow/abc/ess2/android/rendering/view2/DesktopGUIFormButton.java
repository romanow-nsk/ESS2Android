package romanow.abc.ess2.android.rendering.view2;

import android.graphics.Bitmap;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIFormButton;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIFormButton extends View2BaseDesktop {
    private  Button textField;
    public DesktopGUIFormButton(){
        type = Values.GUIFormButton;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        int textColor = context.getView().getTextColor() | 0xFF000000;
        MainActivity main = context.getMain().main();
        //ContextThemeWrapper newContext = new ContextThemeWrapper(main, R.style.BlackButton);
        //textField = new Button(newContext);
        LinearLayout xx=(LinearLayout)main.getLayoutInflater().inflate(R.layout.listbox_item, null);
        xx.setPadding(5, 5, 5, 5);
        textField=(Button) xx.findViewById(R.id.dialog_listbox_name);
        ImageButton img=(ImageButton) xx.findViewById(R.id.dialog_listbox_img);
        xx.removeView(img);
        FormContext2 context= getContext();
        Meta2GUI element = getElement();
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(xx,
                context.x(element.getX()),
                context.y(element.getY()),
                context.dx(element.getDx()),
                context.dy(hh));
        //setButtonParams(textField,true);
        xx.setBackgroundColor(context.getView().getBackColor() | 0xFF000000);
        textField.setClickable(true);
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,context.dy(textSize));
        textField.setText(element.getTitle());
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textField.setBackgroundColor(getElemBackColor() | 0xFF000000);
        //textField.setTextColor(context.getView().getTextColor() | 0xFF000000);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Meta2GUIFormButton elem =(Meta2GUIFormButton)element;
                if (elem.isOwnUnit() && elem.getUnitLevel()!=0){
                    context.setIndex(elem.getUnitLevel(),elem.getUnitIdx());
                    }
                context.openForm(elem.getFormName(),FormContext2.ModeNext);
                }
            });
        setInfoClick(textField);
        panel.addView(xx);
        }
    @Override
    public void putValue(long vv) throws UniException {}
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        return null;
        }
    @Override
    public void showInfoMessage() {
        }
    public boolean needRegister() {
        return false;
        }
}