package com.yqman.cloudfile.test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.yqman.cloudfile.db.Tables;
import com.yqman.persistence.android.database.IDatabaseContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by manyongqiang on 2017/12/13.
 */

public class DbTestUtils {
    private static final String TAG = "DbTestUtils";
    private final IDatabaseTest mDatabase;
    private final Random mRandom;
    private long mStartInsertTime;
    private AtomicInteger mInsertThread;
    private static final int MAX_INSERT_THREAD = 5;
    private static final int MAX_INSERT_COUNT = 1_0000;

    public DbTestUtils(IDatabaseTest databaseTest) {
        mDatabase = databaseTest;
        mRandom = new Random();
        mInsertThread = new AtomicInteger(0);
    }

    public void runInsertTest() {
        mDatabase.delete(Tables.CACHE_FILE_TABLE, null, null);
        mInsertThread.set(0);
        mStartInsertTime = System.currentTimeMillis();
        for (int ind = 0; ind < MAX_INSERT_THREAD; ind++) {
            internalInsertTest(MAX_INSERT_COUNT * ind);
        }
    }

    private void internalInsertTest(final int startIndex) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = buildContentValues();
                for (int index = 0; index < MAX_INSERT_COUNT; index++) {
                    contentValues.put(Tables.CacheFileColumn.FSID, index + startIndex);
                    mDatabase.insert(Tables.CACHE_FILE_TABLE, null, contentValues);
                }
                int count = mInsertThread.incrementAndGet();
                if (count == MAX_INSERT_THREAD) {
                    mDatabase.printInsertTime(System.currentTimeMillis() - mStartInsertTime);
                } else if (count > MAX_INSERT_COUNT) {
                    Log.d(TAG, "insert err:" + count);
                }
            }
        }).start();
    }

    private ContentValues buildContentValues() {
        int fsid = mRandom.nextInt();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.CacheFileColumn.FSID, fsid);
        contentValues.put(Tables.CacheFileColumn.CATEGORY, 1);
        contentValues.put(Tables.CacheFileColumn.PATH, fsid);
        contentValues.put(Tables.CacheFileColumn.PARENT_PATH, fsid);
        return contentValues;
    }

    public static void test(Context context) {
        IDatabaseContext databaseHelper = new CloudFileDbWCDBTest(context);
        WCDBTest wcdbTest = new WCDBTest(databaseHelper.getDatabase(true));
        DbTestUtils wc = new DbTestUtils(wcdbTest);
        databaseHelper = new CloudFileDbWALTest(context);
        NormalTest normalTest = new NormalTest(databaseHelper.getDatabase(true));
        DbTestUtils normal = new DbTestUtils(normalTest);
        normal.runInsertTest();
        wc.runInsertTest();
    }

    public void runQueryTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Cursor cursor = mDatabase.query(Tables.CACHE_FILE_TABLE, Tables.CacheFileColumn.Query.PROJECTION, null,
                        null, null, null, null);
                loopCursor(cursor);
                mDatabase.printQueryTime(System.currentTimeMillis() - startTime);
            }
        }).start();
    }

    private void loopCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        cursor.moveToPosition(0);
        while (cursor.moveToNext()) {
            ;
        }
    }

}
