package com.yqman.network.cookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

/**
 * Created by yqman on 2016/7/7.
 * 在序列化过程中，如果被序列化的类中定义了writeObject 和 readObject 方法，
 * 虚拟机会试图调用对象类里的 writeObject 和 readObject 方法，进行用户自定义的序列化和反序列化。
 * 如果没有这样的方法，则默认调用是 ObjectOutputStream 的 defaultWriteObject 方法以及 ObjectInputStream 的 defaultReadObject 方法。
 * 用户自定义的 writeObject 和 readObject 方法可以允许用户控制序列化的过程，比如可以在序列化的过程中动态改变序列化的数值。
 * <p>
 * Serialized的机制是利用反射机制对为数据进行可序列化的操作的.
 * 参考链接：http://www.hollischuang.com/archives/1140
 */
class SerializableOkHttpCookies implements Serializable {

    private transient final Cookie cookies;
    /**
     * transient关键字使得Serializable关键字不对其进行序列化操作，但是通过write、readObject方法手动存储该对象.
     * 这种技巧在Collection中,如ArrayList,经常出现
     */
    private transient Cookie clientCookies;

    SerializableOkHttpCookies(Cookie cookies) {
        this.cookies = cookies;
    }

    Cookie getCookies() {
        Cookie bestCookies = cookies;
        if (clientCookies != null) {
            bestCookies = clientCookies;
        }
        return bestCookies;
    }

    /**
     * writeObject ---> writeObject0 --->writeOrdinaryObject--->writeSerialData--->invokeWriteObject
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookies.name());
        out.writeObject(cookies.value());
        out.writeLong(cookies.expiresAt());
        out.writeObject(cookies.domain());
        out.writeObject(cookies.path());

        out.writeBoolean(cookies.secure());
        out.writeBoolean(cookies.httpOnly());
        out.writeBoolean(cookies.hostOnly());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();

        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();

        Cookie.Builder builder = new Cookie.Builder();

        builder = builder.name(name);
        builder = builder.value(value);
        builder = builder.expiresAt(expiresAt);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = builder.path(path);

        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        clientCookies = builder.build();
    }
}