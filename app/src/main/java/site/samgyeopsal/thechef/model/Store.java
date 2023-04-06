package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * @filename Store
 * @author 최태승
 * @since 2023.04.03
 * JSON 데이터와 매핑되는 Store 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.04.03	최태승		최초 생성
 * </pre>
 */
public class Store {

    @SerializedName("fid")
    public String sid ="";

    @SerializedName("fstore_name")
    public String storeName = "";

    @SerializedName("ftitle")
    public String stitle = "";

    @SerializedName("totalprice")
    public long totalPrice = 0;

    @SerializedName("fthumb")
    public String sThumbUrl = "";

    @SerializedName("expire")
    public int expire = 0;

    @SerializedName("fstatus")
    public String fStatus = "";

    @SerializedName("fstory")
    public String story = "";

    @SerializedName("fdate")
    public long fDate = 0;

    @SerializedName("end_date")
    public String endDate = "";

    @SerializedName("ctid")
    public String ctid = "";

    @SerializedName("ctname")
    public String ctName = "";

    @SerializedName("tid")
    public String tId = "";

    @SerializedName("tname")
    public String tName = "";

    @SerializedName("memail")
    public String mEmail = "";

    @SerializedName("totalemail")
    public int totalEmail = 0;

    @SerializedName("rfcount")
    public int rfCount = 0;

    @SerializedName("rscount")
    public int rsCount = 0;

    @SerializedName("products")
    public List<Product> products = Collections.emptyList();

    @SerializedName("imgs")
    public List<Image> images = Collections.emptyList();


    public String getSid(){ return sid;}
    public String getStoreName(){return storeName;}
    @Override
    public String toString(){
        return this.sThumbUrl +", "+ storeName;
    }

}
