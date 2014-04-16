package com.aga.mine.mains;

//import org.cocos2d.layers.CCColorLayer; // 이게 왜 들어가있지??
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
//import org.cocos2d.types.ccColor4B; // 이게 왜 들어가있지??

public class Login extends CCLayer{
	
	final String folder = "02appload/";
	final String fileExtension = ".png";

	CCSprite bg;

	public static CCScene scene() {
		CCScene scene = CCScene.node();
//		CCLayer layer = CCColorLayer.node(new ccColor4B(255, 0, 255, 0)); // 이게 왜 들어가있지??
		Login layer2 = new Login();
//		scene.addChild(layer); // 이게 왜 들어가있지??
		scene.addChild(layer2);
//		layer.addChild(layer2); // 이게 왜 들어가있지??
		return scene;	
	}

	public Login() {
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0), folder + "appload-bg" + fileExtension);
		setMain();
		setForeground();
	}
	
	private void setMain() {
		//게임이름
		CCSprite title = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(
						folder + "appload-title" + fileExtension));
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
	
	private void setForeground() {
		//페이스북
		CCMenuItem facebook = CCMenuItemImage.item(
				folder + "Facebook-normal" + fileExtension,
				folder + "Facebook-select" + fileExtension,
				this, "facebookCallback");
		
		//게스트
		CCMenuItem guest = CCMenuItemImage.item(
				folder + "Guest-normal" + fileExtension,
				folder + "Guest-select" + fileExtension,
				this, "guestCallback");
		
		CCMenu loginMenu = CCMenu.menu(facebook, guest);
		bg.addChild(loginMenu);
		loginMenu.setContentSize(bg.getContentSize().width,bg.getContentSize().height);
		loginMenu.setPosition(0.5f ,0f);
		loginMenu.setAnchorPoint(0.5f ,0f);

		facebook.setPosition(loginMenu.getContentSize().width/2, facebook.getContentSize().height*2.70f);
		guest.setPosition(loginMenu.getContentSize().width/2, guest.getContentSize().height *1.55f);			
	}
	
	public void facebookCallback(Object sender){
		MainApplication.getInstance().getActivity().loginFaceBook();
	}

	public void guestCallback(Object sender) {
		CCScene scene = Home2.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

}