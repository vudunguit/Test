package com.aga.mine.mains;

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

import android.content.Context;
import android.util.Log;

import com.aga.mine.mains.MainActivity.InviteCallback;
import com.aga.mine.pages.UserData;
import com.aga.mine.util.Popup;

public class ShopBroomstick2 extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "23broomstick/";
	final String fileExtension = ".png";
	
	CCSprite bg;
	CCLabel gold;
	
	long [][] stickArray = { 
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
	
	public InviteCallback mInviteCallback = new InviteCallback() {

		@Override
		public void onInvited(List<String> invitedFriends, String requestId) {
			//To Do:
			String senderID = FacebookData.getinstance().getUserInfo().getId();
			
			for (String recipientID : invitedFriends) {
				Log.e("ShopBroomstick2", "recipientID : " + recipientID);
				String data = 
						"0,RequestModeMailBoxAdd*22," + requestId + "*1," + recipientID + "*19," + senderID + "*20,Broomstick*21," + quantity;				
				DataFilter.sendMail(data);
			}
			
		}
    };

	public ShopBroomstick2() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		MainApplication.getInstance().getActivity().setInviteCallback(mInviteCallback);
		
		// BackGround 파일
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "bb1" + fileExtension);
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
		
		gold = FrameTitle6.setTitle(boardFrame, folder);
	}
	
	private void setMainMenu(CCSprite parentSprite){
		CCMenu gameMenu = CCMenu.menu(buttons(stickArray));
		gameMenu.alignItemsVertically(-3);
		gameMenu.setPosition(CGPoint.ccp(parentSprite.getContentSize().width / 2 - 5, parentSprite.getContentSize().height / 2 - 12));
		parentSprite.addChild(gameMenu);
	}
	
	private CCMenuItem[] buttons(long[][] valuesArray) {
		List<CCMenuItem> listMenus = new ArrayList<CCMenuItem>();
		
		for (long[] ds : valuesArray) {
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
			CCLabel  gold = CCLabel.makeLabel(new NumberComma().numberComma(ds[0]), "Arial", 28);
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
		String values = new NumberComma().numberComma(value1);
		if (value2 > 0)
			values = values + "+" + new NumberComma().numberComma(value2);
		return values;
	}

	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	final int previous = 501;
	final int home= 502;
	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			switch (value) {
			case previous:
				scene = Shop.scene();
				CCDirector.sharedDirector().replaceScene(scene);
				break;

			case home:
				scene = Home.scene();
				CCDirector.sharedDirector().replaceScene(scene);
				break;
				
			case Constant.PURCHASING_OK:
				isPurchase = true;
				makeAPurchase();
				this.removeChildByTag(Constant.POPUP_LAYER, true);
				break;
				
			case Constant.PURCHASING_CANCEL:
				isPurchase = false;
				makeAPurchase();
				this.removeChildByTag(Constant.POPUP_LAYER, true);
				break;
			}

		}
	}

	private boolean  isPurchase = false;
	private long quantity;
	private long price;
	
	private void makeAPurchase() {
//				String recipientID = FacebookData.getinstance().getRecipientID(); // 상점 이동 방식에 따른 ID 변경
//				MainApplication.getInstance().getActivity().sendInvite(recipientID, "우편물 발송", null);
////				FacebookData.getinstance().getRequestID(recipientID);  //test용
		
		// 테스트용 (facebook invite에서 requestID 받으면 그것으로 대체 위에 코드)
		if (isPurchase) {
			long requestID = (long) (Math.random() * 72036854775807L);  //facebook 알림글번호로 대체할 것
			String recipientID = FacebookData.getinstance().getRecipientID(); // 상점 이동 방식에 따른 ID 변경
			String senderID = FacebookData.getinstance().getUserInfo().getId();
			String data = 
					"0,RequestModeMailBoxAdd*22," + requestID + 
					"*1," + recipientID + "*19," + senderID + "*20,Broomstick*21," + quantity;
			long myGold = Long.parseLong(FacebookData.getinstance().getDBData("Gold"));
			if (myGold < price) {
				return;
			}
			String sum = String.valueOf(myGold - price);
			DataFilter.sendMail(data);
			FacebookData.getinstance().modDBData("Gold", sum);
			gold.setString(new NumberComma().numberComma(sum));
		}

	}
	
	public void buttonCallback(Object sender) {
		Popup.popupOfPurchase(this);
		
		long[] value = (long[]) ((CCNode)sender).getUserData();
		price = value[0];
		quantity = value[1];
		// 로그용
		for (double d : value) {
			Log.e("ShopBroomstick2", "buttonCallback : " + (int)d);
		}
	}
	
}
//end