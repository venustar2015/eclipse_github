package com.hzcn.opengles_draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class DrawCylinder {
	private FloatBuffer vertexBuffer;
	private int vCount;
	private float xAngle;
	private float yAngle;
	private float zAngle;

	public DrawCylinder(float length, float radius, int blocks,
			float sectorBlocks) {
		xAngle = yAngle = zAngle = 1.0f;
		float len = length / blocks;
		float sectorAngle = 360 / sectorBlocks;

		List<Float> arrayList = new ArrayList<Float>();

		for (int i = 0; i < blocks; i++) {
			for (float angle = 360; angle > 0; angle = angle - sectorAngle) {
				// 左上角顶点坐标
				float x1 = -(length / 2 - i * len);
				float y1 = (float) (radius * Math.sin(Math.toRadians(angle)));
				float z1 = (float) (radius * Math.cos(Math.toRadians(angle)));

				// 右上角顶点坐标
				float x2 = x1 + len;
				float y2 = y1;
				float z2 = z1;

				// 右下角顶点坐标
				float x3 = x2;
				float y3 = (float) (radius * Math.sin(Math.toRadians(angle
						- sectorAngle)));
				float z3 = (float) (radius * Math.cos(Math.toRadians(angle
						- sectorAngle)));

				// 左下角顶点坐标
				float x4 = x1;
				float y4 = y3;
				float z4 = z3;

				// 第一个三角形
				arrayList.add(x2);
				arrayList.add(y2);
				arrayList.add(z2);

				arrayList.add(x1);
				arrayList.add(y1);
				arrayList.add(z1);

				arrayList.add(x3);
				arrayList.add(y3);
				arrayList.add(z3);

				// 第二个三角形
				arrayList.add(x1);
				arrayList.add(y1);
				arrayList.add(z1);

				arrayList.add(x3);
				arrayList.add(y3);
				arrayList.add(z3);

				arrayList.add(x4);
				arrayList.add(y4);
				arrayList.add(z4);

			}
		}
		int size = arrayList.size();
		vCount = size / 3;
		float[] vertex = new float[size];
		for (int i = 0; i < size; i++) {
			vertex[i] = arrayList.get(i);
		}

		ByteBuffer vB = ByteBuffer.allocateDirect(size * 4);
		vB.order(ByteOrder.nativeOrder());
		vertexBuffer = vB.asFloatBuffer();
		vertexBuffer.put(vertex);
		vertexBuffer.flip();
		vertexBuffer.position(0);

	}

	public void drawSelf(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);// 设置绘制线的颜色
		gl.glRotatef(xAngle, 1, 0, 0);
		gl.glRotatef(yAngle, 0, 1, 0);
		gl.glRotatef(zAngle, 0, 0, 1);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vCount);

	}

	/**
	 * @param xAngle
	 *            the xAngle to set
	 */
	public void setxAngle(float xAngle) {
		this.xAngle += xAngle;
	}

	/**
	 * @param yAngle
	 *            the yAngle to set
	 */
	public void setyAngle(float yAngle) {
		this.yAngle += yAngle;
	}

	/**
	 * @param zAngle
	 *            the zAngle to set
	 */
	public void setzAngle(float zAngle) {
		this.zAngle += zAngle;
	}
}