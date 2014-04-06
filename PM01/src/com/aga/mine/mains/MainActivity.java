package com.aga.mine.mains;

import java.io.IOException;
import java.util.List;

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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.aga.mine.view.FriendListAapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public SimpleFacebook mSimpleFacebook;
    private ProgressDialog mProgress;

    private CCGLSurfaceView mGLSurfaceView;
    CCDirector director = CCDirector.sharedDirector();
    
    private RelativeLayout main;
    private ListView mListView;
    
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constant.MSG_DISPLAY_FRIENDLIST:
				//API �� : 
				FriendListAapter adapter = new FriendListAapter(MainActivity.this);
				mListView.setAdapter(adapter);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.setMargins(120, 500, 120, 400); //adjust position
				//MainActivity.this.addContentView(listview, params);
				main.addView(mListView, params);
				break;
				
			case Constant.MSG_HIDE_SCROLLVIEW:
				main.removeView(mListView);
				break;
			}
		}
    };

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
        //setContentView(mGLSurfaceView);
        setContentView(R.layout.activity_main);
        main = (RelativeLayout) findViewById(R.id.main);
        main.addView(mGLSurfaceView);
        mListView = new ListView(this);

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
            // ����� ������ �й� (�������� �ƴҽ� ���Ḹ ���� ��)
            NetworkController.getInstance().sendRequestGameOver(0);
            NetworkController.getInstance().closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "mine ����");
        new Process().killProcess(Process.myPid());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
    }

    // Back-key Ŭ���� ���α׷� ���� �ڵ�
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:

            new AlertDialog.Builder(this).setTitle(R.string.app_name)
                        .setMessage("�����ϰڽ��ϱ�?")
                        .setPositiveButton("��", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true); // ��Activity finish�� �ٸ� Activity�� �ߴ� �� ����.
                                //      finish();
                                new Process().killProcess(Process.myPid());
                                // android.os.Process.killProcess(android.os.Process.myPid()); 
                                // -> �ش� ������ ���μ����� ���� Kill��Ų��.
                            }
                        })
                        .setNegativeButton("�ƴϿ�", null)
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
    private void showDialog(String title, String message) {
        mProgress = ProgressDialog.show(this, title, message, true);
    }
    
    private void hideDialog() {
        if (mProgress != null) {
            mProgress.hide();
        }
    }
    
    public void loginFaceBook() {
        mSimpleFacebook.login(mOnLoginListener);
    }
    
    public void logoutFaceBook() {
    	mSimpleFacebook.logout(mLogoutListener);
    }
    
    public void getMyProfile() {
        mSimpleFacebook.getProfile(mOnProfileListener);
    }
    
    public void getFriendsInfo() {
        mSimpleFacebook.getFriends(mOnFriendsListener);
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
            getMyProfile();
            getFriendsInfo();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            //hideDialog();
        }
    };
    

    private OnLogoutListener mLogoutListener = new OnLogoutListener() {
		
		@Override
		public void onFail(String reason) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onException(Throwable throwable) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onThinking() {
			showDialog("Please wait", "Logging out...");
			
		}
		
		@Override
		public void onLogout() {
			hideDialog();
			// clear facebook data and value and go to login scene
			FacebookData.initialize();
			CCScene scene = Login.scene();
			director.replaceScene(scene);
		}
	};
	
    
    // listener for profile request
    final OnProfileListener mOnProfileListener = new OnProfileListener() {

        @Override
        public void onFail(String reason) {
            // insure that you are logged in before getting the profile
            Log.d(TAG, reason);
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e(TAG, "Bad thing happened", throwable);
        }

        @Override
        public void onThinking() {
            // show progress bar or something to the user while fetching
            // profile
            Log.i(TAG, "Login...");
        }

        @Override
        public void onComplete(Profile profile) {
            //save my profile
            FacebookData.getinstance().setUserInfo(profile);
            Log.e("FacebookHelper", "setUserInfo");
            nextCallback(FacebookData.getinstance().facebookReady(1));
        }
    };
    
    // get friends listener
    private OnFriendsListener mOnFriendsListener = new OnFriendsListener() {

        @Override
        public void onFail(String reason) {
            hideDialog();
            Log.w(TAG, reason);
        }

        @Override
        public void onException(Throwable throwable) {
            hideDialog();
            Log.e(TAG, "Bad thing happened", throwable);
        }

        @Override
        public void onThinking() {
            showDialog("Please Wait", "Loading friends infomation...");
            Log.i(TAG, "Thinking...");
        }

        @Override
        public void onComplete(List<Profile> friends) {
            hideDialog();
            FacebookData.getinstance().setFriendsInfo(friends);
            Log.e("FacebookHelper", "setFriendsInfo");
            nextCallback(FacebookData.getinstance().facebookReady(100));
            
            //ToDo: go to Daily Scene
//          CCScene scene = Daily.scene();
//          director.replaceScene(scene);
        }
    };
    
    OnInviteListener mOnInviteListener = new OnInviteListener() {

        @Override
        public void onFail(String reason) {
            // insure that you are logged in before inviting
            Log.w(TAG, reason);
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e(TAG, "Bad thing happened", throwable);
        }

        @Override
        public void onComplete(List<String> invitedFriends, String requestId) {
            //String msg = "Invitation was sent to " + invitedFriends.size() + " users, invite request=" + requestId;
            //ToDo:
        }

        @Override
        public void onCancel() {
            Log.w(TAG, "cancled dialog");
        }

    };
    
    private void nextCallback(boolean facebookReady) {
        Log.e("FacebookHelper", "nextCallback");
        if (facebookReady) {
        	//ToDo: ���ɰ���: ȭ���� ������ �ʰ� ������ �ε��� �⼮�η� �̵�
        	//1) �ε��� ǥ��
        	//2) �ڵ鷯 send message
        	//3) ������ �ε� �� �ε��� ����
        	//4) ȭ�� �̵�
        	
        	//�ȵ���̵� ��� ��ü
/*            HomeScroll.getInstance().setData(
                    DataFilter.getRanking(FacebookData.getinstance().getUserInfo(),FacebookData.getinstance().getFriendsInfo())
            );*/
            
            // daily(�⼮��)�� 1�� 1ȸ�� ȣ���ϹǷ� DailyBeckoner���� üũ �� �̵��ϰ� ��.(�̹� 1ȸ�̻� ���ӽ� home scene���� �̵�) 
            // DailyBeckoner ȣ��� facebook �������� ������ �־�ߵ˴ϴ�.
            new DailyBeckoner();
        }
    }

}
