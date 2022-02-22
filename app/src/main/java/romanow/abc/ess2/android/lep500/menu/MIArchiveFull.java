package romanow.abc.ess2.android.lep500.menu;

import static me.romanow.lep500.MainActivity.ViewProcHigh;

import me.romanow.lep500.R;
import romanow.abc.ess2.android.lep500.I_ArchiveMultiSelector;
import romanow.abc.ess2.android.lep500.MainActivity;
import romanow.lep500.FileDescription;
import romanow.lep500.FileDescriptionList;

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
            main.defferedStart();
            for (FileDescription ff : fd){
                main.procArchive(ff,true);
                }
            main.defferedFinish();
        }
    };

}
