package kr.co.ecommtech.epsi.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.dialog.CustomDialog;
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

        processStartApplication();
    }

    private void processStartApplication() {
        boolean isNotSupport = false;

        // NFC 미지원 단말
        if (Utils.hasFeature(this, PackageManager.FEATURE_NFC) == false) {
            isNotSupport = true;
        }

        if (isNotSupport) {
            new CustomDialog(this, new CustomDialog.CustomDialogListener() {
                @Override
                public void onCreate(Dialog dialog) {
                    dialog.setContentView(R.layout.dialog_alert);

                    TextView content = dialog.findViewById(R.id.tv_dlg_contents);
                    content.setText("NFC 미지원 단말입니다.");

                    TextView okBtn = dialog.findViewById(R.id.tv_btn_ok);
                    okBtn.setText("확인");
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
            }).show();
            return;
        }
    }
}
