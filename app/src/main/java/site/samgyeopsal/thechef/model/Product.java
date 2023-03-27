package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class Product {

    @SerializedName("fpid")
    public String id = "";

    @SerializedName("fporigin_price")
    public int originPrice = 0;

    @SerializedName("fpprice")
    public int price = 0;

    @SerializedName("fptitle")
    public String title = "";

    @SerializedName("fpcontent")
    public String content = "";

    @SerializedName("options")
    public List<Option> options = Collections.emptyList();
}
