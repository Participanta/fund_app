package cn.hukecn

import android.app.Application
import androidx.multidex.MultiDexApplication
import cn.hukecn.utils.Preference
import kotlin.properties.Delegates

class MyApplication : MultiDexApplication() {
    private var phoneNum by Preference("phone","")


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MyApplication by Delegates.notNull()
            private set
    }

    fun getPhoneNumber():String = phoneNum
}