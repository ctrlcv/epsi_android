package kr.co.ecommtech.epsi.ui.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import kr.co.ecommtech.epsi.ui.data.LogIn;
import kr.co.ecommtech.epsi.ui.data.RestError;
import kr.co.ecommtech.epsi.ui.network.HttpClient;
import kr.co.ecommtech.epsi.ui.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {
    private final static String TAG = "LoginManager";

    private static LoginManager mSingletonInstance;
    private static LogIn mLogInInfo = null;
    private OnChangedLoginStateListener mListener;

    public static LoginManager getInstance() {
        if (mSingletonInstance == null) {
            synchronized (LoginManager.class) {
                mSingletonInstance = new LoginManager();
            }
        }
        return mSingletonInstance;
    }

    public interface OnChangedLoginStateListener {
        void onChangedLogIn(boolean isLogin, String message);
    }

    public synchronized  LogIn getLogInInfo() {
        return mLogInInfo;
    }

    public void setOnChangedLoginStateListener(OnChangedLoginStateListener listener) {
        mListener = listener;
    }

    public synchronized void requestLogIn(Context context, String email, String password) {
        QueryService elQueryService = HttpClient.getRetrofit().create(QueryService.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("userid", email);
        map.put("password", password);

        Call<LogIn> call = elQueryService.logIn(map);

        call.enqueue(new Callback<LogIn>() {
            @Override
            public void onResponse(Call<LogIn> call, Response<LogIn> response) {
                if (response.isSuccessful()){
                    LogIn loginInfo = response.body();

                    Objects.requireNonNull((Activity)context).runOnUiThread(new Runnable(){
                        public void run(){
                            if (loginInfo == null) {
                                Log.e(TAG, "requestLogin() loginInfo is null, return");
                                return;
                            }

                            if (mLogInInfo == null) {
                                mLogInInfo = new LogIn();
                            }

                            mLogInInfo = loginInfo;

                            if (isAutoLogIn(context)) {
                                storeLogInInfo(context, mLogInInfo);
                            }

                            Log.d(TAG,"requestLogin() Login success!!");
                            if (mListener != null) {
                                mListener.onChangedLogIn(true, loginInfo.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"requestLogin() Status Code : " + response.code());
                    RestError error = Utils.parseError(response);

                    if (mListener != null) {
                        mListener.onChangedLogIn(false, error.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LogIn> call, Throwable t) {
                Log.e(TAG,"requestLogin() Fail msg : " + t.getMessage());
                if (mListener != null) {
                    mListener.onChangedLogIn(false, t.getMessage());
                }
            }
        });
    }

    public synchronized boolean isPreferServerData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isPreferServer = sharedPreferences.getBoolean("isPreferServer", false);

        Log.d(TAG, "isPreferServer:" + isPreferServer);
        return isPreferServer;
    }

    public synchronized  void setPreferServerData(Context context, boolean set) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "setPreferServerData set:" + set);
        editor.putBoolean("isPreferServer", set);
        editor.apply();
    }

    public synchronized boolean isAutoLogIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false);

        Log.d(TAG, "isAutoLogin:" + isAutoLogin);
        return isAutoLogin;
    }

    public synchronized void initLoginInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        mLogInInfo = null;
    }

    public synchronized boolean isLoggedIn(Context context) {
        LogIn logInInfo = loadLogInInfo(context);
        if (logInInfo != null) {
            // 로그인 정보 있음
            if (!TextUtils.isEmpty(logInInfo.getAccessToken()) && !TextUtils.isEmpty(logInInfo.getRefreshToken())) {
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String accessDate = dayTime.format(new Date(logInInfo.getExpiresDate()));
                String refreshDate = dayTime.format(new Date(logInInfo.getRefreshExpiresDate()));
                String currentTime = dayTime.format(new Date(System.currentTimeMillis()));

                Log.d(TAG, accessDate + "," + refreshDate + "," + currentTime);

                long t = logInInfo.getRefreshExpiresDate() - System.currentTimeMillis();
                if (t > 0) {
                    Log.d(TAG, "로그인 기간 남음 t:" + t);
                    return true;
                } else {
                    Log.d(TAG, "토큰만료");
                    return false;
                }
            } else {
                Log.d(TAG, "토큰 또는 사용자 정보 없음");
                return false;
            }
        } else {
            Log.d(TAG, "토큰 또는 사용자 정보 없음");
            return false;
        }
    }

    private synchronized LogIn loadLogInInfo(Context context) {
        if (mLogInInfo != null) {
            return mLogInInfo;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false);
        if (!isAutoLogin) {
            return null;
        }

        mLogInInfo = new LogIn();
        mLogInInfo.setId(sharedPreferences.getInt("id", 0));
        mLogInInfo.setUserId(sharedPreferences.getString("userId", null));
        mLogInInfo.setUserName(sharedPreferences.getString("userName", null));
        mLogInInfo.setAuth(sharedPreferences.getString("auth", null));
        mLogInInfo.setAccessToken(sharedPreferences.getString("accessToken", null));
        mLogInInfo.setExpiresDate(sharedPreferences.getLong("expiresDate", 0));
        mLogInInfo.setRefreshToken(sharedPreferences.getString("refreshToken", null));
        mLogInInfo.setRefreshExpiresDate(sharedPreferences.getLong("refreshTokenExpiresDate", 0));

        return mLogInInfo;
    }

    public synchronized void initLogInInfo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        mLogInInfo = null;
    }

    private synchronized void storeLogInInfo(Context context, LogIn logInInfo) {
        if (logInInfo == null) {
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

//        editor.putBoolean("isAutoLogin", true);

        editor.putInt("id", logInInfo.getId());
        editor.putString("userId", logInInfo.getUserId());
        editor.putString("userName", logInInfo.getUserName());
        editor.putString("auth", logInInfo.getAuth());

        editor.putString("accessToken", logInInfo.getAccessToken());
        editor.putLong("expiresDate", logInInfo.getExpiresDate());
        editor.putString("refreshToken", logInInfo.getRefreshToken());
        editor.putLong("refreshTokenExpiresDate", logInInfo.getRefreshExpiresDate());

        editor.apply();
    }
}
