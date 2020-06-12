package cn.hukecn.bean


data class IndexValue(
    val hq_str_int_hangseng: IndexItem,
    val hq_str_s_sh000001: IndexItem,
    val hq_str_s_sh000016: IndexItem,
    val hq_str_s_sz399001: IndexItem,
    val hq_str_s_sz399006: IndexItem,
    val hq_str_s_sz399300: IndexItem
)

data class IndexItem(
    val name: String,
    val percent: Double,
    val value: Double
)