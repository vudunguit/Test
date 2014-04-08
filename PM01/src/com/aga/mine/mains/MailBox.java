package com.aga.mine.mains;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.util.Log;

public class MailBox {

	final String commonfolder = "00common/";
	final String fileExtension = ".png";

	final int mailcloseButton = 1008;
	final int mailReceiveAllButton = 1009;
	
	public static boolean buttonActive = true;
	
	CCSprite broomstickBackground1;
	CCSprite broomstickBackground2;
	CCSprite presentBackground1;
	CCSprite presentBackground2;
	
	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	public String[] mailOpen() {
		try {
			 return DataFilter.mailFilter(new DataController().execute(
					"0,RequestModeMailBoxRead*1," + FacebookData.getinstance().getUserInfo().getId()).get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
}
	
	MailBox(CCLayer parentLayer, String imageFolder, CCNode nodeThis) {
//		public void postbox() {
		
		String[] mail = mailOpen();
		ArrayList<String[]> goldList = new ArrayList<String[]>();
		ArrayList<String[]> BroomstickList = new ArrayList<String[]>();
		
		Log.e("MailBox", "mailSize [" + mail.length + "]");
		for (String string : mail) {
			Log.e("MailBox", "mailOpen [" + string + "]");
			if (string != null && !string.equals("")) {
				String[] mailArray = string.split("\\*");
//				for (String string2 : mailArray) {
//					Log.e("MailBox", "mailOpen2 : " + string2);
//				}		
				
				if (mailArray[2].equals("Gold")) {
					goldList.add(mailArray);
				} else {
					BroomstickList.add(mailArray);
				}
			}

		}

		for (String[] strings : BroomstickList) {
			for (String string : strings) {
				Log.e("MailBox", "Broomstick : " + string);
			}
		}

		for (String[] strings : goldList) {
			for (String string : strings) {
				Log.e("MailBox", "gold : " + string);
			}
		}
		
			buttonActive = false;

			// 불투명 배경
			CCSprite child = CCSprite.sprite(commonfolder + "opacitybg" + fileExtension);
			child.setPosition(winsize().width/2, winsize().height/2);
			
			// 기본 배경
			CCSprite board = CCSprite.sprite(imageFolder + "postboxbg" + fileExtension);
			board.setPosition(child.getContentSize().width/2, child.getContentSize().height/2);
			
			// 빗자루 배경 (활성)
			broomstickBackground1 = CCSprite.sprite(imageFolder + "broomstickBackgroundActive" + fileExtension);
			broomstickBackground1.setPosition(board.getContentSize().width/2, board.getContentSize().height/2+18.0f);
			
			// 빗자루 배경 (비활성)
			broomstickBackground2 = CCSprite.sprite(imageFolder + "broomstickBackgroundInactive" + fileExtension);
			broomstickBackground2.setPosition(board.getContentSize().width/2, board.getContentSize().height/2+18.0f);
			
			// 선물 배경 (활성)
			presentBackground1 = CCSprite.sprite(imageFolder + "giftBackgroundActive" + fileExtension);
			presentBackground1.setPosition(board.getContentSize().width/2, board.getContentSize().height/2+18.0f);
			
			// 선물 배경 (비활성)
			presentBackground2 = CCSprite.sprite(imageFolder + "giftBackgroundInactive" + fileExtension);
			presentBackground2.setPosition(board.getContentSize().width/2, board.getContentSize().height/2+18.0f);
			
			//
			// 빗자루 메뉴
			CCMenuItem broomstickMenu = CCMenuItemImage.item(
					imageFolder + "postboxBlankButton" + fileExtension,
					imageFolder + "postboxBlankButton" + fileExtension,
					nodeThis, "pesterCallback");

			
			// 빗자루 메뉴 이름
			CCSprite broomstickMenuTitle = CCSprite.sprite(
					Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "postboxBroomstickTitle" + fileExtension));
			broomstickMenuTitle.setPosition(broomstickMenu.getContentSize().width/2, broomstickMenu.getContentSize().height/2);
			broomstickMenu.addChild(broomstickMenuTitle);
			
			//
			// 선물 메뉴
			CCMenuItem giftMenu = CCMenuItemImage.item(
					imageFolder + "postboxBlankButton" + fileExtension,
					imageFolder + "postboxBlankButton" + fileExtension,
					nodeThis, "presentCallback");

			// 선물 메뉴 이름
			CCSprite giftMenuTitle = CCSprite.sprite(
					Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "postboxGiftTitle" + fileExtension));
			giftMenuTitle.setPosition(giftMenu.getContentSize().width/2, giftMenu.getContentSize().height/2);
			giftMenu.addChild(giftMenuTitle);
			
			
			CCMenuItem close = CCMenuItemImage.item(
					imageFolder + "postboxCloseNormal" + fileExtension,
					imageFolder + "postboxClosePress" + fileExtension,
					nodeThis, "clicked2");
			close.setTag(mailcloseButton);
			
			CCMenuItem[] menu = {broomstickMenu, giftMenu, close};
			CCMenu postMenu = CCMenu.menu(menu);
			//CCMenu postMenu2 = CCMenu.menu(menu);


			postMenu.setContentSize(
					broomstickBackground1.getContentSize().width,
					broomstickMenu.getContentSize().height);
			postMenu.setPosition(
					0.0f,broomstickBackground1.getContentSize().height - postMenu.getContentSize().height);

			broomstickMenu.setPosition(
					broomstickMenu.getContentSize().width/2, broomstickMenu.getContentSize().height/2);
			giftMenu.setPosition(
					broomstickMenu.getPosition().x+broomstickMenu.getContentSize().width,
					broomstickMenu.getPosition().y);
			close.setPosition(
					postMenu.getContentSize().width - close.getContentSize().width * 0.8f,
					postMenu.getContentSize().height - close.getContentSize().height * 0.5f);
			
			// 우편 수량
			CCSprite postCountBack = CCSprite.sprite(imageFolder + "broomstickCount" + fileExtension);
			postCountBack.setPosition(
					postCountBack.getContentSize().width / 2 + 10.0f,
					//broomstickBackground.getContentSize().height + postCountBack.getContentSize().height / 2 - 120.0f);
					broomstickBackground1.getContentSize().height - 100.0f);
			broomstickBackground1.addChild(postCountBack);

			CCLabel postCountNumber = CCLabel.makeLabel(BroomstickList.size() +" ", "Arial", 30.0f);
			postCountNumber.setColor(ccColor3B.ccc3(64, 46, 1));
			postCountNumber.setPosition(
					postCountBack.getContentSize().width - postCountNumber.getContentSize().width / 2 - 10.0f,
					postCountBack.getContentSize().height / 2);
			postCountBack.addChild(postCountNumber);
		
//			CCLabel postCountText = CCLabel.makeLabel("최대 50개까지 수신 가능", "Arial", 15.0f);
			CCLabel postCountText = CCLabel.makeLabel("최대 7일간 보관", "Arial", 15.0f);
			postCountText.setColor(ccColor3B.ccc3(64, 46, 1));
			postCountText.setAnchorPoint(0.0f, 0.5f);
			postCountText.setPosition(
					postCountBack.getContentSize().width / 2 + 70f,
					//broomstickBackground.getContentSize().height + postCountText.getContentSize().height / 2 - 110.0f);
					broomstickBackground1.getContentSize().height - 100.0f);
			broomstickBackground1.addChild(postCountText);

			
			// Receive All
			String BroomstickAll = ""; 
			for (String[] str : BroomstickList) {
				BroomstickAll += "," + str[0];
			}
			
			CCMenuItem receiveAllButton = CCMenuItemImage.item(
					imageFolder + "receiveAllButtonNormal" + fileExtension,
					imageFolder + "receiveAllButtonPress" + fileExtension,
					nodeThis, "clicked2");
			receiveAllButton.setTag(mailReceiveAllButton);
			receiveAllButton.setUserData(BroomstickAll);
			
			CCSprite receiveAllText = CCSprite.sprite(
					Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "receiveAllButton" + fileExtension));
			receiveAllText.setPosition(
					receiveAllButton.getContentSize().width / 2,
					receiveAllButton.getContentSize().height / 2);
			
