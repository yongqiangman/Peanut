package com.yqman.cloudfile.db;

/**
 * Created by manyongqiang on 2017/11/27.
 *
 */

public interface Tables {
    String CACHE_FILE_TABLE = "cache_file_table";

    interface BaseColumn {
        String ID = "id";
    }

    interface CacheFileColumn extends BaseColumn {
        String CATEGORY = "category";
        String OPEN_UK = "operator_uk";
        String FSID = "fsid";
        String SERVER_CTIME = "server_ctime";
        String SERVER_MTIME = "server_mtime";
        String IS_DIR = "is_dir";
        String LOCAL_MTIME = "local_mtime";
        String LOCAL_CTIME = "local_ctime";
        String SIZE = "size";
        String FILE_NAME = "file_name";
        String PATH = "path";
        String PARENT_PATH = "parent_path";
        String PROPERTY = "property";

        interface Query {
            String[] PROJECTION = {
                    CacheFileColumn.ID,
                    CacheFileColumn.CATEGORY,
                    CacheFileColumn.OPEN_UK,
                    CacheFileColumn.FSID,
                    CacheFileColumn.SERVER_CTIME,
                    CacheFileColumn.SERVER_MTIME,
                    CacheFileColumn.IS_DIR,
                    CacheFileColumn.LOCAL_MTIME,
                    CacheFileColumn.LOCAL_CTIME,
                    CacheFileColumn.SIZE,
                    CacheFileColumn.FILE_NAME,
                    CacheFileColumn.PATH,
                    CacheFileColumn.PARENT_PATH,
                    CacheFileColumn.PROPERTY,
            };
            int ID = 0;
            int CATEGORY = 1;
            int OPEN_UK = 2;
            int FSID = 3;
            int SERVER_CTIME = 4;
            int SERVER_MTIME = 5;
            int IS_DIR = 6;
            int LOCAL_MTIME = 7;
            int LOCAL_CTIME = 8;
            int SIZE = 9;
            int FILE_NAME = 10;
            int PATH = 11;
            int PARENT_PATH = 12;
            int PROPERTY = 13;
        }
    }
}
