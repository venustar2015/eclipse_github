package com.hznc.sharing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hznc.draw.RecorderVI;
import com.hznc.draw.PlayVI;
import com.hznc.nc_monitor.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 该类定义了许多工具方法�??
 */
public class Util {
	
	 public static String getFormatTime(int sec){
	        NumberFormat numberFormat=NumberFormat.getNumberInstance();
	        numberFormat.setMinimumIntegerDigits(2);
	        int hours=0,minutes=0,seconds=0;
	        seconds=sec%60;
	        minutes=sec%3600/60;
	        hours=sec/3600;
	        return numberFormat.format(hours)+":"+numberFormat.format(minutes)+":"+numberFormat.format(seconds);
    }
	public static String getFormatTime2(int timeInMillis) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumIntegerDigits(2);
		float timeInSecond = timeInMillis / 1000;
		int minutes = 0;
		float seconds = 0;
		seconds = ((float) (timeInMillis % 60000)) / 1000;
		minutes = (int) (timeInSecond % 3600 / 60);
		String formatedMinute = numberFormat.format(minutes);
		numberFormat.setMinimumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(1);
		String formatedSeconds = numberFormat.format(seconds);
		return formatedMinute + ":" + formatedSeconds;
	}

	/**
	 * 从指定文件加载代码，并将每行代码放进指定的ArrayList<String> 对象中�?�此时每行代码都包含有�?�时间头”�??
	 * 
	 * @param file
	 *            代码文件
	 * @param codesLoaded
	 *            用于存储代码的ArrayList<String> 对象�?
	 */
	public static void loadCodes(File file, ArrayList<String> codesLoaded,
			ListView codesListView) {
		try {
			if (!codesLoaded.isEmpty()) {
				codesLoaded.clear();
			}
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String code;
			while ((code = bufferedReader.readLine()) != null) {
				if (code.trim().isEmpty())
					continue;
				if (code.startsWith("GCode"))
					continue;
				codesLoaded.add(code);
			}
			bufferedReader.close();
			convertLoadedCodes(codesLoaded, CodeHandler.codesConverted,
					CodeHandler.times, CodeHandler.rowNum);
			String[] codes = new String[CodeHandler.codesConverted.size()];
			CodeHandler.codesConverted.toArray(codes);
			codesListView.setAdapter(new CodeAdapter<String>(
					GlobalParameters.activity, R.layout.code_list_item, codes));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getTimeFromeCode(String code) {
		int a = (Integer.parseInt(code.substring(0, 2))) * 60000
				+ (int) ((Float.parseFloat(code.substring(3))) * 1000);
		return a;
	}

	/**
	 * 将{@link #loadCodes(File, ArrayList)}得到的ArrayList<String>
	 * 中得到的代码分析并分解成“时间�?�部分和“代码内容�?�部分�??
	 * 分别保存在ArrayList<Integer>对象和ArrayList<String>对象中�?�二者的元素�?�?对应�?
	 * 
	 * @param codesLoaded
	 *            {@link #loadCodes(File, ArrayList)}执行后得到的ArrayList<String>对象
	 * @param codesConverted
	 *            用于存储“代码内容的”的ArrayList<String>对象
	 * @param times
	 *            用于存储“时间�?�部分的ArrayList<Integer>对象�?
	 * @param rowNum
	 */
	public static void convertLoadedCodes(ArrayList<String> codesLoaded,
			ArrayList<String> codesConverted, ArrayList<Integer> times,
			ArrayList<Integer> rowNum) {
		if (!codesConverted.isEmpty())
			codesConverted.clear();
		if (!times.isEmpty())
			times.clear();
		if (!rowNum.isEmpty())
			rowNum.clear();
		for (int i = 0, j = -1; i < codesLoaded.size(); i++) {
			if (codesLoaded.get(i).startsWith("TimeTag")) {
				j = i;
				continue;
			}
			if (j == -1)
				codesConverted.add(codesLoaded.get(i));
			if (i > j && j != -1) {
				try {
					times.add(getTimeFromeCode(codesLoaded.get(i).substring(1,
							8)));
					rowNum.add(Integer.parseInt(codesLoaded.get(i)
							.substring(10)));
				} catch (Exception e) {
					Toast.makeText(GlobalParameters.activity, "时间和行标错误！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 以指定的文件名保存代码文件�??
	 * 
	 * @param name
	 *            文件名，不包括文件后�?�?
	 */
	public static void saveRecordedCodes(String name) {
		File file = new File(GlobalParameters.localDir, name
				+ GlobalParameters.CODE_FORMAT_SUFFIX);
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					file));
			bufferedWriter.write("GCode");
			bufferedWriter.newLine();
			for (String str : GlobalParameters.codes) {
				bufferedWriter.write(str);
				bufferedWriter.newLine();
			}
			bufferedWriter.newLine();
			bufferedWriter.write("TimeTag");
			bufferedWriter.newLine();
			bufferedWriter.write("[00:00.0] 0");
			for (int i = 0; i < CodeHandler.timeTag.size(); i++) {
				bufferedWriter.newLine();
				bufferedWriter.write(CodeHandler.timeTag.get(i));
			}
			bufferedWriter.close();
			GlobalParameters.codes = null;
			CodeHandler.codesConverted.clear();
			CodeHandler.codesLoaded.clear();
			CodeHandler.times.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制波形�?
	 * 
	 * @param holder
	 *            要被绘制的SurfaceView的SurfaceHolder对象
	 * @param shortBuffer
	 *            用于波形绘制的数�?
	 * @param waveY
	 *            波形绘制的Y坐标偏移�?
	 * @param count
	 *            绘制的次数，次数值会被绘制出�?
	 * @param magnifying
	 *            绘制的放大�?�数�?
	 */
	public static void drawWave(PlayVI monitorvi, short[] shortBuffer,
			int count, double magnifying) {
		double[] doubleBuffer = new double[shortBuffer.length];
		for (int i = 0; i < shortBuffer.length; i++) {
			doubleBuffer[i] = (double) shortBuffer[i];
		}
		monitorvi.setArray(doubleBuffer.length, doubleBuffer, 1);
	}

	public static void RdrawWave(RecorderVI monitorvi, short[] shortBuffer,
			int count, double magnifying) {
		double[] doubleBuffer = new double[shortBuffer.length];
		for (int i = 0; i < shortBuffer.length; i++) {
			doubleBuffer[i] = (double) shortBuffer[i];
		}
		monitorvi.setArray(doubleBuffer.length , doubleBuffer, 1);
	}

	/**
	 * 原来用与给保存的wav文件写头。现为mp3格式，故不再使用该方法，该方法仍保留，后面可能会用到�?
	 */
	public static void writeWaveFileHeader(FileOutputStream out,
			long totalAudioLength, long totalDataLength, long longSampleRate,
			int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLength & 0xff);
		header[5] = (byte) ((totalDataLength >> 8) & 0xff);
		header[6] = (byte) ((totalDataLength >> 16) & 0xff);
		header[7] = (byte) ((totalDataLength >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (1 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLength & 0xff);
		header[41] = (byte) ((totalAudioLength >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLength >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLength >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	/**
	 * 将录制的数据加上头后保存成wav文件�?
	 */
	public static void saveAsWaveFileHeader(String fileName) {
		try {
			File pcmFile = new File(GlobalParameters.localDir, fileName);
			File wavFile = new File(
					GlobalParameters.localDir,
					fileName.substring(0, fileName
							.indexOf(GlobalParameters.AUDIO_TEMP_FORMAT_SUFFIX))
							+ GlobalParameters.AUDIO_FORMAT_SUFFIX);
			if (!pcmFile.exists())
				return;
			if (wavFile.exists()) {
				wavFile.delete();
			}
			FileInputStream fileInputStream = new FileInputStream(pcmFile);
			long audioLength = fileInputStream.getChannel().size();
			FileOutputStream fileOutputStream = new FileOutputStream(wavFile);
			writeWaveFileHeader(fileOutputStream, audioLength,
					audioLength + 36, GlobalParameters.audioRecordSamplingRate,
					1, 2 * GlobalParameters.audioRecordSamplingRate);
			byte[] buffer = new byte[1024];
			while ((fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer);
			}
			fileInputStream.close();
			fileOutputStream.close();
			pcmFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于快�?�傅里叶变换由波形数据获取频谱数据�??
	 * 
	 * @see #RFFT(int, int, double[], double[])
	 * @see #spectrum(int, int, double[])
	 */
	public static void FFT(int sw, int n, double[] x, double[] y) {
		int i, j, k, l, m, l1, n1, n2, s;
		double t1, t2, u1, u2, w1, w2, p2, z;
		n1 = n / 2;
		n2 = n - 1;
		j = 1;
		s = (int) (Math.log(n) / Math.log(2));
		for (l = 1; l <= n2; l++) {
			if (l < j) {
				t1 = x[j];
				t2 = y[j];
				x[j] = x[l];
				y[j] = y[l];
				x[l] = t1;
				y[l] = t2;
			}
			;
			k = n1;
			while (k < j) {
				j -= k;
				k /= 2;
			}
			j = j + k;
		}
		m = 1;
		for (i = 1; i <= s; i++) {
			u1 = 1;
			u2 = 0;
			k = m;
			m *= 2;
			p2 = Math.PI / k;
			w1 = Math.cos(p2);
			w2 = (-Math.sin(p2));
			if (sw == 1)
				w2 = -w2;
			for (j = 1; j <= k; j++) {
				for (l = j; l <= n; l += m) {
					l1 = l + k;
					t1 = x[l1] * u1 - y[l1] * u2;
					t2 = x[l1] * u2 + y[l1] * u1;
					x[l1] = x[l] - t1;
					y[l1] = y[l] - t2;
					x[l] += t1;
					y[l] += t2;
				}
				z = u1 * w1 - u2 * w2;
				u2 = u1 * w2 + u2 * w1;
				u1 = z;
			}
		}
		if (sw == 1) {
			z = 2;
			for (i = 1; i <= n; i++) {
				x[i] /= z;
				y[i] /= z;
			}
		}
		if (sw == 0) {
			z = n1;
			for (i = 1; i <= n; i++) {
				x[i] /= z;
				y[i] /= z;
			}
		}
	}

	/**
	 * 用于快�?�傅里叶变换由波形数据获取频谱数据�??
	 * 
	 * @see #FFT(int, int, double[], double[])
	 * @see #spectrum(int, int, double[])
	 */
	public static void RFFT(int w, int n, double[] x, double[] y) // 实数据FFT
	{
		int i, k, k1, k2, n2, kv;
		double pn, r0, g0, hr, hi, nr, ni, wr, wi, xr, xi, yr, yi, z;
		n = n / 2;
		n2 = n / 2;
		pn = Math.PI / n;
		for (i = 1; i <= n; i++) {
			y[i] = x[2 * i];
			x[i] = x[2 * i - 1];
		}
		FFT(2, n, x, y);
		r0 = x[1];
		g0 = y[1];
		x[1] = r0 + g0;
		y[1] = 0;
		x[n + 1] = r0 - g0;
		y[n + 1] = 0;
		y[n2 + 1] = -y[n2 + 1];
		kv = n2 - 1;
		for (k = 1; k <= kv; k++) {
			k1 = k + 1;
			k2 = n - k + 1;
			hr = x[k1];
			hi = y[k1];
			nr = x[k2];
			ni = -y[k2];
			xr = nr + hr;
			xi = ni + hi;
			yr = nr - hr;
			yi = ni - hi;
			r0 = k * pn;
			wr = Math.sin(r0);
			wi = Math.cos(r0);
			z = wr * yr - wi * yi;
			wi = wr * yi + wi * yr;
			wr = z;
			x[k2] = xr - wr;
			y[k2] = wi - xi;
			x[k1] = xr + wr;
			y[k1] = xi + wi;
		}
		n = 2 * n;
		z = n;
		for (i = 1; i <= n / 2; i++) {
			x[i] /= z;
			y[i] /= z;
		}
		for (i = 1; i <= n / 2; i++) {
			x[i + n / 2] = x[n / 2 - i + 1];
			y[i + n / 2] = -y[n / 2 - i + 1];
		}
	}

	/**
	 * 用于快�?�傅里叶变换由波形数据获取频谱数据�??
	 * 
	 * @see #FFT(int, int, double[], double[])
	 * @see #RFFT(int, int, double[], double[])
	 */
	public static double[] spectrum(int t, int n, double[] x) {
		int i;
		double[] f = new double[n / 2];
		double[] w1 = new double[n + 2];
		double[] w2 = new double[n + 2];
		w1[0] = 0;
		w2[0] = 0;
		for (i = 1; i <= n; i++) {
			w1[i] = x[i - 1];
			w2[i] = 0;
		}
		RFFT(0, n, w1, w2);
		for (i = 1; i < n / 2; i++) {
			w1[i - 1] = w1[i];
			w2[i - 1] = w2[i];
		}
		for (i = 0; i < n / 2; i++) {
			f[i] = w1[i] * w1[i] + w2[i] * w2[i];
			if (t == 1)
				f[i] = Math.sqrt(f[i]);
		}
		return f;
	}

	/**
	 * 从服务器获取数据�?
	 * 
	 * @param url
	 *            请求数据的服务器链接
	 * @param name
	 *            请求的json数组的内容对应的名称�?
	 */
	public static String getDataContent(String url, String name) {
		InputStream is = null;
		String result = "";
		StringBuilder sb = new StringBuilder();
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
					2000);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			return "error";
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
		} catch (Exception e) {
			return "error";
		}
		try {
			JSONObject jsonObj = new JSONObject(sb.toString());
			result = jsonObj.getString(name);
		} catch (JSONException e) {
			return "error";
		}

		return result.replace("\r\n", "\n");
	}

	/**
	 * 用于在录制开始前从服务器获取代码数据�?
	 */
	public static void initRecordCodesList(final Handler handler,
			final ListView codeListView) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				GlobalParameters.codeContent = Util.getDataContent(
						GlobalParameters.codeUrl,
						GlobalParameters.codeContentName);
				if ((GlobalParameters.codeContent != null && GlobalParameters.codeContent
						.equals("error"))
						|| GlobalParameters.codeContent.equals("")) {
					handler.sendEmptyMessage(-1);
				} else {
					GlobalParameters.codes = GlobalParameters.codeContent
							.split("\n");
					handler.sendEmptyMessage(0);
				}
			}
		}.start();
	}

	/**
	 * 该类定义了代码显示区的ListView的Adapter�?
	 */
	public static class CodeAdapter<T> extends ArrayAdapter<T> implements
			OnFocusChangeListener {
		T[] objects;
		String currentLineContent = null;
		int position = -1;
		ViewGroup parent = null;

		public CodeAdapter(Context context, int resource, T[] objects) {
			super(context, resource, objects);
			this.objects = objects;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView;
			this.position = position;
			this.parent = parent;
			if (convertView == null) {
				convertView = GlobalParameters.activity.getLayoutInflater()
						.inflate(R.layout.code_list_item, null);
				textView = (TextView) convertView.findViewById(R.id.code_item);
				convertView.setTag(textView);
			}
			textView = (TextView) convertView.getTag();
			textView.setText((String) objects[position]);
			textView.setOnFocusChangeListener(this);
			return convertView;
		}

		@Override
		public void onFocusChange(View v, boolean state) {
			// TODO Auto-generated method stub
			if (state) {
				v.setBackgroundColor(Color.parseColor("#AAAAAA"));
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	/**
	 * 用于初始化播放时代码显示区的代码内容显示�?
	 */
	public static void initPlayCodesList(ListView codesListAudioPlay) {
		// TODO Auto-generated method stub
		String[] codes = new String[CodeHandler.codesConverted.size()];
		CodeHandler.codesConverted.toArray(codes);
		if (!CodeHandler.codesLoaded.isEmpty()) {
			codesListAudioPlay.setAdapter(new CodeAdapter<String>(
					GlobalParameters.activity, R.layout.code_list_item, codes));
		}
	}

	public static void getWebFileList(final String macID,final String mediaType, final Handler handler) {
		// Android 4.0 之后不能在主线程中请求HTTP请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				// MediaType,0--�?有格�?,1--音频格式wav�?2--音频格式mp3�?3--视频格式3gp�?4--视频格式mp4�?5--图片格式png�?6--图片格式Jpg
				String webResult = getContent("http://www.cybernc.cn:8800/NCMMediaForAndroid/GetNcMMediaList?MacID="
						+ macID + "&MediaType=" + mediaType);

				if (webResult == "网络连接失败" || webResult == "下载文件列表失败") {
					msg.what = -1;
					msg.obj = webResult;
					handler.sendMessage(msg);
				} 
				else {
					try {
						JSONArray content = new JSONObject(webResult).getJSONArray("MMList");
						String[] fileInfoArray = new String[4];
						for (int i = 0; i < content.length(); i++) {
							JSONObject tempObj = (JSONObject) content.get(i);
							fileInfoArray[0] += tempObj.getString("FeatureTitle")+"\n";
							fileInfoArray[1] += tempObj.getString("CreateTime")+"\n";
							fileInfoArray[2] += tempObj.getString("MMFileName")+"\n";
							fileInfoArray[3] += tempObj.getString("RecordTime")+"\n";
						}
						msg.what = 0;
						msg.obj = fileInfoArray;
						handler.sendMessage(msg);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static String getContent(String url) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			return "网络连接失败";
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

		} catch (Exception e) {
			return "下载文件列表失败";
		}
		return sb.toString();
	}

	/**
	 * 文件上传
	 * 
	 * @param urlString
	 *            上传地址
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件�?
	 * @param macID
	 *            机器ID
	 * @param comment
	 *            备注
	 * @param creatTime
	 * @param mediaLength
	 */
	public static void uploadFile(String urlString, File filePath, String fileName, String macID, String comment, String creatTime, long mediaLength, final Handler handler) {
		try {
			handler.sendEmptyMessage(0);
			Log.e("1", "1");
			String mediaType, gCodeName;
			gCodeName = fileName.substring(0, fileName.length() - 3) + "txt";
			Log.e("1", "6");
			File codePath = new File(filePath.toString().substring(0, filePath.getAbsolutePath().length() - 3) + "txt");
			Log.e("1", "2");
			if (fileName.endsWith(GlobalParameters.VIDEO_FORMAT_SUFFIX)) {
				mediaType = "4";
			} 
			else if (fileName.endsWith(GlobalParameters.AUDIO_FORMAT_SUFFIX)) {
				mediaType = "2";
			}
			else {
				mediaType = "0";
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = sdf.parse(creatTime);
			String time = format.format(date);
			RequestParams fileParams = new RequestParams();
			fileParams.put("MacID", macID);
			fileParams.put("MediaType", mediaType);
			fileParams.put("FeatureTitle", comment);
			fileParams.put("CreateTime", time);
			fileParams.put("MMFileName", fileName);
			fileParams.put("LRCFileName", gCodeName);
			fileParams.put("RecordTime", mediaLength + "");
			fileParams.put("MediaFile", filePath, null);
			if(codePath.exists())
			fileParams.put("CodeFile", codePath, null);
			Log.e("1", "3");
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(urlString, fileParams, new AsyncHttpResponseHandler() {
				public void onFailure(int arg0, org.apache.http.Header[] arg1, byte[] arg2, Throwable arg3) {

					Log.e("2", "2");
					handler.sendEmptyMessage(-1);
				}
				public void onSuccess(int arg0, org.apache.http.Header[] arg1, byte[] arg2) {
					Log.e("3", "3");
					handler.sendEmptyMessage(1);
				}
				
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
