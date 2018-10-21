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

package com.yqman.peanut.home.repository

import android.content.Context
import android.util.Log
import com.yqman.library.network.NetworkTaskManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

class ImageSoup {
    fun parse(context: Context, url: String): ArrayList<String> {
        val data = ArrayList<String>()
        val doc = Jsoup.parse(getRemoteFile(url, context), "utf-8")
        val parseData = ArrayList<String>()
        researchImg(doc, parseData)
        data.addAll(parseData)
        return data
    }

    /*
   方法作用：递归搜素body内的所有图片标签，并取出其URL，这里推荐使用jsoup的选择器，功能强大效率高，比递归我多了
    */
    private fun researchImg(root: Element, list: ArrayList<String>) {
        val son = root.children()
        if (son == null) {
            return
        } else {
            for (e in son) {
                researchImg(e, list)
                if (e.tagName() == "img") {
                    list.add(e.attr("src"))
                }
                Log.d("ImageSoup", "elements detail ${e.toString()}")
            }
        }
    }

    private fun getRemoteFile(url: String, context: Context): File {
        val network = NetworkTaskManager.getInstance(context)
        val file = File(context.cacheDir, "soup-${url.hashCode()}")
        file.createNewFile()
        network.downloadFile(url, null, null, file)
        Log.d("ImageSoup", "file Size ${file.length()}")
        return file
    }
}