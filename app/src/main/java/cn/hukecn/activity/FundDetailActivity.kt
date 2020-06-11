package cn.hukecn.activity

import android.graphics.Color
import android.text.TextUtils
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.bean.FundDeatil
import cn.hukecn.fund.R
import cn.hukecn.network.FundRetrofitHolder
import cn.hukecn.network.FundService
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fund_detail.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

class FundDetailActivity : AppBaseActivity() {
    var set1: LineDataSet? = null
    var values = ArrayList<Entry>()
    var netWorthTrendValues = ArrayList<Entry>()
    var acWorthTrendValues = ArrayList<Entry>()
    var grandTotalValues = ArrayList<Entry>()
    var fundScaleValues = ArrayList<BarEntry>()

    override fun initView() {
        initLineCharts()
        initData()
        initBarCharts()
        value_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0-> {
                        setLineChartValues(netWorthTrendValues)
                        values = netWorthTrendValues
                    }
                    1->{
                        setLineChartValues(acWorthTrendValues)
                        values = acWorthTrendValues

                    }
                    2->{
                        setLineChartValues(grandTotalValues)
                        values = grandTotalValues
                    }
                }

                radioButton_year.isChecked = true
            }

        })

        radio_group.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when(checkedId){
                    R.id.radioButton_month-> setLineChartValues(sublist(values.size-30,values.size))
                    R.id.radioButton_three_month->setLineChartValues(sublist(values.size-90,values.size))
                    R.id.radioButton_six_month->setLineChartValues(sublist(values.size-180,values.size))
                    R.id.radioButton_year->setLineChartValues(sublist(values.size-365,values.size))
                }
            }
        })

        iv_back.setOnClickListener {finish()}
    }

    private fun sublist(from : Int,end :Int) : ArrayList<Entry>{
        var splitList = ArrayList<Entry>()
        var endIndex  = end
        var fromIndex  = from
        if(from < 0){
            fromIndex = 0
        }

        if(end > values.size) {
            endIndex = values.size
        }
        for (index in fromIndex until endIndex){
            splitList.add(values[index])
        }

        return splitList
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fund_detail
    }

    private fun initData() {
        val intent = intent
        if (intent != null) {
            val fundid = intent.getStringExtra("fundid")
            val fundname = intent.getStringExtra("fundname")
            if (fundid == null || fundname == null) {
                Toast.makeText(applicationContext, "System Error...", Toast.LENGTH_LONG).show()
                return
            }
            tv_fund_name.text = fundname
            tv_fund_code.text = fundid.toString()
            getFundDetail(fundid)
        }
    }

    private fun getFundDetail(fundid: String) {
        FundRetrofitHolder.retrofit
                ?.create(FundService::class.java)
                ?.getFundDetail(fundid)
                ?.subscribeOn(Schedulers.io())
                ?.doOnSubscribe {showLoadingDialog()}
                ?.doFinally(){dismissLoadingDialog()}
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                   parseData(it.data) }) { }
    }

    private fun parseData(fundDeatil: FundDeatil?) {

        //解析获取单位净值数据
        val dataNewWorthTrend = fundDeatil!!.data_netWorthTrend
        val dataNewWorthTrendJsonArray = JSONArray(dataNewWorthTrend)
        for (index in 0 until dataNewWorthTrendJsonArray.length()) {
            val jsonObject = dataNewWorthTrendJsonArray.getJSONObject(index)
            val x = jsonObject.getLong("x")
            val y = jsonObject.getDouble("y")
            netWorthTrendValues.add(Entry(x.toFloat(), y.toFloat()))
            if (index == dataNewWorthTrendJsonArray.length() - 1) {
                tv_latest_value_title.text = resources.getString(R.string.latest_value, mFormat.format(x))
                tv_latest_value.text = y.toString()
                val pre = dataNewWorthTrendJsonArray.getJSONObject(index - 1).getDouble("y")
                val percent = (Math.round(((y / pre) - 1) * 10000) / 100.00)
                val bac = if (percent < 0) resources.getColor(R.color.limegreen) else resources.getColor(R.color.firebrick)
                rlyt_value.setBackgroundColor(bac)
                tv_increase_value.text = "$percent%"
            }
        }
        values = netWorthTrendValues
        setLineChartValues(sublist(values.size - 30, values.size))

        //解析获取累计单位净值数据
        val Data_ACWorthTrend = fundDeatil.data_ACWorthTrend
        val dataACWorthTrendTrendJsonArray = JSONArray(Data_ACWorthTrend)
        for (index in 0 until dataACWorthTrendTrendJsonArray.length()) {
            val jsonObject = dataACWorthTrendTrendJsonArray.getJSONArray(index)
            val x = jsonObject.get(0)
            val y = jsonObject.get(1)
            acWorthTrendValues.add(Entry(x.toString().toFloat(), y.toString().toFloat()))
        }


        //解析获取累计收益率数据
        val Data_grandTotal = fundDeatil.data_grandTotal
        val dataGrandTotalJsonArray = JSONArray(Data_grandTotal)
        for (index in 0 until dataGrandTotalJsonArray.length()) {
            val jsonObject = dataGrandTotalJsonArray.getJSONObject(index)
            if (jsonObject.getJSONArray("data") != null) {
                val jsonArray = jsonObject.getJSONArray("data")
                for (j in 0 until jsonArray.length()) {
                    val dataJsonArray = jsonArray.getJSONArray(j)
                    val x = dataJsonArray.get(0)
                    val y = dataJsonArray.get(1)
                    grandTotalValues.add(Entry(x.toString().toFloat(), y.toString().toFloat()))
                }
                break
            }

        }


        val syl_1y = fundDeatil.syl_1y
        setEarnPercent(syl_1y, tv_one_month_earn)


        //近三个月收益率
        val syl_3y = fundDeatil.syl_3y
        setEarnPercent(syl_3y, tv_three_month_earn)

        //近六个月收益率
        val syl_6y = fundDeatil.syl_6y
        setEarnPercent(syl_6y, tv_six_month_earn)


        //近一年收益率
        val syl_1n = fundDeatil.syl_1n
        setEarnPercent(syl_1n, tv_year_earn)


        //获取规模变动
        val data_fluctuationScale = fundDeatil.data_fluctuationScale
        val data_fluctuationScaleJsonObject = JSONObject(data_fluctuationScale)
        val categories = data_fluctuationScaleJsonObject.getJSONArray("categories")
        val series = data_fluctuationScaleJsonObject.getJSONArray("series")

        for (index in 1 until series.length()) {
            val y = series.getJSONObject(index).getDouble("y").toFloat()
            val x = convert2long(categories.getString(index)).toFloat()
            fundScaleValues.add(BarEntry(x, y))
        }

        setBarChartDatas(fundScaleValues)


        //获取经理信息
        val data_current_manager = fundDeatil.data_currentFundManager
        val currentFundManagerJsonObject = JSONArray(data_current_manager)
        val mangerInfo = currentFundManagerJsonObject.getJSONObject(0)
        val managerName = mangerInfo.getString("name")
        val star = mangerInfo.getInt("star")
        val workTime = mangerInfo.getString("workTime")
        val headUrl = mangerInfo.getString("pic")

        tv_manager_name.text = managerName
        tv_manager_star.rating = star.toFloat()
        tv_manage_time.text = workTime
        Glide.with(this).load(headUrl).into(iv_manager_header)
        setBarChartDatas(fundScaleValues)
    }

    private fun setEarnPercent(percent: String?,textView:TextView) {
        if(TextUtils.isEmpty(percent)){
            textView.text = ""
            return
        }

        var earnPercent =Math.round(percent!!.toDouble()*100)/100.00

        if(earnPercent > 0){
            textView.setTextColor(Color.RED)
        }else{
            textView.setTextColor(Color.GREEN)
        }
        textView.text = "$earnPercent%"
    }

    private fun setLineChartValues(values: ArrayList<Entry>) {
        setData(values)
        mLineChar!!.animateX(1500)
        //刷新
        mLineChar!!.invalidate()
    }

    private fun setData(values: ArrayList<Entry>) {
        if (mLineChar.data != null && mLineChar.data.dataSetCount > 0) {
            set1 = mLineChar.data.getDataSetByIndex(0) as LineDataSet
            set1!!.values = values
            mLineChar.data.notifyDataChanged()
            mLineChar.notifyDataSetChanged()
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = LineDataSet(values, "近一个月走势")

            // 在这里设置线
//            set1!!.enableDashedHighlightLine(10f, 5f, 0f)
            set1!!.color =  resources.getColor(R.color.actionbar_bac)
            set1!!.setDrawCircles(false)
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
            mLineChar.data = data
            mLineChar.xAxis.valueFormatter = object : ValueFormatter() {
                var mFormat = SimpleDateFormat("M月d")
                override fun getFormattedValue(value: Float): String {
                    return mFormat.format(value)
                }
            }
        }
    }

    private fun initLineCharts() {
//        //设置手势滑动事件
//        mLineChar.setOnChartGestureListener(this);
//        //设置数值选择监听
//        mLineChar.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChar.setDrawGridBackground(false)
        //设置描述文本
        mLineChar.description.isEnabled = false
        //设置支持触控手势
        mLineChar.setTouchEnabled(true)
        //设置缩放
        mLineChar.isDragEnabled = true
        //设置推动
        mLineChar.setScaleEnabled(true)
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChar.setPinchZoom(true)
    }


    private fun initBarCharts() {
        //后台绘制
        bar_chart.setDrawGridBackground(false)
        //设置描述文本
        bar_chart.description.isEnabled = false
        //设置支持触控手势
        bar_chart.setTouchEnabled(true)
        //设置缩放
        bar_chart.isDragEnabled = true
        //设置推动
        bar_chart.setScaleEnabled(true)
        //如果禁用,扩展可以在x轴和y轴分别完成
        bar_chart.setPinchZoom(true)
    }

    private fun setBarChartDatas(datas: ArrayList<BarEntry>){
        val set1 = BarDataSet(datas, "")

        set1.color = resources.getColor(R.color.actionbar_bac)
        set1.valueTextSize = 10f
//        set1.formLineWidth = 0.5f
//        set1.formSize = 15f
//        set1.setDrawValues(false)
        val dataSets = ArrayList<IBarDataSet>()
        //添加数据集
        dataSets.add(set1)

        //创建一个数据集的数据对象
        val data = BarData(dataSets)
        data.barWidth = 3500000000f

        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toString()+"亿"
            }
        })

        //谁知数据
        bar_chart.data = data

        bar_chart.xAxis.setDrawGridLines(false)//不绘制格网线
        bar_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        bar_chart.xAxis.valueFormatter = object : ValueFormatter() {
            var mFormat = SimpleDateFormat("yy-MM-dd")
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(value)
            }
        }
        bar_chart.xAxis.axisMinimum = datas[0].x-7000000000
        bar_chart.xAxis.axisMaximum = datas[datas.size-1].x+7000000000

        bar_chart.axisRight.isEnabled = false
        bar_chart.axisLeft.isEnabled = false

        bar_chart.animateX(1500)
        //刷新
        bar_chart.invalidate()

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
        var DATE_MM_DD = "MM-dd"
        var mFormat = SimpleDateFormat(DATE_MM_DD)
    }
}