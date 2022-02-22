package romanow.abc.ess2.android.lep500.menu;

import static me.romanow.lep500.MainActivity.ViewProcHigh;

import java.io.FileInputStream;
import java.io.InputStream;

import me.romanow.lep500.R;
import romanow.abc.ess2.android.lep500.I_ArchiveMultiSelector2;
import romanow.abc.ess2.android.lep500.MainActivity;
import romanow.lep500.FileDescription;
import romanow.lep500.FileDescriptionList;

public class MIFileProcess extends MIFileBrowser {
    private boolean full=false;
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector2 procFiles = new I_ArchiveMultiSelector2() {
        @Override
        public void onSelect(String path, FileDescriptionList fd, boolean longClick) {
            main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
            main.setFullInfo(full);
            main.hideFFTOutput = !full;
            main.defferedStart();
            for (FileDescription ff : fd){
                try{
                    final InputStream is = new FileInputStream(path+"/"+ff.getOriginalFileName());
                    if (is==null){
                        main.errorMes("Файл "+ff.getOriginalFileName()+" не найден");
                        continue;
                        }
                    main.processInputStream(ff,is,ff.getOriginalFileName());
                    } catch (Throwable ee){
                        main.errorMes(ff.getOriginalFileName()+"\n"+ee.toString());
                        }
                    }
            main.defferedFinish();
            }
        };
    public MIFileProcess(MainActivity main0, boolean full) {
        super(main0,"Файлы "+(!full ? "кратко" : "подробно"));
        this.full = full;
        setProcSelector(procFiles);
        }
}
