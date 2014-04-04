package com.aga.mine.mains;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.aga.mine.pages.UserData;
import com.sromku.simple.fb.entities.Profile;

public class GameInvite extends CCLayer {
	
	final String commonfolder = "00common/";
	final String invitefolder = "30invite/";	
	final String folder = "53invite/";
	
	final int singleMode = 1;
	final int randomMode = 2;
	final int inviteMode = 3;
	
	final static int gameInvitescrollLayerTag = 11;
	final static int gameInviteLayerTag = 12;
	
	
	static CCSprite bg;
	static CCLabel player1Label;
	static CCLabel player2Label;
	CCSprite inviteButtonOff;
	CCMenuItem inviteButton;
	
	static int friendsSize;
	boolean scrollLock = false;
	
	int mode = 0;
	public boolean isOwner = false;
	
    private static CCScene scene;
    
	private Context mContext;
	private static GameInvite versusMatchLayer;
	UserData userData;

	
	static CCLayer inviteScroll = CCLayer.node();
	
	public static CCScene scene() {
//		CCScene scene = CCScene.node();
//		CCLayer layer = new GameInvite();
//		scene.addChild(layer);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
//		return scene;

			scene = CCScene.node();
			CCLayer layer = new GameInvite();
//			scene.addChild(layer);		
//			if (scene == null)
//				scene = CCScene.node();
//			if (layer == null)
//				layer = new Home(ccColor4B.ccc4(244, 122, 122, 0));
	        scene.addChild(layer, gameInviteLayerTag, gameInviteLayerTag);
	        
	        scene.addChild(inviteScroll, gameInvitescrollLayerTag, gameInvitescrollLayerTag);
//	        Log.e("Logo", "getContentSize : " + scene.getChildByTag(gameInvitescrollLayerTag).getContentSize());
//	        Log.e("Logo", "getAnchorPoint: " + scene.getChildByTag(gameInvitescrollLayerTag).getAnchorPoint());
	        scene.getChildByTag(gameInvitescrollLayerTag).setPosition(
	        		scene.getContentSize().width / 2 - scene.getChildByTag(gameInvitescrollLayerTag).getContentSize().width / 2,
	        		scene.getContentSize().height / 2 - scene.getChildByTag(gameInvitescrollLayerTag).getContentSize().height / 2);
	        
			CGPoint pf = bg.convertToWorldSpace(boardFrame.getPosition().x,boardFrame.getPosition().y);
			scrollTopBoundery = pf.y - (boardFrame.getAnchorPoint().y * boardFrame.getContentSize().height) + 445 - (81 * friendsSize) - 5;
			scrollBottomBoundery = pf.y - (boardFrame.getAnchorPoint().y * boardFrame.getContentSize().height) + 180;
			
			scene.getChildByTag(gameInvitescrollLayerTag).setPosition(86,100);
//			scene.getChildByTag(gameInvitescrollLayerTag).setPosition(
//	        		scene.getContentSize().width / 2 - scene.getChildByTag(gameInvitescrollLayerTag).getContentSize().width / 2, scrollTopBoundery);
	        return scene;
	}
	
	static float scrollTopBoundery; 
	static float scrollBottomBoundery; 

	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}

	public static synchronized GameInvite getInstance() {
		if (versusMatchLayer == null) {
			versusMatchLayer = new GameInvite();
			Log.e("GameInvite", "new Instance");
		} else {
			Log.e("GameInvite", "get Instance");
		}
		return versusMatchLayer;
	}

	private GameInvite() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		inviteScroll = InviteScroll.getInstance().getLayer(this);
		inviteScroll.setAnchorPoint(0.5f, 1);
		inviteScroll.setPosition(
				CCDirector.sharedDirector().winSize().width / 2 - inviteScroll.getContentSize().width / 2,
				CCDirector.sharedDirector().winSize().height / 2 - inviteScroll.getContentSize().height + 110);
		
		friendsSize = userData.facebookFriendsInfo.size();
		if (friendsSize < 4) {
			friendsSize = 4;
			scrollLock = true;
		} 
		
		this.mode =GameDifficulty.mode;
		
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg-open.png");
	
		setBackBoardMenu(folder + "invitematch-backboard2.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		
		// 하단 이미지
		BottomMenu3.setBottomMenu(null, folder, this); 
		
//		setBackground();
//		setMatchingMenu();
//		//setGameMenu();
//		aaa(1);
//		setSceneMenu();
		
		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
		this.setIsTouchEnabled(true);
	}
	

	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb, 0, 0);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
