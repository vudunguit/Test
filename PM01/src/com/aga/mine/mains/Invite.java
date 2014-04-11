package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import com.aga.mine.pages.UserData;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class Invite extends CCLayer {
	
//	public static int mode;
	
	final String commonfolder = "00common/";
	final String folder = "30invite/";
	final String fileExtension = ".png";

	CCSprite bg;
	
	int mode = 0;
	
	CCSprite backboardUpper;
	CCSprite boardFrame;
	CCSprite sprite1;
	CCSprite sprite2;
	
	public static float displayLeft;
	public static float displayRight;
	public static float displayTop;
	public static float displayBottom;
	
	CCMenuItem inviteButton; 
	CCMenuItem timerBack;
	CCMenu menu111;
	
	private Context mContext;
	UserData userData;
	private static Invite inviteLayer;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new Invite();
		scene.addChild(layer);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}

//	private CGSize winsize() {
//		return CCDirector.sharedDirector().winSize();
//	}

	public static synchronized Invite getInstance() {
		if (inviteLayer == null) {
			inviteLayer = new Invite();
		}
		return inviteLayer;
	}

	private Invite() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		
		//배경 그림 설정
//		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg-invite" + fileExtension);
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);		
		setBackBoardMenu(folder + "invite-bb2" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		// 하단 이미지
		BottomImage.setBottomImage(this);
		
		this.setIsTouchEnabled(true);
	}

//	String[] testNameList =  {"김 길 동","이 길 동","박 길 동","최 길 동"};

	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite backBoard = CCSprite.sprite(imageFullPath);
		bg.addChild(backBoard);
		backBoard.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		backBoard.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(backBoard);
//		friendsList(backBoard, testNameList); // 안드로이드 리스트뷰로 대체
	}
	
	// 게시판 설정
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle2.setTitle(boardFrame, folder);
	}

	// 메인 메뉴
	private void setMainMenu(CCSprite parentSprite){
		CCLabel  inviteCountText = CCLabel.makeLabel("현재초대인원         명", "Arial", 25); // 현재 초대 인원 (노란색)
		parentSprite.addChild(inviteCountText);
		inviteCountText.setColor(ccColor3B.ccYELLOW);
		inviteCountText.setPosition(parentSprite.getContentSize().width / 2, 187);
		
		String app_Invite = FacebookData.getinstance().getDBData("InviteNumber");
		CCLabel  inviteCount = CCLabel.makeLabel(
				new NumberComma().numberComma(app_Invite), "Arial", 35); // 현재 초대 인원 (노란색)
		parentSprite.addChild(inviteCount);
		inviteCount.setColor(ccColor3B.ccYELLOW);
		inviteCount.setPosition(parentSprite.getContentSize().width/2 + 60, 190);

		CCLabel inviteText = CCLabel.makeLabel("초대는 하루 20명까지 가능 합니다.", "Arial", 20); // 흰색
		parentSprite.addChild(inviteText);
		inviteText.setPosition(parentSprite.getContentSize().width/2, 160.0f);		

		jewelButton(parentSprite, -1.1f, 3000, 30);
		jewelButton(parentSprite, 0, 7000, 50);
		jewelButton(parentSprite, 1.1f, 10000, 70);
	}
		
	private void jewelButton(CCSprite backBoard, float position, int gold, int friends) {

		CCSprite statusPanel = CCSprite.sprite(folder + "invite-statusPanel" + fileExtension); //보석 버튼
		statusPanel.setAnchorPoint(0.5f, 0.0f);
		statusPanel.setPosition(
				backBoard.getContentSize().width/2 + statusPanel.getContentSize().width * position, 
				statusPanel.getContentSize().height/2-10.0f);
		
		CCLabel  statusPanelText1 = CCLabel.makeLabel(
				"Gold " + new NumberComma().numberComma(gold), "Arial", 22); // 보석 보상 갯수 (노란색)
		statusPanelText1.setColor(ccColor3B.ccYELLOW);
		statusPanelText1.setAnchorPoint(0.5f, 1.0f);
		statusPanelText1.setPosition(
				statusPanel.getContentSize().width / 2,
				statusPanel.getContentSize().height - 3);
		
		CCLabel  statusPanelText2 = CCLabel.makeLabel("친구 초대", "Arial", 22); // 현재 초대 인원 (노란색)
		statusPanelText2.setColor(ccColor3B.ccYELLOW);
		statusPanelText2.setAnchorPoint(0.5f, 1.0f);
		statusPanelText2.setPosition(
				statusPanel.getContentSize().width / 2,
				statusPanel.getContentSize().height - 39f);

		CCLabel  statusPanelText3 = CCLabel.makeLabel(friends + "명", "Arial", 22); // 현재 초대 인원 (노란색)
		statusPanelText3.setColor(ccColor3B.ccYELLOW);
		statusPanelText3.setAnchorPoint(0.5f, 1.0f);
		statusPanelText3.setPosition(
				statusPanel.getContentSize().width / 2,
				statusPanel.getContentSize().height - 66f);
		

		backBoard.addChild(statusPanel);
		statusPanel.addChild(statusPanelText1);
		statusPanel.addChild(statusPanelText2);
		statusPanel.addChild(statusPanelText3);
	}
	
	// 안드로이드 리스트 뷰로 대체 (이미지 참고용으로 놔뒀습니다.)
