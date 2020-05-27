package cn.hukecn.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitServiceManager {

	//超时时间5s
	private static final int DEFAULT_TIME_OUT = 5;
	private static final int DEFAULT_READ_TIME_OUT = 10;
	private Retrofit mRetrofit;
	private static LoggingInterceptor loggingInterceptor;

	private RetrofitServiceManager(String baseUrl, HttpCommonInterceptor commonInterceptor) {
		//创建OKHttpClient
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		//连接超时时间
		builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
		//写操作,超时时间
		builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
		//读操作,超时时间
		builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
		builder.addInterceptor(commonInterceptor);
		if(loggingInterceptor == null){
			loggingInterceptor = new LoggingInterceptor();
		}
		builder.addInterceptor(loggingInterceptor);
		// 创建Retrofit
		mRetrofit = new Retrofit.Builder()
				.client(builder.build())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(new StringConverterFactory())
				.addConverterFactory(GsonConverterFactory.create())
				.baseUrl(baseUrl)
				.build();
	}



	/**
	 * 获取RetrofitServiceManager
	 *
	 * @return
	 */
	static RetrofitServiceManager newInstance(String baseUrl,HttpCommonInterceptor commonInterceptor) {
		return new RetrofitServiceManager(baseUrl,commonInterceptor);
	}


	/**
	 * 获取RetrofitServiceManager
	 *
	 * @return
	 */
	static RetrofitServiceManager newInstance(String baseUrl) {
		HttpCommonInterceptor.Builder interceptorBuilder = new HttpCommonInterceptor.Builder()
				.addHeaderParams("Content-Type", "application/json ");
		return newInstance(baseUrl,interceptorBuilder.build());
	}

	/**
	 * 获取对应的Service
	 *
	 * @param service Service 的 class
	 * @param <T>
	 * @return
	 */
	public <T> T create(Class<T> service) {
		return mRetrofit.create(service);
	}



}
