package com.aga.mine.mains;

import java.util.concurrent.ExecutionException;

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
		parentSprite.addChild(ccVersion);
		ccVersion.setPosition(parentSprite.getContentSize().width - 80, 70);

		if (!version.equals(getVersionData())) {
			Uri uri = Uri.parse("https://play.google.com/store/apps");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			CCDirector.sharedDirector().getActivity().startActivity(intent);
		}
		schedule("nextSceneCallback", 2.2f);
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
	
	public void nextSceneCallback(float dt) {
		CCScene scene = Login.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
}