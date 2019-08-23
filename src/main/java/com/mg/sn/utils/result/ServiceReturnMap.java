package com.mg.sn.utils.result;

import java.util.HashMap;
import java.util.Map;

public class ServiceReturnMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 3858917861009893518L;
    private static String CODEFILED;
    private static String MSGFILED;
    private static String DATAFILED;

    public ServiceReturnMap() {
        this.put(CODEFILED, 0);
        this.put(MSGFILED, "操作成功！");
    }

    public static ServiceReturnMap returnObject(boolean flag, String msg) {
        return flag ? success(msg) : error(msg);
    }

    public static ServiceReturnMap error(String msg) {
        return error(msg, false);
    }

    public static ServiceReturnMap error(String msg, Object obj) {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(CODEFILED, 1);
        r.put(MSGFILED, msg);
        r.put(DATAFILED, obj);
        return r;
    }

    public static ServiceReturnMap error(Object obj) {
        return error("", obj);
    }

    public static ServiceReturnMap success() {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(DATAFILED, true);
        return r;
    }

    public static ServiceReturnMap success(String message) {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(MSGFILED, message);
        r.put(DATAFILED, message);
        return r;
    }

    public static ServiceReturnMap success(Object o) {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(DATAFILED, o);
        return r;
    }

    public static ServiceReturnMap success(String message, Object o) {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(DATAFILED, o);
        r.put(MSGFILED, message);
        return r;
    }

    public static ServiceReturnMap success(boolean flag) {
        ServiceReturnMap r = new ServiceReturnMap();
        if (!flag) {
            r.put(CODEFILED, 1);
        }

        r.put(MSGFILED, flag);
        r.put(DATAFILED, flag);
        return r;
    }

    public static ServiceReturnMap returnByFlag(boolean flag) {
        ServiceReturnMap r = new ServiceReturnMap();
        if (!flag) {
            r.put(CODEFILED, 1);
            r.put(MSGFILED, flag);
        }

        r.put(DATAFILED, flag);
        return r;
    }

    public static ServiceReturnMap success(Map<String, Object> map) {
        ServiceReturnMap r = new ServiceReturnMap();
        r.put(DATAFILED, map);
        return r;
    }

    public boolean isError() {
        return (Integer)this.get(CODEFILED) == 1;
    }

    public Object getData() {
        return this.get(DATAFILED);
    }

    public boolean isSuccess() {
        return !this.isError();
    }

    static {
        CODEFILED = CommonConstant.CODEFILED;
        MSGFILED = CommonConstant.MSGFILED;
        DATAFILED = CommonConstant.DATAFILED;
    }
}
