package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class MaterialCode {
    @SerializedName("materialid")
    int materialId;

    @SerializedName("materialcd")
    String materialCd;

    @SerializedName("materialname")
    String materialName;

    public MaterialCode(int materialId, String materialCd, String materialName) {
        this.materialId = materialId;
        this.materialCd = materialCd;
        this.materialName = materialName;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCd() {
        return materialCd;
    }

    public void setMaterialCd(String materialCd) {
        this.materialCd = materialCd;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
