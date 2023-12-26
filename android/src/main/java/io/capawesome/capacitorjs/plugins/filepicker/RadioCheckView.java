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
        init();
    }

    private void init() {
        setChecked(false);
    }

    public void setChecked(boolean enable) {
        if (enable) {
            setImageResource(R.drawable.ic_preview_radio_on);
            //mDrawable = getDrawable();
            //mDrawable.setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);
        } else {
            setImageResource(R.drawable.ic_preview_radio_off);
            //mDrawable = getDrawable();
            //mDrawable.setColorFilter(mUnSelectUdColor, PorterDuff.Mode.SRC_IN);
        }
    }
}
