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
import kr.co.ecommtech.epsi.ui.utils.CommUtils;

public class TypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mCodeList;
    private OnTypeItemSelectedListener mListener;
    private String mSetPosition = "";
    private String mDistanceDirection = "";

    public TypeListAdapter(Context context, OnTypeItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(ArrayList<Object> codeList, String setPosition, String distanceDirection) {
        mCodeList = codeList;

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

        displayTitle = ((TypeCode)codeItem).getTypeName();
        viewHolder.mItemImage.setImageResource(CommUtils.getImageResourceId(mSetPosition, displayTitle, mDistanceDirection));

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
