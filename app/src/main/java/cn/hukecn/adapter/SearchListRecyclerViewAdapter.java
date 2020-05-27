package cn.hukecn.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.hukecn.bean.FundItem;
import cn.hukecn.fund.R;

public class SearchListRecyclerViewAdapter extends BaseQuickAdapter<FundItem, BaseViewHolder> {

    public SearchListRecyclerViewAdapter(int layoutResId, List<FundItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FundItem item) {
        bindView(item,helper);
    }

    private void bindView(FundItem bean, BaseViewHolder helper) {
        helper.setText(R.id.tv_fund_name,bean.getFund_name());
        helper.setText(R.id.tv_fund_code,bean.getFund_code());
        helper.setText(R.id.tv_fund_type,bean.getFund_type());
        helper.addOnClickListener(R.id.tv_add);
    }
}
