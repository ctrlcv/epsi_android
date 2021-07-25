package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.data.TypeCode;
import kr.co.ecommtech.epsi.ui.services.DistanceDirection;

public class DirectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mCodeList;
    private OnDirectionItemSelectedListener mListener;
    private String mPipeType = "";
    private String mSetPosition = "";
    private String mDistanceDirection = "";

    public DirectionListAdapter(Context context, OnDirectionItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(ArrayList<Object> codeList, String pipeType, String setPosition, String distanceDirection) {
        mCodeList = codeList;

        mPipeType = pipeType;
        mSetPosition = setPosition;
        mDistanceDirection = distanceDirection;
    }

    public interface OnDirectionItemSelectedListener {
        void onDirectionItemSelected(View v, Object selectedObject);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new DirectionListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_type, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (mCodeList == null) {
            return;
        }

        String displayTitle = "";

        Object codeItem = mCodeList.get(position);
        DirectionListAdapter.ViewHolder viewHolder = (DirectionListAdapter.ViewHolder) holder;

        if (codeItem instanceof TypeCode) {
            displayTitle = ((TypeCode)codeItem).getTypeName();
            viewHolder.mItemImage.setImageResource(getImageResourceId(mSetPosition, displayTitle, mDistanceDirection));

        } else if (codeItem instanceof String) {
            displayTitle = (String)codeItem;
            viewHolder.mItemImage.setImageResource(getImageResourceId(displayTitle, mPipeType, mDistanceDirection));

        } else if (codeItem instanceof DistanceDirection) {
            if ((DistanceDirection)codeItem == DistanceDirection.EL_DIRECTION_CENTER) {
                displayTitle = "CENTER";
            } else if ((DistanceDirection)codeItem == DistanceDirection.EL_DIRECTION_LEFT) {
                displayTitle = "LEFT";
            } else if ((DistanceDirection)codeItem == DistanceDirection.EL_DIRECTION_RIGHT) {
                displayTitle = "RIGHT";
            }
            viewHolder.mItemImage.setImageResource(getImageResourceId(mSetPosition, mPipeType, displayTitle));
        }

        viewHolder.mItemTitle.setText(displayTitle);
        viewHolder.mItemLayout.setBackgroundColor((position % 2 == 1) ? 0xFFF4F4F4 : 0xFFFFFFFF);
    }

    public int getImageResourceId(String setPosition, String pipeType, String distanceDirection) {
        if (!TextUtils.isEmpty(setPosition) && setPosition.equals("경계석")) {
            if (!TextUtils.isEmpty(pipeType)) {
                switch (pipeType) {
                    case "진직형":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.d_type_0_center;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.d_type_0_center;
                        } else {
                            return R.drawable.d_type_0_center;
                        }

                    case "T분기형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_0_right;
                        } else {
                            return R.drawable.t_type_0_center;
                        }

                    case "T분기형(90°)":
                    default:
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_90_right;
                        } else {
                            return R.drawable.t_type_90_center;
                        }

                    case "T분기형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_180_right;
                        } else {
                            return R.drawable.t_type_180_center;
                        }

                    case "T분기형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_270_right;
                        } else {
                            return R.drawable.t_type_270_center;
                        }

                    case "엘보형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_0_right;
                        } else {
                            return R.drawable.l_type_0_center;
                        }

                    case "엘보형(90°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_90_right;
                        } else {
                            return R.drawable.l_type_90_center;
                        }

                    case "엘보형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_180_right;
                        } else {
                            return R.drawable.l_type_180_center;
                        }

                    case "엘보형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_270_right;
                        } else {
                            return R.drawable.l_type_270_center;
                        }

                    case "관말형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_0_right;
                        } else {
                            return R.drawable.e_type_0_center;
                        }

                    case "관말형(90°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_90_right;
                        } else {
                            return R.drawable.e_type_90_center;
                        }

                    case "관말형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_180_right;
                        } else {
                            return R.drawable.e_type_180_center;
                        }

                    case "관말형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_270_right;
                        } else {
                            return R.drawable.e_type_270_center;
                        }
                }
            } else {
                if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                    return R.drawable.l_type_0_left;
                } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                    return R.drawable.l_type_0_right;
                } else {
                    return R.drawable.l_type_0_center;
                }
            }
        } else {
            if (!TextUtils.isEmpty(pipeType)) {
                switch (pipeType) {
                    case "진직형":
                        return R.drawable.d_type_0;

                    case "T분기형(0°)":
                    default:
                        return R.drawable.t_type_0;

                    case "T분기형(90°)":
                        return R.drawable.t_type_90;

                    case "T분기형(180°)":
                        return R.drawable.t_type_180;

                    case "T분기형(270°)":
                        return R.drawable.t_type_270;

                    case "엘보형(0°)":
                        return R.drawable.l_type_0;

                    case "엘보형(90°)":
                        return R.drawable.l_type_90;

                    case "엘보형(180°)":
                        return R.drawable.l_type_180;

                    case "엘보형(270°)":
                        return R.drawable.l_type_270;

                    case "관말형(0°)":
                        return R.drawable.e_type_0;

                    case "관말형(90°)":
                        return R.drawable.e_type_90;

                    case "관말형(180°)":
                        return R.drawable.e_type_180;

                    case "관말형(270°)":
                        return R.drawable.e_type_270;
                }
            } else {
                return R.drawable.l_type_0;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mCodeList == null) {
            return 0;
        }

        return mCodeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.item_image)
        ImageView mItemImage;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.item_title_layout)
        RelativeLayout mItemLayout;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_item_title)
        TextView mItemTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rootView = itemView;
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedCarIndex = getAdapterPosition();
                    mListener.onDirectionItemSelected(v, mCodeList.get(selectedCarIndex));
                }
            });
        }
    }
}
