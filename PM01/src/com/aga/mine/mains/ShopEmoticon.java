package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.pages.UserData;

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

	static CCLayer layer2;
	private Context mContext;
	private UserData userData;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopEmoticon();
		scene.addChild(layer);
		layer2 = CCLayer.node();
		scene.addChild(layer2);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}

//	private CGSize winsize() {
//		return CCDirector.sharedDirector().winSize();
//	}
	
	public ShopEmoticon() {
//		mContext = CCDirector.sharedDirector().getActivity();
//		userData = UserData.share(mContext);
//		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
//		this.setIsTouchEnabled(true);
//		setBackground();
//		setBackBoardMenu();
//		setForeground();
		
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
//		setBackBoardMenu(commonfolder + "gamebb" + fileExtension);
		setBackBoardMenu(commonfolder + "bb1" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);
//		this.setIsTouchEnabled(true);
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_EMOTICONLIST);
	}
	
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
//		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height / 2);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
	}
	
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle6.setTitle(boardFrame, folder);
	}
	
	private void setMainMenu(CCSprite parentSprite){
		emoti(parentSprite);
	}
	
	private void emoti(CCSprite parentSprite) {
		String emoticonBundle = FacebookData.getinstance().getDBData("Emoticons");
		String[] emoticonArray = emoticonBundle.split(",");
		CGPoint point = CGPoint.ccp(
				parentSprite.getPosition().x - parentSprite.getContentSize().width * (0.5f - parentSprite.getAnchorPoint().x), 
				parentSprite.getPosition().y + parentSprite.getContentSize().height * (1 - parentSprite.getAnchorPoint().y) - 200);
		for (String string : emoticonArray) {
			CCLabel emoticon = CCLabel.makeLabel(string, "Arial", 20);
			parentSprite.addChild(emoticon);
			parentSprite.setPosition(point);
			point.set(parentSprite.getPosition().x, parentSprite.getPosition().y - emoticon.getContentSize().height);
		}
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
				scene = Shop.scene();
				break;

			case home:
				scene = Home.scene();
				break;
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

}
