package com.zqbweather.app.activity;



import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zqbweather.app.R;
import com.zqbweather.app.model9.City9;
import com.zqbweather.app.model9.County9;
import com.zqbweather.app.model9.Province9;
import com.zqbweather.app.model9.XDWeatherDB;
import com.zqbweather.app.util.BaiduLocation;
import com.zqbweather.app.util.HttpCallbackListener;
import com.zqbweather.app.util.HttpUtil;
import com.zqbweather.app.util.SAXUtil9;

public class ChooseAreaActivity9 extends ActivityBase{

    public static final int LEVEL_PROVINCE9 = 0;
    public static final int LEVEL_CITY9 = 1;
    public static final int LEVEL_COUNTY9 = 2;


    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button location;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private XDWeatherDB xdWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province9> province9List;
    private List<City9> city9List;
    private List<County9> county9List;

    private Province9 selectedProvince9;
    private City9 selectedCity9;

    private int currentLevel;

    private boolean isFromWeatherActivity;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra(
                "from_weather_activity", false);
        SharedPreferences prefs = MyApplication.getContext().
                getSharedPreferences("open.weather.com.cn_data", MODE_PRIVATE);
        if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity9.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.choose_area);

        titleText = (TextView) findViewById(R.id.title_text);
        location = (Button) findViewById((R.id.location));
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,
                R.layout.area_item, dataList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String areaName = getItem(position);
//				 TextView textView = (TextView) super.getView(position, convertView, parent);
                View view = LayoutInflater.from(getContext()).inflate(R.layout.area_item, null);
                TextView textView = (TextView) view.findViewById(R.id.area_name);
                textView.setText(areaName);
//				 textView.setTextColor(getResources().getColor(R.color.white));

                return textView;
            }
        };
        listView.setAdapter(adapter);

        xdWeatherDB = XDWeatherDB.getIntance();

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationProgressDialog();
                BaiduLocation.getInstance().requestLocation();
                String address = "http://flash.weather.com.cn/wmaps/xml/" + MyApplication.getContext().
                        getSharedPreferences("location_info", MODE_PRIVATE).
                        getString("city_pyname", "") +
                        ".xml";
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        if (SAXUtil9.saxHandle("county", response)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(MyApplication.getContext(),
//                                            response, Toast.LENGTH_LONG).show();
                                    SAXUtil9.saxHandle("location", response);
                                    WeatherActivity9.actionStart(ChooseAreaActivity9.this,
                                            MyApplication.getContext().getSharedPreferences("location_info",
                                                    MODE_PRIVATE).getString("county_name", ""),
                                            MyApplication.getContext().getSharedPreferences("location_info",
                                                    MODE_PRIVATE).getString("city_pyname", ""),
                                            MyApplication.getContext().getSharedPreferences("location_info",
                                                    MODE_PRIVATE).getString("county_weather_code", ""));
                                    clossLocationProgressDialog();
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index,
                                    long arg3){
                if(currentLevel == LEVEL_PROVINCE9){
                    selectedProvince9 = province9List.get(index);
                    if("beijing".equals(selectedProvince9.getProvincePyName())
                            || "tianjin".equals(selectedProvince9.getProvincePyName())
                            || "shanghai".equals(selectedProvince9.getProvincePyName())
                            || "chongqing".equals(selectedProvince9.getProvincePyName())
                            || "xianggang".equals(selectedProvince9.getProvincePyName())
                            || "aomen".equals(selectedProvince9.getProvincePyName())
                            || "taiwan".equals(selectedProvince9.getProvincePyName())){
                        queryzhiCounties();
                    }
                    else {
                        queryCities();
                    }
                }else if(currentLevel == LEVEL_CITY9){
                    selectedCity9 = city9List.get(index);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY9){
                    String countyName = county9List.get(index).getCountyName();
                    String cityPyName = county9List.get(index).getCityPyName();
                    String countyWeatherCode = county9List.get(index).getCountyWeatherCode();
                    WeatherActivity9.actionStart(ChooseAreaActivity9.this, countyName, cityPyName,
                            countyWeatherCode);
                    finish();
                }
            }
        });
        queryProvince();

    }

    private void queryProvince(){
        province9List = xdWeatherDB.loadProvince();
        if(province9List.size() > 0){
            dataList.clear();
            for(Province9 province : province9List){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE9;
        }else{
            queryFromServer(null, "province");
        }
    }

    private void queryCities(){
        city9List = xdWeatherDB.loadCity(selectedProvince9.getProvincePyName());
        if (city9List.size() > 0) {
            dataList.clear();
            for (City9 city : city9List) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince9.getProvinceName());
            currentLevel = LEVEL_CITY9;
        } else {
                queryFromServer(selectedProvince9.getProvincePyName(), "city");
        }
    }

    private void queryzhiCounties(){

        county9List = xdWeatherDB.loadCounty(selectedProvince9.getProvincePyName());
        if(county9List.size() > 0){
            dataList.clear();
            for(County9 county : county9List){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince9.getProvinceName());
            currentLevel = LEVEL_COUNTY9;
        }else{
            queryFromServer(selectedProvince9.getProvincePyName(), "county");
        }
    }

    private void queryCounties(){

        county9List = xdWeatherDB.loadCounty(selectedCity9.getCityPyName());
        if(county9List.size() > 0){
            dataList.clear();
            for(County9 county : county9List){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity9.getCityName());
            currentLevel = LEVEL_COUNTY9;
        }else{
            queryFromServer(selectedCity9.getCityPyName(), "county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://flash.weather.com.cn/wmaps/xml/" + code +
                    ".xml";
        }else{
            address = "http://flash.weather.com.cn/wmaps/xml/china.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

            @Override
            public void onFinish(String response) {
                // TODO Auto-generated method stub
                boolean result = false;
                if("province".equals(type)){
                    result = SAXUtil9.saxHandle("province", response);
                }else if("city".equals(type)){
                    result = SAXUtil9.saxHandle("city", response);
                }else if("county".equals(type)){
                    result = SAXUtil9.saxHandle("county", response);
                }
                if(result){
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                if("beijing".equals(selectedProvince9.getProvincePyName())
                                        || "tianjin".equals(selectedProvince9.getProvincePyName())
                                        || "shanghai".equals(selectedProvince9.getProvincePyName())
                                        || "chongqing".equals(selectedProvince9.getProvincePyName())
                                        || "xianggang".equals(selectedProvince9.getProvincePyName())
                                        || "aomen".equals(selectedProvince9.getProvincePyName())
                                        || "taiwan".equals(selectedProvince9.getProvincePyName())){
                                    queryzhiCounties();
                                }else {
                                    queryCounties();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity9.this, "加载失败",
                                Toast.LENGTH_LONG).show();
                    }

                });
            }

        });
    }

    private void showProgressDialog() {
        // TODO Auto-generated method stub
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        // TODO Auto-generated method stub
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showLocationProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在定位...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void  clossLocationProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        if(currentLevel == LEVEL_COUNTY9){
            queryCities();
        }else if(currentLevel == LEVEL_CITY9){
            queryProvince();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this, WeatherActivity9.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
