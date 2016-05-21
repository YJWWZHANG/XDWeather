package com.zqbweather.app.model9;

/**
 * Created by admin on 2016/2/5.
 */
public class County9 {
    private int id;
    private String countyName;
    private String countyWeatherCode;
    private String cityPyName;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCountyName(){
        return countyName;
    }

    public void setCountyName(String countyName){
        this.countyName = countyName;
    }

    public String getCountyWeatherCode(){
        return countyWeatherCode;
    }

    public void setCountyWeatherCode(String countyWeatherCode){
        this.countyWeatherCode = countyWeatherCode;
    }

    public String getCityPyName(){
        return cityPyName;
    }

    public void setCityPyName(String cityPyName){
        this.cityPyName = cityPyName;
    }
}
