package kr.co.ecommtech.epsi.ui.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import kr.co.ecommtech.epsi.ui.utils.Utils;

public class NfcService {
    private final static String TAG = "NfcService";

    private final static String TAG_PIPE_GROUP = "|1:";
    private final static String TAG_PIPE_GROUP_NAME = "|2:";
    private final static String TAG_PIPE_TYPE = "|3:";
    private final static String TAG_PIPE_TYPENAME = "|4:";
    private final static String TAG_SET_POSITION = "|5:";
    private final static String TAG_DISTANCE_DIRECTION = "|6:";
    private final static String TAG_DISTANCE = "|7:";
    private final static String TAG_DISTANCE_LR = "|8:";
    private final static String TAG_PIPE_DEPTH = "|9:";
    private final static String TAG_PIPE_DIAMETER = "|A:";
    private final static String TAG_MATERIAL = "|B:";
    private final static String TAG_MATERIAL_NAME = "|C:";
    private final static String TAG_POSITION_X = "|D:";
    private final static String TAG_POSITION_Y = "|E:";
    private final static String TAG_OFFER_COMPANY = "|F:";
    private final static String TAG_PHONE_NUMBER = "|G:";
    private final static String TAG_MEMO = "|H:";
    private final static String TAG_BUILD_COMPANY = "|I:";
    private final static String TAG_BUILD_PHONE = "|J:";

    private static NfcService mSingletonInstance;

    private String mSerialNumber;
    private String mPipeGroup;
    private String mPipeGroupName;
    private String mPipeGroupColor;
    private String mPipeType;
    private String mPipeTypeName;
    private String mSetPosition;
    private String mDistanceDirection;
    private double mDistance;
    private double mDistanceLR;
    private double mDiameter;
    private String mMaterial;
    private String mMaterialName;
    private double mPipeDepth;
    private double mPositionX;
    private double mPositionY;
    private String mOfferCompany;
    private String mCompanyPhone;
    private String mMemo;
    private String mBuildCompany;
    private String mBuildPhone;
    private String mSiteImageUrl;
    private Bitmap mSiteImage;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNfcTagFilters;
    private Activity mActivity;

    private boolean mIsReadMode = false;
    private boolean mIsWriteMode = false;
    private boolean mIsFormatted = false;

    private boolean mLoadFromMap = false;

    private boolean mTabChangedFromReadToWrite = false;

    public static NfcService getInstance() {
        if (mSingletonInstance == null) {
            synchronized (NfcService.class) {
                mSingletonInstance = new NfcService();
            }
        }
        return mSingletonInstance;
    }

    public void initializeNfcMode(Context context) {
        if (mNfcAdapter != null) {
            mNfcAdapter = null;
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            Utils.showToast(context, "이 단말은 NFC 를 지원하지 않습니다. 정보읽기를 사용할 수 없습니다.");
            return;
        }

        checkNfcEnabled(context);

        Log.d(TAG, "initializeNfcMode()");

        mActivity = (Activity)context;

        mNfcPendingIntent = PendingIntent.getActivity(mActivity, 0, new Intent(mActivity, mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter nfcDetected = new IntentFilter();
        nfcDetected.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        nfcDetected.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        nfcDetected.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);

        mNfcTagFilters = new IntentFilter[] { nfcDetected };
    }

    public void onResumeNfcMode(Context context, Intent intent) {
        if (!mIsReadMode && !mIsWriteMode) {
            return;
        }

        checkNfcEnabled(context);

        Log.d(TAG, "onResumeNfcMode()");

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch((Activity)context, mNfcPendingIntent, mNfcTagFilters, null);
        } else {
            Log.e(TAG, "onResumeNfcMode() mNfcAdapter is NULL");
        }
    }

