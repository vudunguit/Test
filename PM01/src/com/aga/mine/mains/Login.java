package com.aga.mine.mains;

//import org.cocos2d.layers.CCColorLayer; // 이게 왜 들어가있지??
import java.util.Locale;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
//import org.cocos2d.types.ccColor4B; // 이게 왜 들어가있지??

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.aga.mine.pages2.GameData;

public class Login extends CCLayer{
	
	final String folder = "02appload/";
	final String fileExtension = ".png";
	
	boolean isTermsOK = false;
	
	final int kButtonOff = 0;
	final int kButtonOn = 1;
	final int termsTag = 10;
	final int disableButtonTag = 100;
	final int toggleButtonTag = 200;
	
	CCSprite bg;
	CCMenuItem termsOK;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		Login layer = new Login();
		scene.addChild(layer);
		return scene;	
	}

	public Login() {
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0), folder + "appload-bg" + fileExtension);
		terms(bg);
		setMain(bg);
		setForeground(bg);
	}
	
	private void terms(CCSprite bg) {
		//
		CCSprite termsBody = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "gameTermsBg" + fileExtension));
		termsBody.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2);
		termsBody.setAnchorPoint(0.5f ,0.5f);
		bg.addChild(termsBody, termsTag , termsTag);
		
		//
		String termsStr = Utility.getInstance().getNameWithIsoCodeSuffix(folder + "gameTermsTitle" + fileExtension);
		CCMenuItem termsUrl = CCMenuItemImage.item(termsStr, termsStr,this, "cilcked");
		termsUrl.setTag(0);
		CCMenu policyMenu = CCMenu.menu(termsUrl);
		policyMenu.setPosition(termsBody.getContentSize().width * 0.5f, termsBody.getContentSize().height * 0.55f);
		termsBody.addChild(policyMenu);
		
		// button
