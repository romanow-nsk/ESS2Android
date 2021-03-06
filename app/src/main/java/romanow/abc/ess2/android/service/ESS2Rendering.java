package romanow.abc.ess2.android.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;

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
import romanow.abc.ess2.android.rendering.I_Module;
import romanow.abc.ess2.android.rendering.Module;
import romanow.abc.ess2.android.rendering.ScreenMode;
import romanow.abc.ess2.android.rendering.View2Base;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;
import romanow.abc.ess2.android.rendering.view2.I_Button;

public class ESS2Rendering {
    public final static int FrameH=530;
    public final static int FrameW=400;
    private ESS2ArchitectureData main2;
    private I_Button logoutCallBack = null;                     // CallBack ???????????? ????????????
    private OwnDateTime userLoginTime = new OwnDateTime();
    private ErrorList errorList = new ErrorList();
    private int essOnOffState= Values.ESSStateNone;             // ?????????????????? ??????
    private final static String mainFormName="??????????????";
    private Module module=null;
    private ArrayList<View2Base> guiList = new ArrayList<>();
    private RelativeLayout formPanel=null;
    private ConstraintLayout formView=null;
    //------------------------------------------------------------------------------------------------------------------
    public void openFormDialog(){
        if (formView!=null){
            main2.main().getLogLayout().removeView(formView);
            formView = null;
            formPanel = null;
            }
        formView =(ConstraintLayout)main2.main().getLayoutInflater().inflate(R.layout.form_frame, null);
        formPanel = (RelativeLayout) formView.findViewById(R.id.form_frame_panel);
        formPanel.setPadding(5, 5, 5, 5);
        formPanel.setBackgroundColor(main2.currentView.getView().getBackColor()|0xFF000000);
        LinearLayout layout = main2.main().getLogLayout();
        layout.addView(formView);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)formPanel.getLayoutParams();
        context.setScreen(new ScreenMode(params.height,params.height*3/4));      // pdMode ??????????????????
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
                popup("???? ?????????????? ?????????? "+formName);
                return;
            }
            openForm(form,clearIdx);
        }
        @Override
        public void openForm(Meta2GUIForm form, boolean clearIdx) {
            //---------------- ?????????? ?????? ?????????????????? ???????????? -------------------------------------------------
            setBaseForm(form);
            if (form.isLinkForm()){
                String baseFormName = form.getShortName();
                Meta2GUIForm baseForm = main2.currentView.getView().getForms().getByTitle(baseFormName);
                if (baseForm==null){
                    popup("???? ?????????????? ?????????????? ?????????? "+baseFormName);
                    return;
                }
                int idx = form.getBaseFormIndex();
                int level = form.getFormLevel();
                if (level!=0 && idx!=-1)            // ???????????? ?????? ?????????????????????? ???????????? ???? ?????????????????? ???????????? ????????
                    setIndex(level,idx);
                setBaseForm(baseForm);
                }
            //---------------------------------------------------------------------------------------------
            if (main2.manager.getCurrentAccessLevel() > form.getAccessLevel()){
                popup("???????????????????????? ?????????????? ??????????????");
                return;
            }
            Meta2GUIForm prev = getForm();
            if (prev!=null && form.getLevel()>prev.getLevel()+1){
                popup("???????????????? ?????????????? ?????????? ?????? ???????????????? ???? "+prev.getTitle()+" ?? "+form.getTitle());
                }
            setForm(form);
            int level = form.getLevel();
            if (level!=0 && clearIdx){
                setIndex(level,0);
                setName(level,getForm().getTitle());
                setSize(level,getBaseForm().getElementsCount());
                };
            int vv[] = getIdx();
            System.out.println("???????? ????????????????: "+vv[0]+" "+vv[1]+" "+vv[2]+" "+vv[3]);
            String ss[] = getMenuFormStack();
            System.out.println("???????? ????????: "+ss[0]+" "+ss[1]+" "+ss[2]+" "+ss[3]);
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
        main2.formMenuButton().setText(mainFormName);
        setMainForm();
        createLoopThread();
        repaintView();
        }
    public void renderOff(){            // ???????????????????? ???? ESS2ArchitectureData
        setRenderingOnOff(false);
        context.setForm(null);
        main2.main().getLogLayout().removeView(formView);
        main2.main().scrollDown();
        }
    //------------------------------ ???????? ???????????????????? ------------------------------------------------------
    private Thread guiLoop = null;
    private boolean shutDown=false;
    private int renderSeqNum=0;                                         // ???????????????????????????????? ?????????? ????????????????????
    private boolean renderingOn=false;
    private void setRenderingOnOff(boolean vv){
        renderingOn = vv;
        }
    //private boolean repaintValuesOn=false;                           // ???????????????????? ????????????
    //private boolean repaintBusy=false;                               // ???????????????????? ??????????
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
                            System.out.println("??????????????????: " + (new OwnDateTime().timeInMS()-tt));
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
        names.add("??????????");
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
        if (context.getForm().isEmpty()){         // ???????????????????? ???????????????????? ???????????? ????????????
            context.setForm(formList.getByTitle(context.getForm().getChilds().get(0).getTitle()));
            repaintView();
            }
        for(ESS2Device device : main2.deployed.getDevices()){          // ???????????????? ????????
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
                    popup("???????????????????????? ?????????????? ??????????????");
                    return;
                }
                new OK(200, 200, essOnOffState == Values.ESSStateOff ? "???????????????? ??????" : "?????????????????? ??????", new I_Button() {
                    @Override
                    public void onPush() {
                        try {
                            //---------------- TODO --- ?????????????? ???????????? ------------------------------------------
                            main2.plm.writeRegister("",0,Values.LocalCommandRegister, essOnOffState==Values.ESSStateOff ?
                                    Values.ESSOnCommand : Values.ESSOffCommand);
                            testESSOnOffState();
                        }catch (UniException ee){
                            popup("???? ?????????????????????? ?????????????? ?????? "+Values.ESSStateRegister+"\n"+ee.toString());
                        }
                    }
                });
            }
        });
        add(OnOff);
        OnOff.setBounds(context.x(5), context.y(10), context.x(50), context.y(50));
        testESSOnOffState();
        //------------------ TODO ------------------ ?? ?????????????? ?????????? ---------------------------------
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
        //------------------- TODO --------------- ?????????? ???? ???????????? ---------------------------------
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
        main2.formMenuButton().setText(baseForm.getTitle());
        //----------------------------------- ?????????????????? ?????????????????? ????????????????????
        guiList.clear();
        int idx[] = new int[Values.FormStackSize];
        renderGuiElement(baseForm.getControls(),0,0,0,idx);  // ?????????????????????? ?????????????????? ?????? ???????? ??????????????
        for(View2Base view :  guiList)
            ((View2BaseDesktop)view).addToPanel(formPanel);                      // ???????????????? ???? ????????????
        //----------------------------------------------------------------------------------
        module=null;
        if (!context.getForm().noModule()){
            Pair<String,Object> res = context.getForm().createModule();
            if (res.o1!=null){
                popup(res.o1);
            }
            else{
                if (!(res.o2 instanceof I_Module)){
                    popup(context.getForm().getModuleName() +" ???? ???????????? ??????");
                }
                else{
                    module = (Module)res.o2;
                    module.init(main2,formPanel,ctx.getService(),ctx.getService2(),ctx.loginSettings().getSessionToken(), context.getForm(),context);
                }
            }
        }
        //------------------------------------------------------------------------------------
        //repaintBusy=false;
        if (!errorList.valid()){
            main2.main().errorMes(""+errorList.getErrCount()+ " ???????????? ????????????????????");
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
            popup("???? ?????????????? ?????????????? ??????????");
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
    //----------------------- ?????????????????? ?????????????????? ----------------------------------------
    public void renderGuiElement(Meta2GUI meta, int baseX, int baseY, int groupLevel, int groupIndexes[]){
        if (meta instanceof Meta2GUIArray){
            Meta2GUIArray array = (Meta2GUIArray) meta;
            Meta2GUI elem = array.getElem();
            for(int i=0;i<array.getSize();i++){
                int cIdx = i;
                groupIndexes[groupLevel] = cIdx;        // ?????????????????????????? ?????????????? ?? ???????????? (?????????????? 1 - level=0)
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
            //--------------- TODO ------------ ?????????? ???? ???????? --------------------------------------------
            //int formLevel = context.getForm().getLevel();
            //if (groupLevel > formLevel){
            //    errorList.addError("?????????????? ???????????????? "+meta.getFullTitle()+"="+groupLevel+" ???????????? ???????????? ?????????? "+context.getForm().getFullTitle()+"="+formLevel);
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
            if (meta instanceof Meta2GUIReg) {          // ?????? ?????????????????? - ?????????????? ???????????????? ???? ?????????????? View
                Meta2GUIReg regGUI = (Meta2GUIReg)meta;
                Meta2RegLink link = regGUI.getRegLink();
                Meta2Register register = link.getRegister();
                String equipName= regGUI.getEquipName();
                ESS2Equipment equipment = main2.deployed.getEquipments().getByName(equipName);
                if (equipment==null){
                    errorList.addError("???? ?????????????? ???????????????????????? "+equipName+" ?????? "+regGUI.getFullTitle());
                    return;
                }
                int connectorsSize = equipment.getLogUnits().size();
                if (connectorsSize==0){
                    errorList.addError("?????? ?????????????????? ?????? "+equipName);
                    return;
                }
                //------------- ?????????????? ???????????????? ??????????????????, ???????????????? ???????????????????????? ?? Unit  ---------------------
                int treeLevel = register.getArrayLevel()-1;         // ??????-???? ???????????????? ?? ???????????? Meta-?????????????????? (+device+units) -1
                int grlevel = groupLevel-1;                         // ??????-???? ???????????????? ?? ??????????
                int stacklevel = context.getForm().getLevel()-1;    // ?????????????? ?????????? ???????????????? ???????? ?????? ??????. ????????????
                if (treeLevel > stacklevel){
                    errorList.addError("?????????????? ?????????????? ????????-???????????? > ???????????? ?????????? "+
                            equipName+" ?????? "+regGUI.getFullTitle()+"="+(treeLevel+1)+" "+
                            context.getForm().getTitle()+"="+(stacklevel+1));
                    return;
                }
                int regOffset=0;
                stacklevel = treeLevel;
                //-------------------------------------------------------------------------------
                //System.out.println(register.getFullTitle());
                //for(int i=0;i<groupIndexes.length;i++)
                //    System.out.print(groupIndexes[i]+" ");
                //System.out.println("->"+groupLevel);
                //context.show();
                //--------------------------------------------------------------------------------
                for(Meta2Entity cc = register.getHigh(); cc!=null; cc = cc.getHigh()) {
                    if (!(cc instanceof Meta2Array))
                        continue;
                    Meta2Array array = (Meta2Array) cc;
                    int elemIdx = context.getIndex(stacklevel+1);
                    elemIdx += grlevel<0 ? 0 : groupIndexes[grlevel];
                    if (elemIdx >=array.getSize())          // ?????????? ???? ?????????????? ??????????????
                        return;
                    switch(array.getArrayType()){
                        case Values.ArrayTypeModbus:
                            regOffset += array.getStep()*elemIdx;
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
                if (newElem.getUnitIdx() >= connectorsSize){
                    errorList.addError("???????????? Unit "+newElem.getUnitIdx()+" ????????????????  "+equipName+" ?????? "+regGUI.getFullTitle());
                    return;
                }
                newElem.setDevice(equipment.getLogUnits().get(newElem.getUnitIdx()).getDevice().getRef());
                newElem.setDevUnit(equipment.getLogUnits().get(newElem.getUnitIdx()).getUnit());        // ???????????????????? Unit
            }
            guiList.add(newElem);
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------
    public synchronized void repaintValues(){
        //if (repaintBusy)
        //    return;
        //-------------- ?????????? ?????????? ??????????????????????????
        //if (context.getForm()!=prevForm){
        //    prevForm = context.getForm();
        //    return;
        //    }
        testESSOnOffState();
        if (module!=null)
            module.repaintValues();
        HashMap<Integer,Integer> map = new HashMap<>();
        for (View2Base element : guiList){
            element.repaintBefore();
        }
        for (View2Base element : guiList){
            Meta2RegLink link = element.getRegLink();
            if (link==null)
                continue;
            ESS2Device device = element.getDevice();
            int regNumFull = link.getRegNum()+element.getRegOffset();                   // ?????????????? ????????????????
            device.putValue(element.getDevUnit(),regNumFull,0);                   // ?????????????? ???? ??????????????????
            if (link.getRegister().doubleSize())
                device.putValue(element.getDevUnit(),regNumFull+1,0);     // ?????????????? ???? ??????????????????
            Meta2RegLink vv[] = element.getSettingsLinks();
            //---------- ?????????????????????????????? ???????????????? ?? ???????? ???? ?????????????? ?? ??????????, ?????? ?? ????????????????, ?????? ????????????????
            for(Meta2RegLink link2 : vv){
                regNumFull = link2.getRegNum();
                device.putValue(element.getDevUnit(),regNumFull,0);               // ?????????????? ?????? ????????????????
                if (link2.getRegister().doubleSize())
                    device.putValue(element.getDevUnit(),regNumFull+1,0);                        // ?????????????? ?????? ????????????????
            }
            vv = element.getDataLinks();
            for(Meta2RegLink link2 : vv){
                regNumFull = link2.getRegNum();
                device.putValue(element.getDevUnit(),regNumFull,0);               // ?????????????? ?????? ????????????????
                if (link2.getRegister().doubleSize())
                    device.putValue(element.getDevUnit(),regNumFull+1,0);                        // ?????????????? ?????? ????????????????
            }
        }
        renderSeqNum++;             // ???????????????????? ????????. ?????????? ??????????????
        for(ESS2Device device : main2.deployed.getDevices()){
            ArrayList<UnitRegisterList> list2 = device.createList(false);
            for(UnitRegisterList list : list2){
                System.out.println(device.getShortName()+"["+list.getUnitIdx()+"]="+list.size());
                readPLMRegistersAsync(device,list,context.getForm());                    // ?????????????????????? ????????????
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
                                if (renderSeqNum!=currentRenderSeqNum){      // ???????????????????? ????????.???????????? ??????????????
                                    System.out.println("???????????????????????? ?????????????? ?????????????? (??????????????????????) "+renderSeqNum+" "+currentRenderSeqNum);
                                    return;
                                    }
                                }
                            try {
                                repaintValuesOnAnswer(device, list.getUnitIdx(), values,currentForm);
                                System.out.println("??????????: " + (new OwnDateTime().timeInMS()-tt));
                                } catch (UniException e) {
                                    AppData.ctx().popupAndLog(true,"???????????? GUI: "+e.toString());
                                    }
                                }
                        });
                    } catch (final UniException ee){
                        main2.main().guiCall(new Runnable() {
                            @Override
                            public void run() {
                                AppData.ctx().popupAndLog(true,"???????????? ??????????????: "+ee.toString());
                                main2.clearDeployedMetaData();
                                }
                            });
                        }
                    }
                }).start();
            }
        //-----------------------------------------------------------------------------------------------------------------------
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
                if (!element.getDevice().getShortName().equals(device.getShortName()))        // ???????????????????? ???? ???????????? ????????
                    continue;
                if (element.getDevUnit()!=unitIdx)      // ???????????????????? ?????????? ???????????????????? Unit
                    continue;
                int regNumFull = link.getRegNum()+element.getRegOffset();
                Integer vv = device.getValue(unitIdx,regNumFull);
                if (vv==null){
                    popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull);
                    continue;
                }
                int sum = vv.intValue() & 0x0FFFF;
                if (link.getRegister().doubleSize()){       // ?????????????????? ???????????????? (???????????????????? ????????????)
                    vv = device.getValue(unitIdx,regNumFull+1);
                    if (vv==null){
                        popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull+1);
                        continue;
                    }
                    sum |= vv.intValue() << 16;
                }
                element.putValue(sum);
                Meta2RegLink links[] = element.getSettingsLinks();
                for(int i=0;i<links.length;i++){
                    regNumFull = links[i].getRegNum();
                    vv = device.getValue(unitIdx,regNumFull);
                    if (vv==null){
                        popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull);
                        continue;
                    }
                    sum = vv.intValue() & 0x0FFFF;
                    if (links[i].getRegister().doubleSize()){       // ?????????????????? ???????????????? (???????????????????? ????????????)
                        vv = device.getValue(unitIdx,regNumFull+1);
                        if (vv==null){
                            popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull+1);
                            continue;
                        }
                        sum |= vv.intValue() << 16;
                    }
                    element.putValue(links[i].getRegister(),sum,i);
                }
                links = element.getDataLinks();
                for(int i=0;i<links.length;i++){
                    regNumFull = links[i].getRegNum();
                    vv = device.getValue(unitIdx,regNumFull);
                    if (vv==null){
                        popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull);
                        continue;
                    }
                    sum = vv.intValue() & 0x0FFFF;
                    if (links[i].getRegister().doubleSize()){       // ?????????????????? ???????????????? (???????????????????? ????????????)
                        vv = device.getValue(unitIdx,regNumFull+1);
                        if (vv==null){
                            popup("???? ???????????? ?????????????? ?? ???????????? ?????????????? "+regNumFull+1);
                            continue;
                        }
                        sum |= vv.intValue() << 16;
                    }
                    element.putValue(links[i].getRegister(),sum,i);
                }
                element.repaintValues();
            }
            if (module!=null)
                module.repaintValues();
        }
    //---------------- TODO --------------- ?????????????? ???????????? ---------------------------------------------
    public void testESSOnOffState() {
        }
    //---------------------------------------------------------------------------------------------
    }
