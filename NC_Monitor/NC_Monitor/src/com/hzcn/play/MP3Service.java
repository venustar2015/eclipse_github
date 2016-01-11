package com.hzcn.play;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MP3Service{
	private static MP3Service ms;
	private MediaFormat mf;
	private MediaExtractor me;
	private MediaCodec mc;
	private AudioTrack audioTrack=null;
	private int bufferSize=0;
	private ByteBuffer[] inputBuffers;
	private ByteBuffer[] outputBuffers;
	private List<byte[]> datas=Collections.synchronizedList(new ArrayList<byte[]>());
	private String dataSource=null;
	private Handler dataHandler;
	private long fileDuration=0;
	private long startTime=0;
	private boolean EOF;
	private boolean EOP;
	private int maxProg=0;
	private boolean pauseFlag;
	private boolean releasedFlag=false;
	private long pauseStart=0,pauseStop=0,currentTime=0,pauseTime=0;
	private Handler progressHandler=null;
	private long postFlag=0;
	private Thread meThread=new Thread(){
		@Override
		public void run() {
			BufferInfo bufferInfo=new BufferInfo();
			if(mc==null)Log.e("mc is null","mc is null");
			do{
				try{
					while(datas.size()>=10);
					int inputIndex=mc.dequeueInputBuffer(-1);
					int sampleSize=me.readSampleData(inputBuffers[inputIndex], 0);
					long sampleTime=me.getSampleTime();
					me.getSampleFlags();
					if(sampleSize<0){
						EOF=true;
						return;
					}
					mc.queueInputBuffer(inputIndex, 0, sampleSize, sampleTime, 0);
					int outputIndex=mc.dequeueOutputBuffer(bufferInfo, 10000);
					me.advance();
					if(outputIndex>=0){
						byte[] bytes=new byte[bufferInfo.size];
						outputBuffers[outputIndex].get(bytes,0,bufferInfo.size);
						datas.add(bytes);
						outputBuffers[outputIndex].clear();
						mc.releaseOutputBuffer(outputIndex, false);
					}else if(outputIndex==MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
						outputBuffers=mc.getOutputBuffers();
					}else if(outputIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
					}else{
					}
				}catch(Exception e){}
			}while(!EOF&&!EOP);
		}
	};
	private Thread atThread=new Thread(){
		public void run(){
			while(true){
				if(EOP)break;
				while(pauseFlag){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
				}
				while(datas.size()==0);
				synchronized(MP3Service.this){
					if(datas.isEmpty())continue;
					postData(datas.get(0));
					setProgress();
					audioTrack.write(datas.get(0), 0, datas.get(0).length);
					setProgress();
					datas.remove(0);
					if(EOF&&datas.isEmpty()){
						EOP=true;
						setProgress();
						break;
					}
					if(EOP)break;
				}
			}
		}
	};
	private MP3Service(String dataSource,Handler dataHandler,Handler progressHandler){
		this.dataSource=dataSource;
		this.dataHandler=dataHandler;
		this.progressHandler=progressHandler;
	}
	public boolean initMP3Service() {
	// TODO Auto-generated method stub
		if(dataSource==null){
			Log.e("initMP3ServiceError","not a file");
			return false;
		}
		try{
			String mime=null;
			int sampleRate=0,channel=0;
			me=new MediaExtractor();
			me.setDataSource(dataSource);
			int trackNum=me.getTrackCount();
			for(int i=0;i<trackNum;i++){
				mf=me.getTrackFormat(i);
				mime=mf.getString(MediaFormat.KEY_MIME);
				Log.e("mime",mime);
				if(mime.equals("audio/mpeg")){
					me.selectTrack(i);
					sampleRate=mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
					mf.getInteger(MediaFormat.KEY_BIT_RATE);
					channel=mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
					int channelConfig=channel==1?AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;
					bufferSize=AudioTrack.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
					audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, 2*bufferSize, AudioTrack.MODE_STREAM);
					audioTrack.play();
				}
			}
			mc=MediaCodec.createDecoderByType(mime);
			mc.configure(mf, null, null, 0);
			mc.start();
			inputBuffers=mc.getInputBuffers();
			outputBuffers=mc.getOutputBuffers();
		}catch(Exception e){
			return false;
		}
		return true;
	}
	public void startMP3Service(){
		EOF=false;
		EOP=false;
		pauseFlag=false;
		pauseTime=0;
		currentTime=0;
		pauseStart=0;
		pauseStop=0;
		clear(datas);
		meThread.start();
		setStartTime();
		maxProg=(int) (getDuration()/100);
		setProgress();
		atThread.start();
	}
	protected void setStartTime() {
		// TODO Auto-generated method stub
		startTime=System.currentTimeMillis();
	}
	public int getPlayingTimeInSec(){
		return (int) (System.currentTimeMillis()-startTime)/1000;
	}
	public long getPlayingTimeInMSec(){
		return System.currentTimeMillis()-startTime;
	}
	protected void postData(byte[] bs) {
		// TODO Auto-generated method stub
		double[] doubleBuffer=new double[bs.length/2];
		final double[][] chrunks=new double[doubleBuffer.length/2048][2048];
        for(int i=0;i<bs.length;i+=2){
        	doubleBuffer[i/2]=(double)((short)(((0x00ff&(short)bs[i+1])<<8)|(0x00ff&(short)bs[i])));
    	}
        for(int i=0;i<doubleBuffer.length/2048;i++){
    		for(int j=0; j<2048;j++){
    			chrunks[i][j] = doubleBuffer[i*2048+j];
    		}
        }
        postFlag++;
        new Timer().schedule(new TimerTask(){
        	int count=0;
        	long flag=postFlag;
        	public void run(){
        		if(flag!=postFlag)this.cancel();
        		if(count==chrunks.length)return;
        		Message msg=new Message();
    			msg.obj=chrunks[count++];
    			dataHandler.sendMessage(msg);
        	}
        }, 0, 80);
	}
	public void seekTo(long ms){
		if(me!=null){
			pause();
			clear(datas);
			goOn();
			me.seekTo(ms*1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
		}
	}
	private void clear(List<byte[]> datas) {
		// TODO Auto-generated method stub
		synchronized(MP3Service.this){
			datas.clear();
		}
	}
	public long getDiv(){
		long div=fileDuration;
		if(div>3600000){
			div=120000;
		}else if(div>600000){
			div=30000;
		}else{
			div=3000;
		}
		return div;
	}
	public long seekToPosition(int position){
		if(position<0)position=0;
		if(position>maxProg)position=maxProg;
		long disPosition=fileDuration*position/maxProg;
		pauseTime-=disPosition-currentTime;
		seekTo(disPosition);
		return disPosition;
	}
	public long seekForward(){
		long div=getDiv();
		long dstPosition=currentTime+div;
		pauseTime+=-div;
		if(fileDuration<=dstPosition)dstPosition=fileDuration;
		seekTo(dstPosition);
		return dstPosition;
	}
	public long seekBack(){
		long div=getDiv();
		long dstPosition=currentTime-div;
		pauseTime+=div;
		if(dstPosition<=0)dstPosition=0;
		seekTo(dstPosition);
		return dstPosition;
	}
	public void seekToEnd(){
		seekTo(fileDuration);
	}
	public void seekToStart(){
		seekTo(0);
	}
	public static MP3Service getInstance(String dataSource,Handler dataHandler,Handler progressHandler){
		if(ms==null||(ms!=null&&ms.releasedFlag)){
			ms=new MP3Service(dataSource,dataHandler,progressHandler);
			if(ms.initMP3Service()){
				ms.startMP3Service();
			}else{
				return null;
			}
		}
		return ms;
	}
	public long getDuration(){
		if(mc!=null&&me!=null&&audioTrack!=null){
			return fileDuration=mf.getLong(MediaFormat.KEY_DURATION)/1000;
		}
		return 0;
	}
	public void setProgress(){
		int[] info=new int[3];
		if(EOP){
			info[0]=maxProg;
			info[1]=(int) (fileDuration/1000);
		}else{
			info[0]=(int) (getCurrentTime()*maxProg/fileDuration);
			info[1]=(int) (currentTime/1000);
		}
		info[2]=(int) (fileDuration/1000);
		if(!pauseFlag){
			Message msg=new Message();
			msg.obj=info;
			progressHandler.sendMessage(msg);
		}
	}
	private synchronized long getCurrentTime() {
		// TODO Auto-generated method stub
		
		if(pauseFlag&&pauseStart==0){
			pauseStart=System.currentTimeMillis();
		}
		if(!pauseFlag&&pauseStart!=0){
			pauseStop=System.currentTimeMillis();
		}
		if(pauseStart!=0&&pauseStop!=0){
			pauseTime+=pauseStop-pauseStart;
			Log.e("pauseTime", ""+pauseStop+"-"+pauseStart+"="+pauseTime);
			pauseStart=0;
			pauseStop=0;
		}
		currentTime=System.currentTimeMillis()-startTime-pauseTime;
		return currentTime;
	}
	public void pause(){
		pauseFlag=true;
		audioTrack.pause();
	}
	public void goOn(){
			pauseFlag = false;
			audioTrack.play();
	}
	public MP3Service stop(){
		pauseFlag=false;
		EOP=true;
		if(audioTrack.getState()!=AudioTrack.PLAYSTATE_PAUSED
				&&audioTrack.getState()!=AudioTrack.STATE_UNINITIALIZED){
			audioTrack.stop();
			mc.stop();
		}
		audioTrack.flush();
		clear(datas);
		return this;
	}
	public void release(){
		audioTrack.release();
		mc.release();
		me.release();
		releasedFlag=true;
	}
	public MP3Service replay(){
		stop().release();
		return getInstance(dataSource,dataHandler,progressHandler);
	}
}