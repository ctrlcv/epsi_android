package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupCodeList {
    @SerializedName("success")
    public boolean success;

    @SerializedName("result")
    public List<GroupCode> groupCodeList;

    public GroupCodeList(boolean success, List<GroupCode> groupCodeList) {
        this.success = success;
        this.groupCodeList = groupCodeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<GroupCode> getGroupCodeList() {
        return groupCodeList;
    }

    public void setGroupCodeList(List<GroupCode> groupCodeList) {
        this.groupCodeList = groupCodeList;
    }
}
