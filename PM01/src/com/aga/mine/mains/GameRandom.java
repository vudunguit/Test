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
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.aga.mine.mains.NetworkController;
import com.aga.mine.pages.UserData;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.entities.Profile;

public class GameRandom extends CCLayer {
	int mode = 0;
	int count = 5;
	
	public boolean isOwner = false;

	static CCSprite bg;
	static CCLabel player1Label;
	static CCLabel player2Label;
	public String aaaa = " "; 
	
	final String commonfolder = "00common/";
	final String folder = "52random/";
	
	CCSprite sprite1;
	CCSprite sprite2;
	CCSprite illust;
	CCSprite countdownNumber1; 
	CCSprite countdownNumber2; 
	CCSprite countdownNumber3; 
	CCSprite countdownNumber4; 
	CCSprite countdownNumber5; 
	
	private Context mContext;
	UserData userData;
	
	private static GameRandom randomMatchLayer;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameRandom();
		scene.addChild(layer);
		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}
	
	 public static synchronized GameRandom getInstance() {
		if (randomMatchLayer == null) {
			randomMatchLayer = new GameRandom();
			Log.e("GameRandom", "new Instance");
		} else {
			Log.e("GameRandom", "get Instance");
		}
		return randomMatchLayer;
	}

	private GameRandom() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		this.mode =GameDifficulty.mode;
		
		//��� �׸� ����
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");
		
		setBackBoardMenu(commonfolder + "gamebb.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
		// ��� �޴�
		TopMenu2.setSceneMenu(this);
		 // ��� �ּ�ó��
		// �ϴ� �̹���
		BottomImage.setBottomImage(this);
//		BottomMenu2.setBottomMenu(null, folder, this);

		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
		this.setIsTouchEnabled(true);
		try {
			NetworkController.getInstance().sendRequestMatch(userData.difficulty); // ���̵� ����
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// �� ���� ����
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb, 0, 0);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
		countdown(bb);
	}
	
	// �Խ��� ����
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle2.setTitle(boardFrame, folder);
	}

	// ���� �޴�
	private void setMainMenu(CCSprite parentSprite){
		int matchingPanelPosition = -20;
		
		List<CCSprite> matchingPanel = new ArrayList<CCSprite>();
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelMe.png")); 
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelOther.png")); 
		
		CCSprite pictureFrame = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
		
		for (int i = 0; i < matchingPanel.size(); i++) {
			parentSprite.addChild(matchingPanel.get(i), 0, i + 10);
			matchingPanel.get(i).setPosition(
					parentSprite.getContentSize().width / 2, 
					parentSprite.getContentSize().height - matchingPanel.get(0).getContentSize().height * (i + 0.5f) - (i * 5) - 35  + matchingPanelPosition);

			 // facebook �̹���
			pictureFrame = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
			matchingPanel.get(i).addChild(pictureFrame, 0, i + 100);
			pictureFrame.setScale(0.8f);		
			pictureFrame.setPosition(pictureFrame.getContentSize().width * 2, matchingPanel.get(0).getContentSize().height / 2);
		}
		
//		CCSprite player1Photo = CCSprite.sprite(FacebookData.getinstance().getUserPhoto()); // ������ ����
		CCSprite player1Photo = CCSprite.sprite(commonfolder + "noPicture.png"); // ������ ����
		matchingPanel.get(0).getChildByTag(100).addChild(player1Photo, 0, 1000);
		player1Photo.setScale(1 / 0.8f);
		player1Photo.setAnchorPoint(0.5f, 0.5f);
		player1Photo.setPosition(pictureFrame.getContentSize().width / 2, pictureFrame.getContentSize().height / 2);
		

		// facebook�̸� (network���� ���� ��)
//		player1Label = CCLabel.makeLabel(userData.facebookUserInfo.getName(), "Arial", 30.0f);
		player1Label = CCLabel.makeLabel(FacebookData.getinstance().getUserInfo().getName(), "Arial", 30.0f);
		player2Label = CCLabel.makeLabel("�� ��", "Arial", 30.0f);
		Log.e("GameRandom", "kMessageMatchCompleted : " + "yourName");
		
		matchingPanel.get(0).addChild(player1Label);
		matchingPanel.get(1).addChild(player2Label);
		
		player1Label.setAnchorPoint(0,  0.5f);
		player1Label.setPosition(
				pictureFrame.getPosition().x + ((1 - pictureFrame.getAnchorPoint().x) * pictureFrame.getContentSize().width) + 10, 
				matchingPanel.get(0).getContentSize().height / 2);
		player2Label.setAnchorPoint(0,  0.5f);
		player2Label.setPosition(player1Label.getPosition().x, matchingPanel.get(1).getContentSize().height / 2);
	}
	
	private void countdown(CCSprite parentSprite){

		illust = CCSprite.sprite(folder + "randomMatchingBottomIllust.png"); // ī��Ʈ �ٿ� ���
		parentSprite.addChild(illust);
		illust.setPosition(parentSprite.getContentSize().width / 2, illust.getContentSize().height / 2);

		countdownNumber1 = CCSprite.sprite(folder + "n01.png");
		countdownNumber2 = CCSprite.sprite(folder + "n02.png");
		countdownNumber3 = CCSprite.sprite(folder + "n03.png");
		countdownNumber4 = CCSprite.sprite(folder + "n04.png");
		countdownNumber5 = CCSprite.sprite(folder + "n05.png");

		illust.addChild(countdownNumber1);
		illust.addChild(countdownNumber2);
		illust.addChild(countdownNumber3);
		illust.addChild(countdownNumber4);
		illust.addChild(countdownNumber5);
		
		countdownNumber1.setVisible(false);
		countdownNumber2.setVisible(false);
		countdownNumber3.setVisible(false);
		countdownNumber4.setVisible(false);
		countdownNumber5.setVisible(true);
		
		countdownNumber1.setPosition(illust.getContentSize().width / 2 , illust.getContentSize().height / 2);
		countdownNumber2.setPosition(countdownNumber1.getPosition());
		countdownNumber3.setPosition(countdownNumber1.getPosition());
		countdownNumber4.setPosition(countdownNumber1.getPosition());
		countdownNumber5.setPosition(countdownNumber1.getPosition());
	}
	
