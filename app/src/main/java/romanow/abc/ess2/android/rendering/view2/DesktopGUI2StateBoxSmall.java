package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.TextView;

import romanow.abc.core.constants.Values;

public class DesktopGUI2StateBoxSmall extends DesktopGUI2StateBox {
    public DesktopGUI2StateBoxSmall() {
        type = Values.GUI2StateBoxSmall;
        }
    @Override
    protected View createComponent() {
        int textColor = context.getView().getTextColor() | 0xFF000000;
        return new BorderedTextView(context.getMain().main(),textColor);
        }
    @Override
    protected void viewComponent() {
        BorderedTextView bb = (BorderedTextView)  textField;
        bb.setEnabled(false);
        bb.setText("");
        }
    @Override
    protected void putValueOwn(int cc) {
        BorderedTextView bb = (BorderedTextView)  textField;
        bb.setBackgroundColor(cc);
        bb.setText("");
        }
    @Override
    protected int getSize(){
        return 15;
        }
}