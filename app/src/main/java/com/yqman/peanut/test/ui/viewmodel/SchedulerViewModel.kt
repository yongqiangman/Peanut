/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yqman.peanut.test.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.yqman.monitor.LogHelper
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class SchedulerViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val TAG = "ThreadRunTest"
        private const val TIME: Long = 5000000 // n秒
        private const val NANO_TIME = true
        private const val MAX_RUN_THREAD = 8
        private const val MAX_THREAD_TASK = 50
    }
    private var mIsStarted = false
    private val mPrioritySemaphore = Semaphore(0)
    private val mExecutorServices = ArrayList<ExecutorService>()

    /**
     * 启动线程
     */
    @Synchronized
    fun start() {
        if (!mIsStarted) {
            val minService = startThread(Thread.MIN_PRIORITY)
            if (minService != null) {
                mExecutorServices.add(minService)
            }
            val normService = startThread(Thread.NORM_PRIORITY)
            if (normService != null) {
                mExecutorServices.add(normService)
            }
            val maxService = startThread(Thread.MAX_PRIORITY)
            if (maxService != null) {
                mExecutorServices.add(maxService)
            }
            mPrioritySemaphore.release(MAX_RUN_THREAD * 3)
            mIsStarted = true
        }
    }

    /**
     * 关闭线程池
     */
    @Synchronized
    fun finish() {
        LogHelper.d(TAG, "finish2")
        for (service in mExecutorServices) {
            LogHelper.d(TAG, "finish")
            service.shutdownNow()
        }
    }

    fun getTag(): String {
        return TAG
    }

    /**
     * 启动线程
     *
     * @param priority 线程优先级
     */
    private fun startThread(priority: Int): ExecutorService? {
        var executorService: ExecutorService? = null
        when (priority) {
            Thread.NORM_PRIORITY -> executorService = Executors.newFixedThreadPool(MAX_RUN_THREAD, MyThreadFactory("Mid", Thread.NORM_PRIORITY))
            Thread.MIN_PRIORITY -> executorService = Executors.newFixedThreadPool(MAX_RUN_THREAD, MyThreadFactory("Low", Thread.MIN_PRIORITY))
            Thread.MAX_PRIORITY -> executorService = Executors.newFixedThreadPool(MAX_RUN_THREAD, MyThreadFactory("Hig", Thread.MAX_PRIORITY))
            else -> {
            }
        }
        if (executorService == null) {
            return null
        }
        val atomicInteger = AtomicInteger(0)
        for (ii in 0 until MAX_RUN_THREAD) {
            executorService.submit(MyThreadPoolRunnable(atomicInteger, MAX_RUN_THREAD * MAX_THREAD_TASK))
        }
        return executorService
    }

    private fun getTime(): Long {
        return if (NANO_TIME) {
            System.nanoTime()
        } else {
            System.currentTimeMillis()
        }
    }

    /**
     * long型转换为数字
     */
    private fun getString(time: Long): String {
        var time = time
        val builder = StringBuilder()
        var value: Long = 0
        while (time > 0) {
            value = time % 1000
            time = time / 1000
            builder.append(value)
            builder.append("_")
        }
        return builder.toString()
    }

    /**
     * 线程池工程
     */
    inner class MyThreadFactory(val mName: String, val mPriority: Int) : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(runnable: Runnable?): Thread {
            LogHelper.d(TAG, "new Thread")
            val thread = Thread(runnable, "PriorityTask # " + mName + " # " + mCount.getAndIncrement())
            thread.priority = mPriority
            return thread
        }
    }

    /**
     * 线程池运行的线程
     * 当前线程执行的任务数
     * 最大任务数
     */
    inner class MyThreadPoolRunnable(private val mCountInteger: AtomicInteger,
                                     private val mMaxTaskCount: Int)
        : Runnable, Comparable<Any> {
        private var mIsStarted = false
        /**
         * 启动时间
         */
        private var mStartTime: Long = 0

        override fun compareTo(other: Any): Int {
            return 0
        }

        override fun run() {
            try {
                if (!mIsStarted) {
                    val threadName = Thread.currentThread().name
                    LogHelper.d(TAG,  "$threadName await semaphore")
                    mPrioritySemaphore.acquire()
                    mIsStarted = true
                    if (mStartTime == 0L) {
                        mStartTime = System.currentTimeMillis();
                    }
                }
                val threadName = Thread.currentThread().toString()
                for (ii in 0 until MAX_THREAD_TASK) {
                    val time = getTime()
                    var runTime = 0L
                    while (runTime < TIME) {
                        runTime = getTime() - time
                    }
                    LogHelper.d(TAG, "$threadName count: ${mCountInteger.incrementAndGet()} time=${getString(runTime)}")
                }
                if (mCountInteger.get() >= mMaxTaskCount) {
                    LogHelper.e(TAG, "$threadName count:${mCountInteger.get()} finished time=${System.currentTimeMillis() - mStartTime}")
                }
            } catch (e: InterruptedException) {
                val threadName = Thread.currentThread().toString()
                val time = System.currentTimeMillis();
                LogHelper.e(TAG,"$threadName intercept $time")
            }
        }
    }
}

