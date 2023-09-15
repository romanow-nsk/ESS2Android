package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.Utils.httpError;
import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.awt.Image;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.artifacts.Artifact;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUI2StateBox;
import romanow.abc.core.entity.metadata.view.Meta2GUIImage;
import romanow.abc.core.entity.metadata.view.Meta2GUIImageBit;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.core.utils.Pair;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.I_Value;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIImageBit extends View2BaseDesktop {
    private ImageView imagePanel=null;
    private Bitmap imgFalse=null;
    private Bitmap imgTrue=null;
    private Meta2GUIImageBit element;
    private int dx;
    private int dy;
    private int bitNum=0;
    private int lastBitValue=0;

    public DesktopGUIImageBit(){
        type = Values.GUIImageBit;
    }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        element = (Meta2GUIImageBit) getElement();
        bitNum = element.getBitNum();
        imagePanel = new ImageView(context.getMain().main(),null);
        int color = getBackColor();
        imagePanel.setBackgroundColor(color);
        dx = context.dx(element.getImageW());
        dy = context.dy(element.getImageH());
        setBounds(imagePanel,
                context.x(element.getX()+getDxOffset()+element.getDx()+5),
                context.y(element.getY()+getDyOffset()),
                dx,dy );
        if (element.getPictureFor0().getOid()!=0)
            loadImage(context.getMain().main(), element.getPictureFor0().getRef(), new I_Value<Bitmap>() {
                @Override
                public void onEnter(Bitmap value) {
                    imgFalse = Bitmap.createScaledBitmap(value,dx,dy,false);
                }
            });
        if (element.getPictureFor1().getOid()!=0)
            loadImage(context.getMain().main(), element.getPictureFor1().getRef(), new I_Value<Bitmap>() {
                @Override
                public void onEnter(Bitmap value) {
                    imgTrue = Bitmap.createScaledBitmap(value,dx,dy,false);
                }
            });
        panel.addView(imagePanel);
        setInfoClick(imagePanel);
        }

    @Override
    public void putValue(long vv) throws UniException {
        lastBitValue = (int)(vv>>bitNum) & 01;
        if (lastBitValue==0 && imgFalse==null)
            return;
        if (lastBitValue==1 && imgTrue==null)
            return;
        Bitmap bb = lastBitValue==0 ? imgFalse : imgTrue;
        imagePanel.setImageBitmap(bb);
        }

    public void showInfoMessage() {
        Meta2BitRegister set = (Meta2BitRegister) getRegister();
        Meta2Bit bit = set.getBits().getByCode(bitNum);
        String ss = "Картинка-бит: Разряд регистра "+toHex(set.getRegNum()+getRegOffset())+" ["+toHex(set.getRegNum())+"]("+bitNum+") "+set.getShortName()+"$"+set.getTitle()+"$";
        ss+=bit==null ? " не найден " : bit.getTitle();
        context.getMain().main().popupInfo(ss);
        }

    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = (Meta2Register) getRegister();
        if (!(register instanceof Meta2BitRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
}
