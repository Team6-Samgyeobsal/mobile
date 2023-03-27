package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

public class Option {

    @SerializedName("poid")
    public String id = "";

    @SerializedName("pooption")
    public String option = "";

    // Parent id (Option 의 parent 는 Product)
    @SerializedName("fpid")
    public String pid = "";
}
