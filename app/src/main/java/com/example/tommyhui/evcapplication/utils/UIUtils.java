package com.example.tommyhui.evcapplication.utils;

/**
 * Created by Derek on 28/4/15.
 */
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.example.tommyhui.evcapplication.R;

public class UIUtils {

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private static final int[] RES_IDS_ACTION_BAR_SIZE = { R.attr.actionBarSize };

    /** Calculates the Action Bar height in pixels. */
    public static int calculateActionBarSize(Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static Drawable getTintedDrawable(Resources res, int drawableResId, int colorResId) {
        Drawable drawable = ResourcesCompat.getDrawable(res, drawableResId, null);
        DrawableCompat.setTint(drawable, res.getColor(colorResId));
//        int color = res.getColor(colorResId);
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static Drawable getDefaultSelectableBackgroundDrawable(Context context) {
        int[] attrs = new int[] { android.R.attr.selectableItemBackground};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        return drawableFromTheme;
    }
}
