package com.yqman.network.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by yqman on 2016/7/7.
 */
public class CookieManager implements CookieJar {
    private final PersistentCookieStore cookieStore;

    public CookieManager(PersistentCookieStore persistentCookieStore) {
        cookieStore = persistentCookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookieStore != null) {
            cookieStore.put(url, cookies);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (cookieStore != null) {
            return cookieStore.get(url);
        }
        return null;
    }
}