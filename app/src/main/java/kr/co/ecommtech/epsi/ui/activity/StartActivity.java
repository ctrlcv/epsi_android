package kr.co.ecommtech.epsi.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import kr.co.ecommtech.epsi.ui.utils.Utils;

public class StartActivity extends BaseActivity {
    private static final int REQUEST_PERMISSIONS = 1009;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
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

    private void checkPermissions() {
        for (String p : PermissionActivity.requiredPermissions) {
            boolean check = Utils.checkPermission(this, p);

            for (String permission : PermissionActivity.permissions) {
                if (permission.equals(p)) {
                    check = true;
                }
            }

            if (check == false) {
                startActivityForResult(new Intent(this, PermissionActivity.class), REQUEST_PERMISSIONS);
                return;
            }
        }

    }
}
