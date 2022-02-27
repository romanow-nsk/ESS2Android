package romanow.abc.ess2.android.rendering.view2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;

import romanow.abc.ess2.android.R;

@SuppressLint("AppCompatCustomView")
public class StyleButton extends Button {
    public StyleButton(Context context) {
        super(context,null, R.style.FormButton);
    }
}
