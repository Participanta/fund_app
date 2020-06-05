package cn.hukecn.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.jaeger.library.StatusBarUtil

/**
 * 所有Activity的基类
 */
abstract class AppBaseActivity : AppCompatActivity() {
    private var mUnBinder: Unbinder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayoutId() > 0) {
            setContentView(getLayoutId())
        }
        mUnBinder = ButterKnife.bind(this, this)
        StatusBarUtil.setTransparent(this)
        initView()
    }

    override fun onResume() {
        super.onResume()
    }

    open fun getLayoutId(): Int = 0

    open fun initView() {}
    override fun onDestroy() {
        super.onDestroy()
        mUnBinder!!.unbind()
    }

    companion object {
        val TAG = AppBaseActivity::class.java.simpleName
    }
}