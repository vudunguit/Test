package com.aga.mine.mains;

import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.util.Log;

public class Home extends CCLayer{
		
	final String commonfolder = "00common/";
	final String folder = "10home/";
	final String fileExtension = ".png";
	static CCLayer scroll = CCLayer.node();
	public static CCScene scene() {
		Log.e("Home", "scene");
		CCScene scene = CCScene.node();
		CCLayer homeLayer = new Home();
		scene.addChild(homeLayer, 2, 2);
//		layer.setIsTouchEnabled(true);
//		setScrollView(scene);
		
//		CCLayer scroll = HomeScroll.getInstance().getLayer(DataFilter.getRanking());
//		scene.addChild(scroll, 1, 1);
//		scroll.setAnchorPoint(0.5f, 1);
//		scroll.setPosition(
//				CCDirector.sharedDirector().winSize().width / 2 - scroll.getContentSize().width / 2,
//				CCDirector.sharedDirector().winSize().height / 2 - scroll.getContentSize().height + 110);
		scene.addChild(scroll, 1, 1);
		
		CCLayer mailBox = CCLayer.node();
		scene.addChild(mailBox,3,3);
        return scene;
	}

//	private static void setScrollView(CCScene scene) {
//		scene.addChild(HomeScroll.getInstance().getLayer(), 1, 1);
//
//		CGPoint pb = bb.convertToWorldSpace(profileBg.getPosition().x,profileBg.getPosition().y);
////		CGPoint bf = bg.convertToWorldSpace(boardFrame.getPosition().x,boardFrame.getPosition().y);
//		scene.getChildByTag(1).setPosition(
//        		scene.getContentSize().width / 2 - scene.getChildByTag(1).getContentSize().width / 2,
//        		pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5);
////		
////		scrollTopBoundery = pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5; 
////		scrollBottomBoundery = bf.y - (boardFrame.getAnchorPoint().y * boardFrame.getContentSize().height) + 180; 
//	}
	
