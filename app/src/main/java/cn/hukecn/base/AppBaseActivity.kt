package cn.hukecn.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import cn.hukecn.fund.R
import com.jaeger.library.StatusBarUtil
import pub.devrel.easypermissions.EasyPermissions

/**
 * 所有Activity的基类
 */
abstract class AppBaseActivity : AppCompatActivity() {
    private var mUnBinder: Unbinder? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayoutId() > 0) {
            setContentView(getLayoutId())
        }
        mUnBinder = ButterKnife.bind(this, this)
        setStatusBar()
        initView()
    }

    open fun getLayoutId(): Int = 0

    open fun initView() {}
    override fun onDestroy() {
        super.onDestroy()
        mUnBinder!!.unbind()
    }

    open fun showLoadingDialog(){
        if (progressDialog == null) progressDialog = ProgressDialog(this@AppBaseActivity)
        progressDialog?.let {
            if(!it.isShowing)it.show()
        }
    }

    open fun dismissLoadingDialog(){
        progressDialog?.let {
            if(it.isShowing)it.dismiss()
        }
    }

    /**
     * 重写要申请权限的Activity或者Fragment的onRequestPermissionsResult()方法，
     * 在里面调用EasyPermissions.onRequestPermissionsResult()，实现回调。
     *
     * @param requestCode  权限请求的识别码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    protected open fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }


        companion object {
        val TAG = AppBaseActivity::class.java.simpleName
    }
}