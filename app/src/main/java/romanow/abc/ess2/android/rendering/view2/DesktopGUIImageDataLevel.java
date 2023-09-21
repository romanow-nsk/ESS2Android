package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.Utils.httpError;
import static romanow.abc.core.entity.metadata.Meta2Entity.toHex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.awt.Image;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.metadata.Meta2Bit;
import romanow.abc.core.entity.metadata.Meta2BitRegister;
import romanow.abc.core.entity.metadata.Meta2DataRegister;
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.Meta2SettingRegister;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUI2StateBox;
import romanow.abc.core.entity.metadata.view.Meta2GUIFormButton;
import romanow.abc.core.entity.metadata.view.Meta2GUIImageBit;
import romanow.abc.core.entity.metadata.view.Meta2GUIImageDataLevel;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.I_Value;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIImageDataLevel extends View2BaseDesktop {
    private Bordered bordered = new Bordered(Color.BLACK);
    private BorderedImageView imagePanel=null;
    private Bitmap img=null;
    private Meta2GUIImageDataLevel element;
    private int dx;
    private int dy;
    private double scriptValue=0;
    private boolean valueValid=false;

    public DesktopGUIImageDataLevel(){
        type = Values.GUIImageDataLevel;
    }
    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        element = (Meta2GUIImageDataLevel) getElement();
        imagePanel = new BorderedImageView(context.getMain().main(),null){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Paint paint = new Paint();
                if (valueValid) {
                    int yy = 0;
                    if (scriptValue > element.getHighLevel())
                        yy = imagePanel.getHeight();
                    else if (scriptValue < element.getLowLevel())
                        yy = 0;
                    else{
                        yy = (int) ((scriptValue - element.getLowLevel()) / (element.getHighLevel() - element.getLowLevel()) * dy);
                        }
                    int color = Color.GREEN;
                    if (scriptValue < element.getFailLevel())
                        color = Color.RED;
                    else if (scriptValue < element.getWarnLevel())
                        color = Color.YELLOW;
                    paint.setColor(0xFFF0F0F0);
                    int dd = dy-yy;
                    Rect rect = new Rect(0,0,dx,dd);
                    canvas.drawRect(rect,paint);
                    paint.setColor(color);
                    rect = new Rect(0,dd,dx,dy);
                    canvas.drawRect(rect,paint);
                    }
                if (img!=null)
                    canvas.drawBitmap(img,null, new Rect(0,0,dx,dy),paint);
                //canvas.restore();
                }
            };
        int color = getBackColor();
        imagePanel.setBackgroundColor(color);
        dx = context.dx(element.getImageW());
        dy = context.dy(element.getImageH());
        setBounds(imagePanel,
                context.x(element.getX()+getDxOffset()+element.getDx()+5),
                context.y(element.getY()+getDyOffset()),
                dx,dy );
        if (element.getPicture().getOid()!=0)
            loadImage(context.getMain().main(), element.getPicture().getRef(), new I_Value<Bitmap>() {
                @Override
                public void onEnter(Bitmap value) {
                    img = Bitmap.createScaledBitmap(value,dx,dy,false);
                    imagePanel.setImageBitmap(img);
                    }
                });
        imagePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (element.isOwnUnit() && element.getUnitLevel()!=0){
                    context.setIndex(element.getUnitLevel(),element.getUnitIdx());
                }
                context.openForm(element.getFormName(),FormContext2.ModeNext);
            }
        });
        panel.addView(imagePanel);
        setInfoClick(imagePanel);
        }

    public void showInfoMessage() {
        Meta2Register set = getRegister();
        String ss = "Картинка-уровень: Разряд регистра "+toHex(set.getRegNum()+getRegOffset())+" ["+toHex(set.getRegNum())+"] "+set.getShortName()+"$"+set.getTitle()+"$";
        context.getMain().main().popupInfo(ss);
        }

    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        super.setParams(context0,meta0, element0,onEvent0);
        Meta2Register register = getRegister();
        if (!(register instanceof Meta2DataRegister || register instanceof Meta2SettingRegister))
            return "Недопустимый "+register.getTypeName()+" для "+getTypeName();
        return null;
        }
    //----------------------------------------------------------------------------------------------
    @Override
    public void putValue(long vv) throws UniException {
        Meta2Register register = getRegister();
        scriptValue = register.regValueToFloat(getUnitIdx(), (int) vv);
        valueValid = true;
        imagePanel.invalidate();
        }
}
