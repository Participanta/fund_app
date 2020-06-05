package cn.hukecn.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.fund.R
import kotlinx.android.synthetic.main.activity_splash.*
import pub.devrel.easypermissions.EasyPermissions


class SplashActivity : AppBaseActivity(),EasyPermissions.PermissionCallbacks {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    private var alphaAnimation: AlphaAnimation?=null

    @SuppressLint("SetTextI18n")
    override fun initView() {

        //渐变展示启动屏
        alphaAnimation= AlphaAnimation(0.3f, 1.0f)
        alphaAnimation?.duration = 1500
        alphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(arg0: Animation) {
                redirectTo()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {}

        })

        checkPermission()
    }


    fun redirectTo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 6.0以下版本(系统自动申请) 不会弹框
     * 有些厂商修改了6.0系统申请机制，他们修改成系统自动申请权限了
     */
    private fun checkPermission(){
        val perms = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        EasyPermissions.requestPermissions(this, "应用需要以下权限，请允许", 0, *perms)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    fun getPhoneNumber(){
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val deviceid = tm.deviceId //获取智能设备唯一编号

        val te1 = tm.line1Number //获取本机号码

        val imei = tm.simSerialNumber //获得SIM卡的序号

        val imsi = tm.subscriberId //得到用户Id

        Log.i("phoneNum",te1+"")

    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 0) {
            if (perms.isNotEmpty()) {
                if (perms.contains(Manifest.permission.READ_PHONE_STATE)
                        && perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (alphaAnimation != null) {
                        getPhoneNumber()
                        iv_web_icon.startAnimation(alphaAnimation)
                    }
                }
            }
        }
    }
}