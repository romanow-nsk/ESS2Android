package romanow.abc.ess2.android.rendering.view2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUI2StateBox;
import romanow.abc.core.entity.metadata.view.Meta2GUI3StateBox;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.R;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUI3StateBox extends View2BaseDesktop {
    protected View textField;
    protected int bitNum=0;
    public DesktopGUI3StateBox(){
        type = Values.GUI3StateBox;
        }
    private int iconsWarning[]={
            R.drawable.balllightgray,R.drawable.ballyellow,R.drawable.ballred,R.drawable.ballblue };
    private int iconsWorking[]={
            R.drawable.balllightgray,R.drawable.ballgreen,R.drawable.ballred,R.drawable.ballblue };
    protected View createComponent(){
        return new ImageView(context.getMain().main());
    }
    protected void viewComponent(){
        ((ImageView)textField).setImageResource(R.drawable.ballwhite); // NOI18N
        }
    protected int getSize(){
        return 30;
        }
    protected int getOffset(){
        return 0;
        }
    @Override
    public void addToPanel(RelativeLayout panel) {
        FormContext2 context= getContext();
        Meta2GUI2StateBox element = (Meta2GUI2StateBox) getElement();
        int hh = element.getH();
        if (hh==0) hh=25;
        if (element.getDx()!=0){
            setLabel(panel);
            }
        textField = createComponent();
        int dd = element.getW2();
        if (dd==0) dd=50;
        int sz = getSize();
        int offset = (25-sz)/2;
        setBounds(textField,
                context.x(element.getX()+element.getDx()+getDxOffset()+dd-5+offset),
                context.y(element.getY()+getDyOffset()+(hh-15)/2-5+offset),
                context.dx(sz),
                context.dy(sz));
        viewComponent();
        setInfoClick(textField);
        panel.addView(textField);
        }
    public void showInfoMessage() {
        FormContext2 context= getContext();
        Meta2GUI3StateBox element = (Meta2GUI3StateBox) getElement();
        int bitNumElem = element.getBitNum();
        Meta2BitRegister set = (Meta2BitRegister)getRegister();
        Meta2Bit bit = set.getBits().getByCode(bitNumElem);
        String ss = "Разряды регистра "+(set.getRegNum()+getRegOffset()+" ["+set.getRegNum()+"]("+bitNum+"/"+(bitNum+1)+") "+set.getShortName()+"$"+set.getTitle()+"$");
        ss+=bit.getTitle();
        context.getMain().main().popupInfo(ss);
        }
    private Bitmap getColorIconBitmap(int color){
        int szz = context.dy(getSize());
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getMain().main().getResources(), color);
        return Bitmap.createScaledBitmap(bitmap2,szz,szz,false);
        }
    @Override
    public void putValue(long vv) throws UniException {
        int pair = (int)((vv>>bitNum) & 03);
        ImageView bb = (ImageView) textField;
        Meta2GUI3StateBox element = (Meta2GUI3StateBox) getElement();
        int ss = getContext().getForm().getFormLevel()==0 ? iconsWarning[pair] : iconsWorking[pair];
        //bb.setImageResource(ss);
        bb.setImageBitmap(getColorIconBitmap(ss));
        }
    @Override
    public String setParams(FormContext2 context, ESS2Architecture meta, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context,meta, element0,onEvent0);
        if (!(getRegister() instanceof Meta2BitRegister))
            return "Недопустимый "+getRegister().getTypeName()+" для "+getTypeName();
        return null;
        }
    @Override
    public void setBitNum(int nbit) {
        bitNum = nbit;
        }
}
