package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.data.Address;
import kr.co.ecommtech.epsi.ui.data.AddressResponse;

public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Address> mAddressList;
    private OnAddressItemSelectedListener mListener;

    public SearchListAdapter(Context context, OnAddressItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(AddressResponse response) {
        mAddressList = new ArrayList<Address>();
        mAddressList.addAll(response.getAddresses());
    }

    public interface OnAddressItemSelectedListener {
        void onAddressItemSelected(View v, Address selectedAddress);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mAddressList == null) {
            return;
        }

        Address address = mAddressList.get(position);

        SearchListAdapter.ViewHolder viewHolder = (SearchListAdapter.ViewHolder)holder;
        viewHolder.mAddressJibun.setText(address.getJibunAddress());
        viewHolder.mAddressRoad.setText(address.getRoadAddress());
        viewHolder.mItemLayout.setBackgroundColor((position % 2 == 1) ? 0xFFF4F4F4 : 0xFFFFFFFF);
    }

    @Override
    public int getItemCount() {
        if (mAddressList == null) {
            return 0;
        }

        return mAddressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.item_title_layout)
        LinearLayout mItemLayout;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_address_jibun)
        TextView mAddressJibun;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_address_road)
        TextView mAddressRoad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rootView = itemView;
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedCarIndex = getAdapterPosition();
                    mListener.onAddressItemSelected(v, mAddressList.get(selectedCarIndex));
                }
            });
        }
    }
}
