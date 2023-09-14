package romanow.abc.ess2.android.rendering.view2;

import static romanow.abc.core.Utils.httpError;

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
import romanow.abc.core.entity.metadata.Meta2Register;
import romanow.abc.core.entity.metadata.view.Meta2GUI;
import romanow.abc.core.entity.metadata.view.Meta2GUIData;
import romanow.abc.core.entity.metadata.view.Meta2GUIImage;
import romanow.abc.core.entity.subject2area.ESS2Architecture;
import romanow.abc.ess2.android.rendering.FormContext2;
import romanow.abc.ess2.android.rendering.I_GUI2Event;
import romanow.abc.ess2.android.rendering.I_Value;
import romanow.abc.ess2.android.rendering.View2BaseDesktop;

public class DesktopGUIImage extends View2BaseDesktop {
    private ImageView imagePanel=null;
    private Meta2GUIImage element;
    private int dx;
    private int dy;
    public DesktopGUIImage(){
        type = Values.GUIImage;
        }

    @Override
    public void addToPanel(RelativeLayout panel) {
        setLabel(panel);
        element = (Meta2GUIImage) getElement();
        imagePanel = new ImageButton(context.getMain().main(),null);
        imagePanel.setBackgroundColor(getBackColor());
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
                    Bitmap bitmap = Bitmap.createScaledBitmap(value,dx,dy,false);
                    imagePanel.setImageBitmap(bitmap);
                }
            });
        panel.addView(imagePanel);
        setInfoClick(imagePanel);
        }

    @Override
    public void putValue(long vv) throws UniException { }

    public void showInfoMessage() {
        String ss = "Картинка "+element.getPicture().getTitle();
        context.getMain().main().popupInfo(ss);
        }

    @Override
    public String setParams(FormContext2 context0, ESS2Architecture meta0, Meta2GUI element0, I_GUI2Event onEvent0) {
        return super.setParams(context0,meta0, element0,onEvent0);
        }
}
