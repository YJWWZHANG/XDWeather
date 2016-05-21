package com.zqbweather.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zqbweather.app.activity.ChooseAreaActivity9;
import com.zqbweather.app.activity.MyApplication;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;


public class BaiduLocation {
	
	public static BaiduLocation baiduLocation;

	private LocationClient mLocationClient = null;
	private MyLocationListenner myListener = new MyLocationListenner();
	
	private BaiduLocation(){

		mLocationClient = new LocationClient(MyApplication.getContext());
		mLocationClient.registerLocationListener(myListener);
	}
	
	public synchronized static BaiduLocation getInstance(){
		if(baiduLocation == null){
			baiduLocation = new BaiduLocation();
		}
		return baiduLocation;
	}

	private void stopLocation(){
		if(mLocationClient != null && mLocationClient.isStarted()){
			mLocationClient.stop();
		}
	}

	public void requestLocation(){
		setLocationOption();
		if(mLocationClient != null && !mLocationClient.isStarted()){
			mLocationClient.start();
		}
		if(mLocationClient != null && mLocationClient.isStarted()){
			mLocationClient.requestLocation();
		}
	}
	
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
	}

	private class MyLocationListenner implements BDLocationListener{
		@Override
		public void onReceiveLocation(BDLocation location){
			if(location.getAddrStr() == null){
				Toast.makeText(MyApplication.getContext(), "定位失败", Toast.LENGTH_LONG).show();
				return;
			}
			saveLocationInfo(location);
			Toast.makeText(MyApplication.getContext(), "位置：" + location.getAddrStr(),
					Toast.LENGTH_LONG).show();
		}
		
		
		@Override
		public void onReceivePoi(BDLocation poiLocation){
//			if(poiLocation == null){
//				Toast.makeText(MyApplication.getContext(), "定位失败", Toast.LENGTH_LONG).show();
//				return;
//			}
//			saveLocationInfo(poiLocation);
//			Toast.makeText(MyApplication.getContext(), "位置：" + poiLocation.getAddrStr(),
//					Toast.LENGTH_LONG).show();
		}
	}


	private void saveLocationInfo(BDLocation bdLocation){
		SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(
				"location_info", Context.MODE_PRIVATE).edit();
		editor.putString("time", bdLocation.getTime());
		editor.putString("error_code", Integer.toString(bdLocation.getLocType()));
		editor.putString("latitude", Double.toString(bdLocation.getLatitude()));
		editor.putString("longitude", Double.toString(bdLocation.getLongitude()));
		editor.putString("radius", Float.toString(bdLocation.getRadius()));
		editor.putString("province",bdLocation.getProvince());
		editor.putString("city_code", bdLocation.getCityCode());
		editor.putString("city", bdLocation.getCity());
		editor.putString("district", bdLocation.getDistrict());
		editor.putString("street", bdLocation.getStreet());
		editor.putString("address", bdLocation.getAddrStr());
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		try {
			String cityPyName = PinyinHelper.toHanyuPinyinStringArray(
					bdLocation.getCity().charAt(0), outputFormat)[0] +
					PinyinHelper.toHanyuPinyinStringArray(
					bdLocation.getCity().charAt(1), outputFormat)[0];
			editor.putString("city_pyname", cityPyName);
		}catch (Exception e){
			e.printStackTrace();
		}

		editor.commit();
	}
}
