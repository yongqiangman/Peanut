package com.yqman.evan

import android.app.Application

import android.app.Service
import android.content.Intent
import com.squareup.leakcanary.LeakCanary

/**
 * Created by manyongqiang on 2018/2/5.
 *
 */

class EvanApplication internal constructor() : Application() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, EvanService::class.java))
        oomAnalysis()
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

    companion object {
        val service: Class<out Service>
            get() = EvanService::class.java
    }
}
