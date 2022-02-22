package romanow.abc.ess2.android.lep500;

import static me.romanow.lep500.MainActivity.createFatalMessage;

import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.abc.ess2.android.lep500.service.BaseActivity;
import romanow.lep500.fft.FFT;
import romanow.lep500.fft.FFTStatistic;

public class FFTAdapter implements FFTCallBackPlus {
    private FFTStatistic inputStat;
    private BaseActivity main;
    public FFTAdapter(BaseActivity main0, String title){
        inputStat = new FFTStatistic(title);
        main = main0;
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
        main.defferedAdd(inputStat);
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
