package com.yqman.library.android;

/**
 * Created by manyongqiang on 2018/6/13.
 */

public class DeviceInfo {
    /**
     * 使用系统api获取系统核数
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
