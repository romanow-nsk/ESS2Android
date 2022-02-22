package romanow.abc.ess2.android.lep500.menu;

import java.io.FileInputStream;

import romanow.abc.ess2.android.lep500.FFTPlayerAdapter;
import romanow.abc.ess2.android.lep500.I_ArchiveSelector;
import romanow.abc.ess2.android.lep500.MainActivity;
import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.lep500.FileDescription;

public class MIResultsPlayer extends MenuItem {
    public MIResultsPlayer(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Слушать пики спектра") {
            @Override
            public void onSelect() {
                main.selectFromArchive("Слушать пики спектра",voiceResultsSelector);
                }
            });
        }
    //------------------------------------------------------------------------------------------
    private I_ArchiveSelector voiceResultsSelector = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            new VoicePlayer(fd,main){
                @Override
                public void convert(String outFile, FileDescription fd) {
                    try {
                        String pathName = AppData.ctx().androidFileDirectory() + "/" + fd.getOriginalFileName();
                        FileInputStream fis = new FileInputStream(pathName);
                        main.processInputStream(false,fd,fis,"",new FFTPlayerAdapter(main,"",outFile));
                        } catch (Throwable e) {
                            main.errorMes("Файл не открыт: "+fd.getOriginalFileName()+"\n"+main.createFatalMessage(e,10));
                            }
                }
            };
        }
    };
}
