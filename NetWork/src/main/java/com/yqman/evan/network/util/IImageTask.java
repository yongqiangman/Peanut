package com.yqman.evan.network.util;

import android.widget.ImageView;

/**
 * Created by manyongqiang on 2018/2/5.
 */

public interface IImageTask {
    void setImage(String url, ImageView imageView, int errResId, int defaultResId);

    void cancelAllImageRequest();
}
