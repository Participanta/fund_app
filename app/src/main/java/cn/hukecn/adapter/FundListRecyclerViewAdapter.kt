package cn.hukecn.adapter

import android.graphics.Color
import android.text.TextUtils
import cn.hukecn.fund.FundBean
import cn.hukecn.fund.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class FundListRecyclerViewAdapter(layoutResId: Int, data: List<FundBean?>?) : BaseQuickAdapter<FundBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: FundBean) {
        bindView(item, helper)
    }

    private fun bindView(bean: FundBean, helper: BaseViewHolder) {
        val money = bean.money
        val this_income = bean.income
        val percent = bean.gszzl
        if (this_income < 0.00 || !TextUtils.isEmpty(percent) && bean.gszzl!!.startsWith("-")) {
            helper.setTextColor(R.id.tv_income, Color.GREEN)
            helper.setTextColor(R.id.tv_fundpz, Color.GREEN)
        } else {
            helper.setTextColor(R.id.tv_income, mContext.resources.getColor(R.color.firebrick))
            helper.setTextColor(R.id.tv_fundpz, mContext.resources.getColor(R.color.firebrick))
        }
        helper.setText(R.id.tv_income, this_income.toString() + "")
        helper.setText(R.id.tv_fundpz, if (bean.gszzl == null) "" else bean.gszzl + "%")
        helper.setText(R.id.tv_fundname, bean.name)
        helper.setText(R.id.tv_money, money.toString() + "")
        helper.setText(R.id.tv_fund_worth, bean.dwjz)
        helper.addOnClickListener(R.id.llyt_root)
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.tv_money)
        helper.addOnClickListener(R.id.iv_edit)
    }
}