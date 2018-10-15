package com.yqman.network;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.yqman.network.cookie.CookieManager;
import com.yqman.network.cookie.PersistentCookieStore;
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
public class OkHttpInstance implements INetworkTask {
    private static final String TAG = "OkHttpInstance";
    private static final boolean IS_NEED_INTERCEPT = false;
    /**
     * 最大缓存大小
     */
    private static final int MAX_CACHE_SIZE = 100 * 1024 * 1024;

    private OkHttpClient mOkHttpClient;

    public OkHttpInstance(String dirPath) throws IllegalArgumentException {
        this(new File(dirPath), new PersistentCookieStore(new File(dirPath, "cookie").getAbsolutePath()));
    }

    public OkHttpInstance(File cacheFile, PersistentCookieStore persistentCookieStore) {
        File dirFile = new File(cacheFile, TAG);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(dirFile, MAX_CACHE_SIZE));
        if (IS_NEED_INTERCEPT) {
            builder.interceptors().add(getIntercept()); //如果是添加在networkInterceptors()中那么请求报文会有两个User-Agent域
        }
        builder.cookieJar(new CookieManager(persistentCookieStore)); //cookie自动管理器
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
            okhttp3.Call call = getGetRequest(url, params, headerMap);
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
            okhttp3.Call call = getPostRequest(url, params, headerMap, contentType);
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
        MediaType MEDIA_TYPE_PNG = MediaType.parse(FileTools.getTypeForName(file.getName()));
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file); //创建一个okhttp3.RequestBody
        Request.Builder builder = getGetBuilder(url, params, headerMap);
        builder.post(requestBody);
        okhttp3.Call call = mOkHttpClient.newCall(builder.build());
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
        okhttp3.Call call = getGetRequest(url, params, headerMap);
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

    private Call getPostRequest(String url, @NonNull HashMap<String, String> requestParam,
                                HashMap<String, String> headerMap,
                                String contentType) {
        Request request = getPostBuilder(url, requestParam, headerMap, contentType).build();
        return mOkHttpClient.newCall(request);
    }

    /**
     * 获取到一个get请求，并将url和Call存入集合中
     */
    protected Call getGetRequest(String url) {
        return getGetRequest(url, null);
    }

    protected Call getGetRequest(String url, @Nullable HashMap<String, String> requestParam) {
        return getGetRequest(url, requestParam, null);
    }

    protected Call getGetRequest(String url, @Nullable HashMap<String, String> requestParam,
                               HashMap<String, String> headerMap) {
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
}