package cn.hukecn.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class HttpCommonInterceptor : Interceptor {
    private val mHeaderParamsMap: MutableMap<String, String> = HashMap()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        // 新的请求
        val requestBuilder = oldRequest.newBuilder()
        requestBuilder.method(oldRequest.method(), oldRequest.body())

        //添加公共参数,添加到header中
        if (mHeaderParamsMap.size > 0) {
            for ((key, value) in mHeaderParamsMap) {
                requestBuilder.header(key, value)
            }
        }
        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }

    class Builder {
        var mHttpCommonInterceptor: HttpCommonInterceptor
        fun addHeaderParams(key: String, value: String): Builder {
            mHttpCommonInterceptor.mHeaderParamsMap[key] = value
            return this
        }

        fun addHeaderParams(key: String, value: Int): Builder {
            return addHeaderParams(key, value.toString())
        }

        fun addHeaderParams(key: String, value: Float): Builder {
            return addHeaderParams(key, value.toString())
        }

        fun addHeaderParams(key: String, value: Long): Builder {
            return addHeaderParams(key, value.toString())
        }

        fun addHeaderParams(key: String, value: Double): Builder {
            return addHeaderParams(key, value.toString())
        }

        fun build(): HttpCommonInterceptor {
            return mHttpCommonInterceptor
        }

        init {
            mHttpCommonInterceptor = HttpCommonInterceptor()
        }
    }
}