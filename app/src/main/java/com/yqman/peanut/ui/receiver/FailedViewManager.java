package com.yqman.peanut.ui.receiver;

import java.lang.ref.WeakReference;

import com.yqman.android.scheduler.receiver.IFailedManager;
import com.yqman.wdiget.CustomDialog;
import com.yqman.wdiget.ToastHelper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

/**
 * Created by manyongqiang on 2017/10/20.
 * 请求错误视图控制器
 */

public class FailedViewManager implements IFailedManager {
    private static final Integer NETWORK_ERROR = Integer.MAX_VALUE;
    private static final Integer DEFAULT_ERROR = Integer.MAX_VALUE - 1;
    private SparseArray<FailedViewBean> maps = new SparseArray<>();

    private WeakReference<Activity> mActivityWeakRef;

    FailedViewManager(@NonNull Activity activity) {
        mActivityWeakRef = new WeakReference<Activity>(activity);
    }

    public void put(int errno, FailedViewBean bean) {
        maps.append(errno, bean);
    }

    public void setNetworkErrorView(@NonNull FailedViewBean bean) {
        maps.append(NETWORK_ERROR, bean);
    }

    public void setDefaultErrorView(@NonNull FailedViewBean bean) {
        maps.append(DEFAULT_ERROR, bean);
    }

    @Override
    public final void showNetWorkErrorView(String contentMsg) {
        FailedViewBean failedViewBean = maps.get(NETWORK_ERROR);
        if (failedViewBean == null) {
            showDefaultErrorView(contentMsg);
        } else {
            show(failedViewBean, contentMsg);
        }
    }

    @Override
    public final void showServerErrorView(int errno, @Nullable String contentMsg) {
        FailedViewBean failedViewBean = maps.get(errno);
        if (failedViewBean == null) {
            showDefaultErrorView(contentMsg);
            return;
        }
        show(failedViewBean, contentMsg);
    }

    private void showDefaultErrorView(@Nullable String contentMsg) {
        FailedViewBean failedViewBean = maps.get(DEFAULT_ERROR);
        if (failedViewBean == null) {
            return;
        }
        show(failedViewBean, contentMsg);
    }

    private void show(@NonNull FailedViewBean failedViewBean, @Nullable String contentMsg) {
        Activity activity = getActivity();
        CustomDialog.Builder builder = failedViewBean.builder;
        if (builder != null && activity != null) {
            builder.setActivity(activity);
            builder.setContentText(contentMsg);
            builder.show();
        } else {
            if (!TextUtils.isEmpty(contentMsg)) {
                ToastHelper.showToast(activity, contentMsg);
            }
        }
    }

    @Override
    public final Activity getActivity() {
        Activity activity = mActivityWeakRef.get();
        if (activity != null && !activity.isFinishing()) {
            return activity;
        }
        return null;
    }

    static class FailedViewBean {
        private CustomDialog.Builder builder;

        FailedViewBean() {
            this(null);
        }

        FailedViewBean(CustomDialog.Builder builder) {
            this.builder = builder;
        }
    }

}