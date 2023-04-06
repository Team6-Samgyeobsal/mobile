package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

/**
 * @filename ReviewCount
 * @author 최태승
 * @since 2023.03.29
 * JSON 데이터와 매핑되는 ReviewCount 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.29	최태승		최초 생성
 * </pre>
 */
public class ReviewCount {

    @SerializedName("rfcount")
    public int rfCount = 0;

    @SerializedName("rscount")
    public int rsCount = 0;
}
