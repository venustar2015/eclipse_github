package com.hznc.draw;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;

//=========================================================================

public class ResonanceVI extends View {
	public ArrayList<double[]> mData = new ArrayList<double[]>();
	private PlayDraw mDraw = new PlayDraw();
	public long id = 0;
	public int c1 = 0xff000000;
	public int c2 = 0x0090d0;
	public int c3 = 0xff000044;
	public int c4 = 0xffff00;
	public int mLA1 = -1, mLA2 = -1;
	public double mXMax = 1;
	public double mMax1 = 10, mMin1 = -10;
	private int order = 30;

	// constructor 1 required for in-code creation
	public ResonanceVI(Context context) {
		super(context);
	}

	// constructor 2 required for inflation from resource file
	public ResonanceVI(Context context, AttributeSet attr) {
		super(context, attr);
	}

	// constructor 3 required for inflation from resource file
	public ResonanceVI(Context context, AttributeSet attr, int defaultStyles) {
		super(context, attr, defaultStyles);
	}

	public void setPar(int x, int y, int w, int h) {
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h, x, y));
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setDrawingCacheEnabled(true);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		int w = MeasureSpec.getSize(widthSpec);
		int h = MeasureSpec.getSize(heightSpec);
		setMeasuredDimension(w, h);
	}

	void drawResonance(Canvas canvas, int x0, int y0) {
		int pOrder = (y0-120 )/ 20;
		Path[] pathArray = new Path[pOrder];
		Paint p = new Paint();
		p.setColor(Color.YELLOW);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(2);
		int dataSize = mData.get(0).length;
		if (pOrder > mData.size())
			pOrder = mData.size();
		for (int i = 0; i < pOrder; i++) {
			pathArray[i] = new Path();
			pathArray[i].moveTo(0, 0);
			for (int j = 0; j < dataSize; j++) {
				if (mData.get(i)[j] > 100000)
					mData.get(i)[j] = 100000;
				pathArray[i].lineTo(j* (canvas.getWidth() - canvas.getHeight() / 2)/ dataSize
						, (float) (-canvas.getHeight()* mData.get(i)[j] / 65536));
			}
		}
		canvas.translate(x0 + 10, y0 - 10);
		for (int i = 0; i < pOrder; i++) {
			canvas.drawPath(pathArray[i], p);
			canvas.translate(10, -20);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int h = getMeasuredHeight();
		int w = getMeasuredWidth();
		mDraw.mCanvas = canvas;
		mDraw.fillRect(0, 0, w, h, c2);
		mDraw.drawBorder(1, 1, w - 2, h - 2, 0xff777777, 0xffdddddd, 4);
		int e1 = 35 * w / 800, e2 = 10 * w / 800;
		int dw = (int) ((w - e1 - e2) / 10);
		int dh = (int) ((h - e1 - e2) / 8);
		int x0 = e1 + ((w - e1 - e2) - dw * 10) / 2;
		int y0 = h - e1 - ((h - e1 - e2) - dh * 8) / 2;
		mDraw.arrowLine(0, 4, x0, y0, x0, y0 - 2 * dh, c1);
		mDraw.arrowLine(2, 10, x0, y0, x0 +(canvas.getWidth() - canvas.getHeight() / 2), y0, c1);
		mDraw.line(x0, y0, x0 + y0/2-60-y0%20/2, 120+y0%20, c1, 2);
		mDraw.drawText(x0 + y0/2-60-y0%20/2-50, 120+y0%20+20, c1, 25,"21");
		if (mData.isEmpty())
			return;
		drawResonance(canvas, x0, y0);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onPress(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:

			break;
		}
		return super.onTouchEvent(e);
	}

	public void onPress(float x, float y) {
		// 娣诲姞鎸夐挳鎺т欢
	}

	public int inCircle(int x, int y, int r, int x0, int y0) {
		if (x0 < (x - r))
			return -1;
		if (x0 > (x + r))
			return -1;
		if (y0 < (y - r))
			return -1;
		if (y0 > (y + r))
			return -1;
		return 1;
	}

	public void setArray(double[] mArray) {
		mData.add(0, mArray);
		if (mData.size() > order)
			mData.remove(order);
		postInvalidate();
	}

	// ===========================================
}// End of View class �??MyView