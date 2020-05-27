package cn.hukecn.network;

import com.google.gson.annotations.SerializedName;

public class Template<T> {

    @SerializedName(value = "resultCode",alternate = {"errno"})
    private int errno;
    @SerializedName(value = "resultMsg",alternate = {"errmsg"})
    private String errmsg;
    @SerializedName(value = "result",alternate = {"data"})
    private T data;

    public int getErrno() {
        return errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public T getData() {
        return data;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
