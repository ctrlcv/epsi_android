package kr.co.ecommtech.epsi.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.fragment.AppInfoFragment;
import kr.co.ecommtech.epsi.ui.fragment.HomeFragment;
import kr.co.ecommtech.epsi.ui.fragment.WebViewFragment;
import kr.co.ecommtech.epsi.ui.services.Event;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.LoginManager;
import kr.co.ecommtech.epsi.ui.services.NfcService;

public class DefaultMainActivity extends BaseActivity {
    private static final String TAG = "DefaultMainActivity";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.activity_title)
    TextView mActivityTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.home_btn)
    ImageButton mHomeBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.switch_server)
    ImageView mSwitchServer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_write_layout)
    RelativeLayout mNfcWriteLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_save_layout)
    RelativeLayout mNfcSaveLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_dialog_title)
    TextView mDialogTitle;

    public interface OnBackPressedListener {
        public void onBack();
    }

    private OnBackPressedListener mBackListener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            Log.d(TAG, "onBackPressed() - isDrawerOpen()");
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if(mBackListener != null) {
            mBackListener.onBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_main);
        ButterKnife.bind(this);

        mActivityTitle.setText("");
        mHomeBtn.setVisibility(View.GONE);

        if (LoginManager.getInstance().isPreferServerData(this)) {
            mSwitchServer.setImageResource(R.drawable.switch_on);
        } else {
            mSwitchServer.setImageResource(R.drawable.switch_off);
        }

        HomeFragment groupFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentHodler, groupFragment);
        ft.commitAllowingStateLoss();
        drawerLayout.closeDrawer(GravityCompat.START);

        showKeyBoard(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_main_menu_linear_layout, R.id.btn_menu_close_imagebutton,
              R.id.btn_app_info_list, R.id.btn_login_list, R.id.btn_server_list,
              R.id.btn_manual_list, R.id.home_btn, R.id.btn_nfc_write_cancel})
    public void OnButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_menu_linear_layout:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;

            case R.id.btn_menu_close_imagebutton:
                Log.d(TAG, "MENU - btn_menu_close_imagebutton");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.btn_app_info_list: {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHodler, new AppInfoFragment());
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.btn_login_list:
                if (!LoginManager.getInstance().isLoggedIn(DefaultMainActivity.this)) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.btn_server_list:
                if (LoginManager.getInstance().isPreferServerData(this)) {
                    mSwitchServer.setImageResource(R.drawable.switch_off);
                    LoginManager.getInstance().setPreferServerData(this, false);
                } else {
                    mSwitchServer.setImageResource(R.drawable.switch_on);
                    LoginManager.getInstance().setPreferServerData(this, true);
                }
                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_REFRESH));
                break;

            case R.id.btn_manual_list: {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHodler, new WebViewFragment());
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.home_btn: {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
                    fragmentTransaction.commit();
                }
                break;

            case R.id.btn_nfc_write_cancel:
                if (NfcService.getInstance().isDisableCancel()) {
                    return;
                }

                mNfcWriteLayout.setVisibility(View.GONE);
                NfcService.getInstance().onPauseNfcMode();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        NfcService.getInstance().onResumeNfcMode(this, getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        NfcService.getInstance().onPauseNfcMode();
    }

    @Override
    public void onNewIntent(Intent passedIntent) {
        Log.d(TAG, "onNewIntent(): " + passedIntent);
        NfcService.getInstance().onNewIntentNfcMode(this, passedIntent);
        super.onNewIntent(passedIntent);
    }

    public void setTitle(String title) {
        mActivityTitle.setText(title.trim());
    }

    public void setHomeBtnVisible(boolean visible) {
        mHomeBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibleNfcReadDialog(boolean visible) {
        if (visible) {
            mDialogTitle.setText("TAG 읽기");
        }
        mNfcWriteLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibleNfcWriteDialog(boolean visible) {
        if (visible) {
            mDialogTitle.setText("TAG 쓰기");
        }
        mNfcWriteLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibleNfcSaveDialog(boolean visible) {
        mNfcSaveLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void initInputData() {
        Log.d(TAG, "initInputData()");

        NfcService.getInstance().setSerialNumber("");
        NfcService.getInstance().setPipeGroup("");
        NfcService.getInstance().setPipeGroupName("");
        NfcService.getInstance().setPipeGroupColor("");
        NfcService.getInstance().setPipeType("");
        NfcService.getInstance().setPipeTypeName("");
        NfcService.getInstance().setSetPosition("");
        NfcService.getInstance().setDistanceDirection("");
        NfcService.getInstance().setDistance(0.0);
        NfcService.getInstance().setDistanceLR(0.0);
        NfcService.getInstance().setDiameter(0.0);
        NfcService.getInstance().setMaterial("");
        NfcService.getInstance().setMaterialName("");
        NfcService.getInstance().setPipeDepth(0.0);
        NfcService.getInstance().setPositionX(0.0);
        NfcService.getInstance().setPositionY(0.0);
        NfcService.getInstance().setOfferCompany("");
        NfcService.getInstance().setCompanyPhone("");
        NfcService.getInstance().setMemo("");
        NfcService.getInstance().setBuildCompany("");
        NfcService.getInstance().setBuildPhone("");
        NfcService.getInstance().setSiteImageUrl("");
        NfcService.getInstance().setSiteImage(null);
        NfcService.getInstance().setLockPassword("");
        NfcService.getInstance().setNewPassword("");

        NfcService.getInstance().setReadMode(false);
        NfcService.getInstance().setWriteMode(false);

        setVisibleNfcReadDialog(false);
        setVisibleNfcWriteDialog(false);
    }

    public void showKeyBoard(boolean show) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        if (show) {
            imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, InputMethodManager.RESULT_SHOWN);
        } else {
            imm.hideSoftInputFromWindow(mDialogTitle.getWindowToken(), 0);
        }

    }

}