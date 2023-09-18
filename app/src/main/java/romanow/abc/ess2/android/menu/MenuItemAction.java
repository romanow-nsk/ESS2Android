package romanow.abc.ess2.android.menu;

import android.graphics.Bitmap;

public abstract class MenuItemAction {
    public final String title;
    public abstract void onSelect();
    public MenuItemAction(String title) {
        this.title = title;
    }
    public Bitmap getBitmap(){ return null; }
}
