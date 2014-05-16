package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.content.Intent;
import android.net.Uri;

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
//		parentSprite.addChild(ccVersion);
		ccVersion.setPosition(parentSprite.getContentSize().width - 80, 70);
		
		String webVersion = DataFilter.getGameVersionData();
		if (webVersion == null || !version.equals(webVersion)) {
			Uri uri = Uri.parse("https://play.google.com/store/apps");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			CCDirector.sharedDirector().getActivity().startActivity(intent);
		} else {
		    MainApplication.getInstance().mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextSceneCallback();
                }
            }, 2200);
		}
		
	}

	
	public void nextSceneCallback() {
	    MainActivity mMainActivity = MainApplication.getInstance().getActivity();
	    
	    if(mMainActivity.mSimpleFacebook.isLogin()) {
	        //get myProfile and get Friends info
	        //go to daily scene
	        mMainActivity.getMyProfile();
	        mMainActivity.getFriendsInfo();
	    } else {
    	    CCScene scene = Login.scene();
    	    CCDirector.sharedDirector().replaceScene(scene);
	    }

	}
	
	
}
