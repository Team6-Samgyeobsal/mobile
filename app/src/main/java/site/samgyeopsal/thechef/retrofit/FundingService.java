package site.samgyeopsal.thechef.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import site.samgyeopsal.thechef.model.Funding;

/**
 * @author 최태승
 * @filename ReviewService
 * @since 2023.03.25
 * 펀딩 가게에 대한 정보를 가져오는 HTTP GET 요청을 정의하는 Retrofit API 인터페이스
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
public interface FundingService {
    @GET("/api/store/{id}")
    Call<Funding> getFunding(@Path("id") long id);
}
