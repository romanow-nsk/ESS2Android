package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUITimeSetting;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.I_Success;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUITimeSetting extends View2BaseDesktop {
    private BorderedTextView textField;
    public DesktopGUITimeSetting(){
        type = Values.GUITimeSetting;
        }
    private I_Success back=null;

    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        Meta2GUITimeSetting element = (Meta2GUITimeSetting) getElement();
        Meta2SettingRegister register = (Meta2SettingRegister) getRegister();
        int w2=element.getW2();
        if (w2==0) w2=100;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        setBounds(textField,
                context.x(element.getX()+element.getDx()+5),
                context.y(element.getY()),
                context.x(w2),
                context.y(hh));
        textField.setEnabled(false);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        textField.setBackgroundColor(element.getColor() | 0xFF000000);
        textField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                new ESS2TimeSetting(getArchitecture(), register, getDevice().getDriver(), new I_Success() {
                    @Override
                    public void onSuccess() {
                        if (back!=null)
                            back.onSuccess();
                        }
                    });
                 */
                }
            });
        }

    @Override
    public void putValue(long zz) throws UniException {
        textField.setText(String.format("%2d:%2d:%2d",zz/3600,zz%3600/60,zz%60));
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register =  getRegister();
        if (!(register instanceof Meta2SettingRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
    @Override
    public void showInfoMessage() { }
}