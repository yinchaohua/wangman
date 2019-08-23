package com.mg.sn.utils.result;

public class WrappedResult {

    private static final String DEFAULT_INFOTYPE = "error";
    private boolean successful;
    private Object resultValue;
    private String resultHint;
    private String errorPage;
    private String type = "error";

    public WrappedResult() {
    }

    public static WrappedResult successWrapedResult(Object data) {
        return new WrappedResult(true, data, "", "", "");
    }

    public static WrappedResult failedWrappedResult(String exMessage) {
        return new WrappedResult(false, "", exMessage);
    }

    public static WrappedResult failedWrappedResult(String exMessage, String errorPage) {
        return new WrappedResult(false, "", exMessage, errorPage, "error");
    }

    public static WrappedResult failedWrappedResult(String exMessage, String errorPage, String type) {
        return new WrappedResult(false, "", exMessage, errorPage, type);
    }

    public WrappedResult(boolean isSuccess, Object data, String tip, String errorPage, String type) {
        this.successful = isSuccess;
        this.resultValue = data;
        this.resultHint = tip;
        this.errorPage = errorPage;
        this.type = type;
    }

    public WrappedResult(boolean isSuccess, Object data, String tip) {
        this.successful = isSuccess;
        this.resultValue = data;
        this.resultHint = tip;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Object getResultValue() {
        return this.resultValue;
    }

    public void setResultValue(Object resultValue) {
        this.resultValue = resultValue;
    }

    public String getResultHint() {
        return this.resultHint;
    }

    public void setResultHint(String resultHint) {
        this.resultHint = resultHint;
    }

    public String getErrorPage() {
        return this.errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
