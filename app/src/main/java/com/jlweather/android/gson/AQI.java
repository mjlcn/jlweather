package com.jlweather.android.gson;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
