package kr.co.ecommtech.epsi.ui.data;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AddressResponse {
    @SerializedName("status")
    String status;

    @SerializedName("meta")
    Meta meta;

    @SerializedName("addresses")
    List<Address> addresses;

    public AddressResponse(String status, Meta meta, List<Address> addresses) {
        this.status = status;
        this.meta = meta;
        this.addresses = addresses;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    @Override
    public String toString() {
        return "AddressResponse{" +
                "status='" + status + '\'' +
                ", meta=" + meta +
                ", addresses=" + addresses +
                '}';
    }

    private class Meta {
        @SerializedName("totalCount")
        int totalCount;

        @SerializedName("page")
        int page;

        @SerializedName("count")
        int count;

        public Meta(int totalCount, int page, int count) {
            this.totalCount = totalCount;
            this.page = page;
            this.count = count;
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "totalCount=" + totalCount +
                    ", page=" + page +
                    ", count=" + count +
                    '}';
        }
    }
}
