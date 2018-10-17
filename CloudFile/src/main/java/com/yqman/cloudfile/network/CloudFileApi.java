package com.yqman.cloudfile.network;

import java.util.HashMap;

import com.yqman.library.network.NetworkTaskManager;

import android.content.Context;

/**
 * Created by manyongqiang on 2018/2/6.
 */

public class CloudFileApi {
    private static final String DEFAULT_SERVER_URL = "http://pan.baidu.com/api/";

    public static String sendListRequest(Context context, String path) {
        String url = DEFAULT_SERVER_URL + "list";
        HashMap<String, String> params = new HashMap<>();
        params.put("dir", path);
        params.put("start", String.valueOf(0));
        params.put("limit", String.valueOf(200));
        return NetworkTaskManager.getInstance(context).sendGetRequest(url, params);
    }
}
