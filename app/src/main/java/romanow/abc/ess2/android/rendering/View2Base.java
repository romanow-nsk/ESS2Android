package romanow.abc.ess2.android.rendering;

import android.view.View;

import romanow.abc.core.ErrorList;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.entity.metadata.Meta2RegLink;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIReg;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.ess2.android.NetBackProxy;
import romanow.abc.ess2.android.service.I_ModbusGroupAsyncDriver;
import romanow.abc.ess2.android.service.NetBack;

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
    public void putValue(Meta2Register register, long value, int idx){}
    public void putValue(int data[]) throws UniException {
        putValue(toOneWord(data));
        }
    public abstract void putValue(long vv)  throws UniException;
    public static long toOneWord(int data[]){
        long out=0;
        for(int i=0;i<4 && i<data.length;i++)
            out |= (((long) data[i])&0x0FFFF) << (i*16);
        return out;
        }
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
                showInfoMessage();
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
        name = "romanow.abc.ess2.android.rendering.view2.Desktop"+name.substring(5);
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
    public void readMainRegister(NetBack back) throws UniException {
        if (!(element instanceof Meta2GUIReg))
            throw UniException.config(element.getFullTitle()+" на является регистром");
        Meta2RegLink link = (Meta2RegLink)((Meta2GUIReg) element).getRegLink();
        readRegister(link,regOffset,back);
        }
    public void readRegister(Meta2RegLink link, int regOffset, final NetBack back) {
        final int regNumFull = link.getRegNum()+regOffset;
        final I_ModbusGroupAsyncDriver driver = (I_ModbusGroupAsyncDriver) device.getDriver();
        if (!link.getRegister().doubleSize())
            driver.readRegister(device.getShortName(), devUnit, regNumFull, back);
        else {
            driver.readRegister(device.getShortName(), devUnit, regNumFull, new NetBackProxy(back) {
                @Override
                public void onSuccess(Object val) {
                    final int vv = ((JInt)val).getValue();
                    driver.readRegister(device.getShortName(), devUnit, regNumFull, new NetBackProxy(back) {
                        @Override
                        public void onSuccess(Object val) {
                            final int vv2 = ((JInt)val).getValue();
                            JInt res = new JInt();
                            back.onSuccess(vv & 0x0FFFF | vv2<<16);
                            }
                        });
                    }
                });
            }
        }
    public void writeMainRegister(int value){
        writeMainRegister(value,new NetBack(){
            @Override
            public void onError(int code, String mes) {
                String ss = "Ошибка записи  Modbus: " + code + " "+mes;
                context.getMain().main().popupInfo(ss);
                }
            @Override
            public void onError(UniException ee) {
                String ss = "Ошибка записи Modbus: " + ee.toString();
                context.getMain().main().popupInfo(ss);
                }
            @Override
            public void onSuccess(Object val) {}
                });
        }
    //----------------------------------------------------------------------------------------------------------------------
    public void writeMainRegister(int vv, NetBack back){
        if (!(element instanceof Meta2GUIReg)){
            back.onError(UniException.config(element.getFullTitle()+" на является регистром"));
            return;
        }
        Meta2RegLink link = (Meta2RegLink)((Meta2GUIReg) element).getRegLink();
        writeRegister(link,regOffset,vv,back);
    }
    public void writeRegister(Meta2RegLink link, int regOffset, final int value, final NetBack back) {
        final int regNumFull = link.getRegNum()+regOffset;
        final I_ModbusGroupAsyncDriver driver = (I_ModbusGroupAsyncDriver) device.getDriver();
        if (!link.getRegister().doubleSize())
            driver.writeRegister(device.getShortName(), devUnit, regNumFull, value, back);
        else{
            driver.writeRegister(device.getShortName(), devUnit, regNumFull, value, new NetBackProxy(back) {
                @Override
                public void onSuccess(Object val) {
                    final int vv = ((JInt)val).getValue();
                    driver.writeRegister(device.getShortName(), devUnit, regNumFull, value >> 16, new NetBackProxy(back) {
                        @Override
                        public void onSuccess(Object val) {
                            back.onSuccess(null);
                            }
                        });
                    }
                });
            }
        }
    public void setDxOffset(int dxOffset) {
        this.dxOffset = dxOffset; }
    public void setDyOffset(int dyOffset) {
        this.dyOffset = dyOffset; }
    public void setGroupLevel(int groupLevel) {
        this.groupLevel = groupLevel; }
    public void setGroupIndexes(int[] groupIndexes) {
        this.groupIndexes = groupIndexes; }
    public void setDevice(ESS2Device device) {
        this.device = device; }
    public void setDevUnit(int devUnit) {
        this.devUnit = devUnit; }
    public void setUnitIdx(int unitIdx) {
        this.unitIdx = unitIdx; }
    public int getUnitIdx() {
        return unitIdx; }
    public void setRegOffset(int regOffset) {
        this.regOffset = regOffset; }
    public int[] getGroupIndexes() {
        return groupIndexes; }
    public Meta2GUI getElement() {
        return element; }
    public ESS2Architecture getArchitecture() {
        return architecture; }
    public I_GUI2Event getOnEvent() {
        return onEvent; }
    public ESS2Device getDevice() {
        return device; }
    public int getDevUnit() {
        return devUnit; }
    public int getRegOffset() {
        return regOffset; }
    public int getDxOffset() {
        return dxOffset; }
    public int getDyOffset() {
        return dyOffset; }
    public int getGroupLevel() {
        return groupLevel; }
    public FormContext2 getContext() {
        return context; }
}
