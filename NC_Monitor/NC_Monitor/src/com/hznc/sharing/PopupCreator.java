package com.hznc.sharing;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hznc.nc_monitor.R;

/**
 * 该类包含了对所有弹出菜单、弹出窗口（保存和打开窗口）对象的处理。<br />
 * 注：文件保存窗口暂时未用。原设计用于让用户重新命名文件。当前程序运行时，程序的命名和保存工作都自动完成。暂时保留保存窗口，后面可能会用到。
 */
public class PopupCreator {
    public static ListView fileViewer;
    public static PopupWindow audioPlayPopup,videoPlayPopup;
	public static PopupWindow openPopupWindow;
	public static File viewPath,fileSelected;
    public static RadioGroup audioPlayRadioGroup;
    static View currentFileSelected,popupOpenWindow,popupSaveWindow;
    static TextView pathText, openWindowOk,openWindowCancel,saveWindowOk,saveWindowCancel, localFileBtn, webFileBtn;
    static String[] files;
    public static EditText saveName;
	public static LinearLayout loadingView;
	public static Handler handler;
    @SuppressWarnings("deprecation")
	PopupCreator(Activity activity){
        //音频播放菜单
        View view =activity.getLayoutInflater().inflate(R.layout.audio_play_popupmenu,null);
        audioPlayPopup =new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        audioPlayPopup.setFocusable(true);
        audioPlayPopup.setOutsideTouchable(true);
        audioPlayPopup.setBackgroundDrawable(new BitmapDrawable());
        audioPlayRadioGroup=(RadioGroup)view.findViewById(R.id.audio_play_popup_radiogroup);
//        audioPlayRadioGroup.setOnCheckedChangeListener(AudioPlayHandler.audioPlayHandler);

        viewPath =GlobalParameters.runTimeDir;
        currentFileSelected=new View(GlobalParameters.activity);
    }

	/**
	 * 以字符串数组的形式返回当前目录里面所有的文件名。
	 */
//    public static String[] sortFile(String files[]){
//        if(files==null)return  null;
//        ArrayList<String> items=new ArrayList<String>();
//        if(GlobalParameters.activity instanceof AudioPlayActivity){
//        	for(String file:files){
//        		if(file.endsWith(".mp3")||file.endsWith(".MP3")){
//        			items.add(file);
//        		}
//        	}
//        }else if(GlobalParameters.activity instanceof VideoPlayActivity){
//        	for(String file:files){
//        		if(file.endsWith(".mp4")||file.endsWith(".MP4")){
//        			items.add(file);
//        		}
//        	}
//        }
//        if(items.isEmpty())return null;
//        String[] filteredFiles=new String[items.size()];
//        items.toArray(filteredFiles);
//        String temp="";
//        for(int m=filteredFiles.length-1;m>=0;m--)
//            for(int n=0;n<m;n++){
//                if(filteredFiles[n].compareToIgnoreCase(filteredFiles[n+1])<0){
//                    temp=filteredFiles[n];
//                    filteredFiles[n]=filteredFiles[n+1];
//                    filteredFiles[n+1]=temp;
//                }
//            }
//        return  filteredFiles;
//    }

	/**
	 * 处理弹出窗口确定按钮的方法。根据当前选择的文件类型进行判断和播放。
	 */
}
