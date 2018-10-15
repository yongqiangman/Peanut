package com.yqman.cloudfile.parse;

import com.yqman.cloudfile.io.model.CloudFile;
import com.yqman.network.BaseResponse;

/**
 * Created by manyongqiang on 2018/2/6.
 */

public class CloudFileListResponse extends BaseResponse {
    public CloudFile[] list;
}
