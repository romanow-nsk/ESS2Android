package romanow.abc.ess2.android.rendering.view2;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIScript;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2ScriptFile;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.ScriptException;
import romanow.abc.core.types.TypeFace;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIScript extends View2BaseDesktop {
    private TextView textField;
    private Meta2GUIScript element;
    ESS2ScriptFile scriptFile=null;
    public DesktopGUIScript(){
        type = Values.GUIScript;
    }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        FormContext2 context= getContext();
        element = (Meta2GUIScript)  getElement();
        int textColor = context.getView().getTextColor() | 0xFF000000;
        textField = new BorderedTextView(context.getMain().main(),textColor);
        int dd=element.getW2();
        if (dd==0) dd=DefaultW2;
        int hh = element.getH();
        if (hh==0) hh=DefaultH;
        setBounds(textField,
                context.x(element.getX()+getDxOffset()+element.getDx()+DefaultSpace),
                context.y(element.getY()+getDyOffset()),
                context.x(dd),
                context.y(hh));
        //textField.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
        int textSize = element.getFontSize();
        if (textSize==0) textSize = DefaultTextSize;
        textField.setTextSize(textSize);
        textField.setClickable(false);
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(textField);
        textField.setBackgroundColor(getBackColor());
        //textField.setBorder(BorderFactory.createLineBorder(textColor,1));
        textField.setTextColor(textColor);
        setInfoClick(textField);
        }
    public void showInfoMessage() {
        String ss = "Данные скрипта "+element.getScripName();
        context.getMain().main().popupAndLog(ss);
        }
    @Override
    public void putValue(int vv) throws UniException {
    }
    @Override
    public void repaintBefore(){
        if (scriptFile==null || !scriptFile.isValid())
            return;
        try {
            CallContext call = scriptFile.getScriptCode();
            call.reset();
            call.call(false);
            TypeFace result = scriptFile.getScriptCode().getVariables().get(Values.ScriptResultVariable);
            if (result==null)
                context.getMain().main().errorMes("Ошибка исполнения скрипта\nОтстутствует результат");
            else
                textField.setText(result.valueToString());
        } catch (ScriptException e) {
            context.getMain().main().errorMes("Ошибка исполнения скрипта\n"+e.toString());
            }
        }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2GUIScript script = (Meta2GUIScript)element0;
        scriptFile = meta0.getScripts().getByName(script.getScripName());
        if (scriptFile==null)
            return "Не найден скрипт "+script.getScripName();
        if (scriptFile.isServerScript())
            return "Cкрипт "+script.getScripName()+" серверный";
        if (scriptFile.getScriptType()!=Values.STCalcClient)
            return "Cкрипт "+script.getScripName()+" - недопустимый тип "+scriptFile.getScriptType();
        if (!scriptFile.isPreCompiled())
            return "Cкрипт "+script.getScripName()+" не компилируется предварительно";
        if (!scriptFile.isValid())
            return "Cкрипт "+script.getScripName()+" нет кода";
        return null;
    }
}
