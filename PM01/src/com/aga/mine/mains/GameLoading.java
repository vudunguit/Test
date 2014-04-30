package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.aga.mine.pages2.Game;
import com.aga.mine.util.Util;

public class GameLoading extends CCLayer {
	
	CCSprite bg;
	
	final String folder = "59gameload/";
	final String fileExtension = ".png";
	
	public final int BROOMSTICK_REFRESH_TIME = 900; //900초(15분)
//	private int mBroomstickCount; //빗자루 수량
//	private long mMiliToRefreshBroom; //단위는 ms, 빗자루를 갱신하기까지 남은 시간
	
	private GameLoading() {
		
		/************ 잘 사용했는지 모르겠습니다. 확인 부탁드립니다.  ************/
		// 게임 입장시 출입증격인 Broomstick을 1개 사용
		// 사용후 남은 Broomstick이 6개 미만일시
		// 기존에 빗자루 무료 제공하는 시간을 받아온 후
		// setBroomstickTime(빗자루 무료 지급기) 실행
		int mBroomstickCount = Integer.parseInt(FacebookData.getinstance().getDBData("ReceivedBroomstick"));
		FacebookData.getinstance().modDBData("ReceivedBroomstick", String.valueOf(mBroomstickCount - 1)); //DB에 빗자루 수량 insert
		if (mBroomstickCount <= 6) {
			long pastTime = Util.getBroomstickTime(); //경과 시간(ms), 
			long mMiliToRefreshBroom = pastTime%(BROOMSTICK_REFRESH_TIME*1000); //빗자루 수량과 남은시간 계산후 다시 pref에 세팅
			Util.setBroomstickTime(mMiliToRefreshBroom);
		}
//		// hide scroll view
//		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		setBackground();
		setMainMenu();
		schedule("nextSceneCallback", 1.0f);
	}

	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameLoading();
		scene.addChild(layer, 10, 10);
		return scene;
	}

	private CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	private CGPoint center() {
		return CGPoint.make(winsize().width/2, winsize().height/2);
	}
	
	// 배경 스케일 설정
	private void setBackground() {
		bg = CCSprite.sprite(folder + "gameloadbg" + fileExtension);
		bg.setPosition(center());
		this.addChild(bg);
		
		// 타이틀명
		FrameTitle4.setTitle(bg, folder);
	}
	
	private void setMainMenu() {
		CCSprite cloud = CCSprite.sprite(
				folder + "loadsmoke" + fileExtension);
		cloud.setPosition(50,120);
		cloud.setAnchorPoint(0, 0);
		bg.addChild(cloud);
		
		CCSprite bar = CCSprite.sprite(
				folder + "loadpiece" + fileExtension);
		bar.setPosition(160,127);
		bar.setAnchorPoint(0, 0);
		bg.addChild(bar);
		bar.setScaleX(75);
		
		CCSprite wizard = CCSprite.sprite(
				folder + "loadwizard" + fileExtension);
		wizard.setPosition(420,108);
		wizard.setAnchorPoint(0, 0);
		bg.addChild(wizard);
	}
	
	public void nextSceneCallback(float dt) {
//		GameData.share().isGuestMode = true;
		CCScene scene = Game.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
