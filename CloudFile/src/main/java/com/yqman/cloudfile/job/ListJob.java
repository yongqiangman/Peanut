package com.yqman.cloudfile.job;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.yqman.android.scheduler.receiver.BaseStatus;
import com.yqman.cloudfile.db.CloudFileDatabaseHelper;
import com.yqman.cloudfile.io.model.CloudFile;
import com.yqman.cloudfile.network.CloudFileApi;
import com.yqman.cloudfile.parse.CloudFileListResponse;
import com.yqman.scheduler.task.BaseTask;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by manyongqiang on 2018/2/6.
 *
 */

public class ListJob extends BaseTask {
    private static final String TAG = "ListJob";
    private Context mContext;
    private ResultReceiver mResultReceiver;
    private CloudFile mCloudFile;

    public ListJob(CloudFile cloudFile, Context context, ResultReceiver resultReceiver) {
        super(TAG);
        mContext = context;
        mResultReceiver = resultReceiver;
        mCloudFile = cloudFile;
    }

    @Override
    public void performExecute() {
        String json = CloudFileApi.sendListRequest(mContext, mCloudFile.getPath());
        Log.d(TAG, "json:" + json);
        CloudFileListResponse response = new Gson().fromJson(json, CloudFileListResponse.class);
        if (response != null && response.errno == 0) {
            new CloudFileDatabaseHelper(mContext.getContentResolver()).insertMultiRow(addParentPath(response.list));
        }
        mResultReceiver.send(BaseStatus.SUCCESS, Bundle.EMPTY);
    }

    private List<CloudFile> addParentPath(CloudFile[] cloudFiles) {
        for (CloudFile cloudFile : cloudFiles) {
            cloudFile.setParentPath(mCloudFile.getPath());
        }
        return Arrays.asList(cloudFiles);
    }
}
