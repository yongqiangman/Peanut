package com.yqman.library.ui.receiver;



import android.app.Activity;

import com.netdisk.library.threadscheduler.android.ResultView;

/**
 * Created by manyongqiang on 2018/2/5.
 */

public abstract class BaseResultView extends ResultView {

    public BaseResultView(Activity activity) {
        super(activity);
    }

    @Override
    protected final FailedViewManager initFailedViewManager(Activity activity) {
        FailedViewManager manager = new FailedViewManager(activity);
        manager.setDefaultErrorView(new FailedViewManager.FailedViewBean());
        return manager;
    }

}
