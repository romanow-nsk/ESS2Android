package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUI2StateBox;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUI2StateBox extends View2BaseDesktop {
    protected View textField;
    private int bitNum=0;
    private Button cmdButton=null;     // Кнопка
    private int lastBitValue=-1;        // Последнее значение разряда
    private int lastValue=0;            //
    public DesktopGUI2StateBox(){
        type = Values.GUI2StateBox;
        }
    //----------------------------------------------------------------------
    protected View createComponent(){
        return new ImageView(context.getMain().main());
        }
    protected void viewComponent(){
        ((ImageView)textField).setImageResource(R.drawable.ballwhite); // NOI18N
        }
    protected void putValueOwn(int cc){
        ImageView bb = (ImageView) textField;
        bb.setImageResource(getColorIconName(cc));
        }
    protected int getSize(){
        return 24; }
    //-----------------------------------------------------------------------
    private int getColorIconName(int color){
        switch (color){
            case 0x00C0C0C0: return R.drawable.balllightgray;
            case 0x00FF0000: return R.drawable.ballred;
            case 0x0000FF00: return R.drawable.ballgreen;
            case 0x00FFFF00: return R.drawable.ballyellow;
            }
        int v=0;
        return R.drawable.ballwhite;
    }
    @Override
    public void addToPanel(RelativeLayout panel) {
        Meta2GUI2StateBox element = (Meta2GUI2StateBox) getElement();
        int hh = element.getH();
        if (hh==0) hh=25;
        if (element.getDx()!=0){
            setLabel(panel);
        }
        textField = createComponent();
        int dd = element.getW2();
        if (dd==0) dd=50;
        int sz = getSize();
        int offset = (25-sz)/2;
        FormContext2 context = getContext();
        int xx = element.getX()+element.getDx()+getDxOffset()+dd-5+offset;
        int yy = element.getY()+getDyOffset()+(hh-15)/2-5+offset;
        setBounds(textField,
                context.x(xx),
                context.y(yy),
                context.x(sz),
                context.y(sz));
        viewComponent();
        setInfoClick(textField);
        panel.addView(textField);
        bitNum = element.getBitNum();
        int bSize = element.getButtonSize();
        if (bSize==0)
            return;
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser() && !element.isRemoteEnable();
        //LinearLayout button = (LinearLayout) context.getMain().main().getLayoutInflater().inflate(R.layout.form_button, null);
        //Button cmdButton = (Button) button.findViewById(R.id.form_button);
        cmdButton = new Button(context.getMain().main());
        setBounds(cmdButton,
                context.x(xx+sz+5),
                context.y(yy),
                context.x(bSize),
                context.y(sz+5));
        //cmdButton.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cmdButton.setText("");
        cmdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteDisable){
                    context.getMain().main().popupAndLog("Запрет удаленного управления");
                    return;
                    }
                if (!context.isActionEnable()){
                    context.getMain().main().popupAndLog("Недостаточен уровень доступа");
                    return;
                    }
                if (lastBitValue==-1){
                    context.getMain().main().popupAndLog("Разряд еще не прочитан");
                    return;
                    }
                new OKDialog(context.getMain().main() ,element.getTitle()+" "+(lastBitValue!=0 ? "ОТКЛ" : "ВКЛ"), new I_EventListener() {
                    @Override
                    public void onEvent(String zz) {
                        if (zz==null) return;
                        int vv = lastValue ^ (1<<bitNum);       // Инвертировать разряд
                        writeMainRegister(vv);
                        }
                    });
                }
            });
        panel.addView(cmdButton);
        }
    public void showInfoMessage() {
        Meta2GUI2StateBox element = (Meta2GUI2StateBox) getElement();
        int bitNumElem = element.getBitNum();
        Meta2BitRegister set = (Meta2BitRegister) getRegister();
        Meta2Bit bit = set.getBits().getByCode(bitNumElem);
        String ss = "Разряд регистра "+(set.getRegNum()+getRegOffset()+" ["+set.getRegNum()+"]("+bitNum+") "+set.getShortName()+"$"+set.getTitle()+"$");
        ss+=bit==null ? " не найден " : bit.getTitle();
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(int vv) throws UniException {
        Meta2GUI2StateBox element = (Meta2GUI2StateBox) getElement();
        lastValue = vv;
        lastBitValue = (vv>>bitNum) & 01;
        if (cmdButton!=null)
            cmdButton.setText(lastBitValue!=0 ? "ОТКЛ" : "ВКЛ");
        int cc = (lastBitValue!=0 ? element.getColorYes() : element.getColorNo()) & 0x00FFFFFF;
        putValueOwn(cc);
        }

    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = (Meta2Register) getRegister();
        if (!(register instanceof Meta2BitRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}
