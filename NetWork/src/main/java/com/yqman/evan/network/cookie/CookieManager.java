package com.yqman.evan.network.cookie;

import java.util.List;

import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.IDirectoryVisitor;

import android.content.Context;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by yqman on 2016/7/7.
 */
public class CookieManager implements CookieJar {
    private final PersistentCookieStore cookieStore;

    public CookieManager(Context context, IDirectoryVisitor visitor) {
        PersistentCookieStore store = null;
        try {
            store = new PersistentCookieStore(context, visitor);
        } catch (FileAccessErrException e) {
            // "do not handle"
        } finally {
            cookieStore = store;
        }
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