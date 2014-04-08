package com.aga.mine.pages2;

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

import com.aga.mine.mains.Home;
import com.aga.mine.mains.Utility;

public class GameEnding extends CCLayer {

	String folder = "70game_ending/";
	
	CGSize winSize = CCDirector.sharedDirector().winSize();

//	public static CCScene scene() {
//		CCScene scene = CCScene.node();
////		GameEnding2 layer = new GameEnding();
//		CCLayer layer = new GameEnding();
//		scene.addChild(layer);
//		return scene;
//	}
	public static CCLayer layer() {
		CCLayer layer = new GameEnding();
		return layer;
	}

	public GameEnding() {
		mainMenu();
		setpoint();
	}

	// 받아야되는 값
	// 적색, 청색
	// facebookID (image는 web 또는 저장된 것 호출)
	// 3가지 점수 (점수가 있을시 lv, exp 호출)
	private void mainMenu() {
		
		String userColor = "";
		int facebookID = 1231251551;
		int randomPoint = (int) (Math.random() * 1000) + 1;
		int[] values = {randomPoint, (int)(randomPoint / 5.0f), (int)(randomPoint * 1.5f)};
		
		CCSprite bg = CCSprite.sprite("00common/" + "opacitybg.png");
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
		CCSprite picture = CCSprite.sprite("00common/" + "noPicture.png");
		backboard.addChild(picture);
		picture.setAnchorPoint(0.5f, 0.5f);
		picture.setScale(1.4f);
		picture.setPosition(240, backboard.getContentSize().height - 195);
		
		CCLabel name = CCLabel.makeLabel("홍길동", "Arial", 30);
		backboard.addChild(name);
		name.setAnchorPoint(0, 0.5f);
		name.setPosition(
				picture.getPosition().x + (picture.getContentSize().width * (1 - picture.getAnchorPoint().x))
				+ (picture.getScale() * 20), picture.getPosition().y);
		
		CCLabel point = CCLabel.makeLabel(values[0] + " ", "Arial", 30);
		backboard.addChild(point);
		point.setAnchorPoint(1, 0.5f);
		point.setPosition(460, backboard.getContentSize().height - 280);
		
		CCLabel gold = CCLabel.makeLabel(values[1] + " ", "Arial", 30);
		backboard.addChild(gold);
		gold.setAnchorPoint(1, 0.5f);
		gold.setPosition(460, backboard.getContentSize().height - 338); // 340
		
		CCLabel exp = CCLabel.makeLabel(values[2] + " ", "Arial", 30);
		backboard.addChild(exp);
		exp.setAnchorPoint(1, 0.5f);
		exp.setPosition(460, backboard.getContentSize().height - 396); //400
		
		/*********************************************************/
		// 경험치 바
		CCSprite expbg = null;
		// 경험치가 0일때 true로 사용할 것
		if (values[2] < 500) {
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
			CCSprite expBar = CCSprite.sprite(folder + "ending-exp02.png");
			expbg.addChild(expBar, 1);
			expBar.setAnchorPoint(1, 0.5f);
			expBar.setPosition((int)(Math.random() * 322) + 172, 45); // x값 172 = 0
//			expBar.setPosition(172, 45); // x값 172 = 0
//			expBar.setPosition(494, 45);
			
			CCSprite expTail = CCSprite.sprite(folder + "ending-exp03.png");
			expBar.addChild(expTail);
			expTail.setAnchorPoint(1, 0.5f);
			expTail.setPosition(
					expBar.getAnchorPoint().x * expBar.getContentSize().width, 
					expBar.getAnchorPoint().y * expBar.getContentSize().height);
			expTail.setScaleX((expBar.getPosition().x - 172) / 322);
			
			CCSprite expframe = addChild_Center(expbg, folder + "ending-exp04.png");
			
			CCLabel lv = CCLabel.makeLabel("Level " + 99, "Arial", 36);
			expframe.addChild(lv);
			lv.setColor(ccColor3B.ccYELLOW);
			lv.setAnchorPoint(0, 0.5f);
			lv.setPosition(25, 45);
			
			CCSprite expHead = CCSprite.sprite(folder + "ending-exp05.png");
			expbg.addChild(expHead, 2);
			expHead.setAnchorPoint(0.5f, 0.5f);
			expHead.setPosition(expBar.getPosition());
		}
		
		/*********************************************************/
		// 좌측 버튼
		CCMenuItem buttonL = CCMenuItemImage.item(
				folder + "ending-button1.png",
				folder + "ending-button2.png",
				this, "clicked");
		buttonL.setUserData(values[2] / 10);
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
	
	
	int gamePoint = 1000;
	CCLabel myPoint = null;
	private void setpoint() {
		myPoint = CCLabel.makeLabel("Gold : " + gamePoint, "Arial", 30);
		this.addChild(myPoint, 2);
		myPoint.setPosition(winSize.width / 2, (winSize.height / 5) * 4);
	}

	boolean buttonActive = true;
	public void clicked(Object sender) {
		int value = (Integer) ((CCMenuItemImage)sender).getUserData(); 
		
		if (value == 0) {
			CCScene scene = Home.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		} else {
			if (buttonActive) {
				gamePoint -= value;
				myPoint.setString("Gold : " + gamePoint);
				buttonActive = false;
			}
		}
	}
}