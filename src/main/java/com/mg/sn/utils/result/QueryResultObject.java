package com.mg.sn.utils.result;

import java.util.List;

public class QueryResultObject {

    private long itemCount = 0L;
    private List items;

    public long getItemCount() {
        return itemCount;
    }

    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
}
