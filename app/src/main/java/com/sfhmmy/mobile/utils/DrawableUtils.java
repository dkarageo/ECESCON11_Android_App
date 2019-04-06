/*
 * DrawableUtils.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.utils;

import android.graphics.drawable.Drawable;

import com.sfhmmy.mobile.App;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;


public class DrawableUtils {

    public static Drawable applyTintToDrawable(@NonNull Drawable drawable, int colorRes) {
        Drawable wrapped = DrawableCompat.wrap(drawable);
        wrapped = wrapped.mutate();
        wrapped.setAlpha(255);
        DrawableCompat.setTint(wrapped, App.getAppResources().getColor(colorRes));
        return wrapped;
    }
}
