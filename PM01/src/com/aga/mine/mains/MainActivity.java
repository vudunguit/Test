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
import android.graphics.Color;
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

    private int nMargin = 0;
    private float frameCenterPosition = 0.525f;
    private float frameGeneralWidth = 640;
    // 홈 랭크 리스트뷰 위치 (화면중심 높이 52.5% 위치)
    private float frameGeneralHeight = 889;
    private float rankListFrameLeftPosition = 90;
    private float rankListFrameTopPosition = 364;
    private float rankListFrameRightPosition = 100;
    private float rankListFrameBottomPosition = 186;
    
    // 우편함 리스트뷰 위치 (화면중심 높이 50% 기준)
    private int verticalShiftPosition = 18;
    private float itemListBackgroundHeight = 596;
    private float itemListMarginLeft = 106; // 107
    private float itemListMarginTop = 131;
    private float itemListMarginRight = 106; // 107
    private float itemListMarginBottom = 12;
    
    // 친구초대 리스트뷰 위치 (화면중심 높이 52.5% 위치)
    private float inviteListBackgroundHeight = 580;
    private float inviteListMarginLeft = 108;
    private float inviteListMarginTop = 60;
    private float inviteListMarginRight = 108;
    private float inviteListMarginBottom = 216;
    
    // 이모티콘 리스트뷰 위치 (화면중심 높이 52.5% 위치)
    private float emoticonListBackgroundHeight = 889;
    private float emoticonListMarginLeft = 95;
    private float emoticonListMarginTop = 204;
    private float emoticonListMarginRight = 106;
    private float emoticonListMarginBottom = 186;
    //
    // 초대매치 리스트뷰 위치 (화면중심 높이 52.5% 위치)
    private float matchListBackgroundHeight = 889;
    private float matchListMarginLeft = 88;
    private float matchListMarginTop = 440;
    private float matchListMarginRight = 100;
    private float matchListMarginBottom = 186;
    
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			// 랭킹에 들어가는 페이스북 이미지 크기 및 리스트뷰 오른쪽 2번째 이미지(선물상자)의 rightMargin이 해상도 별로 변경됩니다.
			// 해상도에 맞게 이미지는 커지고 마진은 작아져야 됩니다.(scale값 적용) 
			float scale = main.getWidth() / frameGeneralWidth ;
			float height = main.getHeight();
			
			switch(msg.what) {
			case Constant.MSG_DISPLAY_RANKLIST:
				//API 콜 : 
				FriendListAapter adapter = new FriendListAapter(MainActivity.this);
				mListView.setAdapter(adapter);
				mListView.setCacheColorHint(Color.alpha(0));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
				// assets의 frameGeneral-hd.png, frameMatching-hd.png파일의 width, height을 받아와서 작업을 해야되는데
				// inputstream을 해야되고 핸들러에서 작업 하기에는 문제가 있어 이미지 크기를 상수로 정의하여 작업했습니다.
				params.setMargins(
						(int) (rankListFrameLeftPosition * scale) + nMargin, 
						(int) (height * (1 - frameCenterPosition) + (rankListFrameTopPosition - frameGeneralHeight * 0.5f) * scale) + nMargin, 
						(int) (rankListFrameRightPosition * scale) + nMargin, 
						(int) (height * frameCenterPosition + (rankListFrameBottomPosition - frameGeneralHeight * 0.5f) * scale) + nMargin);
				//MainActivity.this.addContentView(listview, params);
				
				main.addView(mListView, params);
				break;
				
			case Constant.MSG_DISPLAY_ITEMLIST:
//				FriendListAapter itemAdapter = new FriendListAapter(MainActivity.this);
//				mListView.setAdapter(itemAdapter);
				RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				itemParams.setMargins(
						(int) (itemListMarginLeft * scale) + nMargin, 
						(int) (height / 2 + (itemListMarginTop - itemListBackgroundHeight * 0.5f - verticalShiftPosition) * scale) + nMargin, 
						(int) (itemListMarginRight * scale) + nMargin, 
						(int) (height / 2 + (itemListMarginBottom - itemListBackgroundHeight * 0.5f + verticalShiftPosition) * scale) + nMargin);
				main.addView(mListView, itemParams);
				break;
				
			case Constant.MSG_DISPLAY_INVITELIST:
//				FriendListAapter inviteAdapter = new FriendListAapter(MainActivity.this);
//				mListView.setAdapter(inviteAdapter);
				RelativeLayout.LayoutParams inviteParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				inviteParams.setMargins(
						(int) (inviteListMarginLeft * scale) + nMargin, 
						(int) (height * (1 - frameCenterPosition) + (inviteListMarginTop - inviteListBackgroundHeight * 0.5f) * scale) + nMargin, 
						(int) (inviteListMarginRight * scale) + nMargin, 
						(int) (height * frameCenterPosition + (inviteListMarginBottom - inviteListBackgroundHeight * 0.5f) * scale) + nMargin);
				main.addView(mListView, inviteParams);
				break;
				
			case Constant.MSG_DISPLAY_EMOTICONLIST:
//				FriendListAapter emoticonAdapter = new FriendListAapter(MainActivity.this);
//				mListView.setAdapter(emoticonAdapter);
				RelativeLayout.LayoutParams emoticonParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				emoticonParams.setMargins(
						(int) (emoticonListMarginLeft * scale) + nMargin, 
						(int) (height * (1 - frameCenterPosition) + (emoticonListMarginTop - emoticonListBackgroundHeight * 0.5f) * scale) + nMargin, 
						(int) (emoticonListMarginRight * scale) + nMargin, 
						(int) (height * frameCenterPosition + (emoticonListMarginBottom - emoticonListBackgroundHeight * 0.5f) * scale) + nMargin);
				main.addView(mListView, emoticonParams);
				break;
				
			case Constant.MSG_DISPLAY_MATCHLIST:
//				FriendListAapter matchAdapter = new FriendListAapter(MainActivity.this);
//				mListView.setAdapter(matchAdapter);
				RelativeLayout.LayoutParams matchParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				matchParams.setMargins(
						(int) (matchListMarginLeft * scale) + nMargin, 
						(int) (height * (1 - frameCenterPosition) + (matchListMarginTop - matchListBackgroundHeight * 0.5f) * scale) + nMargin, 
						(int) (matchListMarginRight * scale) + nMargin, 
						(int) (height * frameCenterPosition + (matchListMarginBottom - matchListBackgroundHeight * 0.5f) * scale) + nMargin);
				main.addView(mListView, matchParams);
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
			GameData.share().isGuestMode = true;
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
        	//ToDo: 성능개선: 화면을 멈추지 않고 데이터 로딩후 출석부로 이동
        	//1) 로딩바 표시
        	//2) 핸들러 send message
        	//3) 데이터 로딩 후 로딩바 해제
        	//4) 화면 이동
        	
        	//안드로이드 뷰로 대체
/*            HomeScroll.getInstance().setData(
                    DataFilter.getRanking(FacebookData.getinstance().getUserInfo(),FacebookData.getinstance().getFriendsInfo())
            );*/
        	
        	// 게임 모드 설정
        	Log.e("Main", "isGuestMode_B : " + GameData.share().isGuestMode);
			GameData.share().isGuestMode = false;
        	Log.e("Main", "isGuestMode_A : " + GameData.share().isGuestMode);
        	
        	// 게임스코어 받아오기
    		FacebookData.getinstance().setGameScore(DataFilter.getRanking());
        	
            // daily(출석부)는 1일 1회만 호출하므로 DailyBeckoner에서 체크 후 이동하게 됨.(이미 1회이상 접속시 home scene으로 이동) 
            // DailyBeckoner 호출시 facebook 정보들을 가지고 있어야됩니다.
            new DailyBeckoner();
        }
    }

}
