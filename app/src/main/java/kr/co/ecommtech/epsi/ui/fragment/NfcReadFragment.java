package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;
import kr.co.ecommtech.epsi.ui.activity.ImageViewActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.data.Pipe;
import kr.co.ecommtech.epsi.ui.data.PipeList;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.LoginManager;
import kr.co.ecommtech.epsi.ui.services.NetworkStatus;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import kr.co.ecommtech.epsi.ui.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.server_label)
    TextView mServerLabel;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_label)
    TextView mNfcLabel;

    private ArrayList<Pipe> mPipeList = null;
    protected QueryService mQueryService;

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

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);

        if (NfcService.getInstance().isLoadFromMap()) {
            NfcService.getInstance().setLoadFromMap(false);
            loadPipeInfo();

            if (NfcService.getInstance().getPositionX() != 0.0 || NfcService.getInstance().getPositionY() != 0.0) {
                String fileName = String.valueOf(NfcService.getInstance().getPositionX()) + "-" + String.valueOf(NfcService.getInstance().getPositionY()) + ".JPG";
                sendImageRequest(fileName);
            }
        }

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

        if (NfcService.getInstance().isTabChangedFromWriteToRead()) {
            if (!TextUtils.isEmpty(NfcService.getInstance().getPipeTypeName()) &&
                !TextUtils.isEmpty(NfcService.getInstance().getSetPosition())) {
                loadPipeInfo();
            }
        }

        if (LoginManager.getInstance().isPreferServerData(getActivity())) {
            mServerLabel.setVisibility(View.VISIBLE);
            mNfcLabel.setVisibility(View.GONE);
        } else {
            mServerLabel.setVisibility(View.GONE);
            mNfcLabel.setVisibility(View.VISIBLE);
        }

        NfcService.getInstance().setTabChangedFromWriteToRead(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.read_btn, R.id.site_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_btn:
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
                if (nfcAdapter == null) {
                    Utils.showToast(getActivity(), "NFC 읽기를 사용할 수 없습니다.");
                    return;
                } else if (!nfcAdapter.isEnabled()) {
                    Utils.showToast(getActivity(), "NFC 설정을 확인하세요.");
                    return;
                }

                NfcService.getInstance().enableTagReadMode(getActivity());
                if (getActivity() != null) {
                    ((DefaultMainActivity)getActivity()).setVisibleNfcReadDialog(true);
                }
                break;

            case R.id.site_image:
                if (NfcService.getInstance().getSiteImage() != null) {
                    Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
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
                NfcService.getInstance().setReadMode(false);
                NfcService.getInstance().onPauseNfcMode();

                if (LoginManager.getInstance().isPreferServerData(getActivity())) {
                    double lat = NfcService.getInstance().getPositionX();
                    double lon = NfcService.getInstance().getPositionY();
                    reqGetPipeLists(lat, lon);
                } else {
                    loadPipeInfo();

                    if (getActivity() != null) {
                        ((DefaultMainActivity)getActivity()).setVisibleNfcReadDialog(false);
                    }

                    if (NfcService.getInstance().getPositionX() != 0.0 || NfcService.getInstance().getPositionY() != 0.0) {
                        String fileName = String.valueOf(NfcService.getInstance().getPositionX()) + "-" + String.valueOf(NfcService.getInstance().getPositionY()) + ".JPG";
                        sendImageRequest(fileName);
                    }

                    Utils.showToast(getActivity(), "Tag 읽기를 성공하였습니다.");
                }
                break;

            case EL_EVENT_REFRESH:
                if (LoginManager.getInstance().isPreferServerData(getActivity())) {
                    mServerLabel.setVisibility(View.VISIBLE);
                    mNfcLabel.setVisibility(View.GONE);
                } else {
                    mServerLabel.setVisibility(View.GONE);
                    mNfcLabel.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
    }

    private void reqGetPipeLists(double lat, double lon) {
        Log.d(TAG, "reqGetPipeLists() lat:" + lat + ", lon:" + lon);

        HashMap<String, Double> map = new HashMap<>();
        map.put("lat", lat);
        map.put("lon", lon);

        Call<PipeList> call = mQueryService.getPipeOne(map);

        call.enqueue(new Callback<PipeList>() {
            @Override
            public void onResponse(Call<PipeList> call, Response<PipeList> response) {
                if(response.isSuccessful()){
                    PipeList mList = response.body();

                    getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            mPipeList = (ArrayList<Pipe>)mList.pipeList;
                            Log.d(TAG, "reqGetPipeLists() mPipeList: " + mPipeList.size());

                            if (mPipeList.size() >= 1) {
                                Pipe pipe = mPipeList.get(0);
                                NfcService.getInstance().setSerialNumber(pipe.getSerialNumber());
                                NfcService.getInstance().setPipeGroup(pipe.getPipeGroup());
                                NfcService.getInstance().setPipeGroupName(pipe.getPipeGroupName());
                                NfcService.getInstance().setPipeGroupColor(pipe.getPipeGroupColor());
                                NfcService.getInstance().setPipeType(pipe.getPipeType());
                                NfcService.getInstance().setPipeTypeName(pipe.getPipeTypeName());
                                NfcService.getInstance().setSetPosition(pipe.getSetPosition());
                                NfcService.getInstance().setDistanceDirection(pipe.getDistanceDirection());
                                NfcService.getInstance().setDiameter(pipe.getDiameter());
                                NfcService.getInstance().setMaterial(pipe.getMaterial());
                                NfcService.getInstance().setMaterialName(pipe.getMaterialName());
                                NfcService.getInstance().setDistance(pipe.getDistance());
                                NfcService.getInstance().setDistanceLR(pipe.getDistanceLr());
                                NfcService.getInstance().setPipeDepth(pipe.getPipeDepth());
                                NfcService.getInstance().setPositionX(pipe.getPositionX());
                                NfcService.getInstance().setPositionY(pipe.getPositionY());
                                NfcService.getInstance().setOfferCompany(pipe.getOfferCompany());
                                NfcService.getInstance().setCompanyPhone(pipe.getCompanyPhone());
                                NfcService.getInstance().setMemo(pipe.getMemo());
                                NfcService.getInstance().setBuildCompany(pipe.getBuildCompany());
                                NfcService.getInstance().setBuildPhone(pipe.getBuildPhone());
                                loadPipeInfo();

                                if (getActivity() != null) {
                                    ((DefaultMainActivity)getActivity()).setVisibleNfcReadDialog(false);
                                }

                                if (NfcService.getInstance().getPositionX() != 0.0 || NfcService.getInstance().getPositionY() != 0.0) {
                                    String fileName = String.valueOf(NfcService.getInstance().getPositionX()) + "-" + String.valueOf(NfcService.getInstance().getPositionY()) + ".JPG";
                                    sendImageRequest(fileName);
                                }
                                Utils.showToast(getActivity(), "서버에서 Tag 정보 가져오기를 성공하였습니다.");
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqGetPipeLists() Status Code : " + response.code());
                    if (getActivity() != null) {
                        ((DefaultMainActivity)getActivity()).setVisibleNfcReadDialog(false);
                    }
                    Utils.showToast(getActivity(), "서버에서 Tag 정보 가져오기에 실패하였습니다.");
                }
            }

            @Override
            public void onFailure(Call<PipeList> call, Throwable t) {
                Log.e(TAG,"reqGetPipeLists() Fail msg : " + t.getMessage());
                if (getActivity() != null) {
                    ((DefaultMainActivity)getActivity()).setVisibleNfcReadDialog(false);
                }
                Utils.showToast(getActivity(), "서버에서 Tag 정보 가져오기에 실패하였습니다.");
            }
        });
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
        int status = NetworkStatus.getConnectivityStatus(getActivity());
        if(status == NetworkStatus.TYPE_NOT_CONNECTED) {
            Utils.showToast(getActivity(), "Network 이 연결되지 않아 사진 정보를 읽어 올 수가 없습니다. Network 상태 확인 후 다시 시작하세요.");
            return;
        }

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
