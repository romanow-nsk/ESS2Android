package romanow.abc.ess2.android.lep500;

import static me.romanow.lep500.Registration.toBase64;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.jjoe64.graphview.LineGraphView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPILEP500;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.lep500.ble.BTDescriptor;
import romanow.abc.ess2.android.lep500.ble.BTReceiver;
import romanow.abc.ess2.android.lep500.ble.BTTextFile;
import romanow.abc.ess2.android.lep500.ble.BTViewFace;
import romanow.abc.ess2.android.lep500.ble.SensorGroupListener;
import romanow.abc.ess2.android.lep500.menu.MIAbout;
import romanow.abc.ess2.android.lep500.menu.MIArchive;
import romanow.abc.ess2.android.lep500.menu.MIArchiveFull;
import romanow.abc.ess2.android.lep500.menu.MIAudioRecord;
import romanow.abc.ess2.android.lep500.menu.MIConvertToWave;
import romanow.abc.ess2.android.lep500.menu.MIDeleteFromArchive;
import romanow.abc.ess2.android.lep500.menu.MIDownLoad;
import romanow.abc.ess2.android.lep500.menu.MIExport;
import romanow.abc.ess2.android.lep500.menu.MIExportAndSendMail;
import romanow.abc.ess2.android.lep500.menu.MIFileCopy;
import romanow.abc.ess2.android.lep500.menu.MIFileProcess;
import romanow.abc.ess2.android.lep500.menu.MIFullScreen;
import romanow.abc.ess2.android.lep500.menu.MIGroupCreate;
import romanow.abc.ess2.android.lep500.menu.MIGroupDestroy;
import romanow.abc.ess2.android.lep500.menu.MIMap;
import romanow.abc.ess2.android.lep500.menu.MIResultsPlayer;
import romanow.abc.ess2.android.lep500.menu.MISendMail;
import romanow.abc.ess2.android.lep500.menu.MITestCase;
import romanow.abc.ess2.android.lep500.menu.MITestSignal;
import romanow.abc.ess2.android.lep500.menu.MIUpLoad;
import romanow.abc.ess2.android.lep500.menu.MIViewWave;
import romanow.abc.ess2.android.lep500.menu.MIWavePlayer;
import romanow.abc.ess2.android.lep500.menu.MenuItemAction;
import romanow.abc.ess2.android.lep500.service.AppData;
import romanow.abc.ess2.android.lep500.service.BaseActivity;
import romanow.abc.ess2.android.lep500.service.GPSService;
import romanow.abc.ess2.android.lep500.service.NetBack;
import romanow.abc.ess2.android.lep500.service.NetCall;
import romanow.lep500.FFTAudioTextFile;
import romanow.lep500.FileDescription;
import romanow.lep500.FileDescriptionList;
import romanow.lep500.I_EventListener;
import romanow.lep500.fft.Extreme;
import romanow.lep500.fft.ExtremeFacade;
import romanow.lep500.fft.ExtremeList;
import romanow.lep500.fft.FFT;
import romanow.lep500.fft.FFTStatistic;


