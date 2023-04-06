package site.samgyeopsal.thechef.retrofit;


import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import site.samgyeopsal.thechef.model.Review;
import site.samgyeopsal.thechef.model.ReviewResponse;

/**
 * @filename ReviewService
 * @author 최태승
 * @since 2023.03.22
 * 펀딩 리뷰에 대한 정보를 가져오는 HTTP GET 요청을 정의하는 Retrofit API 인터페이스
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.03.22   최태승        최초생성
 * </pre>
 */

/*
 * getReviews() : 리뷰 조회, 정렬 및 필터링
 */
public interface ReviewService {
    @GET("/api/funding/review")
    Call<ReviewResponse> getReviews(
            @Query("fid") String sid, // 펀딩 id
            @Query("sort") int sort, // 리뷰 정렬
            @Query("type") String type // 리뷰 타입
    );

    @POST("/api/funding/{fid}/product/replyReview")
    Call<String> replyToReview(
            @Path("fid") String sid,
            @Body RequestBody requestBody
    );
}
