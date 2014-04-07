package com.aga.mine.mains;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.pages.UserData;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.entities.Profile;

public class Home2 extends CCLayer {
		
	final String commonfolder = "00common/";
	final String folder = "10home/";
	final String fileExtension = ".png";
	
	static CCSprite bg;
	static CCSprite bb;
	static CCSprite boardFrame;
	CCLabel deadline;
	
	public static int mode = 0;
	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	int count = 0;
	
	//
	CCMenuItem broomBack;
	CCMenuItem presentBack;
	CCMenuItem broomFore;
	CCMenuItem presentFore;
	CCMenuItem hidebutton;
	CCMenu broomMenu;
	CCMenu presentMenu;

	int currentTime = 0;
	long broomstickTime1 = 10*1000;
	
	static int friendsSize;
	boolean scrollLock = false;

	CCSprite sprite1;
	CCSprite sprite2;
	
	CCSprite board;
	CCSprite broomstickBackground1;
	CCSprite broomstickBackground2;
	CCSprite presentBackground1;
	CCSprite presentBackground2;
	
	CCMenuItem giftBoxPopup;
	CCMenuItem broomstickPopup;
	
	CCMenuItemToggle boxToggle;
	CCMenuItemToggle broomToggle;
	
	static CCLayer layer2;
	static CCLayer layer3;
    private static CCScene scene;
    
	private Context mContext;
	UserData userData;
//	TopMenu1 topMenu1 = new TopMenu1();
	
	public static CCScene scene() {
//		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
				scene = CCScene.node();
				CCLayer layer = new Home2();
				scene.addChild(layer, 2, 2);
				setScrollView(scene);
//			}
//		});
        return scene;
	}
	
	static float scrollTopBoundery; 
	static float scrollBottomBoundery; 
	
	private static void setScrollView(CCScene scene) {
		scene.addChild(HomeScroll.getInstance().getLayer(), 1, 1);
		CGPoint pb = bb.convertToWorldSpace(profileBg.getPosition().x,profileBg.getPosition().y);
		CGPoint bf = bg.convertToWorldSpace(boardFrame.getPosition().x,boardFrame.getPosition().y);
		scene.getChildByTag(1).setPosition(
        		scene.getContentSize().width / 2 - scene.getChildByTag(1).getContentSize().width / 2,
        		pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5);
		
		scrollTopBoundery = pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5; 
		scrollBottomBoundery = bf.y - (boardFrame.getAnchorPoint().y * boardFrame.getContentSize().height) + 180; 
	}
	
	public void wwww(){
		layer3.setVisible(true);
	}


	CCSprite[] aa;
	
