package cn.hukecn.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitServiceManager private constructor(baseUrl: String, commonInterceptor: HttpCommonInterceptor) {
    private val mRetrofit: Retrofit

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
    </T> */
    fun <T> create(service: Class<T>?): T {
        return mRetrofit.create(service)
    }

    companion object {
        //超时时间5s
        private const val DEFAULT_TIME_OUT = 5
        private const val DEFAULT_READ_TIME_OUT = 10
        private var loggingInterceptor: LoggingInterceptor? = null

        /**
         * 获取RetrofitServiceManager
         *
         * @return
         */
        fun newInstance(baseUrl: String, commonInterceptor: HttpCommonInterceptor): RetrofitServiceManager {
            return RetrofitServiceManager(baseUrl, commonInterceptor)
        }

        /**
         * 获取RetrofitServiceManager
         *
         * @return
         */
        fun newInstance(baseUrl: String): RetrofitServiceManager {
            val interceptorBuilder = HttpCommonInterceptor.Builder()
                    .addHeaderParams("Content-Type", "application/json ")
            return newInstance(baseUrl, interceptorBuilder.build())
        }
    }

    init {
        //创建OKHttpClient
        val builder = OkHttpClient.Builder()
        //连接超时时间
        builder.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
        //写操作,超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
        //读操作,超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
        builder.addInterceptor(commonInterceptor)
        if (loggingInterceptor == null) {
            loggingInterceptor = LoggingInterceptor()
        }
        builder.addInterceptor(loggingInterceptor)
        // 创建Retrofit
        mRetrofit = Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }
}