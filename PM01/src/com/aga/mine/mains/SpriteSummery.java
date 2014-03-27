package com.aga.mine.mains;

import org.cocos2d.nodes.CCSprite;

public class SpriteSummery {
	
	public static CCSprite imageSummary(String image1, String image2) {
		CCSprite sprite1 = CCSprite.sprite(image1);
		CCSprite sprite2 = CCSprite.sprite(image2);
//		sprite2.setAnchorPoint(0.5f, 0.5f); // 원래 이게 맞는건데 안된다. 나중에 확인
		sprite2.setAnchorPoint(0,0);
		sprite2.setPosition(sprite1.getPosition().x/2, sprite1.getPosition().y/2);
		sprite1.addChild(sprite2);
		return sprite1;
	}
	
	public static CCSprite imageSummary(CCSprite baseSprite, String image) {
		CCSprite sprite2 = CCSprite.sprite(image);
//		sprite2.setAnchorPoint(0.5f, 0.5f); // 원래 이게 맞는건데 안된다. 나중에 확인
		sprite2.setAnchorPoint(0,0);
		sprite2.setPosition(baseSprite.getPosition().x/2, baseSprite.getPosition().y/2);
		baseSprite.addChild(sprite2);
		return baseSprite;
	}
	
	
	
}
