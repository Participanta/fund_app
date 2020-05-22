package cn.hukecn.fund;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FundDetailActivity extends AppCompatActivity {
    LineChart mLineChar;
    LineDataSet set1;
    ArrayList<Entry> values = new ArrayList<Entry>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_detail);
        mLineChar = (LineChart) findViewById(R.id.mLineChar);
        initCharts();
        initChartDatas();
    }

    private void initChartDatas() {
        String url = null;
        Intent intent = getIntent();
        if(intent != null)
        {
            String fundid = intent.getStringExtra("fundid");
            String fundname = intent.getStringExtra("fundname");
            if(fundid == null || fundname == null)
            {
                Toast.makeText(getApplicationContext(),"System Error...",Toast.LENGTH_LONG).show();
                return;
            }

            url = "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code="+fundid+"&page=1&per=30&sdate=&edate=&rt=0.4070765394717455";
        }

        MyHttp.get(this, url,new AsyncHttp.HttpListener() {
            @Override
            public void onHttpCallBack(int statusCode, String content) {
                //info.setText(statusCode+"\n"+content);
                if(statusCode == 200)
                {
                    List<HistoryNetBean> list =praseHtml(content);
                    if (list.size() > 0)
                    {
                        for (HistoryNetBean historyNetBean : list){
                            values.add(new Entry(convert2long(historyNetBean.date), Float.parseFloat(historyNetBean.value)));
                        }

                        Collections.sort(values, new Comparator<Entry>() {
                            @Override
                            public int compare(Entry entry, Entry t1) {
                                float len1 = entry.getX();
                                float len2 = t1.getX();
                                return (len1 - len2 > 0) ? 1 : -1;
                            }
                        });
                        //设置数据
                        setData(values);
                        //默认动画
                        mLineChar.animateX(2500);
                        //刷新
                        mLineChar.invalidate();
                        // 得到这个文字
                        Legend l = mLineChar.getLegend();
                        // 修改文字 ...
                        l.setForm(Legend.LegendForm.LINE);
                        //传递数据集
                    }
                }
            }
        },"gbk");



    }
    private void setData(ArrayList<Entry> values) {
        if (mLineChar.getData() != null && mLineChar.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChar.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChar.getData().notifyDataChanged();
            mLineChar.notifyDataSetChanged();
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = new LineDataSet(values, "近一个月走势");

            // 在这里设置线
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // 填充背景只支持18以上
                //Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                //set1.setFillDrawable(drawable);
                set1.setFillColor(Color.RED);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //添加数据集
            dataSets.add(set1);

            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);

            //谁知数据
            mLineChar.setData(data);
        }
    }


    private void initCharts() {
//        //设置手势滑动事件
//        mLineChar.setOnChartGestureListener(this);
//        //设置数值选择监听
//        mLineChar.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChar.setDrawGridBackground(false);
        //设置描述文本
        mLineChar.getDescription().setEnabled(false);
        //设置支持触控手势
        mLineChar.setTouchEnabled(true);
        //设置缩放
        mLineChar.setDragEnabled(true);
        //设置推动
        mLineChar.setScaleEnabled(true);
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChar.setPinchZoom(true);
    }

    private List<HistoryNetBean> praseHtml(String content) {
        List<HistoryNetBean> list = new ArrayList<>();
        String tbody = null;
        int start = -1;
        int end = -1;

        start = content.indexOf("<tbody>");
        end = content.indexOf("</tbody>");
        if(start == -1 || end == -1)
            return list;
        tbody = content.substring(start,end);

        String tr = null;

        int tr_start = 0;
        int tr_end  = 0;
        while(true)
        {
            HistoryNetBean bean = new HistoryNetBean();
            tr_start = tbody.indexOf("<tr>",tr_end);
            tr_end = tbody.indexOf("</tr>",tr_start);
            if(tr_start <= 0 || tr_end <= 0)
                break;

            tr = tbody.substring(tr_start,tr_end);

            start = tr.indexOf("<td>") + 4;
            end = tr.indexOf("</td>");
            if (start ==-1||end == -1)
                break;
            bean.date = tr.substring(start,end);

            start = tr.indexOf("bold")+6;
            end = tr.indexOf("</td>",start);
            if (start ==-1||end == -1)
                break;
            bean.value = tr.substring(start,end);

            start = tr.indexOf("bold",end)+6;
            end = tr.indexOf("</td>",start);

            start = tr.indexOf("bold",end);
            start = tr.indexOf(">",start)+1;
            end = tr.indexOf("</td>",start)-1;
            String strFloat = tr.substring(start,end);
            bean.percent = Float.parseFloat(strFloat);
            list.add(bean);
        }
        return list;
    }
   public static String DATE_YYYY_MM_DD = "yyyy-MM-dd";

    public long convert2long(String date) {
        try {
            if (!TextUtils.isEmpty(date)) {
                SimpleDateFormat sf = new SimpleDateFormat(DATE_YYYY_MM_DD);
                return sf.parse(date).getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
