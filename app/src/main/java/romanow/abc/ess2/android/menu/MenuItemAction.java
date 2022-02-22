package romanow.abc.ess2.android.menu;

public abstract class MenuItemAction {
    public final String title;
    public abstract void onSelect();
    public MenuItemAction(String title) {
        this.title = title;
    }
}
