package site.samgyeopsal.thechef.model;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("fiid")
    public String id = "";

    @SerializedName("fiurl")
    public String url = "";

    // Parent id (Image 의 parent 는 Funding)
    @SerializedName("fid")
    public String pid = "";
}
