package site.samgyeopsal.thechef.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesItem implements Parcelable {

    @SerializedName("mname")
    public String mName ="";

    @SerializedName("qid")
    public String qId = "";

    @SerializedName("fid")
    public String fId = "";

    @SerializedName("qdate")
    public long qDate = 0;

    @SerializedName("fstore_name")
    public String fStoreName = "";

    @SerializedName("oid")
    public String oId = "";

    @SerializedName("oprice")
    public long oPrice = 0;

    @SerializedName("list")
    private List<OrderList> list;

    public static class OrderList {
        @SerializedName("fptitle")
        private String fpTitle;

        @SerializedName("fpid")
        private String fpId;

        public String getFpTitle() {
            return fpTitle;
        }

        public String getFpId() {
            return fpId;
        }

        // Getters and Setters
    }


    @Expose(serialize = false, deserialize = false)
    public OrderResponse details = null;

    public SalesItem(){}

    protected SalesItem(Parcel in){
        mName = in.readString();
        qId = in.readString();
        fId = in.readString();
        qDate = in.readLong();
        fStoreName = in.readString();
        oId = in.readString();
        details = in.readParcelable(OrderResponse.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mName);
        dest.writeString(qId);
        dest.writeString(fId);
        dest.writeLong(qDate);
        dest.writeString(fStoreName);
        dest.writeString(oId);
        dest.writeParcelable(details,flags);
    }

    @Override
    public int describeContents(){return 0;}

    public static final Creator<SalesItem> CREATOR = new Creator<SalesItem>() {
        @Override
        public SalesItem createFromParcel(Parcel in) {
            return new SalesItem(in);
        }

        @Override
        public SalesItem[] newArray(int size) {
            return new SalesItem[size];
        }
    };

    @Override
    public String toString(){
        return mName+","+qDate+","+qId;
    }

    public List<OrderList> getList() {
        return list;
    }

}
