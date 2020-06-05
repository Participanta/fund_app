package cn.hukecn.adapter

import cn.hukecn.bean.FundItem
import cn.hukecn.fund.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class SearchListRecyclerViewAdapter(layoutResId: Int, data: List<FundItem?>?) : BaseQuickAdapter<FundItem, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: FundItem) {
        bindView(item, helper)
    }

    private fun bindView(bean: FundItem, helper: BaseViewHolder) {
        helper.setText(R.id.tv_fund_name, bean.fund_name)
        helper.setText(R.id.tv_fund_code, bean.fund_code)
        helper.setText(R.id.tv_fund_type, bean.fund_type)
        helper.addOnClickListener(R.id.tv_add)
    }
}