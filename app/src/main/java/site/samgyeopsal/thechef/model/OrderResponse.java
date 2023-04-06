package site.samgyeopsal.thechef.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * @filename OrderResponse
 * @author 최태승
 * @since 2023.03.29
 * JSON 데이터와 매핑되는 OrderResponse 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.29	최태승		최초 생성
 * </pre>
 */

public class OrderResponse implements Parcelable {

    @SerializedName("oid")
    public String oId = "";

    @SerializedName("ophone")
    public String oPhone = "";

    @SerializedName("omemo")
    public String oMemo = null;

    @SerializedName("oused_mileage")
    public int oUsedMileage = 0;

    @SerializedName("oorigin_price")
    public int oOriginPrice = 0;

    @SerializedName("oprice")
    public int oPrice = 0;

    @SerializedName("ostatus")
    public String oStatus = "";

    @SerializedName("odate")
    public long oDate = 0;

    @SerializedName("memail")
    public String mEmail = "";

    @SerializedName("pmcode")
    public String pmCode = "";

    @SerializedName("qrused_date")
    public String qrUsedDate = null;

    @SerializedName("cpid")
    public String cpId = "";

    @SerializedName("fstore_name")
    public String fStoreName = "";

    @SerializedName("ftitle")
    public String fTitle = "";

    @SerializedName("fsummary")
    public String fSummary = "";

    @SerializedName("fstory")
    public String fStory = "";

    @SerializedName("fdate")
    public long fDate = 0;

    @SerializedName("fstatus")
    public String fStatus = "";

    @SerializedName("ctid")
    public String ctId = "";

    @SerializedName("ctname")
    public String ctName = "";

    @SerializedName("mname")
    public String mName = "";

    @SerializedName("odateString")
    public String oDateString = "";

    @SerializedName("qid")
    public String qid = "";
    public List<Order> orders = Collections.emptyList();

    public OrderResponse(){}

    protected OrderResponse(Parcel in) {
        oId = in.readString();
        oPhone = in.readString();
        oMemo = in.readString();
        oUsedMileage = in.readInt();
        oOriginPrice = in.readInt();
        oPrice = in.readInt();
        oStatus = in.readString();
        oDate = in.readLong();
        mEmail = in.readString();
        pmCode = in.readString();
        qrUsedDate = in.readString();
        cpId = in.readString();
        fStoreName = in.readString();
        fTitle = in.readString();
        fSummary = in.readString();
        fStory = in.readString();
        fDate = in.readLong();
        fStatus = in.readString();
        ctId = in.readString();
        ctName = in.readString();
        mName = in.readString();
        orders = in.createTypedArrayList(Order.CREATOR);
        oDateString = in.readString();
        qid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(oId);
        dest.writeString(oPhone);
        dest.writeString(oMemo);
        dest.writeInt(oUsedMileage);
        dest.writeInt(oOriginPrice);
        dest.writeInt(oPrice);
        dest.writeString(oStatus);
        dest.writeLong(oDate);
        dest.writeString(mEmail);
        dest.writeString(pmCode);
        dest.writeString(qrUsedDate);
        dest.writeString(cpId);
        dest.writeString(fStoreName);
        dest.writeString(fTitle);
        dest.writeString(fSummary);
        dest.writeString(fStory);
        dest.writeLong(fDate);
        dest.writeString(fStatus);
        dest.writeString(ctId);
        dest.writeString(ctName);
        dest.writeString(mName);
        dest.writeTypedList(orders);
        dest.writeString(oDateString);
        dest.writeString(qid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderResponse> CREATOR = new Creator<OrderResponse>() {
        @Override
        public OrderResponse createFromParcel(Parcel in) {
            return new OrderResponse(in);
        }

        @Override
        public OrderResponse[] newArray(int size) {
            return new OrderResponse[size];
        }
    };
}
