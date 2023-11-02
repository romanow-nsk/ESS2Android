package romanow.abc.ess2.android;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import romanow.abc.ess2.android.service.AppData;

public class TimeSelector {
    private AlertDialog myDlg;
    private MainActivity main;
    private I_CalendarEvent back;
    private long timeStamp=0;
    final public static long TimeMinuteMask = 60000L;
    final public static long TimeHourMask = 60L*TimeMinuteMask;
    final public static long TimeDayMask = 24L*TimeHourMask;
    public TimeSelector(MainActivity main0,I_CalendarEvent back0) {
        main = main0;
        back = back0;
        myDlg=new AlertDialog.Builder(main).create();
        myDlg.setCancelable(true);
        myDlg.setTitle(null);
        LinearLayout layout = (LinearLayout) main.getLayoutInflater().inflate(R.layout.timeselector,null);
        TextView head = (TextView) layout.findViewById(R.id.timeHeader);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.onDate(timeStamp);
                myDlg.cancel();
                }
            });
        final Button hour = (Button)layout.findViewById(R.id.timeHour);
        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> hours = new ArrayList<>();
                for (int i=0;i<24;i++)
                    hours.add(String.format("%02d",i));
                new ListBoxDialog(main, MainActivity.createMenuList(hours), "Часы", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {
                        timeStamp = timeStamp % 60 + index * 60;
                        hour.setText(String.format("%02d",index));
                        }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                }).setResources(R.layout.listbox_short,R.layout.listbox_item_short).create();
                }
            });
        final Button minute = (Button)layout.findViewById(R.id.timeMinute);
        minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> hours = new ArrayList<>();
                for (int i=0;i<60;i++)
                    hours.add(String.format("%02d",i));
                new ListBoxDialog(main, MainActivity.createMenuList(hours), "Минуты", new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {
                        timeStamp = (timeStamp - timeStamp % 60) + index;
                        minute.setText(String.format("%02d",index));
                        }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                }).setResources(R.layout.listbox_short,R.layout.listbox_item_short).create();
            }
        });
        myDlg.setView(layout);
        myDlg.show();
        }
}
