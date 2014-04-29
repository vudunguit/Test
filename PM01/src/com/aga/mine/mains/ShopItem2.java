package com.aga.mine.mains;

import java.util.List;

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
import org.cocos2d.types.ccColor3B;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.pages.UserData;

public class ShopItem2 extends CCLayer {

	final String commonfolder = "00common/";
	final String folder = "22item/";
	final String fileExtension = ".png";
	
	CCSprite bg;
	CCSprite bb;
	
	CCSprite itemOver = CCSprite.sprite(folder + "item-buttoncover" + fileExtension);
	private Context mContext;
	UserData userData;
	
	int attackAttribute = 1;
	int defenseAttribute = 1;
	int price ;
	
	
	CCLabel  sphereNumber1;
	CCLabel  sphereNumber2;
	CCLabel  offenseAttributeSelected1;
	CCLabel  offenseAttributeSelected2;
	CCLabel  defenseAttributeSelected1;
	CCLabel  defenseAttributeSelected2;

	CCLabel  levelFire;
	CCLabel  levelWind;
	CCLabel  levelCloud;
	CCLabel  levelDivine;
	CCLabel  levelEarth;
	CCLabel  levelMirror;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopItem2();
		scene.addChild(layer);
		return scene;
	}

	public ShopItem2() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(folder + "item-backboard" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);
		
		itemOver.setAnchorPoint(0,0);
		this.setIsTouchEnabled(true);
	}

	private void setBackBoardMenu(String imageFullPath) {
		bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
	}
	
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle6.setTitle(boardFrame, folder);
	}
	
	
	private void setMainMenu(CCSprite parentSprite){
			
			setText(parentSprite);
			
		CCMenuItem itemSphere = CCMenuItemImage.item(
				folder + "item-buttonBuy-hd" + fileExtension,
				folder + "item-buttonBuy-select-hd" + fileExtension,
				this, "SphereCallback");
		itemSphere.setUserData("Sphere");
		
		CCMenuItem itemOffenseBuy = CCMenuItemImage.item(
				folder + "item-buttonBuy-hd" + fileExtension,
				folder + "item-buttonBuy-select-hd" + fileExtension,
				this, "offenseCallback");
		itemOffenseBuy.setUserData("Offense");
		
		CCMenuItem itemDefenseBuy = CCMenuItemImage.item(
				folder + "item-buttonBuy-hd" + fileExtension,
				folder + "item-buttonBuy-select-hd" + fileExtension,
				this, "offenseCallback");
		itemDefenseBuy.setUserData("Defense");
		
		CCMenuItem o_fire = CCMenuItemImage.item(
				folder + "item-buttonFire-hd" + fileExtension,
				folder + "item-buttonFire-select-hd" + fileExtension,
				this, "buttonCallback");		
		o_fire.setUserData(0);
		o_fire.addChild(itemOver);
		o_fire.setIsEnabled(false);
		
		CCMenuItem o_wind = CCMenuItemImage.item(
				folder + "item-buttonWind-hd" + fileExtension,
				folder + "item-buttonWind-select-hd" + fileExtension,
				this, "buttonCallback");
		o_wind.setUserData(1);
		
		CCMenuItem o_cloud = CCMenuItemImage.item(
				folder + "item-buttonCloud-hd" + fileExtension,
				folder + "item-buttonCloud-select-hd" + fileExtension,
				this, "buttonCallback");
		o_cloud.setUserData(2);
		
		CCMenuItem d_fire = CCMenuItemImage.item(
				folder + "item-buttonFire-hd" + fileExtension,
				folder + "item-buttonFire-select-hd" + fileExtension,
				this, "buttonCallback");		
		d_fire.setUserData(3);
		d_fire.addChild(itemOver);
		d_fire.setIsEnabled(false);
		
		CCMenuItem d_wind = CCMenuItemImage.item(
				folder + "item-buttonWind-hd" + fileExtension,
				folder + "item-buttonWind-select-hd" + fileExtension,
				this, "buttonCallback");
		d_wind.setUserData(4);
		
		CCMenuItem d_cloud = CCMenuItemImage.item(
				folder + "item-buttonCloud-hd" + fileExtension,
				folder + "item-buttonCloud-select-hd" + fileExtension,
				this, "buttonCallback");
		d_cloud.setUserData(5);
		
		CCMenu spherebuyMenu = CCMenu.menu(itemSphere);
		CCMenu offensebuyMenu = CCMenu.menu(itemOffenseBuy);
		CCMenu defensebuyMenu = CCMenu.menu(itemDefenseBuy);
		CCMenu offenseMenu = CCMenu.menu(o_fire, o_wind, o_cloud);
		CCMenu defenseMenu = CCMenu.menu(d_fire, d_wind, d_cloud);
		parentSprite.addChild(spherebuyMenu);
		parentSprite.addChild(offensebuyMenu);
		parentSprite.addChild(defensebuyMenu);
		parentSprite.addChild(offenseMenu);
		parentSprite.addChild(defenseMenu);

		offenseMenu.alignItemsHorizontally(10);
		defenseMenu.alignItemsHorizontally(10);
		
		spherebuyMenu.setPosition(
				465 - 44, parentSprite.getContentSize().height - 109); // 수정 구매
		
		offensebuyMenu.setPosition(
				465 - 44, parentSprite.getContentSize().height - 208); // 공격아이템 구매
		
		defensebuyMenu.setPosition(
				465 - 44, parentSprite.getContentSize().height - 404); // 방어아이템 구매
		
		offenseMenu.setAnchorPoint(0, 0);
		offenseMenu.setPosition(
				65 + o_fire.getContentSize().width * 1.5f,
				587 - 343 + o_fire.getContentSize().height / 2); // 공격 속성 메뉴
		
		defenseMenu.setAnchorPoint(0, 0);
		defenseMenu.setPosition(
				65 + o_fire.getContentSize().width * 1.5f,
				587 - 538 + o_fire.getContentSize().height / 2); // 방어 속성 메뉴
		
		CCSprite buyText = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "item-textBuy" + fileExtension));
		buyText.setPosition(itemSphere.getContentSize().width/2, itemSphere.getContentSize().height / 2);
