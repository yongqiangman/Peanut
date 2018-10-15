package com.yqman.network.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.yqman.network.R;

import okhttp3.Callback;
import okhttp3.ResponseBody;

/**
 * Created by yqman on 2016/9/20.
 */
class OkHttpImageTask implements Callback {
    private WeakReference<ImageView> mImageViewSoftReferences;
    private int mSample;  //图片压缩比例；设置为1则压缩到手机屏幕大小，值小于1则不压缩
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private final int mScreenWidth;
    private final int mScreenHeight;

    OkHttpImageTask(ImageView imageView) {
        this(imageView, 1);
    }

    OkHttpImageTask(ImageView imageView, int sample) {
        this.mImageViewSoftReferences = new WeakReference<ImageView>(imageView);
        this.mSample = sample;
        Point outSize = new Point();
        WindowManager wm = (WindowManager) imageView.getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null && wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getSize(outSize);
        }
        mScreenHeight = outSize.y;
        mScreenWidth = outSize.x;
    }

    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
        final ImageView imageView;
        if ((imageView = mImageViewSoftReferences.get()) != null) {
            SetBitmap setBitmap = new SetBitmap(imageView, R.drawable.net_image_loading);
            HANDLER.post(setBitmap);
        }
    }

    @Override
    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
        final ImageView imageView;
        if ((imageView = mImageViewSoftReferences.get()) != null) {
            ResponseBody body = response.body();
            if (body != null) {
                byte[] bytes = body.bytes();
                SetBitmap setBitmap = new SetBitmap(imageView, getBitmapWithSample(bytes, mSample, mSample));
                HANDLER.post(setBitmap);
            }
        }
    }

    /**
     * @param bytes        需要解码bytes
     * @param widthSample  值如果小于1那么证明宽度是不需要压缩的
     * @param heightSample 值如果小于1那么证明高度是不需要压缩的
     */
    private Bitmap getBitmapWithSample(byte[] bytes, int widthSample, int heightSample) {
        if (widthSample <= 1 && heightSample <= 1) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inSampleSize =
                    calculateInSampleSize(options, mScreenWidth / widthSample, mScreenHeight / heightSample);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
    }

    /**
     * 根据预期的宽度和高度 计算需要压缩多少倍
     * 最终得到的的高度和宽度两者都不能大于预期的高度和宽度
     *
     * @param options   处理对象
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     *
     * @return 压缩比
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private class SetBitmap implements Runnable {
        private ImageView imageView;
        private Bitmap bitmap;
        private int resId = -1;

        SetBitmap(ImageView imageView, Bitmap bitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
        }

        SetBitmap(ImageView imageView, int resId) {
            this.imageView = imageView;
            this.resId = resId;
        }

        @Override
        public void run() {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else if (resId != -1) {
                imageView.setImageResource(resId);
            }
        }
    }
}