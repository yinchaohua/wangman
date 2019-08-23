package com.mg.sn.utils.Enum;

/**
 * 设备运行状态1:正常;2:失联;3:删除
 */
public enum RunStatus {

    /**
     *  正常
     */
    NORMAL("1", "正常"),

    /**
     * 失联
     */
    MISS("2", "失联"),

    /**
     * 删除
     */
    DEL("3", "删除");

    private String code;
    private String describe;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    RunStatus (String code, String describe) {
        this.code = code;
        this.describe = describe;
    }
}
