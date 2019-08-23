package com.mg.sn.utils.httpclient;


import com.alibaba.fastjson.JSONObject;
import com.mg.sn.utils.result.CommonConstant;

public class HttpClientResult {

    //返回状态码
    private int statusCode;
    //返回值
    private JSONObject object;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    /**
     * 判断是否请求成功
     * @return  成功：true; 失败：false
     */
    public boolean isSuccess () {
        if (CommonConstant.SUCCESS_CODE == statusCode) {
            return true;
        }
        return false;
    }
}
