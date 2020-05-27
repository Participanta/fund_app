package cn.hukecn.network;

import cn.hukecn.base.ApiConfig;

public class FundRetrofitHolder {

    private static RetrofitServiceManager instance;

    static {
        HttpCommonInterceptor.Builder interceptorBuilder = new HttpCommonInterceptor.Builder()
                .addHeaderParams("Content-Type", "application/json;charset=UTF-8");
        instance = RetrofitServiceManager.newInstance(ApiConfig.BASE_URL, interceptorBuilder.build());
    }

    public static RetrofitServiceManager getRetrofit(){
        return instance;
    }



}
