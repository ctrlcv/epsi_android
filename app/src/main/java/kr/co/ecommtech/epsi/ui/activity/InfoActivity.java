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

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadTagFilters;

    public static final String CHARS = "0123456789ABCDEF";

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

        // get an instance of the context's cached NfcAdapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // if null is returned this demo cannot run. Use this check if the
        // "required" parameter of <uses-feature> in the manifest is not set
        if (mNfcAdapter == null){
            Utils.showToast(this, "이 단말기는 NFC를 지원하지 않습니다. 정보읽기를 실행할 수 없습니다.");
            finish();
            return;
        }

        checkNfcEnabled();
        initNfc();
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

        checkNfcEnabled();

        if (mNfcAdapter != null) {
            Log.d(TAG, "onResume(): " + getIntent());

            if (getIntent().getAction() != null) {
                if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                    NdefMessage[] msgs = getNdefMessagesFromIntent(getIntent());
                    NdefRecord record = msgs[0].getRecords()[0];
                    byte[] payload = record.getPayload();

                    String payloadString = new String(payload);
                    Log.d(TAG, "onResume() payloadString - " + payloadString);
                    Utils.showToast(this, payloadString);
                }
            }
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadTagFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            Log.d(TAG, "onPause()");
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void checkNfcEnabled() {
        Log.d(TAG, "checkNfcEnabled()");

        Boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("NFC is currently turned off")
                    .setMessage("Please turn on NFC in the Settings and then use the back button to return to this app.")
                    .setCancelable(false)
                    .setPositiveButton("Update Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id){
                                    startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            })
                    .create()
                    .show();
        }
    }

    private void initNfc() {
        Log.d(TAG, "initNfc()");

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Create intent filter to handle NDEF NFC tags detected from inside our
        // application when in "read mode":
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("application/kr.co.ecommtech.epsi");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Could not add MIME type.", e);
        }

        mReadTagFilters = new IntentFilter[] { ndefDetected };
    }

    NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();

        Log.d(TAG, "getNdefMessagesFromIntent() action:" + action);

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
            action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage)rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
        } else {
            Log.e(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    @Override
    public void onNewIntent(Intent passedIntent) {
        Log.d(TAG, "onNewIntent(): " + passedIntent);

        if (passedIntent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            NdefMessage[] msgs = getNdefMessagesFromIntent(passedIntent);
            String payload = new String(msgs[0].getRecords()[0].getPayload());
            Utils.showToast(this, payload);
        } else if (passedIntent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Utils.showToast(this, "This NFC tag has no NDEF data.");
        }

        super.onNewIntent(passedIntent);
    }

    private String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(CHARS.charAt(data[i] & 0x0F));
        }

        return sb.toString();
    }

    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        Log.d(TAG, "processTag()");

        if (rawMsgs == null) {
            Log.d(TAG, "processTag(), rawMsgs is null");
            return;
        }

        Utils.showToast(this, "스캔 성공");

        NdefMessage[] nDefMsgs;

        nDefMsgs = new NdefMessage[rawMsgs.length];
        for (int i = 0; i < rawMsgs.length; i++) {
            nDefMsgs[i] = (NdefMessage)rawMsgs[i];
            showTag(nDefMsgs[i]);
        }
    }

    private int showTag(NdefMessage nMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(nMessage);
        final int size = records.size();

        Log.d(TAG, "showTag()");

        for (int i = 0 ; i < size ; i++) {
            ParsedRecord record = records.get(i);

            int recordType = record.getType();
            String recordStr = "";

            if (recordType == ParsedRecord.TYPE_TEXT) {
                recordStr = "TEXT:" + ((TextRecord)record).getText();
            } else if (recordType == ParsedRecord.TYPE_URI) {
                recordStr = "URI:" + ((UriRecord)record).getUri().toString();
            }

//            mReadResult.append(recordStr + "\n");
        }

        return size;
    }
}