			receiveAllButton.addChild(receiveAllText);

			CCMenu receiveAllMenu = CCMenu.menu(receiveAllButton);
			
			receiveAllMenu.setContentSize(
					receiveAllButton.getContentSize().width,receiveAllButton.getContentSize().height);
			receiveAllMenu.setPosition(
					broomstickBackground1.getContentSize().width - receiveAllMenu.getContentSize().width - 8.0f,
					broomstickBackground1.getContentSize().height - receiveAllMenu.getContentSize().height / 2 - 100.0f);
			receiveAllButton.setPosition(
					receiveAllMenu.getContentSize().width * 0.45f,
					receiveAllMenu.getContentSize().height / 2);
					
			broomstickBackground1.addChild(receiveAllMenu);

			if (BroomstickList.size() > 0) {
			
				// 우편함 빗자루 리스트
				CCSprite postList = CCSprite.sprite(imageFolder + "postboxList" + fileExtension);
				postList.setPosition(
						10.0f + postList.getContentSize().width / 2 +3.0f,
						broomstickBackground1.getContentSize().height - 130.0f - postList.getContentSize().height / 2 - 3.0f);
				
				CCSprite  postPictrue= CCSprite.sprite(imageFolder + "pumkin" + fileExtension);
				postPictrue.setPosition(
						15 + postPictrue.getContentSize().width / 2,
						(postList.getContentSize().height - 25.0f) / 2);
				
				String nameStr = "";
				String senderName = BroomstickList.get(0)[1];
				if (senderName.equals("0")) {
					nameStr = "이벤트 보상";
				} else if (senderName.equals("1")) {
					nameStr = "PumpkinMines";
				} else if (senderName.equals(FacebookData.getinstance().getUserInfo().getId())) {
					nameStr = "  구 매";
				} else {
					nameStr = senderName;	
				}
				
				// 수정 필요
				CCLabel  name = CCLabel.makeLabel(nameStr, "Arial", 16);
				name.setPosition(
						name.getContentSize().width/2 + 15.0f, 
						postList.getContentSize().height - 25.0f /2f);
				
				CCLabel  time = CCLabel.makeLabel(BroomstickList.get(0)[4], "Arial", 16);
				time.setColor(ccColor3B.ccBLACK);
				time.setAnchorPoint(1.0f, 0.5f);
				time.setPosition(
						postList.getContentSize().width - postPictrue.getContentSize().width / 2 - 82, 
						postList.getContentSize().height - 25 /2f);
				
				CCLabel  itemQuantity = CCLabel.makeLabel("빗자루 " +BroomstickList.get(0)[3] + "개를 선물 받았습니다.", "Arial", 18);
	//			CCLabel  itemQuantity = CCLabel.makeLabel(Broomstick.get(0)[3] + "개 빗자루(골드)를 선물 받았습니다.", "Arial", 15.0f);
				itemQuantity.setAnchorPoint(0, 0.5f);
				itemQuantity.setPosition(
						postPictrue.getPosition().x + (postPictrue.getContentSize().width * (1.15f - postPictrue.getAnchorPoint().x)) +5, 
						postPictrue.getPosition().y + (postPictrue.getContentSize().height * (0.5f - postPictrue.getAnchorPoint().y))); 
		
	//			CCMenuItem getButton = CCMenuItemImage.item(
	//					imageFolder + "broomstickButton1" + fileExtension,
	//					imageFolder + "broomstickButton2" + fileExtension,
	//					nodeThis, "clicked");
	////			getButton.setUserData("");
	//			getButton.setUserData(Broomstick.get(0)[0]);
	//			CCMenu getItem = CCMenu.menu(getButton);
	//			
	//			getItem.setPosition(
	//					postList.getContentSize().width - getButton.getContentSize().width / 2 - 5.0f,
	//					postList.getContentSize().height / 2);
	//			
	//			getButton.setPosition(0,0);
				

				postList.addChild(postPictrue);
				postList.addChild(name);
				postList.addChild(time);
				postList.addChild(itemQuantity);
//				postList.addChild(getItem);
				
				presentBackground1.addChild(postList);
				broomstickBackground1.addChild(postList);
			}
			
			
			//broomstickBackground1에 추가한 메뉴버튼과 충돌로 visible 설정시 충돌 문제로 수정 요함.
			//presentBackground1.addChild(postMenu2); // 문제 지점
			broomstickBackground1.addChild(postMenu);

			
			broomstickBackground1.setVisible(true);
			broomstickBackground2.setVisible(false);
			presentBackground1.setVisible(false);
			presentBackground2.setVisible(true);
			