//	private void friendsList(CCSprite parentSprite, String[] names) {
//
//	//	int i = 0;
//		for (int i = 0; i < names.length; i++) {
//
//			// 친구 리스트 판넬
//			CCSprite listPanel = CCSprite.sprite(folder + "invite-listPanel" + fileExtension);
//			listPanel.setPosition(
//					parentSprite.getContentSize().width / 2,
//					parentSprite.getContentSize().height - listPanel.getContentSize().height * (0.5f + i) - 50);
//
//			// 친구 이미지
//			CCSprite listPictureFrame = CCSprite.sprite(commonfolder + "frame-pictureFrame-hd" + fileExtension);
//			listPictureFrame.setPosition(50.0f, listPanel.getContentSize().height / 2);
//			listPictureFrame.setScale(0.8f);
//
//			listPanel.addChild(listPictureFrame);
//
//			//
//			CCLabel myName = CCLabel.makeLabel(names[i], "Arial", 30.0f);
//			myName.setPosition(CGPoint.make(140.0f,
//					listPanel.getContentSize().height / 2));
//
//			listPanel.addChild(myName);
//
//			// 친구초대
//			inviteButton = CCMenuItemImage.item(
//					Utility.getInstance().getNameWithIsoCodeSuffix(folder + "invite-button1" + fileExtension),
//					Utility.getInstance().getNameWithIsoCodeSuffix(folder + "invite-button1" + fileExtension), 
//					this, "timerCallback");
//
//			timerBack = CCMenuItemImage.item(folder + "blank" + fileExtension, folder + "blank" + fileExtension);
//			timerBack.setPosition(0f,0f);
//
//			CCLabel buttonName = CCLabel.makeLabel("24시간 남음", "Arial", 24.0f);
//			timerBack.addChild(buttonName);
//			buttonName.setColor(ccColor3B.ccRED);
//			buttonName.setPosition(timerBack.getContentSize().width / 2, timerBack.getContentSize().height / 2);
//
//
//
//			//CCMenuItem[] items = {inviteButton, timerBack};
//			//menu111 = CCMenu.menu(items);
//			menu111 = CCMenu.menu(inviteButton);
//			menu111.setContentSize(inviteButton.getContentSize());
//			menu111.setPosition(
//					listPanel.getContentSize().width - inviteButton.getContentSize().width / 2 - 4f,
//					listPanel.getContentSize().height / 2);
//			
//
//			//timerBack.setPosition(inviteButton.getContentSize().width/2,inviteButton.getContentSize().height/2);
//
//			listPanel.addChild(menu111);
//
//			//
//			CCSprite invite;
//			if (visible_) {
//				// 초대하기(활성화)
//				invite = CCSprite.sprite(
//						Utility.getInstance().getNameWithIsoCodeSuffix(folder + "invite-button1" + fileExtension)); 
//			} else {
//				// 초대하기(비활성화)
//				invite = CCSprite.sprite(
//						Utility.getInstance().getNameWithIsoCodeSuffix(folder + "invite-button2" + fileExtension)); 
//			}
//			invite.setPosition(
//					listPanel.getContentSize().width
//							- invite.getContentSize().width / 2 - 4f,
//					listPanel.getContentSize().height / 2);
//
//			// listPanel.addChild(invite);
//
//			//
//			parentSprite.addChild(listPanel);
//		}
//	}

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
			switch (value) {
				case previous :
				case home :
					if (GameData.share().isGuestMode) { // 게스트모드는 들어올수 없기때문에 불필요한 코드지만 만약에 오류상황시 넘길수 있는 상태로 놔둠.
						scene = Home2.scene();
					} else {
						scene = Home.scene();
					}
					break;
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}
	
	public void timerCallback(Object sender) {
		CCNode a= (CCNode)sender;
		a.getParent().addChild(timerBack);
		a.removeFromParentAndCleanup(true);
		Log.e("Invite", "timerCallback : " + a.getUserData());

		long requestID = FacebookData.getinstance().getRequestID();  //test용 
		String recipientID = FacebookData.getinstance().getUserInfo().getId();
		String senderID = "1";
		String sendMailData = 
				"0,RequestModeMailBoxAdd" +
				"*22," + requestID + 
				"*1," + recipientID + 
				"*19," + senderID + 
				"*20,Broomstick*21," + 1;
		FacebookData.getinstance().sendMail(sendMailData);
	}
	
}