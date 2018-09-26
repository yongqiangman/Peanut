package com.yqman.evan.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import com.yqman.evan.BaseActivity
import com.yqman.evan.R
import com.yqman.evan.adapter.RemoteFileAdapter
import com.yqman.evan.databinding.ActivityStorageBinding
import com.yqman.monitor.LogHelper
import com.yqman.evan.presenter.DocumentPresenter

/**
 * Created by manyongqiang on 2018/7/13.
 *
 */
@TargetApi(Build.VERSION_CODES.N)
class StorageActivity : BaseActivity(), DocumentPresenter.IView, RemoteFileAdapter.OnItemClickListener {

    companion object {
        const val TAG = "StorageActivity"
    }

    private val mAdapter = RemoteFileAdapter()
    private val mDocumentPresenter = DocumentPresenter(this, this)

    override fun initView() {
        val binding = DataBindingUtil.setContentView<ActivityStorageBinding>(this, R.layout.activity_storage)
        binding.rvList.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnItemCLickListener(this)
        binding.rvList.adapter = mAdapter
        // accessDocument()
        accessStorageVolume()
    }

    private fun accessStorageVolume() {
        val storageManager: StorageManager? = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        storageManager?.let {
            val storageVolumes = storageManager.storageVolumes
            LogHelper.d(TAG, "volumes size ${storageVolumes.size}")
            if (storageVolumes.size > 1) {
                val storageVolume = storageVolumes[1]
                startActivityForResult(storageVolume.createAccessIntent(null), 100)
            } else {
                val storageVolume = storageVolumes[0]
                startActivityForResult(storageVolume.createAccessIntent(Environment.DIRECTORY_DOWNLOADS), 100)
            }
        }
    }

    private fun accessDocument() {
        val accessIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        accessIntent.type = "image/*"
        accessIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        accessIntent.putExtra(Intent.CATEGORY_OPENABLE, true)
        startActivityForResult(accessIntent, 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            finish()
            return
        }
        when (requestCode) {
            100 -> data?.apply { handleStorageResult(data.data) }
            101 -> data?.apply { handleSelectDocumentResult(data.data) }
            102 -> data?.apply { handleOpenDocumentResult(data) }
            else -> LogHelper.d(TAG, "can not handle $requestCode")
        }
    }

    private fun handleStorageResult(uri: Uri) {
        LogHelper.d(TAG, "uri: $uri")
        val accessUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
        LogHelper.d(TAG, "new uri: + $accessUri")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectDocumentWithInitialLocation(uri)
        } else {
            selectDocument()
        }
    }

    private fun handleOpenDocumentResult(data: Intent) {
        val clipData = data.clipData
        if (clipData != null) {
            for (index in 0 until clipData.itemCount) {
                val item = clipData.getItemAt(index)
                LogHelper.d(TAG, "date: ${item.uri}")
                LogHelper.d(TAG, "date: ${item.text}")
            }
        } else {
            LogHelper.d(TAG, " no date")
        }
    }

    private fun handleSelectDocumentResult(uri: Uri) {
        contentResolver.takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        mDocumentPresenter.updateUri(uri)
    }


    private fun selectDocument() {
        val startAction = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(startAction, 101)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun selectDocumentWithInitialLocation(uri: Uri) {
        val startAction = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startAction.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        startActivityForResult(startAction, 101)
    }

    override fun updateView(dirs: MutableList<DocumentPresenter.FileView>?) {
        dirs?.apply { mAdapter.updateData(dirs) }
    }

    override fun updateTitleBar(title: String?) {
        setTitle(title ?: TAG)
    }

    override fun onItemClick(cloudFile: DocumentPresenter.FileView) {
        if (cloudFile.isDir) {
            mDocumentPresenter.enterDir(cloudFile)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDocumentPresenter.backDir()) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}