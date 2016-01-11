package com.hznc.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class RecorderVI extends View {

		private RecordDraw mDraw = new RecordDraw();
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
		public double mMax2 = 10, mMin2 = -10;
		private int mLen1 = 1024;
		private int mLen2 = 1024;

		private double amplify = 1;
		private double py = 0;
		private int pos = 0;
		private int rFs = 1; 

		private int w = 800, h = 400, w0 = 80, h0 = 80;
		private int type = 0;

		private double wave1[] = null;
		private double wave2[] = null;

	
		public RecorderVI(Context context){
			super(context);
		}
		
		public RecorderVI(Context context,AttributeSet set){
			super(context, set);
		}
		
		public RecorderVI(Context context, AttributeSet attr, int defaultStyles) {
			super(context, attr, defaultStyles);
		}

		@Override
		public void onAttachedToWindow() {
			this.setDrawingCacheEnabled(true);
			super.onAttachedToWindow();

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
			int dr2 = 5 * w / 800;
			int c11 = mDraw.adjustBrightness(c1, 30);
			int c12 = mDraw.adjustBrightness(c1, -30);
			int dw = (w - 4 * dr2) / 11;
			mDraw.drawBorder(0, 0, w, h, c11, c12, dr2);
			int mSize = 26 * w / 800;
			h0 = (int) (h-3*mSize) / 6;
			w0 = (int) (w-0.5*dw) / 10;
			int x0 = (int) (0.7 * mSize);
			int y0 = h - dw + mSize / 2;
			mDraw.axis(x0, y0, 10, w0, 6, h0, c4);
			// ==============================================================================
			double yl1, yh1;
			yl1 = mMin1 / amplify;
			yh1 = mMax1 / amplify;
			yl1 = yl1 + py * (yh1 - yl1);
			// =================================================================================
			double yl2, yh2;
			yl2 = mMin2 / amplify;
			yh2 = mMax2 / amplify;
			yl2 = yl2 + py * (yh2 - yl2);
			
			// =================================================================================
			drawWave(mLA1, mLen1, wave1, yl1, yh1, c5, x0, y0);
			drawWave(mLA2, mLen2, wave2, yl2, yh2, c6, x0, y0);
		}

		public void drawWaveText(int n, double yl, double yh, double mX0,
				double mDt, int c, int size, int x0, int y0) {
			String s1 = String.format("%1.4g", yl);
			String s2 = String.format("%1.4g", yh);
			mDraw.darwLineText(w - size - n * (size + size / 3), y0 - size / 4, w - size - n * (size + size / 3), y0 - 5 * h0, c, size, 0, s1);
			mDraw.darwLineText(w - size - n * (size + size / 3), y0, w - size - n * (size + size / 3), y0 - 6 * h0 + size / 4, c, size, 2, s2);
			double xs = 1;
			if (rFs > 0)
				xs = rFs;
			if (rFs < 0)
				xs = -1.0 / rFs;
			s1 = String.format("%1.4g", mX0 + pos * mDt * xs);
			s2 = String.format("%1.4g", mX0 + mDt * xs * (10 * w0 + pos));
			if (n == 0) {
				mDraw.drawText(x0 + size / 4, y0 - 3 * h0 + size, c, size, s1);
				mDraw.darwLineText(x0 + size / 2, y0 - 3 * h0 + size, x0 + w0 * 10 - size / 4, y0 - 3 * h0 + size, c, size, 2, s2);
			}
			if (n == 2) {
				mDraw.drawText(x0 + size / 4, y0 - 3 * h0 + size + size + size / 3, c, size, s1);
				mDraw.darwLineText(x0 + size/2, y0 - 3*h0 + size + size + size/3, x0 + w0*10 - size/4, y0 - 3*h0 + size + size + size/3, c, size, 2, s2);
			}
			if (n == 1) {
				mDraw.drawText(x0 + size / 4, y0 - 3 * h0 - size / 3, c, size, s1);
				mDraw.darwLineText(x0 + size/2, y0 - 3 * h0 - size/3, x0 + w0*10 - size/4, y0 - 3*h0 - size/3, c, size, 2, s2);
			}
			if (n == 3) {
				mDraw.drawText(x0 + size / 4, y0 - 3 * h0 - size / 3 - size - size/ 3, c, size, s1);
				mDraw.darwLineText(x0 + size/2, y0 - 3 * h0 - size/3 - size - size/3, x0 + w0 * 10 - size/4, y0 - 3 * h0 - size/3 - size - size/3, c, size, 2, s2);
			}
		} 

		public void drawWave(int mLA, int L, double data[], double yl, double yh, int c, int x0, int y0) {
			if (data == null)
				return;
			int i, y1, k1, n;
			double z1, aa;
			n = L;
			aa = h0 * 6.0 / (yh - yl);
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
					y1 = (int) (y0 - aa * (z1 - yl));
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
					xx[i] = x0 + 10*w0*i/n;
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

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			return super.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onTouchEvent(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				int dr3 = 50 * w / 800;
				int dr1 = 16 * w / 800;
				int dw = w / 11;
				if (y > (h - dr3)) {
					int n = (int) (x / dw);
					if (n == 10) {
						amplify = 1;
						py = 0;
						pos = 0;
						rFs = 1;
					}
					if (n == 0)
						pos = mLen1 - 10 * w0;
					if (n == 1)
						pos = pos + 50;
					if (n == 2)
						pos = pos - 50;
					if (n == 3)
						pos = 0;
					if (n == 4) {
						rFs = rFs - 1;
						if (rFs == 0)
							rFs = -2;
					}
					if (n == 5) {
						rFs = rFs + 1;
						if (rFs == 0)
							rFs = 1;
						if (rFs == -1)
							rFs = 1;
					}
					if (n == 6)
						amplify = amplify * 1.25;
					if (n == 7)
						amplify = amplify * 0.8;
					if (n == 8)
						py = py - 0.05;
					if (n == 9)
						py = py + 0.05;
					postInvalidate();
				}
				if ((x < (4 * dr1)) & (y < (4 * dr1)))
					menu();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			return super.onTouchEvent(e);
		}

		public void menu() {

		}

		// ===============================================

		public void setArray(int mLen, double[] mArray, int line) {
			long endTime = Debug.threadCpuTimeNanos();
			Log.i("endtime", String.valueOf(endTime));
			if(line ==1){
				wave1 = mArray;
			}
			if(line ==2){
				wave2 = mArray;
			}
			postInvalidate();
		}

		// ===========================================
	}// End of View class �C MyView



