package com.aga.mine.mains;

import java.util.Calendar;

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

public class HomeTop extends CCLayer{

	final String commonfolder = "00common/";
	final String fileExtension = ".png";
	
	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	public HomeTop(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		setTopMenu(parentSprite, imageFolder, nodeThis);
		setTitle(parentSprite, imageFolder);
		setBottomMenu(parentSprite, imageFolder, nodeThis);
	}

	// 상단 메뉴
	private void setTopMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		
		// 지팡이 상점 바로가기 배경(버튼)
		CCMenuItem broomstickBg = CCMenuItemImage.item(imageFolder
				+ "home-broomstickBg-hd" + fileExtension, imageFolder
				+ "home-broomstickBg-hd" + fileExtension, 
				nodeThis, "clicked");
		broomstickBg.setUserData("broomstick");
		
		// 골드 상점 바로가기 배경(버튼)
		CCMenuItem goldBg = CCMenuItemImage.item(
				imageFolder + "home-goldBg-hd" + fileExtension,
				imageFolder + "home-goldBg-hd" + fileExtension, 
				nodeThis, "clicked");
		goldBg.setUserData("gold");
		
		CCMenu topMenu = CCMenu.menu(broomstickBg, goldBg);
		parentSprite.addChild(topMenu);
		
		topMenu.setColor(ccColor3B.ccc3(255, 255, 255));
		topMenu.setContentSize(CGSize.make(
				parentSprite.getContentSize().width - 80.0f,
				broomstickBg.getContentSize().height));
		topMenu.setPosition(
				35.0f, parentSprite.getContentSize().height - broomstickBg.getContentSize().height / 2 - 65.0f);

		broomstickBg.setPosition(broomstickBg.getContentSize().width / 2, 0f);
		goldBg.setPosition(topMenu.getContentSize().width - goldBg.getContentSize().width/ 2, 0f);

		 // 지팡이 그림
		CCSprite broomstickImg = CCSprite.sprite(imageFolder + "home-broomstickOn-hd" + fileExtension);
		broomstickBg.addChild(broomstickImg);
		broomstickImg.setPosition(
				broomstickImg.getContentSize().width / 2 + 10.0f,
				broomstickBg.getContentSize().height / 2);

		// 지팡이 수량
		CCLabel broomstickEA = CCLabel.makeLabel("+" + FacebookData.getinstance().getDBData("ReceivedBroomstick"), "Arial", 24.0f); // 지팡이 수량
		broomstickBg.addChild(broomstickEA);
		broomstickEA.setAnchorPoint(0, 0.5f);
		broomstickEA.setPosition(
				broomstickImg.getPosition().x + ((1 - broomstickImg.getAnchorPoint().x) * broomstickImg.getContentSize().width) - 2, 
				broomstickBg.getContentSize().height - broomstickEA.getContentSize().height / 2 - 5.0f);

		 // 지팡이 수량 증가 (카운트 다운)
		CCLabel broomstickTime = CCLabel.makeLabel("Loading...", "Arial", 20.0f);
		broomstickBg.addChild(broomstickTime, 0, 2);
		broomstickTime.setPosition(
				broomstickBg.getContentSize().width - 10.0f,
				broomstickTime.getContentSize().height / 2 + 5.0f);
		broomstickTime.setAnchorPoint(1.0f, 0.5f);

