package com.zqbweather.app.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by admin on 2016/2/4.
 */
public class ActivityBase extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.activities.add(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.activities.remove(this);
    }
}
