package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("roadAddress")
    String roadAddress;

    @SerializedName("jibunAddress")
    String jibunAddress;

    @SerializedName("englishAddress")
    String englishAddress;

    @SerializedName("x")
    String longitude;

    @SerializedName("y")
    String latitude;

    public Address(String roadAddress, String jibunAddress, String englishAddress, String longitude, String latitude) {
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.englishAddress = englishAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getJibunAddress() {
        return jibunAddress;
    }

    public void setJibunAddress(String jibunAddress) {
        this.jibunAddress = jibunAddress;
    }

    public String getEnglishAddress() {
        return englishAddress;
    }

    public void setEnglishAddress(String englishAddress) {
        this.englishAddress = englishAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Address{" +
                "roadAddress='" + roadAddress + '\'' +
                ", jibunAddress='" + jibunAddress + '\'' +
                ", englishAddress='" + englishAddress + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
