package com.aga.mine.mains;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.aga.mine.pages.UserData;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameDifficulty extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "51difficulty/";
	final String fileExtension = ".png";

	final int singleMode = 1;
	final int randomMode = 2;
	final int inviteMode = 3;
	
	CCSprite bg;
	
	public static int mode;
	
	private Context mContext;
	UserData userData;

	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new GameDifficulty();
		scene.addChild(layer);
		return scene;
	}

	public GameDifficulty() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		//��� �׸� ����
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		
		setBackBoardMenu(commonfolder + "gamebb" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		// ��� �޴�
		TopMenu2.setSceneMenu(this);
		// �ϴ� �̹���
		BottomImage.setBottomImage(this);

		this.setIsTouchEnabled(true);
	}
	
	
	// �� ���� ����
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
	}
	
	// �Խ��� ����
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle2.setTitle(boardFrame, folder);
	}

	// ���� �޴�
	private void setMainMenu(CCSprite parentSprite){
		
		CCMenuItemImage button1 = CCMenuItemImage.item(
				folder + "difficulty-easybutton1" + fileExtension,
				folder + "difficulty-easybutton2" + fileExtension,
				this, "nextCallback");
		button1.setUserData(1);
		
		CCMenuItemImage button2 = CCMenuItemImage.item(
				folder + 	"difficulty-normalbutton1" + fileExtension,
				folder + 	"difficulty-normalbutton2" + fileExtension,
				this, "nextCallback");
		button2.setUserData(2);
		
		CCMenuItemImage button3 = CCMenuItemImage.item(
				folder + 	"difficulty-hardbutton1" + fileExtension,
				folder + 	"difficulty-hardbutton2" + fileExtension,
				this, "nextCallback");
		button3.setUserData(3);
		
		CCMenu gameMenu = CCMenu.menu(button1, button2, button3);
		parentSprite.addChild(gameMenu, 1);
		gameMenu.setPosition(parentSprite.getContentSize().width / 2 - 5, parentSprite.getContentSize().height - 58);
		
		button1.setPosition(0f, -button1.getContentSize().height*0.5f); // �ʱ�
		button2.setPosition(0f, -button1.getContentSize().height*1.5f); // �߱�
		button3.setPosition(0f, -button1.getContentSize().height*2.5f); // ���
		
		CCSprite	buttonText1 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "difficulty-easytext" + fileExtension));
		button1.addChild(buttonText1);
		buttonText1.setPosition(button1.getContentSize().width - buttonText1.getContentSize().width/2 - 30f, button1.getContentSize().height/2);

		CCSprite	buttonText2 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "difficulty-normaltext" + fileExtension));
		button2.addChild(buttonText2);
		buttonText2.setPosition(button2.getContentSize().width - buttonText2.getContentSize().width/2 - 30f, button2.getContentSize().height/2);

		CCSprite	buttonText3 = CCSprite.sprite(
				Utility.getInstance().getNameWithIsoCodeSuffix(folder + "difficulty-hardtext" + fileExtension));
		button3.addChild(buttonText3);
		buttonText3.setPosition(button3.getContentSize().width - buttonText3.getContentSize().width/2 - 30f, button3.getContentSize().height/2);
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	public void previousCallback(Object sender) {
		CCScene scene = GameMode.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
	}

	public void nextCallback(Object sender) {
		CCScene scene = null;
//		GameData.share().setGameDifficulty((Integer)((CCNode)sender).getUserData());
//		switch (GameData.share().getGameMode()) {  // gameData�� �Űܾߵ�. (������ ����.)
		Log.e("GameDifficulty", "tagNumber  : " + userData.getGameMode());
		
		switch (userData.getGameMode()) {  // gameData�� �Űܾߵ�. (������ ����.)
		case singleMode:
			scene = GameLoading.scene();
			single(scene);
			break;
			
		case randomMode:
			scene = GameRandom.scene();
			break;
			
		case inviteMode:
			scene = GameInvite.scene();
			break;
		}
		CCDirector.sharedDirector().replaceScene(scene);
	}

	private void single(CCScene scene) {
		if (userData.getBroomstick() < 1) {
			scene = GameDifficulty.scene();
			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(mContext, "���ڷ簡 �����մϴ�.", Toast.LENGTH_SHORT).show();
						}
					});
		}
	}

}