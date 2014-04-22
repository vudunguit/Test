package com.aga.mine.mains;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.aga.mine.pages.UserData;
import com.sromku.simple.fb.entities.Profile;

public class GameInvite extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "53invite/";
	
	public boolean isOwner = false;
	
	static CCSprite backboard;
	static CCLabel player1Label;
	static CCLabel player2Label;

    private static CCScene scene;
	private static GameInvite gameInvite;
	
	final int singleMode = 1; // ???
	final int randomMode = 2; // ???
	final int inviteMode = 3; // ???
	
	final static int gameInviteLayerTag = 12; // ???
	
	static CCSprite bg; // ??

	private Context mContext; // 불필요 할듯
	UserData userData; // 불필요 할듯

	public static CCScene scene() {
		scene = CCScene.node();
		CCLayer layer = new GameInvite();
		scene.addChild(layer, gameInviteLayerTag, gameInviteLayerTag);
		return scene;
	}
	
	public static synchronized GameInvite getInstance() {
		if (gameInvite == null)
			gameInvite = new GameInvite();
		return gameInvite;
	} // 불필요 할듯

	private GameInvite() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");	
		setBackBoardMenu(commonfolder + "bb1.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
		
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
	
	// 메인 메뉴
	private void setMainMenu(CCSprite parentSprite){
		int matchingPanelPosition = -20;
		
		List<CCSprite> matchingPanel = new ArrayList<CCSprite>();
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelMe.png")); 
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelOther.png")); 
		
		CCSprite pictureFrame = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
		CCSprite player1Photo = CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진		
		
		for (int i = 0; i < matchingPanel.size(); i++) {
			parentSprite.addChild(matchingPanel.get(i), 0, i + 10);
			matchingPanel.get(i).setPosition(
					parentSprite.getContentSize().width / 2, 
					parentSprite.getContentSize().height - matchingPanel.get(0).getContentSize().height * (i + 0.5f) - (i * 5) - 35  + matchingPanelPosition);

			 // facebook 이미지
			pictureFrame = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
			matchingPanel.get(i).addChild(pictureFrame, 0, i + 100);
			pictureFrame.setScale(0.8f);		
			pictureFrame.setPosition(pictureFrame.getContentSize().width * 2, matchingPanel.get(0).getContentSize().height / 2);
			
			pictureFrame.addChild(player1Photo, 0, i + 1000);
			player1Photo.setScale(1 / 0.8f);
			player1Photo.setAnchorPoint(0.5f, 0.5f);
			player1Photo.setPosition(pictureFrame.getContentSize().width / 2, pictureFrame.getContentSize().height / 2);
		}
		
		player1Label = CCLabel.makeLabel(FacebookData.getinstance().getUserInfo().getName(), "Arial", 30.0f);
		player2Label = CCLabel.makeLabel("상 대", "Arial", 30.0f);
		Log.e("GameInvite", "kMessageMatchCompleted : " + "yourName");
		
		matchingPanel.get(0).addChild(player1Label);
		matchingPanel.get(1).addChild(player2Label);
		
		player1Label.setAnchorPoint(0,  0.5f);
		player1Label.setPosition(
				pictureFrame.getPosition().x + ((1 - pictureFrame.getAnchorPoint().x) * pictureFrame.getContentSize().width) + 10, 
				matchingPanel.get(0).getContentSize().height / 2);
		player2Label.setAnchorPoint(0,  0.5f);
		player2Label.setPosition(player1Label.getPosition().x, matchingPanel.get(1).getContentSize().height / 2);
	}
	

	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	String userName = "_유저";
	String oppenentName = "_상대";
	CCSprite player1Photo = null;
	CCSprite player2Photo = null;
	Bitmap oppenentPhoto = null;
	
	public void matchNameReceiver(final String hostName, final String guestID) {
		
		player1Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100).getChildByTag(1000);
		player2Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(11).getChildByTag(101).getChildByTag(1001);
		
		for (Profile friend : FacebookData.getinstance().getFriendsInfo()) {
			if (guestID.equals(friend.getId())) {
				oppenentName = friend.getName();
				oppenentPhoto = getBitmapFromURL("https://graph.facebook.com/" + friend.getId() +"/picture");
			}
		}
		
		if (isOwner) {
			player1Label.setString(hostName);
			player2Label.setString(oppenentName);
			player2Photo.setDisplayFrame(CCSpriteFrame.frame(
					CCTextureCache.sharedTextureCache().addImage(oppenentPhoto, " "), 
					player2Photo.getBoundingBox(), player2Photo.getPosition()
					));
			
		} else {
			player1Label.setString(oppenentName);
			player2Label.setString(hostName);			
			player1Photo.setDisplayFrame(CCSpriteFrame.frame(
					CCTextureCache.sharedTextureCache().addImage(oppenentPhoto, " "), 
					player1Photo.getBoundingBox(), player1Photo.getPosition()
					));
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
			userData.difficulty = 0;

			switch (value) {
			case previous:
				scene = GameDifficulty.scene();
				break;

			case home:
				scene = Home.scene();
				break;
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
			userData.setGameMode(randomMode);
			NetworkController.getInstance().sendRoomOwner(1); // 방장 권한 주입 (random match에서만 있음)
			CCScene scene = GameRandom.scene();
			CCDirector.sharedDirector().replaceScene(scene);
			Log.e("CallBack", "RandomMatchLayer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}