package com.hznc.record;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hznc.draw.RecorderVI;
import com.hznc.nc_monitor.R;
import com.hznc.sharing.GlobalParameters;
import com.hznc.sharing.Util;
import com.hznc.sharing.Util.CodeAdapter;

public class AudioRecordActivity extends Activity{

	
	
	private LinearLayout rlayout;
    /**
	 * ��Ƶ¼�ƽ���¼�ƴ������½ǵ�ʱ����ʾ�ؼ���
	*/
    public static TextView audioRecordTimeView;
    
    /**
	 * ��Ƶ¼�ƽ������Ͻǵı����ļ���ť��
	*/
    public static ImageButton saveFile;
    
    /**
   	 * ¼�ƿ��أ�λ��¼�ƽ������·���
   	*/
    public static ImageButton audioRecordButton;

    
    /**
	 * ��Ƶ¼�ƽ����¼�ƴ��ڣ������л��Ʋ��εȡ�
	*/
    public static RecorderVI recorderVI;//���Ŵ���

    /**
	 * ������ʾ����LinearLayout���󣬰���������ʾ����ListView��
	 * @see #codesListAudioRecord
	*/
    public static LinearLayout codesAudioRecord;//������ʾ��

    /**
	 * ������ʾ����ListView��������ʾ�����ÿ�д��롣
	*/
    public static ListView codesListAudioRecord;

    /**
	 * ��¼�ƿ�ʼǰ���ڴ���ӷ�������ȡ�����ݲ�������ʾ�ڴ�����ʾ����Handler����
	*/
    public static Handler handler;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_recorder);
		
		  GlobalParameters.setCurrentActivity(this);
	        
	        
	        DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int height =metrics.heightPixels;
			int width =metrics.widthPixels;
	        rlayout = (LinearLayout)findViewById(R.id.audio_record_linear_layout);
	        rlayout.setLayoutParams(new FrameLayout.LayoutParams(width*2/5, height,Gravity.RIGHT));
	        audioRecordTimeView =(TextView)findViewById(R.id.audio_record_time_view);        //ʱ����Ϣ
	        audioRecordButton=(ImageButton)findViewById(R.id.audio_record_button);
	        audioRecordButton.setOnClickListener(AudioRecordHandler.audioRecordHandler);
	        recorderVI = (RecorderVI)findViewById(R.id.audio_record_draw);
	        recorderVI.mMax1 = 32767;
	        recorderVI.mMin1 = -32768;
	        codesListAudioRecord=(ListView)findViewById(R.id.audio_record_list_view);
	        handler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(msg.what==0){
						codesListAudioRecord.setAdapter(new CodeAdapter<String>(GlobalParameters.activity,R.layout.code_list_item,GlobalParameters.codes));
					}else{
						Toast.makeText(AudioRecordActivity.this, "G�����ȡ����", Toast.LENGTH_LONG).show();
					}
				}
			};
			Util.initRecordCodesList(handler,codesListAudioRecord);

	        codesListAudioRecord.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {}
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					int i=visibleItemCount/2;
					int currentRow=((float)visibleItemCount)/2>i?i+1:i;
					if(view.getChildAt(currentRow)!=null)view.getChildAt(currentRow).requestFocus();
				}
			});
	    }
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	// TODO Auto-generated method stub
	    	if(AudioRecordHandler.isRecording == true){
	    		AudioRecordHandler.isRecording =false;
	    		if(keyCode == KeyEvent.KEYCODE_BACK){
	    			new AlertDialog.Builder(this).setTitle("�˳�¼��").setMessage("�Ƿ񱣴���¼����Ƶ��").
	    			setPositiveButton("��", new OnClickListener(){
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					// TODO Auto-generated method stub
	    					if(GlobalParameters.codes!=null){
	    						Util.saveRecordedCodes(AudioRecordHandler.fileName.substring
	    			(0,AudioRecordHandler.fileName.indexOf(GlobalParameters.AUDIO_TEMP_FORMAT_SUFFIX)));
	    					}
	    					Toast.makeText(GlobalParameters.activity, "¼���ѱ���", Toast.LENGTH_SHORT).show();
	    					AudioRecordActivity.this.finish();
	    				}
	    			}).
	    			setNegativeButton("��", new OnClickListener(){

	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					// TODO Auto-generated method stub
	    					File file = new File(GlobalParameters.localDir, AudioRecordHandler.fileName);
	    					if(file.exists())file.delete();
	    					AudioRecordActivity.this.finish();
	    				}
	    			}).create().show();
	    		}
	    	}
	    	return super.onKeyDown(keyCode, event);
	    }
}
