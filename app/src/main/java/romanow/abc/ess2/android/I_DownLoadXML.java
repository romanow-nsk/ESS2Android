package romanow.abc.ess2.android;

import romanow.abc.core.entity.metadata.Meta2XML;

public interface I_DownLoadXML {
    public void onSuccess(Meta2XML xml);
    public void onError(String mes);
}
