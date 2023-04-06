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
public class Option {

    @SerializedName("poid")
    public String id = "";

    @SerializedName("pooption")
    public String option = "";

    // Parent id (Option 의 parent 는 Product)
    @SerializedName("fpid")
    public String pid = "";
}
