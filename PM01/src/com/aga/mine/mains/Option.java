package com.aga.mine.mains;

import java.util.List;
import java.util.Locale;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.aga.mine.pages.UserData;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
//
public class Option extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "40option/";
	final String fileExtension = ".png";

	boolean logCheck = false;
	
	CCSprite bg;
	CCSprite bb;
	CCSprite boardFrame;
	
	CCSprite itemOver = CCSprite.sprite(folder + "option-check" + fileExtension);

	private Context mContext;
	UserData userData ;
	
	public static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new Option();
		scene.addChild(layer);
		return scene;
	}

	public Option() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		
		setBackBoardMenu(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "option-bb" + fileExtension));
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		// 하단 이미지
		BottomImage.setBottomImage(this);

		itemOver.setAnchorPoint(0,0);
		
		this.setIsTouchEnabled(true);
	}
	
	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.5f);
		bb.setAnchorPoint(0.5f, 0.5f);
		radioButton(bb);
		setMainMenu(bb);
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
		MainActivity mMainActivity = MainApplication.getInstance().getActivity();
		
		CCMenuItem guide = CCMenuItemImage.item(
				SpriteSummery.imageSummary(
						folder + "guide-bg" + fileExtension,
						Utility.getInstance().getNameWithIsoCodeSuffix(
								folder + "guide" + fileExtension)),
				SpriteSummery.imageSummary(
					SpriteSummery.imageSummary(
							folder + "guide-bg" + fileExtension,
							Utility.getInstance().getNameWithIsoCodeSuffix(
							folder + "guide" + fileExtension)),
						folder + "option-buttonSelect" + fileExtension),
				this, "guideCallback");
		
		CCMenuItem facebook = CCMenuItemImage.item(
				SpriteSummery.imageSummary(
						folder + "facebook-bg" + fileExtension,
						Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + 
						(mMainActivity.mSimpleFacebook.isLogin() ? "facebook_out" : "facebook_in") +
						fileExtension)),
				SpriteSummery.imageSummary(
					SpriteSummery.imageSummary(
							folder + "facebook-bg" + fileExtension,
							Utility.getInstance().getNameWithIsoCodeSuffix(
							folder + "facebook_out" + fileExtension)),
						folder + "option-buttonSelect" + fileExtension),
				this, "facebookCallback");
		
		CCMenuItem provision = CCMenuItemImage.item(
				SpriteSummery.imageSummary(
						folder + "provision-bg" + fileExtension,
						Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "provision" + fileExtension)),
				SpriteSummery.imageSummary(
					SpriteSummery.imageSummary(
							folder + "provision-bg" + fileExtension,
							Utility.getInstance().getNameWithIsoCodeSuffix(
							folder + "provision" + fileExtension)),
						folder + "option-buttonSelect" + fileExtension),
				this, "provisionCallback");
		
		CCMenuItem more_apps = CCMenuItemImage.item(
				SpriteSummery.imageSummary(
						folder + "moreapps-bg" + fileExtension,
						"singleblackpixel" + fileExtension),
				SpriteSummery.imageSummary(
						folder + "moreapps-bg" + fileExtension,
						folder + "option-buttonSelect" + fileExtension),
				this, "more_appsCallback");
		
		CCMenu optionMenu = CCMenu.menu(guide, facebook, provision, more_apps);
		parentSprite.addChild(optionMenu);
		optionMenu.alignItemsVertically(0);
		optionMenu.setPosition(parentSprite.getContentSize().width / 2, guide.getContentSize().height * 2.6f);
	}
	
	int[][] radioButtonUserdatas = {{1,2},{11,12},{21,22},{31,32}};
	int[][] radioButtonMenuPositions = {{229,54},{229,106},{229,155},{229,205}};
	//
	private void radioButton(CCSprite parentSprite){
		
		int padding = 52;
		Log.e("userdatas", "length : " + radioButtonUserdatas.length);
		for (int i = 0; i < radioButtonUserdatas.length; i++) {
			
		CCMenuItem onButton = CCMenuItemImage.item(
				folder + "option-check1" + fileExtension,
				folder + "option-check1" + fileExtension,
				this, "buttonCallback");		

		Log.e("userdatas[i][0]", "userdatas[i][0] : " + radioButtonUserdatas[i][0]);
		onButton.setUserData(radioButtonUserdatas[i][0]);
		onButton.addChild(itemOver);
		onButton.setIsEnabled(false);
		
		CCMenuItem offButton = CCMenuItemImage.item(
				folder + "option-check1" + fileExtension,
				folder + "option-check1" + fileExtension,
				this, "buttonCallback");
		Log.e("userdatas[i][1]", "userdatas[i][1] : " + radioButtonUserdatas[i][1]);
		offButton.setUserData(radioButtonUserdatas[i][1]);
		
		CCMenu music = CCMenu.menu(onButton, offButton);
		
		parentSprite.addChild(music);
		
		music.alignItemsHorizontally(padding);
		
		music.setAnchorPoint(0, 0);
		
		// 이미지 들어갈 좌표는 아트팀에서 좀 줘야 작업하는데 빠를텐데...
		music.setPosition(
				radioButtonMenuPositions[i][0] + (padding / 2) + (onButton.getContentSize().width) + 29, // 이미지에서 글자 위치 변경 +29 
				544 + (onButton.getContentSize().height / 2) - radioButtonMenuPositions[i][1] - 4) ; // 이미지에서 글자 위치 변경 -4
		}
	}
	
	public void buttonCallback(Object sender) {
		CCMenuItemImage button = (CCMenuItemImage)sender;
		button.getUserData();
		List<CCNode> a = button.getParent().getChildren();
		for (CCNode ccNode : a) {
			final CCMenuItemImage sprite = (CCMenuItemImage)ccNode;
			sprite.setIsEnabled(true);
			sprite.removeChild(itemOver, true);
		}
		button.addChild(itemOver, 777);

		itemOver.setAnchorPoint(0,0);
		button.setIsEnabled(false);
		setElemental(((Integer)button.getUserData()));
	}
	
	private void setElemental(int type) {
		Log.e("Option2", "selectButton : " + type);
	}
	

	public void previousCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void guideCallback(Object sender) {
		CCScene scene = GameGuide1.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void facebookCallback(Object sender) {
		MainActivity mMainActivity = MainApplication.getInstance().getActivity();
		if(mMainActivity.mSimpleFacebook.isLogin()) {
	    	mMainActivity.logoutFaceBook(); 
	    } else 
    		mMainActivity.loginFaceBook();
		}

	public void provisionCallback(Object sender) {
		String address = "http://agatong.co.kr/policy/policy_eng.html";
		if (Locale.getDefault().getLanguage().toString().equals("ko"))
			address = "http://agatong.co.kr/policy/policy_kor.html";
		webViewer(address);
	}

	public void more_appsCallback(Object sender) {
		String address = "http://agatong.co.kr/m/index.html";
		if (Locale.getDefault().getLanguage().toString().equals("ko"))
			address = "http://www.agatong.co.kr/m/index_kor.html";
		webViewer(address);
	}
	
	private void webViewer(String address) {
		Uri uri = Uri.parse(address);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		CCDirector.sharedDirector().getActivity().startActivity(intent);
	}
}