package com.github.demo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.squareup.picasso.Transformation;


public class PicassoTransform implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        final int size = Math.min(source.getWidth(), source.getHeight());
        final int x = (source.getWidth() - size) / 2;
        final int y = (source.getHeight() - size) / 2;

        final Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        final Paint paint = new Paint();
        final BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        final float r = size / 8f;
        final Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawRoundRect(new RectF(0, 0, size, size), r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "rounded_corners";
    }
}