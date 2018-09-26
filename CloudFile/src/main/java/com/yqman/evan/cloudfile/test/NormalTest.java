package com.yqman.evan.cloudfile.test;



import com.yqman.persistence.android.database.IDatabaseOperation;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by manyongqiang on 2017/12/13.
 */

public class NormalTest implements IDatabaseTest {
    private static final String TAG = "NormalTest";
    private final IDatabaseOperation mDatabase;

    public NormalTest(IDatabaseOperation mDatabase) {
        this.mDatabase = mDatabase;
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return mDatabase.delete(table, whereClause, whereArgs);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.insert(table, nullColumnHack, values);
    }

    @Override
    public void printInsertTime(long time) {
        Log.d(TAG, "insert Time:" + time);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public void printQueryTime(long time) {
        Log.d(TAG, "query Time:" + time);
    }
}
