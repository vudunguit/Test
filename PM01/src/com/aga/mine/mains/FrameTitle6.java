package com.aga.mine.mains;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor3B;

public class FrameTitle6 {

	final static String commonfolder = "00common/";
	final static String fileExtension = ".png";
	
	public FrameTitle6() {
	}
	
	public static CCLabel setTitle(CCSprite parentSprite, String imageFolder) {
		
		// Ÿ��Ʋ �ǳ�
		CCSprite titlePanel = CCSprite.sprite(commonfolder + "frame-titlePanel" + fileExtension);
		parentSprite.addChild(titlePanel);
		titlePanel.setAnchorPoint(0.5f, 0.5f);
		titlePanel.setPosition(
			parentSprite.getContentSize().width / 2,
			parentSprite.getContentSize().height - 98);
		
		// Ÿ��Ʋ
		CCSprite frameTitle = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						imageFolder + imageFolder.substring(2, imageFolder.length()-1) + "-title" + fileExtension));
		titlePanel.addChild(frameTitle);
//		frameTitle.setAnchorPoint(0.5f, 0.5f);
		frameTitle.setPosition(titlePanel.getContentSize().width / 2, titlePanel.getContentSize().height / 2);
			
		// ��� �Ⱓ ���
		CCSprite banner = CCSprite.sprite(commonfolder + "titlebanner" + fileExtension);
		titlePanel.addChild(banner);
		banner.setAnchorPoint(0.5f, 1);
		banner.setPosition(titlePanel.getContentSize().width / 2, 10);
		
		// ��� �ؽ�Ʈ
		CCLabel goldText = CCLabel.makeLabel("Gold", "Arial", 24);
		banner.addChild(goldText);
		goldText.setColor(ccColor3B.ccYELLOW);
		goldText.setAnchorPoint(0, 0.5f);
		goldText.setPosition(15, banner.getContentSize().height / 2);
		
		
		// ��� ��
		String gold = new NumberComma().numberComma(FacebookData.getinstance().getDBData("Gold"));
		CCLabel goldValue = CCLabel.makeLabel(gold, "Arial", 24);
		banner.addChild(goldValue);
		goldValue.setAnchorPoint(1, 0.5f);
		goldValue.setPosition(
				banner.getContentSize().width - 15, 
				banner.getContentSize().height / 2);
		return goldValue;
	}
}
