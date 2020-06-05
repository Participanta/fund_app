package cn.hukecn.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Process
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import cn.hukecn.adapter.FundListRecyclerViewAdapter
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.fund.*
import cn.hukecn.network.FundRetrofitHolder
import cn.hukecn.network.FundService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.jaeger.library.StatusBarUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_title_main.*
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    var tv_income: TextView? = null
    var income : Double = 0.0
    var current: Long = 0
    var calendar: Calendar? = null
    var btn_setting: Button? = null
    var fundListRecyclerViewAdapter: FundListRecyclerViewAdapter? = null
    private var fundBeanList: MutableList<FundBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusBarUtil.setTransparent(this)
        ButterKnife.bind(this)
        calendar = Calendar.getInstance()
        btn_setting = findViewById<View>(R.id.btn_setting) as Button
        tv_income = findViewById<View>(R.id.tv_income) as TextView
        initRecyclerView()
        swipe_refresh_layout.setOnRefreshListener {
            getOptionalFundList("2013551128")
        }
        tv_search.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
        tv_search.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java))}
        getOptionalFundList("2013551128")
        getlatestInfo()
        initViewFlipper()
    }

    private fun initViewFlipper() {
        for(index in 0 until 3){
            val view = layoutInflater.inflate(R.layout.item_flipper,null)
            view_flipper.addView(view)
        }
        view_flipper.setInAnimation(this,R.anim.anim_in)
        view_flipper.setOutAnimation(this,R.anim.anim_out)
        view_flipper.setFlipInterval(3500)
        view_flipper.startFlipping()
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
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
    }

    private fun showEditDialog(fundBean: FundBean) {
        val type = InputType.TYPE_CLASS_NUMBER
        MaterialDialog(this).show {
            title(R.string.remind)
            input(waitForPositiveButton = true,hint = "请输入购买金额",inputType = type) { dialog, text ->
                fundBean.money = text.toString().toFloat()
                updateData(fundBean)
                fundListRecyclerViewAdapter!!.notifyDataSetChanged()
                countIncome()
            }
        }
    }

    private fun updateData(fundBean: FundBean) {
        val params: MutableMap<String, Any?> = HashMap()
        params["open_id"] = "2013551128"
        params["fund_code"] = fundBean.fundcode
        params["fund_name"] = fundBean.name
        params["fund_money"] = fundBean.money
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.updateOptionalFund(params)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ Toast.makeText(this@MainActivity, "更新成功", Toast.LENGTH_SHORT).show() }) { }
    }

    private fun deleteItem(fundBean: FundBean) {
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.deleteOptionalFund("2013551128",fundBean.fundcode)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show() }) { }
    }

    private fun toFundDetailActivity(fundBean: FundBean) {
        val intent = Intent(this, FundDetailActivity::class.java)
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
        swipe_refresh_layout.isRefreshing = false
        countIncome()
    }

    private fun refresh() {
        getlatestInfo()
        for (bean in fundBeanList) {
            getFundValuation(bean.fundcode)
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
                ?.subscribe({ listTemplate -> listTemplate.data?.let { updateView(it) } }) { throwable -> Log.i(TAG, "accept: " + throwable.message) }
    }

    private fun getOptionalFundList(openId: String) {
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

    override fun onBackPressed() {
        if (System.currentTimeMillis() - current > 2000) {
            current = System.currentTimeMillis()
            Toast.makeText(applicationContext, "再按一次退出程序", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
            Process.killProcess(Process.myPid())
        }
    }

    override fun onResume() {
        super.onResume()
        getOptionalFundList("2013551128")
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

    val listener = object : AsyncHttp.HttpListener{
        override fun onHttpCallBack(statusCode: Int, content: String?) {
            if (statusCode == 200) {
                if (statusCode == 200) {
                    setIndexValue(content)
                }

            }
        }

    }
    private fun getlatestInfo(): Unit{
            MyHttp.get(this@MainActivity, AppConfig.LATESTINDEXURL, listener, "gbk")
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

    private fun setIndexValue(content: String?){
        val datas = content!!.split(";")
        for (value in datas){
            if(value.contains("hq_str_s_sh000001")){  //上证指数
                val s_sh = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = s_sh.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),0)
            }

            if(value.contains("hq_str_s_sz399006")){  //创业板指
                val s_sz = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = s_sz.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),0,1)
            }
            if(value.contains("hq_str_s_sz399001")){  //深证成指
                val s_sz = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = s_sz.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),1)
            }

            if(value.contains("hq_str_int_hangseng")){  //恒生指数
                val hangseng = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = hangseng.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),2,1)
            }

            if(value.contains("hq_str_s_sz399300")){  //沪深300
                val s_sz = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = s_sz.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),1,1)
            }

            if(value.contains("hq_str_s_sh000016")){  //上证50
                val s_sz = value.substring(value.indexOf("\"")+1,value.length-2)
                val indexValues = s_sz.split(",")
                updateIndexValue(indexValues[0],indexValues[1].toDouble(),indexValues[3].toDouble(),2)
            }
        }
    }

    private fun updateIndexValue(name: String,value: Double,percent: Double,pos: Int,viewPos: Int = 0) {
        var rootView = view_flipper.getChildAt(pos);
        var indexName :TextView ? = null
        var indexValue :TextView ? = null
        var indexRise :TextView ? = null

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