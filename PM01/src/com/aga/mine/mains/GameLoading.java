package com.aga.mine.mains;

import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.util.Log;

import com.aga.mine.pages2.Game;
import com.aga.mine.util.Util;

public class GameLoading extends CCLayer {
	
	CCSprite bg;
	CCSprite bar;
	CCSprite wizard;
	float mScaleX = 1.0f;
	
	final String folder = "59gameload/";
	final String fileExtension = ".png";
	
	private GameLoading() {
		
		/************ 잘 사용했는지 모르겠습니다. 확인 부탁드립니다.  ************/
//		// hide scroll view
//		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		setBackground();
		setMainMenu();
		schedule("nextSceneCallback", 1.0f);
	}

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameLoading();
		scene.addChild(layer, 10, 10);
		return scene;
	}

	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	private CGPoint center() {
		return CGPoint.make(winsize().width/2, winsize().height/2);
	}
	
	// 배경 스케일 설정
	private void setBackground() {
		bg = CCSprite.sprite(folder + "gameloadbg" + fileExtension);
		bg.setPosition(center());
		this.addChild(bg);
		
//		// 타이틀명
//		FrameTitle4.setTitle(bg, folder);
	}
	
	private void setMainMenu() {
		CCSprite cloud = CCSprite.sprite(
				folder + "loadsmoke" + fileExtension);
		cloud.setPosition(50,120);
		cloud.setAnchorPoint(0, 0);
		bg.addChild(cloud);
		
		bar = CCSprite.sprite(
				folder + "loadpiece" + fileExtension);
		bar.setPosition(160,127);
		bar.setAnchorPoint(0, 0);
		bg.addChild(bar);
		//bar.setScaleX(75);
		
		wizard = CCSprite.sprite(folder + "loadwizard" + fileExtension);
		wizard.setPosition(bar.getPosition().x + bar.getContentSize().width * bar.getScaleX() - wizard.getContentSize().width/2, bar.getPosition().y);
		wizard.setAnchorPoint(0, 0);
		bg.addChild(wizard);
		
//		CCScaleTo scale = CCScaleTo.action(3, 85, 1);
//		bar.runAction(scale);
		schedule("update");
	}
	
	/**
	 * GameLoading은 실제 1-2초가 소요되며 Game.java를 로딩하면서 GameLoading 화면이 멈춤. 
	 * 이걸 개선하기 위해서는 Game.java에 소요되는 리소스를 Game 싱글턴 인스턴스에 로딩하고 끝나면 초기화하는
	 * 루틴으로 가야함.
	 */
	public void update(float dt) {
		mScaleX += dt*30;
		bar.setScaleX(mScaleX);
		
		float barRight = bar.getPosition().x + bar.getContentSize().width * bar.getScaleX();
		float limit = bg.getContentSize().width - bar.getPosition().x;
		Log.d("LDK", "limit:" + limit);
		
		if(barRight > limit) {
			unschedule("update");
			Log.d("LDK", "update");
		}
		wizard.setPosition(barRight - wizard.getContentSize().width/2, bar.getPosition().y);
	}
	
	public void nextSceneCallback(float dt) {
//		GameData.share().isGuestMode = true;
		CCScene scene = Game.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
