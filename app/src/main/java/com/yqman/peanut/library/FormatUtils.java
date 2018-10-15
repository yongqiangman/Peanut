package com.yqman.peanut.library;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by manyongqiang on 2018/7/11.
 * 格式工具
 */

public class FormatUtils {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINA);
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    private static final long GB = 1024L * MB;

    public static String formatTime(long millsSecond) {
        return mDateFormat.format(new Date(millsSecond * 1000L));
    }

    public static String formatFileSize(long size) {
        float fSize;
        String strSize;
        if (size > GB) {
            fSize = (float) size / (GB);
            strSize = String.format(Locale.CHINA, "%.2fGB", fSize);
        } else if (size > MB) {
            fSize = (float) size / (MB);
            strSize = String.format(Locale.CHINA, "%.2fMB", fSize);
        } else if ((size * 100) / KB > 0) {
            fSize = (float) size / KB;
            strSize = String.format(Locale.CHINA, "%.2fKB", fSize);
        } else {
            strSize = String.format(Locale.CHINA, "%dB", size);
        }
        return strSize;
    }
}
