package site.samgyeopsal.thechef.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import site.samgyeopsal.thechef.model.Store;

/**
 * @author 최태승
 * @filename FundingService
 * @since 2023.03.25
 * 입점 점포 정보 HTTP GET 요청을 정의하는 Retrofit API 인터페이스
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.03.22   최태승        최초생성
 * </pre>
 */

public interface StoreService {

    @GET("/api/store/{fid}")
    Call<Store> getStore(@Path("fid") String sid);
}
