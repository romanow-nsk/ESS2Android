package romanow.abc.ess2.android.service;

import static romanow.abc.core.Utils.httpError;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import romanow.abc.core.DBRequest;
import romanow.abc.core.Utils;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.artifacts.Artifact;
import romanow.abc.core.entity.metadata.Meta2Equipment;
import romanow.abc.core.entity.metadata.Meta2GUIView;
import romanow.abc.core.entity.metadata.Meta2XML;
import romanow.abc.core.entity.metadata.Meta2XStream;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.entity.subject2area.ESS2Device;
import romanow.abc.core.entity.subject2area.ESS2Equipment;
import romanow.abc.core.entity.subject2area.ESS2MetaFile;
import romanow.abc.core.entity.subject2area.ESS2ScriptFile;
import romanow.abc.core.entity.subject2area.ESS2View;
import romanow.abc.core.entity.subjectarea.AccessManager;
import romanow.abc.core.script.CallContext;
import romanow.abc.core.script.CompileError;
import romanow.abc.core.script.FunctionCode;
import romanow.abc.core.script.Scaner;
import romanow.abc.core.script.Syntax;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.I_DownLoadString;
import romanow.abc.ess2.android.I_DownLoadXML;
import romanow.abc.ess2.android.I_Event;
import romanow.abc.ess2.android.MainActivity;
import romanow.abc.ess2.android.R;

