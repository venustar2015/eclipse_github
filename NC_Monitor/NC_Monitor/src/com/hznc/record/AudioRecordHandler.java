package com.hznc.record;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.hznc.nc_monitor.R;
import com.hznc.sharing.CodeHandler;
import com.hznc.sharing.GlobalParameters;
import com.hznc.sharing.JNILibrary;
import com.hznc.sharing.Util;


/**
 * 该类用于音频播放界面功能实现和事件处理�??
 * @see com.example.record.AudioRecordActivity
 */
public class AudioRecordHandler implements SurfaceHolder.Callback,View.OnClickListener{
	
	/**
	 * 用于采集音频信号的AudioRecord对象�?
	*/
    private static AudioRecord audioRecord;
    
    /**
	 * 采样频率�?
	*/
	private static int frequency=GlobalParameters.audioRecordSamplingRate;
	
	/**
	 * 采样通道配置,单�?�道�?
	*/
    private static int channelConfiguration= AudioFormat.CHANNEL_CONFIGURATION_MONO;
    
    /**
	 * 采集数据点的位数�?16bit�?
	*/
	private static int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;
	
	/**
	 * 缓存{@link #audioRecord}对象采集的数据的数组大小�?
	*/
    private static int bufferSize;
    
    /**
	 * 用于同步当前录制代码行的Handler对象�?
	 * @see android API android.os.Handler
	 */
    private static Handler audioRecordCodesHandler;
    
    /**
	 * 用于实时绘制当前音频波形、频谱�?�谱阵的Handler对象�?
	 * @see android API android.os.Handler
	 */
    private static Handler audioRecordDrawHandler;
    
    /**
	 * 录制状�?�标识�??
	 */
    public static boolean isRecording=false;
    
    /**
	 * 绘制状�?�标识�??
	 */
	private static boolean drawFinished=false;

    /**
	 * 录制�?始时间�??
	 */
    private static long recordStartTime=0;

    /**
	 * 该类的唯�?实例�?<br />
	 * 注：该类构�?�方法为私有。这是简化的单例设计模式�?
	 */
    public static AudioRecordHandler audioRecordHandler=new AudioRecordHandler();
    

    /**
	 * 绘图次数，或采样次数�?
	 */
    private static int time=0;

    /**
	 * 保存的文件名�?
	 */
    public static String fileName;
    private AudioRecordHandler() {
    	audioRecordCodesHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if(msg.what>1000000)return;
        	if(msg.what==-100){
        		Toast.makeText(GlobalParameters.activity, "行号获取错误", Toast.LENGTH_LONG).show();
        	}else{
				int firstPosition=AudioRecordActivity.codesListAudioRecord.getFirstVisiblePosition();
				int lastPosition=AudioRecordActivity.codesListAudioRecord.getLastVisiblePosition();
				int count=lastPosition-firstPosition+1;
				if(firstPosition==0&&msg.what<=count/2){
					Log.e("firstPosition", msg.what+"");
					AudioRecordActivity.codesListAudioRecord.getChildAt(msg.what).requestFocus();
				}else if(lastPosition==AudioRecordActivity.codesListAudioRecord.getCount()-1&&msg.what>=lastPosition-count/2){
					Log.e("lastPosition", msg.what+"");
					AudioRecordActivity.codesListAudioRecord.getChildAt(msg.what-firstPosition).requestFocus();
				}else{/*
					Log.e("first:", firstPosition+"");
					Log.e("last:", lastPosition+"");
					Log.e("count:", count+"");
					Log.e("num:", AudioRecordActivity.codesListAudioRecord.getCount()+"");
					Log.e("row:", msg.what+"");*/
                	AudioRecordActivity.codesListAudioRecord.setSelection(msg.what-count/2-1);
                	AudioRecordActivity.codesListAudioRecord.smoothScrollToPosition(msg.what-count/2);
				}
            	if(GlobalParameters.codes!=null&&GlobalParameters.codes.length>msg.what){
            		String time=Util.getFormatTime2((int)(System.currentTimeMillis()-recordStartTime));
            		CodeHandler.timeTag.add("["+time+"]"+" "+msg.what);
            	}
        	}
        }
    };
        audioRecordDrawHandler=new Handler(){
              public void handleMessage(Message msg){
            	  short[] shortBuffer=(short[]) msg.obj;
                  Util.RdrawWave(AudioRecordActivity.recorderVI, shortBuffer, time, 2);
                  AudioRecordActivity.audioRecordTimeView.setText("时长:"+Util.getFormatTime((int)(System.currentTimeMillis()-recordStartTime)));
                  setDrawFinished(true);
              }
        };
    }

    /**
	 * 执行该方法后�?始录制音频�??<br />
	 * 该方法内完成了录制前的一些初始化操作，并�?启了三个线程�?<br />
	 * {@link AudioDataHandler}<br />
	 * {@link Mp3Encoder}<br />
	 * {@link CodeHandler.RecordCodeProvider}
	 */
    public void recordAudio(){
        CodeHandler.timeTag.clear();
        isRecording=true;
        bufferSize=2*AudioRecord.getMinBufferSize(frequency,channelConfiguration,audioEncoding);
        audioRecord=new AudioRecord(MediaRecorder.AudioSource.MIC,frequency,channelConfiguration,audioEncoding,bufferSize);
        audioRecord.startRecording();
        List<short[]> data=Collections.synchronizedList(new ArrayList<short[]>());
        new Thread(new AudioDataHandler(data)).start();
        new Thread(new Mp3Encoder(data)).start();
        recordStartTime=System.currentTimeMillis();
        CodeHandler.isRecording=true;
        CodeHandler.handler=audioRecordCodesHandler;
        new Thread(CodeHandler.recordCodeProvider).start();
    }

	/**
	 * 类似{@link AudioPlayHandler#surfaceCreated(SurfaceHolder)}�?
	 */
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas c=surfaceHolder.lockCanvas();
    }
    /**
	 * 类似{@link AudioPlayHandler#surfaceChanged(SurfaceHolder, int, int, int)}�?
	 */
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}
    /**
	 * 类似{@link AudioPlayHandler#surfaceDestroyed(SurfaceHolder)}�?
	 */
    @SuppressLint("NewApi")
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    	isRecording=false;
        if(audioRecord!=null){
        	audioRecord.release();
        }
        if(GlobalParameters.codes!=null){
            Util.saveRecordedCodes(fileName.substring(0,fileName.indexOf(GlobalParameters.AUDIO_TEMP_FORMAT_SUFFIX)));
            CodeHandler.timeTag.clear();
        }
        CodeHandler.isRecording=false;
    }
    
	/**
	 * 处理音频录制界面的所有按钮（包括使用TextView模拟的按钮）点击事件�?
	 */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audio_record_button:
                if(!isRecording){
                    recordAudio();
                    AudioRecordActivity.audioRecordButton.setBackgroundResource(R.drawable.audio_recording_btn);
                }
                else {
                    isRecording=false;
                    CodeHandler.isRecording=false;
                    audioRecord.stop();
                    if(GlobalParameters.codes!=null)Util.saveRecordedCodes(fileName.substring(0,fileName.indexOf(GlobalParameters.AUDIO_TEMP_FORMAT_SUFFIX)));
                    Toast.makeText(GlobalParameters.activity, "录音已保�?", Toast.LENGTH_LONG).show();
                    AudioRecordActivity.audioRecordButton.setBackgroundResource(R.drawable.audio_record_btn);
                }
                break;
