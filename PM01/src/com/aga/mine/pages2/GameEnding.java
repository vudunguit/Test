package com.aga.mine.pages2;

import java.io.IOException;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.Map;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.DataFilter;
import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.Home;
import com.aga.mine.mains.Home2;
import com.aga.mine.mains.MainApplication;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.Utility;
import com.facebook.android.Facebook;

public class GameEnding extends CCLayer {

	String folder = "70game_ending/";
	
	CGSize winSize = CCDirector.sharedDirector().winSize();
	String myName = "Guest";
	String myID = "0";
	int myLevel = 0;
	int myScore = 0;
	int myGold = 0;
	int myExp = 0;
	float extraTimeReward = 1.25f; // 대전게임 승리자가 연장 게임을 하여서 완료시 기본 보상의 1.25배로 보상을 받음.  
	boolean isExtraTime = false;  // 현재 사용하지 않는 것으로 보임.
	
	int decreaseScore = 0; // 일반적으로 대전 패배시 스코어 감소
	int decreaseGold = 0; // 스코어 보호를 위해 스코어 대신 골드로 대신 감소(선택적)
	
	private CCLabel lv;
	private CCLabel exp;
	private CCSprite bg;
	private CCSprite expHead;
	private CCSprite expTail;
	private CCSprite expBar;
	private float mExpX; //경험치 충전 상태
	private int mLeftExp; //초기: 획득 경험치, 애니메이션이 진행됨에 따라 점점 줄어듬.
	

//	public static CCScene scene() {
//		CCScene scene = CCScene.node();
////		GameEnding2 layer = new GameEnding();
//		CCLayer layer = new GameEnding();
//		scene.addChild(layer);
//		return scene;
//	}
//	public static CCLayer layer() {
//		CCLayer layer = new GameEnding();
//		return layer;
//	}

//	int myScore2;
	int otherScore;
	int closedCell;
	
