package cn.hukecn.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 所有Activity的基类
 */
public abstract class AppBaseActivity extends AppCompatActivity {

    public static final String TAG = AppBaseActivity.class.getSimpleName();

    private Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getLayoutId() > 0){
            setContentView(getLayoutId());
        }
        mUnBinder = ButterKnife.bind(this, this);
        StatusBarUtil.setTransparent(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public int getLayoutId(){
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
