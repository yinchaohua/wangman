package com.mg.sn.utils.Enum;

/**
 * 矿机控制
 */
public enum ControlKey {

    START_PROCESS ("1", "开始进程"),

    STOP_PROCESS ("2", "停止进程"),

    RESTART_PROCESS ("3", "重启进程"),

    RESTART_MINING ("4", "重启矿机");

    private String code;
    private String describe;

    ControlKey(String code, String describe) {
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
