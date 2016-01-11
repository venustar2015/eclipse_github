package com.hzcn.opengles_draw;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CylinderRender implements Renderer {
	public static class MyTouchListener implements OnTouchListener {
		private CylinderRender tgl;
		private GLSurfaceView surface;
		private float previousX;
		private float previousY;

		public MyTouchListener(CylinderRender tgl, GLSurfaceView surface) {
			this.tgl = tgl;
			this.surface = surface;
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
				float dX = (arg1.getX() - previousX) * 0.4f;
				float dY = (arg1.getY() - previousY) * 0.4f;

				tgl.sety(dX);
				tgl.setx(dY);

			}
			previousX = arg1.getX();
			previousY = arg1.getY();
			return true;
		}

	}

	private DrawCylinder cLinder = new DrawCylinder(10, 2, 7, 18);

	@Override
	public void onDrawFrame(GL10 gl) {
		// 启用背景色和深度缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		// 设置坐标偏移量
		gl.glTranslatef(0, 0, -10f);

		// gl.glPushMatrix();
		// 绘制圆柱体
		cLinder.drawSelf(gl);
		// gl.glPopMatrix();

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float ratio = (float) width / height;
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
	}

	@Override
	public void onSurfaceCreated(GL10 gl,
			javax.microedition.khronos.egl.EGLConfig config) {
		// TODO Auto-generated method stub
		gl.glClearColor(1, 1, 1, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	private void setx(float x) {
		cLinder.setxAngle(x);
	}

	private void sety(float y) {
		cLinder.setyAngle(y);
	}

	private void setz(float z) {
		cLinder.setzAngle(z);
	}

}