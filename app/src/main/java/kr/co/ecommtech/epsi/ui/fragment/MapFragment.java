package kr.co.ecommtech.epsi.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.BaseActivity;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.activity.LoginActivity;
import kr.co.ecommtech.epsi.ui.data.Pipe;
import kr.co.ecommtech.epsi.ui.data.PipeList;
import kr.co.ecommtech.epsi.ui.dialog.CustomDialog;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, DefaultMainActivity.OnBackPressedListener {
    private final static String TAG = "MapFragment";

    private final static int markerWidth = 56;
    private final static int markerHeight = 80;

    private static final int PERMISSION_REQUEST_CODE = 9621;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.detail_info_layout)
    LinearLayout mDetailInfoLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_info_title)
    TextView mPipeGroupTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_serialnumber)
    TextView mSerialNumberTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_type)
    TextView mPipeTypeTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_set_position)
    TextView mPipeSetPositionTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_direction_title)
    TextView mPipeDistanceDirectionTitleTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_direction)
    TextView mPipeDistanceDirectionTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_title)
    TextView mPipeDistanceTitleTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance)
    TextView mPipeDistanceTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_diameter)
    TextView mPipeDiameter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_depth)
    TextView mPipeDepth;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_material)
    TextView mPipeMaterial;

    protected QueryService mQueryService;

    Marker mSelectedMarker;
    LatLng mCurrentLatLng = new LatLng(37.49833833333333, 127.06261666666666);

    ArrayList<Pipe> mPipeList = null;
    ArrayList<Marker> mMarkerList = null;

    boolean mIsMapReady = false;

    OverlayImage mCenterImage;
    OverlayImage mPipeBlackImage;
    OverlayImage mPipeBlackSelectImage;
    OverlayImage mPipeBlueImage;
    OverlayImage mPipeBlueSelectImage;
    OverlayImage mPipeRedImage;
    OverlayImage mPipeRedSelectImage;
    OverlayImage mPipeYellowImage;
    OverlayImage mPipeYellowSelectImage;

    Pipe mSelectedPipe;
    FusedLocationSource mLocationSource;

    MapView mMapView;
    NaverMap mNaverMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() mIsMapReady:" + mIsMapReady);

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("지도보기");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(true);
        }

        if (!((DefaultMainActivity)getActivity()).isGPSEnable()) {
            new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                @Override
                public void onCreate(Dialog dialog) {
                    dialog.setContentView(R.layout.dialog_setting);

                    TextView titleTv = dialog.findViewById(R.id.tv_dialog_setting_title);
                    titleTv.setText("GPS 설정");

                    TextView contentTitleTv = dialog.findViewById(R.id.tv_content_title);
                    contentTitleTv.setText("GPS 설정안내");

                    TextView contentBodyTv = dialog.findViewById(R.id.tv_content_body);
                    contentBodyTv.setText("현재 위치 정보를 확인하기 위해 GPS 를 ON 해 주시기 바랍니다. GPS OFF일 경우 서비스 일부가 제한될 수 있습니다.");

                    TextView cancelBtn = dialog.findViewById(R.id.btn_cancel);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView okBtn = dialog.findViewById(R.id.btn_ok);
                    okBtn.setText("GPS 설정");
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
                }
            }).show();
        }

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);
        mIsMapReady = false;

        mMapView = (MapView) rootView.findViewById(R.id.naver_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mCenterImage = OverlayImage.fromResource(R.drawable.map_center);
        mPipeBlackImage = OverlayImage.fromResource(R.drawable.pipe_black);
        mPipeBlackSelectImage = OverlayImage.fromResource(R.drawable.pipe_black_sel);
        mPipeBlueImage = OverlayImage.fromResource(R.drawable.pipe_blue);
        mPipeBlueSelectImage = OverlayImage.fromResource(R.drawable.pipe_blue_sel);
        mPipeRedImage = OverlayImage.fromResource(R.drawable.pipe_red);
        mPipeRedSelectImage = OverlayImage.fromResource(R.drawable.pipe_red_sel);
        mPipeYellowImage = OverlayImage.fromResource(R.drawable.pipe_yellow);
        mPipeYellowSelectImage = OverlayImage.fromResource(R.drawable.pipe_yellow_sel);

        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        ((DefaultMainActivity)getActivity()).findMyLocation(new BaseActivity.OnGpsLocGetListener() {
            @Override
            public void onGpsLocGet(Location location) {
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "get Current Position!");
                updateCurrentPosition(mCurrentLatLng);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(false);
            ((DefaultMainActivity)getActivity()).stopGpsSearch();
        }

        mPipeList = null;
        mMarkerList = null;
        mLocationSource = null;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.view_detail_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_detail_layout:
                if (mSelectedPipe == null) {
                    return;
                }

                NfcService.getInstance().setSerialNumber(mSelectedPipe.getSerialNumber());
                NfcService.getInstance().setPipeGroup(mSelectedPipe.getPipeGroup());
                NfcService.getInstance().setPipeGroupName(mSelectedPipe.getPipeGroupName());
                NfcService.getInstance().setPipeGroupColor(mSelectedPipe.getPipeGroupColor());
                NfcService.getInstance().setPipeType(mSelectedPipe.getPipeType());
                NfcService.getInstance().setPipeTypeName(mSelectedPipe.getPipeTypeName());
                NfcService.getInstance().setSetPosition(mSelectedPipe.getSetPosition());
                NfcService.getInstance().setDistanceDirection(mSelectedPipe.getDistanceDirection());
                NfcService.getInstance().setDiameter(mSelectedPipe.getDiameter());
                NfcService.getInstance().setMaterial(mSelectedPipe.getMaterial());
                NfcService.getInstance().setMaterialName(mSelectedPipe.getMaterialName());
                NfcService.getInstance().setDistance(mSelectedPipe.getDistance());
                NfcService.getInstance().setDistanceLR(mSelectedPipe.getDistanceLr());
                NfcService.getInstance().setPipeDepth(mSelectedPipe.getPipeDepth());
                NfcService.getInstance().setPositionX(mSelectedPipe.getPositionX());
                NfcService.getInstance().setPositionY(mSelectedPipe.getPositionY());
                NfcService.getInstance().setOfferCompany(mSelectedPipe.getOfferCompany());
                NfcService.getInstance().setCompanyPhone(mSelectedPipe.getCompanyPhone());
                NfcService.getInstance().setMemo(mSelectedPipe.getMemo());
                NfcService.getInstance().setBuildCompany(mSelectedPipe.getBuildCompany());
                NfcService.getInstance().setBuildPhone(mSelectedPipe.getBuildPhone());
                NfcService.getInstance().setLoadFromMap(true);

//                Intent intent = new Intent(this, InfoActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra("fromMap", true);
//                startActivity(intent);
                break;
        }
    }

    public void makeMakerList() {
        if (mCurrentLatLng == null || mPipeList == null || mPipeList.size() == 0) {
            return;
        }

        Log.d(TAG, "makeMarkerList()");

        if (mMarkerList == null) {
            mMarkerList = new ArrayList<>();
        }
        mMarkerList.clear();

        Marker newMarker;

        for (int i = 0 ; i < mPipeList.size(); i++) {
            Pipe pipe = mPipeList.get(i);

            newMarker = new Marker();

            if (pipe.getPositionX() > 100 && pipe.getPositionY() < 100) {
                newMarker.setPosition(new LatLng(pipe.getPositionY(), pipe.getPositionX()));
            } else {
                newMarker.setPosition(new LatLng(pipe.getPositionX(), pipe.getPositionY()));
            }

            switch (pipe.getPipeGroup()) {
                case "1":
                    newMarker.setIcon(mPipeBlueImage);
                    break;

                case "2":
                    newMarker.setIcon(mPipeBlackImage);
                    break;

                case "3":
                    newMarker.setIcon(mPipeRedImage);
                    break;

                case "4":
                    newMarker.setIcon(mPipeYellowImage);
                    break;
            }

            newMarker.setTag((Integer)pipe.getPipeId());
            newMarker.setWidth(markerWidth);
            newMarker.setHeight(markerHeight);
            newMarker.setOnClickListener(mMarkerClickListener);
            newMarker.setMap(mNaverMap);

            mMarkerList.add(newMarker);
        }
        Log.d(TAG, "makeMarkerList() mMarkerList: " + mMarkerList.size());
    }

    @Override
    public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady()");
        mNaverMap = naverMap;
        mIsMapReady = true;

        naverMap.setLocationSource(mLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.setOnMapClickListener(mMapClickListener);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);

        if (mCurrentLatLng != null) {
            CameraPosition cameraPosition = new CameraPosition(mCurrentLatLng, 9);
            naverMap.setCameraPosition(cameraPosition);
        }
        reqGetPipeLists();
    }

    @UiThread
    public void updateCurrentPosition(LatLng latLng) {
        if (latLng == null) {
            Log.e(TAG, "updateCurrentPosition() latLng is NULL");
            return;
        }

        CameraPosition cameraPosition = new CameraPosition(latLng, 9);
        if (mNaverMap != null) {
            mNaverMap.setCameraPosition(cameraPosition);
        }
    }

    NaverMap.OnMapClickListener mMapClickListener = new NaverMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
            deSelectMarker();
        }
    };

    Overlay.OnClickListener mMarkerClickListener = new Overlay.OnClickListener() {
        @Override
        public boolean onClick(@NonNull @NotNull Overlay overlay) {
            Marker clickedMarker = (Marker)overlay;

            Log.d(TAG, "Overlay.OnClickListener() clickedMarker:" + clickedMarker.getTag());

            if (clickedMarker.getTag() == null) {
                Log.d(TAG, "Overlay.OnClickListener() clickedMarker.getTag() is NULL, return");
                return false;
            }

            for (int i = 0; i < mPipeList.size(); i++) {
                Pipe pipe = mPipeList.get(i);

                if (pipe.getPipeId() == (int)clickedMarker.getTag()) {
                    switch (pipe.getPipeGroup()) {
                        case "1":
                            if (clickedMarker.equals(mSelectedMarker)) {
                                clickedMarker.setIcon(mPipeBlueImage);
                                mSelectedMarker = null;
                            } else {
                                clickedMarker.setIcon(mPipeBlueSelectImage);
                                mSelectedMarker = clickedMarker;
                            }
                            break;

                        case "2":
                            if (clickedMarker.equals(mSelectedMarker)) {
                                clickedMarker.setIcon(mPipeBlackImage);
                                mSelectedMarker = null;
                            } else {
                                clickedMarker.setIcon(mPipeBlackSelectImage);
                                mSelectedMarker = clickedMarker;
                            }
                            break;

                        case "3":
                            if (clickedMarker.equals(mSelectedMarker)) {
                                clickedMarker.setIcon(mPipeRedImage);
                                mSelectedMarker = null;
                            } else {
                                clickedMarker.setIcon(mPipeRedSelectImage);
                                mSelectedMarker = clickedMarker;
                            }
                            break;

                        case "4":
                            if (clickedMarker.equals(mSelectedMarker)) {
                                clickedMarker.setIcon(mPipeYellowImage);
                                mSelectedMarker = null;
                            } else {
                                clickedMarker.setIcon(mPipeYellowSelectImage);
                                mSelectedMarker = clickedMarker;
                            }
                            break;
                    }
                } else {
                    Marker marker = mMarkerList.get(i);
                    switch (pipe.getPipeGroup()) {
                        case "1":
                            marker.setIcon(mPipeBlueImage);
                            break;

                        case "2":
                            marker.setIcon(mPipeBlackImage);
                            break;

                        case "3":
                            marker.setIcon(mPipeRedImage);
                            break;

                        case "4":
                            marker.setIcon(mPipeYellowImage);
                            break;
                    }
                }
            }
            showDetailInfo();
            return true;
        }
    };

    @SuppressLint("SetTextI18n")
    private void showDetailInfo() {
        if (mSelectedMarker == null) {
            mSelectedPipe = null;
            mDetailInfoLayout.setVisibility(View.GONE);
        } else {
            if (mSelectedMarker.getTag() == null) {
                Log.d(TAG, "showDetailInfo() mSelectedMarker.getTag() is NULL, return");
                return;
            }

            int selectedPipeId = (int)mSelectedMarker.getTag();

            Pipe pipe = null;
            for (int i = 0; i < mPipeList.size(); i++) {
                pipe = mPipeList.get(i);
                if (selectedPipeId == pipe.getPipeId()) {
                    break;
                }
            }

            if (pipe == null) {
                Log.d(TAG, "showDetailInfo() pipe is NULL, return");
                return;
            }

            mSelectedPipe = pipe;

            if (pipe.getPipeGroupName() != null && !TextUtils.isEmpty(pipe.getPipeGroupName())) {
                mPipeGroupTv.setText(pipe.getPipeGroupName());
            } else {
                mPipeGroupTv.setText("");
            }

            if (pipe.getSerialNumber() != null && !TextUtils.isEmpty(pipe.getSerialNumber())) {
                mSerialNumberTv.setText(pipe.getSerialNumber());
            } else {
                mSerialNumberTv.setText("");
            }

            if (pipe.getPipeTypeName() != null && !TextUtils.isEmpty(pipe.getPipeTypeName())) {
                mPipeTypeTv.setText(pipe.getPipeTypeName());
            } else {
                mPipeTypeTv.setText("");
            }

            if (pipe.getSetPosition() != null && !TextUtils.isEmpty(pipe.getSetPosition())) {
                mPipeSetPositionTv.setText(pipe.getSetPosition());
            } else {
                mPipeSetPositionTv.setText("");
            }

            if (TextUtils.isEmpty(pipe.getSetPosition()) ||
                    (!TextUtils.isEmpty(pipe.getSetPosition()) && pipe.getSetPosition().equals("관로위"))) {
                mPipeDistanceDirectionTitleTv.setVisibility(View.GONE);
                mPipeDistanceDirectionTv.setVisibility(View.GONE);
                mPipeDistanceTitleTv.setVisibility(View.GONE);
                mPipeDistanceTv.setVisibility(View.GONE);
            } else {
                mPipeDistanceDirectionTitleTv.setVisibility(View.VISIBLE);
                mPipeDistanceDirectionTv.setVisibility(View.VISIBLE);
                mPipeDistanceTitleTv.setVisibility(View.VISIBLE);
                mPipeDistanceTv.setVisibility(View.VISIBLE);
                mPipeDistanceDirectionTv.setText(pipe.getDistanceDirection());

                if (!TextUtils.isEmpty(pipe.getDistanceDirection()) && pipe.getDistanceDirection().equals("CENTER")) {
                    mPipeDistanceTv.setText("전 " + String.valueOf(pipe.getDistance()) + "m");
                } else {
                    mPipeDistanceTv.setText("전 " + String.valueOf(pipe.getDistance()) + "m  " + "좌 " + String.valueOf(pipe.getDistanceLr()) + "m");
                }
            }

            mPipeDiameter.setText(String.valueOf(pipe.getDiameter()) + "mm");
            mPipeDepth.setText(String.valueOf(pipe.getPipeDepth()) + "m");
            mPipeMaterial.setText(pipe.getMaterialName());

            mDetailInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    private void reqGetPipeLists() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("lat", mCurrentLatLng.latitude);
        map.put("lon", mCurrentLatLng.longitude);

        Call<PipeList> call = mQueryService.getPipeList(map);

        call.enqueue(new Callback<PipeList>() {
            @Override
            public void onResponse(Call<PipeList> call, Response<PipeList> response) {
                if(response.isSuccessful()){
                    PipeList mList = response.body();

                    getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            mPipeList = (ArrayList<Pipe>)mList.pipeList;
                            Log.d(TAG, "LoadPipeList() mPipeList: " + mPipeList.size());
                            if (mIsMapReady) {
                                makeMakerList();
                                if (mCurrentLatLng != null) {
                                    Log.d(TAG, "LoadPipeList() mCurrentLatLng: " + mCurrentLatLng);
                                    updateCurrentPosition(mCurrentLatLng);
                                }
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqGetPipeLists() Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PipeList> call, Throwable t) {
                Log.e(TAG,"reqGetPipeLists() Fail msg : " + t.getMessage());
            }
        });
    }

    public void deSelectMarker() {
        if (mSelectedMarker == null) {
            return;
        }

        for (int i = 0; i < mPipeList.size(); i++) {
            Pipe pipe = mPipeList.get(i);
            Marker marker = mMarkerList.get(i);

            switch (pipe.getPipeGroup()) {
                case "1":
                    marker.setIcon(mPipeBlueImage);
                    break;

                case "2":
                    marker.setIcon(mPipeBlackImage);
                    break;

                case "3":
                    marker.setIcon(mPipeRedImage);
                    break;

                case "4":
                    marker.setIcon(mPipeYellowImage);
                    break;
            }
        }
        mSelectedMarker = null;
        showDetailInfo();
    }

    @Override
    public void onBack() {
        if (mSelectedMarker != null) {
            deSelectMarker();
            return;
        }

        if (getActivity() != null) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((DefaultMainActivity)context).setOnBackPressedListener(this);
    }
}
