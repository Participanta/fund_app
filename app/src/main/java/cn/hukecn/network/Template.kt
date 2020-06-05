package cn.hukecn.network

import com.google.gson.annotations.SerializedName

class Template<T> {
    @SerializedName(value = "resultCode", alternate = ["errno"])
    var errno = 0

    @SerializedName(value = "resultMsg", alternate = ["errmsg"])
    var errmsg: String? = null

    @SerializedName(value = "result", alternate = ["data"])
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }
}