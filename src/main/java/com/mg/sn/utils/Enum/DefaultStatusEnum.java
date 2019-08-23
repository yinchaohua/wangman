package com.mg.sn.utils.Enum;

/**
 * 分组默认开启状态
 */
public enum DefaultStatusEnum {

    /**
     *  开启分组
     */
    OPEN ("1", "开启"),

    /**
     * 关闭分组
     */
    CLOSE ("2", "关闭");

    private String code;
    private String describe;

    DefaultStatusEnum(String code, String describe) {
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
