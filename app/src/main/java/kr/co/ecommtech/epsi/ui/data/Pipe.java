package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class Pipe {
    @SerializedName("pipeid")
    int pipeId;

    @SerializedName("pipegroup")
    String pipeGroup;

    @SerializedName("pipegroupname")
    String pipeGroupName;

    @SerializedName("pipegroupcolor")
    String pipeGroupColor;

    @SerializedName("pipetype")
    String pipeType;

    @SerializedName("pipetypename")
    String pipeTypeName;

    @SerializedName("diameter")
    double diameter;

    @SerializedName("material")
    String material;

    @SerializedName("materialname")
    String materialName;

    @SerializedName("distance")
    double distance;

    @SerializedName("pipedepth")
    double pipeDepth;

    @SerializedName("positionx")
    double positionX;

    @SerializedName("positiony")
    double positionY;

    @SerializedName("offercompany")
    String offerCompany;

    @SerializedName("companyphone")
    String companyPhone;

    @SerializedName("memo")
    String memo;

    @SerializedName("buildcompany")
    String buildCompany;

    @SerializedName("buildphone")
    String buildPhone;

    @SerializedName("siteimageurl")
    String siteImageUrl;

    @SerializedName("locdistance")
    double locDistance;

    public Pipe(int pipeId, String pipeGroup, String pipeGroupName, String pipeGroupColor, String pipeType, String pipeTypeName, double diameter, String material, String materialName, double distance, double pipeDepth, double positionX, double positionY, String offerCompany, String companyPhone, String memo, String buildCompany, String buildPhone, String siteImageUrl, double locDistance) {
        this.pipeId = pipeId;
        this.pipeGroup = pipeGroup;
        this.pipeGroupName = pipeGroupName;
        this.pipeGroupColor = pipeGroupColor;
        this.pipeType = pipeType;
        this.pipeTypeName = pipeTypeName;
        this.diameter = diameter;
        this.material = material;
        this.materialName = materialName;
        this.distance = distance;
        this.pipeDepth = pipeDepth;
        this.positionX = positionX;
        this.positionY = positionY;
        this.offerCompany = offerCompany;
        this.companyPhone = companyPhone;
        this.memo = memo;
        this.buildCompany = buildCompany;
        this.buildPhone = buildPhone;
        this.siteImageUrl = siteImageUrl;
        this.locDistance = locDistance;
    }

    public int getPipeId() {
        return pipeId;
    }

    public void setPipeId(int pipeId) {
        this.pipeId = pipeId;
    }

    public String getPipeGroup() {
        return pipeGroup;
    }

    public void setPipeGroup(String pipeGroup) {
        this.pipeGroup = pipeGroup;
    }

    public String getPipeGroupName() {
        return pipeGroupName;
    }

    public void setPipeGroupName(String pipeGroupName) {
        this.pipeGroupName = pipeGroupName;
    }

    public String getPipeGroupColor() {
        return pipeGroupColor;
    }

    public void setPipeGroupColor(String pipeGroupColor) {
        this.pipeGroupColor = pipeGroupColor;
    }

    public String getPipeType() {
        return pipeType;
    }

    public void setPipeType(String pipeType) {
        this.pipeType = pipeType;
    }

    public String getPipeTypeName() {
        return pipeTypeName;
    }

    public void setPipeTypeName(String pipeTypeName) {
        this.pipeTypeName = pipeTypeName;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPipeDepth() {
        return pipeDepth;
    }

    public void setPipeDepth(double pipeDepth) {
        this.pipeDepth = pipeDepth;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getOfferCompany() {
        return offerCompany;
    }

    public void setOfferCompany(String offerCompany) {
        this.offerCompany = offerCompany;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getBuildCompany() {
        return buildCompany;
    }

    public void setBuildCompany(String buildCompany) {
        this.buildCompany = buildCompany;
    }

    public String getBuildPhone() {
        return buildPhone;
    }

    public void setBuildPhone(String buildPhone) {
        this.buildPhone = buildPhone;
    }

    public String getSiteImageUrl() {
        return siteImageUrl;
    }

    public void setSiteImageUrl(String siteImageUrl) {
        this.siteImageUrl = siteImageUrl;
    }

    public double getLocDistance() {
        return locDistance;
    }

    public void setLocDistance(double locDistance) {
        this.locDistance = locDistance;
    }
}