//		setFriendInvitionMenu(bb);
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
		
//		CCSprite player1Photo = CCSprite.sprite(FacebookData.getinstance().getUserPhoto()); // 프로필 사진
		
//		CCSprite player1Photo = CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진		
//		matchingPanel.get(0).getChildByTag(100).addChild(player1Photo, 0, 1000);
//		player1Photo.setScale(1 / 0.8f);
//		player1Photo.setAnchorPoint(0.5f, 0.5f);
//		player1Photo.setPosition(pictureFrame.getContentSize().width / 2, pictureFrame.getContentSize().height / 2);
		

		// facebook이름 (network에서 받을 것)
//		player1Label = CCLabel.makeLabel(userData.facebookUserInfo.getName(), "Arial", 30.0f);
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
		
//		for (GraphUser friend : FacebookData.getinstance().getFriendsInfo()) {
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
		
//		
//		
//		if (isOwner) {
//			for (GraphUser friend : userData.facebookFriendsInfo) {
//				Log.e("Random", "friend.getId() : " + friend.getId());
//				if (guestID.equals(friend.getId())) {
//					userName = hostName;
//					oppenentName = friend.getName();
//					
//					oppenentPhoto = getBitmapFromURL("https://graph.facebook.com/" + friend.getId() +"/picture");
//					CCSprite old_player2Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(11).getChildByTag(101).getChildByTag(1001);
//					old_player2Photo.setTexture(CCTextureCache.sharedTextureCache().addImage(oppenentPhoto, "player2Photo"));
//					
////					if (userPhoto.getRowBytes() < 100) {
////						player2Photo= CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진
////					} else {
////						player2Photo= CCSprite.sprite(userPhoto); // 프로필 사진
////					}
//					break;
//				}
//			}
//		} else {
//			for (GraphUser friend : userData.facebookFriendsInfo) {
//				Log.e("Random", "friend.getId() : " + friend.getId());
//				if (hostName.equals(friend.getId())) {
//					userName = friend.getName();
//					oppenentName = guestID;
////					player2Photo = player1Photo;
//					
//					oppenentPhoto = getBitmapFromURL("https://graph.facebook.com/" + friend.getId() +"/picture");
//					CCSprite old_player1Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100).getChildByTag(1000);
//					if (oppenentPhoto.getRowBytes() <100) {
//						old_player1Photo.setTexture(CCTextureCache.sharedTextureCache().addImage("buyEn.png"));
//					} else {
//						old_player1Photo.setTexture(CCTextureCache.sharedTextureCache().addImage(oppenentPhoto, "player1Photo"));						
//					}
//
//					
//					CCSprite old_player2Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(11).getChildByTag(101).getChildByTag(1001);
//					old_player2Photo.setTexture(CCTextureCache.sharedTextureCache().addImage(FacebookData.getinstance().getUserPhoto(), "player2Photo"));
//					
//					
////					if (userPhoto.getRowBytes() < 100) {
////						player1Photo= CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진
////					} else {
////						player1Photo= CCSprite.sprite(userPhoto); // 프로필 사진
////					}
//					break;
//				}
//			}
//		}
//
////		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
////			public void run() {
//					player1Label.setString(userName);
//					player2Label.setString(oppenentName);
//
////					player1Photo // 이미지가 안들어감. 수정 요함 random도 작업할것.
//					
////					CCSprite old_player1Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100).getChildByTag(1000);
////					old_player1Photo.setTexture(CCTextureCache.sharedTextureCache().addImage(userPhoto, "userPhoto"));
////					old_player1Photo.setDisplayFrame(CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(name));
//					
////					CCSprite parent1 = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100);
////					parent1.removeAllChildren(true);
////					parent1.addChild(player1Photo, 0, 1000);
//					
//					
////					CCSprite parent2 = (CCSprite) bg.getChildByTag(0).getChildByTag(11).getChildByTag(101);
////					parent2.addChild(player2Photo, 0, 1001);
////					player2Photo.setScale(1 / 0.8f);
////					player2Photo.setAnchorPoint(0.5f, 0.5f);
////					player2Photo.setPosition(parent2.getContentSize().width / 2, parent2.getContentSize().height / 2);		
////			}
////		});
	}
	
