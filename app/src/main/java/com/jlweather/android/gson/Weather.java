package com.jlweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class Weather {

    public String status;

    public Basic basic;

    public Now now;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
