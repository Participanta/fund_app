package cn.hukecn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.hukecn.fund.AppConfig;
import cn.hukecn.fund.AsyncHttp;
import cn.hukecn.fund.BaseTools;
import cn.hukecn.fund.DataBaseHelper;
import cn.hukecn.fund.FundBean;
import cn.hukecn.fund.InsertBDBean;
import cn.hukecn.fund.MyDataBase;
import cn.hukecn.fund.MyHttp;
import cn.hukecn.fund.R;

public class AddActivity extends AppCompatActivity implements AsyncHttp.HttpListener {

    EditText et_id = null,et_money = null;
    Button btn_add = null;
    //BaseNetWork baseNetWork = null;
    InsertBDBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        et_id = (EditText) findViewById(R.id.edt_id);
        et_money = (EditText) findViewById(R.id.edt_money);
        btn_add = (Button) findViewById(R.id.btn_add);

        //baseNetWork = BaseNetWork.getInstance(this);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                String money = et_money.getText().toString();
                if(id.length() == 6)
                {
                    bean = new InsertBDBean();
                    bean.fundid = id;
                    bean.money = money;
                    MyDataBase db = DataBaseHelper.getInstance().getDataBase();
                    MyHttp.get(AddActivity.this, AppConfig.BASEURL+bean.fundid+".js?rt="+System.currentTimeMillis(),AddActivity.this,"utf-8");

                    //baseNetWork.getFundInfo(bean.fundid, AddActivity.this);
                }
                else
                {
                    Toast.makeText(AddActivity.this,"代码输入有误，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onDataListener(FundBean bean) {
        MyDataBase db = DataBaseHelper.getInstance().getDataBase();
        this.bean.fundname = bean.name;
        if(db.insert(this.bean) != -1)
        {
            Toast.makeText(AddActivity.this, "成功添加:"+bean.name+",共"+this.bean.money+"元", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onHttpCallBack(int statusCode, String content) {
        if(statusCode == 200)
        {
            FundBean bean = BaseTools.praseJsonToFundBean(content);
            if(bean != null)
            {
                onDataListener(bean);
            }
        }
    }
}