    public void onPauseNfcMode() {
        if (!mIsReadMode && !mIsWriteMode) {
            return;
        }

        if (mNfcAdapter != null) {
            Log.d(TAG, "onPauseNfcMode()");
            mNfcAdapter.disableForegroundDispatch(mActivity);
        } else {
            Log.e(TAG, "onPauseNfcMode() mNfcAdapter is NULL");
        }
    }

//    public static boolean appendPagesFromBytes(SparseArray<byte[]> pages_output, byte[] bytes_input) {
//        int block = 0;
//        for (int i = 0; i < bytes_input.length; i += 4, block++) {
//            appendPage(pages_output, bytes_input, block);
//        }
//        return true;
//    }

//    public boolean writeNfcA(NfcA tag, byte[] bytes) {
//        for (String string : tag.getTag().getTechList()) {
//            Log.d(TAG, "TechList:" + string);
//        }
//
//        SparseArray<byte[]> pages = new SparseArray<>();
//        AmiiboHelper.appendPagesFromBytes(pages, bytes);
//
//        try {
//            int error = 0;
//
//            for (int key_index = 0; key_index < pages.size(); key_index++) {
//                boolean continue_with_except = true;
//
//                int page_index = pages.keyAt(key_index);
//                byte[] page = pages.get(page_index);
//                byte[] response = null;
//
//                byte[] write = new byte[]{
//                        Constants.COMMAND_WRITE, // COMMAND_WRITE
//                        (byte) (page_index & 0xff),
//                        page[0],
//                        page[1],
//                        page[2],
//                        page[3],
//                };
//                try {
//                    response = tag.transceive(write);
//                    Log.d("MainActivity", "write O :: " + IO.byteArrayToLoggableHexString(write) + " OK");
//                } catch (TagLostException e) {
//                    response = null;
//                    continue_with_except = false;
//                } catch (IOException e) {
//                    Log.d("MainActivity", "write O :: " + IO.byteArrayToLoggableHexString(write) + " KO");
//                    response = null;
//                }
//
//                if (response != null) {
//
//                } else if (!continue_with_except) {
//                    return false;
//                    //throw new TagLostException("having lost completely the tag");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    public boolean authenticateNTag(NfcA tag) {
        byte[] password = hexToBytes("1111");

        byte[] auth = new byte[]{
                (byte) 0x1B,
                password[0],
                password[1],
                password[2],
                password[3]
        };

        Log.d(TAG, "authenticateNTag() auth: " + auth);

        byte[] response = new byte[0];

        try {
            response = tag.transceive(auth);
            Log.d(TAG, "authenticateNTag() return true");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "authenticateNTag() return false");
        return false;
    }

    public void tryReadNfcTag(NfcA nTag216, byte[] uid) {

    }