//	//	��� �׸� ���� (frameMatching)
//	private void setBackground(){
		
//		bg = CCSprite.sprite("bg.png"); //��׶���(Ǯ)
//		bg.setPosition(CGPoint.make(winsize().width/2, winsize().height/2));
//		
//		CCSprite bb = CCSprite.sprite("mode-backboard.png"); //�Խ��� ���
//		bb.setPosition(CGPoint.make(winsize().width/2-4f, winsize().height/2-10f));
//		
		
//		// ----  ���� �� & ��� --------//
//		CCSprite mm = CCSprite.sprite("matchPanelMe.png"); // ��Ī ��
//		mm.setPosition(bb.getContentSize().width/2-10.0f,bb.getContentSize().height - mm.getContentSize().height/2-35f);
//		//mm.setPosition(CGPoint.make(winsize().width/2-3f,winsize().height-303.0f)); // �������� �̹��� �������� ���� ��ġ �̵� �ʿ�
//		//mm.setAnchorPoint(0.5f,0.5f);
//		bb.addChild(mm);
//		
//		CCSprite mo = CCSprite.sprite("matchPanelOther.png"); // ��Ī ���
//		mo.setPosition(bb.getContentSize().width/2-10.0f,bb.getContentSize().height - (mo.getContentSize().height*1.5f)-40f);
//		bb.addChild(mo);
//		//mo.setPosition(CGPoint.make(winsize().width/2-3f, winsize().height-349.0f)); // �������� �̹��� �������� ���� ��ġ �̵� �ʿ�
//		//mo.setAnchorPoint(0.5f,0.5f);
//		//this.addChild(mo);
//		
//		CCSprite pictureFrame = CCSprite.sprite("frame-pictureFrame-hd.png"); // ��Ī �̹���
//		//pf.setPosition(CGPoint.make(75.0f, mm.getAnchorPoint().y+1f)); // �������� �̹��� �������� ���� ��ġ �̵� �ʿ�
//		pictureFrame.setPosition(pictureFrame.getContentSize().width*2, mm.getContentSize().height/2); // CGpoint�� ���� ������....
//		//pf.setAnchorPoint(0.5f, 0f);
//		pictureFrame.setScale(0.8f);
//		mm.addChild(pictureFrame);
//		mo.addChild(pictureFrame);
//		
//		// �̸��� ��Ʈ������ db���� ������
//		CCLabel  myName = CCLabel.makeLabel("���浿", "Arial", 30.0f);
//		myName.setPosition(CGPoint.make(250.0f, mm.getContentSize().height/2));
//		mm.addChild(myName);
//		
//		myName = CCLabel.makeLabel("�ʱ浿", "Arial", 30.0f);
//		myName.setPosition(CGPoint.make(250.0f, mo.getContentSize().height/2));
//		mo.addChild(myName);
//		// ----  ���� �� & ��� --------//
//		
//		illust = CCSprite.sprite("randomMatchingBottomIllust.png"); // ī��Ʈ �ٿ� ���
//		illust.setPosition(
//				bb.getContentSize().width/2 - 4.0f, 
//				illust.getContentSize().height/2 + 20.0f);
//		bb.addChild(illust);
//
//		countdownNumber1 = CCSprite.sprite("n01.png"); // ī��Ʈ 1
//		countdownNumber1.setPosition(
//				illust.getContentSize().width/2, 
//				illust.getContentSize().height/2);
//		illust.addChild(countdownNumber1);
//		countdownNumber1.setVisible(false);
//
//		countdownNumber2 = CCSprite.sprite("n02.png"); // ī��Ʈ 2
//		countdownNumber2.setPosition(
//				illust.getContentSize().width/2, 
//				illust.getContentSize().height/2);
//		illust.addChild(countdownNumber2);
//		countdownNumber2.setVisible(false);
//		
//		countdownNumber3 = CCSprite.sprite("n03.png"); // ī��Ʈ 3
//		countdownNumber3.setPosition(
//				illust.getContentSize().width/2, 
//				illust.getContentSize().height/2);
//		illust.addChild(countdownNumber3);
//		countdownNumber3.setVisible(false);
//		
//		countdownNumber4 = CCSprite.sprite("n04.png"); // ī��Ʈ 4
//		countdownNumber4.setPosition(
//				illust.getContentSize().width/2, 
//				illust.getContentSize().height/2);
//		illust.addChild(countdownNumber4);
//		countdownNumber4.setVisible(false);
//		
//		countdownNumber5 = CCSprite.sprite("n05.png"); // ī��Ʈ 5
//		countdownNumber5.setPosition(
//				illust.getContentSize().width/2, 
//				illust.getContentSize().height/2);
//		illust.addChild(countdownNumber5);
//		countdownNumber5.setVisible(false);
//		
//		
//
//		
//		
//		
//		// ----  �Խ��� Ʋ --------//
//		CCSprite boardFrame = CCSprite.sprite("frameMatching.png"); // �Խ��� 
//		boardFrame.setPosition(bb.getContentSize().width/2+5f, bb.getContentSize().height/2+20f);
//
//		CCSprite titlePanel = CCSprite.sprite("frame-titlePanel.png"); // �Խ��� ���� 
//		titlePanel.setPosition(boardFrame.getContentSize().width/2, boardFrame.getContentSize().height-100.0f);
//		
//		CCSprite frameTitle = CCSprite.sprite("random-titleKo.png"); // �Խ��� ��(������Ī ����)
//		frameTitle.setPosition(titlePanel.getContentSize().width/2,titlePanel.getContentSize().height/2+2f);
//		
//		this.addChild(bg); // ��׶���
//		
//		titlePanel.addChild(frameTitle); // ���п� �Խ��Ǹ� ���̱�
//		boardFrame.addChild(titlePanel); // �Խ��ǿ� ���� ���̱�
//		bb.addChild(boardFrame, 2); // �麸�忡 �Խ���(Ʋ) ���̱�
//		this.addChild(bb); // layer�� �麸��ٺ�
//		
//		}
	
	
//	
//	// scene �̵� �޴�
//	private void setSceneMenu(){
//		
//		CCMenuItemImage frameButtonBack = CCMenuItemImage.item(
//				"frame-buttonBack-normal.png",
//				"frame-buttonBack-select.png",
//				this, "PreviousCallback");
//		
//		CCMenuItemImage frameButtonHome = CCMenuItemImage.item(
//				"frame-buttonHome-normal.png",
//				"frame-buttonHome-select.png",
//				this,"homeCallback");
//		
//		CCMenuItem[] items = { frameButtonBack, frameButtonHome };
//		
//		CCMenu menu = CCMenu.menu(items);
//
//		frameButtonBack.setPosition(frameButtonBack.getContentSize().width/2+0.0f, 0f);
//		frameButtonHome.setPosition(winsize().width - frameButtonHome.getContentSize().width/2-0.0f, 0f);
//		menu.setPosition(CGPoint.ccp(0f, winsize().height - 200.0f));
//		addChild(menu);
//	}

