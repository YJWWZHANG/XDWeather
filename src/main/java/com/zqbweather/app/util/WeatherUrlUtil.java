package com.zqbweather.app.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class WeatherUrlUtil {
    private final static String TAG = "WeatherUrlUtil";  
    private static final String MAC_NAME = "HmacSHA1";  
    private static final String ENCODING = "UTF-8";  
    private static final String appid = "ea9af9b4e30a5b2b";  
    private static final String private_key = "dc2d15_SmartWeatherAPI_0242a41";  
    private static final String url_header="http://open.weather.com.cn/data/?";  
  

    private static byte[] HmacSHA1Encrypt(String url, String privatekey)  
            throws Exception {  
        byte[] data = privatekey.getBytes(ENCODING);  

        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);  

        Mac mac = Mac.getInstance(MAC_NAME);  

        mac.init(secretKey);  
        byte[] text = url.getBytes(ENCODING);  

        return mac.doFinal(text);  
    }  

    private static String getKey(String url, String privatekey) throws Exception {  
        byte[] key_bytes = HmacSHA1Encrypt(url, privatekey);  
        String base64encoderStr = Base64.encodeToString(key_bytes, Base64.NO_WRAP);  
        return URLEncoder.encode(base64encoderStr, ENCODING);  
    }  

    private static String getInterfaceURL(String areaid,String type,String date) throws Exception{  
        String keyurl=url_header+"areaid="+areaid+"&type="+type+"&date="+date+"&appid=";  
        String key=getKey(keyurl+appid,private_key);  
        String appid6 = appid.substring(0, 6);  
          
        return keyurl+appid6+"&key=" + key;  
    }  
      
    public static String getInterfaceURL(String areaid,String type){  
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");  
        String date = dateFormat.format(new Date());  
        //String type="forecast3d";//"index";//"forecast3d";"observe"  
        try {  
            return getInterfaceURL(areaid,type,date);  
        } catch (Exception e) {  
            Log.e(TAG, e.getMessage(),e.fillInStackTrace());  
        }  
        return null;  
    }  
}
