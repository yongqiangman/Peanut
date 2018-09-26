package com.yqman.evan.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manyongqiang on 2018/2/6.
 */

public class BaseResponse {

    @SerializedName("errno")
    public int errno;

    @SerializedName("errmsg")
    public String errmsg;
}
