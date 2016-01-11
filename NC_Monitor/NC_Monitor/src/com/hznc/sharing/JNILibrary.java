package com.hznc.sharing;

/**
 * 该类是调用动态链接库的接口�??<br />
 * MP3编码�?要调用到第三方的C++库�?�要使用到JNI编程�?术�?�这里不详细说明原理。仅仅说明功能�??
 */
public class JNILibrary {
	
	/**
	 * 本类的唯�?实例对象。构造方法为私有。这是简化的单例设计模式�?
	 */
	public static JNILibrary jniLibrary=new JNILibrary();
	private JNILibrary(){}
	
	/**
	 * 调用该方法，会初始化MP3编码器�??<br />
	 * @param channel 通道配置�?
	 * @param sampleRate 采样率�??
	 * @param brate 比特率�?�单位kbps�?
	 */
	public native void init(int channel, int sampleRate, int brate);
	
	/**
	 * 调用该方法会�?毁MP3编码器，释放资源�?
	 */
	public native void destroy();
	
	/**
	 * 调用该方法对数据进行编码�?
	 * @param buffer �?要编码的数据�?
	 * @param len buffer的大小�?�调用该方法实际会调用到对应的动态链接库里面的c++函数，所以buffer的长度必须传递给该方法作为c++函数的参数�??
	 * @return MP3编码后的数据�?
	 */
	public native byte[] encode(short[] buffer, int len);
	
	/**
	 * 该静态块用于在类加载时加载动态链接库�?<br />
	 * 注：android内核基于linux内核，其动�?�链接库完整文件名为"lib"+"文件�?"+".so"�?"mp3lame"对应的动态链接库文件�?"libmp3lame.so"�?
	 */
	static{
		System.loadLibrary("mp3lame");
	}
}
