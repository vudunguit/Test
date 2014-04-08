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
	
	final static int scrollLayerTag = 1;
	final static int homeLayerTag = 2;
	final static int mailBoxLayerTag = 3;
	
	// 나중에 다른 클래스에서 정의한 것과 하나로 합치기
	final int broomstickButton = 1001;
	final int goldButton = 1002;
	final int shopButton = 1003;
	final int enterButton = 1004;
	final int optionButton = 1005;
	final int inviteButton = 1006;
	final int mailButton = 1007;
	final int mailcloseButton = 1008;
	final int mailReceiveAllButton = 1009;
	final int presentGoldButton = 1010;
	final int presentBroomstickButton = 1011;
	
//	static CCLayer scroll = CCLayer.node(); // 안드로이드 뷰로 인하여 제거됨.
	
	public static CCScene scene() {
		Log.e("Home", "scene");
		CCScene scene = CCScene.node();
		CCLayer home = new Home();
		scene.addChild(home, homeLayerTag, homeLayerTag);
//		layer.setIsTouchEnabled(true);
//		setScrollView(scene);
		
//		CCLayer scroll = HomeScroll.getInstance().getLayer(DataFilter.getRanking());
//		scene.addChild(scroll, 1, 1);
//		scroll.setAnchorPoint(0.5f, 1);
//		scroll.setPosition(
//				CCDirector.sharedDirector().winSize().width / 2 - scroll.getContentSize().width / 2,
//				CCDirector.sharedDirector().winSize().height / 2 - scroll.getContentSize().height + 110);
//		scene.addChild(scroll, scrollLayerTag, scrollLayerTag); // 안드로이드 뷰로 인하여 제거됨.
		
		CCLayer mailBox = CCLayer.node();
		scene.addChild(mailBox,mailBoxLayerTag,mailBoxLayerTag);
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
		CCSprite backGround = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg-open" + fileExtension);
		CCSprite backBoard = setBackBoardMenu(backGround, folder + "home-backboard" + fileExtension);
		CCSprite boardFrame = setBoardFrameMenu(backGround, commonfolder + "frameGeneral-hd" + fileExtension);

//		scroll = HomeScroll.getInstance(this).getLayer();
//		scroll = HomeScroll.getInstance().getLayer();
/*		scroll = HomeScroll.getInstance().getLayer(this); // 안드로이드 뷰로 인하여 제거됨.
		scroll.setAnchorPoint(0.5f, 1);
		scroll.setPosition(
				CCDirector.sharedDirector().winSize().width / 2 - scroll.getContentSize().width / 2,
				CCDirector.sharedDirector().winSize().height / 2 - scroll.getContentSize().height + 110);*/
		
//		CCSprite profileBg = (CCSprite) backBoard.getChildByTag(10);
//		CGPoint pb = backBoard.convertToWorldSpace(profileBg.getPosition().x,profileBg.getPosition().y);
//		int friendsSize = FacebookData.getinstance().getFriendsInfo().size();
//		CCLayer scroll = (CCLayer) this.getParent().getChildByTag(1);
//		scroll.setPosition(
//        		winsize().width / 2 - scroll.getContentSize().width / 2,
//        		pb.y - (profileBg.getAnchorPoint().y * profileBg.getContentSize().height) - (94 * friendsSize) - 5);
//		CCTouchDispatcher.sharedDispatcher().setDispatchEvents(false);
		this.setIsTouchEnabled(true);
		
		//display scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_FRIENDLIST);
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
					CCScene scene = ShopGold2.scene();
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
		}

	}
	


	public void clicked2(Object sender) {
		//hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		
		int value = ((CCNode) sender).getTag();
		CCScene scene;
		
		switch (value) {
		case broomstickButton :
			Log.e("Home", "CallBack3 : broomstick" + value);
			FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
			scene = ShopBroomstick2.scene();
			break;
		case goldButton :
			Log.e("Home", "CallBack3 : gold" + value);
			FacebookData.getinstance().setRecipientID(FacebookData.getinstance().getUserInfo().getId());
			scene = ShopGold2.scene();
			break;
		case shopButton :
				Log.e("Home", "CallBack3 : shop" + value);
			scene = Shop.scene();
			break;
		case enterButton :
			Log.e("Home", "CallBack3 : enter" + value);
			scene = GameMode.scene();
			break;
		case optionButton :
			Log.e("Home", "CallBack3 : option" + value);
			scene = Option.scene();
			break;
		case inviteButton :
			Log.e("Home", "CallBack3 : invite" + value);
			scene = Invite.scene();
			break;
		default:
			Log.e("Home", "CallBack3 : Home" + value);
			scene = Home.scene();
			break;
		}
		
		if (value <= inviteButton) {
			CCDirector.sharedDirector().replaceScene(scene);
			return;
		}
		
		CCScene homeScene = (CCScene) this.getParent();
		CCLayer mailBoxLayer = (CCLayer) homeScene.getChildByTag(mailBoxLayerTag);
		String data = (String) ((CCMenuItemImage)sender).getUserData();
		
		if (value == mailButton) {
			// 안드로이드 스크롤뷰로 교체해야됩니다.
			new MailBox(mailBoxLayer, "11mailbox/", this);
			
			List<CCNode> layers = homeScene.getChildren();
			for (CCNode ccNode : layers) { // home scene에 붙은 layer
				CCLayer tempLayer = (CCLayer) ccNode;
				if (tempLayer != mailBoxLayer) {  // layer중에 mailboxlayer만 제외
					tempLayer.setIsTouchEnabled(false);
					
//					List<CCNode> sprites = tempLayer.getChildren();
//					for (CCNode ccNode2 : sprites) {
//						Log.e("Home", "CCNode " + ccNode2);
//						if (ccNode2 instanceof CCMenu) { // layer중에 menu만 고르고
//							Log.e("Home", "ccNode2 " + ccNode2);
//							List<CCNode> tempMenuItems = ccNode2.getChildren(); // menu에서 버튼을 골라서
//							for (CCNode ccNode3 : tempMenuItems) {
//								((CCMenuItem)ccNode3).setIsEnabled(false); // 터치 잠금
//							}
//						} else if (ccNode2 instanceof CCMenuItem) {
//							((CCMenuItem)ccNode2).setIsEnabled(false); // 바로 menuitem이 나오면 터치 잠금
//
						}
					}
				} else if (value == mailcloseButton) {
					mailBoxLayer.removeChildByTag(999, true);
					List<CCNode> layers = homeScene.getChildren();
					for (CCNode ccNode : layers) { // home scene에 붙은 layer
						CCLayer tempLayer = (CCLayer) ccNode;
							tempLayer.setIsTouchEnabled(true);
					}
				} else if (value == mailReceiveAllButton) {
					String[] items = data.split(",");
					for (String item : items) {
						Log.e("Home", "mailReceiveAll : " + item);
						if (!item.equals("")) {
							DataFilter.itemEraser(item);	
						}
					}
					// child sprite 제거 방식으로 수정 요함.
					mailBoxLayer.removeChildByTag(999, true);
					new MailBox(mailBoxLayer, "11mailbox/", this);
				} else if (value == presentGoldButton) {
					Log.e("Home", "presentGold : " + value);
					String[] items = data.split(",");
					for (String item : items) {
							FacebookData.getinstance().setRecipientID(item);
							scene = ShopGold2.scene();
							CCDirector.sharedDirector().replaceScene(scene);
					}
				} else if (value == presentBroomstickButton) {
					Log.e("Home", "presentBroomstick : " + value);
					String[] items = data.split(",");
					for (String item : items) {
							String senderID = FacebookData.getinstance().getUserInfo().getId();
							String sendMailData = 
									"0,RequestModeMailBoxAdd*22," + FacebookData.getinstance().getRequestID() + 
									"*1," + item + "*19," + senderID + "*20,Broomstick*21," + 1;
							FacebookData.getinstance().sendMail(sendMailData);
					}
				}
	}
	
	private void modifyMenu(CCMenu menu, boolean enabled) {
		for (CCNode menuItem : menu.getChildren()) {
			((CCMenuItem) menuItem).setIsEnabled(enabled);
		}
	}
}