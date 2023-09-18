package romanow.abc.ess2.android;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

import romanow.abc.ess2.android.menu.MenuItemAction;


public class ListBoxDialog {
    private Activity activity;
    private String title;
    private ArrayList<MenuItemAction> menuList = new ArrayList<>();
    private boolean autoClose=true;
    private I_ListBoxListener ls=null;
    private int nLines=1;
    private float textSize=0;
    private int textAlignment=View.TEXT_ALIGNMENT_CENTER;
    AlertDialog myDlg=null;
    boolean imgMode = false;
    public ListBoxDialog(Activity activity0, ArrayList<MenuItemAction> menuList0, String title0, I_ListBoxListener ls0){
        activity = activity0;
        title = title0;
        menuList = menuList0;
        ls = ls0;
        imgMode = true;
        }
    public ListBoxDialog setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
        }
    public ListBoxDialog setListener(I_ListBoxListener ls) {
        this.ls = ls;
        return this;
        }
    public ListBoxDialog setnLines(int nLines) {
        this.nLines = nLines;
        return this;
        }
    public ListBoxDialog setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
        }
    public ListBoxDialog setTextAlignment(int align) {
        textAlignment = align;
        return this;
        }
    public void create(){
        try {
             myDlg=new AlertDialog.Builder(activity).create();
             myDlg.setCancelable(true);
             myDlg.setTitle(null);
             LinearLayout lrr=(LinearLayout)activity.getLayoutInflater().inflate(R.layout.listbox, null);
             LinearLayout trmain=(LinearLayout)lrr.findViewById(R.id.dialog_listbox_panel);
             trmain.setPadding(5, 5, 5, 5); 
             if (title!=null){
                 TextView hd=(TextView)lrr.findViewById(R.id.dialog_listbox_header);
                 hd.setText(title);
                 hd.setOnClickListener(new OnClickListener(){
                        public void onClick(final View arg0) {
                        if (autoClose) myDlg.cancel();
                        }});
                }
             myDlg.setOnCancelListener(new OnCancelListener(){
                public void onCancel(DialogInterface arg0) {
                }
             });
            for (int i=0;i<menuList.size();i++){
                final int ii = i;
                LinearLayout xx=(LinearLayout)activity.getLayoutInflater().inflate(R.layout.listbox_item, null);
                xx.setPadding(5, 5, 5, 5);
                Button tt=(Button) xx.findViewById(R.id.dialog_listbox_name);
                ImageButton img=(ImageButton) xx.findViewById(R.id.dialog_listbox_img);
                tt.setText(menuList.get(i).title);
                Bitmap bitmap = menuList.get(i).getBitmap();
                if (bitmap!=null)
                    img.setImageBitmap(bitmap);
                else {
                    xx.removeView(img);
                    }
                StringTokenizer ss = new StringTokenizer(menuList.get(i).title,"\n");
                int cnt = ss.countTokens();
                tt.setLines(cnt > nLines ? cnt : nLines);
                if (textSize!=0)
                    tt.setTextSize(textSize);
                tt.setTextAlignment(textAlignment);
                tt.setOnClickListener(new OnClickListener(){
                    public void onClick(final View arg0) {
                    	if (ls!=null) ls.onSelect(ii);
                    	myDlg.cancel();                	
                    }});
                tt.setOnLongClickListener(new OnLongClickListener(){
    				public boolean onLongClick(View arg0) {
                        if (ls!=null) ls.onLongSelect(ii);
                        myDlg.cancel();
                        return false;
        				}
                                  });
                trmain.addView(xx);
                }
             myDlg.setView(lrr);
             myDlg.show();
             } catch(Exception ee){  }                
               catch(Error ee){  }
            }
        public void cancel(){
            myDlg.cancel();
            }
}
 