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

package com.yqman.library.network.soup;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SimpleSoup {

    public void parse(File file) {
        try {
            Document doc = Jsoup.parse(file, "GB2312");
            Elements elements = doc.select("div._list_row2_list > *");
            for(Element element: elements){
                String detail = element.attr("href").substring(1);
                String str = element.select("img[data-src]").first().attr("data-src");

                //个人信息
                Element tmp = element.select("span._row2_person").first();
            }
        } catch (IOException e) {
            // do nothing
        }

    }
}
