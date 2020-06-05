package cn.hukecn.fund

import android.content.Context
import cn.hukecn.fund.AsyncHttp.HttpListener
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import org.apache.http.Header
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by Kelson on 2016/1/6.
 */
object MyHttp {
    var client = AsyncHttpClient()
    operator fun get(context: Context?, url: String?, listener: HttpListener?, charset: String?) {
        client.setTimeout(10000)
        client.threadPool = AppThreadPool.instance!!.pool
        client.responseTimeout = 10000
        client[context, url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(i: Int, headers: Array<Header>, bytes: ByteArray) {
                try {
                    if (listener != null && bytes != null) listener.onHttpCallBack(i, String(bytes, Charset.forName(charset)))
                } catch (e: UnsupportedEncodingException) {
                }
            }

            override fun onFailure(i: Int, headers: Array<Header>, bytes: ByteArray, throwable: Throwable) {
                //
            }
        }]
    }
}