package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.data.GroupCode;
import kr.co.ecommtech.epsi.ui.data.MaterialCode;
import kr.co.ecommtech.epsi.ui.data.TypeCode;

public class CodeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Object> mCodeList;
    private OnListItemSelectedListener mListener;

    public CodeListAdapter(Context context, OnListItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(ArrayList<Object> codeList) {
        mCodeList = codeList;
    }

    public interface OnListItemSelectedListener {
        void onItemSelected(View v, Object selectedObject);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CodeListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_code, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (mCodeList == null) {
            return;
        }

        String displayTitle = "";
        Object codeItem = mCodeList.get(position);

        if (codeItem instanceof GroupCode) {
            displayTitle = ((GroupCode)codeItem).getGroupName();
        } else if (codeItem instanceof TypeCode) {
            displayTitle = ((TypeCode)codeItem).getTypeName();
        } else if (codeItem instanceof MaterialCode) {
            displayTitle = ((MaterialCode)codeItem).getMaterialName();
        }

        CodeListAdapter.ViewHolder viewHolder = (CodeListAdapter.ViewHolder)holder;
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
        @BindView(R.id.item_title_layout)
        LinearLayout mItemLayout;

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
                    mListener.onItemSelected(v, mCodeList.get(selectedCarIndex));
                }
            });
        }
    }
}
