package cn.hukecn.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {

	private static final String TAG = "LoggingInterceptor";
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request oldRequest = chain.request();
		try {
			JSONObject json = new JSONObject();
			json.put("method", oldRequest.method());
			json.put("url", oldRequest.url());
			json.put("header", oldRequest.headers());
			RequestBody body = oldRequest.body();
			if (body != null) {
				Buffer buffer = new Buffer();
				body.writeTo(buffer);
				Charset charset = Charset.forName("UTF-8");
				MediaType contentType = body.contentType();
				if (contentType != null) {
					charset = contentType.charset(charset);
				}
				String paramsStr = buffer.readString(charset);
				json.put("body", paramsStr);
			}
			Log.i(TAG, "intercept: "+json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Request.Builder requestBuilder = oldRequest.newBuilder();
		if (oldRequest.body() instanceof FormBody) {
			FormBody.Builder newFormBody = new FormBody.Builder();
			FormBody oldFormBody = (FormBody) oldRequest.body();
			for (int i = 0; i < oldFormBody.size(); i++) {
				newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
			}
			requestBuilder.method(oldRequest.method(), newFormBody.build());
		}
		Request newRequest = requestBuilder.build();
		Response response = chain.proceed(newRequest);
		MediaType mediaType = response.body().contentType();
		String body = response.body().string();
		try {
			JSONObject json = new JSONObject();
			json.put("method", response.request().method());
			json.put("url", response.request().url());
			json.put("code", response.code());
			json.put("headers", response.headers());
			try {
				JSONObject jsonBody = new JSONObject(body);
				json.put("body", jsonBody);
			} catch (JSONException e) {
				e.printStackTrace();
				json.put("body", body);
			}
			Log.i(TAG, "intercept: "+json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Response newResponse = response.newBuilder().body(ResponseBody.create(mediaType, body)).build();
		return newResponse;
	}
}
