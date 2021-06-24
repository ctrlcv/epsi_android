package kr.co.ecommtech.epsi.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends BaseActivity {
    public static final List<String> requiredPermissions = new ArrayList<>();

    static {
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.NFC);
    }

    public static String[] permissions = {};

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
