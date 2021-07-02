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
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;

import kr.co.ecommtech.epsi.ui.utils.Utils;

public class NfcService {
    private final static String TAG = "NfcService";

    private static NfcService mSingletonInstance;

    private String mPipeGroup;
    private String mPipeGroupName;
    private String mPipeGroupColor;
    private String mPipeType;
    private String mPipeTypeName;
    private double mDiameter;
    private String mMaterial;
    private String mMaterialName;
    private double mDistance;
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

    private PendingIntent mReadPendingIntent;
    private PendingIntent mWritePendingIntent;
    private IntentFilter[] mReadTagFilters;
    private IntentFilter[] mWriteTagFilters;

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
            Utils.showToast(context, "이 단말은 NFC 를 지원하지 않습니다. 읽기모드를 사용할 수 없습니다.");
            return;
        }

        checkNfcEnabled(context);

        Log.d(TAG, "initializeNfcMode()");

        mNfcPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter nfcDetected = new IntentFilter();
        nfcDetected.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        nfcDetected.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        nfcDetected.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);

        mNfcTagFilters = new IntentFilter[] { nfcDetected };
    }

    public void onResumeNfcMode(Context context, Intent intent) {
        checkNfcEnabled(context);

        if (intent.getAction() == null) {
            Log.d(TAG, "onResumeNfcMode() intent.getAction() is NULL");
            return;
        }

        Log.d(TAG, "onResumeNfcMode()");

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch((Activity)context, mReadPendingIntent, mNfcTagFilters, null);
        } else {
            Log.e(TAG, "onResumeNfcMode() mNfcAdapter is NULL");
        }
    }

    public void onPauseNfcMode(Context context) {
        if (mNfcAdapter != null) {
            Log.d(TAG, "onPauseNfcMode()");
            mNfcAdapter.disableForegroundDispatch((Activity)context);
        } else {
            Log.e(TAG, "onPauseNfcMode() mNfcAdapter is NULL");
        }
    }

    public void onNewIntentReadMode(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Log.e(TAG, "onNewIntentReadMode() intent.getAction() is null");
            return;
        }

        if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
            Log.d(TAG, msgs[0].getRecords()[0].getPayload().toString());

        } else if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Utils.showToast(context, "This NFC tag has no NDEF data.");
        }
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

    public void onResumeNfcWriteMode(Context context) {
        Log.d(TAG, "onResumeNfcWriteMode()");
        checkNfcEnabled(context);
    }

    public void onPauseNfcWriteMode(Context context) {
        if (mNfcAdapter != null) {
            Log.d(TAG, "onPauseNfcWriteMode()");
            mNfcAdapter.disableForegroundDispatch((Activity)context);
        } else {
            Log.e(TAG, "onPauseNfcWriteMode() mNfcAdapter is NULL");
        }
    }

    public void onNewIntentWriteMode(Context context, Intent intent) {
        Log.d(TAG, "onNewIntentWriteMode() " + intent);

        if (!mIsWriteMode) {
            Log.e(TAG, "onNewIntentWriteMode() mIsWriteMode is false, return");
            return;
        }

        // Currently in tag WRITING mode
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(context, buildNdefMessage(), detectedTag);
        }
    }

    public void enableTagWriteMode(Context context) {
        mIsWriteMode = true;
        mNfcAdapter.enableForegroundDispatch((Activity)context, mWritePendingIntent, mWriteTagFilters, null);

//        mImageViewImage.setImageDrawable(getResources().getDrawable(R.drawable.android_writing_logo));
//        mEditTextData.setEnabled(false);
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
            }

            Utils.showToast(context, "Cannot write to this tag. This tag does not support NDEF.");
            return false;

        } catch (Exception e) {
            Utils.showToast(context, "Cannot write to this tag due to an Exception.");
        }

        return false;
    }

    public NdefMessage buildNdefMessage() {
        // get the values from the form's text fields:
        String data = "epsi";//mEditTextData.getText().toString().trim();

        // create a new NDEF record and containing NDEF message using the app's custom MIME type:
        String mimeType = "application/kr.co.ecommtech.epsi.android.nfc";

        byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
        byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
        byte[] id = new byte[0];

        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, dataBytes);
        NdefMessage message = new NdefMessage(new NdefRecord[] { record });

        // return the NDEF message
        return message;
    }

    public boolean isReadMode() {
        return mIsReadMode;
    }

    public void setReadMode(boolean isReadMode) {
        this.mIsReadMode = isReadMode;
    }

    public boolean isWriteMode() {
        return mIsWriteMode;
    }

    public void setWriteMode(boolean isWriteMode) {
        this.mIsWriteMode = isWriteMode;
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
