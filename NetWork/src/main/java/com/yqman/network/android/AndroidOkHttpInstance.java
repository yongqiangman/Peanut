package com.yqman.network.android;

import java.io.File;

import com.yqman.network.OkHttpInstance;
import com.yqman.network.android.cookie.AndroidPersistentCookieStore;

import android.content.Context;
import android.widget.ImageView;

public class AndroidOkHttpInstance extends OkHttpInstance implements IImageTask {

    public AndroidOkHttpInstance(Context context, String dirPath) throws IllegalArgumentException {
        super(new File(dirPath),
                new AndroidPersistentCookieStore(context, new File(dirPath, "cookie").getAbsolutePath()));
    }

    @Override
    public void setImage(String url, ImageView imageView, int errResId, int defaultResId) {
        imageView.setImageResource(defaultResId);
        if (url == null || url.isEmpty()) {
            return;
        }
        okhttp3.Call call = getGetRequest(url);
        OkHttpImageTask okHttpImageTask = new OkHttpImageTask(imageView);
        call.enqueue(okHttpImageTask);
    }
}
