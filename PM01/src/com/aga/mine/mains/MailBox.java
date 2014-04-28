package com.aga.mine.mains;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.os.Message;
import android.util.Log;

import com.aga.mine.view.BroomstickItem;
import com.aga.mine.view.GoldItem;
import com.aga.mine.view.MailItem;
import com.aga.mine.view.MailListAdapter;

public class MailBox {

	final String commonfolder = "00common/";

	final int mailcloseButton = 1008;
	final int mailReceiveAllButton = 1009;
	final int broomTab = 1012;
	final int goldTab = 1013;
	
	int broomstickBg2Tag = 301;
	int presentBg2Tag = 302;
	int broomstickBg1Tag = 303;
	int presentBg1Tag = 304;
	
	public static boolean buttonActive = true;

	CCSprite broomstickBackground1;
	CCSprite broomstickBackground2;
	CCSprite presentBackground1;
	CCSprite presentBackground2;
	
	public static CCLabel postNumber = null;
	
	private ArrayList<MailItem> mBroomList = new ArrayList<MailItem>();
	private ArrayList<MailItem> mGoldList = new ArrayList<MailItem>();

	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	public MailBox(CCLayer parentLayer, String imageFolder, CCNode nodeThis) {
		this(parentLayer, imageFolder, nodeThis, Constant.MAIL_TAB_BROOM);
	}

