package com.yqman.evan.activity

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.yqman.evan.BaseActivity
import com.yqman.evan.R
import com.yqman.monitor.LogHelper
import com.yqman.wdiget.HorizontalDotView
import com.yqman.wdiget.HorizontalScrollPage
import com.yqman.wdiget.ToastHelper

class BannerActivity : BaseActivity(), HorizontalScrollPage.OnItemClickedListener {
    companion object {
        const val TAG = "BannerActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        val view : HorizontalScrollPage = findViewById(R.id.scroll_page)
        val dot : HorizontalDotView = findViewById(R.id.dot_page)
        view.mItemClickListener = this
        view.mItemSelectedListener = object : HorizontalScrollPage.OnItemSelectedListener {
            override fun onItemSelect(prePos: Int, selected: Int, sum: Int) {
                dot.updatePos(prePos, selected, sum)
            }
        }
        view.mImageLoader = object : HorizontalScrollPage.ImageLoader {
            override fun updateImageView(imageView: ImageView, url: String) {
                Picasso.get().load(url).into(imageView)
            }
        }
        val list = ArrayList<String>().apply {
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
        }
        view.setImageResource(list)
        Handler().postDelayed({view.setImageResource(ArrayList())}, 10_000)
        Handler().postDelayed({
            list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=0019339b4e285e1a3d279ab9efdc1f36&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170324%2F663c85eae74f4320a3e382f03af76d52_th.jpg")
            list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=75ef000500890fd5d7c37d0c23e31436&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fe%2F58fb005766f6b.jpg")
            view.setImageResource(list)
        }, 20_000)
    }

    override fun onClickItem(pos: Int) {
        LogHelper.d(TAG, "pos $pos")
        ToastHelper.showToast(this, "index$pos")
    }
}