package romanow.abc.ess2.android.lep500;

import romanow.lep500.fft.FFTCallBack;
import romanow.lep500.fft.FFTStatistic;

public interface FFTCallBackPlus extends FFTCallBack {
    public FFTStatistic getStatistic();
}
