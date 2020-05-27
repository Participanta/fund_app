package cn.hukecn.network;

import java.util.List;
import java.util.Map;

import cn.hukecn.bean.FundItem;
import cn.hukecn.bean.OptionalFundItem;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FundService {
    @GET("search_fund")
    Observable<Template<List<FundItem>>> searchFund(@Query("name") String name);

    @POST("add_optional_fund")
    Observable<Template<String>> addOptionalFund(@Body Map<String,Object> params);

    @GET("query_optional_fund_lists")
    Observable<Template<List<OptionalFundItem>>> getOptionalFundList(@Query("open_id") String open_id);
}
