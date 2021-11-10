package kr.co.ecommtech.epsi.ui.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.dialog.CustomDialog;
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

    public final static byte CMD_READ = (byte)0x30;
    public final static byte CMD_WRITE = (byte)0xA2;

    public final static byte CMD_PWD_AUTH = (byte)0x1B;         // 암호 쓰기
    public final static byte AUTH0_ADDRESS_216 = (byte)0xE3;
    public final static byte PROT_ADDRESS_216 = (byte)0xE4;
    public final static byte PWD_ADDRESS_216 = (byte)0xE5;
    public final static byte PACK_ADDRESS_216 = (byte)0xE6;

    public static byte[] DEFAULT_PACK = new byte[] { (byte)0xFF, (byte)0xFF };
    public final static byte[] DEFAULT_PASSWORD = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public final static byte[] PACK = new byte[] {(byte) 0x22, (byte) 0x22};
    private final static String USER_PASSWORD = "9621";

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

    private String mLockPassword;
    private String mNewPassword;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNfcTagFilters;
    private Activity mActivity;

    private boolean mIsReadMode = false;
    private boolean mIsWriteMode = false;
    private boolean mIsFormatted = false;
    private boolean mLoadFromMap = false;
    private boolean mReLoadMarker = false;

    private boolean mTabChangedFromReadToWrite = false;
    private boolean mTabChangedFromWriteToRead = false;
    private boolean mIsDisableCancel = false;

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
            Log.d(TAG, "onPauseNfcMode() - disableForegroundDispatch()");
            mNfcAdapter.disableForegroundDispatch(mActivity);
        } else {
            Log.e(TAG, "onPauseNfcMode() mNfcAdapter is NULL");
        }
    }

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

        if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) &&
            !NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) &&
            !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            return;
        }

        Log.d(TAG, "onNewIntentNfcMode() readMode : " + mIsReadMode + ", writeMode :" + mIsWriteMode);

        mIsDisableCancel = true;

        if (mIsReadMode) {
            Log.d(TAG, "onNewIntentNfcMode() readMode");

            initTagInfo();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = tag.getId();
            String tagId = toReversedHex(id);

            if (!TextUtils.isEmpty(tagId)) {
                setSerialNumber(tagId.replace(" ", ":").toUpperCase());
            }

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
            mIsDisableCancel = false;
            return;
        }

        if (mIsWriteMode) {
            Log.d(TAG, "onNewIntentNfcMode() writeMode");
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            byte[] id = detectedTag.getId();
            String tagId = toReversedHex(id);

            if (!TextUtils.isEmpty(tagId)) {
                setSerialNumber(tagId.replace(" ", ":").toUpperCase());
            }

            boolean isTagLock = isTagLockByPassword(detectedTag);
            Log.d(TAG, "onNewIntentNfcMode() isTagLock:" + isTagLock);

            if (isTagLock) {
                if (!removeTagPassword(detectedTag, USER_PASSWORD)) {
                    Log.e(TAG, "onNewIntentNfcMode() removeTagPassword() return false");

//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    if (!removeTagBytePassword(detectedTag, DEFAULT_PASSWORD)) {
                        Log.e(TAG, "onNewIntentNfcMode() removeTagBytePassword() return false");
                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DEL_PW_FAIL));
                        mIsDisableCancel = false;
                        return;
                    }
                }

                Log.d(TAG, "onNewIntentNfcMode() removeTagPassword() return true");
            }

            if (!writeTag(context, buildNdefMessage(), detectedTag)) {
                Log.e(TAG, "onNewIntentNfcMode() writeTag() return false");
                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_FAIL));
                mIsDisableCancel = false;
                return;
            }

            if (!setTagPassword(detectedTag, USER_PASSWORD)) {
                Log.e(TAG, "onNewIntentNfcMode() setTagPassword() return false");
                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_SET_PW_FAIL));
                mIsDisableCancel = false;
                return;
            }

            EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DONE));