	public GameEnding(int myScore2, int otherScore, int closedCell) {
		basket = new HashMap<String, String>();
		Log.e("GameEnding", "myScore : " + myScore2+ ", otherScore : " + otherScore + ", closedCell : " + closedCell);
		
		// 플레이 중이던 모든 소리 정지
		
		this.myScore = myScore2;
		this.otherScore = otherScore;
		this.closedCell = closedCell;
		
		if (!GameData.share().isGuestMode) {
			myName = FacebookData.getinstance().getUserInfo().getName();
			myID = FacebookData.getinstance().getUserInfo().getId();
			myLevel = Integer.parseInt(FacebookData.getinstance().getDBData("LevelCharacter"));
			myGold = Integer.parseInt(FacebookData.getinstance().getDBData("Gold"));
			myExp = Integer.parseInt(FacebookData.getinstance().getDBData("Exp"));
			try {
				NetworkController.getInstance().sendRoomOwner(NetworkController.getInstance().guest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
		
		// 결과에 맞는 소리 재생
		// Config.getInstance().vsWin
		if (Config.getInstance().getVs()) {
			Log.e("GameEnding", "Win");
			// 승리 효과음
			// 남은 생명수로 하트 애니 출력
//			myScore = myScore2;
			mLeftExp = (int) (myScore * 1.5f);
			myGold = myScore * 5;
		} else {
			Log.e("GameEnding", "Lose");
			// 패배 효과음
//			myScore = otherScore;
			myScore = (int) (otherScore / 3.0f);
			myGold = (int) (otherScore / 10.0f);
//			decreaseScore = (int) (otherScore / 3.0f);
			decreaseGold = (int) (otherScore / 10.0f);
		}
		
		if (isExtraTime) {
			myScore = (int) (myScore2 * extraTimeReward);
			mLeftExp = (int) (myScore * 1.5f);
			myGold = myScore * 5;
		}
		
//		myScore = (int) (Math.random() * 1000) + 1;
		//test
		//myGold = 12345;
		//myExp = 3000;
		//myScore = 2123;
//		mainMenu(true);
		
		// 팝업에 표현되는 모든 숫자는 이번에 획득 또는 상실 되는 숫자들만 표현 
		mainMenu(Config.getInstance().getVs());
	}

	// 받아야되는 값
	// 적색, 청색
	// facebookID (image는 web 또는 저장된 것 호출)
	// 3가지 점수 (점수가 있을시 lv, exp 호출)
	private void mainMenu(boolean showAni) {
		Log.e("GameEnding", "showAni(vsWin) :" + showAni);
		String userColor = "";
//		int randomPoint = myScore;
		
		bg = CCSprite.sprite("00common/" + "opacitybg.png");
		this.addChild(bg);
		bg.setPosition(winSize.width / 2, winSize.height / 2);
		
		CCSprite base = CCSprite.sprite(folder + "ending-base.png");
		bg.addChild(base);
		base.setAnchorPoint(0.5f, 0.5f);
		base.setPosition(winSize.width / 2, (winSize.height / 3) * 2);
		
		addChild_Center(base, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-title.png"));
		
		CCSprite backboard = addChild_Center(base, folder + "ending-bbWin.png");
		addChild_Center(backboard, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-win.png"));
		addChild_Center(backboard, folder + "ending-winImageR.png");
		
		/*********************************************************/
		// 
		CCSprite picture = CCSprite.sprite("noPicture.png");
		backboard.addChild(picture);
		picture.setAnchorPoint(0.5f, 0.5f);
		picture.setScale(1.4f);
		picture.setPosition(240, backboard.getContentSize().height - 195);
		
		CCLabel name = CCLabel.makeLabel(myName, "Arial", 30);
		backboard.addChild(name);
		name.setAnchorPoint(0, 0.5f);
		name.setPosition(
				picture.getPosition().x + (picture.getContentSize().width * (1 - picture.getAnchorPoint().x))
				+ (picture.getScale() * 20), picture.getPosition().y);
		
		CCLabel point = CCLabel.makeLabel(String.valueOf(myScore), "Arial", 30);
		backboard.addChild(point);
		point.setAnchorPoint(1, 0.5f);
		point.setPosition(460, backboard.getContentSize().height - 280);
		
		CCLabel gold = CCLabel.makeLabel(String.valueOf(myGold), "Arial", 30);
		backboard.addChild(gold);
		gold.setAnchorPoint(1, 0.5f);
		gold.setPosition(460, backboard.getContentSize().height - 338); // 340
		
		// 획득한 경험치만 숫자로 표현(기존+획득 아님)
		exp = CCLabel.makeLabel(String.valueOf(mLeftExp), "Arial", 30);
		backboard.addChild(exp);
		exp.setAnchorPoint(1, 0.5f);
		exp.setPosition(460, backboard.getContentSize().height - 396); //400
		
		/*********************************************************/
		//현재 레벨 위치 : (현재레벨/현재레벨Max)% * 322px, 
		mExpX = (myExp/(float)UserData.expPerLevel[0]); //단위는 0 ~ 1.0
		//test : 획득 경험치는 8000이라 가정함. 
//		mLeftExp = 8000;
		
		// 경험치 바
		CCSprite expbg = null;
		// 경험치가 0일때 true로 사용할 것
//		if (myExp < 500) {
//			expbg = base;	
//		} else {
			expbg = CCSprite.sprite(folder + "ending-exp01.png");
			bg.addChild(expbg);
	//		bg.setAnchorPoint(0.5f, 0.5f);
			expbg.setPosition(
					base.getPosition().x - (base.getAnchorPoint().x * base.getContentSize().width)
					+ expbg.getAnchorPoint().x * expbg.getContentSize().width, 
					base.getPosition().y - (base.getAnchorPoint().y * base.getContentSize().height) 
					- expbg.getAnchorPoint().y * expbg.getContentSize().height
					);
			expBar = CCSprite.sprite(folder + "ending-exp02.png");
			expbg.addChild(expBar, 1);
			expBar.setAnchorPoint(1, 0.5f);
			expBar.setPosition((int)(mExpX * 322) + 172, 45); // x값 172 = 0
//			expBar.setPosition(172, 45); // x값 172 = 0
//			expBar.setPosition(494, 45);
			
			expTail = CCSprite.sprite(folder + "ending-exp03.png");
			expBar.addChild(expTail);
			expTail.setAnchorPoint(1, 0.5f);
			expTail.setPosition(
					expBar.getAnchorPoint().x * expBar.getContentSize().width, 
					expBar.getAnchorPoint().y * expBar.getContentSize().height);
			expTail.setScaleX((expBar.getPosition().x - 172) / 322);
			
			CCSprite expframe = addChild_Center(expbg, folder + "ending-exp04.png");
			
			lv = CCLabel.makeLabel("Level " + myLevel, "Arial", 36);
			expframe.addChild(lv);
			lv.setColor(ccColor3B.ccYELLOW);
			lv.setAnchorPoint(0, 0.5f);
			lv.setPosition(25, 45);
			
			expHead = CCSprite.sprite(folder + "ending-exp05.png");
			expbg.addChild(expHead, 2);
			expHead.setAnchorPoint(0.5f, 0.5f);
			expHead.setPosition(expBar.getPosition());
//		}
		
		/*********************************************************/
		// 좌측 버튼
		CCMenuItem buttonL = CCMenuItemImage.item(
				folder + "ending-button1.png",
				folder + "ending-button2.png",
				this, "clicked");
		buttonL.setUserData(myScore / 10); // GameScore 손실 대신 gold로 대체 
		CCSprite textL = CCSprite.sprite(Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-defense.png"));
		buttonL.addChild(textL);
		textL.setPosition(buttonL.getContentSize().width / 2, buttonL.getContentSize().height / 2);
		
		CCMenu leftbutton = CCMenu.menu(buttonL);
		bg.addChild(leftbutton, 2);
		leftbutton.setPosition(
				expbg.getPosition().x - (expbg.getAnchorPoint().x * expbg.getContentSize().width)
				+ leftbutton.getAnchorPoint().x * buttonL.getContentSize().width + 10, 
				expbg.getPosition().y - (expbg.getAnchorPoint().y * expbg.getContentSize().height) 
				- leftbutton.getAnchorPoint().y * buttonL.getContentSize().height - 10
				);
		
		// 우측 버튼
		CCMenuItem buttonR = CCMenuItemImage.item(
				folder + "ending-button1.png",
				folder + "ending-button2.png",
				this, "clicked");
		buttonR.setUserData(0);
		CCSprite textR = CCSprite.sprite(Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-done.png"));
		buttonR.addChild(textR);
		textR.setPosition(buttonR.getContentSize().width / 2, buttonR.getContentSize().height / 2);
		
		CCMenu rightbutton = CCMenu.menu(buttonR);
		bg.addChild(rightbutton, 3);
		rightbutton.setPosition(
				expbg.getPosition().x + ((1 - expbg.getAnchorPoint().x) * expbg.getContentSize().width)
				- rightbutton.getAnchorPoint().x * buttonR.getContentSize().width - 10,
				expbg.getPosition().y - (expbg.getAnchorPoint().y * expbg.getContentSize().height) 
				- rightbutton.getAnchorPoint().y * buttonR.getContentSize().height - 10
				);
		
		//경험치 및 레벨업 애니메이션
		//경험치 1000당 1초
		if (!GameData.share().isGuestMode && showAni) {
			schedule("expAni");
		}
	}
	
	public void expAni(float dt) {
		if(mLeftExp <= 0) {
			unschedule("expAni");
			return;
		}
		int gainedExp = (int)(dt * 1000);
		myExp += gainedExp;
		mLeftExp -= gainedExp;
		//만일 mLeftExp가 음수가 되면 보정
		if(mLeftExp < 0) {
			myExp += mLeftExp;
			mLeftExp = 0;
		}
		
		//늘어난 경험치가 현재 레벨의 최대 경험치를 넘지 않으면 애니메이션 넘으면 레벨 팝업
		if(myExp <= UserData.expPerLevel[0]) {
			//경험치 애니메이션
			mExpX = (myExp/(float)UserData.expPerLevel[0]);
			expBar.setPosition((int)(mExpX * 322) + 172, 45);
			expTail.setScaleX((expBar.getPosition().x - 172) / 322);
			expHead.setPosition(expBar.getPosition());
			exp.setString(String.valueOf(myExp));
		} else {
			//레벨 팝업
			unschedule("expAni");
			myExp = myExp - UserData.expPerLevel[0];
			final CCSprite levelUp = CCSprite.sprite("lv_up_popup/lvup.png");
			bg.addChild(levelUp, 2000);
			levelUp.setAnchorPoint(0.5f, 0.5f);
			levelUp.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height / 2);
			
			CCSprite levelUpKo = CCSprite.sprite("lv_up_popup/lvupKo.png");
			levelUp.addChild(levelUpKo, 1);
			levelUpKo.setAnchorPoint(0.5f, 0.5f);
			levelUpKo.setPosition(levelUp.getContentSize().width/2, levelUp.getContentSize().height/2);
			
			Log.e("GameEnding", "myLevel : " + myLevel);
			myLevel++; 
			basket.put("LevelCharacter", String.valueOf(myLevel)); // 레벨업(기존레벨에 +1)
			basket.put("Exp", String.valueOf(0)); // 레벨업에 따른 경험치 0으로 초기화 
			lv.setString("Level " + myLevel);
			
			//3초 후에 레벨팝업을 제거하고 다시 경험치 애니메이션 구동
			schedule(new UpdateCallback() {
				@Override
				public void update(float d) {
					unschedule(this);
					bg.removeChild(levelUp, true);
					schedule("expAni");
				}
			}, 3.0f);
		}
		
	}
	
	private CCSprite addChild_Center(CCSprite parentSprite, String childSpriteImage) {
		
		CCSprite childSprite = CCSprite.sprite(childSpriteImage);
		parentSprite.addChild(childSprite, 1);
		
		childSprite.setAnchorPoint(0.5f, 0.5f);
		childSprite.setPosition(parentSprite.getContentSize().width / 2, parentSprite.getContentSize().height / 2);
		
//		Log.e("" + childSpriteImage, "" + childSprite.getPosition()
//				+ ", " + childSprite.getAnchorPoint() + ", " + childSprite.getContentSize());
		return childSprite;
	}
	
	Map<String, String> basket;
	
	boolean buttonActive = true;
	public void clicked(Object sender) {
		MainApplication.getInstance().getActivity().click();
		int tag = (Integer) ((CCMenuItemImage)sender).getUserData(); 
		
		CCScene scene = null;
		if (GameData.share().isGuestMode) {
			scene = Home2.scene();
		} else {
			scene = Home.scene();
		}
		if (tag == 0) {
			Log.e("GameEnding", "홈으로 가기");
		} else {
			if (buttonActive) {
				myGold -= tag;
				usedGold = true;
				buttonActive = false;
			}
		}
		

		// reward
		// DB에 적용시키는 데이터들
		if (!GameData.share().isGuestMode) {
			Log.e("GameEnding", "DB : " + DataFilter.getUserDBData(FacebookData.getinstance().getUserInfo().getId()));			
			if (Config.getInstance().getVs()) { // 승리 (스코어 및 경험치, 골드, 승률 ok)
				Log.e("GameEnding", "승리 보상");
				DataFilter.addGameScore(String.valueOf(myScore));
//				basket.put("Point", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Point")) + myScore));
				basket.put("Gold", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Gold")) + myGold));	
//				basket.put("Exp", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Exp")) + mLeftExp));	
				
				// 레벨업이 안됐을시 획득한 전체 경험치를
				// 레벨업이 됐을시 레벨업후 남은 경험치를 넣어주세요.
				// 경험치 계산식은 score * 1.5입니다. 전체 경험치를 어디서 받는지 몰라서 수식으로 넣었습니다.
				basket.put("Exp", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Exp")) + (int) (myScore * 1.5f))); // 남은 경험치 
				basket.put("HistoryWin", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryWin")) + 1));	
//				basket.put("Exp",String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Exp")) + mLeftExp));	
			} else { // 패배(스코어 및 경험치, 골드, 승률 ok)
				Log.e("GameEnding", "패배 패널티");
				if (usedGold)
					basket.put("Gold", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Gold")) - myGold));					
				else
					DataFilter.addGameScore(String.valueOf(-myScore));
//					basket.put("Score", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("Score")) - decreaseScore));
				basket.put("HistoryLose", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryLose")) + 1));	
			}
			
			FacebookData.getinstance().modDBData(basket);
			Log.e("GameEnding", "DB : " + DataFilter.getUserDBData(FacebookData.getinstance().getUserInfo().getId()));
		}
		// 홈으로 갈때 DB데이터 갱신 필요
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	private boolean usedGold = false;
	
}