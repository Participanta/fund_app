package cn.hukecn.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        try {
            val json = JSONObject()
            json.put("method", oldRequest.method())
            json.put("url", oldRequest.url())
            json.put("header", oldRequest.headers())
            val body = oldRequest.body()
            if (body != null) {
                val buffer = Buffer()
                body.writeTo(buffer)
                var charset = Charset.forName("UTF-8")
                val contentType = body.contentType()
                if (contentType != null) {
                    charset = contentType.charset(charset)
                }
                val paramsStr = buffer.readString(charset)
                json.put("body", paramsStr)
            }
            Log.i(TAG, "intercept: $json")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBuilder = oldRequest.newBuilder()
        if (oldRequest.body() is FormBody) {
            val newFormBody = FormBody.Builder()
            val oldFormBody = oldRequest.body() as FormBody
            for (i in 0 until oldFormBody.size()) {
                newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i))
            }
            requestBuilder.method(oldRequest.method(), newFormBody.build())
        }
        val newRequest = requestBuilder.build()
        val response = chain.proceed(newRequest)
        val mediaType = response.body().contentType()
        val body = response.body().string()
        try {
            val json = JSONObject()
            json.put("method", response.request().method())
            json.put("url", response.request().url())
            json.put("code", response.code())
            json.put("headers", response.headers())
            try {
                val jsonBody = JSONObject(body)
                json.put("body", jsonBody)
            } catch (e: JSONException) {
                e.printStackTrace()
                json.put("body", body)
            }
            Log.i(TAG, "intercept: $json")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return response.newBuilder().body(ResponseBody.create(mediaType, body)).build()
    }

    companion object {
        private const val TAG = "LoggingInterceptor"
    }
}