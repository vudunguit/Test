package com.aga.mine.main;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;

import com.aga.mine.util.Util;

public class BottomMenu3 {
	
	final static String fileExtension = ".png";
	
	public BottomMenu3() {
	}
	
	public static void setBottomMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		setBottomMenu(parentSprite, imageFolder, nodeThis, 999);
	}
	
	// 하단 메뉴
	public static void setBottomMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis, int tag) {
		
		// 좌측 버튼(랜덤매칭)
		CCMenuItem button1 = CCMenuItemImage.item(
				CCSprite.sprite(CCTextureCache.sharedTextureCache().addImageExternal(Util.RESOURCE + 
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-button1" + fileExtension))),
				CCSprite.sprite(CCTextureCache.sharedTextureCache().addImageExternal(Util.RESOURCE + 
				Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-button1" + fileExtension))), 
				nodeThis, "randomMatch");
		CCMenu bottomMenu = CCMenu.menu(button1);
		nodeThis.addChild(bottomMenu, tag, tag);
		bottomMenu.setPosition(0.0f, 0.0f);
		
		button1.setPosition(5, 30);
		button1.setAnchorPoint(0, 0);
	}

}
