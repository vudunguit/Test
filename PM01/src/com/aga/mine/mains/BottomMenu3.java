package com.aga.mine.mains;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;

import com.aga.mine.mains.Utility;

public class BottomMenu3 {
	
	final static String fileExtension = ".png";
	
	public BottomMenu3() {
	}

	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	// 하단 메뉴
	public static void setBottomMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		
		// 좌측 버튼(랜덤매칭)
		CCMenuItem button1 = SpriteSummery.menuItemBuilder(
				imageFolder + "random-button1" + fileExtension, 
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-buttonText1" + fileExtension), 
				imageFolder + "random-button2" + fileExtension, 
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-buttonText2" + fileExtension), 
				nodeThis, 
				"randomMatching");
//		CCMenuItem button1 = CCMenuItemImage.item(
//						Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-buttonText1" + fileExtension),
//						Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-buttonText2" + fileExtension), 
//						nodeThis, "randomMatching");
		
		// 삭제됨.
//		// 우측 버튼(게임 스타트)
//		CCMenuItem button3 = CCMenuItemImage.item(
//				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "matching-buttonPlay" + fileExtension),
//				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "matching-buttonPlay-select" + fileExtension),
//						nodeThis, "gameStart");
		
//		CCMenu bottomMenu = CCMenu.menu(button1, button3);
		CCMenu bottomMenu = CCMenu.menu(button1);
		nodeThis.addChild(bottomMenu);
		bottomMenu.setPosition(0.0f, 0.0f);
		
		button1.setPosition(5, 30);
		button1.setAnchorPoint(0, 0);

//		button3.setPosition(winsize().width - 5, 30);
//		button3.setAnchorPoint(1, 0);
	}

}
