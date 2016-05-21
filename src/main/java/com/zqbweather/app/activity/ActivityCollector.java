package com.zqbweather.app.activity;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/2/4.
 */
public class ActivityCollector{

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
