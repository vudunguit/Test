package com.aga.mine.mains;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.Utility;
import com.aga.mine.pages.UserData;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.entities.Profile;


public class InviteScroll extends CCColorLayer{
	
	final String commonfolder = "00common/";
	final String invitefolder = "30invite/";	
	
	CCMenuItem inviteButton;
	CCSprite inviteButtonOff;
	Context mContext;
	UserData userData;
	
	private static InviteScroll inviteScroll;
	
	public static InviteScroll getInstance() {
		if (inviteScroll == null) {
			inviteScroll = new InviteScroll(ccColor4B.ccc4(96, 66, 20, 255));
//			inviteScroll = new InviteScroll(ccColor4B.ccc4(244, 122, 122, 255));
		}
		return inviteScroll;
	}
	
	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	String friendBackBoardString = invitefolder + "invite-listPanel.png";
	final int width = 470; // ����Ʈ ���� �� 460
	final int height = (int) CCSprite.sprite(friendBackBoardString).getContentSize().height +3; // ����Ʈ ���� 78
	int max_items = 4;
	final int base_tag = 100;
	int seperatorWeight = 3;
	
	protected InviteScroll(ccColor4B color) {
		super(color);
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
//		List<GraphUser> friends = FacebookData.getinstance().getFriendsInfo();
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
		if (friends.size() > 3) {
			max_items = friends.size();	
		}
//		max_items = friends.size();
		//
		CGRect rect = CGRect.make(0, -height, width, height * (max_items - 1) + 10);
		CGSize contentSize = CGSize.make(rect.size.width - rect.origin.x, rect.size.height - rect.origin.y);
		this.setContentSize(contentSize);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*******************************************************/
		/** �������� 
		 * 
		 * ��ũ�Ѻ� scroll view
		 * 
		 * ���� ��ġ�� facebook ģ���� �� �ʴ�ź� ������ ����Ʈ���� ����
		 * �̸��� ����
		 * 
 		 *  **/
		int count = 0;
		for (Profile friend : friends) {
			// ģ�� ����Ʈ
			CCSprite inviteBackBoard = CCSprite.sprite(friendBackBoardString);
			this.addChild(inviteBackBoard);
			inviteBackBoard.setPosition(
					this.getContentSize().width / 2, 
					this.getContentSize().height - 5 - (count * (inviteBackBoard.getContentSize().height + 3)));
			inviteBackBoard.setAnchorPoint(0.5f, 1);
			setFriendInvitionMenu(inviteBackBoard, friend.getName(), friend.getId(), count + 1);
			count ++;
			if (count > 9)  // �ӽ÷� ���� 10������� �ް� �̻�� ģ���е� ���Ƽ� �����ɸ�.
				break;
		}
		/*******************************************************/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	//ģ�� �ʴ� �޴�
	// facebookģ���� network�� ���� ���� ��
	private void setFriendInvitionMenu(CCSprite parentSprite, String friendName, String friendID, int ranking){
		
		if (friendName.length() > 11) {
			friendName = friendName.substring(0, 10) + "..";
		}
		
		// �̹���Ʋ
		CCSprite friendImage = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png");
		parentSprite.addChild(friendImage);
		friendImage.setScale(0.8f);
		friendImage.setPosition(50.0f, parentSprite.getContentSize().height / 2);

		
		// ����� �̹��� URL
//		String imageUrl = "https://graph.facebook.com/" + friendID + "/picture"; 
////		Log.e("Home", "facebookFriendsInfo : " + imageUrl);
//		CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // ������ ����
//		
		
		Bitmap userPhoto = getBitmapFromURL("https://graph.facebook.com/" + friendID +"/picture");  // �̻�� ģ���� �ӽ� �ּ�
		Log.e("InviteScroll","userPhoto_Bytes : " + userPhoto.getRowBytes());  // �̻�� ģ���� �ӽ� �ּ�
		//
		CCSprite userImage;
		if (userPhoto.getRowBytes() < 100) {  // �̻�� ģ���� �ӽ� �ּ�
//		if (userPhoto == null) {
			userImage= CCSprite.sprite(commonfolder + "noPicture.png"); // ������ ����
		} else {  // �̻�� ģ���� �ӽ� �ּ�
			userImage= CCSprite.sprite(userPhoto); // ������ ����  // �̻�� ģ���� �ӽ� �ּ�
		}  // �̻�� ģ���� �ӽ� �ּ�
		
		friendImage.addChild(userImage);
		userImage.setAnchorPoint(0.5f, 0.5f);
		userImage.setPosition(friendImage.getContentSize().width / 2, friendImage.getContentSize().height / 2);
		userImage.setScale(1 / 0.8f);
		
		CCLabel friendNameLabel = CCLabel.makeLabel(friendName, "Arial", 30.0f);
		parentSprite.addChild(friendNameLabel);
		friendNameLabel.setAnchorPoint(0,  0.5f);
		friendNameLabel.setPosition(
				friendImage.getPosition().x + ((1 - friendImage.getAnchorPoint().x) * friendImage.getContentSize().width) + 10, 
				parentSprite.getContentSize().height / 2);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*******************************************************/
		/** �������� 
		 * 
		 * scroll view
		 * 
		 * ��ư�� �������� ��ũ�� ��ü ��Ȱ�� �Ǵ� ��ư�鸸 ��Ȱ��ȭ
		 * (��������� �ѹ��� �ʴ��ϴ°��� ��������)
		 * message�� ������ �ٽ� Ȱ��ȭ(�ʴ뿡 �źν� �ٽ� �ʴ��ϱ� ����)
		 * 
 		 *  **/
		// �ʴ� ��ư
		inviteButton = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button1.png"),
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button2.png"),
				this, "matchCallback");
		inviteButton.setUserData(friendID);
		inviteButton.setScale(0.9f);
//		inviteButton.setIsEnabled(false);
		/*******************************************************/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		CCMenu inviteMenu = CCMenu.menu(inviteButton);
		inviteMenu.alignItemsVertically(0);
		parentSprite.addChild(inviteMenu);
		inviteMenu.setPosition(
				parentSprite.getContentSize().width - inviteButton.getContentSize().width / 2 - 5, 
				parentSprite.getContentSize().height / 2);
		
		inviteButtonOff = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(invitefolder + "invite-button2.png"));
		inviteButtonOff.setVisible(false);
		int idTag = (int)Long.parseLong(friendID);
		parentSprite.addChild(inviteButtonOff, 1, idTag);
		inviteButtonOff.setPosition(inviteMenu.getPosition());
	}
	
	
