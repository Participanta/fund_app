package cn.hukecn.bean

class FundDeatil constructor(){
     val data_netWorthTrend //单位净值数据
            : String? = null
     val data_ACWorthTrend //累计单位净值数据
            : String? = null
     val data_grandTotal //累计收益率数据
            : String? = null
     val syl_1y //近一个月收益率
            : String? = null
     val syl_3y //近三个月收益率
            : String? = null
     val syl_6y //近六个月收益率
            : String? = null
     val syl_1n //近一年收益率
            : String? = null
     val data_fluctuationScale //获取规模变动
            : String? = null
     val data_currentFundManager //获取经理信息
            : String? = null
     val swithSameType //同类型基金涨幅榜
            : String? = null
     val data_assetAllocation //资产配置
            : String? = null
     val data_performanceEvaluation //业绩评价
            : String? = null
}