//		buyText.setAnchorPoint(0.5f, 0.5f);
		itemSphere.addChild(buyText);
		itemOffenseBuy.addChild(buyText);
		itemDefenseBuy.addChild(buyText);
		
		levelFire = CCLabel.makeLabel("Lv" + FacebookData.getinstance().getDBData("LevelFire"), "Arial", 20);
		levelFire.setColor(ccColor3B.ccYELLOW);
		levelFire.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelFire.setAnchorPoint(1, 1);
		o_fire.addChild(levelFire);
		
		levelWind = CCLabel.makeLabel("Lv"+FacebookData.getinstance().getDBData("LevelWind"), "Arial", 20);
		levelWind.setColor(ccColor3B.ccYELLOW);
		levelWind.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelWind.setAnchorPoint(1, 1);
		o_wind.addChild(levelWind);				
		
		levelCloud = CCLabel.makeLabel("Lv"+FacebookData.getinstance().getDBData("LevelCloud"), "Arial", 20);
		levelCloud.setColor(ccColor3B.ccYELLOW);
		levelCloud.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelCloud.setAnchorPoint(1, 1);
		o_cloud.addChild(levelCloud);				
		
		levelDivine = CCLabel.makeLabel("Lv"+FacebookData.getinstance().getDBData("LevelDivine"), "Arial", 20);
		levelDivine.setColor(ccColor3B.ccYELLOW);
		levelDivine.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelDivine.setAnchorPoint(1, 1);
		d_fire.addChild(levelDivine);						
		
		levelEarth = CCLabel.makeLabel("Lv"+FacebookData.getinstance().getDBData("LevelEarth"), "Arial", 20);
		levelEarth.setColor(ccColor3B.ccYELLOW);
		levelEarth.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelEarth.setAnchorPoint(1, 1);
		d_wind.addChild(levelEarth);					
		
		levelMirror = CCLabel.makeLabel("Lv"+FacebookData.getinstance().getDBData("LevelMirror"), "Arial", 20);
		levelMirror.setColor(ccColor3B.ccYELLOW);
		levelMirror.setPosition(CGPoint.make(o_fire.getContentSize().width - 12, o_fire.getContentSize().height - 7));
		levelMirror.setAnchorPoint(1, 1);
		d_cloud.addChild(levelMirror);						
	}
		
	
