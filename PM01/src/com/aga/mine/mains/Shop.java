package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.view.MotionEvent;

public class Shop extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "20shop/";
	final String fileExtension = ".png";

	CCSprite bg;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new Shop();
		scene.addChild(layer);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}

	public Shop() {
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "gamebb" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);
		
		this.setIsTouchEnabled(true);
	}
	
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
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
		CCMenuItemImage button1 = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-gold1" + fileExtension),
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-gold2" + fileExtension),
				this, "button1Callback");
		
		CCMenuItemImage button2 = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-item1" + fileExtension),
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-item2" + fileExtension),
				this, "button2Callback");
		
		CCMenuItemImage button3 = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-broomstick1" + fileExtension),
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-broomstick2" + fileExtension),
				this, "button3Callback");
		
		CCMenuItemImage button4 = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-emoticon1" + fileExtension),
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "shop-emoticon2" + fileExtension),
				this, "button4Callback");
		
		CCMenu gameMenu = CCMenu.menu(button1, button2, button3, button4);
		gameMenu.setPosition(parentSprite.getContentSize().width / 2 - 5, parentSprite.getContentSize().height - 58f);
		parentSprite.addChild(gameMenu);
		
		button1.setPosition(0, -button1.getContentSize().height * 0.5f); // 골드
		button2.setPosition(0, -button1.getContentSize().height * 1.5f); // 아이템
		button3.setPosition(0, -button1.getContentSize().height * 2.5f); // 빗자루
		button4.setPosition(0, -button1.getContentSize().height * 3.5f); // 이모티콘
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	public void previousCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void button1Callback(Object sender) {
		FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
		CCScene scene = ShopGold2.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void button2Callback(Object sender) {
		CCScene scene = ShopItem2.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void button3Callback(Object sender) {
		FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
		CCScene scene = ShopBroomstick2.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void button4Callback(Object sender) {
		CCScene scene = ShopEmoticon.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

}