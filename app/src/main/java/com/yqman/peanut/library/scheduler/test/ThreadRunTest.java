package com.yqman.peanut.library.scheduler.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.yqman.monitor.LogHelper;

import android.support.annotation.NonNull;

/**
 * Created by manyongqiang on 2018/3/5.
 * 测试线程池
 */

public class ThreadRunTest {
    private static final String TAG = "ThreadRunTest";
    private static final long TIME = 5_000_000; // n秒
    private static final boolean NANO_TIME = true;
    private static final int MAX_RUN_THREAD = 8;
    private static final int MAX_THREAD_TASK = 50;
    private boolean mIsStarted = false;
    private final Semaphore mPrioritySemaphore = new Semaphore(0);

    private static ThreadRunTest mInstance;
    private ArrayList<ExecutorService> mExecutorServices = new ArrayList<>();

    private ThreadRunTest() {
    }

    public static ThreadRunTest getInstance() {
        if (mInstance == null) {
            mInstance = new ThreadRunTest();
        }
        return mInstance;
    }

    /**
     * 启动线程
     */
    public synchronized void start() {
        if (!mIsStarted) {
            mExecutorServices.add(startThread(Thread.MIN_PRIORITY));
            mExecutorServices.add(startThread(Thread.NORM_PRIORITY));
            mExecutorServices.add(startThread(Thread.MAX_PRIORITY));

            mPrioritySemaphore.release(MAX_RUN_THREAD * 3);
            mIsStarted = true;
        }
    }

    /**
     * 关闭线程池
     */
    public synchronized void finish() {
        LogHelper.d(TAG, "finish2");
        for (ExecutorService service : mExecutorServices) {
            LogHelper.d(TAG, "finish");
            service.shutdownNow();
        }
    }

    public String getTag() {
        return TAG;
    }

    /**
     * 启动线程
     *
     * @param priority 线程优先级
     */
    private ExecutorService startThread(int priority) {
        ExecutorService executorService = null;
        switch (priority) {
            case Thread.NORM_PRIORITY:
                executorService =
                        Executors.newFixedThreadPool(MAX_RUN_THREAD, new MyThreadFactory("Mid", Thread.NORM_PRIORITY));
                break;
            case Thread.MIN_PRIORITY:
                executorService =
                        Executors.newFixedThreadPool(MAX_RUN_THREAD, new MyThreadFactory("Low", Thread.MIN_PRIORITY));
                break;
            case Thread.MAX_PRIORITY:
                executorService =
                        Executors.newFixedThreadPool(MAX_RUN_THREAD, new MyThreadFactory("Hig", Thread.MAX_PRIORITY));
                break;
            default:
                break;
        }
        if (executorService == null) {
            return null;
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int ii = 0; ii < MAX_RUN_THREAD; ii++) {
            executorService.submit(new MyThreadPoolRunnable(atomicInteger, MAX_RUN_THREAD * MAX_THREAD_TASK));
        }
        return executorService;
    }

    /**
     * 线程池工程
     */
    private class MyThreadFactory implements ThreadFactory {
        private final String mName;
        private final int mPriority;

        private final AtomicInteger mCount = new AtomicInteger(1);

        MyThreadFactory(String name, int priority) {
            mName = name;
            mPriority = priority;
        }

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            LogHelper.d(TAG, "new Thread");
            Thread thread =
                    new Thread(runnable, "PriorityTask # " + mName + " # " + mCount.getAndIncrement());
            thread.setPriority(mPriority);
            return thread;
        }
    }

    /**
     * 线程池运行的线程
     */
    private class MyThreadPoolRunnable implements Runnable, Comparable {
        /**
         * 当前线程执行的任务数
         */
        private AtomicInteger mCountInteger;

        /**
         * 最大任务数
         */
        private final int mMaxTaskCount;

        /**
         * 启动时间
         */
        private long mStartTime = 0;

        MyThreadPoolRunnable(AtomicInteger countInteger, int maxTaskCount) {
            mCountInteger = countInteger;
            mMaxTaskCount = maxTaskCount;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return 0;
        }

        private boolean mIsStarted = false;

        @Override
        public void run() {
            try {
                if (!mIsStarted) {
                    String threadName = Thread.currentThread().getName();
                    LogHelper.d(TAG, threadName + " await semaphore");
                    mPrioritySemaphore.acquire();
                    mIsStarted = true;
                    if (mStartTime == 0) {
                        mStartTime = System.currentTimeMillis();
                    }
                }
                String threadName = Thread.currentThread().toString();
                for (int ii = 0; ii < MAX_THREAD_TASK; ii++) {
                    long time = getTime();
                    long runTime = 0;
                    while (runTime < TIME) {
                        runTime = getTime() - time;
                    }

                    LogHelper.d(TAG,
                            threadName + " count:" + mCountInteger.incrementAndGet() + " time=" + getString(runTime));
                }
                if (mCountInteger.get() >= mMaxTaskCount) {
                    LogHelper.e(TAG,
                            threadName + " count:" + mCountInteger.get()
                                    + " finished time=" + (System.currentTimeMillis() - mStartTime));
                }
            } catch (InterruptedException e) {
                String threadName = Thread.currentThread().toString();
                long time = System.currentTimeMillis();
                LogHelper.e(TAG, threadName + " intercept " + time);
            }

        }
    }

    private long getTime() {
        if (NANO_TIME) {
            return System.nanoTime();
        } else {
            return System.currentTimeMillis();
        }
    }

    /**
     * long型转换为数字
     */
    private String getString(long time) {
        StringBuilder builder = new StringBuilder();
        long value = 0;
        while (time > 0) {
            value = time % 1000;
            time = time / 1000;
            builder.append(value);
            builder.append("_");
        }
        return builder.toString();
    }
}
