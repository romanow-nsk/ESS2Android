package romanow.abc.ess2.android.lep500.menu;

import static me.romanow.lep500.MainActivity.ViewProcHigh;

import me.romanow.lep500.R;
import romanow.abc.ess2.android.lep500.FFTAdapter;
import romanow.abc.ess2.android.lep500.LEP500Settings;
import romanow.abc.ess2.android.lep500.MainActivity;
import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.lep500.fft.FFT;
import romanow.lep500.fft.FFTAudioSource;
import romanow.lep500.fft.FFTHarmonic;
import romanow.lep500.fft.FFTParams;

public class MITestSignal extends MenuItem {
    public MITestSignal(MainActivity main0) {
        super(main0);
        if (!AppData.ctx().set().fullInfo)
            return;
        main.addMenuList(new MenuItemAction("Тестовый сигнал") {
            @Override
            public void onSelect() {
                LEP500Settings set = AppData.ctx().set();
                main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
                main.defferedStart();
                FFTParams params = new FFTParams().W(set.p_BlockSize* FFT.Size0).procOver(set.p_OverProc).
                            compressMode(false).winMode(set.winFun).freqHZ(set.measureFreq);
                FFT fft = new FFT();
                fft.setFFTParams(params);
                fft.calcFFTParams();
                double hz[]={3,5,8,13,21,34,48};
                double ampl[]={1,1,1,1,1,1,1};
                FFTAudioSource source = new FFTHarmonic(set.measureFreq,hz,ampl,set.measureDuration,0.1);
                main.addToLogHide("Отсчетов: "+source.getFrameLength());
                main.addToLogHide("Кадр: "+set.p_BlockSize*FFT.Size0);
                main.addToLogHide("Перекрытие: "+set.p_OverProc);
                main.addToLogHide("Дискретность: "+String.format("%5.4f",fft.getStepHZLinear())+" гц");
                FFTAdapter adapter = new FFTAdapter(main,title);
                fft.fftDirect(source,adapter);
                adapter.getStatistic().setFreq(set.measureFreq);
                main.normalize();
                main.showStatistic(main.deffered().get(0),0);
                main.paintOne(AppData.ctx().set().measureFreq);
                }
            });
        }

}
