package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.component.SectionImageView;

public class CrossSectionFragment extends Fragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.section_image)
    SectionImageView mSectionImage;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cross, container, false);
        ButterKnife.bind(this, rootView);

        mSectionImage.setText("5.33m", "1.23m");

        return rootView;
    }
}
