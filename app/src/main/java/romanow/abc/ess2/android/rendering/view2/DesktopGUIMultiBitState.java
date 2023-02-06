package romanow.abc.ess2.android.rendering.view2;

import android.widget.RelativeLayout;

import java.util.ArrayList;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIMultiBitState;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIMultiBitState extends View2BaseDesktop {
    private int regNums[];
    private BorderedTextView textField;
    public DesktopGUIMultiBitState(){
        type = Values.GUIMultiBitState;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUIMultiBitState element = (Meta2GUIMultiBitState)   getElement();
        Meta2BitRegister register = (Meta2BitRegister)  getRegister();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element.getW2();
        if (dd==0) dd=100;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+element.getDx()+DefaultSpace),
                context.y(element.getY()+getDyOffset()),
                context.x(dd),
                context.y(hh));
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        panel.addView(textField);
        textField.setBackgroundColor(element.getColor() | 0xFF000000);
        setInfoClick(textField);
        }
    public ArrayList<String> createStateText(int regNum, int bitMask, long regVal){
        ArrayList<String> out = new ArrayList<>();
        if (regNum==0)
            return out;
        Meta2BitRegister register = (Meta2BitRegister) getRegister();
        if (register==null){
            context.getMain().main().popupInfo("Группа состояний: не найден регистр "+regNum);
            return out;
            }
        if (register.getType()!=Values.RegBitSet){
            context.getMain().main().popupInfo("Группа состояний: тип регистра "+regNum);
            return out;
            }
        for(int i=0,vv=1;i<16;i++,vv<<=1){
            if ((vv & bitMask)!=0 && (vv & regVal)!=0){
                Meta2Bit bit = register.getBits().getByCode(i);
                if (bit!=null)
                    out.add(bit.getTitle());
                else
                    context.getMain().main().popupInfo("Не найден "+i+"-ый разряд в "+getElement().getFullTitle());
                    }
                }
        for(Meta2Bit bit : register.getBits().getList()){
            int mask = 1<< bit.getBitNum();
            if ((mask & bitMask)!=0 && (mask & regVal)!=0){  // В маске установлен бит из описателя разряда
                out.add(bit.getTitle());
                }
            }
        return out;
        }
    public String createRegInfo(int regNum, int bitMask){
        return regNum==0 ? "" : " "+regNum+String.format("[%x] ",bitMask);
        }
    public void showInfoMessage() {
        FormContext2 context= getContext();
        Meta2GUIMultiBitState element = (Meta2GUIMultiBitState)   getElement();
        Meta2DataRegister register = (Meta2DataRegister)  getRegister();
        String ss = "Группа состояний "+register.getFullTitle()+String.format("[%x] ",element.getBitMask());
        context.getMain().main().popupInfo(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        Meta2GUIMultiBitState element = (Meta2GUIMultiBitState) getElement();
        ArrayList<String> out = createStateText(element.getRegNum(),element.getBitMask(),vv);
        String ss = "";
        for(String zz : out)
            ss+=zz+"\n";
        textField.setText(ss);
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2BitRegister register = (Meta2BitRegister)  getRegister();
        if (!(register instanceof Meta2BitRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}