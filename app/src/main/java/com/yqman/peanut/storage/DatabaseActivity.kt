package com.yqman.peanut.storage

import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.yqman.android.scheduler.receiver.BaseResultReceiver
import com.yqman.android.scheduler.receiver.ErrorType
import com.yqman.peanut.BaseActivity
import com.yqman.peanut.BaseExtras
import com.yqman.peanut.EvanApplication
import com.yqman.cloudfile.db.CloudFileDatabaseHelper
import com.yqman.cloudfile.db.Tables
import com.yqman.cloudfile.io.model.CloudFile
import com.yqman.peanut.R
import com.yqman.peanut.databinding.ActivityDatabaseBinding
import java.util.*

/**
 * Created by manyongqiang on 2018/7/13.
 *
 */

class DatabaseActivity : BaseActivity(), View.OnClickListener {
    private companion object {
        const val TAG = "DatabaseActivity"
    }
    private lateinit var mHelper: CloudFileDatabaseHelper
    private lateinit var mRandom: Random
    private var mCurrentFsid: Long = 0
    private val mDisplayMsg = ObservableField<String>()
    private lateinit var mBinding: ActivityDatabaseBinding

    override fun initDataBeforeView() {
        mHelper = CloudFileDatabaseHelper(contentResolver)
        mRandom = Random()
    }

    override fun initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_database)
        mBinding.displayMsg = mDisplayMsg
        title = "Database"
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
        val cursor = contentResolver.query(mHelper.queryUri, Tables.CacheFileColumn.Query.PROJECTION,
                null, null, null)
        printCursor(cursor)
        cursor?.let { cursor.close() }
    }

    private fun queryDb(fsid: String) {
        val where = Tables.CacheFileColumn.FSID + "=?"
        val cursor = contentResolver.query(mHelper.queryUri, Tables.CacheFileColumn.Query.PROJECTION,
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
        val intent = Intent(this, EvanApplication.service)
        intent.putExtra(BaseExtras.CLOUD_FILE, cloudFile)
        intent.putExtra(BaseExtras.SERVICE_TYPE, "CLOUD_FILE_SERVICE")
        intent.putExtra(BaseExtras.RESULT_RECEIVER, ResultReceiver(this))
        startService(intent)
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

    class ResultReceiver(reference: DatabaseActivity) :
            BaseResultReceiver<DatabaseActivity>(reference, Handler(), null) {

        override fun onSuccess(reference: DatabaseActivity, resultData: Bundle?) {
            super.onSuccess(reference, resultData)
            com.yqman.monitor.LogHelper.d(TAG, "success")
        }

        override fun onFailed(reference: DatabaseActivity, errType: ErrorType, errno: Int, resultData: Bundle): Boolean {
            com.yqman.monitor.LogHelper.d(TAG, "failed")
            return super.onFailed(reference, errType, errno, resultData)
        }
    }
}