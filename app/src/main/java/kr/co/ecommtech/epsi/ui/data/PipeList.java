package kr.co.ecommtech.epsi.ui.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PipeList {
    @SerializedName("success")
    public boolean success;

    @SerializedName("result")
    public List<Pipe> pipeList;

    public PipeList(boolean success, List<Pipe> pipeList) {
        this.success = success;
        this.pipeList = pipeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Pipe> getPipeList() {
        return pipeList;
    }

    public void setPipeList(List<Pipe> pipeList) {
        this.pipeList = pipeList;
    }
}
