package romanow.abc.ess2.android.rendering.module;

public class ModuleFileSource {
    /*
    implements I_FileSource {
    private DataServer db;
    private String path;
    private String name="";
    private I_FileModuleDir module;
    public String getTitle(){ return "Источник файлов"; }
    public ModuleFileSource(){}
    @Override
    public void start(DataServer db0,I_FileModuleDir module0, HashMap<String, String> params) throws UniException {
        db=db0;
        module = module0;
        String pp = params.get("path");
        if (pp==null)
            throw UniException.bug("Нет параметра path - каталог источника файлов");
        path = db.dataServerFileDir()+"/"+pp;
        name = params.get("name");
        if (name==null)
            name="";
            }

    @Override
    public synchronized void stop() {}
    @Override
    public void tryToMake() {}
    @Override
    public synchronized FileSource getNext() throws UniException {
        File ff = new File(path);
        if (!ff.exists()){
            ff.mkdir();
            return null;
            //throw UniException.bug("Каталог не найден: "+path);
            }
        if (!ff.isDirectory()){
            throw UniException.bug("Это не каталог: "+path);
            }
        String[] files = ff.list(new FilenameFilter() {
            @Override public boolean accept(File folder, String name) {
                return true;
                }
            });
        if (files.length==0)
            return null;
        ff = new File(path+"/"+files[0]);
        return new FileSource(module.getModule(),"","",files[0],ff.length());
        }
    @Override
    public void deleteFile(String fname) {
        File ff = new File(path+"/"+fname);
        boolean bb = ff.delete();
        }
    @Override
    public String getName() {
        return name;
        }
    @Override
    public String getPath() {
        return path;
        }
    }

     */
    }