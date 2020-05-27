package cn.hukecn.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.hukecn.adapter.SearchListRecyclerViewAdapter;
import cn.hukecn.base.AppBaseActivity;
import cn.hukecn.bean.FundItem;
import cn.hukecn.network.FundRetrofitHolder;
import cn.hukecn.fund.R;
import cn.hukecn.network.FundService;
import cn.hukecn.network.Template;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppBaseActivity {
    private static final String TAG = "SearchActivity";
    List<FundItem> fundItemList = new ArrayList<>();

    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.recyclerview_search)
    RecyclerView recyclerViewSearch;

    private SearchListRecyclerViewAdapter searchListRecyclerViewAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editSearch.requestFocus();
        initRecyclerView();
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFund(editSearch.getText().toString());

            }
        });
    }

    private void initRecyclerView() {
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        searchListRecyclerViewAdapter = new SearchListRecyclerViewAdapter(R.layout.fund_search_recycler_item,fundItemList);
        recyclerViewSearch.setAdapter(searchListRecyclerViewAdapter);
        searchListRecyclerViewAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FundItem fundItem = fundItemList.get(position);
                addOptionalFund(fundItem);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    private void searchFund(String name){
        FundRetrofitHolder.getRetrofit()
                .create(FundService.class)
                .searchFund(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Template<List<FundItem>>>() {
                    @Override
                    public void accept(Template<List<FundItem>> listTemplate) throws Exception {
                        fundItemList.clear();
                        fundItemList.addAll(listTemplate.getData());
                        searchListRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void addOptionalFund(FundItem fundItem){
        Map<String,Object> params = new HashMap<>();
        params.put("open_id","2013551128");
        params.put("fund_code",fundItem.getFund_code());
        params.put("fund_name",fundItem.getFund_name());

        FundRetrofitHolder.getRetrofit()
                .create(FundService.class)
                .addOptionalFund(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Template<String>>() {
                    @Override
                    public void accept(Template<String> listTemplate) throws Exception {
                        Toast.makeText(SearchActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
