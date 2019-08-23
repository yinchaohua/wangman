package com.mg.sn.mill.model.bo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 星节点返回DATA
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class StarNodeDataBo implements Serializable {

    //数据
    private JSONArray data;

    //每页记录数
    private Integer numsPerPage;

    //总记录数
    private Integer count;

    //总页数
    private Integer totalPages;

    //当前页数
    private Integer currentPage;

}
