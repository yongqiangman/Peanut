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

package com.yqman.peanut.cloudfile.ui.view

import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.netdisk.library.threadscheduler.android.BaseResultReceiver
import com.netdisk.library.threadscheduler.android.ErrorType
import com.yqman.cloudfile.db.CloudFileDatabaseHelper
import com.yqman.cloudfile.db.Tables
import com.yqman.cloudfile.io.model.CloudFile
import com.yqman.peanut.BaseExtras
import com.yqman.peanut.EvanApplication
import com.yqman.peanut.R
import com.yqman.peanut.databinding.FragmentDatabaseBinding
import java.util.*

class DatabaseFragment: Fragment(), View.OnClickListener {
    private companion object {
        const val TAG = "DatabaseActivity"
    }
    private lateinit var mHelper: CloudFileDatabaseHelper
    private lateinit var mRandom: Random
    private var mCurrentFsid: Long = 0
    private val mDisplayMsg = ObservableField<String>()
    private lateinit var mBinding: FragmentDatabaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_database, container, false)
        mBinding.displayMsg = mDisplayMsg
        mBinding.listener = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mHelper = CloudFileDatabaseHelper(context!!.contentResolver)
        mRandom = Random()
    }

    override fun onClick(view: View?) {
        when (view) {
            mBinding.buttonCreate -> insertDb()
            mBinding.buttonRetrieve -> queryDb()
            mBinding.buttonUpdate -> updateDb()
            mBinding.buttonDelete -> deleteDb()
        }
    }

    private fun insertDb() {
        val uri = mHelper.insertSingleRow(getRandomCloudFile())
        mDisplayMsg.set("insert: $uri")
    }

    private fun queryDb() {
        val cursor = context?.contentResolver?.query(mHelper.queryUri, Tables.CacheFileColumn.Query.PROJECTION,
                null, null, null)
        printCursor(cursor)
        cursor?.let { cursor.close() }
    }

    private fun queryDb(fsid: String) {
        val where = Tables.CacheFileColumn.FSID + "=?"
        val cursor = context?.contentResolver?.query(mHelper.queryUri, Tables.CacheFileColumn.Query.PROJECTION,
                where, arrayOf(fsid), null)
        if (cursor == null || cursor.count <= 0) {
            mDisplayMsg.set("nothing")
        } else {
            mDisplayMsg.set(cursor.getString(Tables.CacheFileColumn.Query.PATH))
        }
        cursor?.let { cursor.close() }
    }

    private fun updateDb() {
        val cloudFile = getRandomCloudFile()
        cloudFile.fsid = mCurrentFsid
        val selection = Tables.CacheFileColumn.FSID + "=?"
        val selectionArgs = arrayOf("" + mCurrentFsid)
        val row = mHelper.updateSingleRow(cloudFile, selection, selectionArgs)
        mDisplayMsg.set("update: $row")
    }

    private fun deleteDb() {
        val cloudFile = CloudFile()
        cloudFile.path = "/"
        val intent = Intent(context, EvanApplication.service)
        intent.putExtra(BaseExtras.CLOUD_FILE, cloudFile)
        intent.putExtra(BaseExtras.SERVICE_TYPE, "CLOUD_FILE_SERVICE")
        intent.putExtra(BaseExtras.RESULT_RECEIVER, ResultReceiver(this))
        context?.startService(intent)
        val selection = Tables.CacheFileColumn.FSID + "=?"
        val selectionArgs = arrayOf("" + mCurrentFsid)
        val row = mHelper.deleteRow(selection, selectionArgs)
        mDisplayMsg.set("delete:$row")
    }

    private fun getRandomCloudFile(): CloudFile {
        val cloudFile = CloudFile()
        cloudFile.fsid = mRandom.nextLong()
        cloudFile.fileName = "name" + mRandom.nextGaussian()
        cloudFile.parentPath = "parent" + mRandom.nextGaussian()
        cloudFile.path = "path" + mRandom.nextGaussian()
        cloudFile.localCTime = mRandom.nextLong()
        cloudFile.serverCTime = mRandom.nextLong()
        return cloudFile
    }

    private fun printCursor(cursor: Cursor?) {
        if (cursor == null) {
            com.yqman.monitor.LogHelper.d(TAG, "cursor is null")
            return
        }
        for (index in 0 until cursor.count) {
            cursor.moveToPosition(index)
            val fsid = cursor.getLong(Tables.CacheFileColumn.Query.FSID)
            mDisplayMsg.set("$fsid")
            mCurrentFsid = fsid
        }
    }

    class ResultReceiver(reference: DatabaseFragment) :
            BaseResultReceiver<DatabaseFragment>(reference, Handler(), null) {

        override fun onSuccess(reference: DatabaseFragment, resultData: Bundle?) {
            super.onSuccess(reference, resultData)
            com.yqman.monitor.LogHelper.d(TAG, "success")
        }

        override fun onFailed(reference: DatabaseFragment, errType: ErrorType, errno: Int, resultData: Bundle): Boolean {
            com.yqman.monitor.LogHelper.d(TAG, "failed")
            return super.onFailed(reference, errType, errno, resultData)
        }
    }
}