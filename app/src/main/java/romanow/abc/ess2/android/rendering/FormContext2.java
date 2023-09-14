package romanow.abc.ess2.android.rendering;

import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPIESS2;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2GUIForm;
import romanow.abc.core.entity.metadata.Meta2GUIView;
import romanow.abc.core.entity.metadata.render.FormContextBase;
import romanow.abc.core.entity.metadata.render.I_ContextBack;
import romanow.abc.core.entity.subjectarea.AccessManager;
import romanow.abc.ess2.android.service.ESS2ArchitectureData;

public abstract class FormContext2 extends FormContextBase {
    private ESS2ArchitectureData main;
    public ESS2ArchitectureData getMain() {
        return main;
        }
    public void setMain(ESS2ArchitectureData main2) {
        this.main = main2;
        }
    public FormContext2(I_ContextBack back0, int idx1, int idx2, int idx3) {
        super(back0, idx1, idx2, idx3);
        }
    public FormContext2(I_ContextBack back0) {
        super(back0);
        }
    public abstract void openForm(String formName,boolean clearIdx);
    public abstract void openForm(Meta2GUIForm form,boolean clearIdx);
    private String menuFormStack[] = new String[Values.MenuStackSize];
    public String[] getMenuFormStack() {
        return menuFormStack; }
    public void setMenuFormStack(String[] menuFormStack) {
        this.menuFormStack = menuFormStack; }
    @Override
    public int x(int x){
        return super.x(x);
        }
    /*
    private ESS2ArchitectureData main;
    private Meta2GUIView view;              // Общие данные ЧМИ
    private String platformName="";
    private boolean valid=false;
    private ScreenMode screen = new ScreenMode();   // Данные экрана (панели)
    private boolean localUser = false;              // Индикатор локального пользователя
    private boolean superUser = false;              // Индикатор суперпользователя
    private Meta2GUIForm form = null;               // Текущая форма
    private Meta2GUIForm baseForm = null;           // Текущая форма (отображаемая)
    private AccessManager manager = null;           // Менеджер доступа с данными User
    private RestAPIBase service = null;             // Сервис webAPI текущего соединения
    private RestAPIESS2 service2 = null;            // Сервис webAPI ESS2 текущего соединения
    private String token="";                        // Токен сессии сервера данных
    private int idx[]=new int[Values.FormStackSize];// Стек начальных индексов групповых элементов GUI
    private int size[]=new int[Values.FormStackSize];// Стек размерностей групповых элементов GUI в форме
    private String menuFormStack[] = new String[Values.MenuStackSize];
    public void show(){
        System.out.print((form!=null ? form.getFormLevel() : "")+"->");
        for(int i=0;i<idx.length;i++)
            System.out.print(idx[i]+"["+size[i]+"] ");
        System.out.println();
    }
    public int getIndex(int level) {
        if (level<=0) return 0;
        return idx[level-1]; }
    public void setIndex(int level, int val) {
        idx[level-1] = val;
        for(int i=level;i<Values.FormStackSize;i++)
            idx[i]=0;
    }
    public String getName(int level) {
        if (level<=0) return "";
        return menuFormStack[level-1]; }
    public void setName(int level, String val) {
        menuFormStack[level-1] = val;
        for(int i=level;i<Values.MenuStackSize;i++)
            menuFormStack[i]="";
    }
    public int getSize(int level) {
        if (level<=0) return 0;
        return size[level-1]; }
    public void setSize(int level, int val) {
        size[level-1] = val;
        for(int i=level;i<Values.FormStackSize;i++)
            size[i]=0;
    }
    public FormContext2(int idx1, int idx2, int idx3) {
        for(int i=0;i<Values.FormStackSize;i++)
            idx[i]=size[i]=0;
        idx[0] = idx1;
        idx[1] = idx2;
        idx[2] = idx3; }
    public FormContext2() {
        for(int i=0;i<Values.FormStackSize;i++)
            idx[i]=size[i]=0;
    }
    public RestAPIBase getService() {
        return service; }
    public void setService(RestAPIBase service) {
        this.service = service; }
    public int x(int x){
        return screen.x(x); }
    public int y(int y){
        return screen.y(y);
        }
    public int dx(int dx){
        return screen.dx(dx); }
    public int dy(int dy){
        return screen.dy(dy);
        }
    public abstract void reOpenForm();
    public void openForm(String formName){
        openForm(formName,true);
        }
    public abstract void openForm(String formName,boolean clearIdx);
    public abstract void openForm(Meta2GUIForm form,boolean clearIdx);
    public boolean isActionEnable(){
        return manager.getCurrentAccessLevel() <= form.getWriteLevel();
        }
    public ESS2ArchitectureData getMain() {
        return main; }
    public void setMain(ESS2ArchitectureData main) {
        this.main = main; }
    public Meta2GUIView getView() {
        return view; }
    public void setView(Meta2GUIView view) {
        this.view = view; }
    public String getPlatformName() {
        return platformName; }
    public void setPlatformName(String platformName) {
        this.platformName = platformName; }
    public boolean isValid() {
        return valid; }
    public void setValid(boolean valid) {
        this.valid = valid; }
    public ScreenMode getScreen() {
        return screen; }
    public void setScreen(ScreenMode screen) {
        this.screen = screen; }
    public boolean isLocalUser() {
        return localUser; }
    public void setLocalUser(boolean localUser) {
        this.localUser = localUser; }
    public boolean isSuperUser() {
        return superUser; }
    public void setSuperUser(boolean superUser) {
        this.superUser = superUser; }
    public Meta2GUIForm getForm() {
        return form; }
    public void setForm(Meta2GUIForm form) {
        this.form = form; }
    public Meta2GUIForm getBaseForm() {
        return baseForm; }
    public void setBaseForm(Meta2GUIForm baseForm) {
        this.baseForm = baseForm; }
    public AccessManager getManager() {
        return manager; }
    public void setManager(AccessManager manager) {
        this.manager = manager; }
    public RestAPIESS2 getService2() {
        return service2; }
    public void setService2(RestAPIESS2 service2) {
        this.service2 = service2; }
    public String getToken() {
        return token; }
    public void setToken(String token) {
        this.token = token; }
    public int[] getIdx() {
        return idx; }
    public void setIdx(int[] idx) {
        this.idx = idx; }
    public int[] getSize() {
        return size; }
    public void setSize(int[] size) {
        this.size = size; }
    public String[] getMenuFormStack() {
        return menuFormStack; }
    public void setMenuFormStack(String[] menuFormStack) {
        this.menuFormStack = menuFormStack; }
     */
}
