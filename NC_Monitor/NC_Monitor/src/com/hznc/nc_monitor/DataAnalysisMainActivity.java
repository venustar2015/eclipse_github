package com.hznc.nc_monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hznc.cloud.CloudMainActivity;
import com.hznc.record.AudioRecordActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class DataAnalysisMainActivity extends ActionBarActivity implements OnItemClickListener{

//	��������ͼ�������
	private String[] titles=new String[]{"��Ƶ����","��Ƶ����","�񶯷���"};
	private int[] imageIds= new int[]{R.drawable.audio_play,R.drawable.video_play,R.drawable.signal_analysis };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
//		�����涨��������벼���ļ�activity_main_menu_item�е�ID�����
		List<Map<String, Object>> listItems= new ArrayList<Map<String,Object>>();
		for (int i = 0; i < titles.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("header", imageIds[i]);
			listItem.put("item", titles[i]);
			listItems.add(listItem);
		}
//		����һ��ArrayAdapter
		SimpleAdapter simpleAdapter= new SimpleAdapter(this, listItems, R.layout.activity_main_menu_item, 
				new String[]{"header","item"}, new int[]{R.id.ammi_header,R.id.ammi_item});
		ListView list =(ListView) findViewById(R.id.amm_list);
		list.setAdapter(simpleAdapter);
		
//		����ÿһ��ĵ��ʱ��
		list.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent=null;
		try {
			switch (position) {
			case 0:
		
				break;
			case 1:

				break;
			case 2:
				break;
		
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