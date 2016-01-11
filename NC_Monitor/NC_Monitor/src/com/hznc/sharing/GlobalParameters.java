package com.hznc.sharing;

import android.app.Activity;
import android.media.AudioFormat;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * 此类中定义了�?些全�?常量和变量，作为各功能组件的参数或记录程序当前的某些值�?�部分暂未使用�?�详见字段说明�??
 */
public class GlobalParameters {
    
    /**
	 * 该Activity对象用于记录当前运行的Activity�?
	 */
    public static Activity activity;
    
	/**
	 * 表示MP3文件后缀名的字符串常量�?�未使用，暂时保留�??<br />
	 * 与{@link #AUDIO_FORMAT_SUFFIX}相同�?
	 */
    public final static String MP3_SUFFIX=".mp3";
	
	/**
	 * 表示MP3录制临时文件名后�?的字符串常量�?
	 */
    public final static String AUDIO_TEMP_FORMAT_SUFFIX=".mp3";
	
	/**
	 * 表示MP3文件后缀名的字符串常量�?�未使用，暂时保留�??<br />
	 * 与{@link #MP3_SUFFIX}相同�?
	 */
    public final static String AUDIO_FORMAT_SUFFIX=".mp3";
	
	/**
	 * 表示视频录制文件后缀名的字符串常量�??
	 */
    public final static String VIDEO_FORMAT_SUFFIX =".mp4";
	
	/**
	 * 表示代码文件后缀名的字符串常量�??
	 */
    public final static String CODE_FORMAT_SUFFIX =".txt";
	
	/**
	 * 程序修改后遗留的常量。暂时保留�??
	 */
    public final static String AUDIO_TEMP_NAME ="";
	
	/**
	 * 程序修改后遗留的常量。暂时保留�??
	 */
    public final static String VIDEO_TEMP_NAME ="";
	
	/**
	 * 程序修改后遗留的常量。暂时保留�??
	 */
    public final static String CODE_TEMP_NAME ="";
    
    /**
     * 机床ID
     */
    public static String MAC_ID="";
    
    /**
     * 下载地址
     */
    public static String DOWNLOAD_URL="";
    
    /**
     * 下载文件�?(含路�?)
     */
    public static File DOWNLOAD_FILEPATH= null;
    
    /**
     * 下载状�??
     */
    public static Boolean DOWNLOAD_FLAG= false;
    
    /**
     * 在线播放缓存状�??
     */
    public static Boolean BUFFER_FLAG= false;
    
    /**
     * 上传地址
     */
    public static String UPLOAD_URL = "http://www.cybernc.cn:8800/NCMMediaForAndroid/UploadFile";
    
    /**
     * 上传文件�?(含路�?)
     */
    public static File UPLOAD_FILEPATH = null;
    
    /**
	 * 屏幕的宽度，单位：相素�??
	 */
    public static  int screenWidth;
    
    /**
	 * 屏幕的宽度，单位：相素�??
	 */
    public static  int screenHeight;

    /**
	 * 程序运行目录，即保存文件目录�?
	 */
    public static File runTimeDir, localDir, cloudDir;

    /**
	 * 音频录制的采样率�?
	 */
    public static int audioRecordSamplingRate =AudioConfiguration.AUDIO_SAMPLING_RATE_44100;
    
    /**
	 * 音频录制的PCM编码：PCM16�?
	 */
    public static int audioRecordEncode=AudioConfiguration.AUDIO_ENCODING_PCM16;
    
    /**
	 * 音频录制的�?�道模式：单通道�?
	 */
    public static int audioRecordChannelConfiguration=AudioConfiguration.AUDIO_CHANNEL_MONO;
    
    /**
	 * 用于保存当前播放或录制的代码的字符串�?
	 */
    public static String codeContent=null;
    
    /**
	 * 获取代码内容的请求链接�??
	 */
    public static final String codeUrl="http://www.cybernc.cn:8800/NCMMediaForAndroid/GetGCodeContent_ForArd?MacID=NERC20141000001";
    
    /**
	 * 获取行号的请求链接�??
	 */
    public static final String rowUrl="http://www.cybernc.cn:8800/NCMMediaForAndroid/GetGcodeRunRow_ForArd?MacID=NERC20141000001";
    
