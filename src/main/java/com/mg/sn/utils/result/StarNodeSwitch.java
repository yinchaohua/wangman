package com.mg.sn.utils.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class StarNodeSwitch {

    public static StarNodeResultObject dtoSwitch (Page page) {
        StarNodeResultObject resultObject = new StarNodeResultObject();
        resultObject.setPageIndex((int)page.getCurrent());
        resultObject.setItemCount(page.getPages());
        resultObject.setPageSize((int)page.getSize());
        resultObject.setTotal((int)page.getSize());
        resultObject.setItems(page.getRecords());
        return resultObject;
    }

    public static StarNodeResultObject dtoSwitch (Object data) {
        ArrayList<Object> list = Lists.newArrayList();
        list.add(data);
        return dtoSwitch(data);
    }

    public static StarNodeResultObject dtoSwitch (List list) {
        StarNodeResultObject resultObject = new StarNodeResultObject();
        resultObject.setPageIndex(1);
        resultObject.setItems(list);
        resultObject.setItemCount(list.size());
        resultObject.setPageSize(list.size());
        resultObject.setTotal(list.size());
        return resultObject;
    }

}
