package romanow.abc.ess2.android.rendering.view2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class BorderedImageView extends ImageView {
    private Bordered bordered;
    private Paint paint = new Paint();
    public BorderedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bordered = new Bordered(Color.BLACK);
        }
    public BorderedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bordered = new Bordered(Color.BLACK);
        }
    public BorderedImageView(Context context, int color) {
        super(context);
        bordered = new Bordered(color);
        }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bordered.onDraw(this,canvas);
        }
    }