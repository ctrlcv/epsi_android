package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MaterialCodeList {
    @SerializedName("success")
    public boolean success;

    @SerializedName("result")
    public List<MaterialCode> materialCodeList;

    public MaterialCodeList(boolean success, List<MaterialCode> materialCodeList) {
        this.success = success;
        this.materialCodeList = materialCodeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<MaterialCode> getMaterialCodeList() {
        return materialCodeList;
    }

    public void setMaterialCodeList(List<MaterialCode> materialCodeList) {
        this.materialCodeList = materialCodeList;
    }
}
