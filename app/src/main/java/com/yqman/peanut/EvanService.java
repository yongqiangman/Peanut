package com.yqman.peanut;

import com.netdisk.library.threadscheduler.Configuration;
import com.netdisk.library.threadscheduler.ITaskScheduler;
import com.netdisk.library.threadscheduler.TaskSchedulerManager;
import com.yqman.cloudfile.io.model.CloudFile;
import com.yqman.cloudfile.service.CloudFileService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by manyongqiang on 2018/2/8.
 *
 */

public class EvanService extends Service {
    private ITaskScheduler mScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        mScheduler = new TaskSchedulerManager(new Configuration.Builder().build());
        mScheduler.start();
        mCloudFileService = new CloudFileService(this, mScheduler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScheduler.destroy();
    }

    private static final String CLOUD_FILE_SERVICE = "CLOUD_FILE_SERVICE";
    private CloudFileService mCloudFileService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }
        String serviceType = intent.getStringExtra(BaseExtras.SERVICE_TYPE);
        if (TextUtils.isEmpty(serviceType)) {
            return super.onStartCommand(intent, flags, startId);
        }
        ResultReceiver resultReceiver = intent.getParcelableExtra(BaseExtras.RESULT_RECEIVER);
        CloudFile cloudFile = intent.getParcelableExtra(BaseExtras.CLOUD_FILE);
        switch (serviceType) {
            case CLOUD_FILE_SERVICE:
                mCloudFileService.getDirList(cloudFile, resultReceiver);
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