//    public void tryWriteNfcTag(NfcA nTag216, byte[] uid) {
//        boolean read_successfully = false;
//        try {
//            nTag216.connect();
//
//            boolean authenticated = authenticateNTag(nTag216);
//
//            if (authenticated) {
//                boolean result = AmiiboIO.writeAmiibo(ntag215, getAmiibo().data.getBlob());
//
//                _scan_listener.onWriteResult(result);
//                read_successfully = result;
//            } else {
//                // 비밀번호가 일치하지 않습니다...
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            ntag215.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (!read_successfully) {
//            _please_retry.setVisibility(View.VISIBLE);
//        }
//    }


    public void onNewIntentNfcMode(Context context, Intent intent) {
        if (!mIsReadMode && !mIsWriteMode) {
            return;
        }

        if (intent.getAction() == null) {
            Log.e(TAG, "onNewIntentNfcMode() intent.getAction() is null");
            return;
        }

        String action = intent.getAction();
        Log.d(TAG, "onNewIntentNfcMode() action : " + action);
        Log.d(TAG, "onNewIntentNfcMode() readMode : " + mIsReadMode + ", writeMode :" + mIsWriteMode);

        if (mIsReadMode) {
            Log.d(TAG, "onNewIntentNfcMode() readMode");

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] id = tag.getId();
                String tagId = toHex(id);

                if (!TextUtils.isEmpty(tagId)) {
                    setSerialNumber(tagId.replace(" ", ":").toUpperCase());
                }

                dumpTagData(tag);

                Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (rawMessages != null) {
                    Log.d(TAG, "onNewIntentNfcMode() readMode, rawMessages.length :" + rawMessages.length);

                    NdefMessage[] messages = new NdefMessage[rawMessages.length];
                    for (int i = 0; i < rawMessages.length; i++) {
                        messages[i] = (NdefMessage)rawMessages[i];
                    }

                    for (int i = 0; i < messages.length; i++) {
                        NdefRecord[] nDefRecords = messages[i].getRecords();

                        for (int j = 0; j < nDefRecords.length; j++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                String nfcValue = new String(nDefRecords[j].getPayload(), StandardCharsets.UTF_8);
                                parseNfcData(nfcValue);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "onNewIntentNfcMode() readMode, rawMessages is NULL");
                }

                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_READ_NFC_PIPEINFO));
            }
        } else if (mIsWriteMode) {
            // Currently in tag WRITING mode
            Log.d(TAG, "onNewIntentNfcMode() writeMode : " + intent.getAction());

            if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

                Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                byte[] id = detectedTag.getId();
                String tagId = toHex(id);

                if (!TextUtils.isEmpty(tagId)) {
                    setSerialNumber(tagId.replace(" ", ":").toUpperCase());
                }

                //isTagLockByPassword(detectedTag);
//                isRightPassword(detectedTag, "1111");
                checkIsLockByPassword(detectedTag, "1111");
                //writeTag(context, buildNdefMessage1(), detectedTag);

                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_PIPEINFO));
            }
        }
    }

    private void parseNfcData(String data) {
        if (data == null || TextUtils.isEmpty(data)) {
            return;
        }

        Log.d(TAG, "parseNfcData() data:" + data);
        data = data.replaceAll("en", "");

        if (data.contains(TAG_PIPE_GROUP)) {
            String value = data.replace(TAG_PIPE_GROUP, "");
            setPipeGroup(value);
            return;
        }

        if (data.contains(TAG_PIPE_GROUP_NAME)) {
            String value = data.replace(TAG_PIPE_GROUP_NAME, "");
            setPipeGroupName(value);
            return;
        }

        if (data.contains(TAG_PIPE_TYPE)) {
            String value = data.replace(TAG_PIPE_TYPE, "");
            setPipeType(value);
            return;
        }

        if (data.contains(TAG_PIPE_TYPENAME)) {
            String value = data.replace(TAG_PIPE_TYPENAME, "");
            setPipeTypeName(value);
            return;
        }

        if (data.contains(TAG_SET_POSITION)) {
            String value = data.replace(TAG_SET_POSITION, "");
            setSetPosition(value);
            return;
        }

        if (data.contains(TAG_DISTANCE_DIRECTION)) {
            String value = data.replace(TAG_DISTANCE_DIRECTION, "");
            setDistanceDirection(value);
            return;
        }

        if (data.contains(TAG_DISTANCE)) {
            String value = data.replace(TAG_DISTANCE, "");

            if (TextUtils.isEmpty(value)) {
                setDistance(0.0);
            } else {
                setDistance(Double.parseDouble(value));
            }
            return;
        }
        if (data.contains(TAG_DISTANCE_LR)) {
            String value = data.replace(TAG_DISTANCE_LR, "");

            if (TextUtils.isEmpty(value)) {
                setDistanceLR(0.0);
            } else {
                setDistanceLR(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains(TAG_PIPE_DEPTH)) {
            String value = data.replace(TAG_PIPE_DEPTH, "");

            if (TextUtils.isEmpty(value)) {
                setPipeDepth(0.0);
            } else {
                setPipeDepth(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains(TAG_PIPE_DIAMETER)) {
            String value = data.replace(TAG_PIPE_DIAMETER, "");

            if (TextUtils.isEmpty(value)) {
                setDiameter(0.0);
            } else {
                setDiameter(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains(TAG_MATERIAL)) {
            String value = data.replace(TAG_MATERIAL, "");
            setMaterial(value);
            return;
        }

        if (data.contains(TAG_MATERIAL_NAME)) {
            String value = data.replace(TAG_MATERIAL_NAME, "");
            setMaterialName(value);
            return;
        }

        if (data.contains(TAG_POSITION_X)) {
            String value = data.replace(TAG_POSITION_X, "");

            if (TextUtils.isEmpty(value)) {
                setPositionX(0.0);
            } else {
                setPositionX(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains(TAG_POSITION_Y)) {
            String value = data.replace(TAG_POSITION_Y, "");

            if (TextUtils.isEmpty(value)) {
                setPositionY(0.0);
            } else {
                setPositionY(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains(TAG_OFFER_COMPANY)) {
            String value = data.replace(TAG_OFFER_COMPANY, "");
            setOfferCompany(value);
            return;
        }

        if (data.contains(TAG_PHONE_NUMBER)) {
            String value = data.replace(TAG_PHONE_NUMBER, "");
            setCompanyPhone(value);
            return;
        }

        if (data.contains(TAG_MEMO)) {
            String value = data.replace(TAG_MEMO, "");
            setMemo(value);
            return;
        }

        if (data.contains(TAG_BUILD_COMPANY)) {
            String value = data.replace(TAG_BUILD_COMPANY, "");
            setBuildCompany(value);
            return;
        }

        if (data.contains(TAG_BUILD_PHONE)) {
            String value = data.replace(TAG_BUILD_PHONE, "");
            setBuildPhone(value);
        }
    }

    private String dumpTagData(Tag tag) {
        Log.d(TAG, "dumpTagData()");

        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";
                try {
                    MifareClassic mifareTag;
                    try {
                        mifareTag = MifareClassic.get(tag);
                    } catch (Exception e) {
                        // Fix for Sony Xperia Z3/Z5 phones
                        tag = cleanupTag(tag);
                        mifareTag = MifareClassic.get(tag);
                    }
                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        Log.d(TAG, "dumpTagData() :" + sb.toString());
        return sb.toString();
    }


    private void checkNfcEnabled(Context context) {
        boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled) {
            new AlertDialog.Builder(context)
                    .setTitle("NFC is currently turned off")
                    .setMessage("Please turn on NFC in the Settings and then use the back button to return to this app.")
                    .setCancelable(false)
                    .setPositiveButton("Update Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id){
                                    ((Activity)context).startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            })
                    .create()
                    .show();
        }
    }

    NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
        Log.d(TAG, "getNdefMessagesFromIntent()");

        NdefMessage[] msgs = null;
        String action = intent.getAction();

        Log.d(TAG, "getNdefMessagesFromIntent() action : " + action);

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }

        } else {
            Log.e(TAG, "Unknown intent :" + action);
            return null;
        }
        return msgs;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private Tag cleanupTag(Tag oTag) {
        if (oTag == null)
            return null;

        String[] sTechList = oTag.getTechList();

        Parcel oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0) {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0) {
            tagService = oParcel.readStrongBinder();
        } else {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx = -1;
        int mc_idx = -1;
        short oSak = 0;
        short nSak = 0;

        for (int idx = 0; idx < sTechList.length; idx++) {
            if (sTechList[idx].equals(NfcA.class.getName())) {
                if (nfca_idx == -1) {
                    nfca_idx = idx;
                    if (oTechExtras[idx] != null && oTechExtras[idx].containsKey("sak")) {
                        oSak = oTechExtras[idx].getShort("sak");
                        nSak = oSak;
                    }
                } else {
                    if (oTechExtras[idx] != null && oTechExtras[idx].containsKey("sak")) {
                        nSak = (short) (nSak | oTechExtras[idx].getShort("sak"));
                    }
                }
            } else if (sTechList[idx].equals(MifareClassic.class.getName())) {
                mc_idx = idx;
            }
        }

        boolean modified = false;

        if (oSak != nSak) {
            oTechExtras[nfca_idx].putShort("sak", nSak);
            modified = true;
        }

        if (nfca_idx != -1 && mc_idx != -1 && oTechExtras[mc_idx] == null) {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
            modified = true;
        }

        if (!modified) {
            return oTag;
        }

        Parcel nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras, 0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if (isMock == 0) {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);

        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);

        nParcel.recycle();

        return nTag;
    }

    public void enableTagReadMode() {
        Log.d(TAG, "enableTagReadMode()");
        setReadMode(true);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(mActivity, mNfcPendingIntent, null, null);
        }
    }

    public void enableTagWriteMode() {
        Log.d(TAG, "enableTagWriteMode()");
        setWriteMode(true);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(mActivity, mNfcPendingIntent, null, null);
        }
    }

    public final static byte CMD_READ = (byte)0x30;
    public final static byte AUTH0_ADDRESS_216 = (byte)0xE3;
    public final static byte CMD_PWD_AUTH = (byte)0x1B;         // 암호 쓰기

    public byte[] nfcARead(NfcA nfcA, byte address) throws IOException {
        byte[] cmd = {CMD_READ, address};
        return nfcA.transceive(cmd);
    }

    byte[] getAuthConfig(NfcA nfcA) throws IOException {
        return nfcARead(nfcA, AUTH0_ADDRESS_216);
    }

    boolean isTagLockByPassword(Tag tag) {
        try {
            NfcA nfcA = NfcA.get(tag);
            if (nfcA == null) {
                return false;
            }

            nfcA.connect();

            byte[] bytes = getAuthConfig(nfcA);

            Log.d(TAG, "isTagLockByPassword():" + bytesToHex(bytes));

            for (int i = 0; i < bytes.length ; i++) {
                Log.d(TAG, "isTagLockByPassword() bytes[" + i + "] :" + bytes[i]);
            }

            nfcA.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    boolean isRightPassword(Tag tag, String password) {
        try {
            MifareUltralight nfcA = MifareUltralight.get(tag);
            if (nfcA == null) {
                return false;
            }

            nfcA.connect();

            byte[] bytesPassword = password.getBytes();
            Log.d(TAG, "isRightPassword() bytesPassword:" + bytesToHex(bytesPassword));

            for (int i = 0; i < bytesPassword.length ; i++) {
                Log.d(TAG, "isRightPassword() bytesPassword[" + i + "] :" + bytesPassword[i]);
            }

            byte[] cmd = {CMD_PWD_AUTH, bytesPassword[0], bytesPassword[1], bytesPassword[2], bytesPassword[3]};

            byte[] response = nfcA.transceive(cmd);

            Log.d(TAG, "isRightPassword() response:" + bytesToHex(response));

            for (int i = 0; i < response.length ; i++) {
                Log.d(TAG, "isRightPassword() response[" + i + "] :" + response[i]);
            }

            nfcA.close();

            if ((response != null) && (response.length >= 2)) {
                byte[] packResponse = Arrays.copyOf(response, 2);
                Log.d(TAG, "isRightPassword() packResponse:" + bytesToHex(response));

                for (int i = 0; i < response.length ; i++) {
                    Log.d(TAG, "isRightPassword() packResponse[" + i + "] :" + response[i]);
                }

//                if (!(pack[0] == packResponse[0] && pack[1] == packResponse[1])) {
//                    Toast.makeText(ctx, "Tag could not be authenticated:\n" + packResponse.toString() + "≠" + pack.toString(), Toast.LENGTH_LONG).show();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] keygen(byte[] uuid) {
        byte[] key = new byte[4];
        int[] uuid_to_ints = new int[uuid.length];

        for (int i = 0; i < uuid.length; i++)
            uuid_to_ints[i] = (0xFF & uuid[i]);

        if (uuid.length == 7) {
            key[0] = ((byte) (0xFF & (0xAA ^ (uuid_to_ints[1] ^ uuid_to_ints[3]))));
            key[1] = ((byte) (0xFF & (0x55 ^ (uuid_to_ints[2] ^ uuid_to_ints[4]))));
            key[2] = ((byte) (0xFF & (0xAA ^ (uuid_to_ints[3] ^ uuid_to_ints[5]))));
            key[3] = ((byte) (0xFF & (0x55 ^ (uuid_to_ints[4] ^ uuid_to_ints[6]))));
            return key;
        }

        return new byte[]{0};
    }

    public boolean checkIsLockByPassword(Tag tag, String password) {
        NfcA nfcA = NfcA.get(tag);
        if (nfcA == null) {
            return false;
        }

        byte[] bytesPassword = password.getBytes();
        Log.d(TAG, "checkIsLockByPassword() bytesPassword:" + bytesToHex(bytesPassword));

        for (int i = 0; i < bytesPassword.length ; i++) {
            Log.d(TAG, "checkIsLockByPassword() bytesPassword[" + i + "] :" + bytesPassword[i]);
        }

        try {
            nfcA.connect();

            byte[] auth = new byte[]{
                    (byte) 0x1B,
                    bytesPassword[0],
                    bytesPassword[1],
                    bytesPassword[2],
                    bytesPassword[3]
            };
            byte[] response = new byte[0];
            try {
                response = nfcA.transceive(auth);

                Log.d(TAG, "checkIsLockByPassword() response:" + bytesToHex(response));

                for (int i = 0; i < response.length ; i++) {
                    Log.d(TAG, "checkIsLockByPassword() response[" + i + "] :" + response[i]);
                }

                Log.d(TAG, "checkIsLockByPassword() return TRUE");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "checkIsLockByPassword() return FALSE");
            return false;
        } catch (IOException e) {
            Log.d(TAG, "checkIsLockByPassword() exception");
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
            } catch (IOException e) {
                Log.d(TAG, "checkIsLockByPassword() close NfcA close exception");
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean writeTag(Context context, NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Log.d(TAG, "writeTag()");

            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                try {
                    if (!ndef.isConnected()) {
                        ndef.connect();
                    } else {
                        Log.d(TAG, "ndef.isConnected() is TRUE");
                    }

                    if (!ndef.isWritable()) {
                        Utils.showToast(context, "이 TAG는 읽기전용 모드 입니다. TAG 쓰기가 불가능 합니다.");
                        return false;
                    }

                    if (ndef.getMaxSize() < size) {
                        Utils.showToast(context, "Cannot write to this tag. Message size (" + size
                                + " bytes) exceeds this tag's capacity of "
                                + ndef.getMaxSize() + " bytes.");
                        return false;
                    }

                    ndef.writeNdefMessage(message);
                    Utils.showToast(context, "TAG 쓰기에 성공하였습니다.");
                } catch (Exception e) {
                    Log.e(TAG, "writeTag() write Exception");

                    if (!mIsFormatted) {
                        formatNfcTag(message, tag);
                        mIsFormatted = true;
                        return writeTag(context, message, tag);
                    }

                    e.printStackTrace();
                    Utils.showToast(context, "TAG 쓰기에 실패하였습니다. 다시 시도하세요. 오류가 반복된다면 TAG에 문제가 있을 수 있습니다. 관리자에게 문의하시기 바랍니다.");
                    return false;
                } finally {
                    ndef.close();
                }
                return true;
            } else {
                NdefFormatable formatAble = NdefFormatable.get(tag);
                if (formatAble != null) {
                    try {
                        formatAble.connect();
                        formatAble.format(message);
                        formatAble.close();
                    } catch (IOException | FormatException ex) {
                        Log.e(TAG, "writeTag() format Exception");
                        ex.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "writeTag() formatAble is null");
                }
            }

            Utils.showToast(context, "TAG 쓰기에 실패하였습니다. 이 TAG 는 NDEF 를 지원하지 않습니다.");
            return false;

        } catch (Exception e) {
            Log.e(TAG, "writeTag() Exception");
            Utils.showToast(context, "오류가 발생하여 TAG 쓰기에 실패하였습니다. 관리자에게 문의하세요.");
            e.printStackTrace();
        }
        return false;
    }

    private void formatNfcTag(NdefMessage message, Tag tag) throws IOException {
        NdefFormatable formatAble = NdefFormatable.get(tag);
        if (formatAble != null) {
            Log.d(TAG, "formatNfcTag()");

            try {
                formatAble.connect();
                formatAble.format(message);
                formatAble.close();
            } catch (IOException | FormatException ex) {
                Log.e(TAG, "formatNfcTag() Exception");
                ex.printStackTrace();
            }
        } else {
            Log.e(TAG, "formatNfcTag() formatAble is null");
            cleanupTag(tag);
        }
    }

    private NdefRecord textToNdefRecord(String text) {
        try {
            String lang = "en";
            byte[] textBytes = text.getBytes();
            byte[] langBytes = lang.getBytes("UTF-8");
            int langLength = langBytes.length;
            int textLength = textBytes.length;
            byte[] payload = new byte[1 + langLength + textLength];

            // set status byte (see NDEF spec for actual bits)
            payload[0] = (byte) langLength;

            // copy langbytes and textbytes into payload
            System.arraycopy(langBytes, 0, payload, 1, langLength);
            System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

            NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

            return recordNFC;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NdefMessage buildNdefMessage1() {
        NdefMessage message = new NdefMessage(new NdefRecord[] {
                textToNdefRecord(TAG_PIPE_GROUP + mPipeGroup),
                textToNdefRecord(TAG_PIPE_GROUP_NAME + mPipeGroupName),
                textToNdefRecord(TAG_PIPE_TYPE + mPipeType),
                textToNdefRecord(TAG_PIPE_TYPENAME + mPipeTypeName),
                textToNdefRecord(TAG_SET_POSITION + mSetPosition),
                textToNdefRecord(TAG_DISTANCE_DIRECTION + mDistanceDirection),
                textToNdefRecord(TAG_DISTANCE + mDistance),
                textToNdefRecord(TAG_DISTANCE_LR + mDistanceLR),
                textToNdefRecord(TAG_PIPE_DEPTH + mPipeDepth),
                textToNdefRecord(TAG_PIPE_DIAMETER + mDiameter),
                textToNdefRecord(TAG_MATERIAL + mMaterial),
                textToNdefRecord(TAG_MATERIAL_NAME + mMaterialName),
                textToNdefRecord(TAG_POSITION_X + mPositionX),
                textToNdefRecord(TAG_POSITION_Y + mPositionY),
                textToNdefRecord(TAG_OFFER_COMPANY + mOfferCompany),
                textToNdefRecord(TAG_PHONE_NUMBER + mCompanyPhone),
                textToNdefRecord(TAG_MEMO + mMemo),
                textToNdefRecord(TAG_BUILD_COMPANY + mBuildCompany),
                textToNdefRecord(TAG_BUILD_PHONE + mBuildPhone)
            });

        return message;
    }

    public NdefMessage buildNdefMessage2() {
        NdefMessage message = new NdefMessage(new NdefRecord[] {

        });

        return message;
    }

    public boolean isReadMode() {
        return mIsReadMode;
    }

    public void setReadMode(boolean isReadMode) {
        this.mIsReadMode = isReadMode;

        if (isReadMode && mIsWriteMode) {
            mIsWriteMode = false;
        }
    }

    public boolean isWriteMode() {
        return mIsWriteMode;
    }

    public void setWriteMode(boolean isWriteMode) {
        this.mIsWriteMode = isWriteMode;
        this.mIsFormatted = false;


        if (isWriteMode && mIsReadMode) {
            mIsReadMode = false;
        }
    }

    public void setSerialNumber(String serialNumber) {
        this.mSerialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public String getPipeGroup() {
        return mPipeGroup;
    }

    public void setPipeGroup(String pipeGroup) {
        this.mPipeGroup = pipeGroup;
    }

    public String getPipeGroupName() {
        return mPipeGroupName;
    }

    public void setPipeGroupName(String pipeGroupName) {
        this.mPipeGroupName = pipeGroupName;
    }

    public String getPipeGroupColor() {
        return mPipeGroupColor;
    }

    public void setPipeGroupColor(String pipeGroupColor) {
        this.mPipeGroupColor = pipeGroupColor;
    }

    public String getPipeType() {
        return mPipeType;
    }

    public void setPipeType(String pipeType) {
        this.mPipeType = pipeType;
    }

    public String getPipeTypeName() {
        return mPipeTypeName;
    }

    public void setPipeTypeName(String pipeTypeName) {
        this.mPipeTypeName = pipeTypeName;
    }

    public String getSetPosition() {
        return mSetPosition;
    }

    public void setSetPosition(String setPosition) {
        this.mSetPosition = setPosition;
    }

    public String getDistanceDirection() {
        return mDistanceDirection;
    }

    public void setDistanceDirection(String distanceDirection) {
        this.mDistanceDirection = distanceDirection;
    }

    public double getDistanceLR() {
        return mDistanceLR;
    }

    public void setDistanceLR(double distanceLR) {
        this.mDistanceLR = distanceLR;
    }

    public double getDiameter() {
        return mDiameter;
    }

    public void setDiameter(double diameter) {
        this.mDiameter = diameter;
    }

    public String getMaterial() {
        return mMaterial;
    }

    public void setMaterial(String material) {
        this.mMaterial = material;
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    public void setMaterialName(String materialName) {
        this.mMaterialName = materialName;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    public double getPipeDepth() {
        return mPipeDepth;
    }

    public void setPipeDepth(double pipeDepth) {
        this.mPipeDepth = pipeDepth;
    }

    public double getPositionX() {
        return mPositionX;
    }

    public void setPositionX(double positionX) {
        this.mPositionX = positionX;
    }

    public double getPositionY() {
        return mPositionY;
    }

    public void setPositionY(double positionY) {
        this.mPositionY = positionY;
    }

    public String getOfferCompany() {
        return mOfferCompany;
    }

    public void setOfferCompany(String offerCompany) {
        this.mOfferCompany = offerCompany;
    }

    public String getCompanyPhone() {
        return mCompanyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.mCompanyPhone = companyPhone;
    }

    public String getMemo() {
        return mMemo;
    }

    public void setMemo(String memo) {
        this.mMemo = memo;
    }

    public String getBuildCompany() {
        return mBuildCompany;
    }

    public void setBuildCompany(String buildCompany) {
        this.mBuildCompany = buildCompany;
    }

    public String getBuildPhone() {
        return mBuildPhone;
    }

    public void setBuildPhone(String buildPhone) {
        this.mBuildPhone = buildPhone;
    }

    public String getSiteImageUrl() {
        return mSiteImageUrl;
    }

    public void setSiteImageUrl(String siteImageUrl) {
        this.mSiteImageUrl = siteImageUrl;
    }

    public Bitmap getSiteImage() {
        return mSiteImage;
    }

    public void setSiteImage(Bitmap siteImage) {
        this.mSiteImage = siteImage;
    }


    public boolean isTabChangedFromReadToWrite() {
        return mTabChangedFromReadToWrite;
    }

    public void setTabChangedFromReadToWrite(boolean tabChangedFromReadToWrite) {
        this.mTabChangedFromReadToWrite = tabChangedFromReadToWrite;
    }

    public byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String bytesToHex(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public boolean isLoadFromMap() {
        return mLoadFromMap;
    }

    public void setLoadFromMap(boolean loadFromMap) {
        this.mLoadFromMap = loadFromMap;
    }
}
