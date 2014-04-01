package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.aga.mine.pages2.Game;

public class GameLoading extends CCLayer {
	
	CCSprite bg;
	
	final String folder = "59gameload/";
	final String fileExtension = ".png";
	
	private GameLoading() {
		setBackground();
		setMainMenu();
		schedule("nextSceneCallback", 1.0f);
	}

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameLoading();
		scene.addChild(layer);
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
		
		// 타이틀명
		FrameTitle4.setTitle(bg, folder);
	}
	
	private void setMainMenu() {
		CCSprite cloud = CCSprite.sprite(
				folder + "loadsmoke" + fileExtension);
		cloud.setPosition(50,120);
		cloud.setAnchorPoint(0, 0);
		bg.addChild(cloud);
		
		CCSprite bar = CCSprite.sprite(
				folder + "loadpiece" + fileExtension);
		bar.setPosition(160,127);
		bar.setAnchorPoint(0, 0);
		bg.addChild(bar);
		bar.setScaleX(75);
		
		CCSprite wizard = CCSprite.sprite(
				folder + "loadwizard" + fileExtension);
		wizard.setPosition(420,108);
		wizard.setAnchorPoint(0, 0);
		bg.addChild(wizard);
	}
	
	public void nextSceneCallback(float dt) {
		GameData.share().isGuestMode = true;
		CCScene scene = Game.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
