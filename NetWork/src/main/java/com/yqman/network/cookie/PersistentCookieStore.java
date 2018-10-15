package com.yqman.network.cookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.LocalDirectory;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by yqman on 2016/7/7.
 * 数据持久化的类，接收url和对应的Cookie数据，将其持久化进入磁盘中
 * 对外提供的方法有get、put、remove三个方法
 */
public class PersistentCookieStore {
    private ReentrantLock mLock = new ReentrantLock();
    //内存 String url使用的host就是同一个网站下链接共享一个cookie
    private final Map<String, ConcurrentHashMap<String, Cookie>> mCookies;
    private final CookieConfig mConfig;

    public PersistentCookieStore(String dirPath) throws IllegalArgumentException {
        try {
            LocalDirectory directory = new LocalDirectory(new File(dirPath));
            mConfig = CookieConfig.getInstance(directory);
            mCookies = new HashMap<>();
            //磁盘数据恢复到内存中
            Set<Map.Entry<Object, Object>> entrySets = mConfig.getEntrySet();
            if (entrySets == null || entrySets.isEmpty()) {
                return;
            }
            for (Map.Entry<Object, Object> entry : entrySets) {
                String[] cookieNames = split((String) entry.getValue(), ",");
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
        } catch (FileAccessErrException e) {
            throw new IllegalArgumentException(e);
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

    private void add(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        addCookie(url, cookie);

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

    protected void addCookie(HttpUrl url, Cookie cookie) { }

    List<Cookie> get(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (mCookies.containsKey(url.host())) {
            ret.addAll(mCookies.get(url.host()).values());
        }
        List<Cookie> cookies = getCookie(url);
        if (cookies != null) {
            ret.addAll(cookies);
        }
        return ret;
    }

    protected List<Cookie> getCookie(HttpUrl url) {
        return new ArrayList<Cookie>();
    }

    void removeAll() {
        mConfig.clear();
        mConfig.commit();
        mCookies.clear();
    }

    boolean remove(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        if (mCookies.containsKey(url.host()) && mCookies.get(url.host()).containsKey(name)) {
            mCookies.get(url.host()).remove(name);

            if (mConfig.has(name)) {
                mConfig.remove(name);
            }
            mConfig.put(url.host(), join(",", mCookies.get(url.host()).keySet())); //更新url对应的cookie值
            mConfig.commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除url对应的所有cookie
     */
    boolean remove(HttpUrl url) {
        if (mCookies.containsKey(url.host())) {
            ConcurrentHashMap<String, Cookie> data = mCookies.remove(url.host());
            for (Cookie cookie : data.values()) {
                String name = getCookieToken(cookie);
                if (mConfig.has(name)) {
                    mConfig.remove(name);
                }
                mConfig.put(url.host(),
                        join(",", mCookies.get(url.host()).keySet())); //更新url对应cookie
                mConfig.commit();
            }
            return true;
        } else {
            return false;
        }
    }

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
            //  do no thing
        } catch (ClassNotFoundException e) {
            //  do no thing
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

    private static String[] split(String text, String expression) {
        if (text.length() == 0) {
            return new String[] {};
        } else {
            return text.split(expression, -1);
        }
    }

    private static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

}
