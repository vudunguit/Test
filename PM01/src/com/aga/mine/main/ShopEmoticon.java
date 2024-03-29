﻿package com.aga.mine.main;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;

import android.util.Log;
import android.widget.Toast;

import com.aga.mine.main.R;
import com.aga.mine.util.Popup;
import com.aga.mine.util.Util;

public class ShopEmoticon extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "24emoticon/";
	final String fileExtension = ".png";
	
	CCSprite bg;
	
	int mode = 0;
	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	
	CCSprite boardFrame;
	CCSprite 	layer4;
	
	CCLabel goldLabel;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopEmoticon();
		scene.addChild(layer);
		return scene;
	}
		
	public ShopEmoticon() {
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "bb1" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);
		
		//애니메이션을 하기위해 자기자신의 인스턴스를 주입
		MainApplication.getInstance().setShopEmoticon(this);

		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_EMOTICONLIST);
	}
	
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(CCTextureCache.sharedTextureCache().addImageExternal(Util.RESOURCE + imageFullPath));
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
	}
	
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(CCTextureCache.sharedTextureCache().addImageExternal(Util.RESOURCE + imageFullPath));
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		
		goldLabel = FrameTitle6.setTitle(boardFrame, folder);
	}
	
	final int previous = 501;
	final int home= 502;
	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			switch (value) {
			case previous:
				MainApplication.getInstance().getActivity().click();
				scene = Shop.scene();
				CCDirector.sharedDirector().replaceScene(scene);
				break;

			case home:
				MainApplication.getInstance().getActivity().click();
				scene = Home.scene();
				CCDirector.sharedDirector().replaceScene(scene);
				break;
				
			case Constant.PURCHASING_OK:
				String emoticons = FacebookData.getinstance().getDBData("Emoticons");
				emoticons += "," + emoticonID;
				FacebookData.getinstance().modDBData("Emoticons", emoticons);
				
				String gold = FacebookData.getinstance().getDBData("Gold");
				gold = String.valueOf(Long.parseLong(gold) - 100);
				FacebookData.getinstance().modDBData("Gold", gold);
				SoundEngine.sharedEngine().playEffect(CCDirector.sharedDirector().getActivity(), R.raw.buy);
				MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_EMOTICONLIST);
				break;
				
			case Constant.PURCHASING_CANCEL:
				MainApplication.getInstance().getActivity().click();
				CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(CCDirector.sharedDirector().getActivity(), "구매 취소", Toast.LENGTH_SHORT).show();
					}
				});
				break;
			}
			
		}
	}

	int emoticonID;
	public void popup(int emoticonID){
		Popup.popupOfPurchase(this);
		this.emoticonID = emoticonID;
	}
	
	//골드 감소 애니메이션
	int _mygold;
	int _price;
	int pps;
	int gamso = 0;
	
	public void makeAPurchase(int gold, int price) {
		_mygold = gold;
		_price = price;
		pps = _price / 2;
		gamso = 0;
		// 골드 차감 애니
		schedule("goldAni");
	}
	

	
	public void goldAni(float dt) {
		gamso += dt * pps;
		goldLabel.setString(new NumberComma().numberComma(_mygold + gamso));
		// 골드 차감은 gamso가 작아져야함.
		if (gamso < _price) {
			unschedule("goldAni");
			goldLabel.setString(new NumberComma().numberComma(FacebookData.getinstance().getDBData("Gold")));
			gamso = 0;
		}
	}
}