	public Home() {
//		FacebookData.getinstance().setUserInfo(FacebookData.getinstance().getUserInfo());
		CCSprite backGround = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg2" + fileExtension);
		CCSprite backBoard = setBackBoardMenu(backGround, folder + "home-backboard" + fileExtension);
		CCSprite boardFrame = setBoardFrameMenu(backGround, commonfolder + "frameGeneral-hd" + fileExtension);

//		scroll = HomeScroll.getInstance(this).getLayer();
		scroll = HomeScroll.getInstance().getLayer();
		scroll.setAnchorPoint(0.5f, 1);
		scroll.setPosition(
				CCDirector.sharedDirector().winSize().width / 2 - scroll.getContentSize().width / 2,
				CCDirector.sharedDirector().winSize().height / 2 - scroll.getContentSize().height + 110);
		
//		CCSprite profileBg = (CCSprite) backBoard.getChildByTag(10);
//		CGPoint pb = backBoard.convertToWorldSpace(profileBg.getPosition().x,profileBg.getPosition().y);
//		int friendsSize = FacebookData.getinstance().getFriendsInfo().size();
//		CCLayer scroll = (CCLayer) this.getParent().getChildByTag(1);
//		scroll.setPosition(
//        		winsize().width / 2 - scroll.getContentSize().width / 2,
//        		pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5);
//		this.setIsTouchEnabled(true);
		
		this.setIsTouchEnabled(true);
	}
	
	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}

	// 백 보드 설정
	private CCSprite setBackBoardMenu(CCSprite parent, String imageFullPath) {
		CCSprite backBoard = setHomeMenu(parent, imageFullPath);
		new HomeMiddle(backBoard, folder, this);
		return backBoard;
	}

	// 게시판 설정
	private CCSprite setBoardFrameMenu(CCSprite parent, String imageFullPath) {
		CCSprite boardFrame = setHomeMenu(parent, imageFullPath);	
		new HomeTop(boardFrame, folder, this);
//		new HomeBottom(boardFrame, folder, this);
		return boardFrame;
	}
	
	private CCSprite setHomeMenu(CCSprite parent, String imageFullPath) {
		CCSprite sprite = CCSprite.sprite(imageFullPath);
		parent.addChild(sprite);
		sprite.setPosition(parent.getContentSize().width / 2, parent.getContentSize().height * 0.525f);
		sprite.setAnchorPoint(0.5f, 0.5f);
		return sprite;
	}
	
	public void clicked(Object sender) {
		
		final int broomstick = 0;
		final int gold = 1;
		final int shop = 2;
		final int enter = 3;
		final int option = 4;
		final int invite = 5;
		final int mail = 6;
		final int mailclose = 7;
		final int getItem = 8;
		final int mailReceiveAll = 9;
		final int presentGold = 10;
		final int presentBroomstick = 11;
		
		int value = 99;
		CCScene scene;
		
		String data = (String) ((CCMenuItemImage)sender).getUserData();
		String[] callback = {"broomstick","gold","shop","enter","option",
				"invite","mailopen","mailclose","getItem","mailReceiveAll",
				"presentGold","presentBroomstick"};
		Log.e("Home", "CallBack1 : " + data);
		for (int i = 0; i < callback.length; i++) {
			if (data.indexOf(callback[i]) >= 0) {
				value = i;
				break;
			}
		}
		Log.e("Home", "CallBack2 : " + value);
		
		switch (value) {
		case broomstick :
			Log.e("Home", "CallBack3 : broomstick" + data);
			FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
			scene = ShopBroomstick2.scene();
			break;
		case gold :
			Log.e("Home", "CallBack3 : gold" + data);
			FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
			scene = ShopGold2.scene();
			break;
			case shop :
				Log.e("Home", "CallBack3 : shop" + data);
			scene = Shop.scene();
			break;
		case enter :
			Log.e("Home", "CallBack3 : enter" + data);
			scene = GameMode.scene();
			break;
		case option :
			Log.e("Home", "CallBack3 : option" + data);
			scene = Option.scene();
			break;
		case invite :
			Log.e("Home", "CallBack3 : invite" + data);
			scene = Invite.scene();
			break;
		default:
			Log.e("Home", "CallBack3 : Home" + data);
			scene = Home.scene();
			break;
		}
		
		CCScene homeScene = (CCScene) this.getParent();
		CCLayer mailBoxLayer = (CCLayer) homeScene.getChildByTag(3);
		
		if (value == mail) {
			Log.e("Home", "CallBack2 : " + value);
			new MailBox(mailBoxLayer, "11mailbox/", this);
			
			List<CCNode> layers = homeScene.getChildren();
			for (CCNode ccNode : layers) {
				CCLayer tempLayer = (CCLayer) ccNode;
				if (tempLayer != mailBoxLayer) {
//					tempLayer.setIsTouchEnabled(false);
					List<CCNode> sprites = tempLayer.getChildren();
					for (CCNode ccNode2 : sprites) {
						Log.e("Home", "CCNode " + ccNode2);
						if (ccNode2 instanceof CCMenu) {
							Log.e("Home", "ccNode2 " + ccNode2);
							List<CCNode> tempMenuItems = ccNode2.getChildren();
							for (CCNode ccNode3 : tempMenuItems) {
								((CCMenuItem)ccNode3).setIsEnabled(false);
							}
//							CCMenuItem tempMenuItem = (CCMenuItem)ccNode2;	
						}
					}
				}
			}
//			((CCLayer)homeScene.getChildByTag(1)).setTouchEnabled(false);
//			((CCLayer)homeScene.getChildByTag(2)).setIsTouchEnabled(false);
//			((CCLayer)homeScene.getChildByTag(3)).setIsTouchEnabled(false);
////			((CCLayer)homeScene.getChildByTag(1)).removeAllChildren(true);
////			((CCLayer)homeScene.getChildByTag(2)).removeAllChildren(true);
//			CCLayer layer = CCLayer.node();
//			scene.addChild(homeLayer, 2, 2);
		} else if (value == mailclose) {
			mailBoxLayer.removeChildByTag(999, true);
			List<CCNode> layers = homeScene.getChildren();
			for (CCNode ccNode : layers) {
				if ((CCLayer)ccNode != mailBoxLayer) {
//					((CCLayer)ccNode).setIsTouchEnabled(false);
					((CCLayer)ccNode).setVisible(true);
				}
			}
//			((CCLayer)homeScene.getChildByTag(1)).setIsTouchEnabled(true);
//			((CCLayer)homeScene.getChildByTag(2)).setIsTouchEnabled(true);
		} else if (value == mailReceiveAll) {
			String[] items = data.split(",");
			for (String item : items) {
				Log.e("Home", "mailReceiveAll : " + item);
				DataFilter.itemEraser(item);
			}
			// child sprite 제거 방식으로 수정 요함.
			mailBoxLayer.removeChildByTag(999, true);
			new MailBox(mailBoxLayer, "11mailbox/", this);
		} else if (value == presentGold) {
			Log.e("Home", "presentGold : " + value);
			String[] items = data.split(",");
			for (String item : items) {
				if (!item.equals("presentGold")) {
					FacebookData.getinstance().setRecipientID(item);
					scene = ShopGold2.scene();
					CCDirector.sharedDirector().replaceScene(scene);
				}
			}
		} else if (value == presentBroomstick) {
			Log.e("Home", "presentBroomstick : " + value);
			String[] items = data.split(",");
			for (String item : items) {
				if (!item.equals("presentBroomstick")) {
					String senderID = FacebookData.getinstance().getUserInfo().getId();
					String sendMailData = 
							"0,RequestModeMailBoxAdd*22," + FacebookData.getinstance().getRequestID() + 
							"*1," + item + "*19," + senderID + "*20,Broomstick*21," + 1;
					FacebookData.getinstance().sendMail(sendMailData);
				}
			}
		} else {
			CCDirector.sharedDirector().replaceScene(scene);
		}

	}
	
}