package com.mg.sn.utils.result;

public class StarNodeWrappedResult extends WrappedResult {

    private int code;

    public static StarNodeWrappedResult successWrapedResult(Object data) {
        return data instanceof ServiceReturnMap ? returnWrapedResult((ServiceReturnMap)data) : new StarNodeWrappedResult(true, data, "", "", "");
    }

    public static StarNodeWrappedResult successWrapedResult(String message, Object data) {
        return data instanceof ServiceReturnMap ? returnWrapedResult((ServiceReturnMap)data) : new StarNodeWrappedResult(true, data, message, "", "");
    }

    public static StarNodeWrappedResult failWrapedResult(String message) {
        return new StarNodeWrappedResult(false, (Object)null, message, "", "");
    }

    public static StarNodeWrappedResult failWrapedResult(String message, Object object) {
        return new StarNodeWrappedResult(false, object, message, "", "");
    }

    public static StarNodeWrappedResult returnWrapedResult(ServiceReturnMap serviceReturnMap) {
        String code = serviceReturnMap.getOrDefault(CommonConstant.CODEFILED, "-1").toString();
        String msg = serviceReturnMap.getOrDefault(CommonConstant.MSGFILED, "-1").toString();
        Object object = serviceReturnMap.get(CommonConstant.DATAFILED);
        return 0 == Integer.parseInt(code) ? successWrapedResult(msg, object) : failWrapedResult(msg, object);
    }

    public StarNodeWrappedResult(boolean isSuccess, Object data, String tip, String errorPage, String type) {
        super(isSuccess, data, tip, errorPage, type);
        this.code = isSuccess ? 0 : 1;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String toString() {
        return "StarNodeWrappedResult(code=" + this.getCode() + ")";
    }


    protected boolean canEqual(Object other) {
        return other instanceof StarNodeWrappedResult;
    }


}
