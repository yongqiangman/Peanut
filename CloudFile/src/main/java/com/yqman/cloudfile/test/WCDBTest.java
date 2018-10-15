package com.yqman.cloudfile.test;



import com.yqman.persistence.android.database.IDatabaseOperation;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by manyongqiang on 2017/12/13.
 */

public class WCDBTest implements IDatabaseTest {
    private static final String TAG = "WCDBTest";
    private final IDatabaseOperation mWCDatabase;

    public WCDBTest(IDatabaseOperation mWCDatabase) {
        this.mWCDatabase = mWCDatabase;
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return mWCDatabase.delete(table, whereClause, whereArgs);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mWCDatabase.insert(table, nullColumnHack, values);
    }

    @Override
    public void printInsertTime(long time) {
        Log.d(TAG, "insert Time:" + time);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {
        return mWCDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public void printQueryTime(long time) {
        Log.d(TAG, "query Time:" + time);
    }
}
