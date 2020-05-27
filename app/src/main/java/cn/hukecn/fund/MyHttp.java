package cn.hukecn.fund;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

/**
 * Created by Kelson on 2016/1/6.
 */
public class MyHttp {
    static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, final AsyncHttp.HttpListener listener, final String charset){
        client.setTimeout(10000);
        client.setThreadPool(AppThreadPool.getInstance().getPool());
        client.setResponseTimeout(10000);
        client.get(context, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    if(listener != null && bytes != null)
                        listener.onHttpCallBack(i,new String(bytes,charset));
                } catch (UnsupportedEncodingException e) {}
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //
            }
        });
    }
}
