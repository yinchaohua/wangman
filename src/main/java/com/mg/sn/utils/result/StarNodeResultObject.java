package com.mg.sn.utils.result;

public class StarNodeResultObject extends QueryResultObject {

    /**
     * 总数
     */
    private int total;

    /**
     * 页数
     */
    private int pageSize;

    /**
     * 页码
     */
    private int pageIndex;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
