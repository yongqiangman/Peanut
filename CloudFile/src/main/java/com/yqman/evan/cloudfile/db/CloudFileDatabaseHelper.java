package com.yqman.evan.cloudfile.db;

import java.util.List;

import com.yqman.evan.cloudfile.io.model.CloudFile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by manyongqiang on 2017/12/3.
 */

public class CloudFileDatabaseHelper {

    private final ContentResolver mContentResolver;
    private final Uri mCloudFileUri;

    public CloudFileDatabaseHelper(@NonNull ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        mCloudFileUri = CloudFileContract.CacheCloudFileContract.buildCacheCloudFileUri();
    }

    public Uri insertSingleRow(@NonNull CloudFile cloudFile) {
        return mContentResolver.insert(mCloudFileUri, cloudFile.getContentValues());
    }

    public int updateSingleRow(CloudFile cloudFile, String selection, String[] selectionArgs) {
        return mContentResolver.update(mCloudFileUri, cloudFile.getContentValues(), selection, selectionArgs);
    }

    public int insertMultiRow(List<CloudFile> cloudFiles) {
        if (cloudFiles == null || cloudFiles.isEmpty()) {
            return -1;
        }
        ContentValues[] contentValues = new ContentValues[cloudFiles.size()];
        for (int index = 0; index < cloudFiles.size(); index++) {
            contentValues[index] = cloudFiles.get(index).getContentValues();
        }
        return mContentResolver.bulkInsert(mCloudFileUri, contentValues);
    }

    public Uri getQueryUri() {
        return mCloudFileUri;
    }

    public int deleteRow(String selection, String[] selectionArgs) {
        return mContentResolver.delete(mCloudFileUri, selection, selectionArgs);
    }

}