		// 골드
		CCLabel gold = CCLabel.makeLabel(
				new NumberComma().numberComma(FacebookData.getinstance().getDBData("Gold")), "Arial", 22.0f);
		goldBg.addChild(gold);
		gold.setPosition(goldBg.getContentSize().width - 10.0f, gold.getContentSize().height / 2 + 5.0f);
		gold.setAnchorPoint(1.0f, 0.5f);
	}

	private void setTitle(CCSprite parentSprite, String imageFolder) {
		
		// 타이틀 판넬
		CCSprite titlePanel = CCSprite.sprite(commonfolder + "frame-titlePanel" + fileExtension);
		parentSprite.addChild(titlePanel);
		titlePanel.setAnchorPoint(0.5f, 0.5f);
		titlePanel.setPosition(
			parentSprite.getContentSize().width / 2,
			parentSprite.getContentSize().height - 98);
		
		// 타이틀
		CCSprite frameTitle = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						imageFolder + imageFolder.substring(2, imageFolder.length()-1) + "-title" + fileExtension));
		titlePanel.addChild(frameTitle);
		frameTitle.setPosition(titlePanel.getContentSize().width / 2, titlePanel.getContentSize().height / 2);
			
		// 경기 기간 배경
		CCSprite banner = CCSprite.sprite(commonfolder + "titlebanner" + fileExtension);
		titlePanel.addChild(banner);
		banner.setAnchorPoint(0.5f, 1);
		banner.setPosition(titlePanel.getContentSize().width / 2, 10);

		// 주간순위 마감 남은 시간
		CCLabel periodText = CCLabel.makeLabel(setInitTime() + " ", "Arial", 20);
		banner.addChild(periodText);
		periodText.setColor(ccColor3B.ccYELLOW);
		periodText.setAnchorPoint(0.5f, 0.5f);
		periodText.setPosition(
				banner.getContentSize().width / 2, 
				banner.getContentSize().height / 2);
	}
	
	
	private String setInitTime() {
		long deadLineTime = 0;
		Calendar calendar1 = Calendar.getInstance();
		long aa = (deadLineTime - calendar1.getTimeInMillis()) / 1000;
		if (deadLineTime - calendar1.getTimeInMillis() < 990) {
			deadLineTime = FacebookData.getinstance().getInitTime();
			aa = (deadLineTime - calendar1.getTimeInMillis()) / 1000;
		}

		long day3 = aa / (60 * 60 * 24);
		long hour3 = (aa % (60 * 60 * 24)) / (60 * 60);
		long min3 = (aa % (60 * 60)) / 60;
		long sec3 = aa % 60;

		String deadlineText = "";
		if (day3 > 0) 
			deadlineText = day3 + "일 ";
		if (hour3 > 0)
			deadlineText += hour3 + "시 ";
		if (min3 > 0) 
			deadlineText += min3 + "분 ";
		deadlineText += sec3 + "초 후 마감";
		return deadlineText;
	}
	
	// 하단 메뉴
	private void setBottomMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		
		// 좌측 버튼(상점)
		CCMenuItem button1 = CCMenuItemImage.item(
						imageFolder + "home-shopbutton1" + fileExtension,
						imageFolder + "home-shopbutton2" + fileExtension,
						nodeThis, "clicked");
		button1.setUserData("shop");
		
		//중앙 버튼(입장)
		CCMenuItem button2 = CCMenuItemImage.item(
						imageFolder + "home-enterbutton1" + fileExtension,
						imageFolder + "home-enterbutton2" + fileExtension,
						nodeThis, "clicked");
		button2.setUserData("enter");
		
		// 우측 버튼(설정)
		CCMenuItem button3 = CCMenuItemImage.item(
						imageFolder + "home-optionbutton1" + fileExtension,
						imageFolder + "home-optionbutton2" + fileExtension,
						nodeThis, "clicked");
		button3.setUserData("option");
		
		CCMenu bottomMenu = CCMenu.menu(button1, button2, button3);
		nodeThis.addChild(bottomMenu);
		bottomMenu.setPosition(0.0f, 0.0f);
		button1.setPosition(5, 30);
		button1.setAnchorPoint(0, 0);

		button2.setPosition(winsize().width / 2, 30);
		button2.setAnchorPoint(0.5f, 0);

		button3.setPosition(winsize().width - 5, 30);
		button3.setAnchorPoint(1, 0);
		
		// 친구 초대 버튼
		CCMenuItem friendsInvite = CCMenuItemImage.item(
						imageFolder + "home-invitebutton1" + fileExtension,
						imageFolder + "home-invitebutton2" + fileExtension,
						nodeThis, "clicked");
		friendsInvite.setUserData("invite");
		
		CCMenu inviteMenu = CCMenu.menu(friendsInvite);
		parentSprite.addChild(inviteMenu);
		inviteMenu.setContentSize(parentSprite.getContentSize());
		inviteMenu.setPosition(0,0);
		inviteMenu.setAnchorPoint(0,0);
		
		friendsInvite.setPosition((595 * winsize().width) / parentSprite.getBoundingBox().size.width ,
		inviteMenu.getContentSize().height - 710);
		friendsInvite.setAnchorPoint(1, 1);
	}
	
}
