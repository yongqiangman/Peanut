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

package com.yqman.peanut.home.ui.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.yqman.library.ui.StaggerAdapter
import com.yqman.peanut.R

class HomeAdapter: StaggerAdapter<String>() {

    var list: ArrayList<String>? = null
    set(value) {
        field = value
        clearHistoryHeight()
        notifyDataSetChanged()
    }

    override fun onCreateChildrenViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_image, p0, false)
        return ImageViewHolder(view)
    }

    override fun getChildrenItemCount() = list?.size?:0

    override fun onBindChildrenViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        super.onBindChildrenViewHolder(viewHolder, i)
        val url = list?.get(i)
        url?.apply {
            if (viewHolder is ImageViewHolder) {
                Picasso.get().load(this).into(viewHolder.imageView)
            }
            viewHolder.itemView.setOnClickListener {
                mItemClickListener.onItemClick(url, i)
            }
        }
    }
}

class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView)
}