//	
//	private void ranker(CCSprite parentSprite, String friendName, String friendID, int ranking) {
//		String medalColor = "home-circleNull";
//		ccColor3B medalNumberColor = ccColor3B.ccWHITE;
//		
//		
//		
//		switch (ranking) {
//		case 1:
//			medalColor = "home-circleGold-hd";
//			medalNumberColor = ccColor3B.ccBLACK;
//			break;
//		
//		case 2:
//			medalColor = "home-circleSilver-hd";
//			medalNumberColor = ccColor3B.ccBLACK;
//			break;
//			
//		case 3:
//			medalColor = "home-circleBronze-hd";
//			break;
//			
//		default:
//			
//			break;
//		}
//		
//		Log.e("inviteScroll", "friendName - before : " + friendName);
//		if (friendName.length() > 5) {
//			friendName = friendName.substring(0, 4) + "..";
//		}
//		Log.e("inviteScroll", "friendName - after : " + friendName);
//		
//		CCSprite rankingImg = CCSprite.sprite(folder + medalColor + .png"); // �޴� �׸�
//		parentSprite.addChild(rankingImg);
//		rankingImg.setAnchorPoint(0.5f, 0.5f);
//		rankingImg.setPosition(
//				10 + rankingImg.getContentSize().width / 2, 
//				(parentSprite.getContentSize().height - seperatorWeight) / 2.0f + seperatorWeight);
//		
//		CCLabel  rankingNumber = CCLabel.makeLabel("" + ranking, "Arial", 25.0f); // ���� ��ȣ
//		rankingImg.addChild(rankingNumber);
//		rankingNumber.setColor(medalNumberColor);
//		rankingNumber.setPosition(
//				rankingImg.getContentSize().width / 2,rankingImg.getContentSize().height / 2);
//		
//		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png"); // ��Ŀ ������ ����
//		parentSprite.addChild(profilePicture);
//		profilePicture.setPosition(
//				rankingImg.getPosition().x + rankingImg.getContentSize().width * rankingImg.getAnchorPoint().x + 10 +
//				profilePicture.getContentSize().width * profilePicture.getAnchorPoint().x,
//				rankingImg.getPosition().y);
//		
//		// ����� �̹��� URL
//		String imageUrl = "https://graph.facebook.com/" + friendID + "/picture"; 
//		Log.e("Home", "facebookFriendsInfo : " + imageUrl);
//		CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // ������ ����
//		profilePicture.addChild(userImage);
//		userImage.setAnchorPoint(0.5f, 0.5f);
//		userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
//		
//		// facebook friend���� �޾ƿð�
//		CCLabel  rankersName = CCLabel.makeLabel(friendName + " ", "Arial", 30.0f); // ��Ŀ �̸�
//		parentSprite.addChild(rankersName);
//		rankersName.setAnchorPoint(0, 0.5f);
//		rankersName.setPosition(
//				profilePicture.getPosition().x + profilePicture.getContentSize().width * profilePicture.getAnchorPoint().x + 10,
//				parentSprite.getContentSize().height - rankersName.getContentSize().height / 2 -
//				(parentSprite.getContentSize().height - rankersName.getContentSize().height * 2 - seperatorWeight) / 3
//				);
//		
//		// db���� �޾ƿ� ��
//		CCLabel  rankersScore = CCLabel.makeLabel("760,000", "Arial", 30.0f); // ��Ŀ ����
//		parentSprite.addChild(rankersScore);
//		rankersScore.setAnchorPoint(0, 0.5f);
//		rankersScore.setPosition(
//				rankersName.getPosition().x,
//				parentSprite.getContentSize().height - rankersName.getPosition().y + seperatorWeight);
//		
//	}
//	
//	private void friendGiftButton(CCSprite parentSprite) {
//
//		CCMenuItem giftBoxPopup = CCMenuItemImage.item(  // ���� ���� �˾�
//				folder + "home-giftboxOn-hd.png",  
//				folder + "home-giftboxOff-hd.png",
//				this,"giftBoxCallback");
//	    
//		CCMenuItem broomstickPopup = CCMenuItemImage.item(  // ���ڷ� �˾�
//				folder + 	"home-broomStickBigOn-hd.png",
//				folder + 	"home-broomstickBigOff-hd.png", 				 
//				this,"broomStickGiftCallback");
//		
//		CCMenu giftMenu = CCMenu.menu(giftBoxPopup, broomstickPopup);
//		parentSprite.addChild(giftMenu);
//		giftMenu.setAnchorPoint(1, 0.5f);
//
//		giftMenu.setPosition(
//				parentSprite.getContentSize().width - 10, 
//				(parentSprite.getContentSize().height - seperatorWeight) / 2.0f + seperatorWeight);
//		
//		broomstickPopup.setAnchorPoint(1, 0.5f);
//		broomstickPopup.setPosition(0, 0);
//		
//		giftBoxPopup.setAnchorPoint(1, 0.5f);
//		giftBoxPopup.setPosition(
//				broomstickPopup.getPosition().x - 10 - (broomstickPopup.getContentSize().width * broomstickPopup.getAnchorPoint().x),
//				broomstickPopup.getPosition().y);	
//	}
//	
//	
//	// �� ���� ����
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
//		CCSprite profileBg = CCSprite.sprite(folder + "home-profileBg-hd.png"); // ������ ���
//		baseSprite.addChild(profileBg);
//		profileBg.setPosition(
//				baseSprite.getContentSize().width / 2, 
//				baseSprite.getContentSize().height - profileBg.getContentSize().height / 2 - 40.0f);
//		
//		Log.e("Home", "userInfo : " + imageUrl);
//		
//		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd.png"); // ������ ���� Ʋ
//		profilePicture.setPosition(
//				profilePicture.getContentSize().width / 2 + 85, 
//				profileBg.getContentSize().height-60.0f);
//		
//		CCMenuItem post = CCMenuItemImage.item(  // ����
//				folder + "home-mail-hd.png", 
//				folder + "home-mail-hd.png", 				 
//				this,"postCallback");
//		post.setAnchorPoint(1.0f, 0.5f);
//		
//		CCSprite userImage= CCSprite.sprite(getBitmapFromURL(imageUrl)); // ������ ����
//		profilePicture.addChild(userImage);
//		userImage.setAnchorPoint(0.5f, 0.5f);
//		userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
//		
//		CCSprite newIcon = CCSprite.sprite(folder + "home-mailNew-hd01.png"); // ���ο� ����
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
//		CCSprite levelProgressBar = CCSprite.sprite(folder + "home-progressBar-hd.png"); // ���� ���α׷��� ��
//		levelProgressBar.setPosition(
//				profileBg.getContentSize().width/2+10f,
//				levelProgressBar.getContentSize().height/2+25.0f);
//
//		
//		CCLabel  ranking = CCLabel.makeLabel("7", "Arial", 30.0f); // ����
//		ranking.setPosition(
//				ranking.getContentSize().width/2+55.0f, 
//				profileBg.getContentSize().height-60.0f);
//		
//	
//
//		CCLabel  userName = CCLabel.makeLabel(userData.facebookUserInfo.getName(), "Arial", 30); // �̸�
//		userName.setPosition(
//				profilePicture.getPosition().x + ((1 - profilePicture.getAnchorPoint().x) * profilePicture.getContentSize().width) + 12, 
//				profileBg.getContentSize().height - userName.getContentSize().height / 2 - 20);
//		userName.setAnchorPoint(0.0f, 0.5f);
//
//		CCLabel  score = CCLabel.makeLabel(userData.getPoint() + " ", "Arial", 30); // �ְ� ����
//		score.setPosition(
////				levelProgressBar.getPosition().x + ((1 - levelProgressBar.getAnchorPoint().x) * levelProgressBar.getContentSize().width),
////				profileBg.getContentSize().height- score.getContentSize().height / 2 - 20.0f);
////				score.setAnchorPoint(1.0f, 0.5f);
//				userName.getPosition().x, 
//				profileBg.getContentSize().height- score.getContentSize().height / 2 - 60.0f);
//				score.setAnchorPoint(0.0f, 0.5f);
//
////		CCLabel  record = CCLabel.makeLabel("���� ����", "Arial", 20.0f); // ����
////		record.setPosition(
////				userName.getPosition().x, 
////				profileBg.getContentSize().height- record.getContentSize().height/2-60.0f);
////		record.setAnchorPoint(0.0f, 0.5f);
//		
//				String Ko = "��,��,��";
//				String Ch = "��,�,��";
//				String En = "Played,Won,Lost";
//				
//		CCLabel  recordData = CCLabel.makeLabel(
//				"Played " + (userData.getHistory(0) +  userData.getHistory(1)) +
//				" : W" + userData.getHistory(1) + 
//				"/L" + userData.getHistory(0)
//				,"Arial", 20.0f); // ���� ��
//		recordData.setPosition(
//				userName.getPosition().x,
////				profileBg.getContentSize().height - recordData.getContentSize().height / 2 - 85
//				profilePicture.getPosition().y - (profilePicture.getAnchorPoint().y * profilePicture.getContentSize().height)
//				);
//		recordData.setAnchorPoint(0, 1);
//		
////		CCLabel  winningRate = CCLabel.makeLabel("�� ��", "Arial", 20.0f); // �·�
////		winningRate.setPosition(
////				winningRate.getContentSize().width/2+330.0f, 
////				profileBg.getContentSize().height- winningRate.getContentSize().height/2-60.0f);
////		score.setAnchorPoint(1.0f, 0.5f);
//		
////		CCLabel  winningRateData = CCLabel.makeLabel((int)(100 * userData.getHistory(1) / (userData.getHistory(1) + userData.getHistory(0)))+"%", "Arial", 20.0f); // �·� ��
////		winningRateData.setPosition(
////				winningRateData.getContentSize().width/2+330.0f, 
////				profileBg.getContentSize().height- winningRateData.getContentSize().height/2-85.0f);
////		score.setAnchorPoint(1.0f, 0.5f);
//		
//		CCLabel  level = CCLabel.makeLabel("Lv"+""+" 1", "Arial", 40.0f); // ����
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
//	
//	
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
//
	public CCLayer getLayer(){
		return this;
	}
