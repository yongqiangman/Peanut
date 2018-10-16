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

package com.yqman.peanut.test.storage.presenter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.yqman.monitor.LogHelper;
import com.yqman.persistence.file.FileAccessErrException;
import com.yqman.persistence.file.IDirectoryVisitor;
import com.yqman.persistence.file.IFileVisitor;
import com.yqman.persistence.android.file.RemoteDirectory;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by manyongqiang on 2018/7/11.
 * Document操作的业务封装
 */

public class DocumentPresenter {
    private IDirectoryVisitor mRootFile;
    private IDirectoryVisitor mCurrentFile;
    private Context mContext;
    private Deque<IDirectoryVisitor> mHistoryFiles = new ArrayDeque<>();
    private List<IDirectoryVisitor> mDisplayDirs = new ArrayList<>();
    private List<IFileVisitor> mDisplayFiles = new ArrayList<>();
    private IView mView;

    public DocumentPresenter(Context context, IView view) {
        mContext = context;
        mView = view;
    }

    /**
     * 更新展示的uri
     */
    public void updateUri(Uri rawUri) throws FileAccessErrException {
        printString("rawUri:" + rawUri.toString());
        mHistoryFiles.clear();
        mDisplayFiles.clear();
        mRootFile = new RemoteDirectory(mContext, rawUri);
        showRootDocument();
    }

    /**
     * 进入子目录
     */
    public void enterDir(@NonNull FileView file) {
        mHistoryFiles.push(mCurrentFile);
        showDir(file.mIDirectoryVisitor);
    }

    private void showDir(@NonNull IDirectoryVisitor document) {
        printString("file:" + document.getDisplayName());
        mCurrentFile = document;
        mDisplayFiles.clear();
        mDisplayDirs.clear();
        mDisplayFiles = document.listFiles();
        mDisplayDirs = document.listDirectories();
        updateView();
    }

    /**
     * 返回子目录
     */
    public boolean backDir() {
        IDirectoryVisitor bean = mHistoryFiles.poll();
        if (bean == null) {
            if (mCurrentFile == mRootFile) {
                printString("backDir() false");
                return false;
            }
            showRootDocument();
        } else {
            showDir(bean);
        }
        printString("backDir() true");
        return true;
    }

    /**
     * 展示根路径
     */
    private void showRootDocument() {
        showDir(mRootFile);
    }

    /**
     * 更新视图
     */
    private void updateView() {
        ArrayList<FileView> fileViews = new ArrayList<>();
        for (IDirectoryVisitor visitor : mDisplayDirs) {
            fileViews.add(new FileView(visitor));
        }
        for (IFileVisitor visitor : mDisplayFiles) {
            fileViews.add(new FileView(visitor));
        }
        mView.updateView(fileViews);
    }

    private void printString(String content) {
        LogHelper.d("DocumentTest", content);
    }

    public interface IView {
        void updateView(List<FileView> dirs);
    }

    public static class FileView {
        private IDirectoryVisitor mIDirectoryVisitor;
        private IFileVisitor mIFileVisitor;

        private FileView(IDirectoryVisitor IDirectoryVisitor) {
            mIDirectoryVisitor = IDirectoryVisitor;
        }

        private FileView(IFileVisitor IFileVisitor) {
            mIFileVisitor = IFileVisitor;
        }

        public Boolean isDir() {
            return mIDirectoryVisitor != null;
        }

        public String getName() {
            if (isDir()) {
                return mIDirectoryVisitor.getDisplayName();
            } else {
                return mIFileVisitor.getDisplayName();
            }
        }

        public long getSize() {
            if (!isDir()) {
                return mIFileVisitor.getSize();
            }
            return 0;
        }

        public long getModifyTime() {
            if (isDir()) {
                return mIDirectoryVisitor.getMTime();
            } else {
                return mIFileVisitor.getMTime();
            }
        }
    }
}