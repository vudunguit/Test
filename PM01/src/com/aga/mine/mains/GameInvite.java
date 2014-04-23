package com.aga.mine.mains;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.util.Log;

import com.aga.mine.mains.ImageDownloader.ImageLoaderListener;
import com.aga.mine.util.Util;

public class GameInvite extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "53invite/";
	final int singleMode = 1;
	final int randomMode = 2;
	final int inviteMode = 3;
	
	public boolean isOwner = false;
	
	private CCSprite bg;
	static CCSprite backboard;

    private static CCScene scene;
	private static GameInvite gameInvite;
	
	public static CCScene scene() {
		scene = CCScene.node();
		CCLayer layer = new GameInvite();
		scene.addChild(layer);
		return scene;
	}
	
	public static CCScene scene(String id, String name, boolean owner) {
		scene = CCScene.node();
		CCLayer layer = new GameInvite(id, name, owner);
		scene.addChild(layer);
		return scene;
	}
	
	
	
	public static synchronized GameInvite getInstance() {
		if (gameInvite == null)
			gameInvite = new GameInvite();
		return gameInvite;
	} // 불필요 할듯

	private GameInvite() {
		try {
			NetworkController.getInstance().sendRoomOwner(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");	
		setBackBoardMenu(commonfolder + "bb1.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
		Util.setEntry(null, null, true, matchingPanel);
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		
		// 하단 이미지
		BottomMenu3.setBottomMenu(null, folder, this); 
		
//		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		//display scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_MATCHLIST);
	}
	
	private GameInvite(String id, String name, boolean owner) {

		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");	
		setBackBoardMenu(commonfolder + "bb1.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
		Util.setEntry(id, name, owner, matchingPanel);
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		
		// 하단 이미지
		BottomMenu3.setBottomMenu(null, folder, this); 
		
//		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		//display scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_MATCHLIST);
	}
	
	

	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		backboard = CCSprite.sprite(imageFullPath);
		bg.addChild(backboard, 0, 0);
		backboard.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		backboard.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(backboard);
	}
	
	static CCSprite boardFrame;
	// 게시판 설정
	private void setBoardFrameMenu(String imageFullPath) {
		boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle2.setTitle(boardFrame, folder);
	}
	
	List<CCNode> matchingPanel = null;
	// 메인 메뉴
	private void setMainMenu(CCSprite parentSprite){
		
		matchingPanel = new ArrayList<CCNode>();
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelMe.png")); 
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelOther.png")); 
				
		int count = 1;
		for (CCNode panel : matchingPanel) {

			parentSprite.addChild(panel, 0, count);
			panel.setPosition(
					parentSprite.getContentSize().width / 2, 
					parentSprite.getContentSize().height - panel.getContentSize().height * (count * 1.2f - 0.2f));
			
			// 이미지
			CCSprite playerPicture = CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진		
			panel.addChild(playerPicture, 0, 102);
			playerPicture.setAnchorPoint(0.5f, 0.5f);
			playerPicture.setPosition(playerPicture.getContentSize().width * 2.7f, panel.getContentSize().height / 2);
			
			// 이름
			CCLabel playerName = CCLabel.makeLabel("player" + count, "Arial", 30.0f);
			playerName.setAnchorPoint(0, 0.5f);
			playerName.setPosition(
					playerPicture.getPosition().x + ((1.3f - playerPicture.getAnchorPoint().x) * playerPicture.getContentSize().width), 
					playerPicture.getPosition().y);
			panel.addChild(playerName, 0, 103);
			count ++;
		}
	}
		
	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	final int previous = 501;
	final int home= 502;
	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			GameData.share().setGameDifficulty(0);

			switch (value) {
			case previous:
				scene = GameDifficulty.scene();
				
				break;

			case home:
				scene = Home.scene();
				break;
			}
			try {
				NetworkController.getInstance().sendRoomOwner(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

	// BottomMenu3.setBottomMenu
	public void randomMatch(Object sender) {
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		try {
//			GameDifficulty.mode =2;
			GameData.share().setGameMode(randomMode);
			NetworkController.getInstance().sendRoomOwner(1); // 방장 권한 주입 (random match에서만 있음)
			CCScene scene = GameRandom.scene();
			CCDirector.sharedDirector().replaceScene(scene);
			Log.e("CallBack", "RandomMatchLayer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}