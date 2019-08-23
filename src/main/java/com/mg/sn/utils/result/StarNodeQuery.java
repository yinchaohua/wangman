package com.mg.sn.utils.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class StarNodeQuery<T> extends Page<T> {

    public StarNodeQuery(Integer pageIndex, Integer pageSize){
        super(pageIndex == null || pageIndex == 0 ? 1 : pageIndex, pageSize == null || pageSize == 0 ? 10 : pageIndex);
    }
}
