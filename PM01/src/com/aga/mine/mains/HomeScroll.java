package com.aga.mine.mains;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.sromku.simple.fb.entities.Profile;

public class HomeScroll extends CCColorLayer{
	
	
	final String commonfolder = "00common/";
	final String folder = "10home/";
	final String fileExtension = ".png";
	
	final int presentGoldButton = 1010;
	final int presentBroomstickButton = 1011;
	
	private static HomeScroll homeScroll;
	
	public static HomeScroll getInstance() {
//	public static HomeScroll getInstance(CCNode nodeThis) {
		if (homeScroll == null) {
			homeScroll = new HomeScroll(ccColor4B.ccc4(96, 66, 20, 255));
//			homeScroll = new HomeScroll(ccColor4B.ccc4(96, 66, 20, 255), nodeThis);
//			homeScroll = new HomeScroll(ccColor4B.ccc4(244, 122, 122, 255));
		}
		return homeScroll;
	}
	
	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	final int width = 470; // 원본 사이즈 460
	final int height = 94; // 원본사이즈 94
	int max_items = 4;
	final int base_tag = 100;
	int seperatorWeight = 3;
	Context mContext;
	
//	private HomeScroll(ccColor4B color, CCNode nodeThis) {
	private HomeScroll(ccColor4B color) {
		super(color);
//		Log.e("HomeScroll", "getFriendsInfo : " + FacebookData.getinstance().getFriendsInfo());
		mContext = CCDirector.sharedDirector().getActivity();
		
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
//		List<GraphUser> friends = UserData.share(mContext).facebookFriendsInfo;
//		Log.e("HomeScroll", "HomeScroll : " + friends.size());
//		createScroll(friends);
//		this.nodeThis = nodeThis;
		createScroll(DataFilter.getRanking());
	}
	
	// 기존에 사용하던 것
	private void createScroll(List<Profile> friends) {
		if (friends.size() > max_items) {
			max_items = friends.size();	
		}
		
		//
		CGRect rect = CGRect.make(0, -height, width, height * (max_items - 1) + 10);
		CGSize contentSize = CGSize.make(rect.size.width - rect.origin.x, rect.size.height - rect.origin.y);
		this.setContentSize(contentSize);
		
		int count = 0;
		for (Profile friend : friends) {
			CCSprite friendBackBoard = CCSprite.sprite(folder + "homefriendbb2.png");
			this.addChild(friendBackBoard);
			friendBackBoard.setPosition(
					this.getContentSize().width / 2, 
					this.getContentSize().height - 5 - (count * friendBackBoard.getContentSize().height));
			friendBackBoard.setAnchorPoint(0.5f, 1);
//			Log.e("HomeScroll", "getName : " + friend.getName() + ", getId :" + friend.getId());
			ranker(friendBackBoard, friend.getName(), friend.getId(), null , count +1);
			
			if (friend.getId().equals(FacebookData.getinstance().getUserInfo().getId())) {
				FacebookData.getinstance().ranking = count +1;
			} else {
				friendGiftButton(friendBackBoard, friend.getId());
			}
			count ++;
			if (count > 9) // 임시로 막음 10명까지만 받게 이사님 친구분들 많아서 오래걸림.
				break;
		}
	}
	
	// MainActivity의 nextCallback에서 사용
	private void createScroll(String[][] score2Array) {
		Log.e("HomeScroll", "createScroll - length : " + score2Array.length);
		if (score2Array.length > 3) {
			max_items = score2Array.length;	
		}
		
		//
		CGRect rect = CGRect.make(0, -height, width, height * (max_items - 1) + 10);
		CGSize contentSize = CGSize.make(rect.size.width - rect.origin.x, rect.size.height - rect.origin.y);
		this.setContentSize(contentSize);
		
		int count = 0;
		for (String[] friend : score2Array) {
			CCSprite friendBackBoard = CCSprite.sprite(folder + "homefriendbb2.png");
			this.addChild(friendBackBoard);
			friendBackBoard.setPosition(
					this.getContentSize().width / 2, 
					this.getContentSize().height - 5 - (count * friendBackBoard.getContentSize().height));
			friendBackBoard.setAnchorPoint(0.5f, 1);
//			Log.e("HomeScroll", "getName : " + friend.getName() + ", getId :" + friend.getId());
			ranker(friendBackBoard, friend[0], friend[0], friend[2],count +1);
			if (friend[0].equals(FacebookData.getinstance().getUserInfo().getId())) {
				Log.e("HomeScroll", "ranking : " + (count + 1));
				FacebookData.getinstance().ranking = count +1;
			} else {
				friendGiftButton(friendBackBoard, friend[0]);
			}
			count ++;
			if (count > 9) // 임시로 막음 10명까지만 받게 이사님 친구분들 많아서 오래걸림.
				break;
		}
	}
	
