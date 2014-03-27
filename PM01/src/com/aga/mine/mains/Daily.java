package com.aga.mine.mains;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Daily extends Activity {
	
	private CCGLSurfaceView mGLSurfaceView;
	CCDirector director = CCDirector.sharedDirector();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
        
		// 아래에서 스레드 문제 발생할수 있음. (datafilter 사용시)
		String facebookID = FacebookData.getinstance().getUserInfo().getId();
		if (!DataFilter.readFilter(getUserDBData(facebookID))) {
			Log.e("Daily", "setUserDBData");
			setUserDBData(facebookID);
		}
		DataFilter.dailyFilter(director, getDailyData(facebookID));
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

	private String getUserDBData(String facebookID) {
		try {
			return new DataController().execute(
					"0,RequestModeRead*1," + facebookID).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}

	private void setUserDBData(String facebookID) {
		new DataController()
				.execute("0,RequestModeUpdate*1,"
						+ facebookID
						+ "*2,1*3,2*4,3*5,4*6,5*7,6*8,7*9,8*10,9*11,10*12,11*13,12*14,13*15,14*16,15*17,16");
	}

	private String getDailyData(String facebookID) {
		try {
			return new DataController().execute(
					"0,RequestModeDailyCheck*1," + facebookID).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "error";
	}
	   
	private void setDisplayMetrics() {
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
	    int dh       = metrics.heightPixels;
	    int dw       = metrics.widthPixels;
	    int deviceWidth;
	    int deviceHeight;
	    
	    if(dw < dh){
	        deviceWidth  = dw;
	        deviceHeight = dh;
	    }else{
	        deviceWidth  = dh;
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
	
	 // Back-key 클릭시 프로그램 종료 코드
	 public boolean onKeyDown(int keyCode, KeyEvent event){
	  switch(keyCode){
	  case KeyEvent.KEYCODE_BACK:
	   
	   new AlertDialog.Builder(this)
	   .setTitle(R.string.app_name)
	   .setMessage("종료하겠습니까?")
	   .setPositiveButton("예", new DialogInterface.OnClickListener(){
	    
	    public void onClick(DialogInterface dialog, int which){
	     moveTaskToBack(true); // 본Activity finish후 다른 Activity가 뜨는 걸 방지.
//	     finish();
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

		static class MainLayer extends CCLayer {
			
			final String folder = "03daily/";
			final String fileExtension = ".png";
			
			CCSprite bg;
			int dailyCount = 0;
			
			static CCScene scene(int dailyCount) {
				CCScene scene = CCScene.node();
				CCLayer layer = new MainLayer(dailyCount);
				scene.addChild(layer);
				return scene;
			}
			
			public MainLayer(int dailyCount) {
				this.dailyCount = dailyCount;
				bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), folder+"daily-bg" + fileExtension);
				setMain();
				schedule("nextCallback", 20);
				this.setIsTouchEnabled(true);
			}
			
			private void setMain() {
				setTitle();
				closeImage();
				gold(bg, dailyCount);
				goldtext(bg, dailyCount);
			}
			
			//출석부 타이틀
			private void setTitle() {
				CCSprite title = CCSprite.sprite(
						Utility.getInstance().getNameWithIsoCodeSuffix(folder + "daily-title" + fileExtension));
				bg.addChild(title);
				title.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height - title.getContentSize().height * 1.85f);
				title.setAnchorPoint(0.5f, 0.5f);
			}
			
			private void closeImage() {
				CCSprite close = CCSprite.sprite(	folder + "daily-close3" + fileExtension);
				bg.addChild(close, 1, 777);
				close.setPosition(525, bg.getContentSize().height - 178);
				close.setAnchorPoint(0, 0);
			}
			
				// 출석부 gold 이미지
				private void gold(CCSprite parent, int dailyCount) {
					int[] goldX = { 100, 204, 308, 410, 517 };
					int[] goldY = { 1136 - 370, 1136 - 479, 1136 - 590, 1136 - 707, 1136 - 820, 1136 - 940 };
					int count = 1;
					
					for (int k = 0; k < 6; k++) {
						for (int i = 0; i < 5; i++) {
							CCSprite gold = null;
							if ((i + 1) % 5 != 0) {
								if (dailyCount < count) {
									gold = CCSprite.sprite(folder + "daily-coin1.png");
								} else {
									gold = CCSprite.sprite(folder + "daily-coin2.png");
								}
							} else {
								if (dailyCount < count) {
									gold = CCSprite.sprite(folder + "daily-pack1.png");
								} else {
									gold = CCSprite.sprite(folder + "daily-pack2.png");
								}
							}
							parent.addChild(gold, 1);
							gold.setPosition(goldX[i] + 10, goldY[k]);
							
							//도장 찍기
							if (dailyCount > count) {
								CCSprite stamp = CCSprite.sprite(
										Utility.getInstance().getNameWithIsoCodeSuffix(folder + "stamp" + fileExtension));
								parent.addChild(stamp, 100);
								stamp.setPosition(goldX[i], goldY[k]);
								stamp.setAnchorPoint(0.5f, 0.5f);
								stamp.setScale(0.23f);		
							} else if (dailyCount == count) {
								Log.e("Daily", "도장찍기 애니메이션 넣을 곳 입니다.");
								Log.e("Daily", "stamp animation");
								
								CCSprite stamp = CCSprite.sprite(folder + "ani_stamp" + fileExtension); // 임시 이미지
								parent.addChild(stamp, 100);
								stamp.setPosition(goldX[i], goldY[k]);
								stamp.setAnchorPoint(0.5f, 0.5f);
								stamp.setScale(0.23f);		
							}
							
							count++;
						}
					}
				}

				// 출석부 gold 및 보상 가격 text
				private void goldtext(CCSprite parent, int dailyCount) {
					int[] textX = { 100, 204, 308, 410, 517 };
					int[] textY = { 1136 - 338, 1136 - 445, 1136 - 555, 1136 - 670, 1136 - 787, 1136 - 894 };
					int[] reward = {
							100,200,300,400,1000,
							500,600,700,800,2000,
							900,1000,1100,1200,3000,
							1300,1400,1500,1600,4000,
							1700,1800,1900,2000,5000,
							2100,2200,2300,2400,10000};
					
					int count = 0;

					for (int k = 0; k < 6; k++) {
						for (int i = 0; i < 5; i++) {
							CCLabel goldText = CCLabel.makeLabel("Gold", "Arial", 16.0f);
							parent.addChild(goldText, 10);
							goldText.setColor(ccColor3B.ccBLACK);
							goldText.setAnchorPoint(0, 1);
							goldText.setPosition(textX[i] - 45, textY[k]);
							
							CCLabel goldValue = CCLabel.makeLabel("" + reward[count], "Arial", 22.0f);
							parent.addChild(goldValue, 20);
							goldValue.setColor(ccColor3B.ccBLACK);
							goldValue.setAnchorPoint(1, 1);
							goldValue.setPosition(textX[i] + 50, textY[k]);
							
							count++;
						}
					}
					
					Log.e("Daily", "우편 보내기 / Gold " + reward[dailyCount -1]);
					// 우편 보내기
				}
			
			@Override
			public boolean ccTouchesBegan(MotionEvent event) {
				bg.removeChildByTag(777, true);
				CCSprite close = CCSprite.sprite(folder + "daily-close4" + fileExtension);
				bg.addChild(close, 2);
				close.setPosition(525, bg.getContentSize().height - 178);
				close.setAnchorPoint(0, 0);
				return super.ccTouchesBegan(event);
			}

			@Override
			public boolean ccTouchesEnded(MotionEvent event) {
				nextCallback(10f);
				this.setIsTouchEnabled(false);
				return super.ccTouchesEnded(event);
			}

			public void nextCallback(float dt) {
				CCScene scene = Home.scene();
				CCDirector.sharedDirector().replaceScene(scene);
			}
		}
	}
