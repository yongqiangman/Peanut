package com.yqman.evan.network.cookie;

import android.content.Context;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.IDirectoryVisitor;

import android.util.Log;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by yqman on 2016/7/7.
 * 数据持久化的类，接收url和对应的Cookie数据，将其持久化进入磁盘中
 * 对外提供的方法有get、put、remove三个方法
 */
public class PersistentCookieStore {
    private static final String TAG = "PersistentCookieStore";
    private ReentrantLock mLock = new ReentrantLock();
    private final Map<String, ConcurrentHashMap<String, Cookie>> mCookies; //内存 String url使用的host就是同一个网站下链接共享一个cookie
    private final CookieConfig mConfig;

    PersistentCookieStore(Context context, IDirectoryVisitor directoryVisitor) throws FileAccessErrException {
        initWebViewCookieManager(context); //WebView的cookie进行初始化
        mConfig = CookieConfig.getInstance(directoryVisitor);
        mCookies = new HashMap<>();
        //磁盘数据恢复到内存中
        addFakeCookie();
        Set<Map.Entry<Object, Object>> entrySets = mConfig.getEntrySet();
        if (entrySets == null || entrySets.isEmpty()) {
            Log.d(TAG, "no data");
            return;
        }
        for (Map.Entry<Object, Object> entry : entrySets) {
            String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
            for (String name : cookieNames) {
                String encodedCookie = mConfig.getString(name, null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        if (!mCookies.containsKey((String) entry.getKey())) {
                            mCookies.put((String) entry.getKey(), new ConcurrentHashMap<String, Cookie>());
                        }
                        mCookies.get(entry.getKey()).put(name, decodedCookie);
                    }
                }
            }
        }
    }

    private String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    } //返回的值作为该cookie对应的key！

    /**
     * 这里获得的cookie主要是针对http请求报文head中的 Set-Cookie域
     * 比如Set-Cookie: t=B28D5A446447C49C7D7BD5F504FD2D1B; Domain=nowcoder.com; Expires=Fri, 14-Oct-2016 08:09:02 GMT;
     * Path=/
     * 就会转化成一个Cookie对象其中toString为：t=B28D5A446447C49C7D7BD5F504FD2D1B; Domain=nowcoder.com; Expires=Fri, 14-Oct-2016
     * 08:09:02 GMT; Path=/
     *
     * @param url     s
     * @param cookies s
     */
    void put(HttpUrl url, List<Cookie> cookies) {
        mLock.lock();
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                add(url, item);
            }
            mConfig.commit();
        }
        mLock.unlock();
    }

    /**
     * 添加虚假的cookie信息; 正常的情况不应该走这个逻辑
     */
    private void addFakeCookie() {
        HttpUrl url = HttpUrl.parse("https://pan.baidu.com");
        List<Cookie> cookies = new ArrayList<>();
        Cookie cookie = new Cookie.Builder()
                .domain("baidu.com").name("BDUSS")
                .value("S1IcXhqQUdBb0ZPT0t4NzUtM1BMWkttV0lZOEVNSVo0RXRMeU1XWWZSaFJnSjlhQVFBQUFBJCQAAAAAAAAAAAEAAAAO8VXIZ2lfbWFvAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFHzd1pR83daV")
                .build();
        if (!mConfig.has(getCookieToken(cookie))) {
            cookies.add(cookie);
        }
        cookie = new Cookie.Builder()
                .domain("baidu.com").name("PANPSC")
                .value("9838309313047430302%3AD6NVGkQPO1V%2B6HEomkBPOl%2FZsIj986bZZEdlwRrYaNDxTJ3v1z1m"
                        + "%2BoZOREOiazyCCCGLPhVK4uDJPYMhf1rWWcHD10uzHPX09VJtkjt85vcDzLGxCF9yjchTKu%2BdwTb"
                        + "%2Fnr9yQoCeHI88g1PcRcrciMZ09rQO2Yg1vdvDG1Aevkc%3D")
                .build();
        if (!mConfig.has(getCookieToken(cookie))) {
            cookies.add(cookie);
        }
        cookie = new Cookie.Builder()
                .domain("baidu.com").name("STOKEN")
                .value("7e875b825aa19575f7a9d80af5d423d40ff39e19b7f49eae3d31b4a07d2e7966")
                .build();
        if (!mConfig.has(getCookieToken(cookie))) {
            cookies.add(cookie);
        }
        if (!cookies.isEmpty()) {
            put(url, cookies);
        }
    }

    private void add(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        addCookieToWebView(url, cookie); //将当前cookie存入WebView的cookie中

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent() || (cookie.expiresAt() - System.currentTimeMillis()
                                             >= 0)) { //cookie无过期时间限制,或者有过期限制但是时间未到
            if (!mCookies.containsKey(url.host())) {
                mCookies.put(url.host(), new ConcurrentHashMap<String, Cookie>());
            }
            mCookies.get(url.host()).put(name, cookie);
        } else {
            if (mCookies.containsKey(url.host())) {
                mCookies.get(url.host()).remove(name);
            }
        }
        //cookies持久化到本地，每add一次数据就需要将数据本地化一次
        ConcurrentHashMap<String, Cookie> hashMap = mCookies.get(url.host());
        if (hashMap != null) {
            Set<String> keys = hashMap.keySet();
            StringBuilder builder = new StringBuilder();
            for (String str : keys) {
                builder.append(str);
                builder.append(",");
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            mConfig.put(url.host(), builder.toString()); //一个hostUrl 对应一个类似aa@bb.cc@dd的键值对，后者是cookie的key
            mConfig.put(name, encodeCookie(new SerializableOkHttpCookies(cookie)));
            //一个aa@bb.cc@dd为key 后者是对cookie中的所有域变成一个16进制的字符串
            //这样做不会重复？不同网站使用cookie名字相同,但是注意到这里也使用了aa@bb的形式，Domain是url的域名
        }
    }

    List<Cookie> get(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (mCookies.containsKey(url.host())) {
            ret.addAll(mCookies.get(url.host()).values());
        }

        for (Cookie item : ret) {
            Log.d(TAG, "" + url.host() + " getFromOkHttp: " + item.toString());
        }
        getWebViewCookie(url, ret); //补充webView的cookie
        return ret;
    }

    private boolean removeAll() {
        mConfig.clear();
        mConfig.commit();
        mCookies.clear();
        return true;
    }

    public boolean remove(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        if (mCookies.containsKey(url.host()) && mCookies.get(url.host()).containsKey(name)) {
            mCookies.get(url.host()).remove(name);

            if (mConfig.has(name)) {
                mConfig.remove(name);
            }
            mConfig.put(url.host(), TextUtils.join(",", mCookies.get(url.host()).keySet())); //更新url对应的cookie值
            mConfig.commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除url对应的所有cookie
     *
     * @param url
     *
     * @return
     */
    public boolean remove(HttpUrl url) {
        if (mCookies.containsKey(url.host())) {
            ConcurrentHashMap<String, Cookie> data = mCookies.remove(url.host());
            for (Cookie cookie : data.values()) {
                String name = getCookieToken(cookie);
                if (mConfig.has(name)) {
                    mConfig.remove(name);
                }
                mConfig.put(url.host(),
                        TextUtils.join(",", mCookies.get(url.host()).keySet())); //更新url对应cookie
                mConfig.commit();
            }
            return true;
        } else {
            return false;
        }
    }

    /****************************************上面为公共技术代码，下面为底层数据处理********************************************************/
    /**
     * cookies 序列化成 string
     * 因为通过SharePreference进行存储，所以有必要将bytes转换成String。后期针对SerializableOkHttpCookies对象也可以直接使用文件进行存储则不需要进行比特到String的转换
     *
     * @param cookie 要序列化的cookie
     *
     * @return 序列化之后的string
     */
    private String encodeCookie(SerializableOkHttpCookies cookie) {
        if (cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(TAG, "IOException in encodeCookie e=" + e.getMessage());
            return null;
        }
        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * 将字符串反序列化成cookies
     * SharePreference只能存储int、float、String数据因此需要将二进制数据转换成String。如果一个url对应一个缓存文件就不需要这样的处理了。
     *
     * @param cookieString cookies string
     *
     * @return cookie object
     */
    private Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableOkHttpCookies) objectInputStream.readObject()).getCookies();
        } catch (IOException e) {
            Log.d(TAG, "IOException in decodeCookie e=" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException in decodeCookie e=" + e.getMessage());
        }

        return cookie;
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     *
     * @return string containing hex values
     */
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     *
     * @return decoded byte array
     */
    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 下面的方法是将OkHttp的Cookie同步到WebView那边去。也就是实现了okhttp端的登录，在webView可以直接利用这个登录的结果！！
     *
     * @param url    sd
     * @param cookie sd
     */
    private void addCookieToWebView(HttpUrl url, Cookie cookie) {
        android.webkit.CookieManager webkitCookieManager = android.webkit.CookieManager.getInstance();//webView的Cookie管理
        webkitCookieManager.setCookie(url.host(), cookie.toString());
        /**
         * 后面的String类型内容大体如：nforum[PASSWORD]=VCKDSGKjUg52te4omvy04Q%3D%3D; expires=Sat, 15 Jul 2017 10:54:52 GMT;
         * domain=m.byr.cn; path=/
         * webkitCookieManager内部会对这个字符串进行解析：
         * 1、取出nforum[PASSWORD]=VCKDSGKjUg52te4omvy04Q%3D%3D;键值对信息前者为key 后者为value
         * 2、根据键值对后面的信息做出额外的处于，如过期性检查
         * 存储过程是针对同样的url.host()，如果key对应的cookie已经存在则更新value，否则添加
         */
    }

    /**
     * 设置WebView了能够接收处理Cookie信息
     */
    private void initWebViewCookieManager(Context context) {
        android.webkit.CookieSyncManager.createInstance(context.getApplicationContext());
        //webView的Cookie管理
        android.webkit.CookieManager.getInstance().setAcceptCookie(true); //webView的Cookie管理
    }

    /**
     * 将webView中有的Cookie而ret中没有的Cookie，存入ret中
     *
     * @param url s
     */
    private void getWebViewCookie(HttpUrl url, ArrayList<Cookie> ret) {
        if (ret == null) {
            return;
        }
        android.webkit.CookieManager webkitCookieManager = android.webkit.CookieManager.getInstance();//webView的Cookie管理
        String data = webkitCookieManager.getCookie(url.host());

        /**
         * 返回内容如：Hm_lvt_a2cb7064fdf52fd51306dd6ef855264a=1468579102;
         * Hm_lpvt_a2cb7064fdf52fd51306dd6ef855264a=1468579102; nforum[UTMPUSERID]=evanman; nforum[UTMPKEY]=40216954;
         */
        if (data == null || data.isEmpty()) {
            return;
        }
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

}
