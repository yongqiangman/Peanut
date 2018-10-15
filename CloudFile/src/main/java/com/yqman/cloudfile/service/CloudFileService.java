package com.yqman.cloudfile.service;

import java.util.ArrayList;

import com.yqman.cloudfile.io.model.CloudFile;
import com.yqman.cloudfile.job.ListJob;
import com.yqman.scheduler.ITaskScheduler;
import com.yqman.scheduler.TaskRequest;

import android.content.Context;
import android.os.ResultReceiver;
/**
 * Created by manyongqiang on 2018/2/5.
 *
 */

public class CloudFileService implements ICloudFile {
    private ITaskScheduler mScheduler;
    private final Context mContext;

    public CloudFileService(Context context, ITaskScheduler scheduler) {
        mContext = context;
        mScheduler = scheduler;
    }

    @Override
    public boolean deleteFile(ArrayList<CloudFile> targetFile, ResultReceiver resultReceiver) {

        return false;
    }

    @Override
    public void renameFile(CloudFile targetFile, String newName, ResultReceiver resultReceiver) {

    }

    @Override
    public void createDir(CloudFile parentDir, String dirName, ResultReceiver resultReceiver) {

    }

    @Override
    public void getDirList(final CloudFile targetDir, final ResultReceiver resultReceiver) {
        ListJob listJob = new ListJob(targetDir, mContext, resultReceiver);
        mScheduler.addTask(new TaskRequest.Builder(listJob).build());
    }

    @Override
    public void diffCloudFile(ResultReceiver resultReceiver) {

    }
}
