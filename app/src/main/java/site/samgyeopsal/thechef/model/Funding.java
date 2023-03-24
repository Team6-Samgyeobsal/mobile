package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Funding {

    @SerializedName("fstore_name")
    public String storeName = "";

    @SerializedName("fsummary")
    public String fsummary = "";

    @SerializedName("ftitle")
    public String ftitle = "";

    @SerializedName("totalprice")
    public long totalPrice = 0;

    @SerializedName("totalemail")
    public int totalEmail = 0; // 참여한 사람 수

    @SerializedName("fid")
    public String fid = "";

    @SerializedName("tname")
    public String tname = ""; // 더현대 이름

    @SerializedName("ctname")
    public String ctname = "";

    @SerializedName("fstory")
    public String fstory = "";

    @SerializedName("fdate")
    public String fdate = "";

    @SerializedName("end_date")
    public String endDate = "";

    @SerializedName("fstatus")
    public String fstatus = "";

    @SerializedName("fstore_score")
    public double fstore_score = 0;

    @SerializedName("ffunding_score")
    public double ffunding_score = 0;


}