/*
	// ���� �޴�
	private void setGameMenu(){
		
		CCMenuItemImage button1 = CCMenuItemImage.item(
				"uiImages/05 Mode/mode-buttonSingle.png",
				"uiImages/05 Mode/mode-buttonSingle-select.png",
				this, "randomMatching");
		
		CCMenuItemImage button2 = CCMenuItemImage.item(
				"uiImages/05 Mode/mode-buttonVersus.png",
				"uiImages/05 Mode/mode-buttonVersus-select.png",
				this, "gameStart");
				
		CCSprite	buttonText1 = CCSprite.sprite("uiImages/05 Mode/mode-textSingleKo.png");
		buttonText1.setPosition(button1.getContentSize().width-buttonText1.getContentSize().width/2-30f, button1.getContentSize().height/2);
		button1.addChild(buttonText1);
		CCSprite	buttonText2 = CCSprite.sprite("uiImages/05 Mode/mode-textVersusKo.png");
		buttonText2.setPosition(button2.getContentSize().width-buttonText2.getContentSize().width/2-30f, button2.getContentSize().height/2);
		button2.addChild(buttonText2);
		
		CCMenuItem[] gameItems = { button1, button2 };
		CCMenu gameMenu = CCMenu.menu(gameItems);

		button1.setPosition(0f,-button1.getContentSize().height*0.5f); // ������Ī
		button2.setPosition(0f,-button1.getContentSize().height*1.5f); // ���ӽ���
		gameMenu.setPosition(CGPoint.ccp(bb.getContentSize().width/2,bb.getContentSize().height/2-33f));
		
		bb.addChild(gameMenu, 1);
		this.addChild(bb); // �麸��
	}

	*/
	
