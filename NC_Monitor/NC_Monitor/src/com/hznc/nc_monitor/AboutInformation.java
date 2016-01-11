package com.hznc.nc_monitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

public class AboutInformation extends ActionBarActivity{

	WebView about;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_information);
		
		about=(WebView) findViewById(R.id.ai_webView);
		about.loadUrl("file:///android_asset/about_inf.html");
	}
}
