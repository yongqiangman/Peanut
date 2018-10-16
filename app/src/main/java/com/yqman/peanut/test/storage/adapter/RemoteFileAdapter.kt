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

package com.yqman.peanut.test.storage.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yqman.peanut.R
import com.yqman.peanut.databinding.ItemFileListBinding
import com.yqman.peanut.test.storage.presenter.DocumentPresenter

class RemoteFileAdapter : RecyclerView.Adapter<BindViewHolder>() {
    private var mRemoteFiles: List<DocumentPresenter.FileView>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder {
        val mBinding = DataBindingUtil.inflate<ItemFileListBinding>(LayoutInflater.from(parent.context),
        R.layout.item_file_list, parent, false)
        mBinding.listener = mOnItemClickListener
        return BindViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return mRemoteFiles?.size?:0
    }

    fun updateData(files: List<DocumentPresenter.FileView>) {
        mRemoteFiles = files
        if (mRemoteFiles != null) {
            notifyDataSetChanged()
        }
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemCLickListener(onItemCLickListener: OnItemClickListener) {
        mOnItemClickListener = onItemCLickListener
    }

    interface OnItemClickListener {
        fun onItemClick(cloudFile: DocumentPresenter.FileView)
    }

    override fun onBindViewHolder(holder: BindViewHolder, position: Int) {
        holder.item.file = getRemoteFile(position)
        holder.item.executePendingBindings()
    }

    private fun getRemoteFile(position: Int): DocumentPresenter.FileView {
        return mRemoteFiles!![position]
    }
}

class BindViewHolder(val item : ItemFileListBinding) : RecyclerView.ViewHolder(item.root)