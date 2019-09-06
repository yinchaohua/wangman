package com.mg.sn.utils.paramObject;

/**
 * 设备批量分组传入参数对象
 */
public class IDRequestObject {

    /**
     * id数组
     */
    private String[] ids;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     *  控制指令
     */
    private String controlKey;

    public IDRequestObject () {

    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getControlKey() {
        return controlKey;
    }

    public void setControlKey(String controlKey) {
        this.controlKey = controlKey;
    }
}
