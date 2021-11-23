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

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoPageAdapter;
import kr.co.ecommtech.epsi.ui.services.Event;
import kr.co.ecommtech.epsi.ui.services.EventMessage;
import kr.co.ecommtech.epsi.ui.services.NfcService;

public class NfcFragment extends Fragment implements DefaultMainActivity.OnBackPressedListener {
    private static String TAG = "NfcFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_tablayout)
    TabLayout mReadInfoLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.readinfo_viewpager)
    ViewPager2 mReadInfoViewPager;

    InfoPageAdapter mInfoPageAdapter;
    private boolean mIsStartFromMap = false;

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

        mIsStartFromMap = NfcService.getInstance().isLoadFromMap();
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

        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("정보읽기");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(true);
            ((DefaultMainActivity)getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((DefaultMainActivity)getActivity()).initInputData();
        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(false);
            if (!mIsStartFromMap) {
                ((DefaultMainActivity) getActivity()).setOnBackPressedListener(null);
            }
        }
    }

    @Override
    public void onBack() {
        if (getActivity() != null) {
            if (mIsStartFromMap) {
                mIsStartFromMap = false;
                NfcService.getInstance().setReLoadMarker(true);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(this);
                fragmentTransaction.commit();
                EventBus.getDefault().post(new EventMessage(Event.EL_EVENT_MAP_REFRESH));
            } else {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((DefaultMainActivity)context).setOnBackPressedListener(this);
    }
}
