package com.example.mplayer.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ThumbnailUtils;

public class ImageUtils {


    public static Bitmap cropCenter(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    // https://stackoverflow.com/questions/3373860/convert-a-bitmap-to-grayscale-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //https://stackoverflow.com/questions/15759195/reduce-size-of-bitmap-to-some-specified-pixel-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {

//        final int IMAGE_SIZE = bitmapWidth;
//        boolean landscape = bitmap.getWidth() > bitmap.getHeight();
//
//        Bitmap tempBitmap = bitmap;
//
//        float scale_factor;
//        if (landscape) scale_factor = (float)IMAGE_SIZE / bitmap.getHeight();
//        else scale_factor = (float)IMAGE_SIZE / bitmap.getWidth();
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale_factor, scale_factor);
//
//        Bitmap croppedBitmap;
//        if (landscape){
//            int start = (tempBitmap.getWidth() - tempBitmap.getHeight()) / 2;
//            croppedBitmap = Bitmap.createBitmap(tempBitmap, start, 0, tempBitmap.getHeight(), tempBitmap.getHeight(), matrix, true);
//        } else {
//            int start = (tempBitmap.getHeight() - tempBitmap.getWidth()) / 2;
//            croppedBitmap = Bitmap.createBitmap(tempBitmap, 0, start, tempBitmap.getWidth(), tempBitmap.getWidth(), matrix, true);
//        }
//        return croppedBitmap;

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(image, bitmapWidth, bitmapHeight);
        return bitmap;
//        Bitmap bitmap = Bitmap.createBitmap(
//                image,
//                image.getHeight()/2 - image.getWidth()/2,
//                0,
//                image.getWidth(),
//                image.getWidth()
//        );
        //return Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
    }

}
