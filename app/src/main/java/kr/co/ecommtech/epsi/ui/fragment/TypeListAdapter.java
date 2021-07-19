package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class TypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mCodeList;
    private OnTypeItemSelectedListener mListener;
    private String mPipeType = "";
    private String mSetPosition = "";
    private String mDistanceDirection = "";

    public TypeListAdapter(Context context, OnTypeItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(ArrayList<Object> codeList, String pipeType, String setPosition, String distanceDirection) {
        mCodeList = codeList;

        mPipeType = pipeType;
        mSetPosition = setPosition;
        mDistanceDirection = distanceDirection;
    }

    public interface OnTypeItemSelectedListener {
        void onTypeItemSelected(View v, Object selectedObject);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TypeListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (mCodeList == null) {
            return;
        }

        String displayTitle = "";

        Object codeItem = mCodeList.get(position);
        TypeListAdapter.ViewHolder viewHolder = (TypeListAdapter.ViewHolder) holder;

        if (codeItem instanceof TypeCode) {
            displayTitle = ((TypeCode)codeItem).getTypeName();

            switch (displayTitle) {
                case "진직형":
                    viewHolder.mItemImage.setImageResource(R.drawable.d_type_0);
                    break;

                case "T분기형(0°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.t_type_0);
                    break;

                case "T분기형(90°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.t_type_90);
                    break;

                case "T분기형(180°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.t_type_180);
                    break;

                case "T분기형(270°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.t_type_270);
                    break;

                case "엘보형(0°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.l_type_0);
                    break;

                case "엘보형(90°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.l_type_90);
                    break;

                case "엘보형(180°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.l_type_180);
                    break;

                case "엘보형(270°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.l_type_270);
                    break;

                case "관말형(0°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.e_type_0);
                    break;

                case "관말형(90°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.e_type_90);
                    break;

                case "관말형(180°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.e_type_180);
                    break;

                case "관말형(270°)":
                    viewHolder.mItemImage.setImageResource(R.drawable.e_type_270);
                    break;
            }
        }

        viewHolder.mItemTitle.setText(displayTitle);
        viewHolder.mItemLayout.setBackgroundColor((position % 2 == 1) ? 0xFFF4F4F4 : 0xFFFFFFFF);
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
                    mListener.onTypeItemSelected(v, mCodeList.get(selectedCarIndex));
                }
            });
        }
    }
}
