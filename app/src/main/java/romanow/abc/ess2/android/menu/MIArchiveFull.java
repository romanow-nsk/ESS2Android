package romanow.abc.ess2.android.menu;

import static romanow.abc.ess2.android.MainActivity.ViewProcHigh;

import romanow.abc.ess2.android.FileDescription;
import romanow.abc.ess2.android.FileDescriptionList;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.I_ArchiveMultiSelector;
import romanow.abc.ess2.android.MainActivity;

public class MIArchiveFull extends MenuItem {
    public MIArchiveFull(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Архив подробно") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Проcмотр архива",procViewMultiSelectorFull);
            }
        });
    }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procViewMultiSelectorFull = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
            for (FileDescription ff : fd){
                main.procArchive(ff,true);
                }
        }
    };

}
