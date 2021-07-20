package kr.co.ecommtech.epsi.ui.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import kr.co.ecommtech.epsi.ui.utils.Utils;

public class NfcService {
    private final static String TAG = "NfcService";

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

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNfcTagFilters;
    private Activity mActivity;

    private boolean mIsReadMode = false;
    private boolean mIsWriteMode = false;

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
                writeTag(context, buildNdefMessage(), detectedTag);

                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_WRITE_NFC_PIPEINFO));
            }
        }
    }

    private void parseNfcData(String data) {
        if (data == null || TextUtils.isEmpty(data)) {
            return;
        }

        Log.d(TAG, "parseNfcData() data:" + data);

        if (data.contains("PipeGroup:")) {
            String value = data.replace("PipeGroup:", "");
            setPipeGroup(value);
            return;
        }

        if (data.contains("PipeGroupName:")) {
            String value = data.replace("PipeGroupName:", "");
            setPipeGroupName(value);
            return;
        }

        if (data.contains("PipeType:")) {
            String value = data.replace("PipeType:", "");
            setPipeType(value);
            return;
        }

        if (data.contains("PipeTypeName:")) {
            String value = data.replace("PipeTypeName:", "");
            setPipeTypeName(value);
            return;
        }

        if (data.contains("Distance:")) {
            String value = data.replace("Distance:", "");

            if (TextUtils.isEmpty(value)) {
                setDistance(0.0);
            } else {
                setDistance(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains("PipeDepth:")) {
            String value = data.replace("PipeDepth:", "");

            if (TextUtils.isEmpty(value)) {
                setPipeDepth(0.0);
            } else {
                setPipeDepth(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains("PipeDiameter:")) {
            String value = data.replace("PipeDiameter:", "");

            if (TextUtils.isEmpty(value)) {
                setDiameter(0.0);
            } else {
                setDiameter(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains("Material:")) {
            String value = data.replace("Material:", "");
            setMaterial(value);
            return;
        }

        if (data.contains("MaterialName:")) {
            String value = data.replace("MaterialName:", "");
            setMaterialName(value);
            return;
        }

        if (data.contains("PositionX:")) {
            String value = data.replace("PositionX:", "");

            if (TextUtils.isEmpty(value)) {
                setPositionX(0.0);
            } else {
                setPositionX(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains("PositionY:")) {
            String value = data.replace("PositionY:", "");

            if (TextUtils.isEmpty(value)) {
                setPositionY(0.0);
            } else {
                setPositionY(Double.parseDouble(value));
            }
            return;
        }

        if (data.contains("OfferCompany:")) {
            String value = data.replace("OfferCompany:", "");
            setOfferCompany(value);
            return;
        }

        if (data.contains("PhoneNumber:")) {
            String value = data.replace("PhoneNumber:", "");
            setCompanyPhone(value);
            return;
        }

        if (data.contains("Memo:")) {
            String value = data.replace("Memo:", "");
            setMemo(value);
            return;
        }

        if (data.contains("BuildCompany:")) {
            String value = data.replace("BuildCompany:", "");
            setBuildCompany(value);
            return;
        }

        if (data.contains("BuildPhone:")) {
            String value = data.replace("BuildPhone:", "");
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

    public boolean writeTag(Context context, NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Utils.showToast(context, "Cannot write to this tag. This tag is read-only.");
                    return false;
                }

                if (ndef.getMaxSize() < size) {
                    Utils.showToast(context, "Cannot write to this tag. Message size (" + size
                            + " bytes) exceeds this tag's capacity of "
                            + ndef.getMaxSize() + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                Utils.showToast(context, "A pre-formatted tag was successfully updated.");
                return true;
            } else {
                NdefFormatable formatAble = NdefFormatable.get(tag);
                if (formatAble != null) {
                    try {
                        formatAble.connect();
                        formatAble.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            Utils.showToast(context, "Cannot write to this tag. This tag does not support NDEF.");
            return false;

        } catch (Exception e) {
            Utils.showToast(context, "Cannot write to this tag due to an Exception.");
            e.printStackTrace();
        }

        return false;
    }

    private NdefRecord textToNdefRecord(String text) {
        if (text == null || TextUtils.isEmpty(text)) {
            Log.e(TAG, "getTextAsNdef() text is NULL, return");
            return null;
        }

        byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(Charset.forName("UTF-8")),
                new byte[] {},
                textBytes);
    }

    public NdefMessage buildNdefMessage() {
        NdefMessage message = new NdefMessage(new NdefRecord[] {
                textToNdefRecord("PipeGroup:" + mPipeGroup),
                textToNdefRecord("PipeGroupName:" + mPipeGroupName),
                textToNdefRecord("PipeType:" + mPipeType),
                textToNdefRecord("PipeTypeName:" + mPipeTypeName),
                textToNdefRecord("SetPosition:" + mSetPosition),
                textToNdefRecord("DistanceDirection:" + mDistanceDirection),
                textToNdefRecord("Distance:" + mDistance),
                textToNdefRecord("DistanceLR:" + mDistanceLR),
                textToNdefRecord("PipeDepth:" + mPipeDepth),
                textToNdefRecord("PipeDiameter:" + mDiameter),
                textToNdefRecord("Material:" + mMaterial),
                textToNdefRecord("MaterialName:" + mMaterialName),
                textToNdefRecord("PositionX:" + mPositionX),
                textToNdefRecord("PositionY:" + mPositionY),
                textToNdefRecord("OfferCompany:" + mOfferCompany),
                textToNdefRecord("PhoneNumber:" + mCompanyPhone),
                textToNdefRecord("Memo:" + mMemo),
                textToNdefRecord("BuildCompany:" + mBuildCompany),
                textToNdefRecord("BuildPhone:" + mBuildPhone)
                });

        // return the NDEF message
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
}
