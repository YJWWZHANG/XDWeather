package com.zqbweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.zqbweather.app.activity.MyApplication;
import com.zqbweather.app.model9.City9;
import com.zqbweather.app.model9.County9;
import com.zqbweather.app.model9.Province9;
import com.zqbweather.app.model9.XDWeatherDB;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;



/**
 * Created by admin on 2016/2/8.
 */
public class SAXUtil9 extends DefaultHandler{


    private static String level;
    private static String provincePyName;
    private static String cityPyName;


    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException{
        if(level.equals("province")) {
            if("city".equals(localName)) {
                if(!("xisha".equals(attributes.getValue((1)))
                        || "nanshadao".equals(attributes.getValue(1))
                        || "diaoyudao".equals(attributes.getValue(1)))) {
                    Province9 province = new Province9();
                    province.setProvinceName(attributes.getValue(0));
                    province.setProvincePyName(attributes.getValue(1));
                    XDWeatherDB.xdWeatherDB.saveProvince(province);
                }
            }
        }
        if(level.equals("city")){
            if(!"city".equals(localName)) {
                provincePyName = localName;
            }else {
                City9 city = new City9();
                city.setCityName(attributes.getValue(2));
                city.setCityPyName(attributes.getValue(5));
                city.setProvincePyName(provincePyName);
                XDWeatherDB.xdWeatherDB.saveCity(city);
            }
        }
        if(level.equals("county")){
            if(!"city".equals(localName)){
                cityPyName = localName;
            }
            else {
                County9 county = new County9();
                county.setCountyName(attributes.getValue(2));
                county.setCityPyName(cityPyName);
                county.setCountyWeatherCode(attributes.getValue(17));
                XDWeatherDB.xdWeatherDB.saveCounty(county);
            }
        }
        if(level.equals("location")){
            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(
                    "location_info", Context.MODE_PRIVATE).edit();
            if(MyApplication.getContext().getSharedPreferences("location_info", Context.MODE_PRIVATE)
                    .getString("city", "").equals(attributes.getValue(2))) {
                editor.putString("county_name", attributes.getValue(2));
                editor.putString("county_weather_code", attributes.getValue(17));
                editor.commit();
            }
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException{

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException{

    }

    @Override
    public void endDocument()throws SAXException{

    }

    public static boolean saxHandle(String level, String response){
        SAXUtil9.level = level;
        try{
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new SAXUtil9();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(response)));
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
