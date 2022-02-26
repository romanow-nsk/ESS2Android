package romanow.abc.ess2.android.rendering;

import romanow.abc.ess2.android.service.ESS2Rendering;

public class ScreenMode {
    public final boolean pixelMode;
    public final int ScreenW;               // Размер экрана по высоте (720)
    public final int ScreenH;               // Размер экрана по высоте (720)
    public final String mode;
    public ScreenMode(){
        mode="pixelMode";
        pixelMode=true;
        ScreenW=0;
        ScreenH=0;
        }
    public ScreenMode(int screenH,int screenW){
        mode="dpMode";
        pixelMode=false;
        ScreenH=screenH;
        ScreenW=screenW;
        }
    public int y(int y){
        return pixelMode ? y : y * ScreenH / ESS2Rendering.FrameH;
        }
    public int x(int x){
        return pixelMode ? x : x * ScreenW / ESS2Rendering.FrameW;
        }
}