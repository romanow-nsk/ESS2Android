package romanow.abc.ess2.android.menu;

import static romanow.abc.ess2.android.MainActivity.ViewProcHigh;

import java.io.FileInputStream;
import java.io.InputStream;

import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.I_ArchiveMultiSelector2;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.FileDescription;
import romanow.abc.ess2.android.FileDescriptionList;


public class MIFileProcess extends MIFileBrowser {
    private boolean full=false;
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector2 procFiles = new I_ArchiveMultiSelector2() {
        @Override
        public void onSelect(String path, FileDescriptionList fd, boolean longClick) {
            main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
            main.setFullInfo(full);
            main.hideFFTOutput = !full;
            for (FileDescription ff : fd){
                try{
                    final InputStream is = new FileInputStream(path+"/"+ff.getOriginalFileName());
                    if (is==null){
                        main.errorMes("Файл "+ff.getOriginalFileName()+" не найден");
                        continue;
                        }
                    } catch (Throwable ee){
                        main.errorMes(ff.getOriginalFileName()+"\n"+ee.toString());
                        }
                    }
            }
        };
    public MIFileProcess(MainActivity main0, boolean full) {
        super(main0,"Файлы "+(!full ? "кратко" : "подробно"));
        this.full = full;
        setProcSelector(procFiles);
        }
}
