package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class LogIn {
    @SerializedName("success")
    boolean success;

    @SerializedName("message")
    String message;

    @SerializedName("id")
    int id;

    @SerializedName("userid")
    String userId;

    @SerializedName("username")
    String userName;

    @SerializedName("auth")
    String auth;

    @SerializedName("accesstoken")
    String accessToken;

    @SerializedName("expiresdate")
    long expiresDate;

    @SerializedName("refreshtoken")
    String refreshToken;

    @SerializedName("refreshexpiresdate")
    long refreshExpiresDate;

    public LogIn() {

    }

    public LogIn(boolean success, String message, int id, String userId, String userName, String auth, String accessToken, long expiresDate, String refreshToken, long refreshExpiresDate) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.auth = auth;
        this.accessToken = accessToken;
        this.expiresDate = expiresDate;
        this.refreshToken = refreshToken;
        this.refreshExpiresDate = refreshExpiresDate;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(long expiresDate) {
        this.expiresDate = expiresDate;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getRefreshExpiresDate() {
        return refreshExpiresDate;
    }

    public void setRefreshExpiresDate(long refreshExpiresDate) {
        this.refreshExpiresDate = refreshExpiresDate;
    }
}
