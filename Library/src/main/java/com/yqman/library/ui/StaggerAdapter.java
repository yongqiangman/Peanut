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

package com.yqman.library.ui;

import com.yqman.wdiget.recyclerView.BaseRecyclerViewAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;

/**
 * target：解决server不返回宽高时，瀑布流来回跳转的问题；但是最优解还是server直接吐回宽高
 * 原理：detach时记录上次的高度、下次展示时使用上次的高度
 * @param <M>
 */
public abstract class StaggerAdapter<M> extends BaseRecyclerViewAdapter<M> {
    private SparseIntArray mHistoryHeight = new SparseIntArray();

    @Override
    protected void onBindChildrenViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int lastHeight = mHistoryHeight.get(i, 0);
        if (lastHeight != 0) {
            ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
            params.height = lastHeight;
            viewHolder.itemView.setLayoutParams(params);
        }
    }

    @Override
    protected void onChildrenViewDetachedFromWindow(RecyclerView.ViewHolder holder, int position) {
        int index = holder.getAdapterPosition();
        int height = holder.itemView.getMeasuredHeight();
        if (height != 0) { //540是测量得到的加载中图片的高度
            mHistoryHeight.put(index, height);
        }
        super.onChildrenViewDetachedFromWindow(holder, position);
    }

    protected void clearHistoryHeight() {
        mHistoryHeight.clear();
    }
}
