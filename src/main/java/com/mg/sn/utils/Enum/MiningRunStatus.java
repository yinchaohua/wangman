package com.mg.sn.utils.Enum;

/**
 * 矿机运行状态
 */
public enum MiningRunStatus {

    EXECUTE_MIDDLE ("1", "执行中"),

    START ("2", "启动"),

    STOP ("3", "暂停");

    private String code;
    private String describe;

    MiningRunStatus(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }

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
}
