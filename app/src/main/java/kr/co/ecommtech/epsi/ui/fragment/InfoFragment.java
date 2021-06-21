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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;

public class InfoFragment extends Fragment {
    private static String TAG = "InfoFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_tablayout)
    TabLayout mNfcLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nfc_viewpager)
    ViewPager2 mNfcViewPager;

    NfcPageAdapter mNfcPageAdapter;

    final List<String> mTabElement = Arrays.asList("읽기", "쓰기");

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

        mNfcPageAdapter = new NfcPageAdapter(this);
        mNfcViewPager.setAdapter(mNfcPageAdapter);

        new TabLayoutMediator(mNfcLayout, mNfcViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(mTabElement.get(position));
            }
        }).attach();

        mNfcViewPager.setCurrentItem(0);
        return rootView;
    }
}
