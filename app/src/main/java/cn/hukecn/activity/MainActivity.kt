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
import cn.hukecn.adapter.FundDetailAdapter
import cn.hukecn.adapter.FundListRecyclerViewAdapter
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

class MainActivity : AppCompatActivity() {
    var tv_income: TextView? = null
    var tv_info1: TextView? = null
    var tv_info2: TextView? = null
    var tv_info1_1: TextView? = null
    var tv_info1_3: TextView? = null
    var tv_info2_1: TextView? = null
    var tv_info2_3: TextView? = null
    var income : Double = 0.0
    var current: Long = 0
    var adapter: FundDetailAdapter? = null
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
        tv_info1 = findViewById<View>(R.id.tv_info1) as TextView
        tv_info2 = findViewById<View>(R.id.tv_info2) as TextView
        tv_info1_1 = findViewById<View>(R.id.tv_info1_1) as TextView
        tv_info1_3 = findViewById<View>(R.id.tv_info1_3) as TextView
        tv_info2_1 = findViewById<View>(R.id.tv_info2_1) as TextView
        tv_info2_3 = findViewById<View>(R.id.tv_info2_3) as TextView
        adapter = FundDetailAdapter(this, tv_income)
        adapter!!.setOnMenuDelListener { }
        btn_setting!!.setOnClickListener {
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            startActivity(intent)
        }
        initRecyclerView()
        swipe_refresh_layout.setOnRefreshListener {
            getOptionalFundList("2013551128")
            refresh()
        }
        tv_search.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
        getOptionalFundList("2013551128")
        latestInfo
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        fundListRecyclerViewAdapter = FundListRecyclerViewAdapter(R.layout.fund_list_swipe_item, fundBeanList)
        recyclerview.adapter = fundListRecyclerViewAdapter
        fundListRecyclerViewAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            val id = view.id
            val fundBean = fundBeanList.get(position)
            if (id == R.id.llyt_root) {
                toFundDetailActivity(fundBean)
            } else if (id == R.id.btn_delete) {
                deleteItem(fundBean)
                fundBeanList.removeAt(position)
                fundListRecyclerViewAdapter!!.notifyItemRemoved(position)
            } else if (id == R.id.tv_money) {
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
                fundListRecyclerViewAdapter!!.notifyDataSetChanged()
                countIncome()
            }
        }
    }
    fun deleteItem(fundBean: FundBean) {
        val db = DataBaseHelper.getInstance().dataBase
        db.delete(fundBean.fundcode)
    }

    private fun toFundDetailActivity(fundBean: FundBean) {
        val intent = Intent(this, FundDetailActivity::class.java)
        intent.putExtra("fundid", fundBean.fundcode)
        intent.putExtra("fundname", fundBean.name)
        startActivity(intent)
    }

    fun updateView(bean: FundBean) {
        val location = isRepeat(bean)
        if (location == -1) {
            //原列表中没有
            fundBeanList.add(bean)
        } else {
            fundBeanList[location].fundcode = bean.fundcode;
            fundBeanList[location].name = bean.name;
            fundBeanList[location].gszzl = bean.gszzl;
            fundBeanList[location].dwjz = bean.dwjz;
            fundBeanList[location].gsz = bean.gsz;
            fundBeanList[location].gztime = bean.gztime;
            fundBeanList[location].jzrq = bean.jzrq;

            //原列表中有
        }
        //        countIncome();
        fundListRecyclerViewAdapter!!.notifyDataSetChanged()
        swipe_refresh_layout.isRefreshing = false
        countIncome();
    }

    private fun refresh() {
        latestInfo
        handler.sendEmptyMessageDelayed(0, 10000)
        for (bean in fundBeanList) {
            getFundValuation(bean.getFundcode())
        }
    }

    private fun getFundValuation(fund_code: String) {
        FundRetrofitHolder.getRetrofit()
                .create(FundService::class.java)
                .getFundValuation(fund_code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listTemplate -> updateView(listTemplate.data) }) { throwable -> Log.i(TAG, "accept: " + throwable.message) }
    }

    private fun getOptionalFundList(openId: String) {
        FundRetrofitHolder.getRetrofit()
                .create(FundService::class.java)
                .getOptionalFundList(openId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listTemplate ->
                    val optionalFundItems = listTemplate.data
                    for (optionalFundItem in optionalFundItems) {

                        val fundBean = FundBean()
                        fundBean.setFundcode(optionalFundItem.fund_code)
                        fundBean.setName(optionalFundItem.fund_name)
                        fundBean.setMoney(optionalFundItem.fund_money)
                        val location = isRepeat(fundBean)
                        if (location == -1) {
                            //原列表中没有
                            fundBeanList.add(fundBean)
                        } else {
                            //原列表中有
                        }
                    }
                    fundListRecyclerViewAdapter!!.notifyDataSetChanged()
                    refresh()
                }) { }
    }

    private fun countIncome() {
        income = 0.0
        fundBeanList.forEach {
            var this_percent = it.gszzl.toDouble()
            var this_income : Double = (this_percent * it.money).toDouble();
            this_income = (this_income/100.0f).toDouble();
            this_income =Math.round(this_income*10)/10.0;
            it.income = this_income

            income += this_income;
        }

        income =Math.round(income*10)/10.0;
        tv_income!!.setText(income.toString());

        if(income < 0.00)
        {
            tv_income!!.setTextColor(Color.GREEN);
        }else
        {
            tv_income!!.setTextColor(Color.RED);
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
        refresh()
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

    val latestInfo: Unit
        get() {
            MyHttp.get(this@MainActivity, AppConfig.LATESTINDEXURL, { statusCode, content ->
                if (statusCode == 200) {
                    setIndexTextView(content)
                }
            }, "gbk")
        }// 结束时间 8:00的分钟数// 起始时间 00:20的分钟数// 从0:00分开是到目前为止的分钟数// 获取分钟

    // 获取小时
    private val isInTransactionTime: Boolean
        private get() {
            val hour = calendar!![Calendar.HOUR_OF_DAY] // 获取小时
            val minute = calendar!![Calendar.MINUTE] // 获取分钟
            val minuteOfDay = hour * 60 + minute // 从0:00分开是到目前为止的分钟数
            val start = 9 * 60 + 30 // 起始时间 00:20的分钟数
            val end = 15 * 60 // 结束时间 8:00的分钟数
            return if (minuteOfDay >= start && minuteOfDay <= end) {
                true
            } else {
                false
            }
        }

    private fun setIndexTextView(content: String) {
        var indexStr: String
        var start = content.indexOf(',') + 1
        var end = content.indexOf(',', start + 1)
        indexStr = content.substring(start, end) //3539.04
        var temp = indexStr.toDouble()
        tv_info1_1!!.text = String.format("%.2f", temp)
        start = end + 1
        end = content.indexOf(',', start)
        indexStr = content.substring(start, end) //14.051
        temp = indexStr.toDouble()
        start = end + 1
        end = content.indexOf(',', start)
        indexStr = content.substring(start, end) + '%' //0.40%
        tv_info1_3!!.text = indexStr
        if (indexStr.indexOf('-') == -1) {
            tv_info1_1!!.setTextColor(Color.RED)
            tv_info1_3!!.setTextColor(Color.RED)
        } else {
            tv_info1_1!!.setTextColor(Color.GREEN)
            tv_info1_3!!.setTextColor(Color.GREEN)
        }
        start = content.indexOf("创业板指") + 5
        end = content.indexOf(',', start)
        indexStr = content.substring(start, end) //2746.49
        temp = indexStr.toDouble()
        tv_info2_1!!.text = String.format("%.2f", temp)
        start = end + 1
        end = content.indexOf(',', start)
        indexStr = content.substring(start, end) //54.333
        temp = indexStr.toDouble()
        start = end + 1
        end = content.indexOf(',', start)
        indexStr = content.substring(start, end) + '%' //2.02%
        tv_info2_3!!.text = indexStr
        if (indexStr.indexOf('-') == -1) {
            tv_info2_1!!.setTextColor(Color.RED)
            tv_info2_3!!.setTextColor(Color.RED)
        } else {
            tv_info2_1!!.setTextColor(Color.GREEN)
            tv_info2_3!!.setTextColor(Color.GREEN)
        }
    }

    fun isRepeat(bean: FundBean): Int {
        for (i in fundBeanList.indices) {
            if (fundBeanList[i].fundcode == bean.fundcode) return i
        }
        return -1
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}