//	//배경 그림 설정 (frameMatching)
	private void setText(CCSprite parentSprite){
		
		CCLabel gold1 = CCLabel.makeLabel("Gold", "Arial", 25);
		gold1.setColor(ccColor3B.ccYELLOW);
		gold1.setAnchorPoint(0, 0.5f);
		gold1.setPosition(190, parentSprite.getContentSize().height - 90);
		
		int sphereNumber = Integer.parseInt(FacebookData.getinstance().getDBData("SphereNumber"));
		if (sphereNumber < 3) {
			FacebookData.getinstance().modDBData("SphereNumber", "3");
			sphereNumber = 3;
		}
		if (sphereNumber < 9) {
			int spherePrice = userData.buySphere[sphereNumber - 3];	
			sphereNumber1 = CCLabel.makeLabel(
					new NumberComma().numberComma(spherePrice), "Arial", 30);
		} else {
			sphereNumber1 = CCLabel.makeLabel("MAX", "Arial", 30);
			sphereNumber1.setColor(ccColor3B.ccYELLOW);
		}
		sphereNumber1.setAnchorPoint(1, 1);
		sphereNumber1.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 63 - 8);
		
		sphereNumber2 = CCLabel.makeLabel(
				"보유 정령병 :  " + new NumberComma().numberComma(sphereNumber), "Arial", 25);
		sphereNumber2.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 152 + 8);
		sphereNumber2.setAnchorPoint(1, 0);
		
		parentSprite.addChild(gold1);	
		parentSprite.addChild(sphereNumber1);	
		parentSprite.addChild(sphereNumber2);	

		
		/********************* 공격 *************************/
		CCSprite attackItem = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "item-labelAttack" + fileExtension));
		attackItem.setPosition(110, parentSprite.getContentSize().height - 190);
		parentSprite.addChild(attackItem);		
		
		CCLabel gold2 = CCLabel.makeLabel("Gold", "Arial", 25);
		gold2.setColor(ccColor3B.ccYELLOW);
		gold2.setAnchorPoint(0, 0.5f);
		gold2.setPosition(190, parentSprite.getContentSize().height - 190);
		
		int levelFire = Integer.parseInt(FacebookData.getinstance().getDBData("LevelFire"));
		if (levelFire < 1) {
			FacebookData.getinstance().modDBData("LevelFire", "1");
			levelFire = 1;
		}
		if (levelFire < 20) {
			int firePrice = userData.buyOffenseFire[levelFire - 1];	
			userData.offenceMagic = 0;
			offenseAttributeSelected1 = CCLabel.makeLabel(
					new NumberComma().numberComma(firePrice), "Arial", 30);
		} else {
			offenseAttributeSelected1 = CCLabel.makeLabel("MAX", "Arial", 30);
			offenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
		}

		
//		offenseAttributeSelected1 = CCLabel.makeLabel(" " + firePrice, "Arial", 30);
		offenseAttributeSelected1.setUserData("LevelFire");
		offenseAttributeSelected1.setAnchorPoint(1, 1);
		offenseAttributeSelected1.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 163 - 8);
//		offenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
		
		offenseAttributeSelected2 = CCLabel.makeLabel("공격 시간 " + levelFire + "초 증가", "Arial", 22);
		offenseAttributeSelected2.setUserData("LevelFire");
		offenseAttributeSelected2.setAnchorPoint(1, 0);
		offenseAttributeSelected2.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 248 + 8);
