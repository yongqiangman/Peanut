package com.yqman.evan.network.util;

import java.io.File;
import java.util.HashMap;

/**
 * Created by manyongqiang on 2018/2/4.
 */

public interface INetworkTask {

    /**
     * 发送一个Get请求
     *
     * @param url       请求的url
     * @param params    请求的参数
     * @param headerMap 请求的head头
     *
     * @return 返回的jsonString
     */
    String sendGetRequest(String url, HashMap<String, String> params,
                          HashMap<String, String> headerMap);

    /**
     * 发送一个Post请求
     *
     * @param url       请求的url
     * @param params    请求的参数
     * @param headerMap 请求的head头
     *
     * @return 返回的jsonString
     */
    String sendPostRequest(String url, HashMap<String, String> params,
                           HashMap<String, String> headerMap, String contentType);

    /**
     * 上传一个文件
     *
     * @param url       上传的url
     * @param params    上传参数
     * @param headerMap 请求的head头
     * @param file      上传文件
     *
     * @return 是否成功
     */
    boolean uploadFile(String url, HashMap<String, String> params, HashMap<String, String> headerMap, File file);

    /**
     * 下载一个文件
     *
     * @param url       下载的url
     * @param params    下载的参数
     * @param file      下的文件存放的位置
     * @param headerMap 请求的head头
     *
     * @return 是否成功
     */
    boolean downloadFile(String url, HashMap<String, String> params,
                         HashMap<String, String> headerMap, File file);

    /**
     * 取消指定的Request请求
     *
     * @param url 对应的url请求
     *
     * @return 取消是否成功
     */
    void cancelRequest(String url);

    /**
     * 取消全部的网络请求
     *
     * @return 是佛取消成功
     */
    void cancelAll();
}
