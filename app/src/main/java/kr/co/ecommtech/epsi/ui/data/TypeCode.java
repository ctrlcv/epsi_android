package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class TypeCode {
    @SerializedName("typeid")
    int typeId;

    @SerializedName("typecd")
    String typeCd;

    @SerializedName("typename")
    String typeName;

    public TypeCode(int typeId, String typeCd, String typeName) {
        this.typeId = typeId;
        this.typeCd = typeCd;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
