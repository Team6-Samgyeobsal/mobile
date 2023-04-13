package site.samgyeopsal.thechef.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.SalesItem;
/**
 * @author 최태승
 * @filename SalesService
 * @since 2023.04.09
 * 판매 정보 HTTP GET 요청을 정의하는 Retrofit API 인터페이스
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.04.09   최태승        최초생성
 * </pre>
 */
public interface SalesService {
    @GET("/api/queue/saleslist")
    Call<List<SalesItem>> getSalesList(@Query("fid") String sId);

    @GET("/api/order/{oId}")
    Call<OrderResponse> getSalesDetails(@Path("oId") String oId);
}
