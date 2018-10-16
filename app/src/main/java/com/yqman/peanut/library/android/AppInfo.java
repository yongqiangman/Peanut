package com.yqman.peanut.library.android;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by manyongqiang on 2018/2/5.
 */

public class AppInfo {
    private static final String TAG = "AppCommon";
    /**
     * 包名
     */
    private static String PACKAGE_NAME;

    /**
     * 版本号信息
     */
    private static String VERSION_DEFINED;

    /**
     * 存储的目录
     */
    private static String CACHE_DIR_PATH;

    private static volatile boolean mIsInited = false;

    public static void init(Application application) {
        PACKAGE_NAME = application.getPackageName();
        String versionName = "";
        try {
            final PackageManager pm = application.getPackageManager();
            final PackageInfo pi = pm.getPackageInfo(application.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (final Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (!TextUtils.isEmpty(versionName)) {
            VERSION_DEFINED = versionName;
        }
        CACHE_DIR_PATH = application.getCacheDir().getPath();
        mIsInited = true;
    }

    public static String getVersionInfo() {
        if (!mIsInited) {
            Log.d(TAG, "has not init in Application onCreate method");
        }
        return VERSION_DEFINED;
    }

    public static String getPackageName() {
        if (!mIsInited) {
            Log.d(TAG, "has not init in Application onCreate method");
        }
        return PACKAGE_NAME;
    }

    public static String getCacheDirPath() {
        if (!mIsInited) {
            Log.d(TAG, "has not init in Application onCreate method");
        }
        return CACHE_DIR_PATH;
    }

}
