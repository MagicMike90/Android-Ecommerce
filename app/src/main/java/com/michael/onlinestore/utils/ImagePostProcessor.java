package com.michael.onlinestore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by michael on 17/02/2016.
 */
public class ImagePostProcessor {
    private static ImagePostProcessor mInstance;
    private Context mContext;


    public static synchronized ImagePostProcessor getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImagePostProcessor(context);
        }
        return mInstance;
    }
    ImagePostProcessor(Context ctx) {
        mContext = ctx;
    }


    public Bitmap glowProcess(int resId) {
        // An added margin to the initial image
        int margin = 24;
        int halfMargin = margin / 2;

        // the glow radius
        int glowRadius = 16;

        // the glow color
        int glowColor = Color.rgb(0, 192, 255);

        // The original image to use
        Bitmap src = BitmapFactory.decodeResource(mContext.getResources(),
                resId);

        // extract the alpha from the source image
        Bitmap alpha = src.extractAlpha();

        // The output bitmap (with the icon + glow)
        Bitmap bmp = Bitmap.createBitmap(src.getWidth() + margin,
                src.getHeight() + margin, Bitmap.Config.ARGB_8888);

        // The canvas to paint on the image
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setColor(glowColor);

        // outer glow
        paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);

        // original icon
        canvas.drawBitmap(src, halfMargin, halfMargin, null);

        return bmp;
    }
}
