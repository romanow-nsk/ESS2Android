package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2EntityList;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2String;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIBitsList;
import romanow.abc.core.entity.metadata.view.Meta2GUIString;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.ListBoxDialog;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIBitsList extends View2BaseDesktop {
    private TextView textField;
    private Meta2GUIBitsList element;
    private Meta2BitRegister reg;
    private Meta2EntityList<Meta2Bit> bits;
    private boolean valid=false;
    private int lastState;
    private boolean busy=false;
    private ArrayList<String> list = new ArrayList<>();
    public DesktopGUIBitsList(){
        type = Values.GUIBitsList;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        element = (Meta2GUIBitsList) getElement();
        int w2=element.getW2();
        if (w2==0) w2=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int hh = element.getH();
        if (hh==0) hh=25;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+DefaultSpace),
                context.y(element.getY()),
                context.dx(w2),
                context.dy(hh));
        setTextFieldParams(textField);
        panel.addView(textField);
        setInfoClick(textField);
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser();
        setLongClickInfo(textField);
        element = (Meta2GUIBitsList) getElement();
        reg = (Meta2BitRegister)  getRegister();
        bits = reg.getBits();
        bits.createMap();
        textField.setEnabled(true);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size()==0)
                    return;
                MainActivity main = context.getMain().main();
                new ListBoxDialog(main,MainActivity.createMenuList(list),element.getTitle(),null).setnLines(2).create();
            }
        });

        }
    public void showInfoMessage() {
        Meta2BitRegister set = (Meta2BitRegister) getRegister();
        String ss = "Список разрядов регистра "+toHex(set.getRegNum()+getRegOffset())+" ["+toHex(set.getRegNum())+"]"+set.getShortName()+"$"+set.getTitle();
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        list.clear();
        for(int nBit=0; nBit<16;nBit++){
            if ((vv & 1)!=0 ){
                Meta2Bit bit = bits.getByCode(nBit);
                if (bit!=null){
                    list.add(bit.getTitle());
                    }
                }
            vv >>=1;
            }
        textField.setText("Выбрано: "+list.size());
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register =  getRegister();
        if (!(register instanceof Meta2BitRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}
