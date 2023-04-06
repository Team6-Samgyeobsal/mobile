package site.samgyeopsal.thechef.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * @filename Order
 * @author 최태승
 * @since 2023.03.29
 * JSON 데이터와 매핑되는 Order 클래스를 정의
 *
 * <pre>
 * 수정일        	수정자       			수정내용
 * ----------  --------    ---------------------------
 * 2023.03.29	최태승		최초 생성
 * </pre>
 */

public class Order implements Parcelable {

    @SerializedName("poid")
    public String poId = "";

    public int amount = 0;

    @SerializedName("pooption")
    public String poOption = "";

    @SerializedName("fpid")
    public String fpId = "";

    @SerializedName("fid")
    public String fId = "";

    @SerializedName("fpprice")
    public int fpPrice = 0;

    @SerializedName("fporigin_price")
    public int fpOriginPrice = 0;

    @SerializedName("fptitle")
    public String fpTitle = "";

    @SerializedName("fpcontent")
    public String fpContent = "";

    @SerializedName("qid")
    public String qid = "";


    public Order() {}

    protected Order(Parcel in) {
        poId = in.readString();
        amount = in.readInt();
        poOption = in.readString();
        fpId = in.readString();
        fId = in.readString();
        fpPrice = in.readInt();
        fpOriginPrice = in.readInt();
        fpTitle = in.readString();
        fpContent = in.readString();
        qid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poId);
        dest.writeInt(amount);
        dest.writeString(poOption);
        dest.writeString(fpId);
        dest.writeString(fId);
        dest.writeInt(fpPrice);
        dest.writeInt(fpOriginPrice);
        dest.writeString(fpTitle);
        dest.writeString(fpContent);
        dest.writeString(qid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
