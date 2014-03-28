package com.aga.mine.mains;

import java.io.IOException;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public SimpleFacebook mSimpleFacebook;
    private ProgressDialog mProgress;

    private CCGLSurfaceView mGLSurfaceView;
    CCDirector director = CCDirector.sharedDirector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "onCreate");

        MainApplication.getInstance().setActivity(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
    protected void onStart() {
        super.onStart();
        CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.e("Daily", "onStart");
                NetworkController.getInstance();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        CCDirector.sharedDirector().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        CCDirector.sharedDirector().onResume();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
    }

    // Back-key 클릭시 프로그램 종료 코드
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:

            new AlertDialog.Builder(this).setTitle(R.string.app_name)
                        .setMessage("종료하겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true); // 본Activity finish후 다른 Activity가 뜨는 걸 방지.
                                //      finish();
                                new Process().killProcess(Process.myPid());
                                // android.os.Process.killProcess(android.os.Process.myPid()); 
                                // -> 해당 어플의 프로세스를 강제 Kill시킨다.
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
        }

        return true;
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
            deviceHeight = (int)(deviceHeight / (deviceWidth / WidthReset));
            deviceWidth = (int)WidthReset;
        } else if (deviceHeight > HeightReset) {
            deviceWidth = (int)(deviceWidth / (deviceHeight / HeightReset));
            deviceHeight = (int)HeightReset;
        }

        CCDirector.sharedDirector().setScreenSize(deviceWidth, deviceHeight);
    }
    
    //facebook setting--------------------------------------------------------------------------
    private void showDialog() {
        mProgress = ProgressDialog.show(this, "Login", "Waiting for Facebook", true);
    }
    
    private void hideDialog() {
        if (mProgress != null) {
            mProgress.hide();
        }
    }
    
    public void loginFaceBook() {
        mSimpleFacebook.login(mOnLoginListener);
    }
    
    // Login listener
    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason) {
            Log.w(TAG, "Failed to login");
            //hideDialog();
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e(TAG, "Bad thing happened", throwable);
            //hideDialog();
        }

        @Override
        public void onThinking() {
            // show progress bar or something to the user while login is happing
            //showDialog();
        }

        @Override
        public void onLogin() {
            // change the state of the button or do whatever you want
            //hideDialog();
            
            //go to Daily scene
//            CCScene scene = Daily.scene();
//            director.replaceScene(scene);
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            //hideDialog();
        }
    };

}
