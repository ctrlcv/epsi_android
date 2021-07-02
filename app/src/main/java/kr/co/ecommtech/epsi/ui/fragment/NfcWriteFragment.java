package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NfcWriteFragment extends Fragment {
    private static String TAG = "NfcWriteFragment";

    protected QueryService mQueryService;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_nfcwrite, container, false);
        ButterKnife.bind(this, rootView);

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
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
    @OnClick({R.id.item_ganro_type, R.id.item_ganro_kind, R.id.item_material})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_ganro_type:
                break;

            case R.id.item_ganro_kind:
                break;

            case R.id.item_material:
                break;

            default:
                break;
        }
    }

    private void reqGetItemGroup() {
        if (getActivity() == null) {
            return;
        }

//        HashMap<String, Object> map = new HashMap<>();
//        map.put("companycode", mELInfo.getManagecompany());
//        map.put("elcodes", "'" + mELInfo.getElcode() + "'");
//        map.put("getdetail", true);
//        map.put("orderby", "errordatetime DESC");
//
//        Call<ELBreakStateList> call = mElQueryService.getBreakStateList(map);
//
//        call.enqueue(new Callback<ELBreakStateList>() {
//            @Override
//            public void onResponse(Call<ELBreakStateList> call, Response<ELBreakStateList> response) {
//                if(response.isSuccessful()){
//                    ELBreakStateList mList = response.body();
//
//                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable(){
//                        public void run(){
//                            mBreakStateList = (ArrayList<ELBreakState>)mList.mELBreakStateList;
//                            if (mBreakStateList == null || mBreakStateList.size() <= 0) {
//                                mNoBreakInfoLayout.setVisibility(View.VISIBLE);
//                                mBreakInfoRV.setVisibility(View.GONE);
//                                return;
//                            }
//                            mElevatorBreakAdapter.setItems(mBreakStateList);
//                            mElevatorBreakAdapter.notifyDataSetChanged();
//
//                            mNoBreakInfoLayout.setVisibility(View.GONE);
//                            mBreakInfoRV.setVisibility(View.VISIBLE);
//                        }
//                    });
//                } else {
//                    Log.d(TAG,"reqGetBreakInfo() Status Code : " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ELBreakStateList> call, Throwable t) {
//                Log.e(TAG,"reqGetBreakInfo() Fail msg : " + t.getMessage());
//            }
//        });
    }
}
