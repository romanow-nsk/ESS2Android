package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIFormButton;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIFormButton extends View2BaseDesktop {
    private BorderedButton textField;
    public DesktopGUIFormButton(){
        type = Values.GUIFormButton;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedButton(context.getMain().main(),textColor);
        FormContext2 context= getContext();
        Meta2GUI element = getElement();
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()),
                context.y(element.getY()),
                context.x(element.getDx()),
                context.y(hh));
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,textSize);
        textField.setClickable(true);
        textField.setText(element.getTitle());
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.openForm(((Meta2GUIFormButton)element).getFormName());
                }
            });
        setInfoClick(textField);
        panel.addView(textField);
        }
    @Override
    public void putValue(int vv) throws UniException {}
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