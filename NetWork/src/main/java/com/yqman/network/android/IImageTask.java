package com.yqman.network.android;

import android.widget.ImageView;

/**
 * Created by manyongqiang on 2018/2/5.
 */

public interface IImageTask {
    void setImage(String url, ImageView imageView, int errResId, int defaultResId);
}
