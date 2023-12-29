package io.capawesome.capacitorjs.plugins.filepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

public class RadioCheckView extends AppCompatImageView {

    private Drawable mDrawable;

    public RadioCheckView(Context context) {
        super(context);
    }

    public RadioCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChecked(boolean enable) {
        if (enable) {
            setImageResource(R.drawable.ic_preview_radio_on);
        } else {
            setImageResource(R.drawable.ic_preview_radio_off);
        }
    }
}
