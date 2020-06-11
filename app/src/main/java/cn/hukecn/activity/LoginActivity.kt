package cn.hukecn.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.fund.R
import cn.hukecn.utils.Preference
import kotlinx.android.synthetic.main.activity_login.*
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : AppBaseActivity(), EasyPermissions.PermissionCallbacks {

    private var isLogin by Preference("login",false)
    private var phoneNum by Preference("phone","")

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        super.initView()
        tv_login.setOnClickListener{
            phoneNum = edit_phone.text.toString().trim()
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            isLogin = true
        }

        tv_skip.setOnClickListener{
            toMainActivity()
        }
        checkPermission()
        setPhoneNumber()
        Preference("login",true)
    }

    /**
     * 6.0以下版本(系统自动申请) 不会弹框
     * 有些厂商修改了6.0系统申请机制，他们修改成系统自动申请权限了
     */
    private fun checkPermission(){
        val perms = arrayOf(Manifest.permission.READ_PHONE_STATE)
        EasyPermissions.requestPermissions(this, "应用需要以下权限，请允许", 0, *perms)

    }

    fun setPhoneNumber(){
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val te1 = tm.line1Number //获取本机号码
        if(te1 == null){
            edit_phone.isEnabled = true
            edit_phone.setBackgroundResource(R.drawable.shape_login_edit_bac)
        }else{
            edit_phone.isEnabled = false
            edit_phone.setText(te1)
        }
    }

    fun toMainActivity(){
        val intent = Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(intent)
        isLogin = false
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 0) {
            if (perms.isNotEmpty()) {
                if (perms.contains(Manifest.permission.READ_PHONE_STATE)) {
                    setPhoneNumber()
                }
            }
        }
    }
}