//	
//	public CCSprite imageSummary(String image1, String image2) {
//		sprite1 = CCSprite.sprite(image1);
//		sprite2 = CCSprite.sprite(image2);
//		//sprite2.setPosition(sprite1.getContentSize().width/2, sprite1.getContentSize().height/2);
//		sprite2.setPosition(0f,0f);
//		sprite1.addChild(sprite2);
//		return sprite1;
//	}
	
//	
//	// ���� �޴�
//	public void setMatchingMenu(){
//	/*	
//		CCMenuItemSprite button1 = CCMenuItemSprite.item(
//				imageSummary("uiImages/07-01 Random/random-buttonReady-normal.png","uiImages/07-01 Random/random-textReadyKo-normal.png"),
//				imageSummary("uiImages/07-01 Random/random-buttonReady-select.png","uiImages/07-01 Random/random-textReadyKo-select.png"),
//				this, "gameStart");
//		*/
//		CCMenuItemImage button1 = CCMenuItemImage.item(
//				"random-buttonReadyKo.png",
//				"random-buttonReadyKo-select.png",
//				this, "gameStart");
//
////		CCMenuItem[] gameItems = { button1};
////		CCMenu gameMenu = CCMenu.menu(gameItems);
//		CCMenu gameMenu = CCMenu.menu(button1);
//
//		button1.setPosition(button1.getContentSize().width/2,button1.getContentSize().height/2); // ������Ī
//		gameMenu.setPosition(CGPoint.ccp(0.0f, button1.getContentSize().height/2));
//		addChild(gameMenu);
//	}

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
	
	String userName = "_����";
	String oppenentName = "_���";
	CCSprite player1Photo = null;
	CCSprite player2Photo = null;
	
	public void matchNameReceiver(final String hostName, final String guestName) {
		player1Photo = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100).getChildByTag(1000);
		
		if (isOwner) {
//			for (GraphUser friend : userData.facebookFriendsInfo) {
			for (Profile friend : FacebookData.getinstance().getFriendsInfo()) {
				Log.e("Random", "friend.getId() : " + friend.getId());
				if (guestName.equals(friend.getId())) {
					userName = hostName;
					oppenentName = friend.getName();
					Bitmap userPhoto = getBitmapFromURL("https://graph.facebook.com/" + friend.getId() +"/picture");
					if (userPhoto.getRowBytes() < 100) {
						player2Photo= CCSprite.sprite(commonfolder + "noPicture.png"); // ������ ����
					} else {
						player2Photo= CCSprite.sprite(userPhoto); // ������ ����
					}
					break;
				}
			}
		} else {
			for (Profile friend : FacebookData.getinstance().getFriendsInfo()) {
				Log.e("Random", "friend.getId() : " + friend.getId());
				if (hostName.equals(friend.getId())) {
					userName = friend.getName();
					oppenentName = guestName;
					player2Photo = player1Photo;
					Bitmap userPhoto = getBitmapFromURL("https://graph.facebook.com/" + friend.getId() +"/picture");
					if (userPhoto.getRowBytes() < 100) {
						player1Photo= CCSprite.sprite(commonfolder + "noPicture.png"); // ������ ����
					} else {
						player1Photo= CCSprite.sprite(userPhoto); // ������ ����
					}
					break;
				}
			}
		}

		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
			public void run() {
					player1Label.setString(userName);
					player2Label.setString(oppenentName);
					
//					player1Photo // �̹����� �ȵ�. ���� ���� invite�� �۾��Ұ�.
					
					//�ӽ÷� ����.
//					CCSprite parent1 = (CCSprite) bg.getChildByTag(0).getChildByTag(10).getChildByTag(100);
//					parent1.removeAllChildren(true);
//					parent1.addChild(player1Photo, 0, 1000);
//					CCSprite parent = (CCSprite) bg.getChildByTag(0).getChildByTag(11).getChildByTag(101);
//					parent.addChild(player2Photo, 0, 1001);
//					player2Photo.setScale(1 / 0.8f);
//					player2Photo.setAnchorPoint(0.5f, 0.5f);
//					player2Photo.setPosition(parent.getContentSize().width / 2, parent.getContentSize().height / 2);		
			}
		});
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		return true;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	
	public void previousCallback(Object sender) {
		CCScene scene;
		try {
			userData.difficulty = 0;
			NetworkController.getInstance().sendRequestMatch(userData.difficulty); // ���̵� ����
//			if (this.mode == 3) {
			scene = GameDifficulty.scene();
//			} else if (this.mode == 4) {
//				scene = VersusMatchLayer.scene(mContext);
//			} else {
//				scene = ModeSelectLayer.scene(mContext);
//			}
			CCDirector.sharedDirector().replaceScene(scene);
			Log.e("CallBack", "DifficultyLayer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void homeCallback(Object sender) {
		try {
			userData.difficulty = 0;
			NetworkController.getInstance().sendRoomOwner(0); // ���� ����
			NetworkController.getInstance().sendRequestMatch(userData.difficulty); // ���̵� ����
			CCScene scene = Home.scene();
			CCDirector.sharedDirector().replaceScene(scene);
			Log.e("CallBack", "HomeLayer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void gameStart(Object sender) {
		Log.e("gameStart", "1");
		if(userData.getBroomstick() > 0) {
			Log.e("gameStart", "2");
			schedule("count", 1.0f);
			Log.e("gameStart", "3");
		} else {
			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "���ڷ簡 �����մϴ�.", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
		Log.e("CallBack", "GameLayer");
	}
	
	// �ð��� ������ �׳� ���ڵ���.
	public void count(float dt) {
		
//		Log.e("count", "" + count);
		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
			public void run() {
		switch (count) {

//		case 1:
//			countdownNumber1.setVisible(true);
//			countdownNumber2.setVisible(false);
//			countdownNumber3.setVisible(false);
//			countdownNumber4.setVisible(false);
//			countdownNumber5.setVisible(false);
//			break;
//		case 2:
//			countdownNumber1.setVisible(false);
//			countdownNumber2.setVisible(true);
//			countdownNumber3.setVisible(false);
//			countdownNumber4.setVisible(false);
//			countdownNumber5.setVisible(false);
//			break;
//		case 3:
//			countdownNumber1.setVisible(false);
//			countdownNumber2.setVisible(false);
//			countdownNumber3.setVisible(true);
//			countdownNumber4.setVisible(false);
//			countdownNumber5.setVisible(false);
//			break;
//		case 4:
//			countdownNumber1.setVisible(false);
//			countdownNumber2.setVisible(false);
//			countdownNumber3.setVisible(false);
//			countdownNumber4.setVisible(true);
//			countdownNumber5.setVisible(false);
//			break;
//		case 5:
//			countdownNumber1.setVisible(false);
//			countdownNumber2.setVisible(false);
//			countdownNumber3.setVisible(false);
//			countdownNumber4.setVisible(false);
//			countdownNumber5.setVisible(true);
//			break;

		default:
			CCScene scene = GameLoading.scene();
			CCDirector.sharedDirector().replaceScene(scene);
			break;
			
		}
			}
		});
		count--;
	}
	
}