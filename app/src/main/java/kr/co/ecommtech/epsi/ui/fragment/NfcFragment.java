package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoPageAdapter;
import kr.co.ecommtech.epsi.ui.services.NfcService;

public class NfcFragment extends Fragment implements DefaultMainActivity.OnBackPressedListener {
    private static String TAG = "NfcFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_tablayout)
    TabLayout mReadInfoLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_viewpager)
    ViewPager2 mReadInfoViewPager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_write_layout)
    RelativeLayout mNfcWriteLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_save_layout)
    RelativeLayout mNfcSaveLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_dialog_title)
    TextView mDialogTitle;

    InfoPageAdapter mInfoPageAdapter;

    final List<String> mTabElement = Arrays.asList("관로정보", "평면도", "단면도");

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nfc, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("정보읽기");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(true);
        }

        mInfoPageAdapter = new InfoPageAdapter(getActivity());
        mReadInfoViewPager.setAdapter(mInfoPageAdapter);

        new TabLayoutMediator(mReadInfoLayout, mReadInfoViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(mTabElement.get(position));
            }
        }).attach();

        mReadInfoViewPager.setCurrentItem(0);

        NfcService.getInstance().initializeNfcMode(getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initInputData();
        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_nfc_write_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nfc_write_cancel:
                if (NfcService.getInstance().isDisableCancel()) {
                    return;
                }

                mNfcWriteLayout.setVisibility(View.GONE);
                NfcService.getInstance().onPauseNfcMode();
                break;

            default:
                break;
        }
    }

    public void setVisibleNfcReadDialog(boolean visible) {
        if (visible) {
            mDialogTitle.setText("TAG 읽기");
        }
        mNfcWriteLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibleNfcWriteDialog(boolean visible) {
        if (visible) {
            mDialogTitle.setText("TAG 쓰기");
        }
        mNfcWriteLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibleNfcSaveDialog(boolean visible) {
        mNfcSaveLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void initInputData() {
        Log.d(TAG, "initInputData()");

        NfcService.getInstance().setSerialNumber("");
        NfcService.getInstance().setPipeGroup("");
        NfcService.getInstance().setPipeGroupName("");
        NfcService.getInstance().setPipeGroupColor("");
        NfcService.getInstance().setPipeType("");
        NfcService.getInstance().setPipeTypeName("");
        NfcService.getInstance().setSetPosition("");
        NfcService.getInstance().setDistanceDirection("");
        NfcService.getInstance().setDistance(0.0);
        NfcService.getInstance().setDistanceLR(0.0);
        NfcService.getInstance().setDiameter(0.0);
        NfcService.getInstance().setMaterial("");
        NfcService.getInstance().setMaterialName("");
        NfcService.getInstance().setPipeDepth(0.0);
        NfcService.getInstance().setPositionX(0.0);
        NfcService.getInstance().setPositionY(0.0);
        NfcService.getInstance().setOfferCompany("");
        NfcService.getInstance().setCompanyPhone("");
        NfcService.getInstance().setMemo("");
        NfcService.getInstance().setBuildCompany("");
        NfcService.getInstance().setBuildPhone("");
        NfcService.getInstance().setSiteImageUrl("");
        NfcService.getInstance().setSiteImage(null);
        NfcService.getInstance().setLockPassword("");
        NfcService.getInstance().setNewPassword("");

        NfcService.getInstance().setReadMode(false);
        NfcService.getInstance().setWriteMode(false);

        setVisibleNfcReadDialog(false);
        setVisibleNfcWriteDialog(false);
    }

    @Override
    public void onBack() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((DefaultMainActivity)context).setOnBackPressedListener(this);
    }

}
