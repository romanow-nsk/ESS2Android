package romanow.abc.ess2.android.service;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import retrofit2.Call;
import romanow.abc.core.ErrorList;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.IntegerList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.UnitRegisterList;
import romanow.abc.core.entity.metadata.Meta2Array;
import romanow.abc.core.entity.metadata.Meta2Entity;
import romanow.abc.core.entity.metadata.Meta2EntityList;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.metadata.Meta2GUIView;
import romanow.abc.core.entity.metadata.Meta2RegLink;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIArray;
import romanow.abc.core.entity.metadata.view.Meta2GUICollection;
import romanow.abc.core.entity.metadata.view.Meta2GUIReg;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.entity.subject2area.ESS2Equipment;
import romanow.abc.core.entity.subjectarea.WorkSettings;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.I_ListBoxListener;
import romanow.abc.ess2.android.ListBoxDialog;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.module.I_Module;
import romanow.abc.ess2.android.rendering.module.Module;
import romanow.abc.ess2.android.rendering.ScreenMode;
import romanow.abc.ess2.android.rendering.View2Base;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.rendering.view2.I_Button;

public class ESS2Rendering {
    public final static int FrameH=530;
    public final static int FrameW=400;
    private ESS2ArchitectureData main2;
    private I_Button logoutCallBack = null;                     // CallBack кнопки выхода
    private OwnDateTime userLoginTime = new OwnDateTime();
    private ErrorList errorList = new ErrorList();
    private int essOnOffState= Values.ESSStateNone;             // Состояние СНЭ
    private final static String mainFormName="Главный";
    private Module module=null;
    private ArrayList<View2Base> guiList = new ArrayList<>();
    private RelativeLayout formPanel=null;
    private ConstraintLayout formView=null;
    private Button formMenuButton=null;
    //------------------------------------------------------------------------------------------------------------------
    public void openFormDialog(){
        if (formView!=null){
            main2.main().getLogLayout().removeView(formView);
            formView = null;
            formPanel = null;
            }
        main2.main().clearLog();
        formView =(ConstraintLayout)main2.main().getLayoutInflater().inflate(R.layout.form_frame, null);
        formPanel = (RelativeLayout) formView.findViewById(R.id.form_frame_panel);
        formPanel.setPadding(0, 5, 5, 5);
        formPanel.setBackgroundColor(main2.currentView.getView().getBackColor()|0xFF000000);
        LinearLayout layout = main2.main().getLogLayout();
        layout.addView(formView);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)formPanel.getLayoutParams();
        context.setScreen(new ScreenMode(params.height,params.height*3/4,AppData.ScreenMas));      // pdMode координат
        //main2.main().addToLog(""+params.width+" "+params.height);
        main2.main().scrollDown();
        }
    public void openUpperForm(){
        String parent = context.getBaseForm().getParentName();
        if (parent.equals(mainFormName))
            context.openForm(mainFormName,true);
        else
            context.openForm(parent,true);
        }
    public void closeFormDialog(){

        }
    public ESS2Rendering(ESS2ArchitectureData data0){
        main2 = data0;
        }
    private void popup(String ss){
        main2.main().popupAndLog(ss);
        }
    private FormContext2 context = new FormContext2(){
        @Override
        public void reOpenForm() {
            repaintView();
            }
        public void openForm(String formName, boolean clearIdx) {
            Meta2GUIForm form = main2.currentView.getView().getForms().getByTitle(formName);
            if (form==null){
                popup("Не найдена форма "+formName);
                return;
                }
            openForm(form,clearIdx);
            }
        @Override
        public void openForm(Meta2GUIForm form, boolean clearIdx) {
            //---------------- Поиск для групповых кнопок -------------------------------------------------
            setBaseForm(form);
            if (form.isLinkForm()){
                String baseFormName = form.getShortName();
                Meta2GUIForm baseForm = main2.currentView.getView().getForms().getByTitle(baseFormName);
                if (baseForm==null){
                    popup("Не найдена базовая форма "+baseFormName);
                    return;
                }
                int idx = form.getBaseFormIndex();
                int level = form.getFormLevel();
                if (level!=0 && idx!=-1)            // Индекс для предыдущего уровня их групповой кнопки меню
                    setIndex(level,idx);
                setBaseForm(baseForm);
                }
            //---------------------------------------------------------------------------------------------
            if (main2.manager.getCurrentAccessLevel() > form.getAccessLevel()){
                popup("Недостаточен уровень доступа");
                return;
            }
            Meta2GUIForm prev = getForm();
            if (prev!=null && form.getLevel()>prev.getLevel()+1){
                popup("Пропущен уровень формы при переходе из "+prev.getTitle()+" в "+form.getTitle());
                }
            setForm(form);
            int level = form.getLevel();
            if (level!=0 && clearIdx){
                setIndex(level,0);
                setName(level,getForm().getTitle());
                setSize(level,getBaseForm().getElementsCount());
                };
            int vv[] = getIdx();
            System.out.println("Стек индексов: "+vv[0]+" "+vv[1]+" "+vv[2]+" "+vv[3]);
            String ss[] = getMenuFormStack();
            System.out.println("Стек форм: "+ss[0]+" "+ss[1]+" "+ss[2]+" "+ss[3]);
            repaintView();
            }
        };
    //---------------------------------------------------------------------------------------------------------------------------
    public void renderOn(){
        AppData ctx = AppData.ctx();
        context.setView(main2.currentView.getView());
        context.setManager(main2.manager);
        context.setLocalUser(false);
        context.setLocalUser(false);
        context.setSuperUser(main2.manager.getUser().getTypeId()==Values.UserSuperAdminType);
        context.setService(ctx.getService());
        context.setService2(ctx.getService2());
        context.setToken(ctx.loginSettings().getSessionToken());
        context.setPlatformName("Android");
        context.setMain(main2);
        userLoginTime = new OwnDateTime();
        setMainForm();
        createLoopThread();
        repaintView();
        }
    public void renderOff(){            // Вызывается из ESS2ArchitectureData
        setRenderingOnOff(false);
        if (formMenuButton!=null)
            formMenuButton.setVisibility(View.INVISIBLE);
        context.setForm(null);
        main2.main().getLogLayout().removeView(formView);
        main2.main().scrollDown();
        }
    //------------------------------ Цикл рендеринга ------------------------------------------------------
    private Thread guiLoop = null;
    private boolean shutDown=false;
    private int renderSeqNum=0;                                         // Последовательный номер рендеринга
    private boolean renderingOn=false;
    private void setRenderingOnOff(boolean vv){
        renderingOn = vv;
        }
    //private boolean repaintValuesOn=false;                           // Обновление данных
    //private boolean repaintBusy=false;                               // Обновление формы
    public synchronized void shutDown() {
        shutDown = true;
        wokeUp();}
    private synchronized void wokeUp(){
        guiLoop.interrupt(); }
    private void createLoopThread() {
        shutDown=false;
        guiLoop = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!shutDown){
                    long tt =  new OwnDateTime().timeInMS();
                    try {
                        Thread.sleep(((WorkSettings) AppData.ctx().workSettings()).getGUIrefreshPeriod() * 1000);
                        } catch (InterruptedException e) {
                            System.out.println("Разбудили: " + (new OwnDateTime().timeInMS()-tt));
                            }
                    if (shutDown){
                        main2.setRenderingOff();
                        return;
                        }
                    long sec = (new OwnDateTime().timeInMS() - userLoginTime.timeInMS()) / 1000;
                    if (logoutCallBack != null && sec > ((WorkSettings) AppData.ctx().workSettings()).getUserSilenceTime() * 60) {
                        shutDown();
                        logoutCallBack.onPush();
                        return;
                    }
                    if (!renderingOn)
                        continue;
                    main2.main().guiCall(new Runnable() {
                        @Override
                        public void run() {
                            repaintValues();
                            }
                        });
                    }
                }
            });
        guiLoop.start();
        }
    //------------------------------------------------------------------------------------------------------------------------
    public void createChildFormList(){
        Meta2GUIForm baseForm = context.getBaseForm();
        String currentName = baseForm.getTitle();
        final ArrayList<Meta2GUIForm> childs = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        if (!currentName.equals(mainFormName)){
            names.add(baseForm.getParentName());
            }
        names.add(currentName);
        Meta2GUIView currentView = main2.currentView.getView();
        Meta2EntityList<Meta2GUIForm> formList = currentView.getForms();
        for (Meta2GUIForm next : formList.getList()) {
            if (!next.getParentName().equals(currentName))
                continue;
            if (next.isBaseForm())
                continue;
            childs.add(next);
            names.add(next.getTitle().replace("_",""));
            }
        names.add("Выход");
        new ListBoxDialog(main2.main(), names, null, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                if (index!=names.size()-1)
                    context.openForm(names.get(index),true);
                else
                    main2.setRenderingOff();
                }
            @Override
            public void onLongSelect(int index) {
                }
            @Override
            public void onCancel() {}
            }).create();
        }
    //------------------------------------------------------------------------------------------------------------------------
    public synchronized void  repaintView() {
        AppData ctx = AppData.ctx();
        setRenderingOnOff(true);
        Meta2GUIView currentView = main2.currentView.getView();
        Meta2EntityList<Meta2GUIForm> formList = currentView.getForms();
        openFormDialog();
        formPanel.setBackgroundColor(currentView.getBackColor());
        errorList.clear();
        if (main2.currentView == null) {
            return;
            }
        if (context.getForm() == null )
            if (!setMainForm()){
                return;
                }
        if (context.getForm().isEmpty()){         // Рекурсивно пропустить пустые экраны
            context.setForm(formList.getByTitle(context.getForm().getChilds().get(0).getTitle()));
            repaintView();
            }
        for(ESS2Device device : main2.deployed.getDevices()){          // Очистить кэши
            device.clearCash();
            }
        //-----------------------------------------------------------------------------------
        Meta2GUIForm form = context.getForm();
        Meta2GUIForm baseForm = context.getBaseForm();
        int level = context.getForm().getLevel();
        /*--------------- TODO   OnOff Button ----------------------------------------------------
        OnOff = new JButton();
        OnOff.setBorderPainted(false);
        OnOff.setContentAreaFilled(false);
        OnOff.addActionListener(new ActionListener() {      //???????????????????????????????????????????????
            @Override
            public void actionPerformed(ActionEvent e) {
                testESSOnOffState();
                if (essOnOffState==Values.ESSStateNone)
                    return;
                if (essOnOffState==Values.ESSStateOff && main2.manager.getCurrentAccessLevel() > Values.AccessLevel2){
                    popup("Недостаточен уровень доступа");
                    return;
                }
                new OK(200, 200, essOnOffState == Values.ESSStateOff ? "Включить СНЭ" : "Выключить СНЭ", new I_Button() {
                    @Override
                    public void onPush() {
                        try {
                            //---------------- TODO --- ГЛАВНАЯ КНОПКА ------------------------------------------
                            main2.plm.writeRegister("",0,Values.LocalCommandRegister, essOnOffState==Values.ESSStateOff ?
                                    Values.ESSOnCommand : Values.ESSOffCommand);
                            testESSOnOffState();
                        }catch (UniException ee){
                            popup("Не выполняется команда ПЛК "+Values.ESSStateRegister+"\n"+ee.toString());
                        }
                    }
                });
            }
        });
        add(OnOff);
        OnOff.setBounds(context.x(5), context.y(10), context.x(50), context.y(50));
        testESSOnOffState();
        //------------------ TODO ------------------ к главной форме ---------------------------------
        JButton toMain = new JButton();
        toMain.setIcon(new javax.swing.ImageIcon(getClass().getResource(buttonToMain))); // NOI18N
        //toMain.setBorderPainted(false);
        //toMain.setContentAreaFilled(false);
        toMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.openForm(mainFormName);
            }
        });
        add(toMain);
        toMain.setBounds(context.x(870), context.y(620), context.x(40), context.y(40));
        //------------------- TODO --------------- выход из сеанса ---------------------------------
        JButton logout = new JButton();
        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource(buttonLogout))); // NOI18N
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (logoutCallBack!=null){
                    shutDown();
                    logoutCallBack.onPush();
                }
            }
        });
        add(logout);
        logout.setBounds(context.x(870), context.y(10), context.x(40), context.y(40));
         */
        //-----------------------------------------------------------------------------------
        if (context.getForm().getTitle().equals(mainFormName)){
            TextView userTitle = new TextView(main2.main());
            int  access = context.getManager().getCurrentAccessLevel();
            String ss = "  "+context.getManager().getUser().getTitle()+" ["+Values.title("AccessLevel",access)+"] ";
            userTitle.setText(ss);
            View2BaseDesktop.setBounds(userTitle,context.x(20),context.y(FrameH-50), context.x(400),context.y(50));
            userTitle.setClickable(false);
            //userTitle.setFont(new Font("Arial Cyr", Font.PLAIN, context.y(12)));
            formPanel.addView(userTitle);
            }
        //-----------------------------------------------------------------------------------
        level = baseForm.getLevel();
        LinearLayout layout =   (LinearLayout)main2.main().getLayoutInflater().inflate(R.layout.render_menu_button,null);
        formPanel.addView(layout);
        formMenuButton = layout.findViewById(R.id.renderMenu);
        //formMenuButton = main2.main().findViewById(R.id.headerRenderMenu);
        formMenuButton.setText(baseForm.getTitle());
        formMenuButton.setVisibility(View.VISIBLE);
        formMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChildFormList();
                }
            });
        formMenuButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
                }
            });
        //----------------------------------- Рисование элементов управления
        guiList.clear();
        int idx[] = new int[Values.FormStackSize];
        renderGuiElement(baseForm.getControls(),0,0,0,idx);  // Рекурсивный рендеринг для всех уровней
        for(View2Base view :  guiList)
            ((View2BaseDesktop)view).addToPanel(formPanel);                      // Добавить на панель
        //----------------------------------------------------------------------------------
        module=null;
        Meta2GUIForm ff = context.getForm();
        if (!ff.noModule()){
            try {
                Class clazz = Class.forName(AppData.ESS2ModulePackage+"."+ff.getModuleName());
                if (clazz==null){
                    errorList.addError("Не найден класс модуля "+ ff.getModuleName());
                    }
                else{
                    module = (Module)clazz.newInstance();
                    module.init(main2,formPanel,ctx.getService(),ctx.getService2(),ctx.loginSettings().getSessionToken(), context.getForm(),context);
                    }
                } catch (Exception ee){
                    errorList.addError("Ошибка создания объекта для модуля "+ ff.getModuleName());
                    }
            }
        //------------------------------------------------------------------------------------
        //repaintBusy=false;
        if (!errorList.valid()){
            main2.main().errorMes(""+errorList.getErrCount()+ " ошибок рендеринга");
            main2.main().errorMes(errorList.toString());
            }
        //formDialog.show();
        wokeUp();
        }
    //------------------------------------------------------------------------------------------------------------------------
    public boolean setMainForm(){
        if (context.getForm()==null){
            Meta2GUIForm form = main2.currentView.getView().getForms().getByTitle(mainFormName);
            context.setForm(form);
            context.setBaseForm(form);
            }
        if (context.getForm()==null){
            popup("Не найдена главная форма");
            }
        return context.getForm()!=null;
        }
    private void setForm(Meta2GUIForm aaa){
        context.setForm(aaa);
        repaintView();
        }
    //------------------------------------------------------------------------------------------------------------------------
    private I_GUI2Event retryPaintValues = new I_GUI2Event(){
        @Override
        public void onEnter(View2Base element, int iParam, String sParam) {
            guiLoop.interrupt();
            userLoginTime = new OwnDateTime();
            repaintView();
            }
        };
    //-----------------------------------------------------------------------------------------------------------------------
    //----------------------- Рендеринг элементов ----------------------------------------
    public void renderGuiElement(Meta2GUI meta, int baseX, int baseY, int groupLevel, int groupIndexes[]){
        if (meta instanceof Meta2GUIArray){
            Meta2GUIArray array = (Meta2GUIArray) meta;
            Meta2GUI elem = array.getElem();
            for(int i=0;i<array.getSize();i++){
                int cIdx = i;
                groupIndexes[groupLevel] = cIdx;        // Относительные индексы в группе (уровень 1 - level=0)
                int xx = baseX + (array.getDxy() < 0 ? -array.getDxy()*i : 0);
                int yy = baseY + (array.getDxy() > 0 ? array.getDxy()*i : 0);
                renderGuiElement(elem, xx,yy,groupLevel+1,groupIndexes);
            }
        }
        else
        if (meta instanceof Meta2GUICollection){
            Meta2GUICollection collection = (Meta2GUICollection) meta;
            for(Meta2GUI elem : collection.getList()){
                renderGuiElement(elem, baseX,baseY,groupLevel,groupIndexes);
            }
        }
        else{
            //--------------- TODO ------------ вроде не надо --------------------------------------------
            //int formLevel = context.getForm().getLevel();
            //if (groupLevel > formLevel){
            //    errorList.addError("Уровень элемента "+meta.getFullTitle()+"="+groupLevel+" больше уровня формы "+context.getForm().getFullTitle()+"="+formLevel);
            //    return;
            //    }
            View2Base newElem = View2Base.createGUIElement(errorList,context.getPlatformName(),meta);
            if (newElem==null)
                return;
            String ss = newElem.setParams(context,main2.deployed,meta,retryPaintValues);
            if (ss!=null){
                errorList.addError(ss);
                return;
                }
            newElem.setDxOffset(baseX);
            newElem.setDyOffset(baseY);
            newElem.setGroupLevel(groupLevel);
            int vv[] = newElem.getGroupIndexes();
            for(int i=0;i<Values.FormStackSize;i++)
                vv[i]=groupIndexes[i];
            if (meta instanceof Meta2GUIReg) {          // Для регистров - индексы массивов по индесам View
                Meta2GUIReg regGUI = (Meta2GUIReg)meta;
                Meta2RegLink link = regGUI.getRegLink();
                Meta2Register register = link.getRegister();
                String equipName= regGUI.getEquipName();
                ESS2Equipment equipment = main2.deployed.getEquipments().getByName(equipName);
                if (equipment==null){
                    errorList.addError("Не найдено оборудование "+equipName+" для "+regGUI.getFullTitle());
                    return;
                }
                int connectorsSize = equipment.getLogUnits().size();
                if (connectorsSize==0){
                    errorList.addError("Нет устройств для "+equipName);
                    return;
                }
                //------------- Подсчет смещения регистров, индексов контроллеров и Unit  ---------------------
                int treeLevel = register.getArrayLevel()-1;         // Кол-во массивов в дереве Meta-элементов (+device+units) -1
                int grlevel = groupLevel-1;                         // Кол-во массивов в форме
                int stacklevel = context.getForm().getLevel()-1;    // Вершина стека индексов форм для тек. уровня
                if (!link.isOwnUnit() && treeLevel > stacklevel){
                    errorList.addError("Уровень массива мета-данных > уровня формы "+
                            equipName+" для "+regGUI.getFullTitle()+"="+(treeLevel+1)+" "+
                            context.getForm().getTitle()+"="+(stacklevel+1));
                    return;
                }
                int regOffset=0;
                stacklevel = treeLevel;
                if (link.isOwnUnit()){      // Unit задан явно - не групповые = явно перечисленные
                    newElem.setRegOffset(0);
                    newElem.setUnitIdx(link.getUnitIdx());
                }
                else{                       // Иначе генерация по массивам
                    for (Meta2Entity cc = register.getHigh(); cc != null; cc = cc.getHigh()) {
                        if (!(cc instanceof Meta2Array))
                            continue;
                        Meta2Array array = (Meta2Array) cc;
                        int elemIdx = context.getIndex(stacklevel + 1);
                        elemIdx += grlevel < 0 ? 0 : groupIndexes[grlevel];
                        if (elemIdx >= array.getSize())          // Выход за пределы массива
                            return;
                        switch (array.getArrayType()) {
                            case Values.ArrayTypeModbus:
                                regOffset += array.getStep() * elemIdx;
                                break;
                            case Values.ArrayTypeUnit:
                                if (!regGUI.getRegLink().isOwnUnit())
                                    newElem.setUnitIdx(elemIdx);
                                break;
                        }
                        grlevel--;
                        stacklevel--;
                    }
                    newElem.setRegOffset(regOffset);
                }
                if (newElem.getUnitIdx() >= connectorsSize){
                    errorList.addError("Индекс Unit "+newElem.getUnitIdx()+" превышен  "+equipName+" для "+regGUI.getFullTitle());
                    return;
                }
                newElem.setDevice(equipment.getLogUnits().get(newElem.getUnitIdx()).getDevice().getRef());
                newElem.setDevUnit(equipment.getLogUnits().get(newElem.getUnitIdx()).getUnit());        // Физический Unit
            }
            guiList.add(newElem);
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------
    public void putOneLinkRegister(View2Base element,Meta2RegLink link,int offset){
        int regNumFull = link.getRegNum()+offset;
        int regSize = link.getRegister().size16Bit();
        for(int i=0;i<regSize;i++)
            element.getDevice().putValue(element.getDevUnit(),regNumFull+i,0);     // Регистр со смещением
        }
    public synchronized void repaintValues(){       // По физическим устройствам
        if (!renderingOn)
            return;
        testESSOnOffState();
        if (module!=null)
            module.repaintValues();
        for (View2Base element : guiList){
            element.repaintBefore();
        }
        for (View2Base element : guiList){
            Meta2RegLink link = element.getRegLink();
            if (link==null)
                continue;
            putOneLinkRegister(element,link,element.getRegOffset());
            //ESS2Device device = element.getDevice();
            //int regNumFull = link.getRegNum()+element.getRegOffset();                   // Двойные регистры
            //int regSize = link.getRegister().size16Bit();
            //for(int i=0;i<regSize;i++)
            //    device.putValue(element.getDevUnit(),regNumFull+i,0);     // Регистр со смещением
            Meta2RegLink vv[] = element.getSettingsLinks();
            //---------- Вспомогательные регистры с того же девайса и юнита, что и основной, без смещения
            for(Meta2RegLink link2 : vv){
                putOneLinkRegister(element,link2,0);
                //regNumFull = link2.getRegNum();
                //regSize = link.getRegister().size16Bit();
                //for(int i=0;i<regSize;i++)
                //    device.putValue(element.getDevUnit(),regNumFull+i,0); // Регистр БЕЗ СМЕЩЕНИЯ
            }
            vv = element.getDataLinks();
            for(Meta2RegLink link2 : vv){
                putOneLinkRegister(element,link2,element.getRegOffset());
                //regNumFull = link2.getRegNum();
                //regSize = link.getRegister().size16Bit();
                //for(int i=0;i<regSize;i++)
                //    device.putValue(element.getDevUnit(),regNumFull+i,0); // Регистр БЕЗ СМЕЩЕНИЯ
            }
        }
        renderSeqNum++;             // Установить след. номер запроса
        for(ESS2Device device : main2.deployed.getDevices()){
            ArrayList<UnitRegisterList> list2 = device.createList(false);
            for(UnitRegisterList list : list2){
                System.out.println(device.getShortName()+"["+list.getUnitIdx()+"]="+list.size());
                readPLMRegistersAsync(device,list,context.getForm());               // Асинхронная версия
            }
        }
    }
    //--------------------------------------------------------------------------------------------------------------
    private synchronized void readPLMRegistersAsync(final ESS2Device device,final UnitRegisterList list, final Meta2GUIForm currentForm){
        final int currentRenderSeqNum = renderSeqNum;
        final long tt =  new OwnDateTime().timeInMS();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final IntegerList values = new APICall2<IntegerList>() {
                        @Override
                        public Call apiFun() {
                            return AppData.ctx().getService2().readESS2RegistersValues(AppData.ctx().loginSettings().getSessionToken(), device.getShortName(),list);
                        }
                    }.call(main2.main());
                    main2.main().guiCall(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (ESS2Rendering.this){
                                if (renderSeqNum!=currentRenderSeqNum){      // Совпадение посл.номера запроса
                                    System.out.println("Несовпадение номеров запроса (трассировка) "+renderSeqNum+" "+currentRenderSeqNum);
                                    return;
                                    }
                                }
                            try {
                                repaintValuesOnAnswer(device, list.getUnitIdx(), values,currentForm);
                                System.out.println("Ждали: " + (new OwnDateTime().timeInMS()-tt));
                                } catch (UniException e) {
                                    AppData.ctx().popupAndLog(true,"Ошибка GUI: "+e.toString());
                                    }
                                }
                        });
                    } catch (final UniException ee){
                        main2.main().guiCall(new Runnable() {
                            @Override
                            public void run() {
                                AppData.ctx().popupAndLog(true,"Ошибка сервера: "+ee.toString());
                                main2.clearDeployedMetaData();
                                }
                            });
                        }
                    }
                }).start();
            }
    //---------------------------------------------------------------------------------------------
    public int []getRegisterData(ESS2Device device,Meta2RegLink link, int unitIdx,int offset){
        int regNumFull = link.getRegNum()+offset;
        int regSize = link.getRegister().size16Bit();
        int data[] = new int[regSize];
        boolean good=true;
        for(int i=0;i<regSize;i++){
            Integer vv = device.getValue(unitIdx,regNumFull+i);
            if (vv==null){
                popup("Не найден регистр в ответе сервера "+(regNumFull+i));
                good=false;
                break;
                }
            data[i]=vv;
            }
            return good ? data : null;
        }
    //---------------------------------------------------------------------------------------------------------------------------------
    public void repaintValuesOnAnswer(ESS2Device device, int unitIdx,IntegerList values,Meta2GUIForm currentForm) throws UniException {
        if (currentForm!=context.getForm())
            return;
        device.clearCash(unitIdx);
        for(int i=0;i<values.size();i+=2)
            device.putValue(unitIdx,values.get(i),values.get(i+1));
        for (View2Base element : guiList){
            Meta2RegLink link = element.getRegLink();
            if (link==null)
                continue;
            if (!element.getDevice().getShortName().equals(device.getShortName()))        // Пропустить из чужого мапа
                continue;
            if (element.getDevUnit()!=unitIdx)      // Пропустить чужой физический Unit
                continue;
            int data[] = getRegisterData(device,link,unitIdx,element.getRegOffset());
            if (data==null)
                continue;
            element.putValue(data);
            Meta2RegLink links[] = element.getSettingsLinks();
            for(int i=0;i<links.length;i++){
                data = getRegisterData(device,links[i],unitIdx,0);
                if (data==null)
                    continue;
                if (data.length>4){
                    popup("Ошибка размерности доп. регистра "+links[i].getRegNum());
                    continue;
                    }
                element.putValue(links[i].getRegister(),View2Base.toOneWord(data),i);
                }
            links = element.getDataLinks();
            for(int i=0;i<links.length;i++){
                data = getRegisterData(device,links[i],unitIdx,element.getRegOffset());
                if (data==null)
                    continue;
                if (data.length>4){
                    popup("Ошибка размерности доп. регистра "+links[i].getRegNum());
                    continue;
                    }
                element.putValue(links[i].getRegister(),View2Base.toOneWord(data),i);
                }
            element.repaintValues();
            }
        if (module!=null)
            module.repaintValues();
        }
    //---------------- TODO --------------- главная кнопка ---------------------------------------------
    public void testESSOnOffState() {
        }
    //---------------------------------------------------------------------------------------------
    }
