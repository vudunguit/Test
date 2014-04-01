package com.aga.mine.mains;

import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

public class SpriteSummery {
	
	public static CCSprite imageSummary(String image1, String image2) {
		CCSprite sprite1 = CCSprite.sprite(image1);
		CCSprite sprite2 = CCSprite.sprite(image2);
//		sprite2.setAnchorPoint(0.5f, 0.5f); // ���� �̰� �´°ǵ� �ȵȴ�. ���߿� Ȯ��
		sprite2.setAnchorPoint(0,0);
		sprite2.setPosition(sprite1.getPosition().x/2, sprite1.getPosition().y/2);
		sprite1.addChild(sprite2);
		return sprite1;
	}
	
	public static CCSprite imageSummary(CCSprite baseSprite, String image) {
		CCSprite sprite2 = CCSprite.sprite(image);
//		sprite2.setAnchorPoint(0.5f, 0.5f); // ���� �̰� �´°ǵ� �ȵȴ�. ���߿� Ȯ��
		sprite2.setAnchorPoint(0,0);
		sprite2.setPosition(baseSprite.getPosition().x/2, baseSprite.getPosition().y/2);
		baseSprite.addChild(sprite2);
		return baseSprite;
	}
	
	public static CCMenuItemSprite menuItemBuilder(String normalBaseImageStr, String normalChildImageStr, 
			String selectBaseImageStr, String selectChildImageStr, CCNode nodeThis, String actionMethod) {
		CCMenuItemSprite menuItem = CCMenuItemImage.item(
				imageSummary(normalBaseImageStr, normalChildImageStr), 
				imageSummary(selectBaseImageStr, selectChildImageStr), nodeThis, actionMethod);
		return menuItem;
	}
	
}
