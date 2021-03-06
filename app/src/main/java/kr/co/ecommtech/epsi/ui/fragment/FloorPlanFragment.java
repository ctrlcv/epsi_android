package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import kr.co.ecommtech.epsi.ui.component.FloorImageView;
import kr.co.ecommtech.epsi.ui.services.NfcService;
import kr.co.ecommtech.epsi.ui.utils.CommUtils;

public class FloorPlanFragment extends Fragment {
    private static final String TAG = "FloorPlanFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.floor_image)
    FloorImageView mFloorImage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_data_layout)
    RelativeLayout mNoDataLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_type)
    TextView mPipeTypeTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_set_position)
    TextView mPipeSetPositionTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_direction)
    TextView mPipeDistanceDirectionTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance)
    TextView mPipeDistanceTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_direction_title)
    TextView mPipeDistanceDirectionTitleTv;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_pipe_distance_title)
    TextView mPipeDistanceTitleTv;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_floor, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadValues();
    }

    public void loadValues() {
        mFloorImage.setText("","","");

        if (TextUtils.isEmpty(NfcService.getInstance().getSetPosition()) &&
            TextUtils.isEmpty(NfcService.getInstance().getPipeTypeName()) &&
            TextUtils.isEmpty(NfcService.getInstance().getDistanceDirection())) {
            mFloorImage.setText("","","");

            mPipeTypeTv.setText("");
            mPipeSetPositionTv.setText("");
            mPipeDistanceDirectionTv.setText("");
            mPipeDistanceTv.setText("");

            mFloorImage.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.VISIBLE);
            return;
        }

        mFloorImage.setVisibility(View.VISIBLE);
        mNoDataLayout.setVisibility(View.GONE);

        mFloorImage.setImageResource(CommUtils.getImageResourceId(
                NfcService.getInstance().getSetPosition(),
                NfcService.getInstance().getPipeTypeName(),
                NfcService.getInstance().getDistanceDirection()));

        mPipeTypeTv.setText(NfcService.getInstance().getPipeTypeName());
        mPipeSetPositionTv.setText(NfcService.getInstance().getSetPosition());

        if (TextUtils.isEmpty(NfcService.getInstance().getSetPosition()) ||
            (!TextUtils.isEmpty(NfcService.getInstance().getSetPosition()) && NfcService.getInstance().getSetPosition().equals("?????????"))) {
            mPipeDistanceDirectionTitleTv.setVisibility(View.GONE);
            mPipeDistanceDirectionTv.setVisibility(View.GONE);
            mPipeDistanceTitleTv.setVisibility(View.GONE);
            mPipeDistanceTv.setVisibility(View.GONE);
        } else {
            mPipeDistanceDirectionTitleTv.setVisibility(View.VISIBLE);
            mPipeDistanceDirectionTv.setVisibility(View.VISIBLE);
            mPipeDistanceTitleTv.setVisibility(View.VISIBLE);
            mPipeDistanceTv.setVisibility(View.VISIBLE);
            mPipeDistanceDirectionTv.setText(NfcService.getInstance().getDistanceDirection());

            if (!TextUtils.isEmpty(NfcService.getInstance().getDistanceDirection()) && NfcService.getInstance().getDistanceDirection().equals("CENTER")) {
                mPipeDistanceTv.setText("??? " + String.valueOf(NfcService.getInstance().getDistance()) + "m");
                mFloorImage.setText("", String.valueOf(NfcService.getInstance().getDistance()) + "m", "");
            } else {
                String pipeDistanceStr = "??? " + String.valueOf(NfcService.getInstance().getDistance()) + "m  ";

                if (NfcService.getInstance().getDistanceDirection().equals("LEFT")) {
                    pipeDistanceStr = pipeDistanceStr + "??? ";
                    mFloorImage.setText(String.valueOf(NfcService.getInstance().getDistanceLR()) + "m", String.valueOf(NfcService.getInstance().getDistance()) + "m", "");
                } else {
                    pipeDistanceStr = pipeDistanceStr + "??? ";
                    mFloorImage.setText("", String.valueOf(NfcService.getInstance().getDistance()) + "m", String.valueOf(NfcService.getInstance().getDistanceLR()) + "m");
                }

                pipeDistanceStr = pipeDistanceStr + String.valueOf(NfcService.getInstance().getDistanceLR()) + "m";

                mPipeDistanceTv.setText(pipeDistanceStr);
            }
        }
    }
}
