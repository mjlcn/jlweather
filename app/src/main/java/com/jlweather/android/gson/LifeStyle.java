package com.jlweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/11 0011.
 */

public class LifeStyle {

    public LS ls;

    public class LS{
        @SerializedName("brf")
        public String lsBrf;

        @SerializedName("txt")
        public String lsTxt;

        @SerializedName("type")
        public String lsType;
    }
}
