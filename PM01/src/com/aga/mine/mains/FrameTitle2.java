package com.aga.mine.mains;

import org.cocos2d.nodes.CCSprite;

import com.aga.mine.mains.Utility;

public class FrameTitle2 {

	final static String commonfolder = "00common/";
	final static String fileExtension = ".png";
	
	public FrameTitle2() {
	}
	
	public static void setTitle(CCSprite parentSprite, String imageFolder) {
		//
		// 타이틀 판넬
		CCSprite titlePanel = CCSprite.sprite(commonfolder + "frame-titlePanel" + fileExtension);
		parentSprite.addChild(titlePanel);
		titlePanel.setAnchorPoint(0.5f, 0.5f);
		titlePanel.setPosition(
			parentSprite.getContentSize().width / 2,
			parentSprite.getContentSize().height - 110.0f);
		
		// 타이틀
		CCSprite frameTitle = CCSprite.sprite(
		Utility.getInstance().getNameWithIsoCodeSuffix(
				imageFolder + imageFolder.substring(2, imageFolder.length()-1) + "-title" + fileExtension));
		titlePanel.addChild(frameTitle);
		frameTitle.setAnchorPoint(0.5f, 0.5f);
		frameTitle.setPosition(titlePanel.getContentSize().width / 2, titlePanel.getContentSize().height / 2);
	}
	
}
