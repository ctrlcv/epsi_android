package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.utils.Utils;

public class InfoActivity extends BaseActivity {
    private static final String TAG = "InfoActivity";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_tablayout)
    TabLayout mReadInfoLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_viewpager)
    ViewPager2 mReadInfoViewPager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_write_layout)
    RelativeLayout mNfcWriteLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_save_layout)
    RelativeLayout mNfcSaveLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_dialog_title)
    TextView mDialogTitle;

    InfoPageAdapter mInfoPageAdapter;

    final List<String> mTabElement = Arrays.asList("관로정보", "평면도", "단면도");

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        mInfoPageAdapter = new InfoPageAdapter(this);
        mReadInfoViewPager.setAdapter(mInfoPageAdapter);

        new TabLayoutMediator(mReadInfoLayout, mReadInfoViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(mTabElement.get(position));
            }
        }).attach();

        mReadInfoViewPager.setCurrentItem(0);

        NfcService.getInstance().initializeNfcMode(this);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.home_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_btn:
                finish();
                break;

            case R.id.btn_nfc_write_cancel:
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
    protected void onDestroy() {
        super.onDestroy();
        initInputData();
    }

    @Override
    public void onNewIntent(Intent passedIntent) {
        Log.d(TAG, "onNewIntent(): " + passedIntent);
        NfcService.getInstance().onNewIntentNfcMode(this, passedIntent);
        super.onNewIntent(passedIntent);
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
}

