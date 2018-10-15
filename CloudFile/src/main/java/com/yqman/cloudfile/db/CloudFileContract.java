package com.yqman.cloudfile.db;

import com.yqman.persistence.android.database.BaseContract;

import android.net.Uri;

/**
 * Created by manyongqiang on 2017/11/29.
 */

public class CloudFileContract implements BaseContract {
    static final String CONTENT_AUTHORITY = "com.yqman.cloudfile";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class CacheCloudFileContract {
        private static final Uri BaseContentUri = BASE_CONTENT_URI.buildUpon().build();

        public static final String PATH_CACHE_CLOUD_FILE = "cache_cloud_file";
        public static Uri buildCacheCloudFileUri() {
            return BaseContentUri.buildUpon().appendPath(PATH_CACHE_CLOUD_FILE).build();
        }
    }
}
