package com.yqman.evan.cloudfile.db;

import com.yqman.monitor.LogHelper;
import com.yqman.persistence.android.database.BaseContentProvider;
import com.yqman.persistence.android.database.IDatabaseContext;
import com.yqman.persistence.android.database.IDatabaseOperation;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by manyongqiang on 2017/11/27.
 *
 */

public class CloudFileProvider extends BaseContentProvider {

    private static final String TAG = "CloudFileProvider";
    private IDatabaseContext mCloudFileHelper;

    private static final int CACHE_CLOUD_FILE = 1;
    private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(CloudFileContract.CONTENT_AUTHORITY,
                CloudFileContract.CacheCloudFileContract.PATH_CACHE_CLOUD_FILE, CACHE_CLOUD_FILE);
    }

    @Override
    public boolean onCreate() {
        mCloudFileHelper = new CloudFileDatabase(getContext());
        return super.onCreate();
    }

    @Override
    protected Cursor doQuery(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                             @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        IDatabaseOperation databaseOperation = mCloudFileHelper.getDatabase(false);
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case CACHE_CLOUD_FILE:
                cursor = databaseOperation.query(Tables.CACHE_FILE_TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                LogHelper.d(TAG, cursor.getCount() + "column" + cursor.getColumnCount());
                return cursor;
            default:
                LogHelper.e(TAG, "un support query:" + uri.toString());
                return null;
        }
    }

    @Override
    protected Uri doInsert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        ContentResolver resolver = getContentResolver();
        IDatabaseOperation databaseOperation = mCloudFileHelper.getDatabase(true);
        switch (mUriMatcher.match(uri)) {
            case CACHE_CLOUD_FILE:
                databaseOperation.insert(Tables.CACHE_FILE_TABLE, null, contentValues);
                if (resolver != null) {
                    resolver.notifyChange(uri, null, false);
                }
                break;
            default:
                LogHelper.e(TAG, "un support insert:" + uri.toString());
                break;
        }
        return uri;
    }

    @Override
    protected int doDelete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        ContentResolver resolver = getContentResolver();
        IDatabaseOperation databaseOperation = mCloudFileHelper.getDatabase(true);
        int deleteNum = 0;
        switch (mUriMatcher.match(uri)) {
            case CACHE_CLOUD_FILE:
                deleteNum = databaseOperation.delete(Tables.CACHE_FILE_TABLE, selection, selectionArgs);
                if (resolver != null) {
                    resolver.notifyChange(uri, null, false);
                }
                break;
            default:
                LogHelper.e(TAG, "un support delete:" + uri.toString());
                break;
        }
        return deleteNum;
    }

    @Override
    protected int doUpdate(@NonNull Uri uri, @NonNull ContentValues contentValues, @Nullable String selection,
                           @Nullable String[] selectionArgs) {
        ContentResolver resolver = getContentResolver();
        IDatabaseOperation databaseOperation = mCloudFileHelper.getDatabase(true);
        int updateNum = 0;
        switch (mUriMatcher.match(uri)) {
            case CACHE_CLOUD_FILE:
                updateNum = databaseOperation.update(Tables.CACHE_FILE_TABLE, contentValues, selection, selectionArgs);
                if (resolver != null) {
                    resolver.notifyChange(uri, null, false);
                }
                break;
            default:
                LogHelper.e(TAG, "un support update:" + uri.toString());
                break;
        }
        return updateNum;
    }
}
