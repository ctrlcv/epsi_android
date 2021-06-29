package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TypeCodeList {
    @SerializedName("success")
    public boolean success;

    @SerializedName("result")
    public List<TypeCode> typeCodeList;

    public TypeCodeList(boolean success, List<TypeCode> typeCodeList) {
        this.success = success;
        this.typeCodeList = typeCodeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<TypeCode> getTypeCodeList() {
        return typeCodeList;
    }

    public void setTypeCodeList(List<TypeCode> typeCodeList) {
        this.typeCodeList = typeCodeList;
    }
}
