package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

/**
 * @filename User
 * @author 최태승
 * @since 2023.03.18
 * JSON 데이터와 매핑되는 User 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */
public class User {

    /*
     * @SerializedName : Java 객체의 필드와 json 속성 간 매핑을 지정할 수 있게 함.
     * Gson 라이브러리에서 제공
     */

    @SerializedName("member")
    public Member member = null;

    @SerializedName("accessToken")
    public String accessToken = "";

    @SerializedName("refreshToken")
    public String refreshToken = "";

    @SerializedName("store")
    public Store store = null;
}
