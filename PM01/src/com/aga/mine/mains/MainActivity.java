package com.aga.mine.mains;

import java.io.IOException;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	

	private CCGLSurfaceView mGLSurfaceView;
	CCDirector director = CCDirector.sharedDirector();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("MainActivity", "onCreate");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);
		mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
		setContentView(mGLSurfaceView);

		director.attachInView(mGLSurfaceView);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);

		setDisplayMetrics();
		CCDirector.sharedDirector().setDisplayFPS(true);
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);
		
		CCScene scene = Logo.scene();
		director.runWithScene(scene);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			// 종료시 무조건 패배 (게임중이 아닐시 연결만 끊을 것)
			NetworkController.getInstance().sendRequestGameOver(0);
			NetworkController.getInstance().closeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("MainActivity", "mine 종료");
		new Process().killProcess(Process.myPid());
	}
	
	private void setDisplayMetrics() {
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int dh = metrics.heightPixels;
		int dw = metrics.widthPixels;
		int deviceWidth;
		int deviceHeight;

		if (dw < dh) {
			deviceWidth = dw;
			deviceHeight = dh;
		} else {
			deviceWidth = dh;
			deviceHeight = dw;
		}

		float WidthReset = 640f;
		float HeightReset = 1920 * 0.9f;

		if (deviceWidth != WidthReset) {
			deviceHeight = (int) (deviceHeight / (deviceWidth / WidthReset));
			deviceWidth = (int) WidthReset;
		} else if (deviceHeight > HeightReset) {
			deviceWidth = (int) (deviceWidth / (deviceHeight / HeightReset));
			deviceHeight = (int) HeightReset;
		}

		CCDirector.sharedDirector().setScreenSize(deviceWidth, deviceHeight);
	}
	
}
