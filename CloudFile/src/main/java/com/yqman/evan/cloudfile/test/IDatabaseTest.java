package com.yqman.evan.cloudfile.test;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by manyongqiang on 2017/12/13.
 */

public interface IDatabaseTest {
    int delete(String table, String whereClause, String[] whereArgs);

    long insert(String table, String nullColumnHack, ContentValues values);

    Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String
            having, String orderBy);

    void printInsertTime(long time);

    void printQueryTime(long time);

}
