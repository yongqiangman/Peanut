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

package com.yqman.peanut.test.ui.view

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yqman.monitor.LogHelper
import com.yqman.peanut.R
import com.yqman.peanut.databinding.FragmentStorageBinding
import com.yqman.peanut.test.ui.view.adapter.RemoteFileAdapter
import com.yqman.peanut.test.ui.viewmodel.DocumentViewModel
import java.util.ArrayList

@TargetApi(Build.VERSION_CODES.N)
class StorageFragment: Fragment(),  DocumentViewModel.IView, RemoteFileAdapter.OnItemClickListener {
    companion object {
        const val TAG = "StorageActivity"
    }

    private val mAdapter = RemoteFileAdapter()
    private lateinit var mDocumentViewModel: DocumentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentStorageBinding>(inflater, R.layout.fragment_storage, container, false)
        binding.rvList.layoutManager = LinearLayoutManager(context)
        mAdapter.setOnItemCLickListener(this)
        binding.rvList.adapter = mAdapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDocumentViewModel = DocumentViewModel(activity!!.application)
        // accessDocument()
        accessStorageVolume()
    }

    private fun accessStorageVolume() {
        val storageManager: StorageManager? = context?.getSystemService(Context.STORAGE_SERVICE) as StorageManager
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
        context?.contentResolver?.takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        mDocumentViewModel.updateUri(uri)
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

    override fun updateView(dirs: ArrayList<DocumentViewModel.FileView>) {
        dirs?.apply { mAdapter.updateData(dirs) }
    }

    override fun onItemClick(cloudFile: DocumentViewModel.FileView) {
        if (cloudFile.isDir()) {
            mDocumentViewModel.enterDir(cloudFile)
        }
    }

    fun onKeyBack(): Boolean {
        if (mDocumentViewModel.backDir()) {
            return true
        }
        return false
    }
}