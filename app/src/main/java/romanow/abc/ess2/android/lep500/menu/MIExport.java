package romanow.abc.ess2.android.lep500.menu;

import java.io.FileInputStream;

import romanow.abc.ess2.android.lep500.FFTExcelAdapter;
import romanow.abc.ess2.android.lep500.I_ArchiveMultiSelector;
import romanow.abc.ess2.android.lep500.MainActivity;
import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.lep500.FileDescription;
import romanow.lep500.FileDescriptionList;

public class MIExport extends MenuItem {
    public MIExport(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Экспорт в Excel") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Экспорт в Excel",exportSelector);
                }
            });
        }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector exportSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList flist, boolean longClick) {
            for(FileDescription fd : flist) {
                try {
                    String pathName = AppData.ctx().androidFileDirectory() + "/" + fd.getOriginalFileName();
                    FileInputStream fis = new FileInputStream(pathName);
                    main.processInputStream(false, fd, fis, "", new FFTExcelAdapter(main, "", fd));
                    } catch (Throwable e) {
                        main.errorMes("Файл не открыт: " + fd.getOriginalFileName() + "\n" + main.createFatalMessage(e, 10));
                        }
                }
            }
        };
    }

