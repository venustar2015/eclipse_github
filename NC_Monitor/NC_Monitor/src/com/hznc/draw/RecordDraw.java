package com.hznc.draw;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Paint.FontMetrics;

public class RecordDraw {
	public Canvas mCanvas;

	public int adjustBrightness(int color, int percent) {
		int nChange = 255 * percent / 100;
		int r = ((color & 0x00FF0000) >> 16) + nChange;
		int g = ((color & 0x0000FF00) >> 8) + nChange;
		int b = (color & 0x000000FF) + nChange;
		r = r < 0 ? 0 : (r > 255 ? 255 : r);
		g = g < 0 ? 0 : (g > 255 ? 255 : g);
		b = b < 0 ? 0 : (b > 255 ? 255 : b);
		int c = (r << 16) | (g << 8) | b;
		return (c + 0xff000000);
	}

	public void fillRectText(int x, int y, int w, int h, int c, String text) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(c | 0xff000000);
		mPaint.setTextSize(h);
		Float tw = mPaint.measureText(text);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
		int dy = (int) (fontMetrics.bottom - fontMetrics.top);
		float f = w / tw;
		mPaint.setTextScaleX(f);
		mCanvas.drawText(text, x, y + dy / 2 - (h - dy) / 2, mPaint);
	}

	public void drawText(int x, int y, int c, int size, String text) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(c | 0xff000000);
		mPaint.setTextSize(size);
		mCanvas.drawText(text, x, y, mPaint);
	}

	public void drawRectText(int x, int y, int w, int h, int c, int size,
			int type, String text) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(c | 0xff000000);
		mPaint.setTextSize(size);
		Float tw = mPaint.measureText(text);
		if (type == 0)
			mCanvas.drawText(text, x, y + h / 2 + size / 2, mPaint);
		if (type == 1)
			mCanvas.drawText(text, x + w / 2 - tw / 2, y + h / 2 + size / 2,
					mPaint);
		if (type == 2)
			mCanvas.drawText(text, x + w - tw, y + h / 2 + size / 2, mPaint);
	}

	public void drawArcText(int x, int y, int r, int a1, int a2, int c,
			int size, int type, String text) {
		Path p = new Path();
		p.arcTo(new RectF(x - r, y - r, x + r, y + r), a1, a2);
		pathTextOut(p, c, size, type, text);
	}

	public void darwLineText(int x1, int y1, int x2, int y2, int c, int size,
			int type, String text) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		pathTextOut(p, c, size, type, text);
	}

	public void pathTextOut(Path p, int c, int size, int type, String text) {
		Paint mPathPaint = new Paint();
		mPathPaint.setAntiAlias(true);
		mPathPaint.setColor(c | 0xff000000);
		mPathPaint.setTextSize(size);
		if (type == 0)
			mPathPaint.setTextAlign(Paint.Align.LEFT);
		else if (type == 1)
			mPathPaint.setTextAlign(Paint.Align.CENTER);
		else
			mPathPaint.setTextAlign(Paint.Align.RIGHT);
		mCanvas.drawTextOnPath(text, p, 0, 0, mPathPaint);
	}

	public void line(int x1, int y1, int x2, int y2, int c, int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawLine(x1, y1, x2, y2, mPaint);
	}

	public void lineMulti(int n, int x[], int y[], int c, int thick) {
		int i, j;
		float p[] = new float[(n - 1) * 4];
		for (i = 0; i < n - 1; i++) {
			j = 4 * i;
			p[j + 0] = x[i];
			p[j + 1] = y[i];
			p[j + 2] = x[i + 1];
			p[j + 3] = y[i + 1];
		}
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawLines(p, mPaint);
	}

	public void dotLine(int x1, int y1, int x2, int y2, int c, int thick) {
		Paint mPaint = new Paint();
		setDotPen(mPaint, c, thick);
		mCanvas.drawLine(x1, y1, x2, y2, mPaint);
	}

	public void gradLine(int x1, int y1, int x2, int y2, int c1, int c2,
			int thick, int type) {
		Paint mPaint = new Paint();
		int x = x1;
		if (x > x2)
			x = x2;
		int y = y1;
		if (y > y2)
			y = x2;
		int w = Math.abs(x1 - x2);
		int h = Math.abs(y1 - y2);
		setLineGrad(mPaint, x, y, w, h, c1, c2, type, thick);
		mCanvas.drawLine(x1, y1, x2, y2, mPaint);
	}

	public void arc(int x, int y, int r1, int r2, int a1, int a2, int c,
			int thick, boolean type) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawArc(new RectF(x - r1, y - r2, x + r1, y + r2), a1, a2,
				type, mPaint);
	}

	public void gradArc(int x, int y, int r1, int r2, int a1, int a2, int c1,
			int c2, int thick, int type) {
		Paint mPaint = new Paint();
		int x1 = x + (int) (r1 * Math.cos(a1 * 3.14 / 180));
		int y1 = y + (int) (r1 * Math.sin(a1 * 3.14 / 180));
		int x2 = x + (int) (r1 * Math.cos((a1 + a2) * 3.14 / 180));
		int y2 = y + (int) (r1 * Math.sin((a1 + a2) * 3.14 / 180));
		int xx = x1;
		if (xx > x2)
			xx = x2;
		int yy = y1;
		if (yy > y2)
			yy = x2;
		int w = Math.abs(x1 - x2);
		int h = Math.abs(y1 - y2);
		setLineGrad(mPaint, x, y, w, h, c1, c2, type, thick);
		mCanvas.drawArc(new RectF(x - r1, y - r2, x + r1, y + r2), a1, a2,
				false, mPaint);
	}

	public void fillarc(int x, int y, int r1, int r2, int a1, int a2, int c,
			boolean type) {
		Paint mPaint = new Paint();
		setFill(mPaint, c);
		mCanvas.drawArc(new RectF(x - r1, y - r2, x + r1, y + r2), a1, a2,
				type, mPaint);
	}

	public void fillGrdarc(int x, int y, int r1, int r2, int a1, int a2,
			int c1, int c2, int fillType, boolean type) {
		Paint mPaint = new Paint();
		int x1 = x + (int) (r1 * Math.cos(a1 * 3.14 / 180));
		int y1 = y + (int) (r1 * Math.sin(a1 * 3.14 / 180));
		int x2 = x + (int) (r1 * Math.cos((a1 + a2) * 3.14 / 180));
		int y2 = y + (int) (r1 * Math.sin((a1 + a2) * 3.14 / 180));
		int xl = x1;
		int xh = x1;
		if (xl > x2)
			xl = x2;
		if (xh < x2)
			xh = x2;
		int yl = y1;
		int yh = y1;
		if (yl > y2)
			yl = y2;
		if (yh < y2)
			yh = y2;
		int a3 = a1 + a2 / 2;
		int x3 = x + (int) (r1 * Math.cos(a3 * 3.14 / 180));
		int y3 = y + (int) (r1 * Math.sin(a3 * 3.14 / 180));
		if (xl > x3)
			xl = x3;
		if (xh < x3)
			xh = x3;
		if (yl > y3)
			yl = y3;
		if (yh < y3)
			yh = y3;
		int a4 = a1 + a2 / 4;
		int x4 = x + (int) (r1 * Math.cos(a4 * 3.14 / 180));
		int y4 = y + (int) (r1 * Math.sin(a4 * 3.14 / 180));
		if (xl > x4)
			xl = x4;
		if (xh < x4)
			xh = x4;
		if (yl > y4)
			yl = y4;
		if (yh < y4)
			yh = y4;
		int a5 = a1 + a2 / 2 + a2 / 4;
		int x5 = x + (int) (r1 * Math.cos(a5 * 3.14 / 180));
		int y5 = y + (int) (r1 * Math.sin(a5 * 3.14 / 180));
		if (xl > x5)
			xl = x5;
		if (xh < x5)
			xh = x5;
		if (yl > y5)
			yl = y5;
		if (yh < y5)
			yh = y5;
		int w = xh - xl;
		int h = yh - yl;
		setGradType(mPaint, xl, yl, w, h, c1, c2, fillType);
		mCanvas.drawArc(new RectF(x - r1, y - r2, x + r1, y + r2), a1, a2,
				type, mPaint);
	}

	public void drawRoundBorder(int x, int y, int r1, int r2, int c1, int c2,
			int thick) {
		Paint mPaint = new Paint();
		int w = thick / 2;
		r1 = r1 - w;
		r2 = r2 - w;
		setLineGrad(mPaint, x - r1, y - r2, 2 * r1, 2 * r2, c1, c2, 0, thick);
		mCanvas.drawArc(new RectF(x - r1, y - r2, x + r1, y + r2), 0, 360,
				false, mPaint);
	}

	public void drawBorder(int x, int y, int w, int h, int c1, int c2, int thick) {
		int d = thick / 2;
		x = x + d;
		y = y + d;
		w = w - 2 * d;
		h = h - 2 * d;
		line(x, y - d, x, y + h + d, c1, thick);
		line(x - d, y, x + w + d, y, c1, thick);
		line(x + w, y, x + w, y + h, c2, thick);
		line(x, y + h, x + w + d, y + h, c2, thick);
	}

	public void drawRoundRectBorder(int x, int y, int w, int h, int r, int c1,
			int c2, int thick) {
		Paint mPaint = new Paint();
		int d = thick / 2;
		x = x + d;
		y = y + d;
		w = w - 2 * d;
		h = h - 2 * d;
		setLineGrad(mPaint, x, y, w, h, c1, c2, 2, thick);
		mCanvas.drawLine(x, y + r, x, y + h - r, mPaint);
		mCanvas.drawLine(x + r, y, x + w - r, y, mPaint);
		mCanvas.drawLine(x + w, y + r, x + w, y + h - r, mPaint);
		mCanvas.drawLine(x + r, y + h, x + w - r, y + h, mPaint);
		mCanvas.drawArc(new RectF(x + r - r, y + r - r, x + r + r, y + r + r),
				-180, 90, false, mPaint);
		mCanvas.drawArc(new RectF(x + w - r - r, y + r - r, x + w - r + r, y
				+ r + r), -90, 90, false, mPaint);
		mCanvas.drawArc(new RectF(x + w - r - r, y + h - r - r, x + w - r + r,
				y + h - r + r), 0, 90, false, mPaint);
		mCanvas.drawArc(new RectF(x + r - r, y + h - r - r, x + r + r, y + h
				- r + r), 90, 90, false, mPaint);
	}

	public void oval(int x, int y, int w, int h, int c, int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		RectF oval = new RectF(x, y, x + w, y + h);
		mCanvas.drawOval(oval, mPaint);
	}

	public void filloval(int x, int y, int w, int h, int c) {
		Paint mPaint = new Paint();
		setFill(mPaint, c);
		RectF oval = new RectF(x, y, x + w, y + h);
		mCanvas.drawOval(oval, mPaint);
	}

	public void fillgradoval(int x, int y, int w, int h, int c1, int c2, int t) {
		Paint mPaint = new Paint();
		setGradType(mPaint, x, y, w, h, c1, c2, t);
		RectF oval = new RectF(x, y, x + w, y + h);
		mCanvas.drawOval(oval, mPaint);
	}

	public void circle(int x, int y, int r, int c, int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawCircle(x, y, r, mPaint);
	}

	public void fillcircle(int x, int y, int r, int c) {
		Paint mPaint = new Paint();
		setFill(mPaint, c);
		mCanvas.drawCircle(x, y, r, mPaint);
	}

	public void fillGrdcircle(int x, int y, int r, int c1, int c2, int t) {
		Paint mPaint = new Paint();
		setGradType(mPaint, x - r, y - r, 2 * r, 2 * r, c1, c2, t);
		mCanvas.drawCircle(x, y, r, mPaint);
	}

	public void fillRing(int x, int y, int r, int dr, int c) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, dr);
		mCanvas.drawCircle(x, y, r - dr / 2, mPaint);
	}

	public void fillGradRing(int x, int y, int r, int dr, int c1, int c2, int t) {
		Paint mPaint = new Paint();
		setLineGrad(mPaint, x - r, y - r, 2 * r, 2 * r, c1, c2, t, dr);
		mCanvas.drawCircle(x, y, r - dr / 2, mPaint);
	}

	public void drawRect(int x, int y, int w, int h, int c, int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawRect(new RectF(x, y, x + w, y + h), mPaint);
	}

	public void fillRect(int x, int y, int w, int h, int c) {
		Paint mPaint = new Paint();
		setFill(mPaint, c | 0xff000000);
		mCanvas.drawRect(new RectF(x, y, x + w, y + h), mPaint);
	}

	public void fillGradRect(int x, int y, int w, int h, int c1, int c2, int t) {
		Paint mPaint = new Paint();
		setGradType(mPaint, x, y, w, h, c1, c2, t);
		mCanvas.drawRect(new RectF(x, y, x + w, y + h), mPaint);
	}

	public void drawRoundRect(int x, int y, int w, int h, int r, int c,
			int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawRoundRect(new RectF(x, y, x + w, y + h), r, r, mPaint);
	}

	public void fillRoundRect(int x, int y, int w, int h, int r, int c) {
		Paint mPaint = new Paint();
		setFill(mPaint, c | 0xff000000);
		mCanvas.drawRoundRect(new RectF(x, y, x + w, y + h), r, r, mPaint);
	}

	public void fillGradRoundRect(int x, int y, int w, int h, int r, int c1,
			int c2, int t) {
		Paint mPaint = new Paint();
		setGradType(mPaint, x, y, w, h, c1, c2, t);
		mCanvas.drawRoundRect(new RectF(x, y, x + w, y + h), r, r, mPaint);
	}

	public void drawTrangle(int x1, int y1, int x2, int y2, int x3, int y3,
			int c, int thick) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x1, y1);
		drawPath(p, c, thick);
	}

	public void fillTrangle(int x1, int y1, int x2, int y2, int x3, int y3,
			int c) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x1, y1);
		fillPath(p, c);
	}

	public void fillGradTrangle(int x1, int y1, int x2, int y2, int x3, int y3,
			int c1, int c2, int t) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x1, y1);
		int xl = x1;
		int xh = x1;
		int yl = y1;
		int yh = y1;
		if (xl > x2)
			xl = x2;
		if (xl > x3)
			xl = x3;
		if (xh < x2)
			xh = x2;
		if (xh < x3)
			xh = x3;
		if (yl > y2)
			yl = y2;
		if (yl > y3)
			yl = y3;
		if (yh < y2)
			yh = y2;
		if (yh < y3)
			yh = y3;
		fillGradPath(p, xl, yl, xh - xl, yh - yl, c1, c2, t);
	}

	public void drawFour(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int c, int thick) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x1, y1);
		drawPath(p, c, thick);
	}

	public void fillFour(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int c) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x1, y1);
		fillPath(p, c);
	}

	public void fillGradFour(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int c1, int c2, int t) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x1, y1);
		int xl = x1;
		int xh = x1;
		int yl = y1;
		int yh = y1;
		if (xl > x2)
			xl = x2;
		if (xl > x3)
			xl = x3;
		if (xl > x4)
			xl = x4;
		if (xh < x2)
			xh = x2;
		if (xh < x3)
			xh = x3;
		if (xh < x4)
			xh = x4;
		if (yl > y2)
			yl = y2;
		if (yl > y3)
			yl = y3;
		if (yl > y4)
			yl = y4;
		if (yh < y2)
			yh = y2;
		if (yh < y3)
			yh = y3;
		if (yh < y4)
			yh = y4;
		fillGradPath(p, xl, yl, xh - xl, yh - yl, c1, c2, t);
	}

	public void drawFive(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int x5, int y5, int c, int thick) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x1, y1);
		drawPath(p, c, thick);
	}

	public void fillFive(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int x5, int y5, int c) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x1, y1);
		fillPath(p, c);
	}

	public void fillGradFive(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int x5, int y5, int c1, int c2, int t) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x1, y1);
		int xl = x1;
		int xh = x1;
		int yl = y1;
		int yh = y1;
		if (xl > x2)
			xl = x2;
		if (xl > x3)
			xl = x3;
		if (xl > x4)
			xl = x4;
		if (xl > x5)
			xl = x5;
		if (xh < x2)
			xh = x2;
		if (xh < x3)
			xh = x3;
		if (xh < x4)
			xh = x4;
		if (xh < x5)
			xh = x5;
		if (yl > y2)
			yl = y2;
		if (yl > y3)
			yl = y3;
		if (yl > y4)
			yl = y4;
		if (yl > y5)
			yl = y5;
		if (yh < y2)
			yh = y2;
		if (yh < y3)
			yh = y3;
		if (yh < y4)
			yh = y4;
		if (yh < y5)
			yh = y5;
		fillGradPath(p, xl, yl, xh - xl, yh - yl, c1, c2, t);
	}

	public void drawSix(int x1, int y1, int x2, int y2, int x3, int y3, int x4,
			int y4, int x5, int y5, int x6, int y6, int c, int thick) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x6, y6);
		p.lineTo(x1, y1);
		fillPath(p, c);
	}

	public void fillSix(int x1, int y1, int x2, int y2, int x3, int y3, int x4,
			int y4, int x5, int y5, int x6, int y6, int c) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x6, y6);
		p.lineTo(x1, y1);
		fillPath(p, c);
	}

	public void fillGradSix(int x1, int y1, int x2, int y2, int x3, int y3,
			int x4, int y4, int x5, int y5, int x6, int y6, int c1, int c2,
			int t) {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.lineTo(x4, y4);
		p.lineTo(x5, y5);
		p.lineTo(x6, y6);
		p.lineTo(x1, y1);
		int xl = x1;
		int xh = x1;
		int yl = y1;
		int yh = y1;
		if (xl > x2)
			xl = x2;
		if (xl > x3)
			xl = x3;
		if (xl > x4)
			xl = x4;
		if (xl > x5)
			xl = x5;
		if (xl > x6)
			xl = x6;
		if (xh < x2)
			xh = x2;
		if (xh < x3)
			xh = x3;
		if (xh < x4)
			xh = x4;
		if (xh < x5)
			xh = x5;
		if (xh < x6)
			xh = x6;
		if (yl > y2)
			yl = y2;
		if (yl > y3)
			yl = y3;
		if (yl > y4)
			yl = y4;
		if (yl > y5)
			yl = y5;
		if (yl > y6)
			yl = y6;
		if (yh < y2)
			yh = y2;
		if (yh < y3)
			yh = y3;
		if (yh < y4)
			yh = y4;
		if (yh < y5)
			yh = y5;
		if (yh < y6)
			yh = y6;
		fillGradPath(p, xl, yl, xh - xl, yh - yl, c1, c2, t);
	}

	public void Number(int n, int x, int y, double fx, double fy, int c) {
		int x0, x1, x2, x3, x4, x5, y0, y1, y2, y3, y4, y5;
		if ((n == 8) | (n == 0) | (n == 2) | (n == 3) | (n == 5) | (n == 6)
				| (n == 7) | (n == 9) | (n == -1)) {
			x0 = (int) (x + 1 * fx);
			y0 = y;
			x1 = (int) (x + 10 * fx);
			y1 = y;
			x2 = (int) (x + 8 * fx);
			y2 = (int) (y + 2 * fy);
			x3 = (int) (x + 3 * fx);
			y3 = (int) (y + 2 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if ((n == 8) | (n == 0) | (n == 4) | (n == 5) | (n == 6) | (n == 9)
				| (n == -1)) {
			x0 = (int) (x);
			y0 = (int) (y + 1 * fy);
			x1 = (int) (x);
			y1 = (int) (y + 7 * fy);
			x2 = (int) (x + 2 * fx);
			y2 = (int) (y + 6 * fy);
			x3 = (int) (x + 2 * fx);
			y3 = (int) (y + 3 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if ((n == 8) | (n == 0) | (n == 2) | (n == 6) | (n == -1)) {
			x0 = (int) (x);
			y0 = (int) (y + 9 * fy);
			x1 = (int) (x);
			y1 = (int) (y + 15 * fy);
			x2 = (int) (x + 2 * fx);
			y2 = (int) (y + 13 * fy);
			x3 = (int) (x + 2 * fx);
			y3 = (int) (y + 10 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if ((n == 8) | (n == 2) | (n == 3) | (n == 4) | (n == 5) | (n == 6)
				| (n == 9) | (n == 11) | (n == -1)) {
			x0 = (int) (x + 3 * fx);
			y0 = (int) (y + 7 * fy);
			x1 = (int) (x + 2 * fx);
			y1 = (int) (y + 8 * fy);
			x2 = (int) (x + 3 * fx);
			y2 = (int) (y + 9 * fy);
			x3 = (int) (x + 8 * fx);
			y3 = (int) (y + 9 * fy);
			x4 = (int) (x + 9 * fx);
			y4 = (int) (y + 8 * fy);
			x5 = (int) (x + 8 * fx);
			y5 = (int) (y + 7 * fy);
			fillSix(x0, y0, x1, y1, x2, y2, x3, y3, x4, y4, x5, y5, c);
		}

		if ((n == 8) | (n == 0) | (n == 2) | (n == 3) | (n == 5) | (n == 6)
				| (n == 9) | (n == -1)) {
			x0 = (int) (x + 1 * fx);
			y0 = (int) (y + 16 * fy);
			x1 = (int) (x + 10 * fx);
			y1 = (int) (y + 16 * fy);
			x2 = (int) (x + 8 * fx);
			y2 = (int) (y + 14 * fy);
			x3 = (int) (x + 3 * fx);
			y3 = (int) (y + 14 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if ((n == 8) | (n == 0) | (n == 1) | (n == 2) | (n == 3) | (n == 4)
				| (n == 7) | (n == 9) | (n == -1)) {
			x0 = (int) (x + 11 * fx);
			y0 = (int) (y + 1 * fy);
			x1 = (int) (x + 11 * fx);
			y1 = (int) (y + 7 * fy);
			x2 = (int) (x + 9 * fx);
			y2 = (int) (y + 6 * fy);
			x3 = (int) (x + 9 * fx);
			y3 = (int) (y + 3 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if ((n == 8) | (n == 0) | (n == 1) | (n == 3) | (n == 4) | (n == 5)
				| (n == 6) | (n == 7) | (n == 9) | (n == -1)) {
			x0 = (int) (x + 11 * fx);
			y0 = (int) (y + 9 * fy);
			x1 = (int) (x + 11 * fx);
			y1 = (int) (y + 15 * fy);
			x2 = (int) (x + 9 * fx);
			y2 = (int) (y + 13 * fy);
			x3 = (int) (x + 9 * fx);
			y3 = (int) (y + 10 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}

		if (n == 10) // Dot
		{
			x = (int) (x + 4 * fx);
			y = (int) (y + 1 * fy);
			x0 = (int) (x + 5 * fx);
			y0 = (int) (y + 10 * fy);
			x1 = (int) (x + 5 * fx);
			y1 = (int) (y + 13 * fy);
			x2 = (int) (x + 7 * fx);
			y2 = (int) (y + 13 * fy);
			x3 = (int) (x + 7 * fx);
			y3 = (int) (y + 10 * fy);
			fillFour(x0, y0, x1, y1, x2, y2, x3, y3, c);
		}
	}

	public void drawchar(int x, int y, double fx, double fy, char c, int s) {
		if (c == '#')
			Number(-1, x, y, fx, fy, s);
		if (c == '-')
			Number(11, x, y, fx, fy, s);
		if (c == '.')
			Number(10, x, y, fx, fy, s);
		if (c == '0')
			Number(0, x, y, fx, fy, s);
		if (c == '1')
			Number(1, x, y, fx, fy, s);
		if (c == '2')
			Number(2, x, y, fx, fy, s);
		if (c == '3')
			Number(3, x, y, fx, fy, s);
		if (c == '4')
			Number(4, x, y, fx, fy, s);
		if (c == '5')
			Number(5, x, y, fx, fy, s);
		if (c == '6')
			Number(6, x, y, fx, fy, s);
		if (c == '7')
			Number(7, x, y, fx, fy, s);
		if (c == '8')
			Number(8, x, y, fx, fy, s);
		if (c == '9')
			Number(9, x, y, fx, fy, s);
	}

	public void drawPath(Path p, int c, int thick) {
		Paint mPaint = new Paint();
		setPen(mPaint, c, thick);
		mCanvas.drawPath(p, mPaint);
	}

	public void fillPath(Path p, int c) {
		Paint mPaint = new Paint();
		setFill(mPaint, c | 0xff000000);
		mCanvas.drawPath(p, mPaint);
	}

	public void fillGradPath(Path p, int x, int y, int w, int h, int c1,
			int c2, int t) {
		Paint mPaint = new Paint();
		setGradType(mPaint, x, y, w, h, c1, c2, t);
		mCanvas.drawPath(p, mPaint);
	}

	public void drawIcon(int type, int x, int y, int r, int c) {
		int x1 = 0, x2 = 0, x3 = 0, y1 = 0, y2 = 0, y3 = 0;
		if (type == 0) {
			x1 = x - r;
			y1 = y;
			x2 = x - r + (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x - r + (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 + (int) (0.5 * r);
			x2 = x2 + (int) (0.5 * r);
			x3 = x3 + (int) (0.5 * r);
			line(x1, y - (int) (0.5 * r), x1, y + (int) (0.5 * r), c,
					(int) (0.1 * r));
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 1) {
			x1 = x - r;
			y1 = y;
			x2 = x - r + (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x - r + (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 + (int) (0.5 * r);
			x2 = x2 + (int) (0.5 * r);
			x3 = x3 + (int) (0.5 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 2) {
			x1 = x + r;
			y1 = y;
			x2 = x + r - (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x + r - (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 - (int) (0.5 * r);
			x2 = x2 - (int) (0.5 * r);
			x3 = x3 - (int) (0.5 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 3) {
			x1 = x + r;
			y1 = y;
			x2 = x + r - (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x + r - (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 - (int) (0.5 * r);
			x2 = x2 - (int) (0.5 * r);
			x3 = x3 - (int) (0.5 * r);
			line(x1, y - (int) (0.5 * r), x1, y + (int) (0.5 * r), c,
					(int) (0.1 * r));
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 4) {
			r = (int) (r * 0.8);
			x1 = x - r;
			y1 = y;
			x2 = x - r + (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x - r + (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 + (int) (0.1 * r);
			x2 = x2 + (int) (0.1 * r);
			x3 = x3 + (int) (0.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
			x1 = x + r;
			y1 = y;
			x2 = x + r - (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x + r - (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 - (int) (0.1 * r);
			x2 = x2 - (int) (0.1 * r);
			x3 = x3 - (int) (0.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 5) {
			r = (int) (r * 0.8);
			x1 = x - r;
			y1 = y;
			x2 = x - r + (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x - r + (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 + (int) (1.1 * r);
			x2 = x2 + (int) (1.1 * r);
			x3 = x3 + (int) (1.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
			x1 = x + r;
			y1 = y;
			x2 = x + r - (int) (0.866 * r);
			y2 = y - (int) (0.5 * r);
			x3 = x + r - (int) (0.866 * r);
			y3 = y + (int) (0.5 * r);
			x1 = x1 - (int) (1.1 * r);
			x2 = x2 - (int) (1.1 * r);
			x3 = x3 - (int) (1.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 6) {
			r = (int) (r * 0.8);
			x1 = x;
			y1 = y - r;
			x2 = x - (int) (0.5 * r);
			y2 = y - r + (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y - r + (int) (0.866 * r);
			y1 = y1 + (int) (0.1 * r);
			y2 = y2 + (int) (0.1 * r);
			y3 = y3 + (int) (0.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
			x1 = x;
			y1 = y + r;
			x2 = x - (int) (0.5 * r);
			y2 = y + r - (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y + r - (int) (0.866 * r);
			y1 = y1 - (int) (0.1 * r);
			y2 = y2 - (int) (0.1 * r);
			y3 = y3 - (int) (0.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 7) {
			r = (int) (r * 0.8);
			x1 = x;
			y1 = y - r;
			x2 = x - (int) (0.5 * r);
			y2 = y - r + (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y - r + (int) (0.866 * r);
			y1 = y1 + (int) (1.1 * r);
			y2 = y2 + (int) (1.1 * r);
			y3 = y3 + (int) (1.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
			x1 = x;
			y1 = y + r;
			x2 = x - (int) (0.5 * r);
			y2 = y + r - (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y + r - (int) (0.866 * r);
			y1 = y1 - (int) (1.1 * r);
			y2 = y2 - (int) (1.1 * r);
			y3 = y3 - (int) (1.1 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 8) {
			x1 = x;
			y1 = y - r;
			x2 = x - (int) (0.5 * r);
			y2 = y - r + (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y - r + (int) (0.866 * r);
			y1 = y1 + (int) (0.5 * r);
			y2 = y2 + (int) (0.5 * r);
			y3 = y3 + (int) (0.5 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 9) {
			x1 = x;
			y1 = y + r;
			x2 = x - (int) (0.5 * r);
			y2 = y + r - (int) (0.866 * r);
			x3 = x + (int) (0.5 * r);
			y3 = y + r - (int) (0.866 * r);
			y1 = y1 - (int) (0.5 * r);
			y2 = y2 - (int) (0.5 * r);
			y3 = y3 - (int) (0.5 * r);
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (type == 11) {
			r = (int) (r * 0.7);
			drawRect(x - r, y - r, 2 * r, 2 * r, c, 1);
			drawRect(x - r, y - r, 2 * r, 2 * r, c, 1);
			line((int) (x - 0.7 * r), y, (int) (x + 0.7 * r), y, c, 1);
			line((int) (x - 0.7 * r), (int) (y + 0.5 * r), (int) (x + 0.7 * r),
					(int) (y + 0.5 * r), c, 1);
			line((int) (x - 0.7 * r), (int) (y - 0.5 * r), (int) (x + 0.7 * r),
					(int) (y - 0.5 * r), c, 1);
		}
		if (type == 10) {
			int t = (int) (0.1 * r);
			line(x - (int) (0.6 * r), y - (int) (0.4 * r), x + (int) (0.6 * r),
					y - (int) (0.4 * r), c, t);
			line(x + (int) (0.6 * r), y - (int) (0.4 * r), x + (int) (0.6 * r),
					y + (int) (0.4 * r), c, t);
			line(x, y + (int) (0.4 * r), x + (int) (0.6 * r), y
					+ (int) (0.4 * r), c, t);
			line(x - (int) (0.6 * r), y - (int) (0.4 * r), x - (int) (0.1 * r),
					y - (int) (0.3 * r), c, t);
			line(x - (int) (0.6 * r), y - (int) (0.4 * r), x - (int) (0.1 * r),
					y - (int) (0.5 * r), c, t);
		}
		if (type == 12) {
			int r1 = (int) (r * 0.7);
			int r2 = (int) (r * 0.5);
			int t1 = (int) (0.2 * r1);
			int t2 = (int) (0.1 * r1);
			circle(x, y, r1, c, t1);
			line(x - r2, y, x + r2, y, c, t2);
			line(x, y - r2, x, y + r2, c, t2);
		}
		if (type == 13) {
			int r1 = (int) (r * 0.7);
			int r2 = (int) (r * 0.5);
			int t1 = (int) (0.2 * r1);
			int t2 = (int) (0.1 * r1);
			circle(x, y, r1, c, t1);
			line(x - r2, y, x + r2, y, c, t2);
		}
	}

	public void setPen(Paint mPaint, int c, int thick) {
		mPaint.setColor(c | 0xff000000);
		mPaint.setStrokeWidth(thick);
		mPaint.setStyle(Paint.Style.STROKE); // ����
		mPaint.setAlpha(0xff);
		mPaint.setAntiAlias(true);
	}

	public void setDotPen(Paint mPaint, int c, int thick) {
		mPaint.setColor(c | 0xff000000);
		mPaint.setStrokeWidth(thick);
		mPaint.setStyle(Paint.Style.STROKE); // ����
		mPaint.setAlpha(0xff);
		mPaint.setAntiAlias(true);
		mPaint.setPathEffect(new DashPathEffect(new float[] { 5, 5 }, 0));
	}

	public void setFill(Paint mPaint, int c) {
		mPaint.setColor(c | 0xff000000);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAlpha(0xff);
		mPaint.setAntiAlias(true);
	}

	public void setLineGrad(Paint mPaint, int x, int y, int w, int h, int c1,
			int c2, int t, int thick) {
		setGradType(mPaint, x, y, w, h, c1, c2, t);
		mPaint.setStrokeWidth(thick);
		mPaint.setStyle(Paint.Style.STROKE);
	}

	public void setGradType(Paint mPaint, int x, int y, int w, int h, int c1,
			int c2, int t) {
		LinearGradient lg = null;
		RadialGradient lg1 = null;
		SweepGradient lg2 = null;
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAlpha(0xff);
		mPaint.setAntiAlias(true);
		if (t == 0)
			lg = new LinearGradient(x, y, x + w, y + h, c1, c2,
					Shader.TileMode.MIRROR);
		if (t == 1)
			lg = new LinearGradient(x + w, y, x, y + h, c1, c2,
					Shader.TileMode.MIRROR);
		if (t == 2)
			lg = new LinearGradient(x + w / 2, y, x + w / 2, y + h, c1, c2,
					Shader.TileMode.MIRROR);
		if (t == 3)
			lg = new LinearGradient(x, y + h / 2, x + w, y + h / 2, c1, c2,
					Shader.TileMode.MIRROR);
		if (t == 4)
			lg = new LinearGradient(x, y, x + w, y + h,
					new int[] { c1, c2, c1 }, new float[] { 0, 0.5f, 1.0f },
					Shader.TileMode.MIRROR);
		if (t == 5)
			lg = new LinearGradient(x + w, y, x, y + h,
					new int[] { c1, c2, c1 }, new float[] { 0, 0.5f, 1.0f },
					Shader.TileMode.MIRROR);
		if (t == 6)
			lg = new LinearGradient(x + w / 2, y, x + w / 2, y + h, new int[] {
					c1, c2, c1 }, new float[] { 0, 0.5f, 1.0f },
					Shader.TileMode.MIRROR);
		if (t == 7)
			lg = new LinearGradient(x, y + h / 2, x + w, y + h / 2, new int[] {
					c1, c2, c1 }, new float[] { 0, 0.5f, 1.0f },
					Shader.TileMode.MIRROR);
		if (lg != null)
			mPaint.setShader(lg);
		if (t == 8) {
			lg1 = new RadialGradient(x + w / 2, y + w / 2, w / 2, c1, c2,
					Shader.TileMode.MIRROR);
			mPaint.setShader(lg1);
		}
		if (t == 9) {
			lg2 = new SweepGradient(x + w / 2, y + h / 2, new int[] { c1, c2,
					c1 }, new float[] { 0, 0.5f, 1.0f });
			mPaint.setShader(lg2);
		}
	}

	public void circleLine(int x, int y, int r, int dr, int a, int c, int thick) {
		int x1 = x + (int) (r * Math.cos(a * 3.14 / 180));
		int y1 = y + (int) (r * Math.sin(a * 3.14 / 180));
		int x2 = x + (int) ((r - dr) * Math.cos(a * 3.14 / 180));
		int y2 = y + (int) ((r - dr) * Math.sin(a * 3.14 / 180));
		line(x2, y2, x1, y1, c, thick);
	}

	public void fillPin(int x, int y, int r, int w, int a, int c, int c1, int t) {
		int x1 = x + (int) (r * Math.cos(a * 3.14 / 180));
		int y1 = y + (int) (r * Math.sin(a * 3.14 / 180));
		if (t == 0) {
			int x2 = x + (int) (w * Math.cos((a + 90) * 3.14 / 180));
			int y2 = y + (int) (w * Math.sin((a + 90) * 3.14 / 180));
			int x3 = x + (int) (w * Math.cos((a - 90) * 3.14 / 180));
			int y3 = y + (int) (w * Math.sin((a - 90) * 3.14 / 180));
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		if (t == 1) {
			int x2 = x - (int) (3 * w * Math.cos(a * 3.14 / 180));
			int y2 = y - (int) (3 * w * Math.sin(a * 3.14 / 180));
			line(x1, y1, x2, y2, c, w / 2);
		}
		if (t == 2) {
			int x0 = x - (int) (3 * w * Math.cos(a * 3.14 / 180));
			int y0 = y - (int) (3 * w * Math.sin(a * 3.14 / 180));
			int x2 = x0 + (int) (w * 0.7 * Math.cos((a + 90) * 3.14 / 180));
			int y2 = y0 + (int) (w * 0.7 * Math.sin((a + 90) * 3.14 / 180));
			int x3 = x0 + (int) (w * 0.7 * Math.cos((a - 90) * 3.14 / 180));
			int y3 = y0 + (int) (w * 0.7 * Math.sin((a - 90) * 3.14 / 180));
			fillTrangle(x1, y1, x2, y2, x3, y3, c);
		}
		int c11 = adjustBrightness(c1, 30);
		int c21 = adjustBrightness(c1, -30);
		fillGrdcircle(x, y, w + 1, c11, c21, 8);
	}

	public void axis(int x0, int y0, int nx, int sx, int ny, int sy, int c) {
		int x, y, i;
		x = nx * sx;
		y = ny * sy;
		for (i = 1; i < nx; i++)
			dotLine(x0 + i * sx, y0 - y, x0 + i * sx, y0, c, 1);
		for (i = 1; i < ny; i++)
			dotLine(x0, y0 - i * sy, x0 + x, y0 - i * sy, c, 1);
		drawRect(x0, y0 - y, x, y, c, 1);
	}

	public void arrowLine(int type, int n, int x1, int y1, int x2, int y2,
			int c1) {
		int i;
		if (type <= 1) {
			int y3 = y2;
			if (y2 > y1) {
				y2 = y1;
				y1 = y3;
			}
			if (type == 0) {
				line(x1, y1, x2, y2 - 20, c1, 2);
				line(x1 - 1, y2 - 5, x1 - 1, y2 - 15, c1, 2);
				line(x1 + 1, y2 - 5, x1 + 1, y2 - 15, c1, 2);
			}
			if (type == 1) {
				line(x1, y1 + 20, x2, y2, c1, 2);
				line(x1 - 1, y2 + 15, x1 - 1, y2 + 5, c1, 2);
				line(x1 + 1, y2 + 15, x1 + 1, y2 + 5, c1, 2);
			}
			for (i = 0; i <= n; i++)
				line(x1 - 3, y2 + i * (y1 - y2) / n, x1 + 3, y2 + i * (y1 - y2)
						/ n, c1, 2);
		}
		if (type > 1) {
			int x3 = x1;
			if (x1 > x2) {
				x1 = x2;
				x2 = x3;
			}
			if (type == 2) {
				line(x1, y1, x2 + 20, y2, c1, 2);
				line(x2 + 5, y1 - 1, x2 + 15, y2 - 1, c1, 2);
				line(x2 + 5, y1 + 1, x2 + 15, y2 + 1, c1, 2);
			}
			if (type == 3) {
				line(x1 - 20, y1, x2, y2, c1, 2);
				line(x1 - 5, y1 - 1, x1 - 15, y2 - 1, c1, 2);
				line(x1 - 5, y1 + 1, x1 - 15, y2 + 1, c1, 2);
			}
			for (i = 0; i <= n; i++)
				line(x1 + i * (x2 - x1) / n, y2 - 3, x1 + i * (x2 - x1) / n,
						y2 + 3, c1, 2);
		}
	}

}
