package com.yqman.evan.network.task;

import android.widget.ImageView;

/**
 * Created by yqman on 2016/9/20.
 */
public interface ImageTask {
    ImageView getImageView();

    boolean needTask(String url);

    void cancelTask();
}