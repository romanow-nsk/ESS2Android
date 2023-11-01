package romanow.abc.ess2.android.rendering.module;

import android.graphics.Color;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import retrofit2.Call;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.subjectarea.ArchESSEvent;
import romanow.abc.core.entity.subjectarea.Failure;
import romanow.abc.core.entity.subjectarea.FailureSetting;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.APICall2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

import java.util.ArrayList;

public class ModuleFailure extends ModuleEventAll {
    protected ArrayList<Failure> events = new ArrayList<>();
    protected ArrayList<Failure> events1 = new ArrayList<>();
    protected ArrayList<Failure> events2 = new ArrayList<>();
    @Override
    public String getTitle(){ return "Аварии"; }
    public ModuleFailure(){}
    public static int RGB(int r, int g, int b){
        return r << 16 | g << 8 | b;
        }
    public static int getColor(Failure ff) {
        if (ff.isDone() & ff.isQuited()) {
            return RGB(240,240,240);
        } else if (ff.isWarning()) {
            return RGB(200,200, 0);
        } else if (!ff.isQuited()) {
            return !ff.isDone() ? RGB(240, 0, 0) : RGB(240, 100, 0);
        } else {
            return RGB(240, 240, 0);
            }
        }
    public static int getImgResource(Failure ff) {
        if (ff.isDone() & ff.isQuited()) {
            return R.drawable.ballgray;
        } else if (ff.isWarning()) {
            return R.drawable.ballblue;
        } else if (!ff.isQuited()) {
            return !ff.isDone() ? R.drawable.problem : R.drawable.ballyellow;
        } else {
            return R.drawable.balldarkyellow;
        }
    }
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client0,panel, service, service2,token, form, formContext);
        setBack(new I_ModuleBack() {
            @Override
            public void onHeader() {
                new OKDialog(client.main(), "Квитировать всё", new I_EventListener() {
                    @Override
                    public void onEvent(String zz) {
                        new  NetCall<JEmpty>().call(client.main(), service2.quitAllFailures(token), new NetBackDefault() {
                            @Override
                            public void onSuccess(Object val) {
                                repaintValues();
                                }
                            });
                        }
                    });
                }
            @Override
            public void onSelect(int index) {
                setQuited(events.get(index));
                }
            @Override
            public void onLongSelect(int index) {
                }
            });
        repaintValues();
        }
    //------------------------------------------------------------------------------------
    public void setQuited(final Failure failure){
        if (failure.isQuited()) return;
        new OKDialog(client.main() ,"Квитировать "+failure.getTitle(), new I_EventListener() {
            @Override
            public void onEvent(String ss) {
                new NetCall<JEmpty>().call(client.main(), service2.quitFailure(token, failure instanceof FailureSetting, failure.getEquipName(), failure.getLogUnit(), failure.getRegNum(), failure.getBitNum()), new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                         repaintValues();
                        }
                    });
                }
            });
        }
    //------------------------------------------------------------------------------------
    @Override
    public void repaintView() {
        super.repaintView();
        }
    @Override
    public void repaintValues() {
        new NetCall<ArrayList<DBRequest>>().call(client.main(), service.getEntityListLast(token, "FailureBit", 50, 0), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                ArrayList<DBRequest> res = (ArrayList<DBRequest>) val;
                System.out.println("Прочитано событий " + res.size());
                events1.clear();
                Gson gson = new Gson();
                for (DBRequest request : res) {
                    try {
                        events1.add((Failure) request.get(gson));
                    } catch (Exception e1) {
                    }
                }
                new NetCall<ArrayList<DBRequest>>().call(client.main(), service.getEntityListLast(token, "FailureBit", 50, 0), new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        System.out.println("Прочитано событий " + res.size());        // Слияние по времени
                        events2.clear();
                        for (DBRequest request : res) {
                            try {
                                events2.add((Failure) request.get(gson));
                            } catch (Exception e2) {
                            }
                        }
                        selected.clear();
                        events.clear();
                        Failure ff;
                        int idx1 = events1.size() - 1, idx2 = events2.size() - 1;
                        ArrayList<Integer> colors = new ArrayList<>();
                        while (!(idx1 < 0 && idx2 < 0)) {
                            if (idx2 < 0 || idx1 >= 0 && events1.get(idx1).getArrivalTime().timeInMS() > events2.get(idx2).getArrivalTime().timeInMS()){
                                ff = events1.get(idx1--);
                                selected.add(ff);
                                events.add(ff);
                                colors.add(getImgResource(ff));
                                }
                            else{
                                ff = events2.get(idx2--);
                                selected.add(ff);
                                events.add(ff);
                                colors.add(getImgResource(ff));
                                }
                            }
                        showTable(colors);
                    }
                });
            }
        });
    }

    public boolean typeFilter(int type) {
        return true;
        }
}
