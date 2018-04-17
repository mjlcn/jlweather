package com.jlweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jlweather.android.gson.AQI;
import com.jlweather.android.gson.Forecast;
import com.jlweather.android.gson.Weather;
import com.jlweather.android.service.AutoUpdateService;
import com.jlweather.android.util.HttpUtil;
import com.jlweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    //title.xml
    private TextView titleCity;
    //now.xml
    private TextView degreeText;
    private TextView weatherInfoText;

    //forecast.xml
    private LinearLayout forecastLayout;


    //aqi.xml

    private TextView aqiText;
    private TextView pm25Text;
    //
    private ImageView bingPicImg;

    //
    public SwipeRefreshLayout swipeRefresh;

    //
    public DrawerLayout drawerLayout;
    public Button navButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);

        //
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

        //
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = prefs.getString("weather",null);
        final String weatherId;

        String aqiString = prefs.getString("aqi",null);

        if(weatherString != null && aqiString !=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            AQI aqi = Utility.handleAQIResponse(aqiString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
            showAQIInfo(aqi);
        }else {
            //
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        //
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        //
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
    }

    public void  requestWeather(final String weatherId){

        requestWeatherData(weatherId);
        requestAQIData(weatherId);

        loadBingPic();

    }

    public void requestWeatherData(final String weatherId){
        final String userKey = "3cd2fbbf24684b45acc8527551227cc1";

        String weatherUrl = "https://free-api.heweather.com/s6/weather?"
                +"location="+weatherId
                +"&"+"key="+userKey;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "Failed to connect server",Toast.LENGTH_SHORT).show();
//                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("Response//ppz", responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,
                                    "Failed to get weather message --- ",Toast.LENGTH_SHORT).show();
                        }
//                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
    }

    public void requestAQIData(final String weatherId){
        //AQI
        final String userKey = "3cd2fbbf24684b45acc8527551227cc1";
        String aqiUrl = "https://free-api.heweather.com/s6/air?"
                +"location="+weatherId
                +"&"+"key="+userKey;
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "Failed to connect server",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("Response//ppz", responseText);
                final AQI aqi = Utility.handleAQIResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aqi != null && "ok".equals(aqi.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("aqi",responseText);
                            editor.apply();
                            showAQIInfo(aqi);

                        }else {
                            Toast.makeText(WeatherActivity.this,
                                    "Failed to get aqi message --- ",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void showAQIInfo(AQI aqi){
        String aqiInfo = aqi.aqiCityity.aqi;
        String pm25Info = aqi.aqiCityity.pm25;

        aqiText.setText(aqiInfo);
        pm25Text.setText(pm25Info);

    }

    private void showWeatherInfo(Weather weather){
        if(weather !=null && "ok".equals(weather.status)){

            String locationName = weather.basic.locationName;
            String degreee = weather.now.temperature + "℃";
            String weatherInfo = weather.now.condInfo;

            titleCity.setText(locationName);
            degreeText.setText(degreee);
            weatherInfoText.setText(weatherInfo);

            forecastLayout.removeAllViews();
            for(Forecast forecast: weather.forecastList){
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);

                TextView dateText = (TextView)view.findViewById(R.id.date_text);
                TextView infoDayText = (TextView)view.findViewById(R.id.infoDay_text);
                TextView infoNightText = (TextView)view.findViewById(R.id.infoNight_text);
                TextView maxText = (TextView)view.findViewById(R.id.max_text);
                TextView minText = (TextView)view.findViewById(R.id.min_text);

                String maxTmp = forecast.tmpMax+"°";
                String minTmp = forecast.tmpMin+"°";
                dateText.setText(forecast.date);
                infoDayText.setText(forecast.condInfoDay);
                infoNightText.setText(forecast.condInfoNight);
                maxText.setText(maxTmp);
                minText.setText(minTmp);

                forecastLayout.addView(view);
            }

            weatherLayout.setVisibility(View.VISIBLE);

            //
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);

        }else {
            Toast.makeText(WeatherActivity.this,
                    "Failed to get message",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("Bing_Pic//ppz", "onFailure: --- ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });



    }

}
