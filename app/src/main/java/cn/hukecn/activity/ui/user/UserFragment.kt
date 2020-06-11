package cn.hukecn.activity.ui.user

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.hukecn.base.BaseFragment
import cn.hukecn.fund.R

class UserFragment : BaseFragment() {

    private lateinit var notificationsViewModel: UserViewModel

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): UserFragment {
            val fragment = UserFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_user

    override fun initView() {
        notificationsViewModel =
                ViewModelProviders.of(this).get(UserViewModel::class.java)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
        })
    }

    override fun lazyLoad() {

    }
}