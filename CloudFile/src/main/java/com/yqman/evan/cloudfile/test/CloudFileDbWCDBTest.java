package com.yqman.evan.cloudfile.test;

import com.yqman.evan.cloudfile.db.Tables;
import com.yqman.persistence.android.database.BaseWCDBDatabase;
import com.yqman.persistence.android.database.CreateTableSQLBuilder;
import com.yqman.persistence.android.database.IDatabaseOperation;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by manyongqiang on 2017/12/6.
 */

public class CloudFileDbWCDBTest extends BaseWCDBDatabase {
    private static final String DATABASE_NAME = "evan_cloud_file_test.db";
    private static final int DATABASE_VERSION = 1;

    public CloudFileDbWCDBTest(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void create(IDatabaseOperation databaseOperation) {
        CreateTableSQLBuilder builder = new CreateTableSQLBuilder(Tables.CACHE_FILE_TABLE);
        builder.setTableContract("UNIQUE( " + Tables.CacheFileColumn.FSID + " ) ON CONFLICT REPLACE");
        builder.addColumn(Tables.CacheFileColumn.ID, "INTEGER PRIMARY KEY AUTOINCREMENT");
        builder.addColumn(Tables.CacheFileColumn.CATEGORY, "INTEGER NOT NULL");
        builder.addColumn(Tables.CacheFileColumn.OPEN_UK, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.FSID, "INTEGER NOT NULL");
        builder.addColumn(Tables.CacheFileColumn.SERVER_CTIME, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.SERVER_MTIME, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.IS_DIR, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.LOCAL_MTIME, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.LOCAL_CTIME, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.SIZE, "INTEGER");
        builder.addColumn(Tables.CacheFileColumn.FILE_NAME, "TEXT");
        builder.addColumn(Tables.CacheFileColumn.PATH, "TEXT NOT NULL");
        builder.addColumn(Tables.CacheFileColumn.PARENT_PATH, "TEXT");
        builder.addColumn(Tables.CacheFileColumn.PROPERTY, "INTEGER");
        String sql = builder.createSql();
        if (!TextUtils.isEmpty(sql)) {
            databaseOperation.execSQL(sql);
        }
    }

    @Override
    public void upgrade(IDatabaseOperation databaseOperation, int oldVersion, int newVersion) {

    }
}
