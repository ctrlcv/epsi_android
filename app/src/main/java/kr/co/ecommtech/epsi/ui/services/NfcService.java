package kr.co.ecommtech.epsi.ui.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;

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
    private IntentFilter[] mReadTagFilters;

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

    public void initNfcReadMode(Context context) {
        if (mNfcAdapter != null) {
            mNfcAdapter = null;
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            Utils.showToast(context, "이 단말은 NFC 를 지원하지 않습니다. 읽기모드를 사용할 수 없습니다.");
            return;
        }

        mNfcPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("application/kr.co.ecommtech.epsi.android.nfc");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Could not add MIME type.", e);
        }

        mReadTagFilters = new IntentFilter[] { ndefDetected };
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
