package com.example.finalyearproject.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

public class SizeUtils {

    /* convert dp value to pixel value */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /* calculate inSampleSize from src image size to dst required size */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int inSampleSize = 1;
        // if reqSize < srcSize, no inSample, return 1;
        if (srcWidth <= reqWidth || srcHeight <= reqHeight){
            return inSampleSize;
        }
        // half first to keep srcWidth/inSampleSize > reqWidth;
        srcHeight /= 2;
        srcWidth /= 2;
        while ((srcWidth / inSampleSize > reqWidth)
                && (srcHeight / inSampleSize > reqHeight)) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
