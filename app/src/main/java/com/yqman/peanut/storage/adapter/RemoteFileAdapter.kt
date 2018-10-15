package com.yqman.peanut.storage.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yqman.peanut.R
import com.yqman.peanut.databinding.ItemFileListBinding
import com.yqman.peanut.storage.presenter.DocumentPresenter

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