    /**
	 * 获取json数组中代码�?��?��?�的“名称�?��??
	 */
    public static final String codeContentName="GcodeContent";
    
    /**
	 * 获取json数组中行号�?��?��?�的“名称�?��??
	 */
    public static final String codeRowName="GCodeRunRow";
    
    /**
	 * 用于保存每行代码的字符串数组�?
	 */
    public static String[] codes=null;
    public static int videoRecordW=720;
    public static int videoRecordH=720*9/16;
    /**
	 * 设置当前运行的Activity。每次打�?新的Activity时会调用该方法�??
	 */
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalParameters.activity = currentActivity;
    }

    /**
	 * 初始化一些全�?量�?�如{@link #screenHeight}，{@link #runTimeDir}等�??
	 */
	public static PopupCreator popupCreator;

	public static String DOWNLOAD_MEDIAURL;

	public static File DOWNLOAD_MEDIAPATH;

	public static String DOWNLOAD_CODEURL;

	public static File DOWNLOAD_CODEPATH; 
	public static float scale=1;
	
    @SuppressWarnings("deprecation")
	public static void initGlobalObject(){

		popupCreator=new PopupCreator(activity);
        screenWidth=activity.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight=activity.getWindowManager().getDefaultDisplay().getHeight();
        scale=activity.getResources().getDisplayMetrics().density;
        runTimeDir = new File(Environment.getExternalStorageDirectory(),"NCPlatform");
        localDir = new File(runTimeDir,"LocalFile");
        cloudDir = new File(runTimeDir,"CloudFile");
		GlobalParameters.MAC_ID = "NERC20141000001";
        try {
            if(!runTimeDir.exists()){
                if(!runTimeDir.mkdir()) Toast.makeText(activity,"目录创建失败",Toast.LENGTH_SHORT).show();
            }
            if(!localDir.exists()){
                if(!localDir.mkdir())Toast.makeText(activity,"目录创建失败",Toast.LENGTH_SHORT).show();
            }
            if(!cloudDir.exists()){
                if(!cloudDir.mkdir())Toast.makeText(activity,"目录创建失败",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(activity,"目录创建失败",Toast.LENGTH_SHORT).show();
        }
    }
	
	
    public static void setMacID(String macID){
		GlobalParameters.MAC_ID = macID;
    }
    /**
	 * 记录当前录制的代码�?�可能会删除�?
	 */
    public static String codeRecorded="";
    
    /**
	 * 当前录制文件的临时文件名。可能会删除�?
	 */
    public static String currentRecordingTempFile="";
    
    /**
	 * 当前录制文件对应代码文件的临时文件名。可能会删除�?
	 */
    public static String currentRecordingCodeTemp="";
    
    /**
	 * 记录当前的运行状态�?�可能会删除�?
	 */
    public static int currentState=-1;
    
    /**
	 * 清空当前录制的代码�?�可能会删除�?
	 */
    public static void resetCodeRecorded(){
        codeRecorded="";
    }

    
    /**
	 * 设置当前的运行状态�?�可能会删除�?
	 */
    public static void setCurrentState(int runningState){
        currentState=runningState;
    }

    
    /**
	 * 该类用于定义表示各个运行状�?�的常量。可能会删除�?
	 */
    public static class RunningState{
        public final static int AUDIO_RECORDING=0x00,
                                VIDEO_RECORDING=0x01,
                                AUDIO_RECORDED=0x02,
                                VIDEO_RECORDED=0x04,
                                AUDIO_PLAYING=0x08,
                                VIDEO_PLAYING=0x10,
                                WAITING=0xff;
    }
    
    /**
	 * 该类用于定义由于音频录制的配置数据�??
	 */
    public static class AudioConfiguration{
        @SuppressWarnings("deprecation")
		public final static int AUDIO_ENCODING_PCM8= AudioFormat.ENCODING_PCM_8BIT,
                                AUDIO_ENCODING_PCM16=AudioFormat.ENCODING_PCM_16BIT,
                                AUDIO_SAMPLING_RATE_44100=44100,
                                AUDIO_SAMPLING_RATE_22050=22050,
                                AUDIO_SAMPLING_RATE_11025=11025,
                                AUDIO_CHANNEL_MONO=AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                AUDIO_CHANNEL_STEREO=AudioFormat.CHANNEL_CONFIGURATION_STEREO;
    }
}
