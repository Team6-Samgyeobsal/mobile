package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

/**
 * @filename Member
 * @author 최태승
 * @since 2023.03.18
 * JSON 데이터와 매핑되는 Member 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.18	최태승		최초 생성
 * </pre>
 */

public class Member {

    /*
     * @SerializedName : Java 객체의 필드와 json 속성 간 매핑을 지정할 수 있게 함.
     * Gson 라이브러리에서 제공
     */

    @SerializedName("memail")
    public String memail = "";

    @SerializedName("mpassword")
    public String password = "";

    @SerializedName("mname")
    public String name = "";

    @SerializedName("mrole")
    public String role = "";

    @SerializedName("mloginType")
    public String loginType = "";

    @SerializedName("mphone")
    public String phoneNumber = "";

    @SerializedName("mmileage")
    public String mileage = "";

    @SerializedName("mprofile")
    public String mProfile ="";
}
