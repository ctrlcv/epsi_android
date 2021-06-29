package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

public class GroupCode {
    @SerializedName("groupid")
    int groupId;

    @SerializedName("groupcd")
    String groupCd;

    @SerializedName("groupname")
    String groupName;

    @SerializedName("groupcolor")
    String groupColor;

    public GroupCode(int groupId, String groupCd, String groupName, String groupColor) {
        this.groupId = groupId;
        this.groupCd = groupCd;
        this.groupName = groupName;
        this.groupColor = groupColor;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupCd() {
        return groupCd;
    }

    public void setGroupCd(String groupCd) {
        this.groupCd = groupCd;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public void setGroupColor(String groupColor) {
        this.groupColor = groupColor;
    }
}
