package site.samgyeopsal.thechef.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;
/**
 * @filename Funding
 * @author 최태승
 * @since 2023.03.27
 * JSON 데이터와 매핑되는 Funding 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.27	최태승		최초 생성
 * </pre>
 */
public class Funding {

    @SerializedName("fstore_name")
    public String storeName = "";

    @SerializedName("fsummary")
    public String summary = "";

    @SerializedName("ftitle")
    public String title = "";

    @SerializedName("totalprice")
    public long totalPrice = 0;

    @SerializedName("totalemail")
    public int totalEmail = 0; // 참여한 사람 수

    @SerializedName("fid")
    public String id = "";

    @SerializedName("tname")
    public String tName = ""; // 더현대 이름

    @SerializedName("ctname")
    public String ctName = "";

    @SerializedName("fstory")
    public String story = "";

    @SerializedName("fdate")
    public long date = 0;

    @SerializedName("end_date")
    public String endDate = "";

    @SerializedName("fstatus")
    public String status = "";

    @SerializedName("fstore_score")
    public double storeScore = 0;

    @SerializedName("ffunding_score")
    public double fundingScore = 0;

    @SerializedName("expire")
    public int expire = 0;

    @SerializedName("rfcount")
    public int rfCount = 0;

    @SerializedName("rscount")
    public int rsCount = 0;

    @SerializedName("products")
    public List<Product> products = Collections.emptyList();

    @SerializedName("imgs")
    public List<Image> images = Collections.emptyList();

    @SerializedName("fthumb")
    public String fThumbUrl = "";


    @Override
    public String toString(){
        return this.fThumbUrl +", "+ storeName;
    }
}
