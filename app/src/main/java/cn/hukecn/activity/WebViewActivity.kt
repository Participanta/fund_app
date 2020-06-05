package cn.hukecn.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hukecn.fund.R

class WebViewActivity : AppCompatActivity() {
    var view: WebView? = null
    var bar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        initView()
    }

    private fun initView() {
        val intent = intent
        val fundid = intent.getStringExtra("fundid")
        val fundname = intent.getStringExtra("fundname")
        if (fundid == null || fundname == null) {
            Toast.makeText(applicationContext, "System Error...", Toast.LENGTH_LONG).show()
            return
        }
        val tv_title = findViewById<View>(R.id.title) as TextView
        tv_title.text = "$fundname($fundid)"
        view = findViewById<View>(R.id.webview) as WebView
        bar = findViewById<View>(R.id.progressBar) as ProgressBar
        bar!!.progress = 0
        view!!.loadUrl("http://m.1234567.com.cn/m/fund/funddetail/#fundcode=$fundid")
        view!!.settings.javaScriptEnabled = true
        view!!.webViewClient = webViewClient
        view!!.webChromeClient = webChromeClient
    }

    var webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
    var webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress != 100) bar!!.progress = newProgress else bar!!.visibility = View.GONE
            super.onProgressChanged(view, newProgress)
        }
    }
}