//		offenseAttributeSelected2.setColor(ccColor3B.ccYELLOW);
		
		parentSprite.addChild(gold2);				
		parentSprite.addChild(offenseAttributeSelected1);				
		parentSprite.addChild(offenseAttributeSelected2);				
	
		/********************* 방어 *************************/
		
		CCSprite defenseItem = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "item-labelDefense" + fileExtension));
		defenseItem.setPosition(110, parentSprite.getContentSize().height - 385);
		parentSprite.addChild(defenseItem);
		
		
		CCLabel gold3 = CCLabel.makeLabel("Gold", "Arial", 25);
		gold3.setColor(ccColor3B.ccYELLOW);
		gold3.setAnchorPoint(0, 0.5f);
		gold3.setPosition(190, parentSprite.getContentSize().height - 385);
		

		int LevelDivine = Integer.parseInt(FacebookData.getinstance().getDBData("LevelDivine"));
		if (LevelDivine < 1) {
			FacebookData.getinstance().modDBData("LevelDivine", "1");
			LevelDivine = 1;
		}
		if (LevelDivine < 20) {
			int divinePrice = userData.buyOffenseFire[LevelDivine - 1];	
			userData.defenceMagic = 3;
			defenseAttributeSelected1 = CCLabel.makeLabel(
					new NumberComma().numberComma(divinePrice), "Arial", 30);
		} else {
			defenseAttributeSelected1 = CCLabel.makeLabel("MAX", "Arial", 30);
			defenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
		}
		
		
		defenseAttributeSelected1.setUserData("LevelDivine");
		defenseAttributeSelected1.setAnchorPoint(1, 1);
		defenseAttributeSelected1.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 358 - 8);
//		defenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
		
		defenseAttributeSelected2 = CCLabel.makeLabel("피해 시간 " + LevelDivine + "초 감소", "Arial", 22);
		defenseAttributeSelected2.setUserData("LevelDivine");
		defenseAttributeSelected2.setAnchorPoint(1, 0);
		defenseAttributeSelected2.setPosition(464 - 75 - 20, parentSprite.getContentSize().height - 444 + 8);
