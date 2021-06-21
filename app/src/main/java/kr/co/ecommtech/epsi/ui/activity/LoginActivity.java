package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.services.LoginManager;
import kr.co.ecommtech.epsi.ui.utils.Utils;

public class LoginActivity extends BaseActivity implements LoginManager.OnChangedLoginStateListener {
    private final static String TAG = "LoginActivity";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_userid)
    EditText mUserIdEt;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_password)
    EditText mPasswordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.login_btn})
    public void onClick(View view) {
        if (view.getId() != R.id.login_btn) {
            return;
        }

        String userId = mUserIdEt.getText().toString();
        String password = mPasswordEt.getText().toString();

        if (TextUtils.isEmpty(userId)) {
            Utils.showToast(this, "아이디를 입력하세요.");
            mUserIdEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Utils.showToast(this, "아이디를 입력하세요.");
            mPasswordEt.requestFocus();
            return;
        }

        LoginManager.getInstance().requestLogIn(this, userId, password);
        LoginManager.getInstance().setOnChangedLoginStateListener(this);
    }

    @Override
    public void onChangedLogIn(boolean isLogin, String message) {
        if (isLogin) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Log.i(TAG, "onChangedLogin() isLogin false, message:" + message);
            Utils.showToast(this, message);
        }
    }
}
