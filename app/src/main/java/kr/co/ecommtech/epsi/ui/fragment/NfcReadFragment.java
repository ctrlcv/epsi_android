package kr.co.ecommtech.epsi.ui.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.nfc.NdefMessageParser;
import kr.co.ecommtech.epsi.ui.nfc.ParsedRecord;
import kr.co.ecommtech.epsi.ui.nfc.TextRecord;
import kr.co.ecommtech.epsi.ui.nfc.UriRecord;
import kr.co.ecommtech.epsi.ui.utils.Utils;

public class NfcReadFragment extends Fragment {
    private static String TAG = "NfcReadFragment";

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilter;
    private String[][] mTechLists;
    private Tag mTag;
    private IsoDep mTagComm;
    private StringBuilder mReadResult;

    public static final String CHARS = "0123456789ABCDEF";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nfcread, container, false);
        ButterKnife.bind(this, rootView);

        initNfc();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        Intent  targetIntent = new Intent(getActivity(), InfoActivity.class);
        mPendingIntent = PendingIntent.getActivity(getActivity(), 0, targetIntent, 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fail", e);
        }

        mFilter = new IntentFilter[] { ndef };
        mTechLists = new String[][] { new String[] { NfcA.class.getName() } };

        if (mReadResult == null) {
            mReadResult = new StringBuilder();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            Log.d(TAG, "onResume()");
            mNfcAdapter.enableForegroundDispatch(getActivity(), mPendingIntent, mFilter, mTechLists);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null) {
            Log.d(TAG, "onPause()");
            mNfcAdapter.disableForegroundDispatch(getActivity());
        }
    }

    public void onNewIntent(Intent passedIntent) {
        Log.d(TAG, "onNewIntent()");

        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            byte[] tagId = tag.getId();
            mReadResult.append("Tag ID:" + toHexString(tagId) + "\n");
        }

        Log.d(TAG, "onNewIntent():" + mReadResult.toString());

        if (passedIntent != null) {
            processTag(passedIntent);
        }
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

        Utils.showToast(getActivity(), "스캔 성공");

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

            mReadResult.append(recordStr + "\n");
        }

        return size;
    }



}
