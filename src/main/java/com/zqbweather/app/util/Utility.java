package com.zqbweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zqbweather.app.activity.MyApplication;
public class Utility {


	
//	public static void handleWeatherResponse(Context context, String response){
//		try{
//			JSONObject jsonObject = new JSONObject(response);
//			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
//			String cityName = weatherInfo.getString("city");
//			String weatherCode = weatherInfo.getString("cityid");
//			String temp1 = weatherInfo.getString("temp1");
//			String temp2 = weatherInfo.getString("temp2");
//			String weatherDesp = weatherInfo.getString("weather");
//			String publishTime = weatherInfo.getString("ptime");
//			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
//					weatherDesp, publishTime);
//		}catch(JSONException e){
//			e.printStackTrace();
//		}
//	}
	
	public static void handleWeatherResponse(String response){
		try{			
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("c");
			String cityName = weatherInfo.getString("c3");
			String cityPyName = weatherInfo.getString("c4");
			String weatherCode = weatherInfo.getString("c1");
			
			
			JSONObject weatherInfo1 = jsonObject.getJSONObject("f");
			String publishTime = weatherInfo1.getString("f0");
			
			String f1 = weatherInfo1.getString("f1");
			JSONArray jsonArray = new JSONArray(f1);
			JSONObject weatherInfo3 = jsonArray.getJSONObject(0);
			String temp1 = weatherInfo3.getString("fc");
			String temp2 = weatherInfo3.getString("fd");
			String sunriseSet = weatherInfo3.getString("fi");
			JSONObject weatherInfo4 = jsonArray.getJSONObject(1);
			String tomorrowTempDay = weatherInfo4.getString("fc");
			String tomorrowTempNight = weatherInfo4.getString("fd");
			JSONObject weatherInfo5 = jsonArray.getJSONObject(2);
			String tomorrowTTempDay = weatherInfo5.getString("fc");
			String tomorrowTTempNight = weatherInfo5.getString("fd");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
					Locale.CHINA);
//		SharedPreferences.Editor editor = PreferenceManager
//				.getDefaultSharedPreferences(MyApplication.getContext()).edit();
			SharedPreferences.Editor editor = MyApplication.getContext().
					getSharedPreferences("open.weather.com.cn_data", Context.MODE_PRIVATE).edit();
			editor.putBoolean("city_selected", true);
			editor.putString("city_name", cityName);
			editor.putString("citypy_name", cityPyName);
			editor.putString("weather_code", weatherCode);
			editor.putString("temp1", temp1);
			editor.putString("temp2", temp2);
			editor.putString("sunrise_sunset", sunriseSet);
			editor.putString("publish_time", publishTime);
			editor.putString("current_date", sdf.format(new Date()));
			editor.putString("tomorrow_temp_day", tomorrowTempDay);
			editor.putString("tomorrow_temp_night", tomorrowTempNight);
			editor.putString("tomorrow_t_temp_day", tomorrowTTempDay);
			editor.putString("tomorrow_t_temp_night", tomorrowTTempNight);
			editor.commit();
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

}
