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

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Build
import android.os.storage.StorageManager
import com.yqman.monitor.LogHelper
import com.yqman.scheduler.util.FileUtils
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * Created by manyongqiang on 2018/7/9.
 * Environment、mount、ContextCompat访问文件路径
 */
@TargetApi(Build.VERSION_CODES.N)
class StorageViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = "StorageTest"

    fun test() {
        testContextCompat()
        testMountFile()
        testStoreManager()
    }

    private fun testStoreManager() {
        val builder = StringBuilder()
        builder.append("\r\n").append("testStoreManager()").append("\r\n")
        val storageManager = getApplication<Application>().getSystemService(Activity.STORAGE_SERVICE) as StorageManager
        val volumeList = storageManager.storageVolumes
        for (volume in volumeList) {
            builder.append("\r\n").append(volume.getDescription(getApplication<Application>())).append("-")
            var path: String? = null
            try {
                path = volume.javaClass.getMethod("getPath").invoke(volume) as String
            } catch (e: Exception) {
                builder.append(e.message).append("\r\n")
                continue
            }

            builder.append(path).append("\r\n")
            builder.append(FileUtils.checkDirPath(path
                    + "/000/test/StoreManager/"
                    + Date(System.currentTimeMillis()).toString()))
        }
        print(builder.toString())
    }

    private fun testContextCompat() {
        val builder = StringBuilder()
        builder.append("\r\n").append("testContextCompat()").append("\r\n")
        // storage/emulated/0/Android/data/com.baidu.cloudenterprise/files
        val files = getApplication<Application>().getExternalFilesDirs(null)
        if (files == null || files.isEmpty()) {
            builder.append("file is empty").append("\r\n")
        } else {
            for (file in files) {
                var path = file.absolutePath
                builder.append("raw path:").append(path).append("\r\n")
                // 替换路径中存在的包名
                path = path.replace(getApplication<Application>().packageName, "cloudenterprise")
                builder.append("real path:").append(FileUtils.checkDirPath(path
                        + "/000/test/ContextCompat/"
                        + Date(System.currentTimeMillis()).toString()))
            }
        }
        print(builder.append("\r\n").toString())
    }

    private fun testMountFile() {
        val builder = StringBuilder()
        builder.append("\r\n").append("testMountFile()").append("\r\n")
        val secondPath = getSecondaryStorageDirectoriesByMountFile()
        for (path in secondPath) {
            builder.append(path).append("\r\n")
            builder.append(FileUtils.checkDirPath(path
                    + "/000/test/MountFile/"
                    + Date(System.currentTimeMillis()).toString()))
        }
        print(builder.toString())
    }

    private fun print(content: String) {
        LogHelper.d(TAG, content)
    }

    /**
     * 如果getVolumePath没能成功获取双卡，从mounts文件解析出双卡路径
     */
    private fun getSecondaryStorageDirectoriesByMountFile(): List<String> {
        var bufReader: BufferedReader? = null
        val list = ArrayList<String>()
        try {
            // 如果getVolumePath没能成功获取双卡，从mounts文件解析出双卡路径
            bufReader = BufferedReader(FileReader("/proc/mounts"))
            var line: String? = bufReader.readLine()
            while (line != null) {
                if (line.contains("vfat") || line.contains("exfat") || line.contains("/mnt")
                        || line.contains("/storage")) {
                    print(" tmpLine:$line \r\n")
                    val tokens = StringTokenizer(line, " ")
                    var s = tokens.nextToken()
                    s = tokens.nextToken() // Take the second token, i.e. mount poin
                    print("tmpPath:$s \r\n")
                    if (s == "") {
                        continue
                    }
                    if (isStorage(line)) {
                        if (s == "") {
                            continue
                        }
                        list.add(s)
                    }
                }
                line = bufReader.readLine()
            }
        } catch (e: FileNotFoundException) {
            LogHelper.e(TAG, e.message, e)
        } catch (e: IOException) {
            LogHelper.e(TAG, e.message, e)
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close()
                } catch (e: IOException) {
                    LogHelper.e(TAG, e.message, e)
                }

            }
        }
        return list
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
    private fun isStorage(line: String): Boolean {
        return (line.contains("/dev/block/vold") && !line.contains("/mnt/secure") && !line.contains("/mnt/asec")
                && !line.contains("/mnt/obb") && !line.contains("/dev/mapper") && !line.contains("tmpfs"))
    }
}