package romanow.abc.ess2.android;

import android.widget.LinearLayout;

import romanow.abc.ess2.android.service.AppData;

public class SettingsMenu extends SettingsMenuBase {
    public SettingsMenu(MainActivity base0){
        super(base0);
        }
    @Override
    public void settingsSave() {
        base.saveContext();
        }

    @Override
    public void createDialog(LinearLayout trmain){
        try {
            LEP500Settings set = AppData.ctx().set();
            LinearLayout layout = createItem("Частота мин.", String.format("%4.2f",set.FirstFreq), new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        ss = ss.trim().replace(",",".");
                        set.FirstFreq=Double.parseDouble(ss);
                        settingsChanged();
                        } catch (Exception ee){
                           base.popupInfo("Формат числа");}
                            }
                    });
            trmain.addView(layout);
            layout = createItem("Частота макс.", String.format("%4.2f",set.LastFreq), new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        ss = ss.trim().replace(",",".");
                        set.LastFreq=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                        }
                });
            trmain.addView(layout);
            layout = createItem("Блоков*1024", ""+set.p_BlockSize, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.p_BlockSize=Integer.parseInt(ss);
                        settingsChanged();
                        } catch (Exception ee){
                            base.popupInfo("Формат числа");}
                            }
                });
            trmain.addView(layout);
            layout = createItem("% перекрытия", ""+set.p_OverProc, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.p_OverProc=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Сглаживание", ""+set.kSmooth, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.kSmooth=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Измерение (сек)", ""+set.measureDuration, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        int vv = Integer.parseInt(ss);
                        if (vv<10 || vv>300){
                            base.popupInfo("Интервал в диапазоне 10...300");
                            return;
                            }
                        set.measureDuration=vv;
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                        }
            });
            trmain.addView(layout);
            layout = createItem("Тренд (волна)", ""+set.nTrendPoints, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.nTrendPoints=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                    }
                });
            trmain.addView(layout);
            layout = createItem("Тренд (спектр)", ""+set.nTrendPointsSpectrum, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.nTrendPointsSpectrum=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Частота изм.", ""+set.measureFreq, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.measureFreq=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createListBox("Окно БПФ", AppData.WinFuncList, set.winFun, new I_ListBoxListener() {
                @Override
                public void onSelect(int index) {
                    set.winFun = index;
                    settingsChanged();
                    }
                @Override
                public void onLongSelect(int index) {}
                @Override
                public void onCancel() {}
                });
            trmain.addView(layout);
            layout = createItem("Группа", set.measureGroup, true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.measureGroup=ss;
                    settingsChanged();
                    }});
            trmain.addView(layout);
            layout = createItem("Опора", set.measureTitle, true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.measureTitle=ss;
                    settingsChanged();
                }});
            trmain.addView(layout);
            layout = createItem("№ замера", ""+set.measureCounter, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.measureCounter=Integer.parseInt(ss);
                        settingsChanged();
                        } catch (Exception ee){
                            base.popupInfo("Формат числа");}
                        }
                });
            trmain.addView(layout);
            layout = createItem("Mail ", ""+set.mailToSend, true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.mailToSend=ss;
                    settingsChanged();
                    }
                });
            trmain.addView(layout);
            layout = createItem("Отладка", set.fullInfo ? "1" : "0" , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.fullInfo=Integer.parseInt(ss)!=0;
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                        }
                });
            trmain.addView(layout);
            layout = createItem("Режим техника", set.technicianMode ? "1" : "0" , new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.technicianMode=Integer.parseInt(ss)!=0;
                        settingsChanged();
                        base.overLoad(false);
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                }
            });
            trmain.addView(layout);
            layout = createItem("Уровень ампл.(%)", ""+set.amplLevelProc, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.amplLevelProc=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                        }
                });
            trmain.addView(layout);
            layout = createItem("Уровень мощн.(%)", ""+set.powerLevelProc, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.powerLevelProc=Integer.parseInt(ss);
                        settingsChanged();
                    } catch (Exception ee){
                        base.popupInfo("Формат числа");}
                    }
            });
            trmain.addView(layout);
            //---------------------------------------------------------------------------------------
            layout = createItem("K1", ""+set.K1, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K1=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
                });
            trmain.addView(layout);
            layout = createItem("K2", ""+set.K2, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K2=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
            layout = createItem("K3", ""+set.K3, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K3=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
            layout = createItem("K4", ""+set.K4, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K4=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
            layout = createItem("K5", ""+set.K5, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K5=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
            layout = createItem("K6", ""+set.K6, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K6=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
            layout = createItem("K7", ""+set.K7, new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.K7=Double.parseDouble(ss);
                        settingsChanged();
                    } catch (Exception ee){ base.popupInfo("Формат числа");} }
            });
            trmain.addView(layout);
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
}

