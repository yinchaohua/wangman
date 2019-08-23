package com.mg.sn.mill.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.mg.sn.utils.common.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 调用星节点设备信息
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class StarNodeBo implements Serializable {

    //状态码
    private String errno;

    //返回数据
    private JSONObject data;

    //返回信息
    private String errmsg;

    //判断返回结果是否成功
    public boolean isSucc () {
        if (StringUtils.stringEquals(errno, "0") && StringUtils.stringEquals(errmsg, "执行成功")) {
            return true;
        }
        return false;
    }


}
