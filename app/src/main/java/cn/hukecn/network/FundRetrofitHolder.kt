package cn.hukecn.network

import cn.hukecn.base.ApiConfig

object FundRetrofitHolder {
    var retrofit: RetrofitServiceManager? = null

    init {
        val interceptorBuilder = HttpCommonInterceptor.Builder()
                .addHeaderParams("Content-Type", "application/json;charset=UTF-8")
        retrofit = RetrofitServiceManager.newInstance(ApiConfig.BASE_URL, interceptorBuilder.build())
    }
}