//
//	public int getMax_items() {
//		return max_items;
//	}
//
//	public void setMax_items(int max_items) {
//		this.max_items = max_items;
//	}

	
	
	public void matchCallback(Object sender) {
		CCNode a= (CCNode)sender;
		String facebookId = (String)a.getUserData();
//		a.getParent().addChild(timerBack);
//		a.removeFromParentAndCleanup(true);
//		userData.addBroomstick(1);
//		String facebookId = (String)inviteButton.getUserData();

//		a.setVisible(false);
//		int idTag = (int)(Long.parseLong(facebookId));
//		a.getParent().getChildByTag(idTag).setVisible(true);
		try {
		NetworkController.getInstance().sendRequestMatchInvite(
				userData.difficulty, facebookId);
	} catch (IOException e) {
		e.printStackTrace();
	}
		
		
//		a.getParent().addChild(timerBack);
//		a.removeFromParentAndCleanup(true);
//		userData.addBroomstick(1);
		//menu111.addChild(timerBack);
		
		//inviteButton.removeFromParentAndCleanup(true);
		//menu111.removeChild((CCNode) sender, true);
		
		Log.e("inviteScroll", "timerCallback : " + a.getUserData());
	}

	CCNode nodeThis = CCNode.node(); 
	public CCLayer getLayer(CCNode nodeThis){
		this.nodeThis = nodeThis;
		return this;
	}
	
}
