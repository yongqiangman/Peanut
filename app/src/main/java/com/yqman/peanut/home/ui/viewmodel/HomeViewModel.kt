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

package com.yqman.peanut.home.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.yqman.peanut.home.ui.repository.HomeRepository

class HomeViewModel(application: Application): AndroidViewModel(application) {

    // 网址比如："http://m.27270.com/tag/320.html"
    private var mUrl = "http://m.27270.com/tag/"
    private val INIT_PAGE = 320
    private var mCurrentPage = INIT_PAGE
    private val mRepository = HomeRepository(application)

    fun getBannerSrc() = mRepository.getBannerSrc()

    fun refresh() : LiveData<ArrayList<String>> {
        mCurrentPage = INIT_PAGE
        val url = "$mUrl$mCurrentPage.html"
        return mRepository.getImgSrc(url, false)
    }

    fun loadMore() : LiveData<ArrayList<String>> {
        mCurrentPage++
        val url = "$mUrl$mCurrentPage.html"
        return mRepository.getImgSrc(url, true)
    }
}