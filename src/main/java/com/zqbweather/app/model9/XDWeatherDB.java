package com.zqbweather.app.model9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zqbweather.app.activity.MyApplication;
import com.zqbweather.app.db.XDWeatherOpenHelper;
import com.zqbweather.app.model9.Province9;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/2/5.
 */
public class XDWeatherDB {
    public static final String DB_NAME = "xd_weather";
    public static final int VERSION = 1;

    public static XDWeatherDB xdWeatherDB;
    private SQLiteDatabase db;

    private XDWeatherDB(Context context){
        XDWeatherOpenHelper dbHelper = new XDWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static XDWeatherDB getIntance(){
        if(xdWeatherDB == null){
            xdWeatherDB = new XDWeatherDB(MyApplication.getContext());
        }
        return xdWeatherDB;
    }

    public void saveProvince(Province9 province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_pyname", province.getProvincePyName());
            db.insert("Province", null, values);
        }
    }

    public void saveCity(City9 city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_pyname", city.getCityPyName());
            values.put("province_pyname", city.getProvincePyName());
            xdWeatherDB.db.insert("City", null, values);

        }
    }

    public void saveCounty(County9 county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_weather_code", county.getCountyWeatherCode());
            values.put("city_pyname", county.getCityPyName());
            db.insert("County", null, values);
        }
    }

    public List<Province9> loadProvince(){
        List<Province9> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Province9 province = new Province9();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvincePyName(cursor.getString(cursor.getColumnIndex("province_pyname")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public List<City9> loadCity(String provincePyName){
        List<City9> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_pyname = ?", new String[]{provincePyName}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                City9 city = new City9();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));;
                city.setCityPyName(cursor.getString(cursor.getColumnIndex("city_pyname")));
                city.setProvincePyName(cursor.getString(cursor.getColumnIndex("province_pyname")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public List<County9> loadCounty(String cityPyName){
        List<County9> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_pyname = ?", new String[]{cityPyName}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                County9 county = new County9();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyWeatherCode(cursor.getString(cursor.getColumnIndex("county_weather_code")));
                county.setCityPyName(cursor.getString(cursor.getColumnIndex("city_pyname")));
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
