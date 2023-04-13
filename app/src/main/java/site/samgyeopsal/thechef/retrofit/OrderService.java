package site.samgyeopsal.thechef.retrofit;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import site.samgyeopsal.thechef.model.OrderResponse;
import site.samgyeopsal.thechef.model.OrderUser;

/**
 * @filename OrderService
 * @author 최태승
 * @since 2023.03.29
 * 주문 정보를 가져오는 HTTP GET 요청을 정의하는 Retrofit API 인터페이스
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.03.29   최태승        최초생성
 * 2023.04.08   최태승        알람 기능 추가
 * </pre>
 */
public interface OrderService {
    @GET("/api/order/{oid}")
    Call<OrderResponse> getOrder(@Path("oid") String oid);

    //region 함수 추가
    @POST("/api/queue/insertQueue")
    Call<String> insertQueue(@Body RequestBody requestBody);

    @GET("/api/queue/list")
    Call<List<OrderUser>> getQueueList(@Query("fid") String sid);

    @POST("/api/queue/useQrCode")
    Call<String>  useQrCode(@Body RequestBody requestBody);

    @POST("/api/common/message")
    Call<String> sendNotification(@Body RequestBody requestBody);
}
