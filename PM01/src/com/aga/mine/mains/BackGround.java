package com.aga.mine.mains;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class BackGround {

	public BackGround() {
	}
	
	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}

	public static CCSprite setBackground(CCNode nodeThis, CGPoint anchor, String backGroundImage) {
		CCSprite bg = CCSprite.sprite(backGroundImage);
		nodeThis.addChild(bg);
		bg.setScale(winsize().width / bg.getBoundingBox().size.width);
		bg.setPosition(anchor.x * winsize().width, anchor.y * winsize().height);
		bg.setAnchorPoint(anchor);		
		return bg;
	}
}
