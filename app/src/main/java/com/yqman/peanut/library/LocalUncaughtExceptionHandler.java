package com.yqman.peanut.library;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.LocalFile;

import android.content.Context;
import android.os.Process;
import android.util.Log;

/**
 * Created by yqman on 2016/7/21.
 * 处理未捕获异常,如运行时异常IndexOutOfBoundsException，首先将异常打印输出到文件中，随后打印输入到控制台，最后重启应用
 */
public class LocalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String FILE_NAME = "LocalUncaughtExceptionHandler.txt";

    private BlockingDeque<Throwable> mThrowables = new LinkedBlockingDeque<>();
    private PrintThread mPrintThread = new PrintThread();
    private final String mFileDir;

    public LocalUncaughtExceptionHandler(Context context) {
        mPrintThread.start();//启动工作线程
        mFileDir = new File(context.getCacheDir(), FILE_NAME).getAbsolutePath();
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        mThrowables.push(ex);
    }

    /**
     * 重启应用
     */
    private void reStartApplication() {
        if (mLaunchNewActivity != null) {
            mLaunchNewActivity.startNewActivity();
            android.os.Process.killProcess(android.os.Process.myPid()); //杀死当前进程
        }

    }

    private LaunchNewActivity mLaunchNewActivity;

    public void setLaunchNewActivity(LaunchNewActivity launchNewActivity) {
        this.mLaunchNewActivity = launchNewActivity;
    }


    /**
     * 打印线程
     */
    class PrintThread extends Thread {
        private boolean exitFlag = false;

        @Override
        public void run() {
            while (!exitFlag) {
                try {
                    Throwable throwable = mThrowables.poll(1000, TimeUnit.MICROSECONDS);
                    if (throwable == null) {
                        continue;
                    }
                    printErrorInformation(throwable);
                    recordErrorInformation(throwable);
                    reStartApplication();  //等上述工作执行完毕，重启应用
                } catch (InterruptedException e) {
                    mPrintThread.start(); //重启
                }
            }

        }

        /**
         * 写错误信息到文件中
         */
        private void recordErrorInformation(Throwable ex) {
            try {
                LocalFile localFile = new LocalFile(new File(mFileDir));
                Calendar calendar = Calendar.getInstance();
                localFile.writeStringNewLine("------new error-----", true);
                localFile.writeStringNewLine("UTC: " + System.currentTimeMillis(), true);
                localFile.writeStringNewLine(
                        "Time: " + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                                + calendar.get(Calendar.DATE), true);
                localFile.writeStringNewLine("Process: " + Process.myPid(), true);
                localFile.writeStringNewLine("java.lang.RuntimeException: " + ex.getMessage(),
                        true); //ex.getMessage()与ex.getLocalizedMessage()结果一样
                for (StackTraceElement element : ex.getStackTrace()) {
                    localFile.writeStringNewLine("  at " + element.toString(), true);
                }
                if (ex.getCause() != null) {
                    for (StackTraceElement element : ex.getCause().getStackTrace()) {
                        localFile.writeStringNewLine("  at " + element.toString(), true);
                    }
                }
            } catch (FileAccessErrException e) {
                // do nothing
            }
        }

        /**
         * 写错误信息到控制台
         */
        private void printErrorInformation(Throwable ex) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("UTC: ");
            builder.append(System.currentTimeMillis());
            builder.append("\n");
            builder.append("Process: ");
            builder.append(Process.myPid());
            builder.append("\n");

            builder.append(ex.getMessage());  //ex.getMessage()与ex.getLocalizedMessage()结果一样
            builder.append("\n");
            for (StackTraceElement element : ex.getStackTrace()) {
                builder.append("  at ");
                builder.append(element.toString());
                builder.append("\n");
            }
            if (ex.getCause() != null) {
                builder.append("Caused By: ");
                builder.append(ex.getCause().getMessage());
                builder.append("\n");
                for (StackTraceElement element : ex.getCause().getStackTrace()) {
                    builder.append("  at ");
                    builder.append(element.toString());
                    builder.append("\n");
                }

            }
            Log.e("RuntimeException", builder.toString());
        }
    }

    public interface LaunchNewActivity {
        void startNewActivity();
    }
}


