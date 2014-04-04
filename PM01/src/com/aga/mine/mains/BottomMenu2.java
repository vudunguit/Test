package com.aga.mine.mains;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;

import com.aga.mine.mains.Utility;

public class BottomMenu2 {
	
	final static String fileExtension = ".png";
	
	public BottomMenu2() {
	}

	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	// �ϴ� �޴�
	public static void setBottomMenu(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		
		//�߾� ��ư(����)
		CCMenuItem button2 = CCMenuItemImage.item(
				SpriteSummery.imageSummary(
						imageFolder + "random-buttonReady-normal-hd" + fileExtension,
						Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-textReady-normal" + fileExtension)),
				SpriteSummery.imageSummary(
						imageFolder + "random-buttonReady-select-hd" + fileExtension,
						Utility.getInstance().getNameWithIsoCodeSuffix(imageFolder + "random-textReady-select" + fileExtension)), 
						nodeThis, "gameStart");
		
		CCMenu bottomMenu = CCMenu.menu(button2);
		nodeThis.addChild(bottomMenu);
		bottomMenu.setPosition(0.0f, 0.0f);

		button2.setPosition(winsize().width / 2, 30);
		button2.setAnchorPoint(0.5f, 0);
	}

}
