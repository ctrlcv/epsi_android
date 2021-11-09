package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.services.LoginManager;
import kr.co.ecommtech.epsi.ui.services.NfcService;

public class InfoFragment extends Fragment {
    private static String TAG = "InfoFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_tablayout)
    TabLayout mNfcLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_viewpager)
    ViewPager2 mNfcViewPager;
    NfcPageAdapter mNfcPageAdapter;

    List<String> mTabElement;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, rootView);

        Log.d(TAG, "onCreateView()");

        if (LoginManager.getInstance().isLoggedIn(getContext()) && !LoginManager.getInstance().getLogInInfo().getAuth().equals("사용자")) {
            mTabElement = Arrays.asList("읽기", "쓰기");
        } else {
            mTabElement = Arrays.asList("읽기");
        }

        mNfcPageAdapter = new NfcPageAdapter(this);
        mNfcViewPager.setAdapter(mNfcPageAdapter);

        new TabLayoutMediator(mNfcLayout, mNfcViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(mTabElement.get(position));
            }
        }).attach();

        mNfcLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            String prevUnselectedTab;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected() prevUnselectedTab:" + prevUnselectedTab + ", selectTab:" + tab.getText());

                if (prevUnselectedTab.equals("읽기") && tab.getText().toString().equals("쓰기")) {
                    NfcService.getInstance().setTabChangedFromReadToWrite(true);
                }

                if (prevUnselectedTab.equals("쓰기") && tab.getText().toString().equals("읽기")) {
                    NfcService.getInstance().setTabChangedFromWriteToRead(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                prevUnselectedTab = tab.getText().toString();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected() " + tab.getText());
            }
        });

        mNfcViewPager.setCurrentItem(0);
        return rootView;
    }
}
