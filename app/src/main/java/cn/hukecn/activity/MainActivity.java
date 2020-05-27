package cn.hukecn.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hukecn.adapter.FundDetailAdapter;
import cn.hukecn.adapter.FundListRecyclerViewAdapter;
import cn.hukecn.bean.FundItem;
import cn.hukecn.bean.OptionalFundItem;
import cn.hukecn.fund.AppConfig;
import cn.hukecn.fund.AsyncHttp;
import cn.hukecn.fund.BaseTools;
import cn.hukecn.fund.DataBaseHelper;
import cn.hukecn.fund.FundBean;
import cn.hukecn.fund.InsertBDBean;
import cn.hukecn.fund.MyDataBase;
import cn.hukecn.fund.MyHttp;
import cn.hukecn.fund.R;
import cn.hukecn.listener.OnMenuDelListener;
import cn.hukecn.network.FundRetrofitHolder;
import cn.hukecn.network.FundService;
import cn.hukecn.network.Template;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AsyncHttp.HttpListener {

    private static final String TAG = "MainActivity";
    TextView tv_income = null,tv_info1,tv_info2,
            tv_info1_1,tv_info1_2,tv_info1_3;
    TextView tv_info2_1,tv_info2_2,tv_info2_3;

    float income = 0f;
    long current = 0;
    FundDetailAdapter adapter;
    Calendar calendar = null;
    Button btn_setting;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_search)
    TextView tvSearch;


    FundListRecyclerViewAdapter fundListRecyclerViewAdapter;
    private List<FundBean> fundBeanList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        calendar = Calendar.getInstance() ;
        btn_setting = (Button) findViewById(R.id.btn_setting);
        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_info1 = (TextView) findViewById(R.id.tv_info1);
        tv_info2 = (TextView) findViewById(R.id.tv_info2);

        tv_info1_1 = (TextView) findViewById(R.id.tv_info1_1);
        tv_info1_2 = (TextView) findViewById(R.id.tv_info1_2);
        tv_info1_3 = (TextView) findViewById(R.id.tv_info1_3);

        tv_info2_1 = (TextView) findViewById(R.id.tv_info2_1);
        tv_info2_2 = (TextView) findViewById(R.id.tv_info2_2);
        tv_info2_3 = (TextView) findViewById(R.id.tv_info2_3);

        adapter = new FundDetailAdapter(this,tv_income);
        adapter.setOnMenuDelListener(new OnMenuDelListener() {
            @Override
            public void onMenuDel(int position) {

            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOptionalFundList("2013551128");
                refresh();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        getOptionalFundList("2013551128");
        getLatestInfo();
    }





    private void initRecyclerView() {
        fundBeanList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        fundListRecyclerViewAdapter = new FundListRecyclerViewAdapter(R.layout.fund_list_swipe_item,fundBeanList);
        recyclerView.setAdapter(fundListRecyclerViewAdapter);
        fundListRecyclerViewAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id = view.getId();
                FundBean fundBean = fundBeanList.get(position);
                if(id == R.id.llyt_root){
                    toFundDetailActivity(fundBean);
                }else if(id == R.id.btn_delete){
                    deleteItem(fundBean);
                    fundBeanList.remove(position);
                    fundListRecyclerViewAdapter.notifyItemRemoved(position);
                }

            }
        });
    }

    public void deleteItem(FundBean fundBean){
        MyDataBase db = DataBaseHelper.getInstance().getDataBase();
        db.delete(fundBean.id);
    }

    private void toFundDetailActivity(FundBean fundBean) {
        Intent intent = new Intent(this, FundDetailActivity.class);
        intent.putExtra("fundid",fundBean.id);
        intent.putExtra("fundname",fundBean.name);
        startActivity(intent);
    }


    public void ChangeView(FundBean bean) {
        int location = isRepeat(bean);
        if(location == -1)
        {
            //原列表中没有
            fundBeanList.add(bean);
        }else
        {
            //原列表中有
            fundBeanList.set(location,bean);
        }
//        countIncome();
        fundListRecyclerViewAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }

    private void refresh(){
            handler.sendEmptyMessageDelayed(0, 6000);
            for (FundBean bean : fundBeanList)
            {
                //baseNetWork.getFundInfo(bean.fundid, this);
                MyHttp.get(MainActivity.this, AppConfig.BASEURL+bean.getId()+".js?rt="+System.currentTimeMillis(),this,"utf-8");
            }

    }

    private void getOptionalFundList(String openId){
        FundRetrofitHolder.getRetrofit()
                .create(FundService.class)
                .getOptionalFundList(openId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Template<List<OptionalFundItem>>>() {
                    @Override
                    public void accept(Template<List<OptionalFundItem>> listTemplate) throws Exception {
                        List<OptionalFundItem> optionalFundItems = listTemplate.getData();
                        fundBeanList.clear();

                        for (OptionalFundItem optionalFundItem : optionalFundItems){
                            FundBean fundBean = new FundBean();
                            fundBean.setId(optionalFundItem.getFund_code());
                            fundBean.setName(optionalFundItem.getFund_name());
                            fundBeanList.add(fundBean);
                        }
                        fundListRecyclerViewAdapter.notifyDataSetChanged();
                        refresh();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void countIncome() {
//        income = 0;
//        for (int i=0;i<fundBeanList.size();i++){
//            FundBean fundBean = fundBeanList.get(i);
//            for (InsertBDBean bean : list)
//            {
//                float this_percent;
//                String str_num;
//                if(fundBean.pecent_value.charAt(0) == '-')
//                {
//                    int start = 1;
//                    int end = fundBean.pecent_value.indexOf('%');
//                    str_num = fundBean.pecent_value.substring(start,end);
//                    this_percent = Float.valueOf(str_num) * -0.01f;
//                }else
//                {
//                    int start = 0;
//                    int end = fundBean.pecent_value.indexOf('%');
//                    str_num = fundBean.pecent_value.substring(start,end);
//                    this_percent = Float.valueOf(str_num) * 0.01f;
//                }
//
//                this_percent = Math.round(this_percent * 10000);
//                this_percent = this_percent/10000.0f;
//                if(fundBean.id.equals(bean.fundid)){
//                    float this_income = this_percent * Float.valueOf(bean.money);
//                    this_income = Math.round(this_income*100);
//                    this_income = this_income/100.0f;
//                    fundBean.setIncome(this_income);
//                    fundBean.setMoney(Float.valueOf(bean.money));
//                    income += this_income;
//
//                    income = Math.round(income*100);
//                    income = income / 100.0f;
//                }
//
//
//            }
//        }
//
//        tv_income.setText(income + "");
//
//        if(income < 0.00)
//        {
//            tv_income.setTextColor(Color.GREEN);
//        }else
//        {
//            tv_income.setTextColor(Color.RED);
//        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - current > 2000)
        {
            current = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT).show();
        }else
        {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOptionalFundList("2013551128");
        refresh();
    }

    private  android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
                if(!isInTransactionTime()){
                    this.sendEmptyMessageDelayed(1,5000);
                    return;
                }
                refresh();
                this.sendEmptyMessageDelayed(1,5000);
        }
    };

    @Override
    public void onHttpCallBack(int statusCode, String content) {
        if(statusCode == 200)
        {
            FundBean bean = BaseTools.praseJsonToFundBean(content);
            if(bean != null)
                ChangeView(bean);
        }
    }

    public void getLatestInfo(){
        MyHttp.get(MainActivity.this, AppConfig.LATESTINDEXURL, new AsyncHttp.HttpListener() {
            @Override
            public void onHttpCallBack(int statusCode, String content) {
                if(statusCode == 200)
                {
                    setIndexTextView(content);
                }
            }
        },"gbk");
    }

    private boolean isInTransactionTime(){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = calendar.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = 9* 60 + 30;// 起始时间 00:20的分钟数
        final int end = 15 * 60;// 结束时间 8:00的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            return true;
        } else {
            return false;
        }
    }

    private void setIndexTextView(String content) {

        String indexStr;
        int start = content.indexOf(',')+1;
        int end = content.indexOf(',',start+1);
        indexStr = content.substring(start,end);//3539.04

        double temp = Double.parseDouble(indexStr);
        tv_info1_1.setText(String.format("%.2f",temp));

        start = end+1;
        end = content.indexOf(',',start);
        indexStr = content.substring(start,end);//14.051
        temp = Double.parseDouble(indexStr);
        tv_info1_2.setText(String.format("%.2f",temp));

        start = end+1;
        end = content.indexOf(',',start);
        indexStr = content.substring(start,end)+'%';//0.40%
        tv_info1_3.setText(indexStr);

        if(indexStr.indexOf('-') == -1)
        {
            tv_info1_1.setTextColor(Color.RED);
            tv_info1_2.setTextColor(Color.RED);
            tv_info1_3.setTextColor(Color.RED);
        }
        else
        {
            tv_info1_1.setTextColor(Color.GREEN);
            tv_info1_2.setTextColor(Color.GREEN);
            tv_info1_3.setTextColor(Color.GREEN);
        }

        start = content.indexOf("创业板指") + 5;
        end = content.indexOf(',',start);
        indexStr = content.substring(start,end);//2746.49
        temp = Double.parseDouble(indexStr);
        tv_info2_1.setText(String.format("%.2f",temp));

        start = end+1;
        end = content.indexOf(',',start);
        indexStr = content.substring(start,end);//54.333
        temp = Double.parseDouble(indexStr);
        tv_info2_2.setText(String.format("%.2f",temp));

        start = end+1;
        end = content.indexOf(',',start);
        indexStr = content.substring(start,end)+'%';//2.02%
        tv_info2_3.setText(indexStr);

        if(indexStr.indexOf('-') == -1)
        {
            tv_info2_1.setTextColor(Color.RED);
            tv_info2_2.setTextColor(Color.RED);
            tv_info2_3.setTextColor(Color.RED);
        }
        else
        {
            tv_info2_1.setTextColor(Color.GREEN);
            tv_info2_2.setTextColor(Color.GREEN);
            tv_info2_3.setTextColor(Color.GREEN);
        }
    }

    public int isRepeat(FundBean bean){
        for(int i = 0;i < fundBeanList.size();i++)
        {
            if(fundBeanList.get(i).id.equals(bean.id))
                return i;
        }
        return -1;
    }
}
