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

package com.yqman.peanut.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.squareup.picasso.Picasso
import com.yqman.peanut.BaseActivity
import com.yqman.peanut.R
import kotlinx.android.synthetic.main.activity_image_preview.*

private const val IMAGE_PREVIEW_IMG_RESOURCES = "IMAGE_PREVIEW_IMG_RESOURCES"
private const val IMAGE_PREVIEW_INIT_POSITION = "IMAGE_PREVIEW_INIT_POSITION"
class ImagePreviewActivity: BaseActivity() {
    companion object {
        fun getIntent(context: Context, initPosition: Int, imgResource: ArrayList<String>)
                = Intent(context, ImagePreviewActivity::class.java).apply {
            putStringArrayListExtra(IMAGE_PREVIEW_IMG_RESOURCES, imgResource)
            putExtra(IMAGE_PREVIEW_INIT_POSITION, initPosition)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN) // 当前Activity全屏显示
        setContentView(R.layout.activity_image_preview)
        val initPosition = intent.getIntExtra(IMAGE_PREVIEW_INIT_POSITION, 0)
        val imgResources = intent.getStringArrayListExtra(IMAGE_PREVIEW_IMG_RESOURCES)
        scroll_page.setImageLoader { imageView, url ->
            Picasso.get().load(url).into(imageView)
        }
        if (imgResources != null) {
            scroll_page.setImageResource(initPosition, imgResources)
        }
    }
}