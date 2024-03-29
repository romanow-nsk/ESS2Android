package romanow.abc.ess2.android.rendering.module;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.API.RestAPIProxy;
import romanow.abc.core.ErrorList;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.Values;
import romanow.abc.core.drivers.I_ModbusGroupDriver;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.metadata.StreamDataValue;
import romanow.abc.core.entity.metadata.StreamRegisterData;
import romanow.abc.core.entity.metadata.StreamRegisterGroup;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.CalendarDialog;
import romanow.abc.ess2.android.GraphData;
import romanow.abc.ess2.android.I_CalendarEvent;
import romanow.abc.ess2.android.I_ListBoxListener;
import romanow.abc.ess2.android.ListBoxDialog;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.TimeSelector;
import romanow.abc.ess2.android.menu.MenuItemAction;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.AppData;
import romanow.abc.ess2.android.service.BaseActivity;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;
import romanow.abc.ess2.android.service.ESS2FullScreenGraph;
import romanow.abc.ess2.android.service.NetBackDefault;
import romanow.abc.ess2.android.service.NetCall;

public class ModuleTrend extends Module {
    private ArrayList<ConstValue> modeList;
    private ArrayList<ConstValue> modeList2 = new ArrayList<>();
    private ArrayList<String> streamTypes = new ArrayList<>();
    private ConstValue selectedMode=null;
    private Button streamTypeButton;
    private Button streamRegisterButton;
    private Button firstDay;
    private Button lastDay;
    private Button selectedButton;
    private ImageView calendar1;
    private ImageView calendar2;
    private ImageView clock1;
    private ImageView clock2;
    private ImageView toGraphButton;
    private long firstDateMS=0;
    private long lastDateMS=0;
    private long firstTimeMS=0;
    private long lastTimeMS=0;
    private AlertDialog myDlg;
    private ArrayList<String> registerNames = new ArrayList<>();
    private ArrayList<StreamRegisterData> registerList = new ArrayList<>();
    private ArrayList<StreamRegisterData> selectedList = new ArrayList<>();
    //private ArrayList<ArrayList<StreamDataValue>> data = new ArrayList<>();
    private MainActivity main;
    private LineGraphView multiGraph;
    private ArrayList<GraphData> graphData;
    private OwnDateTime getStartTime(){
        long minStamp = 0;
        for(GraphData seq : AppData.ctx().getGraphData()) {
            if (minStamp==0 || seq.data.get(0).timeStamp < minStamp)
                minStamp = seq.data.get(0).timeStamp;
                }
            return new OwnDateTime(minStamp);
            }
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client0, panel, service, service2,token, form, formContext);
        main = client.main();
        graphData = AppData.ctx().getGraphData();
        graphData.clear();
        modeList = Values.constMap().getGroupList("DataStream");
        streamTypes.clear();
        modeList2.clear();
        for(ConstValue cc : modeList){
            if (cc.value()==Values.DataStreamDayly || cc.value()==Values.DataStreamFrequent || cc.value()==Values.DataStreamRare){
                streamTypes.add(cc.title());
                modeList2.add(cc);
                }
            }
        selectedMode=modeList.get(0);
        myDlg=new AlertDialog.Builder(main).create();
        myDlg.setCancelable(true);
        myDlg.setTitle(null);
        RelativeLayout lrr=(RelativeLayout) main.getLayoutInflater().inflate(R.layout.trends, null);
        streamTypeButton = (Button) lrr.findViewById(R.id.streamType);
        streamTypeButton.setPadding(5, 5, 5, 5);
        streamTypeButton.setText("Тип потоковых данных");
        streamTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListBoxDialog(main, MainActivity.createMenuList(streamTypes), "Тип потока", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int idx) {
                        selectedMode = modeList2.get(idx);
                        streamTypeButton.setText(streamTypes.get(idx));
                        streamRegisterButton.setText("Регистр потоковых данных");
                        if (selectedMode.value()==Values.DataStreamNone){
                            return;
                            }
                        ArrayList<StreamRegisterGroup> groups = context.getMain().deployed.getStreamRegisterList(selectedMode.value());
                        registerNames.clear();
                        registerList.clear();
                        for(StreamRegisterGroup group : groups)
                            for(StreamRegisterData registerData : group.getList()){
                                registerData.setUnitIdx(group.getLogUnit());
                                registerList.add(registerData);
                                registerNames.add(registerData.getTitle());
                            }
                        }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                    }).create();
                }
            });
        //----------------------------------------------------------------------------------------------------------------
        streamRegisterButton = (Button) lrr.findViewById(R.id.streamRegister);
        streamRegisterButton.setPadding(5, 5, 5, 5);
        streamRegisterButton.setText("Регистр потоковых данных");
        streamRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMode.value()==Values.DataStreamNone)
                    return;
                new ListBoxDialog(main, MainActivity.createMenuList(registerNames), "Регистр", new I_ListBoxListener() {
                    @Override
                    public void onSelect(final int idx) {
                        //streamRegisterButton.setText(registerNames.get(idx));
                        StreamRegisterData selected = registerList.get(idx);
                        selectedList.add(selected);
                        //-------------------- TODO ----- Чтение данных ????
                        new NetCall<Pair<ErrorList,ArrayList<StreamDataValue>>>().call(main,
                                service2.getStreamData2(token, selectedMode.value(), idx,
                                        firstDateMS+firstTimeMS*TimeSelector.TimeMinuteMask,
                                        lastDateMS+lastTimeMS*TimeSelector.TimeMinuteMask), new NetBackDefault() {
                                    @Override
                                    public void onSuccess(Object val) {
                                        Pair<ErrorList,ArrayList<StreamDataValue>> ans = (Pair<ErrorList,ArrayList<StreamDataValue>>)val;
                                        ErrorList errors = ans.o1;
                                        if (!errors.valid())
                                            main.popupAndLog(errors.toString());
                                        else{
                                            graphData.add(new GraphData(registerNames.get(idx),ans.o2));
                                            selectedButton.setText("Выбрано "+selectedList.size());
                                            toGraphButton.setVisibility(View.VISIBLE);
                                            }
                                    }
                                });
                            }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                    }).setnLines(2).create();
                }
            });
        //-------------------------------------------------------------------------------------------
        firstDay = (Button) lrr.findViewById(R.id.startDay);
        firstDay.setPadding(5, 5, 5, 5);
        firstDay.setText("Начало");
        calendar1 = (ImageView) lrr.findViewById(R.id.startData);
        calendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalendarDialog(main, "Дата начала", new I_CalendarEvent() {
                    @Override
                    public void onDate(long timeInMS) {
                        firstDateMS = timeInMS;
                        firstDay.setText(new OwnDateTime(firstDateMS+firstTimeMS*TimeSelector.TimeMinuteMask).dateTimeToString());
                        }
                    });
                }
            });
        clock1 = (ImageView) lrr.findViewById(R.id.startTime);
        clock1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new TimeSelector(main, new I_CalendarEvent() {
                     @Override
                     public void onDate(long timeInMS) {
                        firstTimeMS = timeInMS;
                         if (firstDateMS==0)
                             firstDay.setText(String.format("%02d:%02d", firstTimeMS / 60, firstTimeMS % 60));
                         else
                             firstDay.setText(new OwnDateTime(firstDateMS+firstTimeMS*TimeSelector.TimeMinuteMask).dateTimeToString());
                     }
                 });
                }
            });
        //-------------------------------------------------------------------------------------------
        lastDay = (Button) lrr.findViewById(R.id.endDay);
        lastDay.setPadding(5, 5, 5, 5);
        lastDay.setText("Окончание");
        calendar2 = (ImageView) lrr.findViewById(R.id.endData);
        calendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalendarDialog(main, "Дата начала", new I_CalendarEvent() {
                    @Override
                    public void onDate(long timeInMS) {
                        lastDateMS = timeInMS;
                        if (lastDateMS==0)
                            lastDay.setText(String.format("%02d:%02d", lastTimeMS / 60, lastTimeMS % 60));
                        else
                            lastDay.setText(new OwnDateTime(lastDateMS+lastTimeMS*TimeSelector.TimeMinuteMask).dateTimeToString());
                        }
                    });
                }
            });
        clock2 = (ImageView) lrr.findViewById(R.id.endTime);
        clock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimeSelector(main, new I_CalendarEvent() {
                    @Override
                    public void onDate(long timeInMS) {
                        lastTimeMS = timeInMS;
                        if (lastDateMS==0)
                            lastDay.setText(String.format("%02d:%02d", lastTimeMS / 60, lastTimeMS % 60));
                        else
                            lastDay.setText(new OwnDateTime(lastDateMS+lastTimeMS*TimeSelector.TimeMinuteMask).dateTimeToString());
                    }
                });
            }
        });
        //------------------------------------------------------------------------------------------
        selectedButton = (Button) lrr.findViewById(R.id.registerList);
        selectedButton.setText("Выбрано "+selectedList.size()+" регистров");
        selectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.size()==0)
                    return;
                ArrayList<String> ss = new ArrayList<>();
                for (StreamRegisterData cc : selectedList)
                    ss.add(cc.getTitle());
                new ListBoxDialog(main, MainActivity.createMenuList(ss), "Регистры", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {}
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                }).setnLines(2).create();
                }
            });
        //------------------------------------------------------------------------------------------
        toGraphButton = (ImageView) lrr.findViewById(R.id.show_trend);
        toGraphButton.setVisibility(View.INVISIBLE);
        toGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDlg.cancel();
                main.clearLog();
                Intent intent = new Intent();
                intent.setClass(main.getApplicationContext(), ESS2FullScreenGraph.class);
                main.startActivityForResult(intent,MainActivity.TO_PREV_FORM);
               /*
               LinearLayout graph = createMultiGraph(R.layout.graphview,0);
                main.getLogLayout().addView(graph);
                for(ArrayList<StreamDataValue> series : data){
                    paintOne(series, Color.BLUE);
                    }
                */
                }
            });
        //------------------------------------------------------------------------------------------
        myDlg.setView(lrr);
        myDlg.show();
        }
    //----------------------------------------------------------------------------------------------
}
