package com.jlweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp_max")
    public String tmpMax;

    @SerializedName("tmp_min")
    public String tmpMin;

    @SerializedName("cond_txt_d")
    public String condInfoDay;

    @SerializedName("cond_txt_n")
    public String condInfoNight;
}
