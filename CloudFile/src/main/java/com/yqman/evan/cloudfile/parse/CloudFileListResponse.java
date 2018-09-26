package com.yqman.evan.cloudfile.parse;

import com.yqman.evan.cloudfile.io.model.CloudFile;
import com.yqman.evan.network.BaseResponse;

/**
 * Created by manyongqiang on 2018/2/6.
 */

public class CloudFileListResponse extends BaseResponse {
    public CloudFile[] list;
}
