package romanow.abc.ess2.android.menu;

import android.graphics.Bitmap;

public class MenuItemRenderAction extends MenuItemAction{
    private Bitmap bitmap;
    private Runnable action;
    @Override
    public void onSelect() {
        action.run();
        }
    public MenuItemRenderAction(String title, Runnable action0) {
        super(title);
        action = action0;
        }
    public void setBitmap(Bitmap bitmap0){
        bitmap = bitmap0; }
    @Override
    public Bitmap getBitmap(){ return bitmap; }

}
