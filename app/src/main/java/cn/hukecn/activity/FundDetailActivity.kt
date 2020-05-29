package cn.hukecn.activity

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.fund.HistoryNetBean
import cn.hukecn.fund.MyHttp
import cn.hukecn.fund.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_fund_detail.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class FundDetailActivity : AppBaseActivity() {
    var mLineChar: LineChart? = null
    var set1: LineDataSet? = null
    var values = ArrayList<Entry>()
    var netWorthTrendValues = ArrayList<Entry>()
    var acWorthTrendValues = ArrayList<Entry>()
    var grandTotalValues = ArrayList<Entry>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fund_detail)
        StatusBarUtil.setTransparent(this)
        mLineChar = findViewById<View>(R.id.mLineChar) as LineChart
        initCharts()
        initChartDatas()
        initView()
    }

    private fun initView() {
        value_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0-> setLineChartValues(netWorthTrendValues)
                    1->setLineChartValues(acWorthTrendValues)
                    2->setLineChartValues(grandTotalValues)
                }
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fund_detail
    }

    private fun initChartDatas() {
        var url: String? = null
        val intent = intent
        if (intent != null) {
            val fundid = intent.getStringExtra("fundid")
            val fundname = intent.getStringExtra("fundname")
            if (fundid == null || fundname == null) {
                Toast.makeText(applicationContext, "System Error...", Toast.LENGTH_LONG).show()
                return
            }
            tv_title.text = fundname
            getFundDetail(fundid);
        }
    }

    private fun getFundDetail(fundid: String) {
        var url = "http://fund.eastmoney.com/pingzhongdata/$fundid.js"
        MyHttp.get(this, url, { statusCode, content ->
            if (statusCode == 200) {
                parseData(content)

            }
        }, "utf-8")
    }

    private fun parseData(content: String?) {
        val datas = content?.split(";")
        for (item in datas!!){
            if (item.contains("Data_netWorthTrend")){   //解析获取单位净值数据
                val  dataNewWorthTrend = item.substring(item.indexOf("=")+1)
                val dataNewWorthTrendJsonArray = JSONArray(dataNewWorthTrend);
                for (index in 0 until dataNewWorthTrendJsonArray.length()){
                    val jsonObject = dataNewWorthTrendJsonArray.getJSONObject(index)
                    val x = jsonObject.getLong("x")
                    val y = jsonObject.getDouble("y")
                    netWorthTrendValues.add(Entry(x.toFloat(), y.toFloat()))
                }
                setLineChartValues(netWorthTrendValues)
            }

            if (item.contains("Data_ACWorthTrend")){   //解析获取单位净值数据
                val  dataNewWorthTrend = item.substring(item.indexOf("=")+1)
                val dataNewWorthTrendJsonArray = JSONArray(dataNewWorthTrend);
                for (index in 0 until dataNewWorthTrendJsonArray.length()){
                    val jsonObject = dataNewWorthTrendJsonArray.getJSONArray(index)
                    val x = jsonObject.get(0)
                    val y = jsonObject.get(1)
                    acWorthTrendValues.add(Entry(x.toString().toFloat(), y.toString().toFloat()))
                }
            }

            if (item.contains("Data_grandTotal")){   //解析获取单位净值数据
                val  dataNewWorthTrend = item.substring(item.indexOf("=")+1)
                val dataNewWorthTrendJsonArray = JSONArray(dataNewWorthTrend);
                for (index in 0 until dataNewWorthTrendJsonArray.length()){
                    val jsonObject = dataNewWorthTrendJsonArray.getJSONObject(index)
                    if(jsonObject.getJSONArray("data")!=null){
                        val jsonArray = jsonObject.getJSONArray("data")
                        for (j in 0 until jsonArray.length()){
                            val dataJsonArray = jsonArray.getJSONArray(j)
                            val x = dataJsonArray.get(0)
                            val y = dataJsonArray.get(1)
                            grandTotalValues.add(Entry(x.toString().toFloat(), y.toString().toFloat()))
                        }
                        break
                    }

                }
            }
        }
    }

    private fun setLineChartValues(values: ArrayList<Entry>) {
        setData(values)
        mLineChar!!.animateX(1500)
        //刷新
        mLineChar!!.invalidate()
    }

    private fun setData(values: ArrayList<Entry>) {
        if (mLineChar!!.data != null && mLineChar!!.data.dataSetCount > 0) {
            set1 = mLineChar!!.data.getDataSetByIndex(0) as LineDataSet
            set1!!.values = values
            mLineChar!!.data.notifyDataChanged()
            mLineChar!!.notifyDataSetChanged()
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = LineDataSet(values, "近一个月走势")

            // 在这里设置线
//            set1!!.enableDashedHighlightLine(10f, 5f, 0f)
            set1!!.color = Color.BLACK
            set1!!.setDrawCircles(false);
            set1!!.valueTextSize = 9f
            set1!!.setDrawFilled(true)
            set1!!.formLineWidth = 0.5f
//            set1!!.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1!!.formSize = 15f
            set1!!.setDrawValues(false)
            set1!!.fillColor = resources.getColor(R.color.actionbar_bac)
            val dataSets = ArrayList<ILineDataSet>()
            //添加数据集
            dataSets.add(set1!!)

            //创建一个数据集的数据对象
            val data = LineData(dataSets)

            //谁知数据
            mLineChar!!.data = data
            mLineChar!!.xAxis.valueFormatter = object : ValueFormatter() {
                var mFormat = SimpleDateFormat("M月d")
                override fun getFormattedValue(value: Float): String {
                    return mFormat.format(value)
                }
            }
        }
    }

    private fun initCharts() {
//        //设置手势滑动事件
//        mLineChar.setOnChartGestureListener(this);
//        //设置数值选择监听
//        mLineChar.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChar!!.setDrawGridBackground(false)
        //设置描述文本
        mLineChar!!.description.isEnabled = false
        //设置支持触控手势
        mLineChar!!.setTouchEnabled(true)
        //设置缩放
        mLineChar!!.isDragEnabled = true
        //设置推动
        mLineChar!!.setScaleEnabled(true)
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChar!!.setPinchZoom(true)
    }

    private fun praseHtml(content: String): List<HistoryNetBean> {
        val list: MutableList<HistoryNetBean> = ArrayList()
        var tbody: String? = null
        var start = -1
        var end = -1
        start = content.indexOf("<tbody>")
        end = content.indexOf("</tbody>")
        if (start == -1 || end == -1) return list
        tbody = content.substring(start, end)
        var tr: String? = null
        var tr_start = 0
        var tr_end = 0
        while (true) {
            val bean = HistoryNetBean()
            tr_start = tbody.indexOf("<tr>", tr_end)
            tr_end = tbody.indexOf("</tr>", tr_start)
            if (tr_start <= 0 || tr_end <= 0) break
            tr = tbody.substring(tr_start, tr_end)
            start = tr.indexOf("<td>") + 4
            end = tr.indexOf("</td>")
            if (start == -1 || end == -1) break
            bean.date = tr.substring(start, end)
            start = tr.indexOf("bold") + 6
            end = tr.indexOf("</td>", start)
            if (start == -1 || end == -1) break
            bean.value = tr.substring(start, end)
            start = tr.indexOf("bold", end) + 6
            end = tr.indexOf("</td>", start)
            start = tr.indexOf("bold", end)
            start = tr.indexOf(">", start) + 1
            end = tr.indexOf("</td>", start) - 1
            val strFloat = tr.substring(start, end)
            bean.percent = strFloat.toFloat()
            list.add(bean)
        }
        return list


    }

    fun convert2long(date: String?): Long {
        try {
            if (!TextUtils.isEmpty(date)) {
                val sf = SimpleDateFormat(DATE_YYYY_MM_DD)
                return sf.parse(date).time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }

    companion object {
        var DATE_YYYY_MM_DD = "yyyy-MM-dd"
    }
}