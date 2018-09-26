package com.yqman.evan.cloudfile.io.model;

import com.google.gson.annotations.SerializedName;
import com.yqman.evan.cloudfile.db.Tables;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by manyongqiang on 2017/12/3.
 */

public class CloudFile implements Parcelable {
    private static final String TAG = "CloudFile";
    /**
     * 文件类型 图片、视频、文档等
     */
    @SerializedName("category")
    public int mCategory;

    @SerializedName("oper_id")
    public long mOperatorUK;

    /**
     * 文件fsid
     */
    @SerializedName("fs_id")
    public long mFsid;

    /**
     * 文件创建时间
     */
    @SerializedName("server_ctime")
    public long mServerCTime;

    /**
     * 文件修改时间
     */
    @SerializedName("server_mtime")
    public long mServerMTime;

    /**
     * 是否为目录
     */
    @SerializedName("isdir")
    public int mIsDir;

    /**
     * 文件本地修改时间
     */
    @SerializedName("local_mtime")
    public long mLocalMTime;

    /**
     * 文件本地创建时间
     */
    @SerializedName("local_ctime")
    public long mLocalCTime;

    /**
     * 文件大小
     */
    @SerializedName("size")
    public long mSize;

    /**
     * 文件名
     */
    @SerializedName("server_filename")
    public String mFileName;

    /**
     * 文件路径
     */
    @SerializedName("path")
    public String mPath;

    /**
     * 父目录文件路径，以"/"结尾
     */
    public String mParentPath;

    /**
     * 目录属性信息
     */
    @SerializedName("share")
    public int mProperty;

    public CloudFile() {

    }

    public CloudFile(Parcel parcel) {
        mCategory = parcel.readInt();
        mOperatorUK = parcel.readLong();
        mFsid = parcel.readLong();
        mServerCTime = parcel.readLong();
        mServerMTime = parcel.readLong();
        mIsDir = parcel.readInt();
        mLocalMTime = parcel.readLong();
        mLocalCTime = parcel.readLong();
        mSize = parcel.readLong();
        mFileName = parcel.readString();
        mPath = parcel.readString();
        mParentPath = parcel.readString();
        mProperty = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mCategory);
        parcel.writeLong(mOperatorUK);
        parcel.writeLong(mFsid);
        parcel.writeLong(mServerCTime);
        parcel.writeLong(mServerMTime);
        parcel.writeInt(mIsDir);
        parcel.writeLong(mLocalMTime);
        parcel.writeLong(mLocalCTime);
        parcel.writeLong(mSize);
        parcel.writeString(mFileName);
        parcel.writeString(mPath);
        parcel.writeString(mParentPath);
        parcel.writeInt(mProperty);
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.CacheFileColumn.CATEGORY, mCategory);
        contentValues.put(Tables.CacheFileColumn.OPEN_UK, mOperatorUK);
        contentValues.put(Tables.CacheFileColumn.FSID, mFsid);
        contentValues.put(Tables.CacheFileColumn.SERVER_CTIME, mServerCTime);
        contentValues.put(Tables.CacheFileColumn.SERVER_MTIME, mServerMTime);
        contentValues.put(Tables.CacheFileColumn.IS_DIR, mIsDir);
        contentValues.put(Tables.CacheFileColumn.LOCAL_MTIME, mLocalMTime);
        contentValues.put(Tables.CacheFileColumn.LOCAL_CTIME, mLocalCTime);
        contentValues.put(Tables.CacheFileColumn.SIZE, mSize);
        contentValues.put(Tables.CacheFileColumn.FILE_NAME, mFileName);
        contentValues.put(Tables.CacheFileColumn.PATH, mPath);
        contentValues.put(Tables.CacheFileColumn.PARENT_PATH, mParentPath);
        contentValues.put(Tables.CacheFileColumn.PROPERTY, mProperty);
        return contentValues;
    }

    public static Parcelable.Creator<CloudFile> CREATOR = new ClassLoaderCreator<CloudFile>() {
        @Override
        public CloudFile createFromParcel(Parcel parcel, ClassLoader classLoader) {
            if (classLoader != null) {
                try {
                    classLoader.loadClass(CloudFile.class.getName());
                    return new CloudFile(parcel);
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "e:" + e.getMessage());
                }
            }
            return null;
        }

        @Override
        public CloudFile createFromParcel(Parcel parcel) {
            return new CloudFile(parcel);
        }

        @Override
        public CloudFile[] newArray(int i) {
            return new CloudFile[i];
        }
    };

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        mCategory = category;
    }

    public long getOperatorUK() {
        return mOperatorUK;
    }

    public void setOperatorUK(long operatorUK) {
        mOperatorUK = operatorUK;
    }

    public long getFsid() {
        return mFsid;
    }

    public void setFsid(long fsid) {
        mFsid = fsid;
    }

    public long getServerCTime() {
        return mServerCTime;
    }

    public void setServerCTime(long serverCTime) {
        mServerCTime = serverCTime;
    }

    public long getServerMTime() {
        return mServerMTime;
    }

    public void setServerMTime(long serverMTime) {
        mServerMTime = serverMTime;
    }

    public int getIsDir() {
        return mIsDir;
    }

    public void setIsDir(int isDir) {
        mIsDir = isDir;
    }

    public long getLocalMTime() {
        return mLocalMTime;
    }

    public void setLocalMTime(long localMTime) {
        mLocalMTime = localMTime;
    }

    public long getLocalCTime() {
        return mLocalCTime;
    }

    public void setLocalCTime(long localCTime) {
        mLocalCTime = localCTime;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public void setParentPath(String parentPath) {
        mParentPath = parentPath;
    }

    public int getProperty() {
        return mProperty;
    }

    public void setProperty(int property) {
        mProperty = property;
    }
}
