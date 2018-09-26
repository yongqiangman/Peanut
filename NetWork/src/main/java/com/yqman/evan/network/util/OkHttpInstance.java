package com.yqman.evan.network.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.yqman.evan.network.cookie.CookieManager;
import com.yqman.evan.network.task.ImageTask;
import com.yqman.evan.network.task.OkHttpImageTask;
import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.IDirectoryVisitor;
import com.yqman.persistence.file.LocalDirectory;
import com.yqman.persistence.android.FileTools;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by yqman on 2016/6/6.
 * 当前类用于访问网络资源 其中XXAbsoluteUrl是可以通用的方法
 * 本类除了对image加载照片之外，其余所有方法都是同步请求因此需要在给UI线程中执行，UI中可以配套使用RxJava
 * <p>
 * okhttp支持对使用CA机构颁发的证书的https网址进行访问，别的机构需要其它操作
 */
class OkHttpInstance implements INetworkTask, IImageTask {
    private static final String TAG = "OkHttpInstance";
    private static final boolean IS_NEED_INTERCEPT = false;
    /**
     * 最大缓存大小
     */
    private static final int MAX_CACHE_SIZE = 100 * 1024 * 1024;
    private int mMaxTaskCount = 35;
    private int mMaxImageTaskCount = 100;

    private OkHttpClient mOkHttpClient;
    private LinkedHashMap<String, WeakReference<okhttp3.Call>> mRunningTask = new LinkedHashMap<>();
    private LinkedHashMap<WeakReference<ImageView>, ImageTask> mRunningImageTasks = new LinkedHashMap<>();

