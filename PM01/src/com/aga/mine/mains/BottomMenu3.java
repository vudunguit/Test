package com.aga.mine.mains;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

public class BottomMenu3 {
	
	final static String fileExtension = ".png";
	
	public BottomMenu3() {
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
				"randomMatch");
		CCMenu bottomMenu = CCMenu.menu(button1);
		nodeThis.addChild(bottomMenu);
		bottomMenu.setPosition(0.0f, 0.0f);
		
		button1.setPosition(5, 30);
		button1.setAnchorPoint(0, 0);
	}

}
