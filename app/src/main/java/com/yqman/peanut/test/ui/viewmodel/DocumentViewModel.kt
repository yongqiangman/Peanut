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
import java.util.ArrayDeque
import java.util.ArrayList

import com.yqman.monitor.LogHelper
import com.yqman.persistence.file.IDirectoryVisitor
import com.yqman.persistence.file.IFileVisitor
import com.yqman.persistence.android.file.RemoteDirectory

import android.net.Uri

/**
 * Created by manyongqiang on 2018/7/11.
 * Document操作的业务封装
 */

class DocumentViewModel(application: Application): AndroidViewModel(application) {
    private var mRootFile: IDirectoryVisitor? = null
    private var mCurrentFile: IDirectoryVisitor? = null
    private val mHistoryFiles = ArrayDeque<IDirectoryVisitor>()
    private var mDisplayDirs = ArrayList<IDirectoryVisitor>()
    private var mDisplayFiles = ArrayList<IFileVisitor>()
    private var mView: IView? = null

    /**
     * 更新展示的uri
     */
    fun updateUri(rawUri: Uri) {
        printString("rawUri:" + rawUri.toString())
        mHistoryFiles.clear()
        mDisplayFiles.clear()
        mRootFile = RemoteDirectory(getApplication(), rawUri)
        showRootDocument()
    }

    /**
     * 进入子目录
     */
    fun enterDir(file: FileView) {
        mHistoryFiles.push(mCurrentFile)
        showDir(file.mIDirectoryVisitor)
    }

    private fun showDir(document: IDirectoryVisitor?) {
        printString("file:" + document?.displayName)
        mCurrentFile = document
        mDisplayFiles.clear()
        mDisplayDirs.clear()
        mDisplayFiles = document?.listFiles()?:ArrayList<IFileVisitor>()
        mDisplayDirs = document?.listDirectories()?:ArrayList<IDirectoryVisitor>()
        updateView()
    }

    /**
     * 返回子目录
     */
    fun backDir(): Boolean {
        val bean = mHistoryFiles.poll()
        if (bean == null) {
            if (mCurrentFile == mRootFile) {
                printString("backDir() false")
                return false
            }
            showRootDocument()
        } else {
            showDir(bean)
        }
        printString("backDir() true")
        return true
    }

    /**
     * 展示根路径
     */
    private fun showRootDocument() {
        showDir(mRootFile)
    }

    /**
     * 更新视图
     */
    private fun updateView() {
        val fileViews = ArrayList<FileView>()
        for (visitor in mDisplayDirs) {
            fileViews.add(FileView(visitor))
        }
        mDisplayFiles.forEach {
            fileViews.add(FileView(mIFileVisitor = it))
        }
        mView?.updateView(fileViews)
    }

    private fun printString(content: String) {
        LogHelper.d("DocumentTest", content)
    }

    interface IView {
        fun updateView(dirs: ArrayList<FileView>)
    }

    inner class FileView(val mIDirectoryVisitor: IDirectoryVisitor? = null,
                          val mIFileVisitor: IFileVisitor? = null) {

        fun isDir(): Boolean {
            return mIDirectoryVisitor != null
        }

        fun getName() = if (isDir()) {
            mIDirectoryVisitor?.displayName?:""
        } else {
            mIFileVisitor?.displayName?:""
        }

        fun getSize(): Long {
            if (!isDir()) {
                return mIFileVisitor?.size?:0
            }
            return 0
        }

        fun getModifyTime(): Long {
            if (isDir()) {
                return mIDirectoryVisitor?.mTime?:0
            } else {
                return mIFileVisitor?.mTime?:0
            }
        }
    }
}