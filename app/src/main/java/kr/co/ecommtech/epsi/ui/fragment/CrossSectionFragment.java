package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.component.SectionImageView;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.utils.CommUtils;

public class CrossSectionFragment extends Fragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.section_image)
    SectionImageView mSectionImage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_data_layout)
    RelativeLayout mNoDataLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_diameter)
    TextView mPipeDiameterTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_depth)
    TextView mPipeDepthTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_material)
    TextView mPipeMaterialTv;

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadValues();
    }

    public void loadValues() {
        if (TextUtils.isEmpty(NfcService.getInstance().getSetPosition())) {
            mSectionImage.setText("","");
            mPipeDiameterTv.setText("");
            mPipeDepthTv.setText("");
            mPipeMaterialTv.setText("");
            mSectionImage.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.VISIBLE);
            return;
        }

        mNoDataLayout.setVisibility(View.GONE);
        mSectionImage.setVisibility(View.VISIBLE);

        mPipeDiameterTv.setText(NfcService.getInstance().getDiameter() + " mm");
        mPipeDepthTv.setText(NfcService.getInstance().getPipeDepth() + " m");
        mPipeMaterialTv.setText(NfcService.getInstance().getMaterialName());

        if (TextUtils.isEmpty(NfcService.getInstance().getSetPosition()) ||
            (!TextUtils.isEmpty(NfcService.getInstance().getSetPosition()) && NfcService.getInstance().getSetPosition().equals("관로위"))) {
            mSectionImage.setImageResource(R.drawable.section);
            mSectionImage.setText(NfcService.getInstance().getPipeDepth() + "m", "");
        } else {
            mSectionImage.setImageResource(R.drawable.section_sepa);
            mSectionImage.setText(NfcService.getInstance().getPipeDepth() + "m", String.valueOf(NfcService.getInstance().getDistance()) + "m");
        }
    }
}