	public MailBox(CCLayer parentLayer, String imageFolder, CCNode nodeThis, int selectedTab) {
		String[] mail = DataFilter.readMail();
		for (String string : mail) {
			Log.e("MailBox", "mailOpen [" + string + "]");
			if (string != null && !string.equals("")) {
				String[] mailArray = string.split("\\*");
				
				MailItem mailItem;
				if (mailArray[2].equals("Broomstick")) {
					mailItem = new BroomstickItem();
					mailItem.serial_number = mailArray[0];
					mailItem.sender_id = mailArray[1];
					mailItem.quantity = mailArray[3];
					mailItem.date = mailArray[4];
					mailItem.sender_name = FacebookData.getinstance().getUserInfo().getName();
					mBroomList.add(mailItem);
				} else {
					mailItem = new GoldItem();
					mailItem.serial_number = mailArray[0];
					mailItem.sender_id = mailArray[1];
					mailItem.quantity = mailArray[3];
					mailItem.date = mailArray[4];
					mailItem.sender_name = FacebookData.getinstance().getUserInfo().getName();
					mGoldList.add(mailItem);
				}
			}
		}

		buttonActive = false;

		// 불투명 배경
		CCSprite opacityBg = CCSprite.sprite(commonfolder + "opacitybg.png");
		opacityBg.setPosition(winsize().width / 2, winsize().height / 2);

		// 기본 배경
		CCSprite postboxBg = CCSprite.sprite(imageFolder + "postboxbg.png");
		postboxBg.setPosition(opacityBg.getContentSize().width / 2,
				opacityBg.getContentSize().height / 2);

		// 빗자루 배경 (활성)
		broomstickBackground1 = CCSprite.sprite(imageFolder + "broomstickBackgroundActive.png");
		broomstickBackground1.setPosition(postboxBg.getContentSize().width / 2,
				postboxBg.getContentSize().height / 2 + 18);

		// 빗자루 배경 (비활성)
		broomstickBackground2 = CCSprite.sprite(imageFolder + "broomstickBackgroundInactive.png");
		broomstickBackground2.setPosition(postboxBg.getContentSize().width / 2,
				postboxBg.getContentSize().height / 2 + 18);

		// 선물 배경 (활성)
		presentBackground1 = CCSprite.sprite(imageFolder + "giftBackgroundActive.png");
		presentBackground1.setPosition(postboxBg.getContentSize().width / 2,
				postboxBg.getContentSize().height / 2 + 18);

		// 선물 배경 (비활성)
		presentBackground2 = CCSprite.sprite(imageFolder + "giftBackgroundInactive.png");
		presentBackground2.setPosition(postboxBg.getContentSize().width / 2,
				postboxBg.getContentSize().height / 2 + 18);

		//
		// 빗자루 메뉴
		CCMenuItem broomstickMenu = CCMenuItemImage.item(imageFolder
				+ "postboxBlankButton.png", imageFolder
				+ "postboxBlankButton.png", nodeThis,
				"clicked2");
		broomstickMenu.setTag(broomTab);

		// 빗자루 메뉴 이름
		CCSprite broomstickMenuTitle = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "postboxBroomstickTitle.png"));
		broomstickMenuTitle.setPosition(broomstickMenu.getContentSize().width / 2, broomstickMenu.getContentSize().height / 2);
		broomstickMenu.addChild(broomstickMenuTitle);

		//
		// 선물 메뉴
		CCMenuItem giftMenu = CCMenuItemImage.item(
				imageFolder + "postboxBlankButton.png", 
				imageFolder + "postboxBlankButton.png", 
				nodeThis, "clicked2");
		giftMenu.setTag(goldTab);

		// 선물 메뉴 이름
		CCSprite giftMenuTitle = CCSprite.sprite(Utility.getInstance()
				.getNameWithIsoCodeSuffix(
						imageFolder + "postboxGiftTitle.png"));
		giftMenuTitle.setPosition(giftMenu.getContentSize().width / 2,
				giftMenu.getContentSize().height / 2);
		giftMenu.addChild(giftMenuTitle);

		//close menu
		CCMenuItem close = CCMenuItemImage.item(imageFolder
				+ "postboxCloseNormal.png", imageFolder
				+ "postboxClosePress.png", nodeThis, "clicked2");
		close.setTag(mailcloseButton);

		CCMenuItem[] menu = { broomstickMenu, giftMenu, close };
		CCMenu postMenu = CCMenu.menu(menu);

		postMenu.setContentSize(broomstickBackground1.getContentSize().width,
				broomstickMenu.getContentSize().height);
		postMenu.setPosition(
				postboxBg.getContentSize().width / 2 - postMenu.getContentSize().width / 2,
				broomstickBackground1.getContentSize().height);

		broomstickMenu.setPosition(broomstickMenu.getContentSize().width / 2,
				broomstickMenu.getContentSize().height / 2);
		giftMenu.setPosition(
				broomstickMenu.getPosition().x
						+ broomstickMenu.getContentSize().width,
				broomstickMenu.getPosition().y);
		close.setPosition(
				postMenu.getContentSize().width - close.getContentSize().width
						* 0.8f,
				postMenu.getContentSize().height
						- close.getContentSize().height * 0.5f);

		// 우편물 수량 백그라운드
		String postCountBackStr = imageFolder;
		if (selectedTab == Constant.MAIL_TAB_BROOM) {
			postCountBackStr += "broomstickCount.png";
		} else {
			postCountBackStr += "giftCount.png";
		}
		CCSprite postCountBack = CCSprite.sprite(postCountBackStr);
		postboxBg.addChild(postCountBack, 665,  665);
		postCountBack.setPosition(
				postboxBg.getContentSize().width / 2 - broomstickBackground1.getContentSize().width / 2 + postCountBack.getContentSize().width / 2  + 10, 
				postboxBg.getContentSize().height / 2 + broomstickBackground1.getContentSize().height / 2 - 100  + 18);
		
		// 우편물 수량 값
		postNumber = CCLabel.makeLabel(
				(selectedTab==Constant.MAIL_TAB_BROOM? mBroomList.size() : mGoldList.size()) + " ", "Arial", 30.0f);
		postNumber.setColor(ccColor3B.ccc3(64, 46, 1));
		postNumber.setPosition(postCountBack.getContentSize().width
				- postNumber.getContentSize().width / 2 - 10.0f,
				postCountBack.getContentSize().height / 2);
		postCountBack.addChild(postNumber);

		// 우편물 보관 기간
		CCLabel postCountText = CCLabel.makeLabel("최대 7일간 보관", "Arial", 15.0f);
		postCountText.setColor(ccColor3B.ccc3(64, 46, 1));
		postCountText.setAnchorPoint(0.0f, 0.5f);
		postCountText.setPosition(
				postCountBack.getPosition().x + (1 - postCountBack.getAnchorPoint().x) * postCountBack.getContentSize().width + 20
				- (postCountText.getAnchorPoint().x * postCountText.getContentSize().width), 
				postCountBack.getPosition().y);
		postboxBg.addChild(postCountText, 555, 555);

		// Receive All (모두받기 버튼을 누르면 현재 아이템 타입의 모든 고유번호로 아이템을 삭제함)
		String BroomstickAll = "";

		for (MailItem item : (selectedTab == 1 ?  mBroomList : mGoldList)) {
			BroomstickAll += "," + item.serial_number;
		}

		// 모두 받기 버튼
		CCMenuItem receiveAllButton = CCMenuItemImage.item(
				imageFolder + "receiveAllButtonNormal.png",
				imageFolder + "receiveAllButtonPress.png",
				nodeThis, "clicked2");
		receiveAllButton.setTag(mailReceiveAllButton);
		receiveAllButton.setUserData(BroomstickAll);

		// 모두 받기 버튼 Text
		CCSprite receiveAllText = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "receiveAllButton.png"));
		receiveAllButton.addChild(receiveAllText);
		receiveAllText.setPosition(
				receiveAllButton.getContentSize().width / 2,
				receiveAllButton.getContentSize().height / 2);

		CCMenu receiveAllMenu = CCMenu.menu(receiveAllButton);
		postboxBg.addChild(receiveAllMenu, 444, 444);
		receiveAllMenu.setContentSize(receiveAllButton.getContentSize().width, receiveAllButton.getContentSize().height);
		receiveAllMenu.setPosition(
				postboxBg.getContentSize().width / 2 + broomstickBackground1.getContentSize().width / 2
						- receiveAllMenu.getContentSize().width - 8,
						postCountBack.getPosition().y - receiveAllMenu.getContentSize().height / 2);
		receiveAllButton.setPosition(
				receiveAllMenu.getContentSize().width * 0.45f,
				receiveAllMenu.getContentSize().height / 2);



		//안드로이드 스크롤뷰
		Message msg = MainApplication.getInstance().getActivity().mHandler.obtainMessage();
		if(selectedTab == 1) {
			msg.obj = mBroomList;
			msg.arg1 = Constant.MAIL_TAB_BROOM;
		} else {
			msg.obj = mGoldList;
			msg.arg1 = Constant.MAIL_TAB_GOLD;
		}
		msg.what = Constant.MSG_DISPLAY_ITEMLIST;
		MainApplication.getInstance().getActivity().mHandler.sendMessage(msg);
		
