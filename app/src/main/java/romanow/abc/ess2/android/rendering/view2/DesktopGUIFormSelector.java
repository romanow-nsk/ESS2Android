package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUIFormSelector;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIFormSelector extends View2BaseDesktop {
    private BorderedButton list;
    private ImageView up;
    private ImageView down;
    private ImageView prev;
    private ImageView next;
    private int maxValue=0;
    private int idx=0;
    private int level=0;
    public DesktopGUIFormSelector(){
        type = Values.GUIFormSelector;
        }
    private boolean busy=false;
    @Override
    public void addToPanel(RelativeLayout panel) {
        busy=true;
        setLabel(panel);
        prev = new ImageView(context.getMain().main());
        FormContext2 context= getContext();
        final Meta2GUIFormSelector element = (Meta2GUIFormSelector) getElement();
        level = element.getFormLevel();
        setBounds(prev,
                context.x(element.getX()+element.getDx()+10),
                context.y(element.getY()),
                context.x(40),
                context.y(40));
        prev.setImageResource(R.drawable.left); // NOI18N
        //textField.setBorderPainted(false);
        //textField.setContentAreaFilled(false);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level==0) return;
                int pos = context.getIndex(level);
                if (pos==0) return;
                context.setIndex(level,pos-1);
                context.reOpenForm();
                }
            });
        panel.addView(prev);
        int textColor = context.getView().getTextColor() | 0xFF000000;
        list = new BorderedButton(context.getMain().main(),textColor);
        maxValue = context.getSize(context.getForm().getFormLevel());
        idx= context.getIndex(element.getFormLevel());            //?????????????????????????
        list.setText(""+(idx+1));
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        list.setTextSize(textSize);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                new NumSelectorPanel(1, maxValue, new I_RealValue() {
                    @Override
                    public void onEvent(double value) {
                        int vv = (int)value-1;
                        context.setIndex(level,vv);
                        context.reOpenForm();
                        //context.openForm(element.getFormName(),vv);
                    }
                });
                */
                }
            });
        setBounds(list,
                context.x(element.getX()+element.getDx()+50),
                context.y(element.getY()),
                context.x(45),
                context.y(40));
        //list.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        panel.addView(list);
        next = new ImageView(context.getMain().main());
        setBounds(next,
                context.x(element.getX()+element.getDx()+95),
                context.y(element.getY()),
                context.x(40),
                context.y(40));
        next.setImageResource(R.drawable.right); // NOI18N
        //textField.setBorderPainted(false);
        //textField.setContentAreaFilled(false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level==0) return;
                int pos = context.getIndex(level);
                if ((pos+1)>=context.getSize(level))
                    return;
                context.setIndex(level,pos+1);
                context.reOpenForm();
                }
            });
        panel.addView(next);
        down = new ImageView(context.getMain().main());
        setBounds(down,
                context.x(element.getX()+element.getDx()+135),
                context.y(element.getY()),
                context.x(40),
                context.y(40));
        down.setImageResource(R.drawable.down); // NOI18N
        //textField.setBorderPainted(false);
        //textField.setContentAreaFilled(false);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (busy) return;
                context.openForm(element.getFormName());
                }
            });
        panel.addView(down);
        up = new ImageView(context.getMain().main());
        setBounds(up,
                context.x(element.getX()+element.getDx()+175),
                context.y(element.getY()),
                context.x(40),
                context.y(40));
        up.setImageResource(R.drawable.up); // NOI18N
        //textField.setBorderPainted(false);
        //textField.setContentAreaFilled(false);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (busy) return;
                context.openForm(context.getForm().getParentName(),false);
                }
            });
        panel.addView(up);
        busy=false;
        }
    @Override
    public void putValue(int vv) throws UniException {}
    @Override
    public void showInfoMessage() { }
    public boolean needRegister() { return false; }
}
