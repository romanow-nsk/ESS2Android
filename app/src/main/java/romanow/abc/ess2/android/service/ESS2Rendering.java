package romanow.abc.ess2.android.service;

import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.subjectarea.WorkSettings;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.view2.I_Button;

public class ESS2Rendering {
    private ESS2ArchitectureData main2;
    private I_Button logoutCallBack = null;                         // CallBack кнопки выхода
    private OwnDateTime userLoginTime = new OwnDateTime();
    public ESS2Rendering(ESS2ArchitectureData data0){
        main2 = data0;
        }
    //------------------------------------------------------------------------------------------------------------------
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
        context.setView(main2.currentView.getView());
        repaintView();
        }
    public void repaintOff(){
        setRenderingOnOff(false);
        //removeAll();
        context.setForm(null);
        //repaint();
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
                        repaintOff();
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
    public void repaintView(){

        }
    //------------------------------------------------------------------------------------------------------------------------
    public void repaintValues(){

    }
}
