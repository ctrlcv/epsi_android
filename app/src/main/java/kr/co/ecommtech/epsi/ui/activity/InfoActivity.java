package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import kr.co.ecommtech.epsi.ui.nfc.NdefMessageParser;
import kr.co.ecommtech.epsi.ui.nfc.ParsedRecord;
import kr.co.ecommtech.epsi.ui.nfc.TextRecord;
import kr.co.ecommtech.epsi.ui.nfc.UriRecord;
import kr.co.ecommtech.epsi.ui.utils.Utils;

public class InfoActivity extends BaseActivity {
    private static final String TAG = "InfoActivity";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_tablayout)
    TabLayout mReadInfoLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_viewpager)
    ViewPager2 mReadInfoViewPager;

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
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.home_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume(): ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause(): ");
    }


    @Override
    public void onNewIntent(Intent passedIntent) {
        Log.d(TAG, "onNewIntent(): " + passedIntent);
        super.onNewIntent(passedIntent);
    }
}
