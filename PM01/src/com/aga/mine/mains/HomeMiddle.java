package com.aga.mine.mains;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor3B;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.aga.mine.pages.UserData;

public class HomeMiddle {

	final String commonfolder = "00common/";
	final String fileExtension = ".png";
	final CCNode nodeThis;

	final int mailButton = 1007;
	final int mailcloseButton = 1008;
	
	Context mContext;
	UserData userData;
	
	public HomeMiddle(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		userInfo(parentSprite, imageFolder, nodeThis);
		this.nodeThis = nodeThis;
	}
	
	private void userInfo(CCSprite parentSprite, String imageFolder, CCNode nodeThis) {
		CCSprite profileBg = CCSprite.sprite(imageFolder + "home-profileBg-hd" + fileExtension); // ������ ���
		parentSprite.addChild(profileBg, 10, 10);
		profileBg.setPosition(
				parentSprite.getContentSize().width / 2, 
				parentSprite.getContentSize().height - profileBg.getContentSize().height / 2 - 40.0f);
		
		
		CCSprite profilePicture= CCSprite.sprite(commonfolder + "frame-pictureFrame-hd" + fileExtension); // ������ ���� Ʋ
		profilePicture.setPosition(
				profilePicture.getContentSize().width / 2 + 85, 
				profileBg.getContentSize().height-60.0f);
		
		CCMenuItem post = CCMenuItemImage.item(  // ����
				imageFolder + "home-mail-hd" + fileExtension, 
				imageFolder + "home-mail-hd" + fileExtension, 				 
				nodeThis,"clicked2");
		post.setTag(mailButton);
		post.setAnchorPoint(1.0f, 0.5f);
		
		//
		
		CCSprite userImage = null;
		
//		Bitmap userBMP =null;
		String imageUrl = "https://graph.facebook.com/" + FacebookData.getinstance().getUserInfo().getUsername() +"/picture";
		try {
			Bitmap userBMP = new DownloadImageTask().execute(imageUrl).get();
			userImage = CCSprite.sprite(userBMP); // ������ ����
			profilePicture.addChild(userImage);
			userImage.setAnchorPoint(0.5f, 0.5f);
			userImage.setPosition(profilePicture.getContentSize().width / 2, profilePicture.getContentSize().height / 2);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		
//		CCSprite userImage = CCSprite.sprite(commonfolder + "noPicture" + fileExtension); // ������ ����
		
		CCSprite newIcon = CCSprite.sprite(imageFolder + "home-mailNew-hd01" + fileExtension); // ���ο� ����
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
		
		CCSprite levelProgressBar = CCSprite.sprite(imageFolder + "home-progressBar-hd" + fileExtension); // ���� ���α׷��� ��
		levelProgressBar.setPosition(
				profileBg.getContentSize().width/2+10f,
				levelProgressBar.getContentSize().height/2+25.0f);
		
		
		
		int levelValue = Integer.parseInt(FacebookData.getinstance().getDBData("LevelCharacter"));
		float expValue = Float.parseFloat(FacebookData.getinstance().getDBData("Exp"));
		int levelLimit = userData.expPerLevel.length;
		int expLimit = 999999999;
		String expStr ;
		float scale;
		if (levelValue < levelLimit) {
			expLimit = userData.expPerLevel[levelValue - 1];
			expStr = (int)expValue + " / " + expLimit;
			scale = (levelProgressBar.getContentSize().width - 4) * (expValue / expLimit);
		} else {
			expStr = "MAX";
			scale = levelProgressBar.getContentSize().width - 4;
		}
		
		CCSprite gaugeBar = CCSprite.sprite(imageFolder + "Home-gaugeBar-hd" + fileExtension); // ���� ������ ��
		levelProgressBar.addChild(gaugeBar);
		gaugeBar.setAnchorPoint(0, 0.5f);
		gaugeBar.setPosition(2, levelProgressBar.getContentSize().height / 2);
		gaugeBar.setScaleX(scale / gaugeBar.getContentSize().width);
		
		CCLabel exp = CCLabel.makeLabel(expStr, "Arial", 11); // ����ġ (�������� �����ʿ�)
		levelProgressBar.addChild(exp);
//		exp.setColor(ccColor3B.ccc3(194, 216, 233));
//		exp.setColor(ccColor3B.ccc3(0, 35, 102));
		exp.setColor(ccColor3B.ccc3(50, 50, 0x8c));
		exp.setPosition(
				levelProgressBar.getContentSize().width/2,
				levelProgressBar.getContentSize().height/2);
		
		
		String rankingStr = "" + FacebookData.getinstance().ranking;
		Log.e("HomeMiddle", "ranking : " + rankingStr);
//		String rankingStr = "0";
//		String[][] rankingArray = DataFilter.getRanking("(" + FacebookData.getinstance().getUserInfo().getId() + ")");
//		for (String[] strings : rankingArray) {
//			if (strings[0].equals(FacebookData.getinstance().getUserInfo().getId())) {
//				rankingStr = strings[0];
//			}
//		}

		CCLabel  ranking = CCLabel.makeLabel(rankingStr, "Arial", 30); // ����
		ranking.setPosition(
				ranking.getContentSize().width/2+55.0f, 
				profileBg.getContentSize().height-60.0f);
		
		CCLabel userName;
		
		if (FacebookData.getinstance().getUserInfo() != null) {
			userName = CCLabel.makeLabel(FacebookData.getinstance().getUserInfo().getName(), "Arial", 30); // �̸�
		} else {
			userName = CCLabel.makeLabel("�̸� �ҷ����� ����.", "Arial", 30); // �̸�
		}
		userName.setPosition(
				profilePicture.getPosition().x + ((1 - profilePicture.getAnchorPoint().x) * profilePicture.getContentSize().width) + 12, 
				profileBg.getContentSize().height - userName.getContentSize().height / 2 - 20);
		userName.setAnchorPoint(0.0f, 0.5f);

		CCLabel  score = CCLabel.makeLabel(
				new NumberComma().numberComma(FacebookData.getinstance().getDBData("Point")),
				"Arial", 30); // �ְ� ����
		score.setPosition(
				userName.getPosition().x, 
				profileBg.getContentSize().height- score.getContentSize().height / 2 - 60.0f);
				score.setAnchorPoint(0.0f, 0.5f);

				String[] Ko = {"�� : ","�� / ","��"," 18"};
				String[] En = {"Played "," : W","/L "," 16"};
							
				String record = null;
				float recordSize;
				if (Locale.getDefault().getLanguage().toString().equals("en")) {
					record =  
							En[0] + 	(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryWin")) +
											Integer.parseInt(FacebookData.getinstance().getDBData("HistoryLose"))) +
							En[1] + 	FacebookData.getinstance().getDBData("HistoryWin") + 
							En[2] + 	FacebookData.getinstance().getDBData("HistoryLose");
					recordSize = Float.parseFloat(En[3]);
				} else {
					record = 
							(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryWin")) +
							Integer.parseInt(FacebookData.getinstance().getDBData("HistoryLose"))) + Ko[0] +
							FacebookData.getinstance().getDBData("HistoryWin") + Ko[1] + 
							FacebookData.getinstance().getDBData("HistoryLose") + Ko[2];
					recordSize = Float.parseFloat(Ko[3]);
				}
			
		CCLabel  recordData = CCLabel.makeLabel(record, "Arial", recordSize); // ���� ��
		recordData.setPosition(
				userName.getPosition().x,
				profilePicture.getPosition().y - (profilePicture.getAnchorPoint().y * profilePicture.getContentSize().height)
				);
		recordData.setAnchorPoint(0, 1);

		
		CCLabel  level = CCLabel.makeLabel("Lv "+ FacebookData.getinstance().getDBData("LevelCharacter"), "Arial", 40.0f); // ����
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
		profileBg.addChild(recordData);
		profileBg.addChild(level);
	}
	

	// Callback Method
//	public void mailCallback(Object sender) {
////			MailBox mail =	new MailBox(this, "11mailbox/", nodeThis);
//			MailBox mail =	new MailBox(null, "11mailbox/", nodeThis);
////			 broomstickBackground1 = mail.getBroomstickBackground1();
////			 broomstickBackground2 = mail.getBroomstickBackground2();
////			 presentBackground1 = mail.getPresentBackground1();
////			 presentBackground2 = mail.getPresentBackground2();
//	}
	
	public void pesterCallback(Object sender) {
//		broomstickBackground1.setVisible(true);
//		broomstickBackground2.setVisible(false);
//		presentBackground1.setVisible(false);
//		presentBackground2.setVisible(true);
	}

	public void presentCallback(Object sender) {
//		broomstickBackground1.setVisible(false);
//		broomstickBackground2.setVisible(true);
//		presentBackground1.setVisible(true);
//		presentBackground2.setVisible(false);
	}
	
//	public void mailboxCloseCallback(Object sender) {
////		this.removeChildByTag(999, true);
//		nodeThis.removeChildByTag(999, true);
//	}
}
