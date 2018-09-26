package com.yqman.evan.cloudfile.service;

import java.util.ArrayList;

import com.yqman.evan.cloudfile.io.model.CloudFile;

import android.os.ResultReceiver;

/**
 * Created by manyongqiang on 2018/2/5.
 */

interface ICloudFile {
    /**
     * 删除指定文件
     *
     * @param targetFiles     要删除的文件
     * @param resultReceiver 异步结果接收器
     *
     * @return 删除成功
     */
    boolean deleteFile(ArrayList<CloudFile> targetFiles, ResultReceiver resultReceiver);

    /**
     * 重命名文件
     *
     * @param targetFile     原始文件
     * @param newName        新文件名
     * @param resultReceiver 异步结果接收器
     */

    void renameFile(CloudFile targetFile, String newName, ResultReceiver resultReceiver);

    /**
     * 创建一个目录
     *
     * @param parentDir      父目录
     * @param dirName        目录名字
     * @param resultReceiver 异步结果接收器
     */
    void createDir(CloudFile parentDir, String dirName, ResultReceiver resultReceiver);

    /**
     * 获取指定目录下的文件
     *
     * @param targetDir 指定目录
     */
    void getDirList(CloudFile targetDir, ResultReceiver resultReceiver);

    void diffCloudFile(ResultReceiver resultReceiver);
}
