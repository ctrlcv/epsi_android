package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

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
import kr.co.ecommtech.epsi.ui.data.Result;
import kr.co.ecommtech.epsi.ui.data.TypeCode;
import kr.co.ecommtech.epsi.ui.data.TypeCodeList;
import kr.co.ecommtech.epsi.ui.dialog.CustomDialog;
import kr.co.ecommtech.epsi.ui.network.HttpClientToken;
import kr.co.ecommtech.epsi.ui.services.DistanceDirection;
import kr.co.ecommtech.epsi.ui.services.Event;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.services.QueryService;
import kr.co.ecommtech.epsi.ui.utils.DecimalDigitsInputFilter;
import kr.co.ecommtech.epsi.ui.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class NfcWriteFragment extends Fragment implements CodeListAdapter.OnCodeItemSelectedListener, TypeListAdapter.OnTypeItemSelectedListener, DirectionListAdapter.OnDirectionItemSelectedListener {
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
    @BindView(R.id.et_set_position)
    TextView mSetPosition;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance_direction)
    TextView mDistanceDirection;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance)
    EditText mPipeDistance;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_distance_lr)
    EditText mPipeDistanceLR;

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
    @BindView(R.id.item_distance_direction)
    RelativeLayout mDistanceDirectionLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.item_distance)
    RelativeLayout mDistanceLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.item_distance_lr)
    RelativeLayout mDistanceLRLayout;

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

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.direction_item_layout)
    RelativeLayout mDirectionItemLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_direction_list_title)
    TextView mDirectionListTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.direction_item_rv)
    RecyclerView mDirectionRecyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.site_image)
    ImageView mSiteImage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.select_site_image)
    LinearLayout mSelectImageLayout;

    private String mSelectedPipeGroup;
    private String mSelectedPipeType;
    private String mSelectedMaterial;

    private ArrayList<GroupCode> mGroupCodeList;
    private ArrayList<TypeCode> mTypeCodeList;
    private ArrayList<MaterialCode> mMaterialCodeList;

    private CodeListAdapter mCodeListAdapter;
    private TypeListAdapter mTypeListAdapter;
    private DirectionListAdapter mDirectionListAdapter;

    private Uri mSelectedFilePathUri;

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

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
        mDirectionRecyclerView.setLayoutManager(layoutManager3);

        mCodeListAdapter = new CodeListAdapter(getActivity(), this);
        mCodeRecyclerView.setAdapter(mCodeListAdapter);

        mTypeListAdapter = new TypeListAdapter(getActivity(), this);
        mTypeRecyclerView.setAdapter(mTypeListAdapter);

        mDirectionListAdapter = new DirectionListAdapter(getActivity(), this);
        mDirectionRecyclerView.setAdapter(mDirectionListAdapter);

        mQueryService = HttpClientToken.getRetrofit().create(QueryService.class);

        mSiteImage.setVisibility(View.GONE);
        mSelectImageLayout.setVisibility(View.VISIBLE);

        mPipeDiameter.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        mDepth.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});
        mPipeDistance.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});
        mPipeDistanceLR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

        mPositionX.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void afterTextChanged(Editable arg0) {
                String str = mPositionX.getText().toString();
                if (str.isEmpty()) return;
                String str2 = PerfectDecimal(str, 3, 6);

                if (!str2.equals(str)) {
                    mPositionX.setText(str2);
                    int pos = mPositionX.getText().length();
                    mPositionX.setSelection(pos);
                }
            }
        });
        mPositionY.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void afterTextChanged(Editable arg0) {
                String str = mPositionY.getText().toString();
                if (str.isEmpty()) return;
                String str2 = PerfectDecimal(str, 3, 6);

                if (!str2.equals(str)) {
                    mPositionY.setText(str2);
                    int pos = mPositionY.getText().length();
                    mPositionY.setSelection(pos);
                }
            }
        });

        reqGetPipeGroupCodes(false);
        reqGetPipeTypeCodes(false);
        reqGetPipeMaterialCodes(false);

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

        if (NfcService.getInstance().isTabChangedFromReadToWrite()) {
            loadPipeInfo();
        }

        NfcService.getInstance().setTabChangedFromReadToWrite(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.item_pipe_group, R.id.item_pipe_type, R.id.item_material, R.id.item_set_position,
              R.id.item_distance_direction, R.id.write_btn, R.id.location_btn, R.id.site_image,
              R.id.select_site_image, R.id.code_item_layout, R.id.type_item_layout, R.id.direction_item_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.code_item_layout:
                mCodeItemLayout.setVisibility(View.GONE);
                break;

            case R.id.type_item_layout:
                mTypeItemLayout.setVisibility(View.GONE);
                break;

            case R.id.direction_item_layout:
                mDirectionItemLayout.setVisibility(View.GONE);
                break;

            case R.id.item_pipe_group:
                if (mGroupCodeList != null && mGroupCodeList.size() > 0) {

                    ArrayList<Object> resultList = new ArrayList<>();
                    for (GroupCode groupCode : mGroupCodeList) {
                        resultList.add(groupCode);
                    }
                    mCodeListAdapter.setItems(resultList);
                    mCodeListAdapter.notifyDataSetChanged();
                    mCodeListTitle.setText("관로종류");
                    mCodeItemLayout.setVisibility(View.VISIBLE);
                } else {
                    reqGetPipeGroupCodes(true);
                }
                break;

            case R.id.item_pipe_type:
                if (mTypeCodeList != null && mTypeCodeList.size() > 0) {
                    ArrayList<Object> resultList = new ArrayList<>();
                    for (TypeCode typeCode : mTypeCodeList) {
                        resultList.add(typeCode);
                    }

                    mTypeListAdapter.setItems(resultList, mSetPosition.getText().toString(), mDistanceDirection.getText().toString());
                    mTypeListAdapter.notifyDataSetChanged();
                    mTypeListTitle.setText("관로형태");
                    mTypeItemLayout.setVisibility(View.VISIBLE);
                } else {
                    reqGetPipeTypeCodes(true);
                }
                break;

            case R.id.item_material:
                if (mMaterialCodeList != null && mMaterialCodeList.size() > 0) {

                    ArrayList<Object> resultList = new ArrayList<>();
                    for (MaterialCode materialCode : mMaterialCodeList) {
                        resultList.add(materialCode);
                    }
                    mCodeListAdapter.setItems(resultList);
                    mCodeListAdapter.notifyDataSetChanged();
                    mCodeListTitle.setText("관로재질");
                    mCodeItemLayout.setVisibility(View.VISIBLE);
                } else {
                    reqGetPipeMaterialCodes(true);
                }
                break;

            case R.id.item_set_position:
                makeSetPositionCodes();
                break;

            case R.id.item_distance_direction:
                String setPosition = mSetPosition.getText().toString();
                if (!TextUtils.isEmpty(setPosition) && setPosition.equals("관로위")) {
                    return;
                }
                makeDistanceDirectionCodes();
                break;

            case R.id.write_btn:
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
                if (nfcAdapter == null) {
                    Utils.showToast(getActivity(), "NFC 쓰기를 사용할 수 없습니다.");
                    return;
                } else if (!nfcAdapter.isEnabled()) {
                    Utils.showToast(getActivity(), "NFC 설정을 확인하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mPipeGroup.getText())) {
                    Utils.showToast(getActivity(), "관로종류를 선택하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mPipeType.getText())) {
                    Utils.showToast(getActivity(), "관로형태를 선택하세요.");
                    return;
                }

                if (TextUtils.isEmpty(mSetPosition.getText())) {
                    Utils.showToast(getActivity(), "설치위치를 선택하세요.");
                    return;
                }

                if (!TextUtils.isEmpty(mSetPosition.getText()) && "경계석".equals(mSetPosition.getText())) {
                    if (TextUtils.isEmpty(mDistanceDirection.getText())) {
                        Utils.showToast(getActivity(), "이격위치를 선택하세요.");
                        return;
                    }

                    if (TextUtils.isEmpty(mPipeDistance.getText())) {
                        Utils.showToast(getActivity(), "이격거리/정면을 입력하세요.");
                        mPipeDistance.requestFocus();
                        return;
                    }

                    if (!TextUtils.isEmpty(mDistanceDirection.getText()) && !"CENTER".equals(mDistanceDirection.getText())) {
                        if (TextUtils.isEmpty(mPipeDistanceLR.getText())) {
                            Utils.showToast(getActivity(), "이격거리/좌우를 입력하세요.");
                            mPipeDistanceLR.requestFocus();
                            return;
                        }
                    }
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

                NfcService.getInstance().enableTagWriteMode(getActivity());
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(true);
                }

//                if (false) {
//                    NfcService.getInstance().setLockPassword("");
//                    NfcService.getInstance().setNewPassword("");
//
//                    new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
//                        @Override
//                        public void onCreate(Dialog dialog) {
//                            dialog.setContentView(R.layout.dialog_password);
//
//                            EditText passwordEt = dialog.findViewById(R.id.et_password);
//                            passwordEt.setFilters(new InputFilter[]{mPasswordFilter});
//
//                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//                            imm.showSoftInput(passwordEt, 0);
//
//                            passwordEt.requestFocus();
//
//                            TextView contentTv = dialog.findViewById(R.id.tv_content);
//                            contentTv.setText("암호 4자리를 입력하세요");
//
//                            TextView okBtn = dialog.findViewById(R.id.btn_password_ok);
//                            okBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    String password = passwordEt.getText().toString();
//                                    if (TextUtils.isEmpty(password)) {
//                                        passwordEt.requestFocus();
//                                    }
//
//                                    if (password.length() != 4) {
//                                        Utils.showToast(getActivity(), "암호는 4자리입니다.");
//                                        return;
//                                    }
//
//                                    dialog.dismiss();
//
//                                    Log.d(TAG, "password :" + password);
//                                    NfcService.getInstance().setLockPassword(password);
//                                    setPipeInfo();
//
//                                    NfcService.getInstance().enableTagWriteMode(getActivity());
//                                    if (getActivity() != null) {
//                                        ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(true);
//                                    }
//                                }
//                            });
//                        }
//                    }).show();
//                }
                break;

            case R.id.location_btn:
                ((InfoActivity)getActivity()).findMyLocation(new BaseActivity.OnGpsLocGetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onGpsLocGet(Location location) {
                        mPositionX.setText(String.format("%10.6f", location.getLatitude()).trim());
                        mPositionY.setText(String.format("%10.6f", location.getLongitude()).trim());
                    }
                });
                break;

            case R.id.select_site_image:
            case R.id.site_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                mImageLauncher.launch(intent);
                break;

            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }

        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);

        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
        }

        return null;
    }

    ActivityResultLauncher<Intent> mImageLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    mSelectedFilePathUri = intent.getData();
                    Log.d(TAG, mSelectedFilePathUri.toString());
                    mSiteImage.setImageURI(mSelectedFilePathUri);
                    mSiteImage.setVisibility(View.VISIBLE);
                    mSelectImageLayout.setVisibility(View.GONE);
                }
            }
        });

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage e) {
        Log.d(TAG, "onMessageEvent()" + e);

        switch (e.what) {
            case EL_EVENT_READ_NFC_PIPEINFO:
                loadPipeInfo();
                break;

            case EL_EVENT_WRITE_NFC_FAIL:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        MessageTv.setText("Tag 쓰기에 실패하였습니다. 다시 시도하시기 바랍니다. 오류가 반복된다면 Tag가 손상되었을 가능성이 있습니다. Tag 상태를 확인하세요.");

                        NfcService.getInstance().onPauseNfcMode();
                        NfcService.getInstance().setWriteMode(false);

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_WRITE_NFC_SET_PW_FAIL:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        MessageTv.setText("암호 저장에 실패하였습니다. 다시 시도하시기 바랍니다. 오류가 반복된다면 Tag가 손상되었을 가능성이 있습니다. Tag 상태를 확인하세요.");

                        NfcService.getInstance().onPauseNfcMode();
                        NfcService.getInstance().setWriteMode(false);

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_WRITE_NFC_DEL_PW_OK:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_password);

                        EditText passwordEt = dialog.findViewById(R.id.et_password);
                        passwordEt.setFilters(new InputFilter[]{mPasswordFilter});

                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.showSoftInput(passwordEt, 0);

                        passwordEt.requestFocus();

                        TextView contentTv = dialog.findViewById(R.id.tv_content);
                        contentTv.setText("저장할 암호 4자리를 입력하세요");

                        TextView okBtn = dialog.findViewById(R.id.btn_password_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String password = passwordEt.getText().toString();
                                if (TextUtils.isEmpty(password)) {
                                    passwordEt.requestFocus();
                                }

                                if (password.length() != 4) {
                                    Utils.showToast(getActivity(), "암호는 4자리입니다.");
                                    return;
                                }

                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword(password);
                                setPipeInfo();

                                NfcService.getInstance().enableTagWriteMode(getActivity());
                                if (getActivity() != null) {
                                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(true);
                                }
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_WRITE_NFC_DEL_PW_FAIL:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        MessageTv.setText("비밀번호가 맞지 않습니다.");

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");

                                NfcService.getInstance().setWriteMode(false);
                                NfcService.getInstance().onPauseNfcMode();
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_WRITE_NFC_DONE:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcWriteDialog(false);
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(true);
                }

                NfcService.getInstance().setWriteMode(false);
                NfcService.getInstance().onPauseNfcMode();
                mSerialNumber.setText(NfcService.getInstance().getSerialNumber());

                if (mSelectedFilePathUri == null) {
                    reqSavePipeInfo();
                } else {
                    String fileName = mPositionX.getText().toString() + "-" + mPositionY.getText().toString() + ".JPG";
                    Log.d(TAG, "save fileName:" + fileName + ", filePath:" + mSelectedFilePathUri.toString());
                    uploadSiteImageByFtp(mSelectedFilePathUri, fileName);
                }
                break;

            case EL_EVENT_UPLOADED_PHOTO:
                reqSavePipeInfo();
                break;

            case EL_EVENT_UPLOADED_PHOTO_FAIL:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        MessageTv.setText("파일 업로드에 실패하였습니다.\n관리자에게 문의 하세요.");

                        NfcService.getInstance().onPauseNfcMode();
                        NfcService.getInstance().setWriteMode(false);

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_DB_SAVE_SUCCESS:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

//                        String password = !TextUtils.isEmpty(NfcService.getInstance().getNewPassword())
//                                            ? NfcService.getInstance().getNewPassword()
//                                            : NfcService.getInstance().getLockPassword();

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        //MessageTv.setText("저장되었습니다.\n\n [주의] 저장된 암호는'" + password + "'입니다. 암호 분실시 Tag 정보를 수정할 수 없습니다. 암호 관리에 유의하시기 바랍니다.");
                        MessageTv.setText("저장되었습니다");

                        mSelectedFilePathUri = null;
                        NfcService.getInstance().onPauseNfcMode();
                        NfcService.getInstance().setWriteMode(false);

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");
                            }
                        });
                    }
                }).show();
                break;

            case EL_EVENT_DB_SAVE_FAIL:
                if (getActivity() != null) {
                    ((InfoActivity) getActivity()).setVisibleNfcSaveDialog(false);
                }

                new CustomDialog(getActivity(), new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onCreate(Dialog dialog) {
                        dialog.setContentView(R.layout.dialog_alert);

                        TextView MessageTv = dialog.findViewById(R.id.tv_dlg_contents);
                        MessageTv.setText("DB 저장에 실패하였습니다.\n관리자에게 문의 하세요.");

                        NfcService.getInstance().onPauseNfcMode();
                        NfcService.getInstance().setWriteMode(false);

                        TextView okBtn = dialog.findViewById(R.id.btn_ok);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                NfcService.getInstance().setLockPassword("");
                                NfcService.getInstance().setNewPassword("");
                            }
                        });
                    }
                }).show();
                break;

            default:
                break;
        }
    }

    public void reqSavePipeInfo() {
        if (getActivity() == null) {
            return;
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("serialno", mSerialNumber.getText().toString());
        map.put("pipegroup", mSelectedPipeGroup);
        map.put("pipetype", mSelectedPipeType);
        map.put("setPosition", mSetPosition.getText().toString());
        map.put("distanceDirection", mDistanceDirection.getText().toString());
        map.put("diameter", mPipeDistance.getText().toString());
        map.put("material", mSelectedMaterial);

        if (!TextUtils.isEmpty(mPipeDistance.getText().toString())) {
            map.put("distance", Double.parseDouble(mPipeDistance.getText().toString()));
        }

        if (!TextUtils.isEmpty(mPipeDistanceLR.getText().toString())) {
            map.put("distanceLr", Double.parseDouble(mPipeDistanceLR.getText().toString()));
        }

        if (!TextUtils.isEmpty(mDepth.getText().toString())) {
            map.put("pipedepth", Double.parseDouble(mDepth.getText().toString()));
        }

        if (!TextUtils.isEmpty(mPositionX.getText().toString())) {
            map.put("positionx", Double.parseDouble(mPositionX.getText().toString()));
        }

        if (!TextUtils.isEmpty(mPositionY.getText().toString())) {
            map.put("positiony", Double.parseDouble(mPositionY.getText().toString()));
        }
        map.put("offercompany", mAgency.getText().toString());
        map.put("companyphone", mPhoneNumber.getText().toString());
        map.put("memo", mMemo.getText().toString());
        map.put("buildcompany", mMaker.getText().toString());
        map.put("buildphone", mMakerPhone.getText().toString());

        Call<Result> call = mQueryService.savePipeInfo(map);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.isSuccessful()){
                    Result result = response.body();

                    requireActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            if (result.isSuccess()) {
                                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_DB_SAVE_SUCCESS));
                            } else {
                                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_DB_SAVE_FAIL));
                            }
                        }
                    });
                } else {
                    Log.d(TAG,"reqSavePipeInfo() Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG,"reqSavePipeInfo() Fail msg : " + t.getMessage());
            }
        });
    }

    public static InputStream getInputStreamByUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadSiteImageByFtp(Uri filePathUri, String uploadFileName) {
        final String parameter = uploadFileName;

        Thread thread = new Thread(new Runnable() {
            String fileName = parameter.trim();

            @Override
            public void run() {
                FTPClient ftpClient = new FTPClient();

                try {
                    ftpClient.connect("139.150.83.28", FTP.DEFAULT_PORT);
                    ftpClient.login("root", "ecomm123456");
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Utils.showToast(requireActivity(), "FTP 접속에 실패하여 사진을 upload 할 수 없습니다. 관리자에게 문의하세요.");
                    return;
                }

                try {
                    ftpClient.deleteFile("/siteImages/" + fileName);
                    ftpClient.changeWorkingDirectory("/usr/local/apache/htdocs/siteImages");

                    InputStream inputStream  = getInputStreamByUri(getActivity(), filePathUri);
                    Log.d(TAG, "uploadSiteImageByFtp() inputStream:" + inputStream.available());
                    boolean isSuccess = ftpClient.storeFile(fileName, inputStream);

                    if (isSuccess){
                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_UPLOADED_PHOTO));
                    } else {
                        EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_UPLOADED_PHOTO_FAIL));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_UPLOADED_PHOTO_FAIL));
                    return;
                }
            }
        });
        thread.start();
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

        if (NfcService.getInstance().getSiteImage() != null) {
            mSiteImage.setImageBitmap(NfcService.getInstance().getSiteImage());
            mSiteImage.setVisibility(View.VISIBLE);
            mSelectImageLayout.setVisibility(View.GONE);
        }

        if (NfcService.getInstance().getPositionX() != 0.0 || NfcService.getInstance().getPositionY() != 0.0) {
            String fileName = String.valueOf(NfcService.getInstance().getPositionX()) + "-" + String.valueOf(NfcService.getInstance().getPositionY()) + ".JPG";
            sendImageRequest(fileName);
        }
    }

    private String getPipeTypeCode(String pipeTypeName) {
        if (pipeTypeName == null || TextUtils.isEmpty(pipeTypeName)) {
            return null;
        }

        for (int i = 0; i < mTypeCodeList.size() ; i++) {
            if (pipeTypeName.equals(mTypeCodeList.get(i).getTypeName())) {
                return mTypeCodeList.get(i).getTypeCd();
            }
        }

        return null;
    }

    private String getPipeGroupCode(String pipeGroupName) {
        if (pipeGroupName == null || TextUtils.isEmpty(pipeGroupName)) {
            return null;
        }

        for (int i = 0; i < mGroupCodeList.size() ; i++) {
            if (pipeGroupName.equals(mGroupCodeList.get(i).getGroupName())) {
                return mGroupCodeList.get(i).getGroupCd();
            }
        }

        return null;
    }

    private String getPipeGroupColor(String pipeGroupName) {
        if (pipeGroupName == null || TextUtils.isEmpty(pipeGroupName)) {
            return null;
        }

        for (int i = 0; i < mGroupCodeList.size() ; i++) {
            if (pipeGroupName.equals(mGroupCodeList.get(i).getGroupName())) {
                return mGroupCodeList.get(i).getGroupColor();
            }
        }

        return null;
    }

    private String getMaterialCode(String materialName) {
        if (materialName == null || TextUtils.isEmpty(materialName)) {
            return null;
        }

        for (int i = 0; i < mMaterialCodeList.size() ; i++) {
            if (materialName.equals(mMaterialCodeList.get(i).getMaterialName())) {
                return mMaterialCodeList.get(i).getMaterialCd();
            }
        }

        return null;
    }

    private void setPipeInfo() {
        mSelectedPipeGroup = getPipeGroupCode(mPipeGroup.getText().toString());
        NfcService.getInstance().setPipeGroup(mSelectedPipeGroup);
        NfcService.getInstance().setPipeGroupName(mPipeGroup.getText().toString());
        NfcService.getInstance().setPipeGroupColor(getPipeGroupColor(mPipeGroup.getText().toString()));

        mSelectedPipeType = getPipeTypeCode(mPipeType.getText().toString());
        NfcService.getInstance().setPipeType(mSelectedPipeType);
        NfcService.getInstance().setPipeTypeName(mPipeType.getText().toString());

        NfcService.getInstance().setSetPosition(mSetPosition.getText().toString());
        NfcService.getInstance().setDistanceDirection(mDistanceDirection.getText().toString());

        if (!TextUtils.isEmpty(mPipeDistance.getText().toString())) {
            NfcService.getInstance().setDistance(Double.parseDouble(mPipeDistance.getText().toString()));
        }

        if (!TextUtils.isEmpty(mPipeDistanceLR.getText().toString())) {
            NfcService.getInstance().setDistanceLR(Double.parseDouble(mPipeDistanceLR.getText().toString()));
        }

        if (!TextUtils.isEmpty(mDepth.getText().toString())) {
            NfcService.getInstance().setPipeDepth(Double.parseDouble(mDepth.getText().toString()));
        }

        if (!TextUtils.isEmpty(mPipeDiameter.getText().toString())) {
            NfcService.getInstance().setDiameter(Double.parseDouble(mPipeDiameter.getText().toString()));
        }

        mSelectedMaterial = getMaterialCode(mMaterial.getText().toString());
        NfcService.getInstance().setMaterial(mSelectedMaterial);
        NfcService.getInstance().setMaterialName(mMaterial.getText().toString());

        double positionX = Double.parseDouble(mPositionX.getText().toString());
//        positionX = Math.round(positionX * 1000000) / 1000000;

        double positionY = Double.parseDouble(mPositionY.getText().toString());
//        positionY = Math.round(positionY * 1000000) / 1000000;

        NfcService.getInstance().setPositionX(positionX);
        NfcService.getInstance().setPositionY(positionY);
        NfcService.getInstance().setOfferCompany(mAgency.getText().toString());
        NfcService.getInstance().setCompanyPhone(mPhoneNumber.getText().toString());
        NfcService.getInstance().setMemo(mMemo.getText().toString());
        NfcService.getInstance().setBuildCompany(mMaker.getText().toString());
        NfcService.getInstance().setBuildPhone(mMakerPhone.getText().toString());
    }

    private void reqGetPipeGroupCodes(boolean showPopup) {
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

                            if (!showPopup) {
                                return;
                            }

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

    private void reqGetPipeTypeCodes(boolean showPopup) {
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

                            if (!showPopup) {
                                return;
                            }

                            if (mTypeCodeList != null && mTypeCodeList.size() > 0) {
                                ArrayList<Object> resultList = new ArrayList<>();
                                for (TypeCode typeCode : mTypeCodeList) {
                                    resultList.add(typeCode);
                                }

                                mTypeListAdapter.setItems(resultList, mSetPosition.getText().toString(), mDistanceDirection.getText().toString());
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

    private void reqGetPipeMaterialCodes(boolean showPopup) {
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

                            if (!showPopup) {
                                return;
                            }

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
        ArrayList<Object> resultList = new ArrayList<>();
        resultList.add("경계석");
        resultList.add("관로위");

        mDirectionListAdapter.setItems(resultList, mPipeType.getText().toString(), mSetPosition.getText().toString(), mDistanceDirection.getText().toString());
        mDirectionListAdapter.notifyDataSetChanged();
        mDirectionListTitle.setText("설치위치");
        mDirectionItemLayout.setVisibility(View.VISIBLE);
    }

    public void makeDistanceDirectionCodes() {
        ArrayList<Object> resultList = new ArrayList<>();
        resultList.add(DistanceDirection.EL_DIRECTION_CENTER);
        resultList.add(DistanceDirection.EL_DIRECTION_LEFT);
        resultList.add(DistanceDirection.EL_DIRECTION_RIGHT);

        mDirectionListAdapter.setItems(resultList, mPipeType.getText().toString(), mSetPosition.getText().toString(), mDistanceDirection.getText().toString());
        mDirectionListAdapter.notifyDataSetChanged();
        mDirectionListTitle.setText("이격위치");
        mDirectionItemLayout.setVisibility(View.VISIBLE);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onTypeItemSelected(View v, Object selectedObject) {
        if (selectedObject instanceof TypeCode) {
            mSelectedPipeType = ((TypeCode)selectedObject).getTypeCd();
            mPipeType.setText(((TypeCode)selectedObject).getTypeName());
        }

        mTypeItemLayout.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDirectionItemSelected(View v, Object selectedObject) {
        if (selectedObject instanceof String) {
            String selText = (String)selectedObject;

            mSetPosition.setText(selText);
            if (!TextUtils.isEmpty(selText) && "관로위".equals(selText)) {
                mDistanceDirection.setText("");
                mPipeDistance.setText("");
                mPipeDistanceLR.setText("");
                mDistanceDirectionLayout.setBackgroundResource(R.drawable.border_c4c4c4_840a47_3dp);
                mDistanceLayout.setBackgroundResource(R.drawable.border_c4c4c4_840a47_3dp);
                mDistanceLRLayout.setBackgroundResource(R.drawable.border_c4c4c4_840a47_3dp);
                mPipeDistance.setEnabled(false);
                mPipeDistanceLR.setEnabled(false);
            } else {
                mDistanceDirectionLayout.setBackgroundResource(R.drawable.border_ffffff_840a47_3dp);
                mDistanceLayout.setBackgroundResource(R.drawable.border_ffffff_840a47_3dp);
                mDistanceLRLayout.setBackgroundResource(R.drawable.border_ffffff_840a47_3dp);
                mPipeDistance.setEnabled(true);
                mPipeDistanceLR.setEnabled(true);
            }
        } else if (selectedObject instanceof DistanceDirection) {
            mDistanceLRLayout.setBackgroundResource(R.drawable.border_ffffff_840a47_3dp);
            mPipeDistanceLR.setEnabled(true);

            if ((DistanceDirection)selectedObject == DistanceDirection.EL_DIRECTION_CENTER) {
                mDistanceDirection.setText("CENTER");
                mDistanceLRLayout.setBackgroundResource(R.drawable.border_c4c4c4_840a47_3dp);
                mPipeDistanceLR.setText("");
                mPipeDistanceLR.setEnabled(false);
            } else if ((DistanceDirection)selectedObject == DistanceDirection.EL_DIRECTION_LEFT) {
                mDistanceDirection.setText("LEFT");
            } else if ((DistanceDirection)selectedObject == DistanceDirection.EL_DIRECTION_RIGHT) {
                mDistanceDirection.setText("RIGHT");
            }
        }

        mDirectionItemLayout.setVisibility(View.GONE);
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
                mSelectImageLayout.setVisibility(View.VISIBLE);
            } else {
                mSiteImage.setImageBitmap(result);
                NfcService.getInstance().setSiteImage(result);

                mSiteImage.setVisibility(View.VISIBLE);
                mSelectImageLayout.setVisibility(View.GONE);
            }
        }
    }

    public InputFilter mPasswordFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL){
        if(str.charAt(0) == '.') str = "0"+str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0; char t;
        while(i < max){
            t = str.charAt(i);
            if(t != '.' && after == false){
                up++;
                if(up > MAX_BEFORE_POINT) return rFinal;
            }else if(t == '.'){
                after = true;
            }else{
                decimal++;
                if(decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }return rFinal;
    }
}
