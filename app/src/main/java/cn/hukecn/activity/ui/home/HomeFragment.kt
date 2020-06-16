package cn.hukecn.activity.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.InputType
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.hukecn.MyApplication
import cn.hukecn.activity.FundDetailActivity
import cn.hukecn.activity.SearchActivity
import cn.hukecn.adapter.FundListRecyclerViewAdapter
import cn.hukecn.base.BaseFragment
import cn.hukecn.bean.FundBean
import cn.hukecn.bean.IndexValue
import cn.hukecn.fund.R
import cn.hukecn.network.FundRetrofitHolder
import cn.hukecn.network.FundService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.scwang.smartrefresh.header.MaterialHeader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_title_main.*
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : BaseFragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var mTitle: String? = null

    private var income : Double = 0.0
    private var calendar: Calendar? = null
    private var fundListRecyclerViewAdapter: FundListRecyclerViewAdapter? = null
    private var fundBeanList: MutableList<FundBean> = ArrayList()
    private var mMaterialHeader: MaterialHeader? = null
    private var phoneNum : String? = null


    companion object {
        private const val TAG = "HomeFragment"
        fun getInstance(title: String): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home
    override fun initView() {
        calendar = Calendar.getInstance()
        initRecyclerView()

        tv_search.setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
        tv_search.setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java))}
        phoneNum = MyApplication.instance.getPhoneNumber()
        initViewFlipper()
        initRefreshView()
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {

        })
    }


    override fun lazyLoad() {
        getOptionalFundList(phoneNum)
        getIndexInfo()
    }

    private fun initRefreshView() {
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.gray, R.color.actionbar_bac)
        mRefreshLayout.setOnRefreshListener {
            getOptionalFundList(phoneNum)
        }
    }

    private fun initViewFlipper() {
        for(index in 0 until 3){
            val view = layoutInflater.inflate(R.layout.item_flipper,null)
            view_flipper.addView(view)
        }
        view_flipper.setInAnimation(activity,R.anim.anim_in)
        view_flipper.setOutAnimation(activity,R.anim.anim_out)
        view_flipper.setFlipInterval(3500)
        view_flipper.startFlipping()
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        fundListRecyclerViewAdapter = FundListRecyclerViewAdapter(R.layout.fund_list_swipe_item, fundBeanList)
        recyclerview.adapter = fundListRecyclerViewAdapter
        fundListRecyclerViewAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            val id = view.id
            val fundBean = fundBeanList[position]
            if (id == R.id.llyt_root) {
                toFundDetailActivity(fundBean)
            } else if (id == R.id.btn_delete) {
                deleteItem(fundBean)
                fundBeanList.removeAt(position)
                fundListRecyclerViewAdapter!!.notifyItemRemoved(position)
            } else if (id == R.id.tv_money || id == R.id.iv_edit) {
                showEditDialog(fundBean)
            }
        }
        var emptyView = layoutInflater.inflate(R.layout.recycler_empty_view,null,false)
        emptyView.setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java))  }
        fundListRecyclerViewAdapter!!.emptyView = emptyView
    }

    private fun showEditDialog(fundBean: FundBean) {
        val type = InputType.TYPE_CLASS_NUMBER
        mContext?.let {
            MaterialDialog(it).show {
            title(R.string.remind)
            input(waitForPositiveButton = true,hint = "请输入购买金额",inputType = type) { dialog, text ->
                fundBean.money = text.toString().toFloat()
                updateData(fundBean)
                fundListRecyclerViewAdapter!!.notifyDataSetChanged()
                countIncome()
            }
        }
        }
    }

    private fun updateData(fundBean: FundBean) {
        val params: MutableMap<String, Any?> = HashMap()
        params["open_id"] = phoneNum
        params["fund_code"] = fundBean.fundcode
        params["fund_name"] = fundBean.name
        params["fund_money"] = fundBean.money
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.updateOptionalFund(params)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ Toast.makeText(activity, "更新成功", Toast.LENGTH_SHORT).show() }) { }
    }

    private fun deleteItem(fundBean: FundBean) {
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.deleteOptionalFund(phoneNum,fundBean.fundcode)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                    refresh()
                }) { }
    }

    private fun toFundDetailActivity(fundBean: FundBean) {
        val intent = Intent(activity, FundDetailActivity::class.java)
        intent.putExtra("fundid", fundBean.fundcode)
        intent.putExtra("fundname", fundBean.name)
        startActivity(intent)
    }

    private fun updateView(bean: FundBean) {
        val location = isRepeat(bean)
        if (location == -1) {
            //原列表中没有
            fundBeanList.add(bean)
        } else {
            fundBeanList[location].fundcode = bean.fundcode;
            fundBeanList[location].name = bean.name
            fundBeanList[location].gszzl = bean.gszzl
            fundBeanList[location].dwjz = bean.dwjz
            fundBeanList[location].gsz = bean.gsz
            fundBeanList[location].gztime = bean.gztime
            fundBeanList[location].jzrq = bean.jzrq

            //原列表中有
        }
        //        countIncome();
        fundListRecyclerViewAdapter!!.notifyDataSetChanged()
        mRefreshLayout.finishRefresh()
        countIncome()
    }

    private fun refresh() {
        getIndexInfo()
        for (bean in fundBeanList) {
            getFundValuation(bean.fundcode)
        }

        if(fundBeanList.size == 0){
            mRefreshLayout.finishRefresh()
        }

        handler.removeMessages(0)
        handler.sendEmptyMessageDelayed(0, 10000)
    }

    private fun getFundValuation(fund_code: String?) {
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.getFundValuation(fund_code)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally {mRefreshLayout.finishRefresh() }
                ?.subscribe({ listTemplate -> listTemplate.data?.let { updateView(it) } }) { throwable -> Log.i(TAG, "accept: " + throwable.message) }
    }

    private fun getOptionalFundList(openId: String?) {
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.getOptionalFundList(openId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ listTemplate ->
                    val optionalFundItems = listTemplate!!.data
                    if (optionalFundItems != null) {
                        for (optionalFundItem in optionalFundItems) {

                            val fundBean = FundBean()
                            fundBean.fundcode = optionalFundItem.fund_code
                            fundBean.name = optionalFundItem.fund_name
                            fundBean.money = optionalFundItem.fund_money
                            val location = isRepeat(fundBean)
                            if (location == -1) {
                                //原列表中没有
                                fundBeanList.add(fundBean)
                            } else {
                                //原列表中有
                            }
                        }
                    }
                    fundListRecyclerViewAdapter!!.notifyDataSetChanged()
                    refresh()
                }) { }
    }

    private fun countIncome() {
        income = 0.0
        fundBeanList.forEach {
            var this_percent = it.gszzl?.toDouble()?:0.0
            var this_income : Double = (this_percent * it.money).toDouble();
            this_income = (this_income/100.0f).toDouble();
            this_income = (this_income * 10).roundToInt() /10.0;
            it.income = this_income

            income += this_income;
        }

        income =Math.round(income*10)/10.0
        tv_income!!.text = income.toString()

        if(income < 0.00)
        {
            tv_income!!.setTextColor(Color.GREEN)
        }else
        {
            tv_income!!.setTextColor(Color.RED)
        }
    }



    override fun onResume() {
        super.onResume()
        getOptionalFundList(phoneNum)
    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (!isInTransactionTime) {
                sendEmptyMessageDelayed(1, 10000)
                return
            }
            refresh()
        }
    }

    private fun getIndexInfo(): Unit{
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.getIndexValue()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    setIndexValue(it.data) }
                ) { }
    }

    // 获取小时
    private val isInTransactionTime: Boolean
        private get() {
            val hour = calendar!![Calendar.HOUR_OF_DAY] // 获取小时
            val minute = calendar!![Calendar.MINUTE] // 获取分钟
            val minuteOfDay = hour * 60 + minute // 从0:00分开是到目前为止的分钟数
            val start = 9 * 60 + 30 // 起始时间 00:20的分钟数
            val end = 15 * 60 // 结束时间 8:00的分钟数
            return minuteOfDay >= start && minuteOfDay <= end
        }

    private fun setIndexValue(indexValue: IndexValue?){
        //上证指数
        var hq_str_s_sh000001 = indexValue!!.hq_str_s_sh000001
        updateIndexValue(hq_str_s_sh000001.name, hq_str_s_sh000001.value, hq_str_s_sh000001.percent, 0)
        //创业板指
        var hq_str_s_sz399006 = indexValue!!.hq_str_s_sz399006
        updateIndexValue(hq_str_s_sz399006.name, hq_str_s_sz399006.value, hq_str_s_sz399006.percent, 0, 1)
        //深证成指
        var hq_str_s_sz399001 = indexValue!!.hq_str_s_sz399001
        updateIndexValue(hq_str_s_sz399001.name, hq_str_s_sz399001.value, hq_str_s_sz399001.percent, 1)
        //恒生指数
        var hq_str_int_hangseng = indexValue!!.hq_str_int_hangseng
        updateIndexValue(hq_str_int_hangseng.name, hq_str_int_hangseng.value, hq_str_int_hangseng.percent, 2, 1)
        //沪深300
        var hq_str_s_sz399300 = indexValue!!.hq_str_s_sz399300
        updateIndexValue(hq_str_s_sz399300.name, hq_str_s_sz399300.value, hq_str_s_sz399300.percent, 1, 1)
        //上证50
        var hq_str_s_sh000016 = indexValue!!.hq_str_s_sh000016
        updateIndexValue(hq_str_s_sh000016.name, hq_str_s_sh000016.value, hq_str_s_sh000016.percent, 2)


    }

    private fun updateIndexValue(name: String,value: Double,percent: Double,pos: Int,viewPos: Int = 0) {
        var rootView = view_flipper.getChildAt(pos);
        var indexName : TextView? = null
        var indexValue : TextView? = null
        var indexRise : TextView? = null

        if(viewPos == 0){
            indexName = rootView.findViewById(R.id.tv_index_name)
            indexValue = rootView.findViewById(R.id.tv_index)
            indexRise = rootView.findViewById(R.id.tv_index_rise)

        }else{
            indexName = rootView.findViewById(R.id.tv_index_name1)
            indexValue = rootView.findViewById(R.id.tv_index1)
            indexRise = rootView.findViewById(R.id.tv_index_rise1)
        }

        if (percent > 0) {
            indexValue.setTextColor(Color.RED)
            indexRise.setTextColor(Color.RED)
        } else {
            indexValue.setTextColor(Color.GREEN)
            indexRise.setTextColor(Color.GREEN)
        }

        var valueTemp = (value * 100).roundToInt() /100.00

        indexName.text = name
        indexValue.text = valueTemp.toString()+""
        indexRise.text = percent.toString()+"%"
    }

    private fun isRepeat(bean: FundBean): Int {
        for (i in fundBeanList.indices) {
            if (fundBeanList[i].fundcode == bean.fundcode) return i
        }
        return -1
    }
}