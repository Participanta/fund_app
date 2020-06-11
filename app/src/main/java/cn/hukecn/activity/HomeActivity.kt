package cn.hukecn.activity

import android.os.Process
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import cn.hukecn.activity.ui.home.HomeFragment
import cn.hukecn.activity.ui.news.NewsFragment
import cn.hukecn.activity.ui.user.UserFragment
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.fund.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaeger.library.StatusBarUtil

class HomeActivity : AppBaseActivity() {
    private var current: Long = 0

    private var mHomeFragment: HomeFragment? = null
    private var mNewsFragment: NewsFragment? = null
    private var mUserFragment: UserFragment? = null

    //默认为0
    private var mIndex = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_news -> {
                switchFragment(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_user -> {
                switchFragment(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setColor(this,resources.getColor(R.color.actionbar_bac),0)
    }

    override fun initView() {
        super.initView()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        switchFragment(mIndex)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    /**
     * 切换Fragment
     * @param position 下标
     */
    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
            0 // 首页
            -> mHomeFragment?.let {
                transaction.show(it)
            } ?: HomeFragment.getInstance("").let {
                mHomeFragment = it
                transaction.add(R.id.flyt_container, it, "home")
            }
            1  //资讯
            -> mNewsFragment?.let {
                transaction.show(it)
            } ?: NewsFragment.getInstance("").let {
                mNewsFragment = it
                transaction.add(R.id.flyt_container, it, "discovery") }
            2  //我的
            -> mUserFragment?.let {
                transaction.show(it)
            } ?: UserFragment.getInstance("").let {
                mUserFragment = it
                transaction.add(R.id.flyt_container, it, "hot") }
        }

        mIndex = position
        transaction.commitAllowingStateLoss()
    }

    /**
     * 隐藏所有的Fragment
     * @param transaction transaction
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
        mNewsFragment?.let { transaction.hide(it) }
        mUserFragment?.let { transaction.hide(it) }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - current > 2000) {
            current = System.currentTimeMillis()
            Toast.makeText(applicationContext, "再按一次退出程序", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
            Process.killProcess(Process.myPid())
        }
    }
}