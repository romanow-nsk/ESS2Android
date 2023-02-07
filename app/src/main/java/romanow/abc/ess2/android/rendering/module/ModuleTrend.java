package romanow.abc.ess2.android.rendering.module;

import android.widget.RelativeLayout;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;

public class ModuleTrend extends Module {
    private final static String buttonAdd = "/drawable/add.png";
    /*
    private TrendPanel trend;
    private Runnable after = new Runnable() {
        @Override
        public void run() {
            trend.setBounds(
                    context.x(0),
                    context.y(60),
                    context.x(Client.PanelW-10),
                    context.y(Client.PanelH-50));
            panel.add(trend);
            panel.revalidate();
            trend.setBack(new I_Success() {
                @Override
                public void onSuccess() {
                    new ESSStreamDataView((ESSClient) client,new I_Value<StreamRegisterData>() {
                        @Override
                        public void onEnter(StreamRegisterData value) {
                            if (value.getValueList().size()==0){
                                System.out.print("Нет данных тренда");
                                return;
                                }
                            trend.addTrendView(value);
                        }
                    });
                }
            });
            */
            /*
            JButton toMain = trend.getRefreshButton();
            toMain.setIcon(new javax.swing.ImageIcon(getClass().getResource(buttonAdd))); // NOI18N
            toMain.setBorderPainted(false);
            toMain.setContentAreaFilled(false);
            toMain.removeActionListener(toMain.getActionListeners()[0]);
            toMain.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ESSStreamDataView(client, true,new I_Value<DataSet>() {
                        @Override
                        public void onEnter(DataSet value) {
                            if (value.getData().size()==0){
                                System.out.print("Нет данных тренда");
                                return;
                            }
                            trend.addTrendView(value);
                        }
                    });
                }
            });
        }
    };
    */
    @Override
    public void init(ESS2ArchitectureData client0, RelativeLayout panel, RestAPIBase service, RestAPIESS2 service2, String token, Meta2GUIForm form, FormContext2 formContext) {
        super.init(client, panel, service, service2,token, form, formContext);
        //trend = new TrendPanel();
        //trend.init(context,after);
    }
}
