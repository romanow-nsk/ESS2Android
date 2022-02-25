package romanow.abc.ess2.android.rendering;

public class ScreenMode {
    public final static int PanelH=700;     // Базовая высота панели без полноэкранного режима
    public final boolean fullScreen;        // Режим полного экрана
    public final int ScreenW;               // Размер экрана по высоте (720)
    public final int ScreenH;               // Размер экрана по высоте (720)
    public final String mode;
    public ScreenMode(){
        mode="panel";
        fullScreen=false;
        ScreenW=960;
        ScreenH=720;
        }
    public ScreenMode(String mode0,int screenH,int screenW){
        mode=mode0;
        fullScreen=true;
        ScreenH=screenH;
        ScreenW=screenW;
        }
    public int y(int y){
        return !fullScreen ? y : y *ScreenH / PanelH;
        }
    public int x(int x){
        //return !fullScreen ? x : x *ScreenH / PanelH;
        return !fullScreen ? x : x * ScreenW * 3 / 4 / PanelH;
        }
}