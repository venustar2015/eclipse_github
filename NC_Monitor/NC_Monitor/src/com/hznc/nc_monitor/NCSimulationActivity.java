package com.hznc.nc_monitor;

import com.hzcn.opengles_draw.CylinderRender;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;

public class NCSimulationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		GLSurfaceView glView=new GLSurfaceView(this);
		CylinderRender render=new CylinderRender();
		glView.setRenderer(render);
		setContentView(glView);
	}
	
}
