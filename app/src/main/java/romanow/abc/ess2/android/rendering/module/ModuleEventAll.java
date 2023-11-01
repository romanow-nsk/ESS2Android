package romanow.abc.ess2.android.rendering.module;


import android.app.UiAutomation;
import android.graphics.Color;
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
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.BaseActivity;
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
    private I_ModuleBack back=null;
    public ModuleEventAll(){}
    private ArrayList<String> prevList = new ArrayList<>();
    private int types[] = {};
    public int[] eventTypes(){ return types; }
    public String getTitle(){ return "Все"; }
    public void setBack(I_ModuleBack back0){
        back = back0;
        }
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client0, panel, service, service2,token, form, formContext);
        if (getClass() == ModuleEventAll.class)
            repaintValues();
        }
    public void showTable(){
        showTable(null);
        }
    public void showTable(ArrayList<Integer> colors){
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
        boolean diff=false;
        if (prevList.size()!=list.size())
            diff=true;
        else{
            for(int i=0;i<list.size();i++)
                if (!list.get(i).equals(prevList.get(i))){
                    diff=true;
                    break;
                    }
                }
        if (!diff)
            return;
        prevList = list;
        MainActivity main = context.getMain().main();
        main.clearLog();
        main.addToLogButton(getTitle(), BaseActivity.greatTextSize, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (back!=null)
                    back.onHeader();
                }
            });
        int idx=0;
        for(String ss : list){
            final int idxx = idx++;
            main.addToLogButton(ss, false,BaseActivity.middleTextSize,colors==null ? 0 : colors.get(idxx),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (back != null)
                                back.onSelect(idxx);
                            }
                        },
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (back != null)
                                back.onLongSelect(idxx);
                            return false;
                        }
                    }
                );
            }
        main.scrollUp();
        //ListBoxDialog dialog = new ListBoxDialog(context.getMain().main(),
        //        MainActivity.createMenuList(list),getTitle(),back).setnLines(3).setTextSize(15).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        //dialog.create();
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
