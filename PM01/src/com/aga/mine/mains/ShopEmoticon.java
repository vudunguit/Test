package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

public class ShopEmoticon extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "24emoticon/";
	final String fileExtension = ".png";
	
	CCSprite bg;
	
	int mode = 0;
	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	
	CCSprite boardFrame;
	CCSprite 	layer4;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopEmoticon();
		scene.addChild(layer);
		return scene;
	}
		
	public ShopEmoticon() {
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "bb1" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);

		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_EMOTICONLIST);
	}
	
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
	}
	
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle6.setTitle(boardFrame, folder);
	}
	
	final int previous = 501;
	final int home= 502;
	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			switch (value) {
			case previous:
				scene = Shop.scene();
				break;

			case home:
				scene = Home.scene();
				break;
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

}
