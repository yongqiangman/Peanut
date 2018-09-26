package com.yqman.evan.network.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.yqman.evan.network.util.DrawableFactory;
import com.yqman.network.R;

import okhttp3.Callback;
import okhttp3.ResponseBody;

/**
 * Created by yqman on 2016/9/20.
 */
public class OkHttpImageTask implements ImageTask, Callback {
    private WeakReference<ImageView> mImageViewSoftReferences;
    private okhttp3.Call mCall;
    private String mUrl;
    private int mSample;  //图片压缩比例；设置为1则压缩到手机屏幕大小，值小于1则不压缩
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public OkHttpImageTask(okhttp3.Call call, ImageView imageView, String url) {
        this(call, imageView, url, 1);
    }

    public OkHttpImageTask(okhttp3.Call call, ImageView imageView, String url, int sample) {
        this.mCall = call;
        this.mImageViewSoftReferences = new WeakReference<ImageView>(imageView);
        this.mUrl = url;
        this.mSample = sample;
    }

    @Override
    public void cancelTask() {
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }
    }

    @Override
    public boolean needTask(String url) {
        if (this.mUrl.equals(url)) {
            return false;
        } else {
            cancelTask();
            return true;
        }
    }

    @Override
    public ImageView getImageView() {
        return mImageViewSoftReferences.get();
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
                SetBitmap setBitmap = new SetBitmap(imageView, new DrawableFactory(imageView.getContext())
                        .getBitmapWithSample(bytes, mSample, mSample));
                HANDLER.post(setBitmap);
            }
        }
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