package romanow.abc.ess2.android.menu;

import java.io.FileInputStream;

import romanow.abc.ess2.android.FileDescription;
import romanow.abc.ess2.android.FileDescriptionList;
import romanow.abc.ess2.android.I_ArchiveMultiSelector;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.service.AppData;

public class MIESS2 extends MenuItem {
    public MIESS2(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("СМУ СНЭЭ") {
            @Override
            public void onSelect() {
                main.getArchitectureData().refreshArchtectureState();
                }
            });
        }
    }

