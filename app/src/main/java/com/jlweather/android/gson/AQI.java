package com.jlweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class AQI {

    @SerializedName("air_now_city")
    public AQICity aqiCityity;

    public String status;

    public class AQICity{

        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
    }
}
