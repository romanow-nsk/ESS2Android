package romanow.abc.ess2.android.lep500.service;

import romanow.abc.core.UniException;

public interface NetBack {
    public void onError(int code, String mes);
    public void onError(UniException ee);
    public void onSuccess(Object val);
    }
