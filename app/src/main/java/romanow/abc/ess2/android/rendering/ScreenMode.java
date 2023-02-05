package romanow.abc.ess2.android.rendering;

import romanow.abc.ess2.android.service.ESS2Rendering;

public class ScreenMode {
    public final boolean pixelMode;
    public final int ScreenW;               // Размер экрана по высоте (720)
    public final int ScreenH;               // Размер экрана по высоте (720)
    public final String mode;
    public final double ScreenMas;
    public ScreenMode(){
        mode="pixelMode";
        pixelMode=true;
        ScreenW=0;
        ScreenH=0;
        ScreenMas=1;
        }
    public ScreenMode(int screenH,int screenW){
        this(screenH,screenW,1);
        }
    public ScreenMode(int screenH,int screenW, double mas){
        mode="dpMode";
        pixelMode=false;
        ScreenH=screenH;
        ScreenW=screenW;
        ScreenMas=mas;
        }
    public int y(int y){
        return pixelMode ? y : (int)(y * ScreenH / ESS2Rendering.FrameH * ScreenMas);
        }
    public int x(int x){
        return pixelMode ? x : (int)(x * ScreenW / ESS2Rendering.FrameW*ScreenMas);
        }
}