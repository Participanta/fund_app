package cn.hukecn.fund

import android.os.Bundle
import android.os.Handler
import android.os.Message
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by Kelson on 2015/12/5.
 */
class AsyncHttp(url: String?, listener: HttpListener?) : Runnable {
    var COUNT = 0
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            listener?.onHttpCallBack(msg.data.getInt("code")
                    , msg.data.getString("content"))
        }
    }
    var strUrl: String? = null
    var listener: HttpListener? = null
    private fun HttpGet() {
        try {
            COUNT++
            val url = URL(strUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 1000
            conn.doInput = true
            val `in` = InputStreamReader(conn.inputStream, "gbk")
            val buffer = BufferedReader(`in`)
            var inputLine: String? = null
            var result: String? = ""
            while (buffer.readLine().also { inputLine = it } != null) {
                result += inputLine
            }
            if (result!!.length > 0) {
                val msg = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("content", result)
                bundle.putInt("code", conn.responseCode)
                msg.data = bundle
                handler.sendMessage(msg)
            }
            `in`.close()
            conn.disconnect()
            if (result.length <= 0 && COUNT < 3) HttpGet()
        } catch (e: MalformedURLException) {
            if (COUNT < 3) HttpGet()
        } catch (e: IOException) {
            if (COUNT < 3) HttpGet()
        }
    }

    override fun run() {
        HttpGet()
    }

    interface HttpListener {
        fun onHttpCallBack(statusCode: Int, content: String?)
    }

    init {
        strUrl = url
        this.listener = listener
    }
}