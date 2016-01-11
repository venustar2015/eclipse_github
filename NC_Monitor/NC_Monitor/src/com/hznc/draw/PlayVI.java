package com.hznc.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

//=========================================================================

public class PlayVI extends View {
	private PlayDraw mDraw = new PlayDraw();
	public long id = 0;
	public int c1 = 0xaaaaaa;
	public int c2 = 0x0090d0;
	public int c3 = 0x000000;
	public int c4 = 0xffffff;
	public int c5 = 0xffff00;
	public int c6 = 0x00ffff;
	public int c7 = 0xffffff;
	public int c8 = 0xff0000;
	public int mLA1 = -1, mLA2 = -1;
	public double mMax1 = 10, mMin1 = -10;
	public double mMax2 = 10, mMin2 = 0;
	private int mLen1 = 1024;
	private int mLen2 = 1024;

	private double amplify = 1;
	private double py = 0;
	private int pos = 0;
	private int rFs = 1; 

	private int w = 800, h = 400, w0 = 80, h0 = 80;
	private double wave1[] = null;
	private double wave2[] = null;

	// constructor 1 required for in-code creation
	public PlayVI(Context context) {
		super(context);
	}

	// constructor 2 required for inflation from resource file
	public PlayVI(Context context, AttributeSet attr) {
		super(context, attr);
	}

	// constructor 3 required for inflation from resource file
	public PlayVI(Context context, AttributeSet attr, int defaultStyles) {
		super(context, attr, defaultStyles);
	}

	@Override
	public void onAttachedToWindow() {
		this.setDrawingCacheEnabled(true);
		super.onAttachedToWindow();
//		if (!AudioPlayHandler.isPlaying)
//	    	AudioPlayHandler.audioPlayHandler.playAudio();
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		int measuredWidth = MeasureSpec.getSize(widthSpec);
		int measuredHeight = MeasureSpec.getSize(heightSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		h = getMeasuredHeight();
		w = getMeasuredWidth();
		mDraw.mCanvas = canvas;
		mDraw.fillRect(0, 0, w, h, c2);
		mDraw.adjustBrightness(c1, 30);
		mDraw.adjustBrightness(c1, -30);
		int mSize = 50* w / 800;
		h0 = (int) (h -80) / 6;
		w0 = (int) (w - 1.5*mSize) / 10;
		int x0 =mSize;
		int y0 = h-60;
		mDraw.axis(x0, y0, 10, w0, 6, h0, c4);
		double yl1, yh1;
		yl1 = mMin1 / amplify;
		yh1 = mMax1 / amplify;
		yl1 = yl1 + py * (yh1 - yl1);
		yh1 = yh1 + py * (yh1 - yl1);
		double yl2, yh2;
		yl2 = mMin2 / amplify;
		yh2 = mMax2 / amplify;
		yl2 = yl2 + py * (yh2 - yl2);
		yh2 = yh2 + py * (yh2 - yl2);
		drawWave(4, mLen1, wave1, yl1, yh1, c5, x0, y0*2/3+10);
		drawWave(2, mLen2, wave2, yl2, yh2, c6, x0, y0+10);
	}

	public void drawWave(int mLA, int L, double data[], double yl, double yh, int c, int x0, int y0) {
		if (data == null)
			return;
		int i, y1, k1, n;
		double z1, aa;
		n = L;
		aa = h0 * mLA / (yh - yl);
		if (rFs > 0) {
			int xx[] = new int[n];
			int yy[] = new int[n];
			for (i = 0; i < n; i++) {
				k1 = (i + pos) * rFs;
				z1 = 0;
				if (k1 >= 0)
					if (k1 < L)
						z1 = data[k1];
				if (z1 < yl)
					z1 = yl;
				if (z1 > yh)
					z1 = yh;
				y1 = (int) (y0- aa * (z1 - yl));
				xx[i] = x0 + 10*w0*i/n;
				yy[i] = y1;
			}
			mDraw.lineMulti(n - 1, xx, yy, c, 2);
		}
		if (rFs < 0) {
			int s = -rFs;
			double[] xs = new double[1025];
			waveZoomOut(1024 / s, s, pos, data, xs);
			if (n > 1024)
				n = 1024;
			int xx[] = new int[n];
			int yy[] = new int[n];
			for (i = 0; i < n - 1; i++) {
				k1 = i;
				z1 = 0;
				if (k1 >= 0)
					if (k1 < L)
						z1 = xs[k1];
				if (z1 < yl)
					z1 = yl;
				if (z1 > yh)
					z1 = yh;
				y1 = (int) (y0 - aa * (z1 - yl));
				xx[i] = x0 + i;
				yy[i] = y1;
			}
			mDraw.lineMulti(n - 1, xx, yy, c, 2);
		}
	}

	public static void waveZoomOut(int len, int n, int pos, double[] in,
			double[] out) {
		int i, j, k;
		double y1, y2;
		for (i = 0; i < len - 1; i++) {
			y1 = 0;
			y2 = 0;
			k = pos + i;
			if (k > 0) {
				y1 = in[pos + i];
				y2 = in[pos + i + 1];
			}
			for (j = 0; j < n; j++)
				out[i * n + j] = y1 + j * (y2 - y1) / n;
		}
		for (j = 0; j < n; j++)
			out[(len - 1) * n + j] = in[len - 1];
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.invalidate();
	}

	// ===============================================

	public void setArray(int mLen, double[] mArray, int line) {
		long endTime = Debug.threadCpuTimeNanos();
		Log.i("endtime", String.valueOf(endTime));
		if(line ==1){
			mLen1 = mLen;
			wave1 = mArray;
		}
		if(line ==2){
			mLen2 = mLen;
			wave2 = mArray;
		}
		postInvalidate();
	}

	// ===========================================
}// End of View class �C MyView