//	
//	String userName = "_유저";
//	String oppenentName = "_상대";
//	
//	public void matchNameReceiver(final String hostName, final String guestName) {
//
//		if (isOwner) {
//			for (GraphUser friend : userData.facebookFriendsInfo) {
//				Log.e("invite", "friend.getId() : " + friend.getId());
//				if (guestName.equals(friend.getId())) {
//					oppenentName = friend.getName();
//					userName = hostName;
//					break;
//				}
//			}
//		} else {
//			for (GraphUser friend : userData.facebookFriendsInfo) {
//				Log.e("invite", "friend.getId() : " + friend.getId());
//				if (hostName.equals(friend.getId())) {
//					userName = friend.getName();
//					oppenentName = guestName;
//					break;
//				}
//			}
//		}
//		
//		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
//			public void run() {
//					player1.setString(userName);
//					player2.setString(oppenentName);
//			}
//		});
//	}

	
	int friendNumber = 0;
	
	//친구 초대 메뉴
	// facebook친구는 network를 통해 받을 것
	private void setFriendInvitionMenu(CCSprite parentSprite){
		
		friendNumber = 10; // 임시로 10명
		
		// 친구 리스트
		CCSprite friendListBg = CCSprite.sprite(invitefolder + "invite-listPanel.png");
		parentSprite.addChild(friendListBg);
		
		// 친구 이미지
		CCSprite friendImage = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
		friendListBg.addChild(friendImage);
		
		CCLabel friendName = CCLabel.makeLabel("친구 아이가~?", "Arial", 30.0f);
		friendListBg.addChild(friendName);
		
		friendListBg.setAnchorPoint(0.5f, 1);
		friendListBg.setPosition(parentSprite.getContentSize().width / 2, 285);
		
		friendImage.setScale(0.8f);
		friendImage.setPosition(50.0f, friendListBg.getContentSize().height / 2);
		
		friendName.setAnchorPoint(0,  0.5f);
		friendName.setPosition(
				friendImage.getPosition().x + ((1 - friendImage.getAnchorPoint().x) * friendImage.getContentSize().width) + 10, 
				friendListBg.getContentSize().height / 2);
		
		// 초대 버튼
		inviteButton = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button1.png"),
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button2.png"),
				this, "matchCallback");
		inviteButton.setUserData("502000000000000");
