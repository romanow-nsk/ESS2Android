package romanow.abc.ess2.android.rendering.view2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Bordered {
    private Paint paint = new Paint();
    public Bordered(int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(4);
        }
    protected void onDraw(View view, Canvas canvas) {
        int ww = view.getWidth();
        int hh = view.getHeight();
        canvas.drawLine(0, 0, ww, 0, paint);
        canvas.drawLine(ww, 0, ww, hh, paint);
        canvas.drawLine(0, hh, ww, hh, paint);
        canvas.drawLine(0, 0, 0, hh, paint);
        }

}
