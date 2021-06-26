package kr.co.ecommtech.epsi.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;

public class PermissionActivity extends BaseActivity implements MultiplePermissionsListener {
    public static final List<String> requiredPermissions = new ArrayList<>();

    static {
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        requiredPermissions.add(Manifest.permission.NFC);
    }

    public static String[] permissions = {};

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_close, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            case R.id.btn_ok:
                checkPermissions();
                break;
        }
    }

    private void checkPermissions() {
        Dexter.withContext(this)
                .withPermissions(requiredPermissions)
                .withListener(this)
                .check();
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        if (multiplePermissionsReport.areAllPermissionsGranted()) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
        permissionToken.continuePermissionRequest();
    }
}
