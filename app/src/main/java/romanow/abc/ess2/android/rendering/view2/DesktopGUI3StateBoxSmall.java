package romanow.abc.ess2.android.rendering.view2;

import android.view.View;

import romanow.abc.core.constants.Values;

public class DesktopGUI3StateBoxSmall extends DesktopGUI3StateBox {
    public DesktopGUI3StateBoxSmall() {
       type = Values.GUI3StateBoxSmall;
       }
    private int iconsWarning[]={ 0xFFC8C8C8,0xFF00FFFF,0xFFFF0000,0xFF0000FF};
    private int iconsWorking[]={ 0xFFC8C8C8,0xFF00FF00,0xFFFF0000,0xFF0000FF};
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
    public void putValue(long vv) {
        BorderedTextView bb = (BorderedTextView)  textField;
        int pair = (int)((vv>>bitNum) & 03);
        int color = getContext().getForm().getFormLevel()==0 ? iconsWarning[pair] : iconsWorking[pair];
        bb.setBackgroundColor(color);
        }
    @Override
    protected int getSize(){
        return 15;
        }
}