//		defenseAttributeSelected2.setColor(ccColor3B.ccYELLOW);
		
		parentSprite.addChild(gold3);
		parentSprite.addChild(defenseAttributeSelected1);
		parentSprite.addChild(defenseAttributeSelected2);
}	

	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	final int previous = 501;
	final int home= 502;
	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			switch (value) {
			case previous:
				scene = Shop.scene();
				break;

			case home:
				scene = Home.scene();
				break;
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

	public void SphereCallback(Object sender) {
		int sphereNumber = Integer.parseInt(FacebookData.getinstance().getDBData("SphereNumber"));
		int spherePrice = userData.buySphere[sphereNumber - 3];
		
		if (sphereNumber < 9) {
			String gold = FacebookData.getinstance().getDBData("Gold");
			FacebookData.getinstance().modDBData("Gold", "" + (Integer.parseInt(gold) - spherePrice));
			FacebookData.getinstance().modDBData("SphereNumber", "" + (sphereNumber + 1));
			
			sphereNumber = Integer.parseInt(FacebookData.getinstance().getDBData("SphereNumber"));
			
			if (sphereNumber < 9) {
				spherePrice = userData.buySphere[sphereNumber - 3];
				sphereNumber1.setString(new NumberComma().numberComma(spherePrice));	
			} else {
				sphereNumber1.setString("MAX");
				sphereNumber1.setColor(ccColor3B.ccYELLOW);
			}
			sphereNumber2.setString("보유 정령병 :  " + new NumberComma().numberComma(sphereNumber));
			
//			Log.e("ShopItem2", "getChildByTag : " + bb.getChildByTag(1));
//			Log.e("ShopItem2", "userData : " + ((CCMenuItemImage)sender).getUserData());
//			userData.increaseSphere();
		}
	}

	public void offenseCallback(Object sender) {
		Log.e("item", "offenseCallback : " + ((CCMenuItemImage)sender).getUserData()); 	
		
		CCLabel  magicLabel1 = defenseAttributeSelected1;
		CCLabel  magicLabel2 = defenseAttributeSelected2;
		CCLabel  magicLabel3 = null;
		int magicNumber = userData.defenceMagic;
				
		if (((CCMenuItemImage)sender).getUserData().equals("Offense")) {
			magicLabel1 = offenseAttributeSelected1;
			magicLabel2 = offenseAttributeSelected2;
			magicNumber = userData.offenceMagic;
		}
		
		String magicType = itemType[magicNumber];
		int magicLevel = Integer.parseInt(FacebookData.getinstance().getDBData(magicType));
		int price = userData.magicPrice[magicNumber][magicLevel];
		
		if (magicLevel < 20) {
			String gold = FacebookData.getinstance().getDBData("Gold");
			FacebookData.getinstance().modDBData("Gold", "" + (Integer.parseInt(gold) - price));
			FacebookData.getinstance().modDBData(magicType, "" + (magicLevel + 1));
			magicLevel = Integer.parseInt(FacebookData.getinstance().getDBData(magicType));
			String levelStr = "Lv" + magicLevel;
						
			switch (magicNumber) {
			case 0:
				magicLabel3 = levelFire;
				break;
	
			case 1:
				magicLabel3 = levelWind;
				break;
				
			case 2:
				magicLabel3 = levelCloud;
				break;
				
			case 3:
				magicLabel3 = levelDivine;
				break;
				
			case 4:
				magicLabel3 = levelEarth;
				break;
				
			case 5:
				magicLabel3 = levelMirror;
				break;			
			}
			
			if (magicLevel < 20) {
				price = userData.magicPrice[magicNumber][magicLevel];
				magicLabel1.setString(new NumberComma().numberComma(price));	
			} else {
				magicLabel1.setString("MAX");
				magicLabel1.setColor(ccColor3B.ccYELLOW);
			}
			magicLabel2.setString(magicText2[magicNumber/3][0] + magicLevel + magicText2[magicNumber/3][1]);
			magicLabel3.setString(levelStr);
		}
	}

	public void buttonCallback(Object sender) {
		CCMenuItemImage button = (CCMenuItemImage)sender;
		List<CCNode> a = button.getParent().getChildren();
//		Log.e("ShopItem2", "a : " + a);
		for (CCNode ccNode : a) {
//			Log.e("ShopItem2", "ccNode : " + ccNode.getUserData());
			final CCMenuItemImage sprite = (CCMenuItemImage)ccNode;
			sprite.setIsEnabled(true);
			sprite.removeChild(itemOver, true);
		}
		button.addChild(itemOver, 777);
		itemOver.setAnchorPoint(0,0);
		button.setIsEnabled(false);
		setElemental(((Integer)button.getUserData()));
//		test(button.getUserData());
	}

	String[] itemType = { "LevelFire", "LevelWind", "LevelCloud", "LevelDivine", "LevelEarth", "LevelMirror" };
	String[][] magicText2 = { {"공격 시간 ","초 증가"}, {"피해 시간 ","초 감소"}};
	private void setElemental(int type) {
		String level = FacebookData.getinstance().getDBData(itemType[type]);
		
		String price = "MAX";
		if (Integer.parseInt(level) < 20) {
			price = "" + userData.magicPrice[type][Integer.parseInt(level)];
		}
		
		
		if (type < 3) {		
			offenseAttributeSelected1.setString(
					new NumberComma().numberComma(price));				
			offenseAttributeSelected2.setString(magicText2[0][0] + level + magicText2[0][1]);	
			userData.offenceMagic =  type;
			if (price.equals("MAX")) {
				offenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
			} else {
				offenseAttributeSelected1.setColor(ccColor3B.ccWHITE);
			}
		} else {			
			defenseAttributeSelected1.setString(
					new NumberComma().numberComma(price));		
			defenseAttributeSelected2.setString(magicText2[1][0] + level + magicText2[1][1]);
			userData.defenceMagic =  type;
			if (price.equals("MAX")) {
				defenseAttributeSelected1.setColor(ccColor3B.ccYELLOW);
			} else {
				defenseAttributeSelected1.setColor(ccColor3B.ccWHITE);
			}
		}
	}

}