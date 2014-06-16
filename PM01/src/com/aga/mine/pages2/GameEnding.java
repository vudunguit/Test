﻿package com.aga.mine.pages2;

import java.io.IOException;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.cocos2d.actions.UpdateCallback;
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

import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.DataFilter;
import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.GameLoading;
import com.aga.mine.mains.GameScore;
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
	int myPastLevel = 0; //변경전 레벨
	int myCurrentLevel; //변경후 레벨
	int myScore = 0; //획득 포인트
	int myGold = 0; //획득 골드
	int myPastGold; //변경전 골드
	int myCurrentGold; //현재 골드
	int myExp = 0; //획득 경험치 
	int myCalcExp; //현재 경험치 + a : 경험치바 애니메이션용 변수
	int myPastExp; //과거 경험치
	int myCurrentExp; //현재 경험치
	float extraTimeReward = 1.25f; // 대전게임 승리자가 연장 게임을 하여서 완료시 기본 보상의 1.25배로 보상을 받음.  
	boolean isExtraTime = false;  // 현재 사용하지 않는 것으로 보임.
	
	int decreaseScore = 0; // 일반적으로 대전 패배시 스코어 감소
	int decreaseGold = 0; // 스코어 보호를 위해 스코어 대신 골드로 대신 감소(선택적)
	
	private CCLabel lv;
	private CCLabel mExpLabel;
	private CCSprite bg;
	private CCSprite expHead;
	private CCSprite expTail;
	private CCSprite expBar;
	private float mExpX; //경험치 충전 상태
	private int mLeftExp; //초기: 획득 경험치, 애니메이션이 진행됨에 따라 점점 줄어듬.
	private CCLabel mPointLabel;
	private CCLabel mGoldLabel;
	
	public int mUserLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelCharacter"));
	int otherScore;
	int closedCell;
	boolean isLocaleKo = false;
	
	private final int done = 0;
	private final int restart = 1;
	private final int defense = 2;
	final private int popupTag = 2000;
	float expPerSecond;
	
	CCMenu leftbutton;
	
	public GameEnding(int myScore, int otherScore, int closedCell) {
		basket = new HashMap<String, String>();
		if (Locale.getDefault().getLanguage().toString().equals("ko"))
			isLocaleKo = true;
		Log.e("GameEnding", "myScore : " + myScore+ ", otherScore : " + otherScore + ", closedCell : " + closedCell);
		
		// 플레이 중이던 모든 소리 정지
		
		this.myScore = myScore;
		this.otherScore = otherScore;
		this.closedCell = closedCell;
		
		if (!GameData.share().isGuestMode) {
			myName = FacebookData.getinstance().getUserInfo().getName();
			myID = FacebookData.getinstance().getUserInfo().getId();
			myPastLevel = Integer.parseInt(FacebookData.getinstance().getDBData("LevelCharacter"));
			myPastGold = Integer.parseInt(FacebookData.getinstance().getDBData("Gold"));
			myPastExp = Integer.parseInt(FacebookData.getinstance().getDBData("Exp"));
			try {
				NetworkController.getInstance().sendRoomOwner(NetworkController.getInstance().GUEST);
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
//					myScore = myScore2;
			myExp = (int) (myScore * 1.5f);
			myGold = (int)(myScore / 5.0f); //기존 곱하기 5로된 오류가 있음.
			if( (myPastExp + myExp) >= UserData.expPerLevel[myPastLevel-1]) {
				myCurrentLevel = myPastLevel + 1;
				myCurrentExp = (myPastExp + myExp) - UserData.expPerLevel[myPastLevel-1];
			} else {
				myCurrentLevel = myPastLevel;
				myCurrentExp = myPastExp + myExp;
			}
			myCurrentGold = myPastGold + myGold;
		} else {
			Log.e("GameEnding", "Lose");
			// 패배 효과음
			if(otherScore > 0) {  //멀티게임 패배
				this.myScore = (int) (otherScore / 3.0f); //차감 포인트
				decreaseScore = (int) (otherScore / 3.0f);
				myGold = (int) (otherScore / 10.0f); //차감 골드
				decreaseGold = (int) (otherScore / 10.0f);
			} else { //싱글게임 패배
				myScore = 0;
				myGold = 0;
				myExp = 0;
			}
		}
		
		if (isExtraTime) {
			this.myScore = (int) (myScore * extraTimeReward);
			myExp = (int) (this.myScore * 1.5f);
			myGold = (int)(this.myScore / 5.0f);
		}
	}
	
	public void startGameOver() {
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
		bg.setPosition(winSize.width / 2, winSize.height / 2);
		this.addChild(bg);
		
		CCSprite base = CCSprite.sprite(folder + "ending-base.png");
		base.setAnchorPoint(0.5f, 0.5f);
		base.setPosition(winSize.width / 2, (winSize.height / 3) * 2);
		bg.addChild(base);
		
		addChild_Center(base, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-title.png"));
		/***************************** background image ****************************/
		String magicianColor = null;
		if (Config.getInstance().getOwner()) {
			magicianColor = "R";
		} else {
			magicianColor = "B";
		}
		Log.e("GameEnding", "isOwner : " + magicianColor);	
		CCSprite backboard = null;
		if (showAni) { // true == 승리시
			backboard = addChild_Center(base, folder + "ending-bbWin.png");
			addChild_Center(backboard, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-win.png")); // 싱글 더블 승리 이미지
			addChild_Center(backboard, folder + "ending-winImage" + magicianColor +".png");
		} else {
			backboard = addChild_Center(base, folder + "ending-bbLose.png");
			if (GameData.share().isMultiGame) {
				addChild_Center(backboard, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-lose.png"));     // 더블모드 패배 이미지	
			} else {
				addChild_Center(backboard, Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-loseS.png"));	// 싱글모드 패배 이미지
			}
			addChild_Center(backboard, folder + "ending-loseImage" + magicianColor +".png");
		}
		
		
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
		
		mPointLabel = CCLabel.makeLabel(String.valueOf(0), "Arial", 30);
		mPointLabel.setAnchorPoint(1, 0.5f);
		mPointLabel.setPosition(460, backboard.getContentSize().height - 280);
		backboard.addChild(mPointLabel);
		
		CGPoint goldTextPosition = CGPoint.ccp(460, backboard.getContentSize().height - 338);
		if (GameData.share().isMultiGame && !showAni) {
			goldTextPosition = CGPoint.ccp(goldTextPosition.x, backboard.getContentSize().height - 347);
		}
		mGoldLabel = CCLabel.makeLabel(String.valueOf(0), "Arial", 30);
		mGoldLabel.setAnchorPoint(1, 0.5f);
		mGoldLabel.setPosition(goldTextPosition); // 340
		backboard.addChild(mGoldLabel);
		
		CCSprite goldImage = CCSprite.sprite(folder + "gold.png");
		goldImage.setPosition(0- goldImage.getContentSize().width*0.5f, mGoldLabel.getContentSize().height*0.45f);
		goldImage.setAnchorPoint(1, 0.5f);
		goldImage.setScale(0.8f);
		mGoldLabel.addChild(goldImage);
		
		// 획득한 경험치만 숫자로 표현(기존+획득 아님)
		mExpLabel = CCLabel.makeLabel(String.valueOf(0), "Arial", 30);
		mExpLabel.setAnchorPoint(1, 0.5f);
		mExpLabel.setPosition(460, backboard.getContentSize().height - 396); //400
		if (showAni || !GameData.share().isMultiGame) {
			backboard.addChild(mExpLabel);
		}
		
		//패배 팝업이면
		if(!showAni && otherScore>0) {
			mExpLabel.setString(String.valueOf(0));
			mPointLabel.setString(String.valueOf(-myScore));
			mGoldLabel.setString(String.valueOf(0));
		}
		
		/*************************** expframe ******************************/
		//현재 레벨 위치 : (현재레벨/현재레벨Max)% * 322px, 
		mExpX = (myPastExp/(float)UserData.expPerLevel[myPastLevel-1]); //단위는 0 ~ 1.0
		//test : 획득 경험치는 10000이라 가정함. 
		mLeftExp = myExp;
		myCalcExp = myPastExp;
		expPerSecond = mLeftExp / 2.0f;
		
		// 경험치 바
		CCSprite expbg = null;
		
		// 이번게임에서 획득한 경험치가 없을때 true
		if (myExp <= 0) {
			expbg = base;	
		} else {
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
			
			lv = CCLabel.makeLabel("Level " + myPastLevel, "Arial", 36);
			lv.setColor(ccColor3B.ccYELLOW);
			lv.setAnchorPoint(0, 0.5f);
			lv.setPosition(25, 45);
			expframe.addChild(lv);
			
			expHead = CCSprite.sprite(folder + "ending-exp05.png");
			expHead.setAnchorPoint(0.5f, 0.5f);
			expHead.setPosition(expBar.getPosition());
			expbg.addChild(expHead, 2);	
		}
		/**************************** buttons *****************************/

		//좌측 버튼
		String buttonText = "ending-restart";
		int leftButtonTag = restart;
		CCMenuItem buttonL = CCMenuItemImage.item(
				folder + "ending-button1.png",
				folder + "ending-button2.png",
				this, "clicked");
		if (otherScore > 0 && !showAni) { //대전 게임 패배일 경우
			Log.d("LDK", "multigame fail");
			buttonText = "ending-defense";
			leftButtonTag = defense;
			buttonL = CCMenuItemImage.item(
					folder + "ending-button1.png",
					folder + "ending-button2.png",
					this, "defenceClicked");
		}

		buttonL.setTag(leftButtonTag);
		buttonL.setUserData(myScore / 10); // GameScore 손실 대신 gold로 대체
		
		CCSprite textL = CCSprite.sprite(Utility.getInstance().getNameWithIsoCodeSuffix(folder + buttonText +".png"));
		buttonL.addChild(textL);
		textL.setPosition(buttonL.getContentSize().width / 2, buttonL.getContentSize().height / 2);
		
		leftbutton = CCMenu.menu(buttonL);
		bg.addChild(leftbutton, 2);
		leftbutton.setPosition(
				expbg.getPosition().x - (expbg.getAnchorPoint().x * expbg.getContentSize().width)
				+ buttonL.getAnchorPoint().x * buttonL.getContentSize().width + 20, 
				expbg.getPosition().y - (expbg.getAnchorPoint().y * expbg.getContentSize().height) 
				- buttonL.getAnchorPoint().y * buttonL.getContentSize().height - 10
				);

			
		// 우측 버튼
		CCMenuItem buttonR = CCMenuItemImage.item(
				folder + "ending-button1.png",
				folder + "ending-button2.png",
				this, "clicked");
		buttonR.setTag(done);
		buttonR.setUserData(0);
		CCSprite textR = CCSprite.sprite(Utility.getInstance().getNameWithIsoCodeSuffix(folder + "ending-done.png"));
		buttonR.addChild(textR);
		textR.setPosition(buttonR.getContentSize().width / 2, buttonR.getContentSize().height / 2);
		
		CCMenu rightbutton = CCMenu.menu(buttonR);
		bg.addChild(rightbutton, 3);
		CGPoint rightButtonPosition = CGPoint.ccp(				
				expbg.getPosition().x + ((1 - expbg.getAnchorPoint().x) * expbg.getContentSize().width)
				- buttonR.getAnchorPoint().x * buttonR.getContentSize().width - 20,
				expbg.getPosition().y - (expbg.getAnchorPoint().y * expbg.getContentSize().height) 
				- buttonR.getAnchorPoint().y * buttonR.getContentSize().height - 10);

//		if (showAni) {
//			rightButtonPosition = CGPoint.ccp(
//					expbg.getPosition().x + ((0.5f - expbg.getAnchorPoint().x) * expbg.getContentSize().width)
//					+ ((rightbutton.getAnchorPoint().x - 0.5f) * buttonR.getContentSize().width), // <-- + 인지 -인지 다시 계산할것
//					rightButtonPosition.y);
//		}		
		rightbutton.setPosition(rightButtonPosition);
		
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
//		int gainedExp = (int)(dt * 50);
		int gainedExp = (int)(dt * expPerSecond);
		myCalcExp += gainedExp;
		mLeftExp -= gainedExp;
		//만일 mLeftExp가 음수가 되면 보정
		if(mLeftExp < 0) {
			myCalcExp += mLeftExp;
			mLeftExp = 0;
			mExpLabel.setString(String.valueOf(myExp));
			mPointLabel.setString(String.valueOf(myScore));
			mGoldLabel.setString(String.valueOf(myGold));
		}
		
		//늘어난 경험치가 현재 레벨의 최대 경험치를 넘지 않으면 애니메이션 넘으면 레벨 팝업
		if(myCalcExp <= UserData.expPerLevel[mUserLevel-1]) {
			//경험치 애니메이션
			mExpX = (myCalcExp/(float)UserData.expPerLevel[myPastLevel-1]);
			expBar.setPosition((int)(mExpX * 322) + 172, 45);
			expTail.setScaleX((expBar.getPosition().x - 172) / 322);
			expHead.setPosition(expBar.getPosition());
			mExpLabel.setString(String.valueOf(myExp - mLeftExp));
			mPointLabel.setString(String.valueOf((int)((myExp - mLeftExp)/1.5f)));
			mGoldLabel.setString(String.valueOf((int)((myExp - mLeftExp)/1.5f/5f)));
		} else {
			//레벨 팝업
			unschedule("expAni");
			myCalcExp = myCalcExp - UserData.expPerLevel[myPastLevel-1];
			
			// 이게 무슨 막코드인가.. 으으.. OTL
			String[] levelUpCommentsEn = {"Level up ","Level up  rewards","G  o  l  d","Attack upgrade","Defenses upgrade","1 sec"};
			String[] levelUpCommentsKo = {"레벨 달성","레벨업 보상","골        드","공격마법","방어마법","1초 증가"};
			int fontLarge = (int)(26 * 2.8f);
			int fontSmall = (int)(19 * 2f);
			float leftMargin = 152;
			float centerTextPosition = 396;
			float rightMargin = 512;
			float enScaleX = 0.6f;

//			final CCSprite levelUp = CCSprite.sprite("lv_up_popup/lvup.png"); // old
			final CCSprite levelUp = CCSprite.sprite("lv_up_popup/ending-lvupbb.png");
			levelUp.setAnchorPoint(0.5f, 0.5f);
			levelUp.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height / 2);
			bg.addChild(levelUp, popupTag, popupTag);
			
			String[] popupText = null;  
			if (isLocaleKo) {
				popupText = levelUpCommentsKo;
				centerTextPosition = 313;
			} else {
				popupText = levelUpCommentsEn;
			}
				
			CCLabel LevelupTitle = CCLabel.makeLabel(popupText[0] + (myPastLevel+1) , "Arial", fontLarge);
			LevelupTitle.setPosition(levelUp.getContentSize().width*0.5f, levelUp.getContentSize().height - 365);
			LevelupTitle.setColor(ccColor3B.ccBLACK);
			LevelupTitle.setOpacity(204);
			
			CCLabel reward = CCLabel.makeLabel(popupText[1], "Arial", fontSmall);
			reward.setPosition(LevelupTitle.getPosition().x, levelUp.getContentSize().height - 483);
			reward.setColor(ccColor3B.ccBLACK);
			reward.setOpacity(204);
			
			
			CCLabel gold1 = CCLabel.makeLabel(popupText[2], "Arial", fontSmall);
			gold1.setPosition(leftMargin, levelUp.getContentSize().height - 535);
			gold1.setAnchorPoint(0, 0.5f);
			gold1.setColor(ccColor3B.ccBLACK);
			gold1.setOpacity(204);
			
			CCLabel gold2 = CCLabel.makeLabel(":", "Arial", fontSmall);
			gold2.setPosition(centerTextPosition, gold1.getPosition().y);
			gold2.setColor(ccColor3B.ccBLACK);
			gold2.setOpacity(204);
			
			CCLabel gold3 = CCLabel.makeLabel(String.valueOf(myCurrentGold), "Arial", fontSmall);
			gold3.setPosition(rightMargin, gold1.getPosition().y);
			gold3.setAnchorPoint(1, 0.5f);
			gold3.setColor(ccColor3B.ccBLACK);
			gold3.setOpacity(204);
			
			CCLabel attack1 = CCLabel.makeLabel(popupText[3], "Arial", fontSmall);
			attack1.setPosition(gold1.getPosition().x, levelUp.getContentSize().height - 585);
			attack1.setAnchorPoint(gold1.getAnchorPoint());
			attack1.setColor(ccColor3B.ccBLACK);
			attack1.setOpacity(204);
			
			CCLabel attack2 = CCLabel.makeLabel(":", "Arial", fontSmall);
			attack2.setPosition(gold2.getPosition().x, attack1.getPosition().y);
			attack2.setColor(ccColor3B.ccBLACK);
			attack2.setOpacity(204);
			
			CCLabel attack3 = CCLabel.makeLabel(popupText[5], "Arial", fontSmall);
			attack3.setPosition(gold3.getPosition().x, attack1.getPosition().y);
			attack3.setAnchorPoint(gold3.getAnchorPoint());
			attack3.setColor(ccColor3B.ccBLACK);
			attack3.setOpacity(204);
			
			
			CCLabel defense1 = CCLabel.makeLabel(popupText[4], "Arial", fontSmall);
			defense1.setPosition(gold1.getPosition().x,levelUp.getContentSize().height - 635);
			defense1.setAnchorPoint(gold1.getAnchorPoint());
			defense1.setColor(ccColor3B.ccBLACK);
			defense1.setOpacity(204);
			
			CCLabel defense2 = CCLabel.makeLabel(":", "Arial", fontSmall);
			defense2.setPosition(gold2.getPosition().x,defense1.getPosition().y);
			defense2.setColor(ccColor3B.ccBLACK);
			defense2.setOpacity(204);
			
			CCLabel defense3 = CCLabel.makeLabel(popupText[5], "Arial", fontSmall);
			defense3.setPosition(gold3.getPosition().x,defense1.getPosition().y);
			defense3.setAnchorPoint(gold3.getAnchorPoint());
			defense3.setColor(ccColor3B.ccBLACK);
			defense3.setOpacity(204);
			
			CCMenuItem okButton = CCMenuItemImage.item(
					Utility.getInstance().getNameWithIsoCodeSuffix("lv_up_popup/ending-ok1.png"),
					Utility.getInstance().getNameWithIsoCodeSuffix("lv_up_popup/ending-ok2.png"),
					this, "popupButton");
			
			CCMenu ok = CCMenu.menu(okButton);
			ok.setPosition(levelUp.getContentSize().width*0.5f, levelUp.getContentSize().height - 754);
			
			
			if (isLocaleKo) {
				LevelupTitle.setString((myPastLevel+1) + popupText[0]);
			} else {
				gold1.setScaleX(enScaleX);
				gold3.setScaleX(enScaleX);
				attack1.setScaleX(enScaleX);
				attack3.setScaleX(enScaleX);
				defense1.setScaleX(enScaleX);
				defense3.setScaleX(enScaleX);
			}
			
			levelUp.addChild(LevelupTitle);
			levelUp.addChild(reward);
			levelUp.addChild(gold1);
			levelUp.addChild(gold2);
			levelUp.addChild(gold3);
			levelUp.addChild(attack1);
			levelUp.addChild(attack2);
			levelUp.addChild(attack3);
			levelUp.addChild(defense1);
			levelUp.addChild(defense2);
			levelUp.addChild(defense3);
			levelUp.addChild(ok);
			
//			CCSprite levelUpKo = CCSprite.sprite("lv_up_popup/lvupKo.png");
//			levelUp.addChild(levelUpKo, 1);
//			levelUpKo.setAnchorPoint(0.5f, 0.5f);
//			levelUpKo.setPosition(levelUp.getContentSize().width/2, levelUp.getContentSize().height/2);
			
//			Log.e("GameEnding", "myLevel : " + myLevel);
//			myLevel++; 
			//basket.put("LevelCharacter", String.valueOf(myLevel)); // 레벨업(기존레벨에 +1)
			//basket.put("Exp", String.valueOf(0)); // 레벨업에 따른 경험치 0으로 초기화 
			lv.setString("Level " + myCurrentLevel);
			
//			//3초 후에 레벨팝업을 제거하고 다시 경험치 애니메이션 구동
//			schedule(new UpdateCallback() {
//				@Override
//				public void update(float d) {
//					unschedule(this);
//					bg.removeChild(levelUp, true);
//					schedule("expAni");
//				}
//			}, 3.0f);
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
	
	
	// DB 저장값들 clicked 되기전으로 전부 옮겨야 됩니다.
	// 1-1. 획득 또는 손실되는 값부터 DB에 저장 (기본 point 차감)
	// 1-2. gamePoint defense시 point회복 및 골드 차감 실행
	// 2. restart 또는 done 체크하여 해당위치로 scene 이동. 
	boolean buttonActive = true;
	public void clicked(Object sender) {
		MainApplication.getInstance().getActivity().click();
		int userdata = (Integer) ((CCMenuItemImage)sender).getUserData(); 
		int tag = ((CCNode)sender).getTag();
		
		CCScene scene = null;
		
		if (tag == restart) {
			scene = GameLoading.scene();
		} else {
			if (GameData.share().isGuestMode)
				scene = Home2.scene();
			else
				scene = Home.scene();
		}
		
		if (tag ==  done) {
			Log.e("GameEnding", "그냥 종료");
		} else if (tag ==  defense) {
			if (buttonActive) {
				myGold -= userdata;
				//usedGold = true;
				buttonActive = false;
			}
		}
		
		
		// 홈으로 갈때 DB데이터 갱신 필요
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void saveGameOver() {
		// reward
		// DB에 적용시키는 데이터들
		if (!GameData.share().isGuestMode) {
			Log.e("GameEnding", "DB : " + DataFilter.getUserDBData(FacebookData.getinstance().getUserInfo().getId()));			
			if (Config.getInstance().getVs()) { // 승리 (스코어 및 경험치, 골드, 승률 ok)
				Log.e("GameEnding", "승리 보상");
				DataFilter.addGameScore(String.valueOf(myScore));
				
				basket.put("Gold", String.valueOf(myCurrentGold));	
				basket.put("Exp", String.valueOf(myCurrentExp)); // 남은 경험치 
				basket.put("LevelCharacter", String.valueOf(myCurrentLevel));
				
				//멀티게임일 경우만 전적 반영
				if(otherScore > 0) {
					basket.put("HistoryWin", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryWin")) + 1));
				}
				
				FacebookData.getinstance().modDBData(basket);
				
				//홈화면에 포인트 갱신 (서버에 저장하지는 않고 로컬만 업데이트)
				List<GameScore> gameScores = FacebookData.getinstance().getGameScore();
				for (GameScore gameScore : gameScores) {
					if (gameScore.getId().equals(myID)) {
						Log.d("LDK", "score:" + gameScore);
						gameScore.score = myScore + gameScore.score;
					}
				}
			} else { // 패배(스코어 및 경험치, 골드, 승률 ok)
				Log.e("GameEnding", "패배 ");

				if(otherScore > 0) {  //멀티게임 패배
					int mPastScore = Integer.valueOf(FacebookData.getinstance().getDBData("Point"));
					if (mPastScore < myScore) {
						myScore = mPastScore;
					} 
					DataFilter.addGameScore(String.valueOf(-myScore));
					
					basket.put("HistoryLose", String.valueOf(Integer.parseInt(FacebookData.getinstance().getDBData("HistoryLose")) + 1));	
					
					FacebookData.getinstance().modDBData(basket);
				} else { //싱글게임 패배
					
				}
			}
		}
	}
	
	public void defenceClicked(Object sender) {
		MainApplication.getInstance().getActivity().click();
		
		mPointLabel.setString(String.valueOf(0));
		mGoldLabel.setString(String.valueOf(-decreaseGold));
		
		//버튼 비활성화
		leftbutton.setIsTouchEnabled(false);
		
		//서버 정보 전송
		DataFilter.addGameScore(String.valueOf(decreaseScore));
		
		basket.put("Gold", String.valueOf(myPastGold-decreaseGold));
		FacebookData.getinstance().modDBData(basket);
		
		//홈화면에 포인트 갱신 (서버에 저장하지는 않고 로컬만 업데이트)
		List<GameScore> gameScores = FacebookData.getinstance().getGameScore();
		for (GameScore gameScore : gameScores) {
			if (gameScore.getId().equals(myID)) {
				Log.d("LDK", "score:" + gameScore);
				gameScore.score = myScore + gameScore.score;
			}
		}
	}
	
	//private boolean usedGold = false;
	
	public void popupButton(Object sender) {
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				unschedule(this);
				bg.removeChildByTag(popupTag,  true);
				schedule("expAni");
			}
		}, 0);
	}
	
}