package site.samgyeopsal.thechef.common;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import site.samgyeopsal.thechef.retrofit.AuthService;
import site.samgyeopsal.thechef.retrofit.FundingService;
import site.samgyeopsal.thechef.retrofit.ReviewService;

/**
 * @filename RetrofitManager
 * @author 최태승
 * @since 2023.03.20
 * Retrofit 라이브러리를 이용하여 REST API를 호출하기 위한 Retrofit 인스턴스를 생성
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.20   최태승        최초 생성
 * </pre>
 */


public class RetrofitManager {

    private static RetrofitManager instance;

    // RetrofitManager의 인스턴스를 생성하거나, 이미 생성된 인스턴스를 반환하는 Singleton 패턴의 getInstance 메서드
    public static RetrofitManager getInstance() {
        if (instance == null) {
            // 인스턴스, Retrofit 객체 생성
            instance = new RetrofitManager();
        }

        // 생성된 인스턴스 할당 후 반환
        return instance;
    }


    public AuthService authService;
    public ReviewService reviewService;
    public FundingService fundingService;


    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authService = retrofit.create(AuthService.class);
        reviewService = retrofit.create(ReviewService.class);
        fundingService = retrofit.create(FundingService.class);
    }
}
