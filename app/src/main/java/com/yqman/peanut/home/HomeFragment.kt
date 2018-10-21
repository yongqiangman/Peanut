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

package com.yqman.peanut.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.yqman.monitor.LogHelper
import com.yqman.peanut.R
import com.yqman.peanut.util.ImagePreviewActivity
import com.yqman.peanut.util.getActivityViewModel
import com.yqman.wdiget.HorizontalDotView
import com.yqman.wdiget.HorizontalScrollPage
import com.yqman.wdiget.ToastHelper
import com.yqman.wdiget.recyclerView.item.SimpleLoadMoreFooter
import com.yqman.wdiget.recyclerView.item.SimpleRefreshHeader
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment(), HorizontalScrollPage.OnItemClickedListener {
    companion object {
        const val TAG = "Home"
    }

    private var imgSrc: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container?.context).inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        initBannerView(root)
        initRecyclerView()
        initData()
    }

    private fun initBannerView(root: View) {
        val bannerView : HorizontalScrollPage = root.findViewById(R.id.scroll_page)
        val bannerDot : HorizontalDotView = root.findViewById(R.id.dot_page)
        bannerView.setItemClickListener(this)
        bannerView.setItemSelectedListener { selected, sum ->
            bannerDot.updatePos(selected, sum)
        }
        bannerView.setImageLoader { imageView, url ->
            Picasso.get().load(url).into(imageView)
        }
    }

    private fun updateBannerResource(list: ArrayList<String>) {
        scroll_page.setImageResource(list)
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recycler_view.adapter = HomeAdapter().apply {
            refreshHeader = SimpleRefreshHeader(context).apply {
                setRefreshListener {
                    getActivityViewModel<HomeViewModel>()?.refresh()
                }
            }
            loadMoreFooter = SimpleLoadMoreFooter(context).apply {
                setLoadMoreListener {
                    getActivityViewModel<HomeViewModel>()?.loadMore()
                }
            }
            enableLoadMoreEvent(true)
            setItemClickListener {
                item, position ->
                activity?.apply {
                    startActivity(ImagePreviewActivity.getIntent(this, position, imgSrc))
                }
            }
        }

    }

    private fun updateImageResource(list: ArrayList<String>) {
        val adapter = recycler_view.adapter
        if (adapter is HomeAdapter) {
            adapter.list = list
        }
    }

    private fun initData() {
        getActivityViewModel<HomeViewModel>()?.apply {
            getBannerSrc().observe(this@HomeFragment, Observer {
                if (it != null) {
                    updateBannerResource(it)
                }
            })
            refresh().observe(this@HomeFragment, Observer {
                val adapter = recycler_view.adapter
                if (adapter is HomeAdapter) {
                    adapter.refreshHeader?.completeRefresh()
                    adapter.loadMoreFooter?.completeLoadMore()
                }
                if (it != null) {
                    imgSrc = it
                    updateImageResource(it)
                }
            })
        }
    }

    override fun onClickItem(pos: Int) {
        LogHelper.d(TAG, "pos $pos")
        ToastHelper.showToast(context, "index$pos")
    }
}