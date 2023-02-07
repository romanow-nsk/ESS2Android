package romanow.abc.ess2.android.rendering.module;


import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import retrofit2.Call;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.subjectarea.ArchESSEvent;
import romanow.abc.core.mongo.DBQueryBoolean;
import romanow.abc.core.mongo.DBQueryInt;
import romanow.abc.core.mongo.DBQueryList;
import romanow.abc.core.mongo.DBQueryLong;
import romanow.abc.core.mongo.DBXStream;
import romanow.abc.core.mongo.I_DBQuery;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.ess2.android.I_ListBoxListener;
import romanow.abc.ess2.android.ListBoxDialog;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;
import romanow.abc.ess2.android.service.NetBack;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;
import java.util.ArrayList;
import java.util.HashMap;

public class ModuleEventAll extends Module {
    public final static int EventsDeepth=1;                 // "Глубина" в днях чтения событий
    protected ArrayList<ArchESSEvent> events = new ArrayList<>();
    protected ArrayList<ArchESSEvent> selected = new ArrayList<>();
    private I_ListBoxListener back=null;
    public ModuleEventAll(){}
    private int types[] = {};
    public int[] eventTypes(){ return types; }
    public void setBack(I_ListBoxListener back0){
        back = back0;
        }
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client, panel, service, service2,token, form, formContext);
        repaintValues();
        }
    private void showTable(){
        HashMap<Integer, ConstValue> map = Values.constMap().getGroupMapByValue("EventType");
            ArrayList<String> list = new ArrayList<>();
            for(ArchESSEvent essEvent : selected){
                OwnDateTime dd = essEvent.getArrivalTime();
                String ss = dd.dateToString()+" "+dd.timeFullToString()+" ";
                ConstValue vv = map.get(essEvent.getType());
                if (vv!=null) ss+=vv.title();
                ss+="\n"+essEvent.getTitle();
                list.add(ss);
                }
        ListBoxDialog dialog = new ListBoxDialog(context.getMain().main(),list,getTitle(),back).setnLines(3).setTextSize(15).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        dialog.create();
        }

    @Override
    public void repaintValues() {
        super.repaintValues();
        long after = new OwnDateTime().timeInMS()-EventsDeepth*24*3600*1000;
        DBQueryList query =  new DBQueryList().
                add(new DBQueryLong(I_DBQuery.ModeGT,"a_timeInMS", after)).
                add(new DBQueryBoolean("valid",true));
        int types[] = eventTypes();
        if (types.length!=0){
            DBQueryList query2 = new DBQueryList(I_DBQuery.ModeOr);
            for (int type : types)
                query2.add(new DBQueryInt(I_DBQuery.ModeEQ,"type", type));
            query.add(query2);
            }
        final String xmlQuery = new DBXStream().toXML(query);
        new NetCall<ArrayList<DBRequest>>().call(context.getMain().main(),service.getEntityListByQuery(token,"ArchESSEvent",xmlQuery,0), new NetBackDefault(){
            @Override
            public void onSuccess(Object val) {
                    ArrayList<DBRequest> oo = (ArrayList<DBRequest>)val;
                    System.out.println("Прочитано событий "+oo.size());
                    events.clear();
                    Gson gson = new Gson();
                    for(DBRequest vv : oo){
                        try {
                            events.add((ArchESSEvent) vv.get(gson));
                            } catch (Exception ee){}
                        }
                    selected.clear();
                    for(int idx=events.size()-1;idx>=0;idx--){
                        ArchESSEvent essEvent = events.get(idx);
                        if (typeFilter(essEvent.getType()))
                            selected.add(essEvent);
                        }
                    System.out.println("Выбрано событий "+selected.size());
                    showTable();
                }
            });
        }
    public boolean typeFilter(int type) {
        return true;
        }
}
