package com.zqbweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2016/2/4.
 */
public class XDWeatherOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_PROVINCE =
            "create table Province (" +
                    "id integer primary key autoincrement, " +
                    "province_name text, " +
                    "province_pyname text)";

    private static final String CREATE_CITY =
            "create table City " +
                    "(id integer primary key autoincrement, " +
                    "city_name text, " +
                    "city_pyname text," +
                    "province_pyname text)";

    private static final String CREATE_COUNTY =
            "create table County " +
                    "(id integer primary key autoincrement, " +
                    "county_name text, " +
                    "county_weather_code text, " +
                    "city_pyname text)";
    public XDWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);;
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int lodVersion){

    }
}
