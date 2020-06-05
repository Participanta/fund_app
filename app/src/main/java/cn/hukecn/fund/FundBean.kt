package cn.hukecn.fund

/**
 * Created by Kelson on 2015/11/9.
 */
class FundBean {
    //    {
    //        "fundcode": "320007",
    //            "name": "诺安成长混合",
    //            "gszzl": "-2.58",
    //            "dwjz": "1.5360",
    //            "gsz": "1.4964",
    //            "gztime": "2020-05-28 11:19",
    //            "jzrq": "2020-05-27"
    //    }
    var fundcode //代码
            : String? = null
    var name //基金名
            : String? = null
    var gszzl //基金涨幅
            : String? = null
    var dwjz //基金净值
            : String? = null
    var gsz //基金净值
            : String? = null
    var gztime //估算时间
            : String? = null
    var jzrq //净值时间
            : String? = null
    var income = 0.0
    var money = 0f

    fun setIncome(income: Long) {
        this.income = income.toDouble()
    }

}