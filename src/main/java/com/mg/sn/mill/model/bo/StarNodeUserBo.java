package com.mg.sn.mill.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 星节点用户
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class StarNodeUserBo implements Serializable {

    //是否冻结 0:未冻结 1:冻结
    private Integer freeze;

    //用户编号
    private String userNo;

    //电话号码
    private String mobile;

    //名称
    private String nickname;

    //青莲用户ID
    private String qingLianUserId;

    //星节点用户ID
    private String userId;

    //青莲云用户TOKEN
    private String qingLianUserToken;
}
