package com.zqbweather.app.model9;

/**
 * Created by admin on 2016/2/5.
 */
public class City9 {
    private int id;
    private String cityName;
    private String cityPyName;
    private String provincePyName;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCityName(){
        return cityName;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }

    public String getCityPyName(){
        return cityPyName;
    }

    public void setCityPyName(String cityPyName){
        this.cityPyName = cityPyName;
    }

    public String getProvincePyName(){
        return provincePyName;
    }

    public void setProvincePyName(String provincePyName){
        this.provincePyName = provincePyName;
    }
}
