package com.mg.sn.utils.result;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mg.sn.utils.common.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 初始化设备返回对象
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class InitEquipmentResult {

    //编码
    private String code;

    //信息
    private String message;

    //对象
    private Object obj;

    /**
     * 判断是否成功
     * @return
     */
    public boolean isSucc () {
        if (StringUtils.stringEquals(code, "00")) {
            return true;
        }
        return false;
    }
}