	public Home2() {
		Log.e("Home", "Home");
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		userData.facebookUserInfo = FacebookData.getinstance().getUserInfo();
		userData.facebookFriendsInfo = FacebookData.getinstance().getFriendsInfo();
		
		friendsSize = userData.facebookFriendsInfo.size();

		if (friendsSize < 4) {
			friendsSize = 4;
			scrollLock = true;
		} 
				
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg2" + fileExtension);

		setBackBoardMenu(folder + "home-backboard-hd2" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
//		// 하단 메뉴
//		BottomMenu1.setBottomMenu(boardFrame, folder, this);
//		
//		// 상단 메뉴
//		aa = topMenu1.setTopMenu(boardFrame, folder, this);
		
		
		// 일1, 월2, 화3, 수4, 목5, 금6, 토7
		// 목요일 자정으로 셋팅
		calendar2 = nextDayOfWeek(5);
		schedule("broomstickMaker", 0.99f);
		
		for (String str : DataFilter.userDBFilter(getUserDBData())) {
			Log.e("Home", "getUserDBData [" + str + "]");
		}
		
		String[] idLvScore = {"ID : ","Lv ","Score "};
//		for (String[] str : DataFilter.scoreFilter(getFriendsScore())) {
//			int count =0;
//			for (String string : str) {
//				Log.e("Home", idLvScore[count] + string);	
//				count ++;
//			}
//		}
		
		Log.e("Home", "setUserScore : " + DataFilter.userScoreFilter(setUserScore(123)));
		
		this.setIsTouchEnabled(true);
	}
	
	private String getFriendsScore() {
		String id = "(" + FacebookData.getinstance().getUserInfo().getId();		
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
		for (Profile profile : friends) {
			id += "," + profile.getId();
		}
		id += ")";
		try {
			return new DataController().execute(
					"0,RequestModeGetInDBUserList" +
					"*23," + id).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getUserDBData() {
		try {
			return new DataController().execute(
					"0,RequestModeRead" +
					"*1," + FacebookData.getinstance().getUserInfo().getId()).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String setUserScore(int addScore) {
		try {
			return new DataController().execute(
					"0,RequestModeAddScore" +
					"*1," + FacebookData.getinstance().getUserInfo().getId() +
					"*18," + addScore).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

//	private String setMailData(String recipientID, String Item, String quantity) {
//		try {
//			return new DataController().execute(
//					"0,RequestModeMailBoxAdd" +
//					"*1," + recipientID + 
//					"*19," + FacebookData.getinstance().getUserInfo().getId() + 
//					"*20," + Item + 
//					"*21," + quantity).get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private String getMailData() {
//		try {
//			return new DataController().execute(
//					"0,RequestModeMailBoxRead" +
//					"*1," + FacebookData.getinstance().getUserInfo().getId()).get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private String deleteMailData(String requestID) {
//		try {
//			return new DataController().execute(
//					"0,RequestModeMailBoxDelete" +
//					"*1," + FacebookData.getinstance().getUserInfo().getId() +
//					"*22," + requestID).get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	
	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		userInfo(bb);
		
//		CCLabel aaa = CCLabel.makeLabel("ba1ba1 : 0 ,0", "Arial", 30);
//		bb.addChild(aaa);
//		aaa.setAnchorPoint(0, 0);
//		aaa.setPosition(0,0);
//		
//		
//		CCLabel bbb = CCLabel.makeLabel("ba2ba2:" + (int)bb.getPosition().x+","+(int)bb.getPosition().y, "Arial", 30);
//		this.addChild(bbb);
//		bbb.setAnchorPoint(0, 0);
//		bbb.setPosition(bg.convertToWorldSpace(bb.getPosition().x,bb.getPosition().y));
//		
//		CCLabel ccc = CCLabel.makeLabel("0,0", "Arial", 50);
//		this.addChild(ccc);
//		ccc.setAnchorPoint(0, 0);
//		ccc.setPosition(0,0);
		
	}
	
	long deadLineTime = 0;
	// 게시판 설정
	private void setBoardFrameMenu(String imageFullPath) {
		boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		
//		deadline = FrameTitle5.setTitle(boardFrame, folder);
		deadLineTime = getInitTime();
//		bsp = CCLabel.makeLabel("layer position",  "Arial", 30.0f);
//        boardFrame.addChild(bsp);
//        bsp.setColor(ccColor3B.ccBLACK);
//		bsp.setPosition(boardFrame.getContentSize().width/2, boardFrame.getContentSize().height + 30);
		
		
//		CCLabel aaa = CCLabel.makeLabel("fr1fr1:" + "0, 0", "Arial", 30);
//		boardFrame.addChild(aaa);
//		aaa.setAnchorPoint(0, 0);
//		aaa.setPosition(0,0);
//		
//		CCLabel bbb = CCLabel.makeLabel("fr2fr2:" + (int)boardFrame.getPosition().x+","+(int)boardFrame.getPosition().y, "Arial", 30);
//		this.addChild(bbb);
//		bbb.setAnchorPoint(1, 1);
//		bbb.setPosition(bg.convertToWorldSpace(boardFrame.getPosition().x,boardFrame.getPosition().y));
	}
	
	private Bitmap image;
	
//	public Photo(Parcel in){
//		title=in.readString();
//		image=(Bitmap)in.readParcelable(Bitmap.class.getClassLoader());
//		imageStr=in.readString();
//	}
	
	// 사용자 이미지 URL
	String imageUrl = ""; 

	
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
	
	static CCSprite profileBg;
	private void userInfo(CCSprite baseSprite) {
		profileBg = CCSprite.sprite(folder + "home-profileBg-hd" + fileExtension); // 프로필 백그
		baseSprite.addChild(profileBg);
		profileBg.setPosition(
				baseSprite.getContentSize().width / 2, 
				baseSprite.getContentSize().height - profileBg.getContentSize().height / 2 - 40.0f);
		
//		Log.e("Home", "userInfo : " + imageUrl);
		
		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd" + fileExtension); // 프로필 사진 틀
		profilePicture.setPosition(
				profilePicture.getContentSize().width / 2 + 85, 
				profileBg.getContentSize().height-60.0f);
		
		CCMenuItem post = CCMenuItemImage.item(  // 우편
				folder + "home-mail-hd" + fileExtension, 
				folder + "home-mail-hd" + fileExtension, 				 
				this,"mailCallback");
		post.setAnchorPoint(1.0f, 0.5f);
		
		//
//		Bitmap userPhoto = getBitmapFromURL("https://graph.facebook.com/" + userData.facebookUserInfo.getUsername() +"/picture");
		CCSprite userImage;
//		if (userPhoto == null) {
			userImage= CCSprite.sprite(commonfolder + "noPicture" + fileExtension); // 프로필 사진
//		} else {
//			userImage= CCSprite.sprite(userPhoto); // 프로필 사진
//		}
//		userImage = CCSprite.sprite(FacebookData.getinstance().getUserPhoto()); // 프로필 사진
		
		
//		if (userData.facebookUserInfo != null) {
//			imageUrl = "https://graph.facebook.com/" + userData.facebookUserInfo.getUsername() +"/picture";
//			
//			CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // 프로필 사진
			profilePicture.addChild(userImage);
			userImage.setAnchorPoint(0.5f, 0.5f);
			userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
//		}
		
		CCSprite newIcon = CCSprite.sprite(folder + "home-mailNew-hd01" + fileExtension); // 새로운 우편
		newIcon.setPosition(
				post.getContentSize().width/2, 
				post.getContentSize().height);
		post.addChild(newIcon);

		CCMenu postMenu = CCMenu.menu(post);

		post.setPosition(0f,0f);
		postMenu.setContentSize(0f, 0f);
		postMenu.setPosition(
			profileBg.getContentSize().width - post.getContentSize().width/2, 
			profileBg.getContentSize().height/2);
		profileBg.addChild(postMenu);
		
		CCSprite levelProgressBar = CCSprite.sprite(folder + "home-progressBar-hd" + fileExtension); // 레벨 프로그레스 바
		levelProgressBar.setPosition(
				profileBg.getContentSize().width/2+10f,
				levelProgressBar.getContentSize().height/2+25.0f);
		
		CCLabel  ranking = CCLabel.makeLabel("7", "Arial", 30.0f); // 순위
		ranking.setPosition(
				ranking.getContentSize().width/2+55.0f, 
				profileBg.getContentSize().height-60.0f);
		
		CCLabel  userName;
		if (userData.facebookUserInfo != null) {
			userName = CCLabel.makeLabel(userData.facebookUserInfo.getName(), "Arial", 30); // 이름
		} else {
			userName = CCLabel.makeLabel(userData.userName, "Arial", 30); // 이름
		}
		userName.setPosition(
				profilePicture.getPosition().x + ((1 - profilePicture.getAnchorPoint().x) * profilePicture.getContentSize().width) + 12, 
				profileBg.getContentSize().height - userName.getContentSize().height / 2 - 20);
		userName.setAnchorPoint(0.0f, 0.5f);

		CCLabel  score = CCLabel.makeLabel(
				new NumberComma().numberComma(userData.getPoint()),
				"Arial", 30); // 최고 점수
		score.setPosition(
//				levelProgressBar.getPosition().x + ((1 - levelProgressBar.getAnchorPoint().x) * levelProgressBar.getContentSize().width),
//				profileBg.getContentSize().height- score.getContentSize().height / 2 - 20.0f);
//				score.setAnchorPoint(1.0f, 0.5f);
				userName.getPosition().x, 
				profileBg.getContentSize().height- score.getContentSize().height / 2 - 60.0f);
				score.setAnchorPoint(0.0f, 0.5f);

//		CCLabel  record = CCLabel.makeLabel("역대 전적", "Arial", 20.0f); // 전적
//		record.setPosition(
//				userName.getPosition().x, 
//				profileBg.getContentSize().height- record.getContentSize().height/2-60.0f);
//		record.setAnchorPoint(0.0f, 0.5f);
		
				String Ko = "전,승,패";
				String Ch = "戰,勝,敗";
				String En = "Played,Won,Lost";
				
		CCLabel  recordData = CCLabel.makeLabel(
				"Played " + (userData.getHistory(0) +  userData.getHistory(1)) +
				" : W" + userData.getHistory(1) + 
				"/L" + userData.getHistory(0)
				,"Arial", 20.0f); // 전적 값
		recordData.setPosition(
				userName.getPosition().x,
//				profileBg.getContentSize().height - recordData.getContentSize().height / 2 - 85
				profilePicture.getPosition().y - (profilePicture.getAnchorPoint().y * profilePicture.getContentSize().height)
				);
		recordData.setAnchorPoint(0, 1);
		
//		CCLabel  winningRate = CCLabel.makeLabel("승 률", "Arial", 20.0f); // 승률
//		winningRate.setPosition(
//				winningRate.getContentSize().width/2+330.0f, 
//				profileBg.getContentSize().height- winningRate.getContentSize().height/2-60.0f);
//		score.setAnchorPoint(1.0f, 0.5f);
		
//		CCLabel  winningRateData = CCLabel.makeLabel((int)(100 * userData.getHistory(1) / (userData.getHistory(1) + userData.getHistory(0)))+"%", "Arial", 20.0f); // 승률 값
//		winningRateData.setPosition(
//				winningRateData.getContentSize().width/2+330.0f, 
//				profileBg.getContentSize().height- winningRateData.getContentSize().height/2-85.0f);
//		score.setAnchorPoint(1.0f, 0.5f);
		
		CCLabel  level = CCLabel.makeLabel("Lv"+""+" 1", "Arial", 40.0f); // 레벨
		level.setPosition(
				level.getContentSize().width/2+115.0f, 
				profileBg.getContentSize().height- level.getContentSize().height/2-110.0f);
		level.setAnchorPoint(1.0f, 0.5f);
		level.setColor(ccColor3B.ccYELLOW);
		
		profileBg.addChild(profilePicture);
		profileBg.addChild(levelProgressBar);
		profileBg.addChild(ranking);
		profileBg.addChild(userName);
		profileBg.addChild(score);
//		profileBg.addChild(record);
		profileBg.addChild(recordData);
//		profileBg.addChild(winningRate);
//		profileBg.addChild(winningRateData);
		profileBg.addChild(level);

//		CCLabel aaa = CCLabel.makeLabel("pr1pr1: 0, 0", "Arial", 30);
//		profileBg.addChild(aaa);
//		aaa.setAnchorPoint(0, 0);
//		aaa.setPosition(0,0);
//		
//		CCLabel bbb = CCLabel.makeLabel("pr2pr2:" + (int)profileBg.getPosition().x+","+(int)profileBg.getPosition().y, "Arial", 30);
//		this.addChild(bbb);
//		bbb.setAnchorPoint(0, 0);
//		bbb.setPosition(baseSprite.convertToWorldSpace(profileBg.getPosition().x,profileBg.getPosition().y));
		
		Log.e("home", "userinfo");
	}
	
	
	// 입력받은 요일의 현재 서버시간의 다음주 요일을 뽑아내는 함수
	public static long nextDayOfWeek(int selectWeek) {
//	public static Calendar nextDayOfWeek(int selectWeek) {
		Calendar _calendar = Calendar.getInstance();
		
//		int difference = selectWeek - _calendar.get(Calendar.DAY_OF_WEEK); // 완성된 시계
//		if (!(difference > 0)) { 
//		   difference += 7; 
//		  }
//		_calendar.add(Calendar.DAY_OF_MONTH, difference);
//		_calendar.set(Calendar.HOUR, 0);
//		_calendar.set(Calendar.MINUTE, 0);
//		_calendar.set(Calendar.SECOND, 0);
		
		_calendar.add(Calendar.MINUTE, 1);
		_calendar.set(Calendar.SECOND, 0);

//		int year = _calendar.get(Calendar.YEAR);
//		int month = _calendar.get(Calendar.MONTH) + 1;
//		int date = _calendar.get(Calendar.DATE);
//		int week = _calendar.get(Calendar.WEEK_OF_YEAR);
//		int hour = _calendar.get(Calendar.HOUR);
//		int min = _calendar.get(Calendar.MINUTE);
//		int sec = _calendar.get(Calendar.SECOND);
//		Log.e("nextDayOfWeek", "before : " + year + "년 " + month + "월 " + date + "일 " + week + "주차 " + hour + "시 " + min + "분 " + sec + "초");
		
		return _calendar.getTimeInMillis();
	}


	private long getInitTime() {
			try {
				return Calendar.getInstance().getTimeInMillis() + (1000 * Long.parseLong(
								new DataController().execute("0,RequestModeGetWeeklyLeftTime").get()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return -10;
	}
	
	private void setInitTime() {
		Calendar calendar1 = Calendar.getInstance();
		long aa = (deadLineTime - calendar1.getTimeInMillis()) / 1000;
		if (deadLineTime - calendar1.getTimeInMillis() < 990) {
			deadLineTime = getInitTime();
			aa = (deadLineTime - calendar1.getTimeInMillis()) / 1000;
		}

		long day3 = aa / (60 * 60 * 24);
		long hour3 = (aa % (60 * 60 * 24)) / (60 * 60);
		long min3 = (aa % (60 * 60)) / 60;
		long sec3 = aa % 60;

		String deadlineText = "";

		if (day3 > 0) {
			deadlineText = day3 + "일 ";
		}

		if (hour3 > 0) {
			deadlineText += hour3 + "시 ";
		}

		if (min3 > 0) {
			deadlineText += min3 + "분 ";
		}

		deadlineText += sec3 + "초 후 마감";
		deadline.setString(deadlineText);
	}
	
	Long calendar2;
	
	private String setDayWeek(int day_week) {
		String result = null;
		
		switch (day_week) {
		case Calendar.SUNDAY:
			result = "일요일";
			break;
		case Calendar.MONDAY:
			result = "월요일";
			break;
		case Calendar.TUESDAY:
			result = "화요일";
			break;
		case Calendar.WEDNESDAY:
			result = "수요일";
			break;
		case Calendar.THURSDAY:
			result = "목요일";
			break;
		case Calendar.FRIDAY:
			result = "금요일";
			break;
		case Calendar.SATURDAY:
			result = "토요일";
			break;
		}
		return result;
	}

	private void panForTranslation(CGPoint translation) {
		if (!scrollLock) {
			scene.getChildByTag(1).setPosition(
					scene.getChildByTag(1).getPosition().x,
					CGPoint.clampf(CGPoint.ccpAdd(
							scene.getChildByTag(1).getPosition(), translation).y, scrollTopBoundery, scrollBottomBoundery));
		}
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		return true;
	}

	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint touchLocation = this.convertTouchToNodeSpace(event);
		touchLocation = this.convertToNodeSpace(touchLocation);
		CGPoint translation = CGPoint.make(0, 0);
		if (event.getHistorySize() > 0) {
			CGPoint oldTouchLocation = CGPoint.make(
					event.getHistoricalX(0, 0),
					event.getHistoricalY(0, 0));
			oldTouchLocation = CCDirector.sharedDirector().convertToGL(oldTouchLocation);
			oldTouchLocation = this.convertToNodeSpace(oldTouchLocation);
			translation = CGPoint.ccpSub(touchLocation, oldTouchLocation);
		}
		this.panForTranslation(translation);
		return super.ccTouchesMoved(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}

	
	//
	// Callback Method
	public void mailCallback(Object sender) {
		if (buttonActive) {
//			MailBox mail =	new MailBox(this, "11mailbox/", this);
			MailBox mail =	new MailBox(null, "11mailbox/", this);
			 broomstickBackground1 = mail.getBroomstickBackground1();
			 broomstickBackground2 = mail.getBroomstickBackground2();
			 presentBackground1 = mail.getPresentBackground1();
			 presentBackground2 = mail.getPresentBackground2();
		}
	}
	
	public void pesterCallback(Object sender) {
		broomstickBackground1.setVisible(true);
		broomstickBackground2.setVisible(false);
		presentBackground1.setVisible(false);
		presentBackground2.setVisible(true);
	}

	public void presentCallback(Object sender) {
		broomstickBackground1.setVisible(false);
		broomstickBackground2.setVisible(true);
		presentBackground1.setVisible(true);
		presentBackground2.setVisible(false);
	}
	
	public void mailboxCloseCallback(Object sender) {
		buttonActive = true;
		this.removeChildByTag(999, true);
	}
    
	public void goldCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = ShopGold2.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}
	
	public void broomstickCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = ShopBroomstick2.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

	public void shopCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = Shop.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}
	
	public void enterCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = GameMode.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}
	
	public void optionCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = Option.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}
	
		public void inviteCallback(Object sender) {
		if (buttonActive) {
			CCScene scene = Invite.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

		public void broomstickMaker(float dt) {
			// new timer
	//		calendar2 = Calendar.getInstance().getTimeInMillis() + 900000;
			setInitTime();
			
			Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
			int a = userData.getBroomstick();
			int b = userData.getTimeMaxBroomstick();
			if (a < b) {
				long bb = userData.getBroomstickRenewalTimeData();
				 
				if (userData.getBroomstickRenewalTimeData() == 1999999999999L){
					userData.setBroomstickRenewalTimeData(System.currentTimeMillis());
				}
					currentTime = (int)((userData.getBroomstickRenewalTimeData() + userData.broomstickRenewalTime -  System.currentTimeMillis()) / 1000);
				if(("" + currentTime%60).length() == 2)
					((CCLabel) aa[1]).setString((currentTime / 60) + ":" + (currentTime%60));
				else
					((CCLabel) aa[1]).setString((currentTime / 60) + ":0" + (currentTime%60));
				
				((CCLabel) aa[0]).setString("+" + userData.getBroomstick());
				
				if ((userData.getBroomstickRenewalTimeData() + userData.broomstickRenewalTime - 1200) <= System.currentTimeMillis()) {
					CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
						public void run() {
							userData.addBroomstickRenewalTimeData();
						}
					});
				}
			} else {
				if (userData.getBroomstickRenewalTimeData() != 1999999999999L) {
					userData.setBroomstickRenewalTimeData(1999999999999L);
				}
				((CCLabel) aa[0]).setString("+" + userData.getBroomstick());
				((CCLabel) aa[1]).setString(" ");
				
			}
		}

}