package be.belga.reporter.entity;

import java.io.Serializable;

public class RestResponse<T> implements Serializable {
    private static final long serialVersionUID = 5812265429092541151L;
    
    private boolean success;
    private int errorCode;
    private String errorMessage;
    private T data;
    
    public RestResponse() {
        success = true;
    }
    public RestResponse(boolean success, int successCode, T data) {
        this.success = success;
        this.errorCode = successCode;
        this.data = data;
    }
    public RestResponse(int errorCode, String errorMessage) {
        success = false;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public boolean isSuccess() {
        return success;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public T getData() {
        return data;
    }
}
