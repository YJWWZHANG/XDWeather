package com.zqbweather.app.activity;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zqbweather.app.R;
import com.zqbweather.app.customview.RefreshableView;
import com.zqbweather.app.service.AutoUpdateService;
import com.zqbweather.app.util.DialogTool;
import com.zqbweather.app.util.HttpCallbackListener;
import com.zqbweather.app.util.HttpUtil;
import com.zqbweather.app.util.SAXUtil;
import com.zqbweather.app.util.Utility;
import com.zqbweather.app.util.WeatherUrlUtil;
import com.zqbweather.app.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity9 extends ActivityBase implements OnClickListener{



    private LinearLayout weatherInfoLayout;
    private RefreshableView refreshableView;
    private DrawerLayout drawerLayout;

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView tempNowText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button openDrawer;

    private TextView tomorrowTempDay;
    private TextView tomorrowTempNight;
    private TextView sunriseSunset;
    private TextView tomorrowTTempDay;
    private TextView tomorrowTTempNight;

    private ListView drawer;
    private DrawerAdapter drawerAdapter;

    private String countyName;
    private String cityPyName;
    private String countyWeatherCode;

    private List<String> drawerList = new ArrayList<String>();

    int[] dayImages = new int[]{
            R.drawable.day00,
            R.drawable.day01,
            R.drawable.day02,
            R.drawable.day03,
            R.drawable.day04,
            R.drawable.day05,
            R.drawable.day06,
            R.drawable.day07,
            R.drawable.day08,
            R.drawable.day09,
            R.drawable.day10,
            R.drawable.day11,
            R.drawable.day12,
            R.drawable.day13,
            R.drawable.day14,
            R.drawable.day15,
            R.drawable.day16,
            R.drawable.day17,
            R.drawable.day18,
            R.drawable.day19,
            R.drawable.day20,
            R.drawable.day21,
            R.drawable.day22,
            R.drawable.day23,
            R.drawable.day24,
            R.drawable.day25,
            R.drawable.day26,
            R.drawable.day27,
            R.drawable.day28,
            R.drawable.day29,
            R.drawable.day30,
            R.drawable.day31,
            R.drawable.day53,
            R.drawable.dayundefined,
    };

    int[] nightImages = new int[]{
            R.drawable.night00,
            R.drawable.night01,
            R.drawable.night02,
            R.drawable.night03,
            R.drawable.night04,
            R.drawable.night05,
            R.drawable.night06,
            R.drawable.night07,
            R.drawable.night08,
            R.drawable.night09,
            R.drawable.night10,
            R.drawable.night11,
            R.drawable.night12,
            R.drawable.night13,
            R.drawable.night14,
            R.drawable.night15,
            R.drawable.night16,
            R.drawable.night17,
            R.drawable.night18,
            R.drawable.night19,
            R.drawable.night20,
            R.drawable.night21,
            R.drawable.night22,
            R.drawable.night23,
            R.drawable.night24,
            R.drawable.night25,
            R.drawable.night26,
            R.drawable.night27,
            R.drawable.night28,
            R.drawable.night29,
            R.drawable.night30,
            R.drawable.night31,
            R.drawable.night53,
    };


    public static void actionStart(Context context, String countyName, String cityPyName, String countyWeatherCode){
        Intent intent = new Intent(context, WeatherActivity9.class);
        intent.putExtra("county_name", countyName);
        intent.putExtra("city_pyname", cityPyName);
        intent.putExtra("county_weather_code", countyWeatherCode);
        context.startActivity(intent);;
    }


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (ListView) findViewById(R.id.right_drawer);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        tempNowText = (TextView) findViewById(R.id.temp_now);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        openDrawer = (Button) findViewById(R.id.open_drawer);
        tomorrowTempDay = (TextView) findViewById(R.id.tomorrow_temp_day);
        tomorrowTempNight = (TextView) findViewById(R.id.tomorrow_temp_night);
        sunriseSunset = (TextView) findViewById(R.id.sunrise_sunset);
        tomorrowTTempDay = (TextView) findViewById(R.id.tomorrow_t_temp_day);
        tomorrowTTempNight = (TextView) findViewById(R.id.tomorrow_t_temp_night);


        initDrawer();
        drawerAdapter = new DrawerAdapter(WeatherActivity9.this, R.layout.drawer_item, drawerList);
        drawer.setAdapter(drawerAdapter);
        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = drawerList.get(position);
                if ("设置".equals(item)) {
//                    LayoutInflater.from(WeatherActivity9.this).inflate(R.layout.dialog_setting, null);
                    DialogTool.createRandomDialog(WeatherActivity9.this, "设置", "确定", "", null, null,
                            LayoutInflater.from(WeatherActivity9.this).inflate(R.layout.dialog_setting,
                                    null), 0, false).show();
                }
                if ("关于".equals(item)) {
                    DialogTool.createMessageDialog(WeatherActivity9.this,
                            "心动天气", "版本：2.0.0", "确定", null, 0, true).show();
                }

            }
        });
        ((Switch) LayoutInflater.from(WeatherActivity9.this).inflate(R.layout.dialog_setting,
                null).findViewById(R.id.switch_update)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(MyApplication.getContext(), "后台更新已开启",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MyApplication.getContext(), "后台更新已关闭",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        countyName = getIntent().getStringExtra("county_name");
        cityPyName = getIntent().getStringExtra("city_pyname");
        countyWeatherCode = getIntent().getStringExtra("county_weather_code");
        if(!(TextUtils.isEmpty(countyName) || TextUtils.isEmpty(cityPyName) ||
                TextUtils.isEmpty(countyWeatherCode))) {
            SharedPreferences.Editor editor = MyApplication.getContext().
                    getSharedPreferences("flash.weather.com.cn_wmaps_xml_china", Context.MODE_PRIVATE).edit();
            editor.putString("county_name", countyName);
            editor.putString("city_pyname", cityPyName);
            editor.putString("county_weather_code", countyWeatherCode);
            editor.commit();
        }

        if(!TextUtils.isEmpty(countyWeatherCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            tempNowText.setVisibility(View.INVISIBLE);
            queryWeatherInfo9();
            queryWeatherInfo(countyWeatherCode);
        }else{
            showWeather();
        }

        switchCity.setOnClickListener(this);
        openDrawer.setOnClickListener(this);

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步中...");
                    }
                });

                SystemClock.sleep(1000);
                queryWeatherInfo9();
                SharedPreferences prefs1 = MyApplication.getContext().getSharedPreferences(
                        "flash.weather.com.cn_wmaps_xml_china", MODE_PRIVATE);
                String weatherCode = prefs1.getString("county_weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch(view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity9.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.open_drawer:
//                Toast.makeText(MyApplication.getContext(), "打开抽屉", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
            default:
                break;
        }
    }


    private void initDrawer(){
        drawerList.add("");
        drawerList.add("设置");
        drawerList.add("关于");
    }

    private void queryWeatherInfo9(){
        SharedPreferences prefs1 = MyApplication.getContext().getSharedPreferences(
                "flash.weather.com.cn_wmaps_xml_china", MODE_PRIVATE);
        String address = "http://flash.weather.com.cn/wmaps/xml/" + prefs1.getString("city_pyname", "") + ".xml";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {

                SharedPreferences prefs1 = MyApplication.getContext().getSharedPreferences(
                        "flash.weather.com.cn_wmaps_xml_china", MODE_PRIVATE);
                String weatherCode = prefs1.getString("county_weather_code", "");
                SAXUtil.saxHandle(weatherCode, response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode){
        String address = WeatherUrlUtil.getInterfaceURL(weatherCode, "forecast_v");
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                    Utility.handleWeatherResponse(response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }




    private void showWeather() {
        // TODO Auto-generated method stub
        SharedPreferences prefs1 = MyApplication.getContext().getSharedPreferences(
                "flash.weather.com.cn_wmaps_xml_china", MODE_PRIVATE);
        currentDateText.setText(prefs1.getString("current_date", ""));
        cityNameText.setText(prefs1.getString("county_name", ""));
        temp1Text.setText(prefs1.getString("tem2", "") + "℃");
        temp2Text.setText(prefs1.getString("tem1", "") + "℃");
        weatherDespText.setText(prefs1.getString("stateDetailed", ""));
        publishText.setText("今天" + prefs1.getString("time", "")
                + "发布");
        if("暂无实况".equals(prefs1.getString("temNow", ""))){
            tempNowText.setVisibility(View.INVISIBLE);
        }else {
            tempNowText.setVisibility(View.VISIBLE);
        }
        tempNowText.setText(prefs1.getString("temNow", "") + "℃");
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        tempNowText.setVisibility(View.VISIBLE);

        SharedPreferences prefs2 = MyApplication.getContext().getSharedPreferences(
                "open.weather.com.cn_data", MODE_PRIVATE);
        tomorrowTempDay.setText(prefs2.getString("tomorrow_temp_night", "")  + "℃");
        tomorrowTempNight.setText(prefs2.getString("tomorrow_temp_day", "")  + "℃");
        sunriseSunset.setText(prefs2.getString("sunrise_sunset", ""));
        tomorrowTTempDay.setText(prefs2.getString("tomorrow_t_temp_night", "")  + "℃");
        tomorrowTTempNight.setText(prefs2.getString("tomorrow_t_temp_day", "")  + "℃");

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("是否退出？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
            }
        });
        dialog.setNeutralButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whitch) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
