package com.mg.sn.mill.model.bo;

import com.mg.sn.utils.common.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 青莲云返回接口信息
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class QingLianBo implements Serializable {

    //返回信息
    private String msg;

    //返回编码
    private String code;

    //返回信息
    private String data;

    public boolean isSucc () {
        if (StringUtils.stringEquals(code, "0")) {
            return true;
        }
        return false;
    }


}
