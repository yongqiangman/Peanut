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
import com.yqman.wdiget.HorizontalDotView
import com.yqman.wdiget.HorizontalScrollPage
import com.yqman.wdiget.ToastHelper
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment(), HorizontalScrollPage.OnItemClickedListener {
    companion object {
        const val TAG = "Home"
    }

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

    val imgSrc = ArrayList<String>().apply {
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
        add("http://c.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbffea08a4c14d8bc3eb13541a3.jpg")
        add("http://g.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcb944f655cb3096b63f624a889.jpg")
        add("http://f.hiphotos.baidu.com/image/pic/item/f603918fa0ec08faf4f358d454ee3d6d54fbdad6.jpg")
        add("http://a.hiphotos.baidu.com/image/h%3D300/sign=e11b6bfe6181800a71e58f0e813533d6/d50735fae6cd7b89ae12a8f2022442a7d9330e40.jpg")
        add("http://e.hiphotos.baidu.com/image/h%3D300/sign=ff937ebff1039245beb5e70fb795a4a8/b8014a90f603738d952a8450be1bb051f819ec64.jpg")
        add("http://b.hiphotos.baidu.com/image/h%3D300/sign=504c7d57bd51f819ee25054aeab54a76/d6ca7bcb0a46f21fd757a52ffb246b600c33ae6f.jpg")
        add("http://e.hiphotos.baidu.com/image/h%3D300/sign=b80e374910950a7b6a3548c43ad1625c/c8ea15ce36d3d539420663bc3787e950352ab092.jpg")
        add("http://h.hiphotos.baidu.com/image/h%3D300/sign=7c6df3a83bd12f2ed105a8607fc3d5ff/94cad1c8a786c91778bfc046c43d70cf3bc75714.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
        add("http://c.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbffea08a4c14d8bc3eb13541a3.jpg")
        add("http://g.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcb944f655cb3096b63f624a889.jpg")
        add("http://f.hiphotos.baidu.com/image/pic/item/f603918fa0ec08faf4f358d454ee3d6d54fbdad6.jpg")
        add("http://a.hiphotos.baidu.com/image/h%3D300/sign=e11b6bfe6181800a71e58f0e813533d6/d50735fae6cd7b89ae12a8f2022442a7d9330e40.jpg")
        add("http://e.hiphotos.baidu.com/image/h%3D300/sign=ff937ebff1039245beb5e70fb795a4a8/b8014a90f603738d952a8450be1bb051f819ec64.jpg")
        add("http://b.hiphotos.baidu.com/image/h%3D300/sign=504c7d57bd51f819ee25054aeab54a76/d6ca7bcb0a46f21fd757a52ffb246b600c33ae6f.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
        add("http://c.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbffea08a4c14d8bc3eb13541a3.jpg")
        add("http://g.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcb944f655cb3096b63f624a889.jpg")
        add("http://f.hiphotos.baidu.com/image/pic/item/f603918fa0ec08faf4f358d454ee3d6d54fbdad6.jpg")
        add("http://a.hiphotos.baidu.com/image/h%3D300/sign=e11b6bfe6181800a71e58f0e813533d6/d50735fae6cd7b89ae12a8f2022442a7d9330e40.jpg")
        add("http://e.hiphotos.baidu.com/image/h%3D300/sign=ff937ebff1039245beb5e70fb795a4a8/b8014a90f603738d952a8450be1bb051f819ec64.jpg")
        add("http://b.hiphotos.baidu.com/image/h%3D300/sign=504c7d57bd51f819ee25054aeab54a76/d6ca7bcb0a46f21fd757a52ffb246b600c33ae6f.jpg")

    }

    val bannerSrc = ArrayList<String>().apply {
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=0019339b4e285e1a3d279ab9efdc1f36&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170324%2F663c85eae74f4320a3e382f03af76d52_th.jpg")
        add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=75ef000500890fd5d7c37d0c23e31436&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fe%2F58fb005766f6b.jpg")
    }

    private fun initData() {
        updateBannerResource(bannerSrc)
        updateImageResource(imgSrc)
    }

    override fun onClickItem(pos: Int) {
        LogHelper.d(TAG, "pos $pos")
        ToastHelper.showToast(context, "index$pos")
    }
}