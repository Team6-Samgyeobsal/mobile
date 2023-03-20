package site.samgyeopsal.thechef.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import site.samgyeopsal.thechef.model.User;

/**
 * @filename AuthService
 * @author 최태승
 * @since 2023.03.20
 * API 서버의 인증(Authentication) API를 호출하기 위한 인터페이스
 *
 * <pre>
 * 수정일        	수정자       	수정내용
 * ----------  --------    ---------------------------
 * 2023.03.20   최태승        최초생성
 * </pre>
 */

/*
 * signIn() : HTTP POST 메서드를 사용하여 API 서버에 요청을 보내 로그인
 *            return 값으로 User 객체를 json 형태로 반환(ResquestBody)
 */
public interface AuthService {
    @POST("/api/account/login")
    Call<User> signIn(@Body RequestBody body);
}
