package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class RestError {
    @SerializedName("success")
    public boolean success;

    @SerializedName("message")
    public String message;

    public RestError() {
        super();
    }

    public RestError(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RestError{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
