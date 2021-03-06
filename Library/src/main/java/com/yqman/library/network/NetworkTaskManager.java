package com.yqman.library.network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.yqman.network.INetworkTask;
import com.yqman.network.R;
import com.yqman.network.android.AndroidOkHttpInstance;
import com.yqman.network.android.IImageTask;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by manyongqiang on 2018/2/4.
 * 网络相关处理
 */

public class NetworkTaskManager {

    private static final String TAG = "NetworkTaskManager";
    private INetworkTask mNetworkTask;
    private IImageTask mImageTask;
    private static NetworkTaskManager mInstance;

    public static NetworkTaskManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized(NetworkTaskManager.class) {
                if (mInstance == null) {
                    mInstance = new NetworkTaskManager();
                    try {
                        final AndroidOkHttpInstance instance = new AndroidOkHttpInstance(context,
                                context.getCacheDir().getAbsolutePath());
                        mInstance.mNetworkTask = instance;
                        mInstance.mImageTask = instance;
                    } catch (IllegalArgumentException e) {
                        Log.d(TAG, "e:" + e.getMessage());
                    }
                }
            }
        }
        return mInstance;
    }

    public String sendGetRequest(String url) {
        return sendGetRequest(url, null);
    }

    public String sendGetRequest(String url, HashMap<String, String> params) {
        return sendGetRequest(url, params, null);
    }

    public String sendGetRequest(String url, HashMap<String, String> params, HashMap<String, String> headerMap) {
        if (isNonUIThread()) {
            return mNetworkTask.sendGetRequest(url, params, addCommonParams(headerMap));
        } else {
            Log.e(TAG, "can not access network on ui thread");
            return null;
        }
    }

    public String sendPostRequest(String url, HashMap<String, String> params) {
        return sendPostRequest(url, params, null);
    }

    public String sendPostRequest(String url, HashMap<String, String> params, HashMap<String, String> headerMap) {
        return sendPostRequest(url, params, headerMap, null);
    }

    public String sendPostRequest(String url, HashMap<String, String> params, HashMap<String, String> headerMap,
                                  String contentType) {
        if (isNonUIThread()) {
            return mNetworkTask.sendPostRequest(url, params, addCommonParams(headerMap), contentType);
        } else {
            Log.e(TAG, "can not access network on ui thread");
            return null;
        }
    }

    public boolean uploadFile(String url, HashMap<String, String> params, HashMap<String, String> headerMap, File file) {
        if (isNonUIThread()) {
            return mNetworkTask.uploadFile(url, params, addCommonParams(headerMap), file);
        } else {
            Log.e(TAG, "can not access network on ui thread");
            return false;
        }
    }

    public boolean downloadFile(String url, HashMap<String, String> params, HashMap<String, String> headerMap, File file) {
        if (isNonUIThread()) {
            return mNetworkTask.downloadFile(url, params, addCommonParams(headerMap), file);
        } else {
            Log.e(TAG, "can not access network on ui thread");
            return false;
        }
    }

    public void setImageResource(String url, ImageView image) {
        setImageResource(url, image, R.drawable.net_image_loading, R.drawable.net_image_loading);
    }

    public void setImageResource(String url, ImageView imageView, @NonNull int errResId, @NonNull int defaultResId) {
        mImageTask.setImage(url, imageView, errResId, defaultResId);
    }

    private boolean isNonUIThread() {
        return Looper.myLooper() != Looper.getMainLooper();
    }

    /**
     * 添加通用参数
     */
    private HashMap<String, String> addCommonParams(HashMap<String, String> headerMap) {
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        if (!headerMap.containsKey("User-Agent")) {
            headerMap.put("User-Agent", getUserAgent());
        }
        if (!headerMap.containsKey("Content-Type")) {
            headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        }
        return headerMap;
    }

    private static String getUserAgent() {
        final String split = ";";
        StringBuilder sb = new StringBuilder();
        sb.append("netdisk");
        sb.append(split);
        sb.append("8.8.0");
        try {
            String device = null;
            device = URLEncoder.encode(Build.MODEL, "UTF-8");
            sb.append(split);
            sb.append(device);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "un support encode");
        }
        try {
            String android = null;
            android = URLEncoder.encode(Build.VERSION.RELEASE, "UTF-8");
            sb.append(split);
            sb.append("android");
            sb.append("-");
            sb.append("android");
            sb.append(split);
            sb.append(android);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "un support encode");
        }
        // netdisk;7.0.0;Nexus 4;android-android;4.0.2;JSbridge1.0.0
        return sb.toString();
    }
}
