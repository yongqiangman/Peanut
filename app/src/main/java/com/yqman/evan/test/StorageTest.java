package com.yqman.evan.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.yqman.monitor.LogHelper;
import com.yqman.scheduler.util.FileUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

/**
 * Created by manyongqiang on 2018/7/9.
 * Environment、mount、ContextCompat访问文件路径
 */
@TargetApi(Build.VERSION_CODES.N)
public class StorageTest {
    private static final String TAG = "StorageTest";

    private final Context mContext;

    public StorageTest(Context context) {
        mContext = context;
    }

    public void test() {
        testContextCompat();
        testMountFile();
        testStoreManager();
    }

    private void testStoreManager() {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n").append("testStoreManager()").append("\r\n");
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Activity.STORAGE_SERVICE);
        List<StorageVolume> volumeList = storageManager.getStorageVolumes();
        for (StorageVolume volume : volumeList) {
            builder.append("\r\n").append(volume.getDescription(mContext)).append("-");
            String path = null;
            try {
                path = (String) volume.getClass().getMethod("getPath").invoke(volume);
            } catch (Exception e) {
                builder.append(e.getMessage()).append("\r\n");
                continue;
            }
            builder.append(path).append("\r\n");
            builder.append(FileUtils.checkDirPath(path
                    + "/000/test/StoreManager/"
                    + new Date(System.currentTimeMillis()).toString()));
        }
        print(builder.toString());
    }

    private void testContextCompat() {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n").append("testContextCompat()").append("\r\n");
        // storage/emulated/0/Android/data/com.baidu.cloudenterprise/files
        File[] files = mContext.getExternalFilesDirs(null);
        if (files == null || files.length <= 0) {
            builder.append("file is empty").append("\r\n");
        } else {
            for (File file : files) {
                String path = file.getAbsolutePath();
                builder.append("raw path:").append(path).append("\r\n");
                // 替换路径中存在的包名
                path = path.replace(mContext.getPackageName(), "cloudenterprise");
                builder.append("real path:").append(FileUtils.checkDirPath(path
                        + "/000/test/ContextCompat/"
                        + new Date(System.currentTimeMillis()).toString()));
            }
        }
        print(builder.append("\r\n").toString());
    }

    private void testMountFile() {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n").append("testMountFile()").append("\r\n");
        List<String> secondPath = getSecondaryStorageDirectoriesByMountFile();
        for (String path : secondPath) {
            builder.append(path).append("\r\n");
            builder.append(FileUtils.checkDirPath(path
                    + "/000/test/MountFile/"
                    + new Date(System.currentTimeMillis()).toString()));
        }
        print(builder.toString());
    }

    private void print(String content) {
        LogHelper.d(TAG, content);
    }

    /**
     * 如果getVolumePath没能成功获取双卡，从mounts文件解析出双卡路径
     */
    private List<String> getSecondaryStorageDirectoriesByMountFile() {
        BufferedReader bufReader = null;
        List<String> list = new ArrayList<String>();
        try {
            // 如果getVolumePath没能成功获取双卡，从mounts文件解析出双卡路径
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = bufReader.readLine()) != null) {
                if (line.contains("vfat") || line.contains("exfat") || line.contains("/mnt")
                        || line.contains("/storage")) {
                    print(" tmpLine:" + line + " \r\n");
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount poin
                    print("tmpPath:" + s + " \r\n");
                    if (s.equals("")) {
                        continue;
                    }
                    if (isStorage(line)) {
                        if (s.equals("")) {
                            continue;
                        }
                        list.add(s);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogHelper.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            LogHelper.e(TAG, e.getMessage(), e);
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    LogHelper.e(TAG, e.getMessage(), e);
                }
            }
        }
        return list;
    }

    /**
     * 判断是否是存储器的形式
     *
     * @param line
     *
     * @return
     *
     * @author 孙奇 V 1.0.0 Create at 2013-2-19 下午05:33:40
     */
    private boolean isStorage(String line) {
        return line.contains("/dev/block/vold") && !line.contains("/mnt/secure") && !line.contains("/mnt/asec")
                && !line.contains("/mnt/obb") && !line.contains("/dev/mapper") && !line.contains("tmpfs");
    }
}
