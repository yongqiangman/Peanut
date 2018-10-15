package com.yqman.peanut

import android.app.Application

import android.app.Service
import android.content.Intent
import com.squareup.leakcanary.LeakCanary
import com.yqman.peanut.library.LocalUncaughtExceptionHandler

/**
 * Created by manyongqiang on 2018/2/5.
 *
 */

class EvanApplication internal constructor() : Application() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, EvanService::class.java))
        oomAnalysis()
        initUncaughtExceptionHandler()
    }

    /**
     * oom分析工具
     */
    private fun oomAnalysis() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    /**
     * 初始化未捕获异常的日志输出
     */
    private fun initUncaughtExceptionHandler() {
        val localUncaughtExceptionHandler = LocalUncaughtExceptionHandler(this)
        localUncaughtExceptionHandler.setLaunchNewActivity {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(intent)
        }
        Thread.setDefaultUncaughtExceptionHandler(localUncaughtExceptionHandler)
    }


    companion object {
        val service: Class<out Service>
            get() = EvanService::class.java
    }
}
