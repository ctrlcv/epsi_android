package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.nfc.NdefMessageParser;
import kr.co.ecommtech.epsi.ui.nfc.ParsedRecord;
import kr.co.ecommtech.epsi.ui.nfc.TextRecord;
import kr.co.ecommtech.epsi.ui.nfc.UriRecord;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.utils.Utils;

public class NfcReadFragment extends Fragment {
    private static String TAG = "NfcReadFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_serialnumber)
    EditText mSerialNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_pipe_group)
    TextView mPipeGroup;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_pipe_type)
    TextView mPipeType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_set_position)
    TextView mSetPosition;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance_direction)
    TextView mDistanceDirection;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance)
    TextView mPipeDistance;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance_lr)
    TextView mPipeDistanceLR;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_depth)
    TextView mDepth;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_diameter)
    TextView mPipeDiameter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_material)
    TextView mMaterial;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_position_x)
    TextView mPositionX;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_position_y)
    TextView mPositionY;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_agency)
    EditText mAgency;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_phone)
    EditText mPhoneNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_memo)
    EditText mMemo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_maker)
    EditText mMaker;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_maker_phone)
    EditText mMakerPhone;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.site_image)
    ImageView mSiteImage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_image)
    LinearLayout mNoImage;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_nfcread, container, false);
        ButterKnife.bind(this, rootView);

        mSiteImage.setVisibility(View.GONE);
        mNoImage.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.read_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_btn:
                NfcService.getInstance().enableTagReadMode();
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcReadDialog(true);
                }
                break;

            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage e) {
        Log.d(TAG, "onMessageEvent()" + e);

        switch (e.what) {
            case EL_EVENT_READ_NFC_PIPEINFO:
                loadPipeInfo();
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcReadDialog(false);
                }
                String fileName = String.valueOf(NfcService.getInstance().getPositionX()) + "-" + String.valueOf(NfcService.getInstance().getPositionY()) + ".JPG";
                sendImageRequest(fileName);
                break;

            default:
                break;
        }
    }

    private void loadPipeInfo() {
        if (!TextUtils.isEmpty(NfcService.getInstance().getSerialNumber())) {
            mSerialNumber.setText(NfcService.getInstance().getSerialNumber());
        } else {
            mSerialNumber.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getPipeGroupName())) {
            mPipeGroup.setText(NfcService.getInstance().getPipeGroupName());
        } else {
            mPipeGroup.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getPipeTypeName())) {
            mPipeType.setText(NfcService.getInstance().getPipeTypeName());
        } else {
            mPipeType.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getSetPosition())) {
            mSetPosition.setText(NfcService.getInstance().getSetPosition());
        } else {
            mSetPosition.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getDistanceDirection())) {
            mDistanceDirection.setText(NfcService.getInstance().getDistanceDirection());
        } else {
            mDistanceDirection.setText("");
        }

        if (NfcService.getInstance().getDistance() != 0.0) {
            mPipeDistance.setText(String.valueOf(NfcService.getInstance().getDistance()));
        } else {
            mPipeDistance.setText("");
        }

        if (NfcService.getInstance().getDistanceLR() != 0.0) {
            mPipeDistanceLR.setText(String.valueOf(NfcService.getInstance().getDistanceLR()));
        } else {
            mPipeDistanceLR.setText("");
        }

        if (NfcService.getInstance().getPipeDepth() != 0.0) {
            mDepth.setText(String.valueOf(NfcService.getInstance().getPipeDepth()));
        } else {
            mDepth.setText("");
        }

        if (NfcService.getInstance().getDiameter() != 0.0) {
            mPipeDiameter.setText(String.valueOf(NfcService.getInstance().getDiameter()));
        } else {
            mPipeDiameter.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getMaterialName())) {
            mMaterial.setText(NfcService.getInstance().getMaterialName());
        } else {
            mMaterial.setText("");
        }

        if (NfcService.getInstance().getPositionX() != 0.0) {
            mPositionX.setText(String.valueOf(NfcService.getInstance().getPositionX()));
        } else {
            mPositionX.setText("");
        }

        if (NfcService.getInstance().getPositionY() != 0.0) {
            mPositionY.setText(String.valueOf(NfcService.getInstance().getPositionY()));
        } else {
            mPositionY.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getOfferCompany())) {
            mAgency.setText(NfcService.getInstance().getOfferCompany());
        } else {
            mAgency.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getCompanyPhone())) {
            mPhoneNumber.setText(NfcService.getInstance().getCompanyPhone());
        } else {
            mPhoneNumber.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getMemo())) {
            mMemo.setText(NfcService.getInstance().getMemo());
        } else {
            mMemo.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getBuildCompany())) {
            mMaker.setText(NfcService.getInstance().getBuildCompany());
        } else {
            mMaker.setText("");
        }

        if (!TextUtils.isEmpty(NfcService.getInstance().getBuildPhone())) {
            mMakerPhone.setText(NfcService.getInstance().getBuildPhone());
        } else {
            mMakerPhone.setText("");
        }
    }

    public void sendImageRequest(String fileName) {
        String url = "http://139.150.83.28/siteImages/" + fileName;

        LoadImageByUrlTask task = new LoadImageByUrlTask(url);
        task.execute();
    }

    private class LoadImageByUrlTask extends AsyncTask<Void, Void, Bitmap> {
        private String urlStr;

        public LoadImageByUrlTask(String urlStr) {
            this.urlStr = urlStr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp = null;
            try {
                URL url = new URL(urlStr);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                mSiteImage.setVisibility(View.GONE);
                mNoImage.setVisibility(View.VISIBLE);
            } else {
                mSiteImage.setImageBitmap(result);
                NfcService.getInstance().setSiteImage(result);

                mSiteImage.setVisibility(View.VISIBLE);
                mNoImage.setVisibility(View.GONE);
            }
        }
    }
}
