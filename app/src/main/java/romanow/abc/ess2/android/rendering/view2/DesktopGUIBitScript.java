package romanow.abc.ess2.android.rendering.view2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUI2StateBox;
import romanow.abc.core.entity.metadata.view.Meta2GUIBitScript;
import romanow.abc.core.entity.metadata.view.Meta2GUIScript;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2ScriptFile;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.ScriptException;
import romanow.abc.core.types.TypeFace;
import romanow.abc.ess2.android.I_EventListener;
import romanow.abc.ess2.android.OKDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIBitScript extends View2BaseDesktop {
    private Meta2Bit bit;
    protected View textField;
    private Meta2GUIBitScript element;
    ESS2ScriptFile scriptFile=null;
    private int sz=0;
    public DesktopGUIBitScript(){
        type = Values.GUIBitScript;
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
        //bb.setImageResource(getColorIconName(cc));
        bb.setImageBitmap(getColorIconBitmap(cc));
        }
    protected int getSize(){
        return 35; }
    //-----------------------------------------------------------------------
    private int getColorIconName(int color){
        switch (color){
            case 0x00C0C0C0: return R.drawable.balllightgray;
            case 0x00FF0000: return R.drawable.ballred;
            case 0x0000FF00: return R.drawable.ballgreen;
            case 0x0000A000: return R.drawable.balldarkgreen;
            case 0x00FFFF00: return R.drawable.ballyellow;
            case 0x00F0A000: return R.drawable.balldarkyellow;
            }
        int v=0;
        return R.drawable.ballwhite;
        }
    private Bitmap getColorIconBitmap(int color){
        int szz = context.dy(getSize());
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getMain().main().getResources(), getColorIconName(color));
        return Bitmap.createScaledBitmap(bitmap2,szz,szz,false);
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        element = (Meta2GUIBitScript) getElement();
        int hh = element.getH();
        if (hh==0) hh=25;
        if (element.getDx()!=0){
            setLabel(panel);
        }
        textField = createComponent();
        int dd = element.getW2();
        if (dd==0) dd=50;
        sz = getSize();
        int offset = (25-sz)/2;
        FormContext2 context = getContext();
        int xx = element.getX()+element.getDx()+getDxOffset()+dd-5+offset;
        int yy = element.getY()+getDyOffset()+(hh-15)/2-5+offset;
        setBounds(textField,
                context.x(xx),
                context.y(yy),
                context.dx(sz),
                context.dy(sz));
        viewComponent();
        setInfoClick(textField);
        panel.addView(textField);
        textField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    @Override
    public void repaintBefore(){
        if (scriptFile==null || !scriptFile.isValid())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {         // Выполнить скрипт в потоке !!!!!!!!!!!!!!!!!!!!!
                try {
                    CallContext call = scriptFile.getScriptCode();
                    call.reset();
                    call.call(false);
                    final TypeFace result = scriptFile.getScriptCode().getVariables().get(Values.ScriptResultVariable);
                    context.getMain().main().guiCall(new Runnable() {
                        @Override
                        public void run() {
                            if (result==null)
                                context.getMain().main().errorMes("Ошибка исполнения скрипта\nОтстутствует результат");
                            else {
                                putValueOwn(result.isBoolValue() ? element.getColorYes() : element.getColorNo());
                                //textField.setText(result.valueToString());
                                }
                            }
                        });
                } catch (ScriptException e) {
                    context.getMain().main().errorMes("Ошибка исполнения скрипта\n"+e.toString());
                }
            }
        }).start();
    }

    @Override
    public void putValue(long vv) throws UniException {}

    public void showInfoMessage() {
        String ss = "Данные скрипта "+element.getScripName();
        context.getMain().main().popupInfo(ss);
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
