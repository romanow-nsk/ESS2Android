package romanow.abc.ess2.android.rendering;

import android.view.View;

import romanow.abc.core.ErrorList;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2RegLink;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIReg;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;

public abstract class View2Base implements I_View2 {
    protected int type= Values.GUINull;
    protected Meta2GUI element=null;          // Элемент мета-данных
    protected ESS2Architecture architecture;
    protected I_GUI2Event onEvent=null;
    protected ESS2Device device=null;         // Описание unit-драйвер
    protected int devUnit=0;                  // Явный номер Unit-а (физический)
    protected int unitIdx=0;                  // Явный индекс Unit-а (логический)
    protected int regOffset=0;
    protected int dxOffset=0;
    protected int dyOffset=0;
    protected int groupLevel=0;                           // Уровень групповых элементов
    protected int groupIndexes[]=new int[Values.FormStackSize];   // Индексы МЕТАЭЛЕМЕНТОВ в группах
    protected FormContext2 context;
    public int getType(){ return type; }
    public Meta2RegLink getRegLink(){                           // Линк регистра в мета-данных
        return element.getRegLink();
        }
    public Meta2RegLink[] getSettingsLinks(){
        return element.getSettingsLinks();
        }
    public Meta2RegLink[] getDataLinks(){
        return element.getDataLinks();
        }
    public void repaintBefore(){}                               // Для исключительных действий (скрипты)
    public void repaintValues(){}                               // После прочтения всех данных
    public void putValue(Meta2Register register, int value, int idx){}
    public abstract void showInfoMessage();
    @Override
    public String getTypeName() {
        return Values.constMap().getGroupMapByValue("GUIType").get(type).title(); }
    @Override
    public String getTitle() {
        return element==null ? "" : element.getTitle();
    }
    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        context = context0;
        onEvent = onEvent0;
        architecture = meta0;
        element = element0;
        return null;
    }

    private I_Value<String> onClose = new I_Value<String>() {               // Событие - закрытие визарда - обновитьь ЧМИ
        @Override
        public void onEnter(String value) {
            onEvent.onEnter(View2Base.this,0,"");
        }
    };
    private I_Value<String> onChange = new I_Value<String>() {               // Событие - Изменение элемента формы
        @Override
        public void onEnter(String value) {}
        };
    public void setInfoClick(View textField){
        textField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context.isInfoMode() ){
                    showInfoMessage();
                    }
                return true;
                }
            });
        }
    public void setBitNum(int nbit){}
    public String toString(){
        return getTitle()+" "+Values.title("GUITypeName",getType())+" x="+element.getX()+" y="+element.getY();
    }
    //--------------------------------------------------------------------------------------------------
    public static View2Base createGUIElement(ErrorList errorList, String platform, Meta2GUI entity){
        String name = entity.getClass().getSimpleName();
        if (!name.startsWith("Meta2")){
            errorList.addError("Недопустимое имя класса "+name);
            return null;
        }
        name = "romanow.abc.ess2.android.rendering.view2.desktop"+name.substring(5);
        try {
            Class  cls = Class.forName(name);
            View2Base element = (View2Base)cls.newInstance();
            return element;
        } catch (Exception ee){
            errorList.addError("Ошибка создания элемента ЧМИ "+name+": "+ee.toString());
            return null;
        }
    }
    //--------------------------------------------------------------------------------------------------
    public Meta2Register getRegister() {
        if (!(element instanceof Meta2GUIReg))
            return null;
        return (Meta2Register) ((Meta2GUIReg) element).getRegLink().getRegister();
    }
    public int readMainRegister() throws UniException {
        if (!(element instanceof Meta2GUIReg))
            throw UniException.config(element.getFullTitle()+" на является регистром");
        Meta2RegLink link = (Meta2RegLink)((Meta2GUIReg) element).getRegLink();
        int vv = device.getDriver().readRegister(device.getShortName(),devUnit,link.getRegNum()+regOffset);
        return vv;
    }
    public void writeMainRegister(int vv) throws UniException {
        if (!(element instanceof Meta2GUIReg))
            throw UniException.config(element.getFullTitle()+" на является регистром");
        Meta2RegLink link = (Meta2RegLink)((Meta2GUIReg) element).getRegLink();
        int regNumFull = link.getRegNum()+regOffset;
        device.getDriver().writeRegister(device.getShortName(),devUnit,regNumFull,vv & 0x0FFFF);
        if (link.getRegister().doubleSize())
            device.getDriver().writeRegister(device.getShortName(),devUnit,regNumFull+1,vv>>16 & 0x0FFFF);
    }
    public int readRegister(Meta2RegLink link, int regOffset) throws UniException {
        int regNumFull = link.getRegNum()+regOffset;
        int vv = device.getDriver().readRegister(device.getShortName(),devUnit,regNumFull) & 0x0FFFF;
        if (link.getRegister().doubleSize()){
            int vv2 = device.getDriver().readRegister(device.getShortName(),devUnit,regNumFull+1) & 0x0FFFF;
            vv |=vv2<<16;
        }
        return vv;
    }
    public void writeRegister(Meta2RegLink link,int vv, int regOffset) throws UniException {
        device.getDriver().writeRegister(device.getShortName(),devUnit,link.getRegNum()+regOffset,vv);
        }

}
