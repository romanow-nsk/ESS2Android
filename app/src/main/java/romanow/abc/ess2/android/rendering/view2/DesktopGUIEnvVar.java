package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIEnvVar;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2EnvValue;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIEnvVar extends View2BaseDesktop {
    private TextView textField;
    private Meta2GUIEnvVar element;
    private ArrayList<Double> envVarValue=new ArrayList<>();
    private boolean valid=false;
    public DesktopGUIEnvVar(){
        type = Values.GUIEnvVar;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        element = (Meta2GUIEnvVar)   getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element.getW2();
        if (dd==0) dd=100;
        int hh = element.getH();
        if (hh==0) hh=25;
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+element.getDx()+5),
                context.y(element.getY()+getDyOffset()),
                context.dx(dd),
                context.dy(hh));
        setTextFieldParams(textField);
        //int textSize = element.getFontSize();
        //if (textSize==0) textSize = DefaultTextSize;
        //setTextSize(textField,textSize);
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        //textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textField.setBackgroundColor(getBackColor());
        //textField.setTextColor(textColor);
        textField.setClickable(false);
        panel.addView(textField);
        setInfoClick(textField);
        }
    public void showInfoMessage() {
        String ss = "Переменная окружения "+element.getEnvVarName();
        context.getMain().main().popupInfo(ss);
    }
    @Override
    public void putValue(long vv) throws UniException {
    }
    @Override
    public void repaintBefore(){
        if (!valid)
            return;
        boolean first=true;
        String out = "";
        for(Double dd : envVarValue){
            if (first)
                first=!first;
            else
                out+=",";
            out +=dd;
            }
        textField.setText(out);
        }
    @Override
    public String setParams(FormContext2 context, ESS2Architecture meta, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context,meta, element0,onEvent0);
        Meta2GUIEnvVar envVar = (Meta2GUIEnvVar) element0;
        String name= envVar.getEnvVarName();
        if (envVar.isWithUnit()) {
            FormContext2 context2 = getContext();
            int idx = context2.getIndex(context.getForm().getFormLevel());
            name += ""+idx;
            }
        ESS2EnvValue value = context.getMain().deployed.getEnvValues().getByName(name);
        if (value==null)
            return "Не найдена переменная окружения "+name;
        if (!value.isValid())
            return "Отсутствует значение переменной окружения "+name;
        envVarValue = value.getEnvValues();
        valid = true;
        return null;
    }
}