//		inviteButton.setIsEnabled(false);
		

		
		CCMenu inviteMenu = CCMenu.menu(inviteButton);
		inviteMenu.alignItemsVertically(0);
		friendListBg.addChild(inviteMenu);
		inviteMenu.setPosition(
				friendListBg.getContentSize().width - inviteButton.getContentSize().width / 2 - 5, 
				friendListBg.getContentSize().height / 2);
		
		inviteButtonOff = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button2.png"));
		inviteButtonOff.setVisible(false);
		friendListBg.addChild(inviteButtonOff);
		inviteButtonOff.setPosition(inviteMenu.getPosition());

	}
	


	private void panForTranslation(CGPoint translation) {
		
		if (!scrollLock) {
//			CGPoint pf = bg.convertToWorldSpace(boardFrame.getPosition().x,boardFrame.getPosition().y);
			
	//		int scrollBottom = 0; 
	//		if (friendsSize < 4) {
	//			scrollBottom = 4 - friendsSize;
	//			friendsSize = 4;
	//		}
			 
			
	//		CGPoint limit = CGPoint.ccpClamp(
	//				CGPoint.ccp(scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x,translation.y), 
	//				CGPoint.ccp(scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x, -130),
	//				CGPoint.ccp(scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x, 290)
	//				);
	//		
	//		scene.getChildByTag(gameInvitescrollLayerTag).setPosition(
	//				scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x,
	//				CGPoint.ccpAdd(scene.getChildByTag(gameInvitescrollLayerTag).getPosition(), translation).y);
			
			// **scroll top** home-profileBg-hd.getposition.bottom - ((94 * items-1) + 10) 
			// **scroll bottom** frameGeneral-hd.getposition.bottom + 180 
			scene.getChildByTag(gameInvitescrollLayerTag).setPosition(
					scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x,
	//				CGPoint.clampf(CGPoint.ccpAdd(scene.getChildByTag(gameInvitescrollLayerTag).getPosition(), translation).y, 0, 290)
	//				CGPoint.clampf(CGPoint.ccpAdd(scene.getChildByTag(gameInvitescrollLayerTag).getPosition(), translation).y, -127, 290)
	
					
					CGPoint.clampf(CGPoint.ccpAdd(scene.getChildByTag(gameInvitescrollLayerTag).getPosition(), translation).y, 
							scrollTopBoundery, scrollBottomBoundery));
			
			//위치 테스트
	//		bsp.setString(" " + scene.getChildByTag(gameInvitescrollLayerTag).getPosition());
			
	//		Log.e("Home", "panForTranslation : " + scene.getChildByTag(gameInvitescrollLayerTag).getPosition());
					
	//				scene.getChildByTag(gameInvitescrollLayerTag).getPosition().x + translation.x,
	//				scene.getChildByTag(gameInvitescrollLayerTag).getPosition().y + translation.y);
	//	    if (selSprite) {
	//	        CGPoint newPos = ccpAdd(selSprite.position, translation);
	//	        selSprite.position = newPos;
	//	    } else {
	//	        CGPoint newPos = ccpAdd(self.position, translation);
	//	        self.position = self boundLayerPos:newPos;      
	//	    }  
		}
	}


	@Override
		public boolean ccTouchesMoved(MotionEvent event) {
//		int touch1 = event.getPointerId(0);
//		int pointerIndex1 = event.findPointerIndex(touch1);
	    
//			printSamples(event);
			CGPoint touchLocation = this.convertTouchToNodeSpace(event);
//			CGPoint touchLocation = CCDirector.sharedDirector().convertToGL(	CGPoint.make(event.getX(), event.getY()));
			touchLocation = this.convertToNodeSpace(touchLocation);
					
			CGPoint translation = CGPoint.make(0, 0);		 
			 if (event.getHistorySize() > 0) {
//					Log.e("ccTouchesMoved", "size/count : " + event.getHistorySize() +", " + event.getPointerCount() );
				CGPoint oldTouchLocation = CGPoint.make(
//						event.getHistoricalX(pointerIndex1, 0), event.getHistoricalY(pointerIndex1, 0));
						event.getHistoricalX(0, 0), event.getHistoricalY(0, 0));
//				Log.e("ccTouchesMoved", "oldTouchLocation1 : " + oldTouchLocation);
				oldTouchLocation = CCDirector.sharedDirector().convertToGL(oldTouchLocation);
//				Log.e("ccTouchesMoved", "oldTouchLocation2 : " + oldTouchLocation);
				oldTouchLocation = this.convertToNodeSpace(oldTouchLocation);
//				Log.e("ccTouchesMoved", "oldTouchLocation3 : " + oldTouchLocation);
				
				translation = CGPoint.ccpSub(touchLocation, oldTouchLocation);    
			}
			 this.panForTranslation(translation);  
			return super.ccTouchesMoved(event);
		}
	
	
	
	public void previousCallback(Object sender) {
		userData.difficulty = 0;
		try {
			NetworkController.getInstance().sendRoomOwner(0);
			CCScene scene = GameDifficulty.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void homeCallback(Object sender) {
		userData.difficulty = 0;
		try {
			NetworkController.getInstance().sendRoomOwner(0);
			CCScene scene = Home.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void matchCallback(Object sender) {
//			String facebookId = (String)inviteButton.getUserData();
////			inviteButton.setVisible(false);
////			inviteButtonOff.setVisible(true);
//			try {
//			NetworkController.getInstance().sendRequestMatchInvite(userData.difficulty, facebookId);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	//		String facebookId = (String)inviteButton.getUserData();
//	//		try {
//	//			NetworkController.getInstance().sendRequestIsPlayerConnected(facebookId);
//	//		} catch (IOException e) {
//	//			e.printStackTrace();
//	//		}
//		}

	public void randomMatching(Object sender) {
		try {
//			GameDifficulty.mode =2;
			userData.setGameMode(randomMode);
			NetworkController.getInstance().sendRoomOwner(1); // 방장 권한
			CCScene scene = GameRandom.scene();
			CCDirector.sharedDirector().replaceScene(scene);
			Log.e("CallBack", "RandomMatchLayer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void gameStart(Object sender) {
		if(userData.getBroomstick() > 0) {
			try {
				// Requestmatch말고 invite로 가야함.
				NetworkController.getInstance().sendRequestMatch(userData.difficulty);
				//CCScene scene = GameLayer.scene(mContext);
				CCScene scene = GameLoading.scene();
				CCDirector.sharedDirector().replaceScene(scene);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "빗자루가 부족합니다.", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
	}

	
}