			//board.addChild(title);
			board.addChild(broomstickBackground1, 3, 2);
			board.addChild(broomstickBackground2, 1, 4);
			board.addChild(presentBackground1, 4, 1);
			board.addChild(presentBackground2, 2, 3);
			child.addChild(board);
			parentLayer.addChild(child,999,999);
			
		}

	public static boolean isButtonActive() {
		return buttonActive;
	}

	public static void setButtonActive(boolean buttonActive) {
		MailBox.buttonActive = buttonActive;
	}

	public CCSprite getBroomstickBackground1() {
		return broomstickBackground1;
	}

	public void setBroomstickBackground1(CCSprite broomstickBackground1) {
		this.broomstickBackground1 = broomstickBackground1;
	}

	public CCSprite getBroomstickBackground2() {
		return broomstickBackground2;
	}

	public void setBroomstickBackground2(CCSprite broomstickBackground2) {
		this.broomstickBackground2 = broomstickBackground2;
	}

	public CCSprite getPresentBackground1() {
		return presentBackground1;
	}

	public void setPresentBackground1(CCSprite presentBackground1) {
		this.presentBackground1 = presentBackground1;
	}

	public CCSprite getPresentBackground2() {
		return presentBackground2;
	}

	public void setPresentBackground2(CCSprite presentBackground2) {
		this.presentBackground2 = presentBackground2;
	}
	
}