//            case R.id.other_commands_audio_record:
//                int location[]=new int[2];
//                view.getLocationOnScreen(location);
//                if(!GlobalParameters.popupCreator.getAudioRecordPopup().isShowing())
//                    GlobalParameters.popupCreator.getAudioRecordPopup().showAtLocation(view,
//                        Gravity.RIGHT|Gravity.TOP,0,location[1]- GlobalParameters.popupCreator.getAudioRecordPopup().getHeight());
//                break;
        }
    }
    

	/**
	 * 设置{@link #drawFinished}�?
	 * @param drawState 为true，代表绘制完成�??
	 */
    public static synchronized void setDrawFinished(boolean drawState){
        drawFinished=drawState;
    }
    

	/**
	 * 获取{@link #drawFinished}的�?��??
	 */
    public static synchronized boolean getDrawFinished(){
        return drawFinished;
    }

	/**
	 * 采集音频数据的线程�??
	 */
    private class AudioDataHandler implements Runnable {
    	List<short[]> data;
    	int bufferSize=4096;
        AudioDataHandler(List<short[]> data){
            this.data=data;
        }
        public void run() {
            while (isRecording) {
                time++;
                Log.e("post data",""+bufferSize);
                byte[] buffer = new byte[bufferSize];
                short[] shortBuffer=new short[bufferSize/2];
                audioRecord.read(buffer, 0, bufferSize);
                for(int i=0;i<bufferSize;i+=2){
                    shortBuffer[i/2]=(short)(((0x00ff&(short)buffer[i+1])<<8)|(0x00ff&(short)buffer[i]));
                }
                data.add(shortBuffer);
                setDrawFinished(false);
                Message msg=new Message();
                msg.obj=shortBuffer;
                audioRecordDrawHandler.sendMessage(msg);
                while (!getDrawFinished());
            }
        }
    }
    public class Mp3Encoder implements Runnable{
    	DataOutputStream dataOutputStream;
    	List<short[]> data;
        Mp3Encoder(List<short[]> data){
        	this.data=data;
            try {
                fileName=GlobalParameters.MAC_ID+"_"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date(System.currentTimeMillis()))+GlobalParameters.AUDIO_TEMP_FORMAT_SUFFIX;
                dataOutputStream=new DataOutputStream(new FileOutputStream(new File(GlobalParameters.localDir,fileName)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
		@Override
		public void run() {
        	JNILibrary.jniLibrary.init(1, frequency, 128);
			while(isRecording){
	        	while(data.isEmpty()&&isRecording){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
	        	if(!isRecording)break;
				try {
	                if(data.get(0).length!=0)dataOutputStream.write(JNILibrary.jniLibrary.encode(data.get(0), data.get(0).length));
	                data.remove(0);
	            } catch (IOException e) {
	            	e.printStackTrace();
	            }
			}
			try {
				dataOutputStream.close();
				JNILibrary.jniLibrary.destroy();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
    }
}
