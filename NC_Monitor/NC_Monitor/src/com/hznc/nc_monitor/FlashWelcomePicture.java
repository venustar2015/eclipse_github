package com.hznc.nc_monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class FlashWelcomePicture extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_welcome_picture);
		
//		�������棬ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                 
                WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		
//		�����̣߳������ȴ�1.5�룬����MainActivity
		Thread thread=new Thread(){
			@Override
			public void run() {
				super.run(); 
				try {
					sleep(1500);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					Intent intent=new Intent("android.intent.action.MAIN_ACTIVITY");
					startActivity(intent);
				}
			}
		};
		
		thread.start();
	}
	
//	��������Activity������ͣ״̬ʱ������Activity
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
