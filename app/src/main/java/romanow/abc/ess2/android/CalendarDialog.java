package romanow.abc.ess2.android;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.ess2.android.service.BaseActivity;

public class CalendarDialog {
    //-----------------------------------------------------------------------
    private ArrayList<Button> dayList = new ArrayList<>();
    private OwnDateTime date=new OwnDateTime();
    private OwnDateTime cdate=new OwnDateTime();
    private int mode=0;
    private LinearLayout calendarPanel;
    String week[]={"Пн","Вт","Ср","Чт","Пт","Сб","Вс"};
    String mnt[]={"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
    public ArrayList<Button> dayList(){ return dayList; }
    I_CalendarEvent back;
    AlertDialog myDlg=null;
    BaseActivity parent;
    RelativeLayout lrr;

    public CalendarDialog(BaseActivity activity, String title,I_CalendarEvent back0){
        back = back0;
        parent=activity;
        myDlg=new AlertDialog.Builder(activity).create();
        myDlg.setCancelable(true);
        lrr=(RelativeLayout)activity.getLayoutInflater().inflate(R.layout.calendar, null);
        try {
            //parent.headerTitle.setText(title);
            date.day(1);
            createList();
        } catch(Exception ee){  }
        catch(Error ee){  }
        myDlg.setView(lrr);
        myDlg.show();
    }
    public void onCreateListStart(int month,int year){ }
    public void onCreateListFinish(int month,int year){ }
    public void onDayClick(int day, int month,int year){}
    private void createList(){
        dayList.clear();
        onCreateListStart(date.month(),date.year());
        LinearLayout xx,dd;
        int i,j,k;
        Button b1=(Button)lrr.findViewById(R.id.dialog_calendar_prev);
        b1.setText(date.month()==1 ? mnt[11] : mnt[date.month()-2]);
        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                date.decMonth();
                createList();
            }});
        b1=(Button)lrr.findViewById(R.id.dialog_calendar_next);
        b1.setText(date.month()==12 ? mnt[0] : mnt[date.month()]);
        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                date.incMonth();
                createList();
            }});
        TextView zz=(TextView)lrr.findViewById(R.id.dialog_calendar_date);
        zz.setText(""+mnt[date.month()-1]+"\n"+(date.year()));
        LinearLayout lr=(LinearLayout)lrr.findViewById(R.id.dialog_calendar_panel);
        lr.removeAllViews();
        xx=(LinearLayout)parent.getLayoutInflater().inflate(R.layout.calendar_row, null);
        for (j=0;j<7;j++){
            dd=(LinearLayout)parent.getLayoutInflater().inflate(R.layout.calendar_item_small, null);
            //dd.setPadding(1, 1, 1, 1);
            TextView tt=(TextView)dd.findViewById(R.id.dialog_calendar_item);
            tt.setText(""+week[j]);
            xx.addView(dd);
        }
        lr.addView(xx);
        int k0=date.dayOfWeek()-1;
        int k1=31;
        k=0;
        final int month=date.month();
        if (month==2){
            k1=28; if (date.year()%4==0) k1=29;
        }
        if (month==4 || month==6 || month==9 || month==11) k1=30;
        for (i=0;i<6;i++){
            xx=(LinearLayout)parent.getLayoutInflater().inflate(R.layout.calendar_row, null);
            for (j=0;j<7;j++,k++){
                dd=(LinearLayout)parent.getLayoutInflater().inflate(R.layout.calendar_item_small, null);
                //dd.setPadding(1, 1, 1, 1);
                Button tt=(Button)dd.findViewById(R.id.dialog_calendar_item);
                if (k>=k0 && k<k0+k1){
                    final int nn=k-k0+1;
                    tt.setText(""+nn);
                    dayList.add(tt);
                    if (month==cdate.month() && nn==cdate.day() && date.year()==cdate.year()){
                        tt.setTextSize(25);
                        tt.setTextColor(Color.YELLOW);
                        tt.setElegantTextHeight(false);
                    }
                    tt.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View arg0) {
                            back.onDate(new OwnDateTime(nn,date.month(),date.year()).timeInMS());
                            myDlg.cancel();
                        }});
                }
                else tt.setBackgroundResource(R.drawable.background_dialog);
                xx.addView(dd);
            }
            lr.addView(xx);
            onCreateListFinish(date.month(),date.year());
            //------------------------------------ перечитывание смен и выходов ----------------------------------------
        }
    }

}
