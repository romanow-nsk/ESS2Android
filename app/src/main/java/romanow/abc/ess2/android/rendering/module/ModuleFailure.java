package romanow.abc.ess2.android.rendering.module;

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
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
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

                }
            @Override
            public void onLongSelect(int index) {
                }
            });
        repaintValues();
        /*
        JButton bb = new MultiTextButton(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        bb.setText("Квитировать всё");
        bb.setBounds(
                context.x(20),
                context.y(600),
                context.x(ESSServiceGUIPanel.buttonXSize),
                context.y(ESSServiceGUIPanel.buttonYSize));
        bb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OK(200, 200, "Квитировать всё", new I_Button() {
                    @Override
                    public void onPush() {
                        try {
                            new APICall2<JEmpty>(){
                                @Override
                                public Call<JEmpty> apiFun() {
                                    return service2.quitAllFailures(token);
                                }
                            }.call(client);
                        repaintValues();
                        } catch (UniException ex) {
                            System.out.println("Ошибка API: "+ex.toString());
                            }
                        }
                    });
                }
            });
        panel.add(bb);
        repaintView();
         */
        }
    //------------------------------------------------------------------------------------
    /*
    public void setQuited(final Failure failure){
        if (failure.isQuited()) return;
        new OK(200, 200, "Квитировать "+failure.getTitle(), new I_Button() {
            @Override
            public void onPush() {
                try {
                    new APICall2<JEmpty>(){
                        @Override
                        public Call<JEmpty> apiFun() {
                            return service2.quitFailure(token,failure instanceof FailureSetting,
                                    failure.getEquipName(), failure.getLogUnit(), failure.getRegNum(),failure.getBitNum());
                        }
                    }.call(client);
                    repaintValues();
                } catch (UniException ex) {
                    System.out.println("Ошибка API: "+ex.toString());
                }
            }
        });
        }
    //------------------------------------------------------------------------------------
    @Override
    public void repaintView() {
        super.repaintView();
        }

    private String[] columnsHeader = new String[] {"дата","время", "тип","событие"};
    private int sizes[] = {100,80,120,700};
    private void showTable(){
            Vector<Vector<String>> data = new Vector<Vector<String>>();
            Vector<String> header = new Vector<String>();
            for(String ss : columnsHeader)
                header.add(ss);
            for(ArchESSEvent essEvent : events){
                Vector<String> row = new Vector<String>();
                OwnDateTime dd = essEvent.getArrivalTime();
                row.add(dd.dateToString());
                row.add(dd.timeFullToString());
                row.add(Values.title("EventType",essEvent.getType()));
                row.add(essEvent.getTitle());
                data.add(row);
                }
            if (table!=null) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(events.size());
                model.setDataVector(data,header);
                }
            else{
                table = new JTable(data,header);
                JScrollPane scroll = new JScrollPane(table);
                scroll.setBounds(
                        context.x(10),
                        context.y(100),
                        context.x(Client.PanelW-20),
                        context.y(Client.PanelH-150));
                panel.add(scroll);
                table.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
                table.setRowHeight(context.y(20));
                table.setSelectionForeground(Color.blue);
                table.setSelectionBackground(Color.yellow);
                table.setVisible(true);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setAutoscrolls(true);
                table.setShowGrid(true);
                listener = new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting())
                            return;
                        int[] selectedRows = table.getSelectedRows();
                        for(int i = 0; i < selectedRows.length; i++) {
                            System.out.println(events.get(selectedRows[i]));
                            setQuited(events.get(selectedRows[i]));
                            }
                        }
                    };
                table.getSelectionModel().addListSelectionListener(listener);
                table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        c.setBackground(events.get(row).getColor());
                        return c;
                        }
                    });
                }
            for(int i=0;i<sizes.length;i++)
                table.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);
            //panel.repaint();
        }
    */
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
                        int idx1 = events1.size() - 1, idx2 = events2.size() - 1;
                        while (!(idx1 < 0 && idx2 < 0)) {
                            if (idx2 < 0 || idx1 >= 0 && events1.get(idx1).getArrivalTime().timeInMS() > events2.get(idx2).getArrivalTime().timeInMS())
                                selected.add(events1.get(idx1--));
                            else
                                selected.add(events2.get(idx2--));
                        }
                        showTable();

                    }
                });
            }
        });
    }

    public boolean typeFilter(int type) {
        return true;
        }
}
