package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;
/**
 * @filename OrderUser
 * @author 최태승
 * @since 2023.04.03
 * JSON 데이터와 매핑되는 OrderUser 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.04.03	최태승        최초 생성
 * 2023.04.06   최태승        알림 추가
 * </pre>
 */

public class OrderUser {

    @SerializedName("mname")
    public String mName = "";

    @SerializedName("qdate")
    public long qDate =0;

    @SerializedName("qid")
    public String qid ="";

    @SerializedName("oid")
    public String oid = "";

    @SerializedName("fstore_name")
    public String storeName = "";

    @Override
    public String toString(){
        return mName+", "+qDate+", "+qid;
    }
}
