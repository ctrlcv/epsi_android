package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;

public class HomeActivity extends BaseActivity {
    private final static String TAG = "HomeActivity";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.readinfo_image, R.id.readinfo_btn, R.id.viewmap_image, R.id.viewmap_btn})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readinfo_image:
            case R.id.readinfo_btn:
                Intent readIntent = new Intent(getApplicationContext(), InfoActivity.class);
                readIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(readIntent);
                break;

            case R.id.viewmap_image:
            case R.id.viewmap_btn:
                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mapIntent);
                break;

            case R.id.home_btn:
                finish();
                break;

            default:
                break;
        }
    }
}
