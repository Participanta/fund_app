package cn.hukecn.adapter;

import android.graphics.Color;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.hukecn.fund.FundBean;
import cn.hukecn.fund.R;

public class FundListRecyclerViewAdapter extends BaseQuickAdapter<FundBean, BaseViewHolder> {

    public FundListRecyclerViewAdapter(int layoutResId, List<FundBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FundBean item) {
        bindView(item,helper);
    }

    private void bindView(FundBean bean, BaseViewHolder helper) {
        float money = bean.getMoney();
        double this_income = bean.getIncome();
        String percent = bean.getGszzl();
        if(this_income < 0.00 || (!TextUtils.isEmpty(percent) && bean.getGszzl().startsWith("-")))
        {
            helper.setTextColor(R.id.tv_income,Color.GREEN);
            helper.setTextColor(R.id.tv_fundpz,Color.GREEN);
        }else
        {
            helper.setTextColor(R.id.tv_income,Color.RED);
            helper.setTextColor(R.id.tv_fundpz,Color.RED);
        }

        helper.setText(R.id.tv_income,this_income+"");
        helper.setText(R.id.tv_fundpz,bean.getGszzl() == null ? "":bean.getGszzl()+"%");
        helper.setText(R.id.tv_fundname,bean.name);
        helper.setText(R.id.tv_money,money+"");
        helper.addOnClickListener(R.id.llyt_root);
        helper.addOnClickListener(R.id.btn_delete);
        helper.addOnClickListener(R.id.tv_money);
    }
}
