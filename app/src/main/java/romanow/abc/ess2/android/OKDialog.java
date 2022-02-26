package romanow.abc.ess2.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OKDialog {
    private AlertDialog myDlg=null;
    private boolean wasChanged=false;
    private Activity base;
    I_EventListener lsn;

    private LinearLayout createItem(final String name, I_EventListener lsn0){
        lsn = lsn0;
        LinearLayout xx=(LinearLayout)base.getLayoutInflater().inflate(R.layout.ok_item, null);
        xx.setPadding(5, 5, 5, 5);
        TextView img=(TextView)xx.findViewById(R.id.ok_button);
        img.setText(name);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsn.onEvent(name);
                myDlg.cancel();
                }
            });
        img.setClickable(true);
        return xx;
        }

    public OKDialog(Activity base0, String parName, final I_EventListener listener){
        base = base0;
        try {
            myDlg=new AlertDialog.Builder(base).create();
            myDlg.setCancelable(true);
            myDlg.setTitle(null);
            myDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    lsn.onEvent(null);
                    myDlg.cancel();
                    }
                });
            LinearLayout layout = createItem(parName,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    lsn.onEvent(ss);
                    myDlg.cancel();
                    }});
            myDlg.setView(layout);
            myDlg.show();
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
}

