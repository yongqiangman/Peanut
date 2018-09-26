package com.yqman.monitor;

import android.util.Log;

/**
 * Created by manyongqiang on 2017/11/27.
 *
 */

public class LogHelper {
    private static final String TAG = "LogHelper";
    private static final boolean IS_DEBUG = true;
    private final static String PACKAGE_NAME = "com.yqman.evan";

    public static boolean isDebug() {
        return IS_DEBUG;
    }

    public static void v(String tag, String message) {
        if (IS_DEBUG) {
            Log.v(TAG, getDetailTag(tag) + ":" + message);
        }
    }

    public static void d(String tag, String message) {
        if (IS_DEBUG) {
            Log.d(TAG, getDetailTag(tag) + ":" + message);
        }
    }

    public static void i(String tag, String message) {
        if (IS_DEBUG) {
            Log.i(TAG, getDetailTag(tag) + ":" + message);
        }
    }

    public static void w(String tag, String message) {
        if (IS_DEBUG) {
            Log.w(TAG, getDetailTag(tag) + ":" + message);
        }
    }

    public static void e(String tag, String message) {
        if (IS_DEBUG) {
            Log.e(TAG, getDetailTag(tag) + ":" + message);
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if (IS_DEBUG) {
            Log.e(TAG, getDetailTag(tag) + ":" + message + ":" + e.getMessage());
        }
    }

    private static String getDetailTag(String tag) {
        return Thread.currentThread().getName() + "[" + getCaller() + "]" + ":" + tag;
    }

    private static String getCaller() {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final StringBuilder result = new StringBuilder();
        for (StackTraceElement ste : stack) {
            final String steString = ste.toString();
            if (android.text.TextUtils.isEmpty(steString) || !steString.contains(PACKAGE_NAME) || steString
                    .contains(TAG)) {
                continue;
            }
            result.append(steString.replace(PACKAGE_NAME, ""));
            break;
        }
        return result.toString();
    }
}