public class MainActivity extends BaseActivity {     //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public BTViewFace btViewFace = new BTViewFace(this);
    public MailSender mail = new MailSender(this);
    private GPSService gpsService;
    public GPSService getGpsService(){ return gpsService; }
    private Handler event = new Handler();
    public volatile boolean shutDown = false;
    public boolean voiceRun = false;
    private AppData ctx;
    //-------------- Постоянные параметры snn-core ---------------------------------------
    private final boolean p_Compress = false;        // Нет компрессии
    private final int compressLevel = 0;
    private final float kMultiple = 3.0f;
    private final float kAmpl = 1f;
    private final int MiddleMode = 0x01;
    private final int DispMode = 0x02;
    private final int MiddleColor = 0x0000FF00;
    private final int DispColor = 0x000000FF;
    private final int GraphBackColor = 0x00A0C0C0;
    final public static String archiveFile = "LEP500Archive.json";
    final public static double ViewProcHigh = 0.6;
    final public static String VoiceFile = "LEP500.wave";
    //------------------------------------------------------------------------------------
    public static final int nFirstMax = 10;  // Количество максимумов в статистике (вывод)
    public boolean hideFFTOutput = false;
    private int waveMas = 1;
    private double waveStartTime = 0;
    //----------------------------------------------------------------------------
    private LinearLayout log;
    private ScrollView scroll;
    private final int CHOOSE_RESULT = 100;
    private final int CHOOSE_RESULT_COPY = 101;
    public final int REQUEST_ENABLE_BT = 102;
    public final int REQUEST_ENABLE_GPS = 103;
    public final int REQUEST_ENABLE_READ = 104;
    public final int REQUEST_ENABLE_WRITE = 105;
    public final int REQUEST_ENABLE_PHONE = 106;
    public final int REQUEST_ENABLE_AUDIO = 107;
    public final int REQUEST_BLUETOOTH_CONNECT = 108;
    public final String BT_OWN_NAME = "LEP500";
    public final String BT_SENSOR_NAME_PREFIX = "VIBR_SENS";
    public final int BT_DISCOVERY_TIME_IN_SEC = 300;
    public final int BT_SCANNING_TIME_IN_SEC = 60;
    private ImageView MenuButton;
    private ImageView GPSState;
    private ImageView NETState;
    //--------------------------------------------------------------------------
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GPSPoint gps = new GPSPoint();
            int state = intent.getIntExtra("state",GPSPoint.GeoNone);
            gps.setCoord(intent.getDoubleExtra("geoY",0),
                    intent.getDoubleExtra("geoX",0),false);
            gps.state(state);
            if (state == GPSPoint.GeoNone)
                GPSState.setImageResource(R.drawable.gps_off);
            if (state == GPSPoint.GeoNet)
                GPSState.setImageResource(R.drawable.gsm);
            if (state == GPSPoint.GeoGPS)
                GPSState.setImageResource(R.drawable.gps);
            }
        };
    private I_EventListener logEvent = new I_EventListener() {
        @Override
        public void onEvent(String ss) {
            addToLog(ss);
        }
    };

    public void addMenuList(MenuItemAction action) {
        menuList.add(action);
    }

    //------------------------------------------------------------------------------------------------------
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NETState.setImageResource(AppData.CNetRes[AppData.ctx().cState()]);
            }
        };
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_CState);
        this.registerReceiver(receiver, filter);
        filter = new IntentFilter(AppData.Event_GPS);
        this.registerReceiver(gpsReceiver, filter);
        }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        unregisterReceiver(gpsReceiver);
        }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ENABLE_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else
                errorMes("Включите разрешение геолокации");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с памятью");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с памятью");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с микрофоном");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else
                errorMes("Включите разрешение работы с телефоном");
            if (testPermission()) onAllPermissionsEnabled();
        }
        //--------------- Не включается -----------------------------------------------------------
        //if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
        //    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //    } else
        //        errorMes("Включите разрешение работы с BlueTooth");
        //    if (testPermission()) onAllPermissionsEnabled();
        //}
    }
    //----------------------------------------------------------------------------------------------
    private boolean testPermission(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(blueToothReceiver, filter);// Не забудьте снять регистрацию в onDestroy
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ENABLE_READ);
            return false;
            }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ENABLE_WRITE);
            return false;
            }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_GPS);
            return false;
            }
        //if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        //        && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_ENABLE_PHONE);
        //    return false;
        //    }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_ENABLE_AUDIO);
            return false;
            }
        //----------------------------------------------------------------------------------------------------------
        //if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        //        && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
        //    return false;
        //    }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Values.init();                  // Статические данные
        ctx = AppData.ctx();
        try {
            ctx.setContext(getApplicationContext());
            new FFT();                          // статические данные
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main);
            MenuButton = (ImageView) findViewById(R.id.headerMenu);
            GPSState = (ImageView) findViewById(R.id.headerGPS);
            log = (LinearLayout) findViewById(R.id.log);
            scroll = (ScrollView) findViewById(R.id.scroll);
            NETState = (ImageView) findViewById(R.id.headerNet);
            if (testPermission()){
                onAllPermissionsEnabled();
                }
            } catch (Exception ee) {
                errorMes(createFatalMessage(ee, 10));
                }
        }
    private void onAllPermissionsEnabled(){
        try{
            btViewFace.init();
            ctx.fileService().loadContext();
            ctx.cState(AppData.CStateGray);
            createMenuList();
            gpsService = new GPSService(this);
            gpsService.startService();
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
                }
            if (!isLocationEnabled()) {
                errorMes(EmoSet, " Включить \"Местоположение\" в настройках");
                popupToast(R.drawable.problem, " Включить \"Местоположение\" в настройках");
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            else
                gpsService.startService();
            MenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createMenuList();
                    menuDialog = new ListBoxDialog(MainActivity.this, createMenuTitles(), "Меню", new I_ListBoxListener() {
                        @Override
                        public void onSelect(int index) {
                            procMenuItem(index);
                            menuDialog = null;
                            }
                        @Override
                        public void onLongSelect(int index) {
                            menuDialog = null;
                            }
                        @Override
                        public void onCancel() {
                            menuDialog = null;
                            }
                        });
                    menuDialog.create();
                }
            });
            if (isAllEnabled())
                btViewFace.init();
            //------------------------------------------------
            //int[] surrogates = {0xD83D, 0xDC7D};
            //String title = "Звенящие опоры России "+
            //        new String(Character.toChars(0x1F349))+
            //        new String(surrogates, 0, surrogates.length)+
            //        "\uD83D\uDC7D";
            String fatalMessage = ctx.set().fatalMessage;
            if (fatalMessage.length()!=0){
                addToLog(false,fatalMessage,14,0x00A00000);
                ctx.set().fatalMessage="";
                saveContext();
                }
            String title = "Звенящие опоры России ";
            addToLog(false, title, 22, 0x00007020);
            //addToLogButton("Рег.код: "+createRegistrationCode(),true,null,null);
            //addToLogButton("ID: "+getSoftwareId64(),true,null,null);
            if (!createRegistrationCode().equals(ctx.loginSettings().getRegistrationCode())) {
                addToLog(false,"Приложение не зарегистрировано\nПолучить регистрационный код для",
                        18,0x00A00000);
                addToLogButton("ID: " + getSoftwareId64(),true,null,null);
                }
            else{
                addToLog(false,"Приложение зарегистрировано\nПолная функциональность",
                        18,0x00007020);
                }
            } catch (Exception ee) {
                errorMes(createFatalMessage(ee, 10));
                }
            //addToLogImage(R.drawable.status_green);
            //---------- проверка перехвата исключений по умолчанию
            //Object oo=null;
            //oo.toString();
        }

    public void clearLog() {
        log.removeAllViews();
        }

    public void popupAndLog(String ss) {
        addToLog(ss);
        popupInfo(ss);
        }

    public void popupAndLog(String ss,int textSize, int color) {
        addToLog(false,ss,textSize,color);
        popupInfo(ss);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutDown = true;
        unregisterReceiver(blueToothReceiver);
        btViewFace.blueToothOff();
        gpsService.stopService();
        saveContext();
        AppData.ctx().stopApplication();
    }

    public void scrollDown() {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void saveBTFile(BTReceiver sensor, FFTAudioTextFile file) {
        file.save(ctx.androidFileDirectory(), logEvent);
        addToLog(btViewFace.getSensorName(sensor) + " записано: " + file.createOriginalFileName() + " [" + file.getData().length + "]");
        ctx.set().measureCounter++;           // Номер следующего измерения
        ctx.fileService().saveContext();
    }

    /*
    public void calcFirstLastPoints(FileDescription file) {
        int width = set.p_BlockSize * FFT.Size0;
        double df = file.getFileFreq() / width;
        noFirstPoints = (int) (set.FirstFreq / df);
        noLastPoints = (int) ((file.getFileFreq()/2 - set.LastFreq) / df);
        }
    */
    public void addToLog(String ss) {
        addToLog(false, ss, 0);
    }

    public void addToLog(boolean fullInfoMes, String ss) {
        addToLog(fullInfoMes, ss, 0);
    }

    @Override
    public void addToLog(String ss, int textSize) {
        addToLog(false, ss, textSize);
    }

    @Override
    public void addToLogHide(String ss) {
        if (!hideFFTOutput)
            addToLog(ss);
    }

    @Override
    public void showStatisticFull(FFTStatistic inputStat, int idx) {
        if (isFullInfo())
            showStatistic(inputStat, idx);
        else
            showShort(inputStat, idx);
    }

    public void addToLog(boolean fullInfoMes, final String ss, final int textSize) {
        addToLog(fullInfoMes, ss, textSize, 0);
    }

    public void addToLog(boolean fullInfoMes, final String ss, final int textSize, final int textColor) {
        addToLog(fullInfoMes, ss, textSize, textColor, -1);
    }

    public void addToLog(boolean fullInfoMes, final String ss, final int textSize, final int textColor, final int imgRes) {
        if (fullInfoMes && !ctx.set().fullInfo)
            return;
        guiCall(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.log, null);
                if (imgRes != -1) {
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(imgRes);
                    layout.addView(imageView);
                }
                TextView txt = new TextView(MainActivity.this);
                txt.setText(ss);
                txt.setTextColor(textColor | 0xFF000000);
                if (textSize != 0)
                    txt.setTextSize(textSize);
                layout.addView(txt);
                log.addView(layout);
                scrollDown();
            }
        });
    }

    public void addToLog(final String ss, final int textSize, final View.OnClickListener listener) {
        guiCall(new Runnable() {
            @Override
            public void run() {
                Button tt = new Button(MainActivity.this);
                tt.setText(ss);
                tt.setPadding(5, 5, 5, 5);
                tt.setBackgroundResource(R.drawable.button_background);
                tt.setTextColor(0xFFFFFFFF);
                tt.setOnClickListener(listener);
                tt.setTextSize(textSize);
                log.addView(tt);
                scrollDown();
            }
        });
    }

    public LinearLayout addToLogButton(String ss) {
        return addToLogButton(ss, false,null, null);
    }

    public LinearLayout addToLogButton(String ss, View.OnClickListener listener) {
        return addToLogButton(ss, false,listener, null);
        }

    public LinearLayout addToLogButton(String ss, boolean jetBrain,View.OnClickListener listener, View.OnLongClickListener listenerLong) {
        LinearLayout button = (LinearLayout) getLayoutInflater().inflate(R.layout.log_item, null);
        Button bb = (Button) button.findViewById(R.id.ok_button);
        bb.setText(ss);
        bb.setTextSize(greatTextSize);
        if (jetBrain)
            bb.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.jetbrainsmonolight));
        if (listener != null)
            bb.setOnClickListener(listener);
        if (listenerLong != null)
            bb.setOnLongClickListener(listenerLong);
        log.addView(button);
        scrollDown();
        return button;
        }

    private void preloadFromText(int resultCode) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/plain");
        intent = Intent.createChooser(chooseFile, "Выбрать txt");
        startActivityForResult(intent, resultCode);
        }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Pair<InputStream, FileDescription> openSelected(Intent data) throws FileNotFoundException {
        Uri uri = data.getData();
        String ss = getFileName(uri);
        /*
        String ss = uri.getEncodedPath();
        try {
            ss = URLDecoder.decode( ss, "UTF-8" );
            } catch (UnsupportedEncodingException e) {
                addToLog("Системная ошибка в имени файла:"+e.toString());
                addToLog(ss);
                return new Pair<>(null,null);
                }
        String ss0 = ss;
        int idx= ss.lastIndexOf("/");
        if (idx!=-1) ss = ss.substring(idx+1);
         */
        FileDescription description = new FileDescription(ss);
        String out = description.getFormatError();
        if (out.length() != 0) {
            addToLog(out);
            return new Pair(null, null);
        }
        addToLog(description.validDescription(), isFullInfo() ? 0 : greatTextSize);
        InputStream is = getContentResolver().openInputStream(uri);
        return new Pair(is, description);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String path = "";
        try {
            if (requestCode == REQUEST_ENABLE_BT) {
                popupAndLog("BlueTooth включен, повторите команду");
            }
            if (requestCode == CHOOSE_RESULT) {
                Pair<InputStream, FileDescription> res = openSelected(data);
                InputStream is = res.o1;
                if (is == null)
                    return;
                log.addView(createMultiGraph(R.layout.graphview, ViewProcHigh));
                defferedStart();
                processInputStream(res.o2, is, res.o2.toString());
                defferedFinish();
            }
            if (requestCode == CHOOSE_RESULT_COPY) {
                final Pair<InputStream, FileDescription> pp = openSelected(data);
                final InputStream is = pp.o1;
                if (is == null)
                    return;
                File ff = new File(ctx.androidFileDirectory());
                if (!ff.exists()) {
                    ff.mkdir();
                }
                final FileOutputStream fos = new FileOutputStream(ctx.androidFileDirectory() + "/" + pp.o2.getOriginalFileName());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                int vv = is.read();
                                if (vv == -1)
                                    break;
                                fos.write(vv);
                            }
                            fos.flush();
                            fos.close();
                            is.close();
                        } catch (final Exception ee) {
                            errorMes(createFatalMessage(ee, 10));
                        }
                    }
                });
                thread.start();
            }
        } catch (Throwable ee) {
            errorMes(createFatalMessage(ee, 10));
        }
    }

    public void procArchive(FileDescription fd, boolean longClick) {
        setFullInfo(longClick);
        hideFFTOutput = !longClick;
        procArchive(fd);
    }

    private void showExtrems(FFTStatistic inputStat, int mode, int idx) {
        int sz = inputStat.getMids().length;
        addToLog(String.format("Диапазон экстремумов: %6.3f-%6.3f", ctx.set().FirstFreq,ctx.set().LastFreq));
        ExtremeList list = inputStat.createExtrems(mode, ctx.set());
        if (list.data().size() == 0) {
            addToLog("Экстремумов не найдено");
            return;
        }
        //if (mode==FFTStatistic.ExtremePowerMode || mode==FFTStatistic.ExtremePower2Mode){
        Pair<String, Integer> ss = list.testAlarm2(ctx.set(), inputStat.getFreqStep());
        addToLog(false, ss.o1, middleTextSize, getPaintColor(idx), AppData.StateColors.get(ss.o2));
        //    }
        int count = MainActivity.nFirstMax < list.data().size() ? MainActivity.nFirstMax : list.data().size();
        ExtremeFacade facade = list.getFacade();
        facade.setExtreme(list.data().get(0));
        double val0 = facade.getValue();
        addToLog(facade.getTitle());
        Extreme extreme = facade.extreme();
        addToLog("Ампл     " + facade.getColName() + "    f(гц)     Декремент");
        addToLog(String.format("%6.3f   %6.3f    %6.3f" + (extreme.decSize == -1 ? "" : "      %6.3f"),
                extreme.value, facade.getValue(),
                extreme.idx * inputStat.getFreqStep(), Math.PI * extreme.decSize / extreme.idx));
        double sum = 0;
        for (int i = 1; i < count; i++) {
            facade = list.getFacade();
            facade.setExtreme(list.data().get(i));
            double proc = facade.getValue() * 100 / val0;
            sum += proc;
            extreme = facade.extreme();
            addToLog(String.format("%6.3f   %6.3f    %6.3f" + (extreme.decSize == -1 ? "" : "      %6.3f"),
                    extreme.value, facade.getValue(),
                    extreme.idx * inputStat.getFreqStep(), Math.PI * extreme.decSize / extreme.idx));
        }
        addToLog(String.format("Средний - %d%% к первому", (int) (sum / (count - 1))));
    }

    public synchronized void showStatistic(FFTStatistic inputStat, int idx) {
        if (ctx.set().isTechnicianMode())
            showExtrems(inputStat, 0, idx);
        else
        for (int i = 0; i < FFTStatistic.extremeFacade.length; i++)
            showExtrems(inputStat, i, idx);
        }

    public synchronized void showShort(FFTStatistic inputStat, int idx) {
        if (ctx.set().isTechnicianMode())
            showShort(inputStat, idx, 0);
        else
        for (int mode = 0; mode < FFTStatistic.extremeFacade.length; mode++)
            showShort(inputStat, idx, mode);
        //showShort(inputStat,idx,FFTStatistic.ExtremePowerMode);
        //showShort(inputStat,idx,FFTStatistic.ExtremePower2Mode);
        }

    public synchronized void showShort(FFTStatistic inputStat, int idx, int mode) {
        ExtremeList list = inputStat.createExtrems(mode, ctx.set());
        Pair<String, Integer> ss = list.testAlarm2(ctx.set(), inputStat.getFreqStep());
        int color = ctx.set().isTechnicianMode() ? getPaintColor(mode) : getPaintColor(idx);
        addToLog(false, ss.o1, middleTextSize, color, AppData.StateColors.get(ss.o2));
        }
    //--------------------------------------------------------------------------
    public DataDescription loadArchive() {
        try {
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            String ss = ctx.androidFileDirectory() + "/" + archiveFile;
            InputStreamReader out = new InputStreamReader(new FileInputStream(ss), "UTF-8");
            DataDescription archive = (DataDescription) gson.fromJson(out, DataDescription.class);
            out.close();
            return archive;
        } catch (Exception ee) {
            errorMes("Ошибка чтения архива:\n" + ee.toString() + "\nСоздан пустой");
            popupInfo("Ошибка чтения архива,создан пустой");
            DataDescription archive2 = new DataDescription();
            saveArchive(archive2);
            return archive2;
        }
    }

    public void saveArchive(DataDescription archive) {
        try {
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            String ss = ctx.androidFileDirectory() + "/" + archiveFile;
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(ss), "UTF-8");
            gson.toJson(archive, out);
            out.flush();
            out.close();
        } catch (Exception ee) {
            errorMes("Ошибка записи архива:\n" + ee.toString());
        }
        popupInfo("Ошибка записи архива");
    }

    //------------------------------------------------------------------------
    private ArrayList<MenuItemAction> menuList = new ArrayList<>();

    private String[] createMenuTitles() {
        String out[] = new String[menuList.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = menuList.get(i).title;
        return out;
    }

    public void procMenuItem(int index) {
        menuList.get(index).onSelect();
    }

    public void createMenuList() {
        menuList.clear();
        new MIArchive(this);
        new MIArchiveFull(this);
        new MIFullScreen(this);
        new MIGroupCreate(this);
        new MIGroupDestroy(this);
        menuList.add(new MenuItemAction("Очистить ленту") {
            @Override
            public void onSelect() {
                log.removeAllViews();
            }
        });
        if (!ctx.set().technicianMode)
            new MIDeleteFromArchive(this);
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Измерение") {
            @Override
            public void onSelect() {
                btViewFace.selectSensorGroup(new SensorGroupListener() {
                    @Override
                    public void onSensor(ArrayList<BTReceiver> receiverList) {
                        for (BTReceiver receiver : receiverList) {
                            String name = btViewFace.getSensorName(receiver).replace("_", "-");
                            BTTextFile file = new BTTextFile(ctx.set(), name, gpsService.lastGPS());
                            receiver.startMeasure(file, false);
                        }
                    }
                });
            }
        });
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Отменить измерение") {
            @Override
            public void onSelect() {
                for (BTReceiver receiver : btViewFace.sensorList)
                    receiver.stopMeasure();
            }
        });
        menuList.add(new MenuItemAction("Настройки") {
            @Override
            public void onSelect() {
                new SettingsMenu(MainActivity.this);
            }
        });
        if (isAllEnabled() && !ctx.set().technicianMode)
            menuList.add(new MenuItemAction("Связь с сервером") {
                @Override
                public void onSelect() {
                new LoginSettingsMenu(MainActivity.this);
            }
            });
        if (ctx.cState()== AppData.CStateGreen && isAllEnabled()){
            new MIUpLoad(MainActivity.this);
            new MIDownLoad(MainActivity.this);
            }
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Выключить все") {
            @Override
            public void onSelect() {
                btViewFace.offAll();
            }
        });
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Сброс BlueTooth") {
            @Override
            public void onSelect() {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled())
                    mBluetoothAdapter.disable();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        });
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Список сенсоров") {
            @Override
            public void onSelect() {
                for (BTDescriptor descriptor : ctx.set().knownSensors)
                    addToLog("Датчик: " + descriptor.btName + ": " + descriptor.btMAC);
            }
        });
        if (isAllEnabled())
        menuList.add(new MenuItemAction("Очистить список") {
            @Override
            public void onSelect() {
                ctx.set().knownSensors.clear();
                ctx.set().createMaps();
                ctx.fileService().saveContext();
            }
        });
        new MIMap(this);
        if (!ctx.set().technicianMode) {
            new MISendMail(this);
            new MIViewWave(this);
            new MIConvertToWave(this);
            new MIWavePlayer(this);
            new MIResultsPlayer(this);
            new MIExport(this);
            new MIFileCopy(this);
            new MIFileProcess(this, false);
            new MIFileProcess(this, true);
            menuList.add(new MenuItemAction("Образец") {
                @Override
                public void onSelect() {
                    BTTextFile file2 = new BTTextFile(ctx.set(), "Тест", gpsService.lastGPS());
                    BTReceiver receiver = new BTReceiver(btViewFace, btViewFace.BTBack);
                    receiver.startMeasure(file2, true);
                    }
                });
            new MITestSignal(this);
            new MIAudioRecord(this);
            }
        new MIExportAndSendMail(this);
        new MIAbout(this);
        if (!ctx.set().technicianMode) {
            menuList.add(new MenuItemAction("Регистрация") {
                @Override
                public void onSelect() {
                    new RegistrationMenu(MainActivity.this);
                }
                });
            if (ctx.set().fullInfo)
                new MITestCase(this);
            }
        menuList.add(new MenuItemAction("Выход") {
            @Override
            public void onSelect() {
                finish();
            }
        }   );
    }

    private I_ArchiveSelector uploadSelector = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            File file = new File(ctx.androidFileDirectory() + "/" + fd.getOriginalFileName());
        }
    };
    private I_ArchiveSelector deleteSelector = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            File file = new File(ctx.androidFileDirectory() + "/" + fd.getOriginalFileName());
            file.delete();
        }
    };
    //-------------------------------------------------------------------------------------------------
    private I_ArchiveSelector archiveProcView = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            procArchive(fd, false);
        }
    };
    private I_ArchiveSelector archiveProcViewFull = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            procArchive(fd, true);
        }
    };

    //----------------------------------------------------------------------------------------------
    public void moveFile(String src, String dst) throws Exception {
        BufferedReader fd1 = new BufferedReader(new InputStreamReader(new FileInputStream(src), "Windows-1251"));
        BufferedWriter fd2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), "Windows-1251"));
        String ss;
        while ((ss = fd1.readLine()) != null) {
            fd2.write(ss);
            fd2.newLine();
        }
        fd2.flush();
        fd1.close();
        fd2.close();
        File file = new File(src);
        file.delete();
    }

    //----------------------------------------------------------------------------------------------
    View.OnClickListener waveStartEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new OneParameterDialog(MainActivity.this, "Параметр графика", "Начало (сек)", "" + waveStartTime, false, false, new I_EventListener() {
                @Override
                public void onEvent(String ss) {
                    try {
                        double val = Double.parseDouble(ss);
                        if (val < 0 || val > currentWave.getData().length / 100.) {
                            popupAndLog("Выход за пределы диапазона");
                            return;
                        }
                        waveStartTime = val;
                        procWaveForm();
                    } catch (Exception ee) {
                        popupAndLog("Формат вещественного числа");
                    }
                }
            });
        }
    };
    //----------------------------------------------------------------------------------------------
    View.OnClickListener waveMasEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new OneParameterDialog(MainActivity.this, "Параметр графика", "Масштаб", "" + waveMas, false, false, new I_EventListener() {
                @Override
                public void onEvent(String ss) {
                    try {
                        int val = Integer.parseInt(ss);
                        waveMas = val;
                        procWaveForm();
                    } catch (Exception ee) {
                        popupAndLog("Формат целого числа");
                    }
                }
            });
        }
    };
    //----------------------------------------------------------------------------------------------
    private FFTAudioTextFile currentWave;

    public void procWaveForm(final FFTAudioTextFile xx) {
        currentWave = xx;
        procWaveForm();
    }

    public void procWaveForm() {
        LinearLayout lrr = (LinearLayout) getLayoutInflater().inflate(R.layout.graphview, null);
        LinearLayout panel = (LinearLayout) lrr.findViewById(R.id.viewPanel);
        LineGraphView graphView = new LineGraphView(this, "");
        graphView.setScalable(true);
        graphView.setScrollable(true);
        graphView.getGraphViewStyle().setTextSize(15);
        panel.addView(graphView);
        addToLog("Начало (сек)", 20, waveStartEvent);
        addToLog("Масштаб", 20, waveMasEvent);
        log.addView(lrr);
        int firstPoint = (int) (waveStartTime * 100);
        int size = currentWave.getData().length;
        int count = size / waveMas;
        int lastPoint = size - firstPoint - count;
        if (lastPoint < 0) lastPoint = 0;
        paintOne(graphView, 0, currentWave.getData(), DispColor, firstPoint, lastPoint, false);
        addToLog("");
    }

    public void procWaveForm(FileDescription fd) {
        String fname = fd.getOriginalFileName();
        try {
            FileInputStream fis = new FileInputStream(ctx.androidFileDirectory() + "/" + fname);
            addToLog(fd.toString(), greatTextSize);
            FFTAudioTextFile xx = new FFTAudioTextFile();
            xx.readData(fd, new BufferedReader(new InputStreamReader(fis, "Windows-1251")));
            waveMas = 1;
            waveStartTime = 0;
            procWaveForm(xx);
        } catch (Throwable e) {
            errorMes("Файл не открыт: " + fname + "\n" + createFatalMessage(e, 10));
        }
    }

    public void selectFromArchive(String title, final I_ArchiveSelector selector) {
        final ArrayList<FileDescription> ss = createArchive();
        ArrayList<String> out = new ArrayList<>();
        for (FileDescription ff : ss)
            out.add(ff.toString());
        new ListBoxDialog(this, out, title, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                selector.onSelect(ss.get(index), false);
            }

            @Override
            public void onLongSelect(int index) {
                selector.onSelect(ss.get(index), true);
            }

            @Override
            public void onCancel() {
            }
        }).create();
    }

    public void selectMultiFromArchive(String title, final I_ArchiveMultiSelector selector) {
        selectMultiFromArchive(false, title, selector);
    }

    public void selectMultiFromArchive(boolean dirList, String title, final I_ArchiveMultiSelector selector) {
        final ArrayList<FileDescription> ss = dirList ? createDirArchive() : createArchive();
        final ArrayList<String> list = new ArrayList<>();
        for (FileDescription ff : ss)
            list.add(dirList ? ff.getOriginalFileName() : ff.toString());
        new MultiListBoxDialog(this, title, list, new MultiListBoxListener() {
            @Override
            public void onSelect(boolean[] selected) {
                FileDescriptionList out = new FileDescriptionList();
                for (int i = 0; i < ss.size(); i++)
                    if (selected[i])
                        out.add(ss.get(i));
                setDefferedList(out);
                selector.onSelect(out, false);
            }
        });
    }

    public void showWaveForm() {
        final ArrayList<FileDescription> ss = createArchive();
        ArrayList<String> out = new ArrayList<>();
        for (FileDescription ff : ss)
            out.add(ff.toString());
        new ListBoxDialog(this, out, "Просмотр волны", new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                procWaveForm(ss.get(index));
            }

            @Override
            public void onLongSelect(int index) {
            }

            @Override
            public void onCancel() {
            }
        }).create();
    }

    public void addArchiveItemToLog(final FileDescription ff) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setFullInfo(false);
                    hideFFTOutput = true;
                    FileInputStream fis = new FileInputStream(ctx.androidFileDirectory() + "/" + ff.getOriginalFileName());
                    addToLog(ff.toString(), greatTextSize);
                    processInputStream(ff, fis, ff.toString());
                } catch (Throwable e) {
                    errorMes("Файл не открыт: " + ff.getOriginalFileName() + "\n" + createFatalMessage(e, 10));
                }
            }
        };
        View.OnLongClickListener listenerLong = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    setFullInfo(true);
                    hideFFTOutput = false;
                    FileInputStream fis = new FileInputStream(ctx.androidFileDirectory() + "/" + ff.getOriginalFileName());
                    addToLog(ff.toString());
                    processInputStream(ff, fis, ff.toString());
                } catch (Throwable e) {
                    errorMes("Файл не открыт: " + ff.getOriginalFileName() + "\n" + e.toString());
                    return false;
                }
                return true;
            }
        };
        addToLogButton(ff.toString(), false,listener, listenerLong);
    }

    //----------------------------------------------------------------------------------------------
    // Создаем BroadcastReceiver для ACTION_FOUND
    private final BroadcastReceiver blueToothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addToLog("BlueTooth: " + device.getName() + " " + device.getAddress());
                if (device.getName().startsWith(BT_SENSOR_NAME_PREFIX)) {
                    BTReceiver receiver = new BTReceiver(btViewFace, btViewFace.BTBack);
                    receiver.blueToothOn(device);
                    btViewFace.sensorList.add(receiver);
                }
            }
        }
    };

    private void addSensorName() {
        if (btViewFace.sensorList.size() != 1) {
            popupAndLog("Нужен один активный датчик");
            return;
        }
        final BTReceiver receiver = btViewFace.sensorList.get(0);
        if (ctx.set().addressMap.get(receiver.getSensorMAC()) != null) {
            popupAndLog("Датчик с именем: " + btViewFace.getSensorName(receiver));
            return;
        }
        new OneParameterDialog(this, "Имя датчика", receiver.getSensorMAC(), "", false, true, new I_EventListener() {
            @Override
            public void onEvent(String ss) {
                if (ctx.set().nameMap.get(ss) != null) {
                    popupAndLog("Имя используется: " + ss);
                    return;
                }
                ctx.set().knownSensors.add(new BTDescriptor(ss, receiver.getSensorMAC()));
                ctx.set().createMaps();
                ctx.fileService().saveContext();
            }
        });
    }

    public LinearLayout log() {
        return log;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                addToLog("Ошибка определения сервиса местоположения:\n" + e.toString());
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    //---------------------------------------- Параметры трубы --------------------------------------
    /*
    public String getSimCardICC() {
        SubscriptionManager manager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        int defaultSmsId = SubscriptionManager.getDefaultSmsSubscriptionId();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
            }
        SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultSmsId);
        return info.getIccId();
        }
     */
    //-----------------------------------------------------------------------------------------------
    Retrofit retrofit = null;
    private Runnable httpKeepAlive = new Runnable() {
        @Override
        public void run() {
            if (AppData.ctx().cState()== AppData.CStateGray)
                return;
            String token = ctx.loginSettings().getSessionToken();
            new NetCall<JInt>().call(MainActivity.this,ctx.getService().keepalive(token), new NetBack() {
                @Override
                public void onError(int code, String mes) {
                    ctx.toLog(false,"Ошибка keep alive: "+mes+". Сервер недоступен");
                    sessionOff();
                    }
                @Override
                public void onError(UniException ee) {
                    ctx.toLog(false,"Ошибка keep alive: "+ee.toString()+". Сервер недоступен");
                    sessionOff();
                    }
                @Override
                public void onSuccess(Object val) {
                }
            });
            if (!shutDown && AppData.ctx().isApplicationOn())
                setDelay(AppData.CKeepALiveTime, httpKeepAlive);
        }
    };
    public void retrofitDisconnect() {
        ctx.cState(AppData.CStateGray);
        }

    public void retrofitConnect() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(AppData.HTTPTimeOut, TimeUnit.SECONDS)
                .connectTimeout(AppData.HTTPTimeOut, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" +ctx.loginSettings().getDataSetverIP() + ":" + ctx.loginSettings().getDataServerPort())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ctx.service(retrofit.create(RestAPIBase.class));
        ctx.service2(retrofit.create(RestAPILEP500.class));
        }
    public void  sessionOn(){
        httpKeepAlive.run();                // Сразу и потом по часам
        ctx.cState(AppData.CStateGreen);
        }
    public void sessionOff() {
        cancelDelay(httpKeepAlive);
        ctx.cState(AppData.CStateGray);
        }
    public void setDelay(int sec, Runnable code) {          // С возвратом в GUI
        event.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(code);
                }
            }, sec * 1000);
        }
    public void cancelDelay(Runnable code) {
        event.removeCallbacks(code);
        }
    public String createRegistrationCode(){
        return Registration.createRegistrationCode(getSoftwareId());
        }
    public boolean isAllEnabled(){
        String ss = ctx.loginSettings().getRegistrationCode();
        return ss.equals(createRegistrationCode()) && ss.length()!=0;
        }
    public String getSoftwareId() {         // Закодированный
        String ss = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return ss;
        }
    public String getSoftwareId64() {         // Закодированный
        return toBase64(getSoftwareId());
        }
}
