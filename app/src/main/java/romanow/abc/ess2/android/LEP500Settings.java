package romanow.abc.ess2.android;

import java.util.ArrayList;
import java.util.HashMap;


public class LEP500Settings extends LEP500Params {
    //----------------- Для javax.mail -------------------------------------------
    public String mailHost="mail.nstu.ru";
    public String mailBox="romanow@corp.nstu.ru";
    public String mailPass="";
    public String mailSecur="starttls";
    public int mailPort=587;
    public String fatalMessage="";          // Текст фатального сообщения при перезагрузке
    public boolean technicianMode=false;       // Полнофункциональный режим
    public LEP500Settings(){}
    public boolean isTechnicianMode() {
        return technicianMode; }
    public void setTechnicianMode(boolean technicianMode) {
        this.technicianMode = technicianMode; }
}
