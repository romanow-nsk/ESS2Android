package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUISetting;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUISetting extends View2BaseDesktop {
    private TextView textField;
    public DesktopGUISetting(){
        type = Values.GUISetting;
    }

    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUISetting element = (Meta2GUISetting) getElement();
        Meta2SettingRegister register = (Meta2SettingRegister) getRegister();
        int w2=element.getW2();
        if (w2==0) w2=100;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        //LinearLayout button = (LinearLayout) context.getMain().main().getLayoutInflater().inflate(R.layout.form_button, null);
        //textField = (Button) button.findViewById(R.id.form_button);
        int hh = element.getH();
        if (hh==0) hh=25;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+DefaultSpace),
                context.y(element.getY()),
                context.x(w2),
                context.y(hh));
        //textField.setEditable(false);
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        setTextSize(textField,textSize);
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        setInfoClick(textField);
        final boolean remoteDisable = !context.isSuperUser() &&  !context.isLocalUser() && !(register).isRemoteEnable();
        int color=remoteDisable || !context.isActionEnable() ? Values.AccessDisableColor : getBackColor();
        textField.setBackgroundColor(color | 0xFF000000);
        textField.setTextColor(textColor);
        setLongClickInfo(textField);
        textField.setOnClickListener(new View.OnClickListener() {
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
                /* ---------------------- TODO ---------------------------------------------
                ESS2SettingsCalculator calculator = new ESS2SettingsCalculator();
                try {
                    calculator.calculate(getArchitecture(), register, DesktopGUISetting.this.getDevice().getDriver());
                    new DigitPanel(calculator.getResult(), new I_RealValue() {
                        @Override
                        public void onEvent(double value) {
                            Meta2SettingRegister register = (Meta2SettingRegister) getRegister();
                            try {
                                if (register.getFormat()==Values.FloatValue)
                                    writeMainRegister(Float.floatToIntBits((float) value));
                                else
                                    writeMainRegister(register.intWithPower(value));
                            } catch (UniException ex) {
                                String ss = "Ошибка записи уставки: "+ex.toString();
                                new Message(300,300,ss,Values.PopupMessageDelay);
                                System.out.println(ss);
                            }

                            //onEvent.onEnter(GUISetting.this,0,"");
                        }
                    });
                } catch (UniException ex) {
                    System.out.println("Калькулятор уставок: "+ex.toString());
                    }
                 */
                }
            });
        }
    @Override
    public void showInfoMessage() {
        Meta2SettingRegister set = (Meta2SettingRegister)getRegister();
        String ss = "Уставка "+set.getRegNum()+" "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+="Удаленное управление - "+(set.isRemoteEnable() ? "да":"нет")+",";
        ss+=" Ед.изм. "+ set.getUnit()+"$";
        ss+="Формулы: "+set.getDefValueFormula()+" / "+set.getMinValueFormula()+" / "+set.getMaxValueFormula();
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(long vv) throws UniException {
        Meta2SettingRegister register = (Meta2SettingRegister) getRegister();
        if (((Meta2GUISetting) getElement()).isIntValue())
            textField.setText(register.regValueToIntString(getUnitIdx(),(int)vv));
        else
            textField.setText(register.regValueToString(getUnitIdx(),(int)vv));
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2SettingRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}
