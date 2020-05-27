package cn.hukecn.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.hukecn.fund.DataBaseHelper;
import cn.hukecn.fund.FundBean;
import cn.hukecn.fund.InsertBDBean;
import cn.hukecn.fund.MyDataBase;
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
        float this_income = bean.getIncome();
        String percent = bean.getPecent_value();
        if(this_income < 0.00 || (!TextUtils.isEmpty(percent) && bean.getPecent_value().startsWith("-")))
        {
            helper.setTextColor(R.id.tv_income,Color.GREEN);
            helper.setTextColor(R.id.tv_fundpz,Color.GREEN);
        }else
        {
            helper.setTextColor(R.id.tv_income,Color.RED);
            helper.setTextColor(R.id.tv_fundpz,Color.RED);
        }

        helper.setText(R.id.tv_income,this_income+"");
        helper.setText(R.id.tv_fundpz,bean.pecent_value);
        helper.setText(R.id.tv_fundname,bean.name);
        helper.setText(R.id.tv_money,money+"");
        helper.addOnClickListener(R.id.llyt_root);
        helper.addOnClickListener(R.id.btn_delete);
    }
}