    OkHttpInstance(Context context, final int maxTaskCount, final int maxImageTaskCount) {
        mMaxTaskCount = maxTaskCount;
        mMaxImageTaskCount = maxImageTaskCount;
        File dirFile = new File(context.getCacheDir(), TAG);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(dirFile, MAX_CACHE_SIZE));
        if (IS_NEED_INTERCEPT) {
            builder.interceptors().add(getIntercept()); //如果是添加在networkInterceptors()中那么请求报文会有两个User-Agent域
        }
        try {
            IDirectoryVisitor directoryVisitor = new LocalDirectory(new File(context.getCacheDir(), TAG));
            builder.cookieJar(new CookieManager(context, directoryVisitor)); //cookie自动管理器
        } catch (FileAccessErrException e) {
            // do nothing
        }
        mOkHttpClient = builder.build();
    }

    /**
     * 获取过滤器
     */
    private Interceptor getIntercept() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder newRequest = chain.request().newBuilder();
                String usrAgent = chain.request().header("User-Agent");
                if (usrAgent == null || usrAgent.equals("")) {
                    newRequest.addHeader("User-Agent",
                            "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, "
                                    + "like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
                    //CMWAP接入方式下User-Agent会被网关过滤，这里使用了Iphone6的chrome配置，其它手机基本上也没啥大问题
                }
                return chain.proceed(newRequest.build());
            }
        };
    }

    @Override
    public String sendGetRequest(String url, HashMap<String, String> params, HashMap<String, String> headerMap) {
        try {
            checkRunningTaskSize();
            okhttp3.Call call = getGetRequest(url, params, headerMap);
            addRunningTask(url, call);
            okhttp3.Response response = call.execute();
            return getResponseString(response);
        } catch (IOException e) {
            Log.d(TAG, "error=" + e.getMessage());
        }
        return null;
    }

    @Override
    public String sendPostRequest(String url, HashMap<String, String> params, HashMap<String, String> headerMap,
                                  String contentType) {
        try {
            checkRunningTaskSize();
            okhttp3.Call call = getPostRequest(url, params, headerMap, contentType);
            addRunningTask(url, call);
            okhttp3.Response response = call.execute();
            return getResponseString(response);
        } catch (IOException e) {
            Log.d(TAG, "error=" + e.getMessage());
        }
        return null;
    }

    private String getResponseString(okhttp3.Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        try {
            return body.string();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean uploadFile(String url, HashMap<String, String> params, HashMap<String, String> headerMap,
                              File file) {
        checkRunningTaskSize();
        MediaType MEDIA_TYPE_PNG = MediaType.parse(FileTools.getTypeForName(file.getName()));
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file); //创建一个okhttp3.RequestBody
        Request.Builder builder = getGetBuilder(url, params, headerMap);
        builder.post(requestBody);
        okhttp3.Call call = mOkHttpClient.newCall(builder.build());
        addRunningTask(url, call);
        try {
            okhttp3.Response response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            Log.d(TAG, "error=" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean downloadFile(String url, HashMap<String, String> params, HashMap<String, String> headerMap,
                                File file) {
        checkRunningTaskSize();
        okhttp3.Call call = getGetRequest(url, params, headerMap);
        addRunningTask(url, call);
        try {
            okhttp3.Response response = call.execute();
            if (!response.isSuccessful()) {
                return false;
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return false;
            }
            BufferedSource source = Okio.buffer(responseBody.source());
            BufferedSink sink = Okio.buffer(Okio.sink(new FileOutputStream(file)));
            source.readAll(sink);
            sink.flush();
            sink.close();
            source.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    @Override
    public void cancelRequest(String url) {
        if (mRunningTask.get(url) != null) {
            if (mRunningTask.get(url).get() != null) {
                mRunningTask.get(url).get().cancel();
            }
            mRunningTask.remove(url);
        }
    }

    @Override
    public void cancelAll() {
        for (String url : mRunningTask.keySet()) {
            cancelRequest(url);
        }
    }

    @Override
    public void setImage(String url, ImageView imageView, int errResId, int defaultResId) {
        WeakReference<ImageView> imageTaskWeakReference = new WeakReference<ImageView>(imageView);
        if (mRunningImageTasks.get(imageTaskWeakReference) != null
                && !mRunningImageTasks.get(imageTaskWeakReference).needTask(url)) {
            return;
        }
        imageView.setImageResource(defaultResId);
        if (url == null || url.isEmpty()) {
            return;
        }
        okhttp3.Call call = getGetRequest(url);
        OkHttpImageTask okHttpImageTask = new OkHttpImageTask(call, imageView, url);
        addImageTask(imageTaskWeakReference, okHttpImageTask);
        call.enqueue(okHttpImageTask);
    }

    @Override
    public void cancelAllImageRequest() {
        while (!mRunningImageTasks.isEmpty()) {
            Iterator<Map.Entry<WeakReference<ImageView>, ImageTask>> iterator =
                    mRunningImageTasks.entrySet().iterator();
            Map.Entry<WeakReference<ImageView>, ImageTask> entry = iterator.next();
            entry.getValue().cancelTask();
            mRunningImageTasks.remove(entry.getKey());
        }
    }

    private void addImageTask(WeakReference<ImageView> imageView, ImageTask imageTask) {
        if (mRunningImageTasks.get(imageView) != null) {
            mRunningImageTasks.put(imageView, imageTask);
        }
        while (mRunningImageTasks.size() > mMaxTaskCount) {
            Iterator<Map.Entry<WeakReference<ImageView>, ImageTask>> iterator =
                    mRunningImageTasks.entrySet().iterator();
            Map.Entry<WeakReference<ImageView>, ImageTask> entry = iterator.next();
            entry.getValue().cancelTask();
            mRunningImageTasks.remove(entry.getKey());
        }
        mRunningImageTasks.put(imageView, imageTask);
    }

    private Call getPostRequest(String url, @NonNull HashMap<String, String> requestParam,
                                HashMap<String, String> headerMap,
                                String contentType) {
        cancelAlreadyTask(url);
        Request request = getPostBuilder(url, requestParam, headerMap, contentType).build();
        return mOkHttpClient.newCall(request);
    }

    /**
     * 获取到一个get请求，并将url和Call存入集合中
     */
    private Call getGetRequest(String url) {
        return getGetRequest(url, null);
    }

    private Call getGetRequest(String url, @Nullable HashMap<String, String> requestParam) {
        return getGetRequest(url, requestParam, null);
    }

    private Call getGetRequest(String url, @Nullable HashMap<String, String> requestParam,
                               HashMap<String, String> headerMap) {
        cancelAlreadyTask(url);
        Request request = getGetBuilder(url, requestParam, headerMap).build();
        return mOkHttpClient.newCall(request);
    }

    private Request.Builder getGetBuilder(String url, @Nullable HashMap<String, String> requestParam, HashMap<String,
            String> headerMap) {
        Request.Builder builder = new Request.Builder();
        if (headerMap != null) {
            for (Map.Entry<String, String> element : headerMap.entrySet()) {
                builder.addHeader(element.getKey(), element.getValue());
            }
        }
        if (requestParam != null && !requestParam.isEmpty()) {
            StringBuilder params = null;
            for (Map.Entry<String, String> element : requestParam.entrySet()) {
                if (params == null) {
                    params = new StringBuilder();
                    params.append(element.getKey()).append("=").append(Uri.encode(element.getValue()));
                } else {
                    params.append("&").append(element.getKey()).append("=").append(Uri.encode(element.getValue()));
                }
            }
            if (params != null) {
                // 转换成url参数字符串
                if (!url.contains("?")) {
                    url += "?";
                } else if (!url.endsWith("?")) {
                    url += "&";
                }
                url += params.toString();
            }
        }
        builder.url(url);
        return builder;
    }

    private Request.Builder getPostBuilder(String url, @Nullable HashMap<String, String> requestParam, HashMap<String,
            String> headerMap, String contentType) {
        Request.Builder builder = new Request.Builder();
        if (headerMap != null) {
            for (Map.Entry<String, String> element : headerMap.entrySet()) {
                builder.addHeader(element.getKey(), element.getValue());
            }
        }
        builder.url(url);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> element : requestParam.entrySet()) {
            stringBuilder.append(element.getKey());
            stringBuilder.append("=");
            stringBuilder.append(element.getValue());
            stringBuilder.append("&");
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        MediaType mediaType = null;
        if (contentType != null && !contentType.isEmpty()) {
            mediaType = MediaType.parse(contentType);
        }
        RequestBody requestBody = RequestBody.create(mediaType, stringBuilder.toString());
        builder.post(requestBody);
        return builder;
    }

    private void cancelAlreadyTask(String url) {
        if (mRunningTask.get(url) != null) {
            okhttp3.Call call = mRunningTask.get(url).get();
            if (call != null) {
                call.cancel();
            }
        }
    }

    private void addRunningTask(String url, Call call) {
        mRunningTask.put(url, new WeakReference<>(call));
    }

    private void checkRunningTaskSize() {
        while (mRunningTask.size() > mMaxImageTaskCount) {
            Map.Entry<String, WeakReference<Call>> set = mRunningTask.entrySet().iterator().next();
            if (set.getValue().get() != null) {
                set.getValue().get().cancel();
            }
            mRunningTask.remove(set.getKey());
        }
    }

}