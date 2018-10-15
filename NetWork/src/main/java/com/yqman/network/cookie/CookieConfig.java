package com.yqman.network.cookie;

import com.yqman.persistence.config.Config;
import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.IDirectoryVisitor;

/**
 * Created by manyongqiang on 2018/2/2.
 */

public class CookieConfig extends Config {
    private static final String TAG = "CookieConfig";
    private static CookieConfig mInstance;

    private CookieConfig(IDirectoryVisitor directoryVisitor) throws FileAccessErrException {
        super(true, directoryVisitor.createNewFile(TAG));
    }

    public static CookieConfig getInstance(IDirectoryVisitor directoryVisitor) throws FileAccessErrException {
        if (mInstance == null) {
            synchronized(CookieConfig.class) {
                if (mInstance == null) {
                    mInstance = new CookieConfig(directoryVisitor);
                }
            }
        }
        return mInstance;
    }
}
