package cn.hukecn.network

import cn.hukecn.bean.FundItem
import cn.hukecn.bean.OptionalFundItem
import cn.hukecn.fund.FundBean
import io.reactivex.Observable
import retrofit2.http.*

interface FundService {
    @GET("search_fund")
    fun searchFund(@Query("name") name: String?): Observable<Template<List<FundItem>>>

    @POST("add_optional_fund")
    fun addOptionalFund(@Body params: Map<String, Any?>): Observable<Template<String>>

    @DELETE("delete_optional_fund")
    fun deleteOptionalFund(@Query("open_id") open_id: String?, @Query("fund_code") fund_code: String?): Observable<Template<String>>

    @POST("update_optional_fund")
    fun updateOptionalFund(@Body params: MutableMap<String, Any?>): Observable<Template<String>>

    @GET("query_optional_fund_lists")
    fun getOptionalFundList(@Query("open_id") open_id: String?): Observable<Template<List<OptionalFundItem>>>

    @GET("get_fund_valuation")
    fun getFundValuation(@Query("fund_code") fund_code: String?): Observable<Template<FundBean>>
}