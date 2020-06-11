package cn.hukecn.activity.ui.news

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.hukecn.adapter.NewsDetailAdapter
import cn.hukecn.base.BaseFragment
import cn.hukecn.bean.NewsDetail
import cn.hukecn.fund.R
import cn.hukecn.network.NewsService
import cn.hukecn.network.RetrofitServiceManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class NewsFragment : BaseFragment() {

    private lateinit var dashboardViewModel: NewsViewModel
    private var newsDetailAdapter : NewsDetailAdapter? = null
    private var datas :MutableList<NewsDetail.ItemBean> = ArrayList()

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): NewsFragment {
            val fragment = NewsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_news


    override fun initView() {
        dashboardViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        initRecyclerView()
        mRefreshLayout.setOnRefreshListener { getFundNews() }
    }

    override fun lazyLoad() {
        getFundNews()
    }



    private fun initRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(mContext)
        newsDetailAdapter = NewsDetailAdapter(datas, mContext)
        recyclerview.adapter = newsDetailAdapter
        newsDetailAdapter!!.setOnItemChildClickListener { adapter, view, position ->


        }
    }

    private fun getFundNews(){
        RetrofitServiceManager.newInstance("http://api.iclient.ifeng.com/")
                .create(NewsService::class.java)
                .getFundNews("CJ33,FOCUSCJ33,HNCJ33")
                ?.subscribeOn(Schedulers.io())
                ?.doFinally{mRefreshLayout.finishRefresh()}
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ data ->
                    datas.clear()
                    datas.addAll(data[0].item)
                    newsDetailAdapter!!.addData(datas)
                })
                {
                    Log.i("8888",it.message)

                }
    }
}