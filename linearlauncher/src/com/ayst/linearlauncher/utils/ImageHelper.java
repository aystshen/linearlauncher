package com.ayst.linearlauncher.utils;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by ayst on 2016/3/6.
 */
public class ImageHelper {

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    public static Drawable mergeColorBg(Drawable srcDrawable, int color) {
        Bitmap bitmap = Bitmap.createBitmap(
                srcDrawable.getIntrinsicWidth(),
                srcDrawable.getIntrinsicHeight(),
                srcDrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        srcDrawable.setBounds(0, 0, srcDrawable.getIntrinsicWidth(), srcDrawable.getIntrinsicHeight());
        srcDrawable.draw(canvas);
        Drawable destDrawable = new BitmapDrawable(bitmap);
        //bitmap.recycle();
        return destDrawable;
    }
}
