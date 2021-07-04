package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.data.GroupCode;
import kr.co.ecommtech.epsi.ui.data.GroupCodeList;
import kr.co.ecommtech.epsi.ui.data.Pipe;
import kr.co.ecommtech.epsi.ui.data.PipeList;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends BaseActivity implements NaverMap.OnMapClickListener, OnMapReadyCallback {
    private final static String TAG = "MapActivity";

    private static NaverMap mNaverMap;
    protected QueryService mQueryService;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.map_view)

    MapView mMapView;
    Marker mMarker;
    LatLng mCurrentLatLng;
    CameraPosition mCameraPosition;

    ArrayList<Pipe> mPipeList;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        mMarker = new Marker();
        mCurrentLatLng = new LatLng(37.649693286681035, 126.79369788486898);

        findMyLocation(new OnGpsLocGetListener() {
            @Override
            public void onGpsLocGet(Location location) {
                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                updateMap(mCurrentLatLng);
            }
        });

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);
        reqGetPipeLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapClick(@NonNull @NotNull PointF pointF, @NonNull @NotNull LatLng latLng) {

    }

    @Override
    public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
        this.mNaverMap = naverMap;
        updateMap(mCurrentLatLng);
    }

    public void updateMap(LatLng latLng) {
        if (latLng == null) {
            Log.e(TAG, "updateMap() latLng is NULL");
            return;
        }

        mMarker.setPosition(latLng);
        mMarker.setMap(mNaverMap);

        mCameraPosition = new CameraPosition(latLng, 17);
        if (mNaverMap != null && mCameraPosition != null) {
            mNaverMap.setCameraPosition(mCameraPosition);
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

                    runOnUiThread(new Runnable(){
                        public void run(){
                            mPipeList = (ArrayList<Pipe>)mList.pipeList;

                            for (Pipe pipe: mPipeList) {
                                Log.d(TAG, "PIPE:" + pipe);
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
}
