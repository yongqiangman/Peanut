package com.yqman.network.android.cookie;

import java.util.ArrayList;
import java.util.List;

import com.yqman.network.cookie.PersistentCookieStore;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class AndroidPersistentCookieStore extends PersistentCookieStore {
    public AndroidPersistentCookieStore(Context context, String dirPath) throws IllegalArgumentException {
        super(dirPath);
        initWebViewCookieManager(context);
    }

    @Override
    protected void addCookie(HttpUrl url, Cookie cookie) {
        addCookieToWebView(url, cookie);
    }

    @Override
    protected List<Cookie> getCookie(HttpUrl url) {
        return getWebViewCookie(url);
    }

    /**
     * 下面的方法是将OkHttp的Cookie同步到WebView那边去。也就是实现了okhttp端的登录，在webView可以直接利用这个登录的结果！！
     * 后面的String类型内容大体如：nforum[PASSWORD]=VCKDSGKjUg52te4omvy04Q%3D%3D; expires=Sat, 15 Jul 2017 10:54:52 GMT;
     * domain=m.byr.cn; path=/
     * webkitCookieManager内部会对这个字符串进行解析：
     * 1、取出nforum[PASSWORD]=VCKDSGKjUg52te4omvy04Q%3D%3D;键值对信息前者为key 后者为value
     * 2、根据键值对后面的信息做出额外的处于，如过期性检查
     * 存储过程是针对同样的url.host()，如果key对应的cookie已经存在则更新value，否则添加
     *
     * @param url    sd
     * @param cookie sd
     */
    private void addCookieToWebView(HttpUrl url, Cookie cookie) {
        CookieManager webkitCookieManager = CookieManager.getInstance();//webView的Cookie管理
        webkitCookieManager.setCookie(url.host(), cookie.toString());
    }

    /**
     * 将webView中有的Cookie而ret中没有的Cookie，存入ret中
     * 返回内容如：Hm_lvt_a2cb7064fdf52fd51306dd6ef855264a=1468579102;
     * Hm_lpvt_a2cb7064fdf52fd51306dd6ef855264a=1468579102; nforum[UTMPUSERID]=evanman; nforum[UTMPKEY]=40216954;
     *
     * @param url s
     */
    private List<Cookie> getWebViewCookie(HttpUrl url) {
        CookieManager webkitCookieManager = CookieManager.getInstance();//webView的Cookie管理
        String data = webkitCookieManager.getCookie(url.host());
        if (data == null || data.isEmpty()) {
            return null;
        }
        ArrayList<Cookie> ret = new ArrayList<>();
        String[] cookies = data.split("; ");
        for (String c : cookies) {
            String[] str = c.split("=");
            if (str.length >= 2) {
                if (!isContains(str[0], ret)) { //不存在则添加
                    Cookie.Builder builder = new Cookie.Builder();
                    builder.name(str[0]);
                    builder.value(str[1]);
                    builder.domain(url.host());
                    Cookie cookie = builder.build();
                    ret.add(cookie);
                }
            }
        }
        return ret;
    }

    private boolean isContains(String name, ArrayList<Cookie> ret) {
        if (ret == null) {
            return false;
        }
        for (Cookie cookie : ret) {
            if (name.equals(cookie.name())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置WebView了能够接收处理Cookie信息
     */
    private void initWebViewCookieManager(Context context) {
        CookieSyncManager.createInstance(context.getApplicationContext());
        //webView的Cookie管理
        CookieManager.getInstance().setAcceptCookie(true); //webView的Cookie管理
    }
}
