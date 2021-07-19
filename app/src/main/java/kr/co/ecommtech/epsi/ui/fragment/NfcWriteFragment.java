package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.BaseActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.data.GroupCode;
import kr.co.ecommtech.epsi.ui.data.GroupCodeList;
import kr.co.ecommtech.epsi.ui.data.MaterialCode;
import kr.co.ecommtech.epsi.ui.data.MaterialCodeList;
import kr.co.ecommtech.epsi.ui.data.TypeCode;
import kr.co.ecommtech.epsi.ui.data.TypeCodeList;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.DistanceDirection;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import kr.co.ecommtech.epsi.ui.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NfcWriteFragment extends Fragment implements CodeListAdapter.OnCodeItemSelectedListener, TypeListAdapter.OnTypeItemSelectedListener {
    private static String TAG = "NfcWriteFragment";

    protected QueryService mQueryService;

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
    @BindView(R.id.et_distance)
    EditText mPipeDistance;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_depth)
    EditText mDepth;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_diameter)
    EditText mPipeDiameter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_material)
    TextView mMaterial;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_position_x)
    EditText mPositionX;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_position_y)
    EditText mPositionY;

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
    @BindView(R.id.code_item_layout)
    RelativeLayout mCodeItemLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.code_item_rv)
    RecyclerView mCodeRecyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_code_list_title)
    TextView mCodeListTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.type_item_layout)
    RelativeLayout mTypeItemLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_type_list_title)
    TextView mTypeListTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.type_item_rv)
    RecyclerView mTypeRecyclerView;

    private String mSelectedPipeGroup;
    private String mSelectedPipeType;
    private String mSelectedMaterial;

    private ArrayList<GroupCode> mGroupCodeList;
    private ArrayList<TypeCode> mTypeCodeList;
    private ArrayList<MaterialCode> mMaterialCodeList;

    private ArrayList<String> mSetPositionList;
    private ArrayList<DistanceDirection> mDistanceDirectionList;

    private CodeListAdapter mCodeListAdapter;
    private TypeListAdapter mTypeListAdapter;

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

        View rootView = inflater.inflate(R.layout.fragment_nfcwrite, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mCodeRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        mTypeRecyclerView.setLayoutManager(layoutManager2);

        mCodeListAdapter = new CodeListAdapter(getActivity(), this);
        mCodeRecyclerView.setAdapter(mCodeListAdapter);

        mTypeListAdapter = new TypeListAdapter(getActivity(), this);
        mTypeRecyclerView.setAdapter(mTypeListAdapter);

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);

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
        loadPipeInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.item_pipe_group, R.id.item_pipe_type, R.id.item_material, R.id.item_set_position, R.id.item_distance_direction, R.id.write_btn, R.id.location_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_pipe_group:
                reqGetPipeGroupCodes();
                break;

            case R.id.item_pipe_type:
                reqGetPipeTypeCodes();
                break;

            case R.id.item_material:
                reqGetPipeMaterialCodes();
                break;

            case R.id.item_set_position:
                makeSetPositionCodes();
                break;

            case R.id.item_distance_direction:
                makeDistanceDirectionCodes();
                break;

            case R.id.write_btn:
                if (TextUtils.isEmpty(mPipeGroup.getText())) {
                    Utils.showToast(getActivity(), "관로종류를 선택하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mPipeType.getText())) {
                    Utils.showToast(getActivity(), "관로형태를 선택하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mPipeDistance.getText())) {
                    Utils.showToast(getActivity(), "이격거리를 입력하세요.");
                    mPipeDistance.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mDepth.getText())) {
                    Utils.showToast(getActivity(), "매설심도를 입력하세요.");
                    mDepth.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mPipeDiameter.getText())) {
                    Utils.showToast(getActivity(), "관경을 입력하세요.");
                    mPipeDiameter.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mMaterial.getText())) {
                    Utils.showToast(getActivity(), "관로재질을 선택하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mPositionX.getText())) {
                    Utils.showToast(getActivity(), "매설위치를 입력하세요.");
                    mPositionX.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mPositionY.getText())) {
                    Utils.showToast(getActivity(), "매설위치를 입력하세요.");
                    mPositionY.requestFocus();
                    return;
                }

                setPipeInfo();
                NfcService.getInstance().enableTagWriteMode();
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(true);
                }
                break;

            case R.id.location_btn:
                ((InfoActivity)getActivity()).findMyLocation(new BaseActivity.OnGpsLocGetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onGpsLocGet(Location location) {
                        mPositionX.setText(String.format("%10.6f", location.getLatitude()));
                        mPositionY.setText(String.format("%10.6f", location.getLongitude()));
                    }
                });
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
                break;

            case EL_EVENT_WRITE_NFC_PIPEINFO:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                }
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

        if (NfcService.getInstance().getDistance() != 0.0) {
            mPipeDistance.setText(String.valueOf(NfcService.getInstance().getDistance()));
        } else {
            mPipeDistance.setText("");
        }

        if (NfcService.getInstance().getDistance() != 0.0) {
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

    private void setPipeInfo() {
        NfcService.getInstance().setPipeGroup(mSelectedPipeGroup);
        NfcService.getInstance().setPipeGroupName(mPipeGroup.getText().toString());
        NfcService.getInstance().setPipeType(mSelectedPipeType);
        NfcService.getInstance().setPipeTypeName(mPipeType.getText().toString());
        NfcService.getInstance().setDistance(Double.parseDouble(mPipeDistance.getText().toString()));
        NfcService.getInstance().setPipeDepth(Double.parseDouble(mDepth.getText().toString()));
        NfcService.getInstance().setDiameter(Double.parseDouble(mPipeDiameter.getText().toString()));
        NfcService.getInstance().setMaterial(mSelectedMaterial);
        NfcService.getInstance().setMaterialName(mMaterial.getText().toString());
        NfcService.getInstance().setPositionX(Double.parseDouble(mPositionX.getText().toString()));
        NfcService.getInstance().setPositionY(Double.parseDouble(mPositionY.getText().toString()));
        NfcService.getInstance().setOfferCompany(mAgency.getText().toString());
        NfcService.getInstance().setCompanyPhone(mPhoneNumber.getText().toString());
        NfcService.getInstance().setMemo(mMemo.getText().toString());
        NfcService.getInstance().setBuildCompany(mMaker.getText().toString());
        NfcService.getInstance().setBuildPhone(mMakerPhone.getText().toString());
    }

    private void reqGetPipeGroupCodes() {
        if (getActivity() == null) {
            return;
        }

        Call<GroupCodeList> call = mQueryService.getPipeGroupCodes();

        call.enqueue(new Callback<GroupCodeList>() {
            @Override
            public void onResponse(Call<GroupCodeList> call, Response<GroupCodeList> response) {
                if(response.isSuccessful()){
                    GroupCodeList mList = response.body();

                    requireActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            mGroupCodeList = (ArrayList<GroupCode>)mList.groupCodeList;

                            if (mGroupCodeList != null && mGroupCodeList.size() > 0) {

                                ArrayList<Object> resultList = new ArrayList<>();
                                for (GroupCode groupCode : mGroupCodeList) {
                                    resultList.add(groupCode);
                                }
                                mCodeListAdapter.setItems(resultList);
                                mCodeListAdapter.notifyDataSetChanged();
                                mCodeListTitle.setText("관로종류");
                                mCodeItemLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqGetPipeGroupCodes() Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GroupCodeList> call, Throwable t) {
                Log.e(TAG,"reqGetPipeGroupCodes() Fail msg : " + t.getMessage());
            }
        });
    }

    private void reqGetPipeTypeCodes() {
        if (getActivity() == null) {
            return;
        }

        Call<TypeCodeList> call = mQueryService.getPipeTypeCodes();

        call.enqueue(new Callback<TypeCodeList>() {
            @Override
            public void onResponse(Call<TypeCodeList> call, Response<TypeCodeList> response) {
                if(response.isSuccessful()){
                    TypeCodeList mList = response.body();

                    requireActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            mTypeCodeList = (ArrayList<TypeCode>)mList.typeCodeList;

                            if (mTypeCodeList != null && mTypeCodeList.size() > 0) {
                                ArrayList<Object> resultList = new ArrayList<>();
                                for (TypeCode typeCode : mTypeCodeList) {
                                    resultList.add(typeCode);
                                }

                                mTypeListAdapter.setItems(resultList);
                                mTypeListAdapter.notifyDataSetChanged();
                                mTypeListTitle.setText("관로형태");
                                mTypeItemLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqGetPipeTypeCodes() Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TypeCodeList> call, Throwable t) {
                Log.e(TAG,"reqGetPipeTypeCodes() Fail msg : " + t.getMessage());
            }
        });
    }

    private void reqGetPipeMaterialCodes() {
        if (getActivity() == null) {
            return;
        }

        Call<MaterialCodeList> call = mQueryService.getPipeMaterialCodes();

        call.enqueue(new Callback<MaterialCodeList>() {
            @Override
            public void onResponse(Call<MaterialCodeList> call, Response<MaterialCodeList> response) {
                if(response.isSuccessful()){
                    MaterialCodeList mList = response.body();

                    requireActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            mMaterialCodeList = (ArrayList<MaterialCode>)mList.materialCodeList;

                            if (mMaterialCodeList != null && mMaterialCodeList.size() > 0) {

                                ArrayList<Object> resultList = new ArrayList<>();
                                for (MaterialCode materialCode : mMaterialCodeList) {
                                    resultList.add(materialCode);
                                }
                                mCodeListAdapter.setItems(resultList);
                                mCodeListAdapter.notifyDataSetChanged();
                                mCodeListTitle.setText("관로재질");
                                mCodeItemLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqGetPipeMaterialCodes() Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MaterialCodeList> call, Throwable t) {
                Log.e(TAG,"reqGetPipeMaterialCodes() Fail msg : " + t.getMessage());
            }
        });
    }

    public void makeSetPositionCodes() {
        mSetPositionList.clear();
        mSetPositionList.add("경계석");
        mSetPositionList.add("관로위");
    }

    public void makeDistanceDirectionCodes() {
        mDistanceDirectionList.add(DistanceDirection.EL_DIRECTION_CENTER);
        mDistanceDirectionList.add(DistanceDirection.EL_DIRECTION_LEFT);
        mDistanceDirectionList.add(DistanceDirection.EL_DIRECTION_RIGHT);
    }

    @Override
    public void onItemSelected(View v, Object selectedObject) {
        if (selectedObject instanceof GroupCode) {
            mSelectedPipeGroup = ((GroupCode)selectedObject).getGroupCd();
            mPipeGroup.setText(((GroupCode)selectedObject).getGroupName());
        } else if (selectedObject instanceof MaterialCode) {
            mSelectedMaterial = ((MaterialCode)selectedObject).getMaterialCd();
            mMaterial.setText(((MaterialCode)selectedObject).getMaterialName());
        }
        mCodeItemLayout.setVisibility(View.GONE);
    }

    @Override
    public void onTypeItemSelected(View v, Object selectedObject) {
        mSelectedPipeType = ((TypeCode)selectedObject).getTypeCd();
        mPipeType.setText(((TypeCode)selectedObject).getTypeName());

        mTypeItemLayout.setVisibility(View.GONE);
    }
}