//            if (false) {
//                if (!TextUtils.isEmpty(mLockPassword) && TextUtils.isEmpty(mNewPassword)) {
//                    Log.d(TAG, "onNewIntentNfcMode() lockPassword:" + mLockPassword + ", NewPassword is Empty");
//
//                    boolean isTagLock = isTagLockByPassword(detectedTag);
//                    Log.d(TAG, "onNewIntentNfcMode() isTagLock:" + isTagLock);
//
//                    if (!isTagLock) {
//                        if (!writeTag(context, buildNdefMessage(), detectedTag)) {
//                            EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_FAIL));
//                            return;
//                        }
//
//                        if (!setTagPassword(detectedTag, getLockPassword())) {
//                            EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_SET_PW_FAIL));
//                            return;
//                        }
//                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DONE));
//                    } else {
//                        if (!removeTagPassword(detectedTag, getLockPassword())) {
//                            EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DEL_PW_FAIL));
//                            return;
//                        }
//                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DEL_PW_OK));
//                    }
//                } else if (!TextUtils.isEmpty(mLockPassword) && !TextUtils.isEmpty(mNewPassword)) {
//                    Log.d(TAG, "onNewIntentNfcMode() lockPassword:" + mLockPassword + ", NewPassword:" + mNewPassword);
//
//                    if (!writeTag(context, buildNdefMessage(), detectedTag)) {
//                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_FAIL));
//                        return;
//                    }
//
//                    if (!setTagPassword(detectedTag, getNewPassword())) {
//                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_SET_PW_FAIL));
//                        return;
//                    }
//                    EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_DONE));
//                } else {
//                    Log.d(TAG, "onNewIntentNfcMode() lockPassword: '" + mLockPassword + "', NewPassword: '" + mNewPassword + "'");
//                }
//            }
        }

        mIsDisableCancel = false;
    }

    private void parseNfcData(String data) {
        if (data == null || TextUtils.isEmpty(data)) {
            return;
        }

        Log.d(TAG, "parseNfcData() data:" + data);
        data = data.replaceAll("en", "");

        if (data.contains(TAG_PIPE_GROUP)) {
            String value = data.replace(TAG_PIPE_GROUP, "");
            setPipeGroup(value.trim());
            return;
        }

        if (data.contains(TAG_PIPE_GROUP_NAME)) {
            String value = data.replace(TAG_PIPE_GROUP_NAME, "");
            setPipeGroupName(value.trim());
            return;
        }

        if (data.contains(TAG_PIPE_TYPE)) {
            String value = data.replace(TAG_PIPE_TYPE, "");
            setPipeType(value.trim());
            return;
        }

        if (data.contains(TAG_PIPE_TYPENAME)) {
            String value = data.replace(TAG_PIPE_TYPENAME, "");
            setPipeTypeName(value.trim());
            return;
        }

        if (data.contains(TAG_SET_POSITION)) {
            String value = data.replace(TAG_SET_POSITION, "");
            setSetPosition(value.trim());
            return;
        }

        if (data.contains(TAG_DISTANCE_DIRECTION)) {
            String value = data.replace(TAG_DISTANCE_DIRECTION, "");
            setDistanceDirection(value.trim());
            return;
        }

        if (data.contains(TAG_DISTANCE)) {
            String value = data.replace(TAG_DISTANCE, "");

            try {
                if ("".equals(value) || TextUtils.isEmpty(value)) {
                    setDistance(0.0);
                } else {
                    setDistance(Double.parseDouble(value));
                }
            } catch (NumberFormatException e) {
                setDistance(0.0);
            }
            return;
        }
        if (data.contains(TAG_DISTANCE_LR)) {
            String value = data.replace(TAG_DISTANCE_LR, "");

            try {
                if ("".equals(value) || TextUtils.isEmpty(value)) {
                    setDistanceLR(0.0);
                } else {
                    setDistanceLR(Double.parseDouble(value));
                }
            } catch (NumberFormatException e) {
                setDistanceLR(0.0);
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
            setMaterial(value.trim());
            return;
        }

        if (data.contains(TAG_MATERIAL_NAME)) {
            String value = data.replace(TAG_MATERIAL_NAME, "");
            setMaterialName(value.trim());
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
            setOfferCompany(value.trim());
            return;
        }

        if (data.contains(TAG_PHONE_NUMBER)) {
            String value = data.replace(TAG_PHONE_NUMBER, "");
            setCompanyPhone(value.trim());
            return;
        }

        if (data.contains(TAG_MEMO)) {
            String value = data.replace(TAG_MEMO, "");
            setMemo(value.trim());
            return;
        }

        if (data.contains(TAG_BUILD_COMPANY)) {
            String value = data.replace(TAG_BUILD_COMPANY, "");
            setBuildCompany(value.trim());
            return;
        }

        if (data.contains(TAG_BUILD_PHONE)) {
            String value = data.replace(TAG_BUILD_PHONE, "");
            setBuildPhone(value.trim());
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
            new CustomDialog(context, new CustomDialog.CustomDialogListener() {
                @Override
                public void onCreate(Dialog dialog) {
                    dialog.setContentView(R.layout.dialog_setting);

                    TextView titleTv = dialog.findViewById(R.id.tv_dialog_setting_title);
                    titleTv.setText("NFC 설정");

                    TextView contentTitleTv = dialog.findViewById(R.id.tv_content_title);
                    contentTitleTv.setText("NFC 설정안내");

                    TextView contentBodyTv = dialog.findViewById(R.id.tv_content_body);
                    contentBodyTv.setText("NFC 기능을 사용하기 위해 NFC 를 ON 해 주시기 바랍니다. NFC OFF일 경우 서비스 일부가 제한될 수 있습니다.");

                    TextView cancelBtn = dialog.findViewById(R.id.btn_cancel);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    TextView okBtn = dialog.findViewById(R.id.btn_ok);
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            ((Activity)context).startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        }
                    });
                }
            }).show();
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

    public void enableTagReadMode(Context context) {
        Log.d(TAG, "enableTagReadMode()");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            return;
        }

        boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled) {
            Utils.showToast(context, "NFC 설정을 확인하세요.");
            return;
        }

        setReadMode(true);
        mNfcAdapter.enableForegroundDispatch(mActivity, mNfcPendingIntent, null, null);
    }

    public void enableTagWriteMode(Context context) {
        Log.d(TAG, "enableTagWriteMode()");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            return;
        }

        boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled) {
            Utils.showToast(context, "NFC 설정을 확인하세요.");
            return;
        }

        setWriteMode(true);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(mActivity, mNfcPendingIntent, null, null);
        }
    }

    boolean isTagLockByPassword(Tag tag) {
        NfcA nfcA = NfcA.get(tag);
        if (nfcA == null) {
            return false;
        }

        try {
            nfcA.connect();

            byte[] cmd = {CMD_READ, AUTH0_ADDRESS_216};
            byte[] response = nfcA.transceive(cmd);

            Log.d(TAG, "isTagLockByPassword():" + bytesToHex(response));

            if (response[3] == 0x00) {
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG,  "isTagLockByPassword() Exception");
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
            } catch (IOException e) {
                Log.e(TAG,  "isTagLockByPassword() close Exception");
            }
        }
        return false;
    }

    boolean removeTagPassword(Tag tag, String password) {
        NfcA nfcA = NfcA.get(tag);

        if (nfcA == null) {
            return false;
        }

        byte[] pwd = new byte[]{ password.getBytes()[0],
                                 password.getBytes()[1],
                                 password.getBytes()[2],
                                 password.getBytes()[3]};

        try {
            nfcA.connect();

            byte[] pwd_auth_result = nfcA.transceive((new byte[] {
                    CMD_PWD_AUTH, // PWD_AUTH
                    pwd[0], pwd[1], pwd[2], pwd[3]}));

            Log.d(TAG, "removeTagPassword() pwd_auth_result:" + bytesToHex(pwd_auth_result));

            byte[] set_pwd_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PWD_ADDRESS_216,
                    DEFAULT_PASSWORD[0], DEFAULT_PASSWORD[1], DEFAULT_PASSWORD[2], DEFAULT_PASSWORD[3]}));

            Log.d(TAG, "removeTagPassword() set_pwd_result:" + bytesToHex(set_pwd_result));

            byte[] set_pack_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PACK_ADDRESS_216,
                    DEFAULT_PACK[0], DEFAULT_PACK[1], 0, 0}));

            Log.d(TAG, "removeTagPassword() set_pack_result:" + bytesToHex(set_pack_result));

            byte[] response = nfcA.transceive(new byte[] {
                    CMD_READ, // READ
                    PROT_ADDRESS_216  // page address
            });

            Log.d(TAG, "removeTagPassword() response:" + bytesToHex(response));

            byte[] response_pageprot = nfcA.transceive(new byte[] {
                    CMD_READ, // READ
                    AUTH0_ADDRESS_216     // page address
            });

            Log.d(TAG, "removeTagPassword() response_pageprot:" + bytesToHex(response_pageprot));

            if ((response_pageprot != null) && (response_pageprot.length >= 16)) {  // read always returns 4 pages
                response_pageprot = nfcA.transceive(new byte[] {
                        CMD_WRITE, // WRITE
                        AUTH0_ADDRESS_216 ,   // page address
                        response_pageprot[0], // keep old value for byte 0
                        response_pageprot[1], // keep old value for byte 1
                        response_pageprot[2], // keep old value for byte 2
                        (byte) (0x0ff)
                });
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG,  "removeTagPassword() Exception");
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
                Log.d(TAG,  "removeTagPassword() call close())");
            } catch (IOException e) {
                Log.e(TAG,  "removeTagPassword() close Exception");
            }
        }

        return false;
    }

    boolean removeTagBytePassword(Tag tag, byte[] pwd) {
        NfcA nfcA = NfcA.get(tag);

        if (nfcA == null) {
            return false;
        }

        try {
            nfcA.connect();

            byte[] pwd_auth_result = nfcA.transceive((new byte[] {
                    CMD_PWD_AUTH, // PWD_AUTH
                    pwd[0], pwd[1], pwd[2], pwd[3]}));

            Log.d(TAG, "removeTagBytePassword() pwd_auth_result:" + bytesToHex(pwd_auth_result));

            byte[] set_pwd_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PWD_ADDRESS_216,
                    DEFAULT_PASSWORD[0], DEFAULT_PASSWORD[1], DEFAULT_PASSWORD[2], DEFAULT_PASSWORD[3]}));

            Log.d(TAG, "removeTagBytePassword() set_pwd_result:" + bytesToHex(set_pwd_result));

            byte[] set_pack_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PACK_ADDRESS_216,
                    DEFAULT_PACK[0], DEFAULT_PACK[1], 0, 0}));

            Log.d(TAG, "removeTagBytePassword() set_pack_result:" + bytesToHex(set_pack_result));

            byte[] response = nfcA.transceive(new byte[] {
                    CMD_READ, // READ
                    PROT_ADDRESS_216  // page address
            });

            Log.d(TAG, "removeTagBytePassword() response:" + bytesToHex(response));

            byte[] response_pageprot = nfcA.transceive(new byte[] {
                    CMD_READ, // READ
                    AUTH0_ADDRESS_216     // page address
            });

            Log.d(TAG, "removeTagBytePassword() response_pageprot:" + bytesToHex(response_pageprot));

            if ((response_pageprot != null) && (response_pageprot.length >= 16)) {  // read always returns 4 pages
                response_pageprot = nfcA.transceive(new byte[] {
                        CMD_WRITE, // WRITE
                        AUTH0_ADDRESS_216 ,   // page address
                        response_pageprot[0], // keep old value for byte 0
                        response_pageprot[1], // keep old value for byte 1
                        response_pageprot[2], // keep old value for byte 2
                        (byte) (0x0ff)
                });
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG,  "removeTagBytePassword() Exception");
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
            } catch (IOException e) {
                Log.e(TAG,  "removeTagBytePassword() close Exception");
            }
        }

        return false;
    }

    boolean setTagPassword(Tag tag, String password) {
        NfcA nfcA = NfcA.get(tag);

        if (nfcA == null) {
            return false;
        }

        try {
            nfcA.connect();

            byte[] pwd = new byte[]{
                    password.getBytes()[0],
                    password.getBytes()[1],
                    password.getBytes()[2],
                    password.getBytes()[3]};

            byte[] set_pwd_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PWD_ADDRESS_216,
                    pwd[0], pwd[1], pwd[2], pwd[3]}));
                    //DEFAULT_PASSWORD[0], DEFAULT_PASSWORD[1], DEFAULT_PASSWORD[2], DEFAULT_PASSWORD[3]}));

            Log.d(TAG, "setTagPassword() set_pwd_result:" + bytesToHex(set_pwd_result));

            byte[] set_pack_result = nfcA.transceive((new byte[] {
                    CMD_WRITE,
                    PACK_ADDRESS_216,
                    PACK[0], PACK[1], 0, 0}));

            Log.d(TAG, "setTagPassword() set_pack_result:" + bytesToHex(set_pack_result));

            byte[] response_pageprot = nfcA.transceive(new byte[] {
                    CMD_READ, // READ
                    AUTH0_ADDRESS_216     // page address
            });

            Log.d(TAG, "setTagPassword() response_pageprot:" + bytesToHex(response_pageprot));

            if ((response_pageprot != null) && (response_pageprot.length >= 16)) {  // read always returns 4 pages
                boolean prot1 = false;  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                int auth0 = 0; // first page to be protected, set to a value between 0 and 37 for NTAG212
                response_pageprot = nfcA.transceive(new byte[] {
                        CMD_WRITE, // WRITE
                        AUTH0_ADDRESS_216 ,   // page address
                        response_pageprot[0], // keep old value for byte 0
                        response_pageprot[1], // keep old value for byte 1
                        response_pageprot[2], // keep old value for byte 2
                        (byte) (auth0 & 0x0ff)
                });
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG,  "setTagPassword() Exception");
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
            } catch (IOException e) {
                Log.e(TAG,  "setTagPassword() close Exception");
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
                    return true;
//                    Utils.showToast(context, "TAG 쓰기에 성공하였습니다.");
                } catch (Exception e) {
                    Log.e(TAG, "writeTag() write Exception");

                    if (!mIsFormatted) {
                        formatNfcTag(message, tag);
                        mIsFormatted = true;
                        ndef.close();

                        return writeTag(context, message, tag);
                    }

                    e.printStackTrace();
                    Utils.showToast(context, "TAG 쓰기에 실패하였습니다. 다시 시도하세요. 오류가 반복된다면 TAG에 문제가 있을 수 있습니다. 관리자에게 문의하시기 바랍니다.");
                    return false;
                } finally {
                    ndef.close();
                }
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

    public NdefMessage buildNdefMessage() {
        NdefMessage message = new NdefMessage(new NdefRecord[] {
                textToNdefRecord(TAG_PIPE_GROUP + mPipeGroup),
                textToNdefRecord(TAG_PIPE_GROUP_NAME + mPipeGroupName),
                textToNdefRecord(TAG_PIPE_TYPE + mPipeType),
                textToNdefRecord(TAG_PIPE_TYPENAME + mPipeTypeName),
                textToNdefRecord(TAG_SET_POSITION + mSetPosition),
                textToNdefRecord(TAG_DISTANCE_DIRECTION + mDistanceDirection),
                textToNdefRecord(TAG_DISTANCE + ((mDistance != 0.0) ? mDistance : "")),
                textToNdefRecord(TAG_DISTANCE_LR + ((mDistanceLR != 0.0) ? mDistanceLR : "")),
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

    public boolean isTabChangedFromWriteToRead() {
        return mTabChangedFromWriteToRead;
    }

    public void setTabChangedFromWriteToRead(boolean tabChangedFromWriteToRead) {
        this.mTabChangedFromWriteToRead = tabChangedFromWriteToRead;
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

    public boolean isReLoadMarker() {
        return mReLoadMarker;
    }

    public void setReLoadMarker(boolean reLoadMarker) {
        this.mReLoadMarker = reLoadMarker;
    }

    public String getLockPassword() {
        return mLockPassword;
    }

    public void setLockPassword(String lockPassword) {
        this.mLockPassword = lockPassword;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        this.mNewPassword = newPassword;
    }

    public NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }

    public boolean isDisableCancel() {
        return mIsDisableCancel;
    }

    public void initTagInfo() {
        setSerialNumber("");
        setPipeGroup("");
        setPipeGroupName("");
        setPipeGroupColor("");
        setPipeType("");
        setPipeTypeName("");
        setSetPosition("");
        setDistanceDirection("");
        setDistance(0.0);
        setDistanceLR(0.0);
        setDiameter(0.0);
        setMaterial("");
        setMaterialName("");
        setPipeDepth(0.0);
        setPositionX(0.0);
        setPositionY(0.0);
        setOfferCompany("");
        setCompanyPhone("");
        setMemo("");
        setBuildCompany("");
        setBuildPhone("");
        setSiteImageUrl("");
        setSiteImage(null);
        setLockPassword("");
        setNewPassword("");
    }


}
