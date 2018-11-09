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

package com.yqman.peanut.home.ui.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

class HomeRepository(val application: Application) {
    private val bannerSrc = MutableLiveData<ArrayList<String>>().apply {
        value = ArrayList<String>().apply {
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535099255&di=aa27e9eb2b7bce3a1a7b6e60d6c613b8&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2F763293ce06bf1e684ef0ea3da43ae5008d8564b8.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536037&di=685e4c0cb8cbe41d4c6bc89a92267792&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fflower%2FAmazing_Color_Flowers_2560x1600_III%2Fwallpapers%2F2560x1600%2FFlowers_Wallpapers_91.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=0019339b4e285e1a3d279ab9efdc1f36&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170324%2F663c85eae74f4320a3e382f03af76d52_th.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534504536036&di=75ef000500890fd5d7c37d0c23e31436&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fe%2F58fb005766f6b.jpg")
        }
    }

    fun getBannerSrc() : LiveData<ArrayList<String>> {
        return bannerSrc
    }

    private val imgSrc = MutableLiveData<ArrayList<String>>()

    fun getImgSrc(url: String, isLoadMore: Boolean) : LiveData<ArrayList<String>> {
        Thread(Runnable {
            val newData = ImageSoup().parse(application, url)
            val lastData = imgSrc.value
            if (isLoadMore && lastData != null) {
                for (data in newData) {
                    if (!lastData.contains(data)) {
                        lastData.add(data)
                    }
                }
                imgSrc.postValue(lastData)
            } else {
                imgSrc.postValue(newData)
            }
        }).start()
        return imgSrc
    }
}