	private void ranker(CCSprite parentSprite, String friendName, String friendID, String score, int ranking) {
//	private void ranker(CCSprite parentSprite, String friendName, String friendID, int ranking) {
		String medalColor = "home-circleNull";
		ccColor3B medalNumberColor = ccColor3B.ccWHITE;
		
		switch (ranking) {
		case 1:
			medalColor = "home-circleGold-hd";
			medalNumberColor = ccColor3B.ccBLACK;
			break;
		
		case 2:
			medalColor = "home-circleSilver-hd";
			medalNumberColor = ccColor3B.ccBLACK;
			break;
			
		case 3:
			medalColor = "home-circleBronze-hd";
			break;
			
		default:
			
			break;
		}
		
		if (friendName.length() > 10) {
			friendName = friendName.substring(0, 9) + "..";
		}
		
		CCSprite rankingImg = CCSprite.sprite(folder + medalColor + fileExtension); // 메달 그림
		parentSprite.addChild(rankingImg);
		rankingImg.setAnchorPoint(0.5f, 0.5f);
		rankingImg.setPosition(
				10 + rankingImg.getContentSize().width / 2, 
				(parentSprite.getContentSize().height - seperatorWeight) / 2.0f + seperatorWeight);
		
		CCLabel  rankingNumber = CCLabel.makeLabel("" + ranking, "Arial", 25.0f); // 순위 번호
		rankingImg.addChild(rankingNumber);
		rankingNumber.setColor(medalNumberColor);
		rankingNumber.setPosition(
				rankingImg.getContentSize().width / 2,rankingImg.getContentSize().height / 2);
		
		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd" + fileExtension); // 랭커 프로필 사진
		parentSprite.addChild(profilePicture);
		profilePicture.setPosition(
				rankingImg.getPosition().x + rankingImg.getContentSize().width * rankingImg.getAnchorPoint().x + 10 +
				profilePicture.getContentSize().width * profilePicture.getAnchorPoint().x,
				rankingImg.getPosition().y);
		
		// 사용자 이미지 URL
//		String imageUrl = "https://graph.facebook.com/" + friendID + "/picture"; 
////		Log.e("HomeScroll", "facebookFriendsInfo : " + imageUrl);
//		CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // 프로필 사진
		
//    	Bitmap myBitmap = BitmapFactory.decodeStream(new URL(src).openStream());
    	
		String imageUrl = "https://graph.facebook.com/" + friendID +"/picture";
		Log.e("HomeScroll", "ranker imageUrl : " + imageUrl);
//		Bitmap userPhoto = getBitmapFromURL(imageUrl);  // 이사님 친구용 임시 주석
//		Log.e("HomeScroll","userPhoto_Bytes : " + userPhoto.getRowBytes()); // 이사님 친구용 임시 주석
		//
		CCSprite userImage = null;
//		userImage = CCSprite.sprite("00common/noPicture.png"); // 프로필 사진
//		if (userPhoto.getRowBytes() > 100) {
//			userImage= CCSprite.sprite(userPhoto); // 프로필 사진
//		}
		try {
			Bitmap downloadimage = new DownloadImageTask().execute(imageUrl).get();
//			if (downloadimage.getWidth() > 0 && downloadimage.getHeight() > 0) // 이미지 사이즈 체크 
//				userImage = CCSprite.sprite(downloadimage); // 다운로드 받은 이미지로 등록 (일단 주석 처리)
//			else
				userImage = CCSprite.sprite(commonfolder + "noPicture.png"); // 이미지 없을시 임시용 파일로 등록
			profilePicture.addChild(userImage);
			userImage.setAnchorPoint(0.5f, 0.5f);
			userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		
		// facebook friend에서 받아올것
		CCLabel  rankersName = CCLabel.makeLabel(friendName + " ", "Arial", 30); // 랭커 이름
		parentSprite.addChild(rankersName);
		rankersName.setAnchorPoint(0, 0.5f);
		rankersName.setPosition(
				profilePicture.getPosition().x + profilePicture.getContentSize().width * profilePicture.getAnchorPoint().x + 10,
				parentSprite.getContentSize().height - rankersName.getContentSize().height / 2 -
				(parentSprite.getContentSize().height - rankersName.getContentSize().height * 2 - seperatorWeight) / 3
				);
		
		if (score == null || score.equals("")) {
			score = "0";
		}
		// db에서 받아올 것
		CCLabel  rankersScore = CCLabel.makeLabel(new NumberComma().numberComma(score), "Arial", 30.0f); // 랭커 점수
		parentSprite.addChild(rankersScore);
		rankersScore.setAnchorPoint(0, 0.5f);
		rankersScore.setPosition(
				rankersName.getPosition().x,
				parentSprite.getContentSize().height - rankersName.getPosition().y + seperatorWeight);
		
	}
	
	private void friendGiftButton(CCSprite parentSprite, String friendID) {

		CCMenuItem boxCallback = CCMenuItemImage.item(  // 선물 상자 팝업
				folder + "home-giftboxOn-hd" + fileExtension,  
				folder + "home-giftboxOff-hd" + fileExtension,
				nodeThis,"clicked2");
//				this,"boxCallback");
		boxCallback.setTag(presentGoldButton);
		boxCallback.setUserData(friendID);
		
		CCMenuItem broomCallback = CCMenuItemImage.item(  // 빗자루 팝업
				folder + "home-broomstickBigOn-hd" + fileExtension,
				folder + "home-broomstickBigOff-hd" + fileExtension, 				 
				nodeThis,"clicked2");
//				this,"broomstickCallback");
		broomCallback.setTag(presentBroomstickButton);
		broomCallback.setUserData(friendID);
		
		CCMenu giftMenu = CCMenu.menu(boxCallback, broomCallback);
		parentSprite.addChild(giftMenu);
		giftMenu.setAnchorPoint(1, 0.5f);

		giftMenu.setPosition(
				parentSprite.getContentSize().width - 10, 
				(parentSprite.getContentSize().height - seperatorWeight) / 2.0f + seperatorWeight);
		
		broomCallback.setAnchorPoint(1, 0.5f);
		broomCallback.setPosition(0, 0);
		
		boxCallback.setAnchorPoint(1, 0.5f);
		boxCallback.setPosition(
				broomCallback.getPosition().x - 10 - (broomCallback.getContentSize().width * broomCallback.getAnchorPoint().x),
				broomCallback.getPosition().y);	
	}
//	
//	// 백 보드 설정
//	private void setBackBoardMenu(String imageFullPath) {
//		bb = CCSprite.sprite(imageFullPath);
//		bg.addChild(bb);
//		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
//		bb.setAnchorPoint(0.5f, 0.5f);
//		userInfo(bb);
////		friends(bb);
//	}
//	
//
//	private void userInfo(CCSprite baseSprite) {
//		CCSprite profileBg = CCSprite.sprite(folder + "home-profileBg-hd" + fileExtension); // 프로필 백그
//		baseSprite.addChild(profileBg);
//		profileBg.setPosition(
//				baseSprite.getContentSize().width / 2, 
//				baseSprite.getContentSize().height - profileBg.getContentSize().height / 2 - 40.0f);
//		
//		Log.e("Home", "userInfo : " + imageUrl);
//		
//		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd" + fileExtension); // 프로필 사진 틀
//		profilePicture.setPosition(
//				profilePicture.getContentSize().width / 2 + 85, 
//				profileBg.getContentSize().height-60.0f);
//		
//		CCMenuItem post = CCMenuItemImage.item(  // 우편
//				folder + "home-mail-hd" + fileExtension, 
//				folder + "home-mail-hd" + fileExtension, 				 
//				this,"postCallback");
//		post.setAnchorPoint(1.0f, 0.5f);
//		
//		CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // 프로필 사진
//		profilePicture.addChild(userImage);
//		userImage.setAnchorPoint(0.5f, 0.5f);
//		userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
//		
//		CCSprite newIcon = CCSprite.sprite(folder + "home-mailNew-hd01" + fileExtension); // 새로운 우편
//		newIcon.setPosition(
//				post.getContentSize().width/2, 
//				post.getContentSize().height);
//		post.addChild(newIcon);
//
//		CCMenu postMenu = CCMenu.menu(post);
//
//		post.setPosition(0f,0f);
//		postMenu.setContentSize(0f, 0f);
//		postMenu.setPosition(
//			profileBg.getContentSize().width - post.getContentSize().width/2, 
//			profileBg.getContentSize().height/2);
//		profileBg.addChild(postMenu);
//		
//		
//		
//		CCSprite levelProgressBar = CCSprite.sprite(folder + "home-progressBar-hd" + fileExtension); // 레벨 프로그레스 바
//		levelProgressBar.setPosition(
//				profileBg.getContentSize().width/2+10f,
//				levelProgressBar.getContentSize().height/2+25.0f);
//
//		
//		CCLabel  ranking = CCLabel.makeLabel("7", "Arial", 30.0f); // 순위
//		ranking.setPosition(
//				ranking.getContentSize().width/2+55.0f, 
//				profileBg.getContentSize().height-60.0f);
//		
//	
//
//		CCLabel  userName = CCLabel.makeLabel(userData.facebookUserInfo.getName(), "Arial", 30); // 이름
//		userName.setPosition(
//				profilePicture.getPosition().x + ((1 - profilePicture.getAnchorPoint().x) * profilePicture.getContentSize().width) + 12, 
//				profileBg.getContentSize().height - userName.getContentSize().height / 2 - 20);
//		userName.setAnchorPoint(0.0f, 0.5f);
//
//		CCLabel  score = CCLabel.makeLabel(userData.getPoint() + " ", "Arial", 30); // 최고 점수
//		score.setPosition(
////				levelProgressBar.getPosition().x + ((1 - levelProgressBar.getAnchorPoint().x) * levelProgressBar.getContentSize().width),
////				profileBg.getContentSize().height- score.getContentSize().height / 2 - 20.0f);
////				score.setAnchorPoint(1.0f, 0.5f);
//				userName.getPosition().x, 
//				profileBg.getContentSize().height- score.getContentSize().height / 2 - 60.0f);
//				score.setAnchorPoint(0.0f, 0.5f);
//
////		CCLabel  record = CCLabel.makeLabel("역대 전적", "Arial", 20.0f); // 전적
////		record.setPosition(
////				userName.getPosition().x, 
////				profileBg.getContentSize().height- record.getContentSize().height/2-60.0f);
////		record.setAnchorPoint(0.0f, 0.5f);
//		
//				String Ko = "전,승,패";
//				String Ch = "戰,勝,敗";
//				String En = "Played,Won,Lost";
//				
//		CCLabel  recordData = CCLabel.makeLabel(
//				"Played " + (userData.getHistory(0) +  userData.getHistory(1)) +
//				" : W" + userData.getHistory(1) + 
//				"/L" + userData.getHistory(0)
//				,"Arial", 20.0f); // 전적 값
//		recordData.setPosition(
//				userName.getPosition().x,
////				profileBg.getContentSize().height - recordData.getContentSize().height / 2 - 85
//				profilePicture.getPosition().y - (profilePicture.getAnchorPoint().y * profilePicture.getContentSize().height)
//				);
//		recordData.setAnchorPoint(0, 1);
//		
////		CCLabel  winningRate = CCLabel.makeLabel("승 률", "Arial", 20.0f); // 승률
////		winningRate.setPosition(
////				winningRate.getContentSize().width/2+330.0f, 
////				profileBg.getContentSize().height- winningRate.getContentSize().height/2-60.0f);
////		score.setAnchorPoint(1.0f, 0.5f);
//		
////		CCLabel  winningRateData = CCLabel.makeLabel((int)(100 * userData.getHistory(1) / (userData.getHistory(1) + userData.getHistory(0)))+"%", "Arial", 20.0f); // 승률 값
////		winningRateData.setPosition(
////				winningRateData.getContentSize().width/2+330.0f, 
////				profileBg.getContentSize().height- winningRateData.getContentSize().height/2-85.0f);
////		score.setAnchorPoint(1.0f, 0.5f);
//		
//		CCLabel  level = CCLabel.makeLabel("Lv"+""+" 1", "Arial", 40.0f); // 레벨
//		level.setPosition(
//				level.getContentSize().width/2+115.0f, 
//				profileBg.getContentSize().height- level.getContentSize().height/2-110.0f);
//		level.setAnchorPoint(1.0f, 0.5f);
//		level.setColor(ccColor3B.ccYELLOW);
//		
//		profileBg.addChild(profilePicture);
//		profileBg.addChild(levelProgressBar);
//		profileBg.addChild(ranking);
//		profileBg.addChild(userName);
//		profileBg.addChild(score);
////		profileBg.addChild(record);
//		profileBg.addChild(recordData);
////		profileBg.addChild(winningRate);
////		profileBg.addChild(winningRateData);
//		profileBg.addChild(level);
//
//	}
//	
	
	public static Bitmap getBitmapFromURL(String src) {
	    try {
//	        URL url = new URL(src);
//	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	        connection.setDoInput(true);
//	        connection.connect();
//	        InputStream input = connection.getInputStream();
//	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	    	
//	    	BitmapFactory.Options options = new BitmapFactory.Options();
//	    	options.inSampleSize = 2;
//	        Bitmap myBitmap = BitmapFactory.decodeStream(new URL(src).openStream()
//	        		,new Rect(50, 50, 50, 50) ,options);
//	        Bitmap resize =  Bitmap.createScaledBitmap(myBitmap, 60, 60, true) ;
//	        return resize;
	    	
	    	Bitmap myBitmap = BitmapFactory.decodeStream(new URL(src).openStream());
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
		}
	    return null;
	}
	
	public CCLayer setData(String[][] score2Array){
   		createScroll(score2Array);
		return this;
	}
	
//   	public CCLayer getLayer(String[][] score2Array){
//   		createScroll(score2Array);
//		return this;
//	}
   	
	CCNode nodeThis = CCNode.node(); 
	public CCLayer getLayer(CCNode nodeThis){
		this.nodeThis = nodeThis;
		return this;
	}
	
   	public CCLayer getLayer(){
   		return this;
   	}

	public int getMax_items() {
		return max_items;
	}

	public void setMax_items(int max_items) {
		this.max_items = max_items;
	}
	
	public void boxCallback(Object sender) {
		CCNode a= (CCNode)sender;
		Log.e("HomeScroll", "boxCallback : " + a.getUserData());
			String[] items = ((String) a.getUserData()).split(",");
			for (String item : items) {
				if (!item.equals("presentGold")) {
					FacebookData.getinstance().setRecipientID(item);
					CCScene scene = ShopGold2.scene();
					CCDirector.sharedDirector().replaceScene(scene);
			}
		}
	}

	public void broomstickCallback(Object sender) {
		CCNode a= (CCNode)sender;
		Log.e("HomeScroll", "broomstickCallback : " + a.getUserData());
			String[] items = ((String) a.getUserData()).split(",");
			for (String item : items) {
				if (!item.equals("presentBroomstick")) {
				String senderID = FacebookData.getinstance().getUserInfo().getId();
				String sendMailData = 
						"0,RequestModeMailBoxAdd*22," + FacebookData.getinstance().getRequestID() + 
						"*1," + item + "*19," + senderID + "*20,Broomstick*21," + 1;
				FacebookData.getinstance().sendMail(sendMailData);
			}
		}
	}
}
