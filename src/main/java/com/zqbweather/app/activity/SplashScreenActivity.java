package com.zqbweather.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.zqbweather.app.R;

public class SplashScreenActivity extends ActivityBase{
	
	private final int SPLASH_DISPLAY_LENGHT = 2000;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent chooseIntent = new Intent(SplashScreenActivity.this, ChooseAreaActivity9.class);
				startActivity(chooseIntent);
				finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}
}
