package itu.banque.api;

import java.io.Serializable;

public class ResultObject implements Serializable {
    String message;
    boolean success;
    Object object;
    public ResultObject(String message, boolean success, Object object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }
    public ResultObject() {
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Object getObject() {
        return object;
    }
    public void setObject(Object object) {
        this.object = object;
    }
}
