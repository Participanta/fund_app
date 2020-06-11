package cn.hukecn.network

import cn.hukecn.bean.NewsDetail
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("ClientNews")
    fun getFundNews(@Query("id") id: String?): Observable<List<NewsDetail>>
}