public class ESS2ArchitectureData {
    public final static int archStateIcons[]={
            R.drawable.settings_gray,
            R.drawable.settings_red,
            R.drawable.settings_yellow,
            R.drawable.settings_green,
            R.drawable.settings_green,
            R.drawable.settings_green
            };
    public final static int connStateIcons[]={
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_gray,
            R.drawable.status_red,
            R.drawable.status_green,
            };
    private MainActivity base;
    private ImageView deployState;
    private ImageView connectState;
    private ImageView renderState;
    private TextView deployStateText;
    private TextView renderStateText;
    private Button formMenuButton;
    private AppData ctx;
    ESS2Architecture deployed=null;         // Развернутая архитектура
    ESS2View currentView=null;              // Текущий вид
    AccessManager manager;
    private int loadCount=0;                // Счетчик загрузок XML-файлов
    private ESS2Architecture arch=null;     // Используется при загрузке
    private ESS2Rendering rendering=null;
    public MainActivity main(){ return base; }
    public Button formMenuButton(){ return formMenuButton; }
    public ESS2ArchitectureData(MainActivity main0){
        rendering = new ESS2Rendering(this);
        base = main0;
        ctx = AppData.ctx();
        deployState = (ImageView) base.findViewById(R.id.headerDeployState);
        connectState = (ImageView) base.findViewById(R.id.headerConnectState);
        renderState = (ImageView) base.findViewById(R.id.headerRenderState);
        deployStateText = (TextView) base.findViewById(R.id.headerDeployStateText);
        renderStateText = (TextView) base.findViewById(R.id.headerRenderStateText);
        formMenuButton = (Button) base.findViewById(R.id.headerRenderMenu);
        renderState.setVisibility(View.INVISIBLE);
        formMenuButton.setVisibility(View.INVISIBLE);
        renderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOffActionPerformed();
                }
            });
        formMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rendering.createChildFormList();
                }
            });
        formMenuButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
                }
            });
        }
    //------------------------------------------------------------------------------------------------
    public void refreshArchtectureState(AccessManager manager0){
        manager = manager0;
        if (ctx.cState()!=AppData.CStateGreen){
            deployState.setImageResource(archStateIcons[0]);
            connectState.setImageResource(connStateIcons[0]);
            renderState.setVisibility(View.INVISIBLE);
            return;
            }
        new NetCall<ArrayList<Long>>().call(base,ctx.getService2().getArchitectureState(ctx.loginSettings().getSessionToken()), new NetBackDefault(){
            @Override
            public void onSuccess(Object vv) {
                ArrayList<Long> val = (ArrayList<Long>)vv;
                int state = val.get(0).intValue();
                deployState.setImageResource(archStateIcons[state]);
                connectState.setImageResource(connStateIcons[state]);
                base.addToLog("Состояние архитектуры: "+ Values.constMap().getGroupMapByValue("ArchState").get(state).title());
                if (state==Values.ASNotDeployed)
                    return;
                long oid = val.get(1);
                /*
                int idx=-1;
                for(int i=0;i<architectures.size();i++)
                    if (architectures.get(i).getOid()==oid){
                        idx=i;
                        break;
                    }
                if (idx==-1){
                    popup("Не найдена развернутая архитектура, oid="+oid);
                    return;
                    }
                */
                loadDeployedArchitecture(oid, state);
                }
            });
        }
    //----------------------------------------------------------------------------------------------
    public void onArchitectureLoaded() {
        boolean hasErrors = arch.getErrors().getErrCount()!=0;
        if (hasErrors){
            base.errorMes(arch.getErrors().toString());
            return;
            }
        renderState.setVisibility(View.VISIBLE);
        base.addToLog(arch.getErrors().toString());
        deployed = arch;
        deployed.testFullArchitecture();
        ModBusClientAndroidDriver driver = new ModBusClientAndroidDriver();
        Object oo[]={base};
        try {
            driver.openConnection(oo,null);
            for (ESS2Equipment equipment : deployed.getEquipments()) {
                equipment.createFullEquipmentPath();
                }
            for(ESS2Device device : deployed.getDevices()){         // НАстроить прокси
                device.setDriver(driver);
                }
            } catch (Exception ee){
                base.addToLog("Ошибка драйвера БД: "+ee.toString());
                }
        refreshDeployedMetaData();
        deployed.createStreamRegisterList();
        //refreshSelectedArchitecture();
        }
    //-------------------------------------------------------------------------------------------------------------------
    private void testXMlFileLoading(){
        if (loadCount<=0){          // Исключение было
            onArchitectureLoaded();
            return;
            }
        loadCount--;
        base.addToLog("Осталось загрузить "+loadCount+" XML-файлов");
        if (loadCount!=0)
            return;
        onArchitectureLoaded();
    }
    private void loadDeployedArchitecture(final long oid, final int state) {
        try {
            new NetCall<DBRequest>().call(base, ctx.getService().getEntity(ctx.loginSettings().getSessionToken(), "ESS2Architecture", oid, 4), new NetBackDefault() {
                @Override
                public void onSuccess(Object val){
                    try {
                        arch = (ESS2Architecture) ((DBRequest) val).get(new Gson());
                        arch.setArchitectureState(state);
                        Artifact artifact;
                        loadCount = arch.getEquipments().size()+arch.getViews().size()+getPrecompiledScriptCount();
                        for (ESS2Equipment equipment : arch.getEquipments()) {
                            ESS2MetaFile metaFile = equipment.getMetaFile().getRef();
                            artifact = metaFile.getFile().getRef();
                            loadXMLArtifact(artifact, new I_DownLoadXML() {
                                @Override
                                public void onSuccess(Meta2XML xml) {
                                    arch.addErrorData(xml);
                                    equipment.setEquipment((Meta2Equipment) xml);
                                    testXMlFileLoading();
                                    }
                                @Override
                                public void onError(String mes) {
                                    base.errorMes(mes);
                                    testXMlFileLoading();
                                    }
                                });
                            }
                        for (ESS2View view : arch.getViews()) {
                            ESS2MetaFile metaFile = view.getMetaFile().getRef();
                            artifact = metaFile.getFile().getRef();
                            loadXMLArtifact(artifact, new I_DownLoadXML() {
                                @Override
                                public void onSuccess(Meta2XML xml) {
                                    arch.addErrorData(xml);
                                    view.setView((Meta2GUIView) xml);
                                    testXMlFileLoading();
                                    }
                                @Override
                                public void onError(String mes) {
                                    base.errorMes(mes);
                                    testXMlFileLoading();
                                    }
                                });
                            }
                        preCompileLocalScripts();
                    } catch (Exception ex) {
                        arch.addErrorData(Utils.createFatalMessage(ex));
                        loadCount=0;
                        testXMlFileLoading();
                        }
                   }
                });
            } catch(Exception ee){
                base.errorMes("!!!!!!!!"+ee.toString());
                }
        }
    //------------------------------------------------------------------------------------------------------------
    public void loadXMLArtifact(Artifact art, I_DownLoadXML back){
        loadFileAsString(art, new I_DownLoadString() {
            @Override
            public void onSuccess(String ss) {
                Meta2XML entity = (Meta2XML) new Meta2XStream().fromXML(ss);
                entity.setHigh(null);
                entity.createMap();
                entity.testLocalConfiguration();
                back.onSuccess(entity);
                }
            @Override
            public void onError(String mes) {
                back.onError(mes);
                }
            });
        }
    //---------------------------------------------------------------------------------------------------------------------
    private void refreshDeployedMetaData(){
        if (deployed==null)
            return;
        int state = deployed.getArchitectureState();
        deployStateText.setText(deployed.getFullTitle());
        deployState.setImageResource(archStateIcons[state]);
        connectState.setImageResource(connStateIcons[state]);
        if (!deployed.isDeployed())
            return;
        setRenderingOff();
        }
    //---------------------------------------------------------------------------------------------------------------------------
    public void clearDeployedMetaData(){
        setRenderingOff();
        deployStateText.setText("");
        deployState.setImageResource(archStateIcons[0]);
        connectState.setImageResource(connStateIcons[0]);
        renderState.setVisibility(View.INVISIBLE);
        deployed = null;
        //main2.currentView = null;
        }
    //---------------------------------------------------------------------------------------------------------------------------
    public void setRenderingOff(){
        currentView=null;
        renderState.setImageResource(R.drawable.connect_off);
        formMenuButton.setVisibility(View.INVISIBLE);
        rendering.renderOff();
        }
    public void setRenderingOn() {
        currentView = null;
        for(ESS2View view : deployed.getViews()){
            if (view.getMetaFile().getRef().getMetaType()==Values.MTViewAndroid){
                currentView = view;
                break;
                }
            }
        if (currentView==null){
            base.errorMes("Не найден ЧМИ для Android");
            return;
            }
        renderState.setImageResource(R.drawable.connect_on);
        formMenuButton.setVisibility(View.VISIBLE);
        rendering.renderOn();
        }
    private void OnOffActionPerformed() {//GEN-FIRST:event_OnOffActionPerformed
        if (currentView!=null){
            setRenderingOff();
            }
        else{
            if (!deployed.isConnected())
                return;
            setRenderingOn();
            }
        }
    //------------------------------------------------------------------------------------------------------------------------
    private int getPrecompiledScriptCount(){
        int cnt=0;
        for(ESS2ScriptFile scriptFile : arch.getScripts()){
            if (!scriptFile.isServerScript() && scriptFile.isPreCompiled() && scriptFile.getScriptType()==Values.STCalcClient)
                cnt++;
            }
        return cnt;
        }
    private void preCompileLocalScripts(){
        for(ESS2ScriptFile scriptFile : arch.getScripts()){
            if (!scriptFile.isServerScript() && scriptFile.isPreCompiled() && scriptFile.getScriptType()==Values.STCalcClient){
                loadFileAsString(scriptFile.getFile().getRef(), new I_DownLoadString() {
                    @Override
                    public void onSuccess(String ss) {
                        Syntax SS = compileScriptLocal(scriptFile, ss);
                        boolean res = SS.getErrorList().size()==0;
                        if (res){
                            scriptFile.setScriptCode(new CallContext(SS, arch));
                            base.addToLog("Скрипт " + scriptFile.getShortName() + " " + scriptFile.getTitle() +" скомпилировался");
                            }
                        else
                            base.errorMes("Скрипт " + scriptFile.getShortName() + " " + scriptFile.getTitle() + " не скомпилировался");
                        scriptFile.setValid(res);
                        testXMlFileLoading();
                        }
                    @Override
                    public void onError(String mes) {
                        base.errorMes("Скрипт " + scriptFile.getShortName() + " " + scriptFile.getTitle() + " не загрузился");
                        base.errorMes(mes);
                        testXMlFileLoading();
                        }
                    });
                }
            else{
                scriptFile.setScriptCode(null);
                scriptFile.setValid(false);
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void loadFileAsString(Artifact art,final I_DownLoadString back){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call2 = ctx.getService().downLoad(ctx.loginSettings().getSessionToken(),art.getOid());
                call2.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            final ResponseBody body = response.body();
                            final long fileSize = body.contentLength();
                            new Thread(new Runnable() {             // Response уже в потоке GUI
                                @Override
                                public void run() {
                                    InputStream in = body.byteStream();
                                    try {
                                        InputStreamReader reader = new InputStreamReader(in,"UTF8");
                                        StringBuffer buffer = new StringBuffer();
                                        int cc;
                                        while ((cc=reader.read())!=-1){
                                            buffer.append((char) cc);
                                        }
                                        reader.close();
                                        base.guiCall(new Runnable() {
                                            @Override
                                            public void run() {
                                                back.onSuccess(buffer.toString());
                                            }
                                        });
                                    } catch (final IOException ee) {
                                        base.guiCall(new Runnable() {
                                            @Override
                                            public void run() {
                                                String mes = Utils.createFatalMessage(ee);
                                                back.onError(mes);
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                        else{
                            base.guiCall(new Runnable() {
                                @Override
                                public void run() {
                                    String mes = httpError(response);
                                    back.onError(mes);
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, final Throwable t) {
                        base.guiCall(new Runnable() {
                            @Override
                            public void run() {
                                String mes = Utils.createFatalMessage(t);
                                back.onError(mes);
                            }
                        });
                    }
                });
            }
        }).start();
        }
    //----------------------------------------------------------------------------------------------------------
    public void loadFileAsStringSync(Artifact art,final I_DownLoadString back){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call2 = ctx.getService().downLoad(ctx.loginSettings().getSessionToken(),art.getOid());
                try {
                    Response<ResponseBody> bbody = call2.execute();
                    if (!bbody.isSuccessful()) {
                        String mes = httpError(bbody);
                        base.errorMes(mes);
                        }
                    ResponseBody body = bbody.body();
                    long fileSize = body.contentLength();
                    InputStream in = body.byteStream();
                    InputStreamReader reader = new InputStreamReader(in,"UTF8");
                    StringBuffer buffer = new StringBuffer();
                    int cc;
                    while ((cc=reader.read())!=-1){
                        buffer.append((char) cc);
                        }
                    reader.close();
                    base.guiCall(new Runnable() {
                        @Override
                        public void run() {
                            back.onSuccess(buffer.toString());
                            }
                        });
                    } catch (IOException ee) {
                        String mes = Utils.createFatalMessage(ee);
                        base.errorMes(mes);
                        }
                }
            }).start();
        }
    //------------------------------------------------------------------------------------------------------------------------
    public Syntax compileScriptLocal(ESS2ScriptFile scriptFile,String src){
        ArrayList<String> lines = new ArrayList<>();
        while(true){
            int idx=src.indexOf('\n');
            if (idx==-1){
                lines.add(src);
                lines.add("#");
                break;
                }
            lines.add(src.substring(0,idx-1));
            src = src.substring(idx+1);
            }
        Scaner lex = new Scaner();
        lex.open(lines);
        Syntax SS = new Syntax(lex) {
            @Override
            public void createFunctionMap() {
                createFunctionMap(ValuesBase.StdScriptFunPackage,Values.constMap().getGroupList("ScriptFunStd"));
                createFunctionMap(false,Values.ESS2ScriptFunAndroid,Values.constMap().getGroupList("ScriptFunGUI"));
                }
            };
        FunctionCode ff = SS.compile();
        if (SS.getErrorList().size()!=0)
            base.errorMes("errors: "+SS.getErrorList().size());
        for(CompileError error : SS.getErrorList())
            base.errorMes(error.toString());
        return SS;
        }
    //---------------------------------------------------------------------------------------------------------------------------
    public ImageView getRenderState() {
        return renderState; }
}
