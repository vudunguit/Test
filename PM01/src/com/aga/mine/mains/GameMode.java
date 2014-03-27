package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.mains.Utility;

public class GameMode extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "50mode/";
	final String fileExtension = ".png";

	CCSprite bg;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameMode();
		scene.addChild(layer);
		return scene;
	}

	public GameMode() {
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "gamebb" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		// 하단 이미지
		BottomImage.setBottomImage(this);
		
		this.setIsTouchEnabled(true);
//		String score = "" + ((int) ((Math.random() - 0.5f) * 1024));
		
		int scoreValue = (int) (Math.random() * 200);
		if (scoreValue < 0) {
			scoreValue = 0;
		}
//		String scoreStr = "" + ((int) (Math.random() * 200));
		
		Log.e("GameMode", "score[" + scoreValue + "]");
		Log.e("GameMode", "score : " + DataFilter.addGameScore("" + scoreValue));
	}
	
	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
	}
	
	// 게시판 설정
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);

		// 게시판명
		FrameTitle2.setTitle(boardFrame, folder);
	}
	
	// 게임 메뉴
	private void setMainMenu(CCSprite parentSprite){
		
		CCMenuItemImage button1 = CCMenuItemImage.item(
				folder + "mode-singlebutton1" + fileExtension,
				folder + "mode-singlebutton2" + fileExtension,
				this, "nextCallback");
		button1.setUserData(1);
		
		CCMenuItemImage button2 = CCMenuItemImage.item(
				folder + 	"mode-randombutton1" + fileExtension,
				folder + 	"mode-randombutton2" + fileExtension,
				this, "nextCallback");
		button2.setUserData(2);
		
		CCMenuItemImage button3 = CCMenuItemImage.item(
				folder + 	"mode-invitebutton1" + fileExtension,
				folder + 	"mode-invitebutton2" + fileExtension,
				this, "nextCallback");
		button3.setUserData(3);
		
		CCMenu gameMenu = CCMenu.menu(button1, button2, button3);
		
		CCSprite	buttonText1 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "mode-singletext" + fileExtension));
		button1.addChild(buttonText1);
		buttonText1.setPosition(button1.getContentSize().width - buttonText1.getContentSize().width/2 - 30f, button1.getContentSize().height/2);

		CCSprite	buttonText2 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "mode-randomtext" + fileExtension));
		button2.addChild(buttonText2);
		buttonText2.setPosition(button2.getContentSize().width - buttonText2.getContentSize().width/2 - 30f, button2.getContentSize().height/2);

		CCSprite	buttonText3 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "mode-invitetext" + fileExtension));
		button3.addChild(buttonText3);
		buttonText3.setPosition(button3.getContentSize().width - buttonText3.getContentSize().width/2 - 30f, button3.getContentSize().height/2);
		
		parentSprite.addChild(gameMenu, 1);
		gameMenu.setPosition(parentSprite.getContentSize().width / 2 - 5, parentSprite.getContentSize().height - 58);
		
		button1.setPosition(0f, -button1.getContentSize().height*0.5f); // 싱글
		button2.setPosition(0f, -button1.getContentSize().height*1.5f); // 대전
		button3.setPosition(0f, -button1.getContentSize().height*2.5f); // 초대
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	
	public void previousCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void nextCallback(Object sender) {
		GameData.share().setGameMode((Integer)((CCNode)sender).getUserData());
		CCScene scene = GameDifficulty.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
}