/*		if (BroomstickList.size() > 0) {

			// 우편함 빗자루 리스트
			CCSprite postList = CCSprite.sprite(imageFolder + "postboxList"
					+ fileExtension);
			postList.setPosition(
					10.0f + postList.getContentSize().width / 2 + 3.0f,
					broomstickBackground1.getContentSize().height - 130.0f
							- postList.getContentSize().height / 2 - 3.0f);

			CCSprite postPictrue = CCSprite.sprite(imageFolder + "pumkin"
					+ fileExtension);
			postPictrue.setPosition(
					15 + postPictrue.getContentSize().width / 2,
					(postList.getContentSize().height - 25.0f) / 2);

			String nameStr = "";
			String senderName = BroomstickList.get(0)[1];
			if (senderName.equals("0")) {
				nameStr = "이벤트 보상";
			} else if (senderName.equals("1")) {
				nameStr = "PumpkinMines";
			} else if (senderName.equals(FacebookData.getinstance()
					.getUserInfo().getId())) {
				nameStr = "  구 매";
			} else {
				nameStr = senderName;
			}

			// 수정 필요
			CCLabel name = CCLabel.makeLabel(nameStr, "Arial", 16);
			name.setPosition(name.getContentSize().width / 2 + 15.0f,
					postList.getContentSize().height - 25.0f / 2f);

			CCLabel time = CCLabel.makeLabel(BroomstickList.get(0)[4], "Arial",
					16);
			time.setColor(ccColor3B.ccBLACK);
			time.setAnchorPoint(1.0f, 0.5f);
			time.setPosition(
					postList.getContentSize().width
							- postPictrue.getContentSize().width / 2 - 82,
					postList.getContentSize().height - 25 / 2f);

			CCLabel itemQuantity = CCLabel.makeLabel(
					"빗자루 " + BroomstickList.get(0)[3] + "개를 선물 받았습니다.",
					"Arial", 18);
			// CCLabel itemQuantity = CCLabel.makeLabel(Broomstick.get(0)[3] +
			// "개 빗자루(골드)를 선물 받았습니다.", "Arial", 15.0f);
			itemQuantity.setAnchorPoint(0, 0.5f);
			itemQuantity
					.setPosition(
							postPictrue.getPosition().x
									+ (postPictrue.getContentSize().width * (1.15f - postPictrue
											.getAnchorPoint().x)) + 5,
							postPictrue.getPosition().y
									+ (postPictrue.getContentSize().height * (0.5f - postPictrue
											.getAnchorPoint().y)));

			// CCMenuItem getButton = CCMenuItemImage.item(
			// imageFolder + "broomstickButton1.png",
			// imageFolder + "broomstickButton2.png",
			// nodeThis, "clicked");
			// // getButton.setUserData("");
			// getButton.setUserData(Broomstick.get(0)[0]);
			// CCMenu getItem = CCMenu.menu(getButton);
			//
			// getItem.setPosition(
			// postList.getContentSize().width -
			// getButton.getContentSize().width / 2 - 5.0f,
			// postList.getContentSize().height / 2);
			//
			// getButton.setPosition(0,0);

			postList.addChild(postPictrue);
			postList.addChild(name);
			postList.addChild(time);
			postList.addChild(itemQuantity);
			// postList.addChild(getItem);

			presentBackground1.addChild(postList);
			broomstickBackground1.addChild(postList);
		}*/

		postboxBg.addChild(postMenu, 777, 777);
		
		if (selectedTab == Constant.MAIL_TAB_BROOM) {
			broomstickBackground2.setVisible(false);
			presentBackground1.setVisible(false); // 선물 비활성
		} else {
			broomstickBackground1.setVisible(false); // 빗자루 비활성
			presentBackground2.setVisible(false); 
		}
		// board.addChild(title);
		postboxBg.addChild(broomstickBackground1, broomstickBg1Tag, broomstickBg1Tag);
		postboxBg.addChild(broomstickBackground2, broomstickBg2Tag, broomstickBg2Tag);
		postboxBg.addChild(presentBackground1, presentBg1Tag, presentBg1Tag);
		postboxBg.addChild(presentBackground2, presentBg2Tag, presentBg2Tag);
		opacityBg.addChild(postboxBg, 888, 888);
		parentLayer.addChild(opacityBg, 999, 999);

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