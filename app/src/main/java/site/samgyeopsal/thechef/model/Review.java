package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

/**
 * @filename Review
 * @author 최태승
 * @since 2023.03.22
 * JSON 데이터와 매핑되는 Review 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.22	최태승		최초 생성
 * </pre>
 */
public class Review {

    @SerializedName("rscore")
    public int score = 0;

    @SerializedName("rtype")
    public String type = "";

    @SerializedName("rimg_rul")
    public String imageUrl = null;

    @SerializedName("memail")
    public String email = "";

    @SerializedName("rdate")
    public String date ="";

    @SerializedName("rcontent")
    public String content = "";
}