//		CCMenuItem termsCheck = CCMenuItemImage.item(
//				folder + "gameTermsCheck1" + fileExtension,
//				folder + "gameTermsCheck2" + fileExtension,
//				this, "cilcked");
//		termsCheck.setTag(1);
//		CCMenu checkMenu = CCMenu.menu(termsCheck);
		CCMenu checkMenu = CCMenu.menu();
		checkMenu.setPosition(termsBody.getContentSize().width * 0.92f, termsBody.getContentSize().height *0.32f);
		termsBody.addChild(checkMenu);

		// toggle
		String fOn = folder + "gameTermsCheck1" + fileExtension;
		String fOff = folder + "gameTermsCheck2" + fileExtension;
		CCMenuItemImage itemOn = CCMenuItemImage.item(fOn, fOn);
		CCMenuItemImage itemOff = CCMenuItemImage.item(fOff, fOff);
		
		CCMenuItemToggle itemToggle = CCMenuItemToggle.item(this, "cilcked", itemOn, itemOff);
		itemToggle.setSelectedIndex(kButtonOff);
		itemToggle.setTag(toggleButtonTag);
		checkMenu.addChild(itemToggle);
		
		
		//
		termsOK = CCMenuItemImage.item(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "gameTermsOkImage1" + fileExtension),
						Utility.getInstance().getNameWithIsoCodeSuffix(folder + "gameTermsOkImage2" + fileExtension),
				this, "cilcked");
		termsOK.setTag(2);
		
		CCMenu okMenu = CCMenu.menu(termsOK);
		okMenu.setIsTouchEnabled(false);
		okMenu.setPosition(termsBody.getContentSize().width *0.5f, termsBody.getContentSize().height *0.14f);
		termsBody.addChild(okMenu);
		
		// termsOK disable image
		CCSprite disableButton = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "gameTermsOkImage3" + fileExtension));
		disableButton.setPosition(termsOK.getContentSize().width/2, termsOK.getContentSize().height/2);
		termsOK.addChild(disableButton, disableButtonTag, disableButtonTag);
	}
	


	private void setMain(CCSprite bg) {
		//게임이름
		CCSprite title = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "game-Woodtitle" + fileExtension));
		bg.addChild(title);
		title.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2);
		title.setAnchorPoint(0.5f ,0.5f);
		
		//로딩바 배경
		CCSprite loadingMap = CCSprite.sprite(folder + "countbg" + fileExtension);
		bg.addChild(loadingMap);
		loadingMap.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2);
		loadingMap.setAnchorPoint(0.5f ,0.5f);
		
		// 로딩바
		CCSprite loadingBar = CCSprite.sprite(folder + "c01" + fileExtension);
		bg.addChild(loadingBar);
		loadingBar.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2 + 0);
		loadingBar.setAnchorPoint(0.5f ,0.5f);
		
		// 냄비
		CCSprite pot = CCSprite.sprite(folder + "pot" + fileExtension);
		bg.addChild(pot);
		pot.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2 + 116);
		pot.setAnchorPoint(0.5f ,0.5f);

		// 마법사
		CCSprite wizard = CCSprite.sprite(folder + "wizard01" + fileExtension);
		bg.addChild(wizard);
		wizard.setPosition(bg.getContentSize().width/2, bg.getContentSize().height/2 + 116);
		wizard.setAnchorPoint(0.5f ,0.5f);
	}
	
	private void setForeground(CCSprite bg) {
		//페이스북
		CCMenuItem facebook = CCMenuItemImage.item(
				folder + "Facebook-normal" + fileExtension,
				folder + "Facebook-select" + fileExtension,
				this, "facebookCallback");
//		facebook.setAnchorPoint(0.5f, 0.5f);
		
		//게스트
		CCMenuItem guest = CCMenuItemImage.item(
				folder + "Guest-normal" + fileExtension,
				folder + "Guest-select" + fileExtension,
				this, "guestCallback");
//		guest.setAnchorPoint(0.5f, 0.5f);
		
		loginMenu = CCMenu.menu(facebook, guest);
		loginMenu.setContentSize(facebook.getContentSize());
		loginMenu.setAnchorPoint(0,0); // menu는 앙카포인트가 레알 이상함.. 도저히 모르겠음..
//		loginMenu.setAnchorPoint(0.5f ,0.5f);
		loginMenu.setPosition(
				bg.getContentSize().width/2,
				bg.getContentSize().height * 0.12f + loginMenu.getContentSize().height/2);
		loginMenu.setScale(0.2f);
		loginMenu.alignItemsVertically(10);		
		loginMenu.setIsTouchEnabled(false);
		loginMenu.setVisible(false);
		bg.addChild(loginMenu);
	}
	
	CCMenu loginMenu;
	
	public void facebookCallback(Object sender){
		MainApplication.getInstance().getActivity().click();
		MainApplication.getInstance().getActivity().loginFaceBook();
	}

	public void guestCallback(Object sender) {
		MainApplication.getInstance().getActivity().click();
		GameData.share().isGuestMode = true;
		CCScene scene = Home2.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	public void cilcked(Object sender) {
		MainApplication.getInstance().getActivity().click();
		int tag = ((CCNode)sender).getTag();
		Log.e("Login", "tag : " + tag);
		switch (tag) {
		case 0:
			String address = "http://agatong.co.kr/policy/policy_eng.html";
			if (Locale.getDefault().getLanguage().toString().equals("ko"))
				address = "http://agatong.co.kr/policy/policy_kor.html";
			webViewer(address);
			break;
			
//		case 1:	
//			isTermsOK = !isTermsOK;
//			termsOK.getChildByTag(disableButtonTag).setVisible(!isTermsOK);
//			((CCMenu)termsOK.getParent()).setIsTouchEnabled(isTermsOK);
//			break;
			
		case 2:
			bg.removeChildByTag(termsTag, true);
			loginMenu.setVisible(true);
			aniEffect();
			break;
			
		case toggleButtonTag:
			CCMenuItemToggle _Toggle = (CCMenuItemToggle)sender;
			if (_Toggle.selectedIndex() == kButtonOn)
				_Toggle.setSelectedIndex(kButtonOn);
			else
				_Toggle.setSelectedIndex(kButtonOff);
			isTermsOK = !isTermsOK;
			termsOK.getChildByTag(disableButtonTag).setVisible(!isTermsOK);
			((CCMenu)termsOK.getParent()).setIsTouchEnabled(isTermsOK);
			
			break;
		}
	}
	
	private void webViewer(String address) {
		Uri uri = Uri.parse(address);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		CCDirector.sharedDirector().getActivity().startActivity(intent);
	}
	
	private void aniEffect() {
		CCScaleTo action1 = CCScaleTo.action(1, 1);
		CCCallFunc action2 = CCCallFunc.action(this, "buttonTouch");		
		loginMenu.runAction(CCSequence.actions(action1, action2));
	}
	
	public void buttonTouch() {
		loginMenu.setIsTouchEnabled(true);
	}
	

}