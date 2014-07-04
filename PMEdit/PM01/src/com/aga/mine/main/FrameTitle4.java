package com.aga.mine.main;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;

import com.aga.mine.main.Utility;
import com.aga.mine.util.Util;

public class FrameTitle4 {

	final static String fileExtension = ".png";
	
	public FrameTitle4() {
	}
	
	public static void setTitle(CCSprite parentSprite, String imageFolder) {
		// 타이틀
		CCSprite frameTitle = CCSprite.sprite(CCTextureCache.sharedTextureCache().addImageExternal(Util.RESOURCE + 
		Utility.getInstance().getNameWithIsoCodeSuffix(
				imageFolder + imageFolder.substring(2, imageFolder.length()-1) + "-title" + fileExtension)));
		parentSprite.addChild(frameTitle);
		frameTitle.setAnchorPoint(0.5f, 0.5f);
		frameTitle.setPosition(parentSprite.getContentSize().width / 2, parentSprite.getContentSize().height / 2);
	}
	
}
