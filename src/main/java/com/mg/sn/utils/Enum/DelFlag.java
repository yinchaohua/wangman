package com.mg.sn.utils.Enum;

/**
 * 删除状态
 */
public enum DelFlag {

    DEL("1", "删除"),

    NOT_DEL ("2", "未删除");

    private String code;
    private String describe;

    DelFlag(String code, String describe) {
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
