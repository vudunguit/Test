package com.aga.mine.mains;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import com.aga.mine.pages.UserData;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class ShopBroomstick2 extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "23broomstick/";
	final String fileExtension = ".png";

	CCSprite bg;
	
	double[][] stickArray = { 
			{ 1000, 5, 0 }, { 1800, 10, 0 }, { 3400, 20, 0 },
			{ 6400, 40, 0 }, { 12000, 80, 0 }, { 22400, 160, 0 } };
	
	private Context mContext;
	UserData userData;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopBroomstick2();
		scene.addChild(layer);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}

	public ShopBroomstick2() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		// BackGround 파일
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
		CCMenu gameMenu = CCMenu.menu(buttons(stickArray));
		gameMenu.alignItemsVertically(-1);
		gameMenu.setPosition(CGPoint.ccp(parentSprite.getContentSize().width / 2 - 6, parentSprite.getContentSize().height / 2 - 13));
		parentSprite.addChild(gameMenu);
	}
	
	private CCMenuItem[] buttons(double[][] valuesArray) {
		List<CCMenuItem> listMenus = new ArrayList<CCMenuItem>();
		
		for (double[] ds : valuesArray) {
			CCMenuItemImage button = CCMenuItemImage.item(
					folder + "buttonnormal" + fileExtension,
					folder + "buttonpressed" + fileExtension,
					this, "buttonCallback");
			button.setUserData(ds);
			listMenus.add(button);
			
			// 빗자루 이미지
			CCSprite goldimage = CCSprite.sprite(folder + (int)ds[1] + fileExtension);
			goldimage.setPosition(goldimage.getContentSize().width / 2, button.getContentSize().height / 2);
			button.addChild(goldimage);
			
			// 가격
			CCLabel  gold = CCLabel.makeLabel(numberComma((int)ds[0]), "Arial", 28);
//			gold.setColor(ccColor3B.ccYELLOW);
//			gold.setAnchorPoint(1, 1);
//			gold.setPosition(button.getContentSize().width - 15, button.getContentSize().height - 5);
			gold.setAnchorPoint(1, 0);
			gold.setPosition(button.getContentSize().width - 15, 14);
			button.addChild(gold);
			
			//gold Text
			CCLabel  goldText = CCLabel.makeLabel("Gold", "Arial", 20);
			goldText.setColor(ccColor3B.ccYELLOW);
//			goldText.setAnchorPoint(gold.getAnchorPoint());
//			goldText.setPosition(gold.getPosition().x - gold.getContentSize().width - 10, gold.getPosition().y - 8);

//			goldText.setAnchorPoint(1, 1);
//			goldText.setPosition(button.getContentSize().width - 60, button.getContentSize().height - 10);
			
			goldText.setAnchorPoint(0, 1);
			goldText.setPosition(
					gold.getPosition().x - (gold.getContentSize().width * gold.getAnchorPoint().x), 
					button.getContentSize().height - 9);
			button.addChild(goldText);
			
			// 빗자루
			CCLabel  stick = CCLabel.makeLabel("빗자루 " + plus((int)ds[1], (int)ds[2]) + "개", "Arial", 26);
			stick.setAnchorPoint(1, 0);
			stick.setPosition(button.getContentSize().width - 132, 15);
			button.addChild(stick);
		}
		
		CCMenuItem[] menusArray = listMenus.toArray(new CCMenuItem[listMenus.size()]);
		return menusArray;
	}
	
	private String plus	(int value1, int value2) {
		String values = numberComma(value1);
		if (value2 > 0)
			values = values + "+" + numberComma(value2);
		return values;
	}
	
	// int 값을 String으로 변환하여 comma 삽입
	private String Decimal(int value) {
		DecimalFormat format = new DecimalFormat("###,###,###,###");
		String ret = format.format(value);
		return ret;
	}
	
	// int 값을 String으로 변환하여 comma 삽입
	private String numberComma(int value) {
		String s = Integer.toString(value);
		return numberComma(s);
	}
	
	// String에 comma 삽입
	private String numberComma(String string) {
		if (string.length() < 4) 
			return string;
		
		String value = string;
		int len = value.length();
		int position = 3;
		for (int i = 0; i < (len -1) / 3; i++) {
			value = value.substring(0, (value.length() - position)) + "," + value.substring((value.length() - position), value.length());
			position += 4;
		}
		return value;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	public void previousCallback(Object sender) {
		CCScene scene = Shop.scene();
		CCDirector.sharedDirector().replaceScene(scene);
		Log.e("CallBack", "ShopLayer");
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
		Log.e("CallBack", "HomeLayer");
	}
	
	public void buttonCallback(Object sender) {
		double[] value = (double[]) ((CCNode)sender).getUserData();
		
		// 로그용
		for (double d : value) {
			Log.e("ShopBroomstick2", "buttonCallback : " + (int)d);
		}
		
//		double requestID = Math.random() * 9223372036854775807L;  //facebook 알림글번호로 대체할 것
		long requestID = FacebookData.getinstance().getRequestID();  //test용 
		String recipientID = FacebookData.getinstance().getRecipientID(); // 상점 이동 방식에 따른 ID 변경
		String senderID = FacebookData.getinstance().getUserInfo().getId();
		String data = 
				"0,RequestModeMailBoxAdd*22," + requestID + 
				"*1," + recipientID + "*19," + senderID + "*20,Broomstick*21," + value[1];
		FacebookData.getinstance().sendMail(data);

		
	}
	
}
//end