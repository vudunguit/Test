package com.aga.mine.mains;

import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.sromku.simple.fb.SimpleFacebook;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class Logo extends CCLayer {

	final String folder = "01trademark/";
	final String fileExtension = ".png";
	
	String Android = "0";
	String version = "0.8.0";
	String Iphone = "1"; 
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new Logo();
		scene.addChild(layer);
		return scene;
	}
	
	public Logo() {		
		versionCheck(BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), folder + "trademark" + fileExtension));
	}
	
	private void versionCheck(CCSprite parentSprite) {
		CCLabel ccVersion = CCLabel.makeLabel("version  " + version, "Arial", 20);
		parentSprite.addChild(ccVersion);
		ccVersion.setPosition(parentSprite.getContentSize().width - 80, 70);

		if (!version.equals(getVersionData())) {
			Uri uri = Uri.parse("https://play.google.com/store/apps");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			CCDirector.sharedDirector().getActivity().startActivity(intent);
		} else {
		    //schedule("nextSceneCallback", 2.2f);
		    MainApplication.getInstance().mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextSceneCallback();
                }
            }, 2200);
		}
		
	}
	
	private String getVersionData() {
		String webversion = "";
			try {
				webversion = new DataController().execute("0,RequestModeIsServerOk*24,0").get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		return webversion;
	}
	
	public void nextSceneCallback() {
	    MainActivity mMainActivity = MainApplication.getInstance().getActivity();
	    
	    if(mMainActivity.mSimpleFacebook.isLogin()) {
	        //get myProfile and get Friends info
	        mMainActivity.getMyProfile();
	        mMainActivity.getFriendsInfo();

	        //go to daily scene
//    	    CCScene scene = Daily.scene();
//    	    CCDirector.sharedDirector().replaceScene(scene);
	    	
	    	// daily(출석부)는 1일 1회만 호출하므로 DailyBeckoner에서 체크 후 이동하게 됨.(이미 1회이상 접속시 home scene으로 이동) 
	    	// DailyBeckoner 호출시 facebook 정보들을 가지고 있어야됩니다.
//	    	new DailyBeckoner();
	    } else {
    	    CCScene scene = Login.scene();
    	    CCDirector.sharedDirector().replaceScene(scene);
	    }

	}
	
	
}
