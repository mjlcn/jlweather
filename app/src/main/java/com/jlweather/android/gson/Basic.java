package com.jlweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class Basic {

    @SerializedName("location")
    public String locationName;

    @SerializedName("cid")
    public String weatherId;
}
