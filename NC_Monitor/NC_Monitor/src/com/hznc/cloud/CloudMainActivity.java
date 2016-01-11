package com.hznc.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hznc.nc_monitor.R;

public class CloudMainActivity extends ActionBarActivity implements OnItemClickListener{
	private String[] titles=new String[]{"文件上传","文件下载"};
	private int[] imageIds= new int[]{R.drawable.cloud_upload,R.drawable.cloud_download};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
//		将上面定义的内容与布局文件activity_main_menu_item中的ID相关联
		List<Map<String, Object>> listItems= new ArrayList<Map<String,Object>>();
		for (int i = 0; i < titles.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("header", imageIds[i]);
			listItem.put("item", titles[i]);
			listItems.add(listItem);
		}
//		创建一个ArrayAdapter
		SimpleAdapter simpleAdapter= new SimpleAdapter(CloudMainActivity.this, listItems, R.layout.activity_main_menu_item, 
				new String[]{"header","item"}, new int[]{R.id.ammi_header,R.id.ammi_item});
		ListView list =(ListView) findViewById(R.id.amm_list);
		list.setAdapter(simpleAdapter);
		
//		创建每一项的点击时间
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		try {
			switch (position) {
			case 0:

				break;
			case 1:

			
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
