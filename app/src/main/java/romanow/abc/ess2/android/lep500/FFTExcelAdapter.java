package romanow.abc.ess2.android.lep500;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import static me.romanow.lep500.MainActivity.createFatalMessage;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.abc.ess2.android.lep500.service.BaseActivity;
import romanow.lep500.FileDescription;
import romanow.lep500.fft.Extreme;
import romanow.lep500.fft.ExtremeFacade;
import romanow.lep500.fft.ExtremeList;
import romanow.lep500.fft.FFT;
import romanow.lep500.fft.FFTStatistic;

public class FFTExcelAdapter implements FFTCallBackPlus {
    private FFTStatistic inputStat;
    private MainActivity main;
    private FileDescription fd;
    public String createOriginalExcelFileName(){
        String dirName = AppData.ctx().androidFileDirectory()+"/"+ AppData.excelDir;
        File ff = new File(dirName);
        if (!ff.exists()){
            ff.mkdir();
            }
        String pathName = dirName+"/"+fd.getOriginalFileName();
        int k = pathName.lastIndexOf(".");
        pathName = pathName.substring(0, k) + ".xls";
        return pathName;
        }
    public FFTExcelAdapter(BaseActivity main0, String title, FileDescription fd0){
        inputStat = new FFTStatistic(title);
        inputStat.setFreq(fd0.getFileFreq());
        main = (MainActivity) main0;
        fd = fd0;
        }
    @Override
    public void onStart(double msOnStep) {}
    @Override
    public void onFinish() {
        if (inputStat.getCount()==0){
            main.popupAndLog("Настройки: короткий период измерений/много блоков");
            return;
            }
        inputStat.smooth(AppData.ctx().set().kSmooth);
        double max = inputStat.normalizeStart(AppData.ctx().set().nTrendPointsSpectrum);
        inputStat.normalizeFinish(max);
        int sz = inputStat.getMids().length;
        ExtremeList list = inputStat.createExtrems(FFTStatistic.ExtremeAbsMode, AppData.ctx().set());
        if (list.data().size()==0){
            main.addToLog("Экстремумов не найдено");
            return;
            }
        Extreme extreme;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Параметры");
        Row hd;
        hd = sheet.createRow(0);
        hd.createCell(0).setCellValue("Геолокация: "+fd.getGps().toString());
        hd = sheet.createRow(1);
        hd.createCell(0).setCellValue("Линия: "+fd.getPowerLine());
        hd = sheet.createRow(2);
        hd.createCell(0).setCellValue("Опора: "+fd.getSupport());
        hd = sheet.createRow(2);
        hd.createCell(0).setCellValue("Дата создания: "+fd.getCreateDate().dateTimeToString());
        hd = sheet.createRow(3);
        hd.createCell(0).setCellValue("Частота: "+String.format("%6.2f",fd.getFileFreq()));
        hd = sheet.createRow(4);
        hd.createCell(0).setCellValue("Датчик: "+fd.getSensor());
        hd = sheet.createRow(5);
        hd.createCell(0).setCellValue("Номер измерения: "+fd.getMeasureCounter());
        double dd[] = inputStat.getNormalized();
        hd = sheet.createRow(6);
        hd.createCell(0).setCellValue("Частот в спектре: "+dd.length);
        Pair<String,Integer> ss = list.testAlarm2(AppData.ctx().set(),inputStat.getFreqStep());
        if (ss.o1!=null){
            hd = sheet.createRow(7);
            hd.createCell(0).setCellValue(ss.o1);
            }
        sheet = workbook.createSheet("Спектр");
        for(int i=0;i<dd.length;i++){
            hd = sheet.createRow(i);
            hd.createCell(0).setCellValue(""+(i+1));
            hd.createCell(1).setCellValue(i*fd.getFileFreq()/dd.length/2);
            hd.createCell(2).setCellValue(dd[i]);
            }
        for(int i=0;i<FFTStatistic.extremeFacade.length;i++)
            writeExtrems(i,workbook);
        String pathName = createOriginalExcelFileName();
        try (FileOutputStream out = new FileOutputStream(new File(pathName))) {
            workbook.write(out);
            out.close();
            } catch (IOException e) { main.addToLog("Ошибка экспорта: "+e.toString());  }
        main.addToLog("Экспорт в Excel: "+fd.getOriginalFileName());
        }
    private void writeExtrems(int mode, HSSFWorkbook workbook){
        String ss="";
        try {
            ss=((ExtremeFacade)FFTStatistic.extremeFacade[mode].newInstance()).getTitle();
            } catch (Exception ee){ }
        HSSFSheet sheet = workbook.createSheet(ss);
        int sz = inputStat.getMids().length;
        Row hd;
        hd = sheet.createRow(0);
        hd.createCell(0).setCellValue("Диапазон экстремумов:");
        hd.createCell(1).setCellValue(AppData.ctx().set().FirstFreq);
        hd.createCell(2).setCellValue(AppData.ctx().set().LastFreq);
        ExtremeList list = inputStat.createExtrems(mode, AppData.ctx().set());
        if (list.data().size()==0){
            hd = sheet.createRow(1);
            hd.createCell(0).setCellValue("Экстремумов не найдено");
            return;
            }
        Extreme extreme;
        extreme = list.data().get(0);
        hd = sheet.createRow(2);
        hd.createCell(0).setCellValue("Осн. частота:");
        hd.createCell(1).setCellValue( extreme.idx*inputStat.getFreqStep());
        if (extreme.decSize!=-1)
            hd.createCell(2).setCellValue( Math.PI*extreme.decSize/extreme.idx);
        int count = main.nFirstMax < list.data().size() ? main.nFirstMax : list.data().size();
        ExtremeFacade facade = list.getFacade();
        facade.setExtreme(list.data().get(0));
        double val0 = facade.getValue();
        extreme = facade.extreme();
        hd = sheet.createRow(3);
        hd.createCell(0).setCellValue("Ампл");
        hd.createCell(1).setCellValue("\u0394спад");
        hd.createCell(2).setCellValue("\u0394тренд");
        hd.createCell(3).setCellValue("f(гц)");
        hd.createCell(4).setCellValue("Декремент");
        hd = sheet.createRow(4);
        hd.createCell(0).setCellValue(extreme.value);
        hd.createCell(1).setCellValue(extreme.diff);
        hd.createCell(2).setCellValue(extreme.trend);
        hd.createCell(3).setCellValue(extreme.idx*inputStat.getFreqStep());
        if (extreme.decSize!=-1)
            hd.createCell(4).setCellValue(Math.PI*extreme.decSize/extreme.idx);
        double sum=0;
        for(int i=1; i<count;i++){
            facade = list.getFacade();
            facade.setExtreme(list.data().get(i));
            double proc = facade.getValue()*100/val0;
            sum+=proc;
            extreme = facade.extreme();
            hd = sheet.createRow(4+i);
            hd.createCell(0).setCellValue(extreme.value);
            hd.createCell(1).setCellValue(extreme.diff);
            hd.createCell(2).setCellValue(extreme.trend);
            hd.createCell(3).setCellValue(extreme.idx*inputStat.getFreqStep());
            if (extreme.decSize!=-1)
                hd.createCell(4).setCellValue(Math.PI*extreme.decSize/extreme.idx);
            }
        hd = sheet.createRow(4+count);
        hd.createCell(0).setCellValue(String.format("Средний - %d%% к первому",(int)(sum/(count-1))));
        }
    @Override
    public boolean onStep(int nBlock, int calcMS, double totalMS, FFT fft) {
        inputStat.setFreqStep(fft.getStepHZLinear());
        long tt = System.currentTimeMillis();
        double lineSpectrum[] = fft.getSpectrum();
        boolean xx;
        try {
            inputStat.addStatistic(lineSpectrum);
            } catch (Exception ex) {
                main.addToLog(createFatalMessage(ex,10));
                return false;
                }
        return true;
        }
    @Override
    public void onError(Exception ee) {
        main.errorMes(createFatalMessage(ee,10));
    }
    @Override
    public void onMessage(String mes) {
        main.addToLogHide(mes);
        }

    @Override
    public FFTStatistic getStatistic() {
        return inputStat;
    }
}
