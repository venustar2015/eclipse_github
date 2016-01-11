package com.hznc.sharing;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * 该类用于为所有播放和录制过程实时获取代码行�?�并向播放或录制对应的处理代码的handler发�?�Message。录制过程对应的代码行获取功能已完成。播放过程对应的代码行尚未完成�??
 * @see AudioPlayHandler
 * @see AudioRecordHandler
 * @see VideoPlayHandler
 * @see VideoRecordHandler
 */
public class CodeHandler {
    public static RecordCodeProvider recordCodeProvider;
    public static int timeTagNum=0;
    public static Handler handler;
    public static Handler playHandler;
    public static CodeHandler codeHandler=new CodeHandler();
    public static boolean isRecording=false,isPlaying=false;
    public static ArrayList<String> codesLoaded=new ArrayList<String>();
    public static ArrayList<String> codesConverted=new ArrayList<String>();
    public static ArrayList<Integer> times=new ArrayList<Integer>();
    public static ArrayList<Integer> rowNum=new ArrayList<Integer>();
	public static ArrayList<String> timeTag=new ArrayList<String>(); 
	public static boolean isPaused=false;
	public static boolean pauseFlag=false;
    CodeHandler(){
        recordCodeProvider =new RecordCodeProvider();
    }
    public static int preRowNum;
    public static int preProgress=0;
    public static int count;
    public static int dstIndex=-1;
    public static int dstPeriod=-1;
    public static int playProgress=0;
    /**
	 * 该内部类用于录制过程实时获取代码行�?�已实现�?
	 */
    public static void setRecordCodeProvider(Handler codeHandler){
    	handler=codeHandler;
    	new Thread(){
    		 public void run() {
	        	preRowNum=-1;
	            while(isRecording){
	            	if((GlobalParameters.codeContent!=null&&GlobalParameters.codeContent.equals("error"))||(
	            			GlobalParameters.codeContent!=null&&GlobalParameters.codeContent.equals(""))||
	            			GlobalParameters.codeContent==null)return;
	            	String rowStr=Util.getDataContent(GlobalParameters.rowUrl, GlobalParameters.codeRowName);
	            	if((rowStr!=null&&rowStr.equals("error"))||rowStr.equals("")){
	            		handler.sendEmptyMessage(-100);
	            		continue;
	            	}
	            	int rowNum=Integer.parseInt(rowStr);
	            	if(rowNum!=preRowNum){
	                	handler.sendEmptyMessage(rowNum);
	                	preRowNum=rowNum;
	            	}
	            	try {
	                  Thread.sleep(300);
	              } catch (InterruptedException e) {
	                  e.printStackTrace();
	              }
	            }
	        }
    	}.start();
    }
    public static void setPlayCodeProvider(Handler codeHandler){
    	handler=codeHandler;
    	preRowNum=-1;
    	pauseFlag=true;
    	isPlaying=true;
    	playHandler=new Handler(){
    		public void handleMessage(Message msg) {
    			int rowNum=getRowNum(msg.what,0,times.size()-1);
    			if(rowNum!=preRowNum){
    				Log.e("rownum",rowNum+"");
    	        	if(times.isEmpty())return;
	            	handler.sendEmptyMessage(rowNum);
	                preRowNum=rowNum;
    			}
    		}
    	};
    }
    public static int getRowNum(int progress,int fromIndex,int toIndex){
    	if(progress>=times.get(toIndex)/100){
    		return rowNum.get(toIndex);
    	}
    	if(progress<=times.get(fromIndex)/100){
    		return rowNum.get(fromIndex);
    	}
    	if(fromIndex==toIndex||fromIndex==(fromIndex+toIndex)/2)return rowNum.get(fromIndex);
    	if(progress<times.get((toIndex+fromIndex)/2)/100){
    		return getRowNum(progress,fromIndex,(toIndex+fromIndex)/2);
    	}else{
    		return getRowNum(progress,(toIndex+fromIndex)/2,toIndex);
    	}
    }
    public class RecordCodeProvider implements Runnable{

        @Override
        public void run() {
        	preRowNum=-1;
            while(isRecording){
            	if((GlobalParameters.codeContent!=null&&GlobalParameters.codeContent.equals("error"))||(
            			GlobalParameters.codeContent!=null&&GlobalParameters.codeContent.equals(""))||
            			GlobalParameters.codeContent==null)return;
            	String rowStr=Util.getDataContent(GlobalParameters.rowUrl, GlobalParameters.codeRowName);
            	if((rowStr!=null&&rowStr.equals("error"))||rowStr.equals("")){
            		handler.sendEmptyMessage(-100);
            		continue;
            	}
            	int rowNum=Integer.parseInt(rowStr);
            	if(rowNum!=preRowNum){
                	handler.sendEmptyMessage(rowNum);
                	preRowNum=rowNum;
            	}
            	try {
                  Thread.sleep(300);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
            }
        }
    }
    public static int getSleepTime(int count){
    	if(count==dstIndex){
    		dstIndex=-1;
    		if(dstPeriod>=0){
    			return dstPeriod;
    		}
    	}
    	int p=times.get(count+1)-times.get(count);
		return p;
    }
    public static void pause(){
    	isPaused=true;
    }
    public static void start(){
    	isPaused=false;
    }
    public static void seekToEnd(){
    	if(times.isEmpty())return;
    	synchronized(codeHandler){
    		count=times.size()-1;
    	}
    }
    public static void seekToStart(){
    	if(times.isEmpty())return;
    	synchronized(codeHandler){
    		count=0;
    	}
    }
    public static void seekTo(long disPosition){
    	Log.e("times size", times.size()+"");
    	if(times.isEmpty())return;
    	synchronized(codeHandler){
    		int[] info=getTimeIndex(disPosition);
    		if(info==null)return;
	    	dstIndex=info[0]<1?0:info[0]-1;
	    	count=dstIndex;
	    	dstPeriod=info[1];
    	}
    }
    public static int[] getTimeIndex(long position){
    	if(times.isEmpty())return null;
    	int sum=0;
    	for(int i=0;i<times.size();i++){
    		sum+=times.get(i);
    		if(position<sum)return new int[]{i,(int) (sum-position)};
    	}
    	return null;
    }
}
