package com.aga.mine.pages2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.Home2;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.Utility;

public class HudLayer extends CCLayer {

	String hudLayerFolder = "61hud/";
	int maxMineNumber;

	CGSize winSize;

	CCLabel label;
	public CCLabel testText;
	// CCLabel debugLabel;
	// CCLabel debugLabelLine2;
	static CCLabel statusMine;

	CCSprite magician;

	CCMenu itemMenu;
	CCSequence magicianAction;

	Context mContext;

	float tilePixelSize = CCTMXTiledMap.tiledMap(GameData.share().gameMap)
			.getTileSize().width;

	// (생명) 태그 넘버
	static final int kTagHeart = 9999; // final x
	static final int kButtonOff = 0; // final x
	static final int kButtonOn = 1; // final x

	public GameEnding mGameEnding;
	HudLayer controlHudLayer;
	
	private Game mGame;
	private GameMinimap mGameMinimap;
	public GameProgressBar mGameProgressBar;

	public HudLayer(Game game) {
		mGame = game;
		
//		mGameEnding = new GameEnding();
		
//		addChild(mGameEnding, GameConfig.share().kDepthPopup, 1234);
//		mGameEnding.setVisible(false);
//		mGameEnding.setIsTouchEnabled(false);
		
		this.winSize = CCDirector.sharedDirector().winSize();
		this.mContext = CCDirector.sharedDirector().getActivity()
				.getApplicationContext();

		// 터치가 안될시 아래 명령을 비활성화 할 것
		// CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0,
		// true);
		// this.setIsTouchEnabled(true);
		// this.isTouchEnabled_ = true; // cclayer 멤버

		int margin = 5;
		label = CCLabel.makeLabel("0", "verdana-Bold",
				(18 * tilePixelSize) / 128);
		label.setColor(ccColor3B.ccBLACK);
		label.setPosition(winSize.width
				- (label.getContentSize().width / 2) - margin,
				(label.getContentSize().height / 2) - margin);
		this.addChild(label);

		int testTextSize = 72;
		testText = CCLabel.makeLabel("시간이 초기화 되면 시작", "Arial",
				(testTextSize * tilePixelSize) / 128);
		testText.setColor(ccColor3B.ccWHITE);
		testText.setPosition(winSize.width / 2, winSize.height / 2);
		this.addChild(testText);
		//
		// 상단 중앙 게임진행시간 및 진행 막대기
		mGameProgressBar = new GameProgressBar();
		addChild(mGameProgressBar);
		// gameProgressBar.startTime(gameEnding);
		// Log.e("HudLayer / ", "minimap setting complete");adsa
		// gameProgressBar.startTime(controlHudLayer);
		mGameProgressBar.startTime();

		//
		// 좌상단 찾은 지뢰 갯수 및 생명 표시
		CCSprite statusBase = CCSprite.sprite(hudLayerFolder
				+ "game-statusBase-hd.png");
		statusBase.setPosition(statusBase.getContentSize().width / 2
				+ margin, winSize.height
				- statusBase.getContentSize().height / 2 - margin);
		this.addChild(statusBase);

		// 현재 맵에서 찾아야되는 지뢰(호박)의 갯수 글자
		statusMine = CCLabel.makeLabel(
		// GameData.share().getMineNumber() + " ",
		// "AvenirNextCondensed-Bold", 11);
				GameData.share().getMineNumber() + " ", "Arial-Bold", 30);
		statusMine.setPosition(statusBase.getContentSize().width / 2,
				statusBase.getContentSize().height / 4);
		statusBase.addChild(statusMine);
		this.updateHeart(); // 생명 표시 초기화

		//
		// 우상단 미니맵 버튼
		CCMenuItem item = CCMenuItemImage.item(hudLayerFolder
				+ "game-buttonMinimap-normal-hd.png", hudLayerFolder
				+ "game-buttonMinimap-select-hd.png", this, "clicked");

		item.setTag(Game.kButtonMinimap);
		CCMenu minimap = CCMenu.menu(item);
		minimap.setPosition(
				winSize.width - item.getContentSize().width / 2,
				winSize.height - item.getContentSize().height / 2);
		minimap.setPosition(CGPoint.ccpSub(minimap.getPosition(),
				CGPoint.ccp(4f, 4f)));
		// Log.e("HudLayer", "minimap setting complete");
		this.addChild(minimap);
		// Log.e("HudLayer", "minimap loaded");

		//
		// 하단 수정구 아이템
		CCSprite itemBase = CCSprite.sprite(hudLayerFolder
				+ "game-itemBase-hd.png");
		this.addChild(itemBase);
		itemBase.setPosition(
				winSize.width / 2,
				itemBase.getContentSize().height / 2
						+ itemBase.getContentSize().height / 12);
		CCMenuItemImage itemOn = null;
		CCMenuItemImage itemOff = null;
		CCMenuItemToggle itemToggle;
		itemMenu = CCMenu.menu();
		String[] fileNames = { "Fire", "Wind", "Cloud", "Divine", "Earth",
				"Mirror" };
		int counter = 1;
		for (int i = 0; i < fileNames.length; i++) {
			String fOn = hudLayerFolder + "game-item" + fileNames[i]
					+ "On-hd.png";
			String fOff = hudLayerFolder + "game-item" + fileNames[i]
					+ "Off-hd.png";
			// Log.e("fOn", fOn);
			// Log.e("fOff", fOff);
			itemOn = CCMenuItemImage.item(fOn, fOn);
			itemOff = CCMenuItemImage.item(fOff, fOff);
			itemToggle = CCMenuItemToggle.item(this, "clicked", itemOff,
					itemOn);
			itemToggle.setSelectedIndex(kButtonOff);
			itemToggle.setTag(counter);
			itemMenu.addChild(itemToggle);
			counter++;
		}
		itemMenu.alignItemsHorizontally(0.0f);
		itemMenu.setPosition(
				itemBase.getContentSize().width / 2,
				itemOn.getContentSize().height / 2
						+ itemOn.getContentSize().height / 8);
		itemBase.addChild(itemMenu);

		// 추가하면 터치가 안됨. 확인할 것

		//
		// 이모티콘
		GameEmoticon gameEmoticon = new GameEmoticon();
		this.addChild(gameEmoticon);

		//
		// test
		// this.schedule("tick", 0.1f);
		this.updateSphereItemNumber();

		//
		// test code - 게임이 시작되면 게임엔딩 화면이 바로뜸
		// GameEnding ending = GameEnding.share(this.mContext);
		// this.addChild(ending, GameConfig.share().kDepthPopup);

		//
		// test code
		// GameLoading loading = GameLoading.share(this.mContext);
		// this.addChild(loading, GameConfig.share().kDepthPopup);

		//
		// test code
		// game과 hudlayer에 mcontext둘다 있음.
		UserData userData = UserData.share(mContext);

		//
		// 마법사
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
				hudLayerFolder + "magician.plist");
		magician = CCSprite.sprite(hudLayerFolder + "R-WizardAngle-0.png");
		this.addChild(magician, -1);
		magician.setPosition(winSize.width * 0.57f, winSize.height * 0.3f);

		// this.addChild(point1);
		// point1.setPosition(winSize.width/2,winSize.height/2 + 100);
		// this.addChild(point2);
		// point2.setPosition(winSize.width/2,winSize.height/2 + 50);
		// this.addChild(point3);
		// point3.setPosition(winSize.width/2,winSize.height/2 - 50);
		// this.addChild(point4);
		// point4.setPosition(winSize.width/2,winSize.height/2 - 100);

		mGameMinimap = new GameMinimap(this);
		
		this.addChild(mGameMinimap, GameConfig.share().kDepthPopup);
		mGameMinimap.setVisible(false);

	}

	// HudLayer() end

	//
	public boolean updateHeart() {
		// Log.e("HudLayer",
		// "HudLayer --------- updateHeart --------- HudLayer");
		// value는 3
		int value = GameData.share().getHeartNumber();

		for (int i = 0; i < GameData.share().kMaxHeartNumber * 2; i++) {
			this.removeChildByTag(kTagHeart, true);
		}

		// 하트 위치
		CGPoint position = CGPoint.make(winSize.width / 20.0f,
				winSize.height - winSize.width / 4.3f);
		int z = 0;

		// add flag
		for (int i = 0; i < GameData.share().kMaxHeartNumber; i++) {
			z = (i < value) ? 10 : 0;

			CCSprite heartOn = CCSprite.sprite(hudLayerFolder
					+ "game-heartOn-hd.png");
			CCSprite heartOff = CCSprite.sprite(hudLayerFolder
					+ "game-heartOff-hd.png");

			this.addChild(heartOff, z);
			heartOff.setPosition(position);
			heartOff.setTag(kTagHeart);

			if (i < value) {
				this.addChild(heartOn, z);
				heartOn.setPosition(position);
				heartOn.setTag(kTagHeart);

				// save for animation
				// lastFlag = flag;
			}
			position = CGPoint.make(position.x
					+ heartOff.getContentSize().width * 1.1f, position.y);
		}

		return true;
	}

	// updateHeart() end

	public void setMagicianTo(int direction) {
		// Log.e("direction", " " + direction);
		String frameName = hudLayerFolder + "R-WizardAngle-" + direction
				+ ".png";
		// Log.e("frameName", frameName);
		// CCSpriteFrame frame =
		// CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(frameName);

		// 저사양 기기에서는 버벅거림.
		CCTexture2D t2d = CCTextureCache.sharedTextureCache().addImage(
				frameName);
		CCSpriteFrame frame = CCSpriteFrame.frame(t2d,
				CGRect.make(0, 0, 301, 277), CGPoint.ccp(0, 0));

		magician.setDisplayFrame(frame);
	}

	//
	// 수정구아이템 버튼 활성화 지정
	// @ sphereType : 수정구 유형
	// @ state : 활성화 여부, kBUttonOn, kButtonOff;

	// button callback
	public void clickEffect(int sphereType) {
		this.clickEffect(sphereType, 0.0f);
	}

	// 안될수도 있는 부분
	public void clickEffect(int sphereType, float startDelay) {
		//
		// 활성화된 아이템버튼의 클릭 효과 오버레이를 일정시간 켰다 끈다.
		CCSprite overlay = CCSprite.sprite(hudLayerFolder
				+ "game-itemOver-hd.png");

		for (CCNode itemNode : itemMenu.getChildren()) {
			CCMenuItemToggle item = (CCMenuItemToggle) itemNode;
			if (item.getTag() == sphereType
					&& item.selectedIndex() == kButtonOn) {
				item.addChild(overlay);
				overlay.setPosition(item.getContentSize().width / 2,
						item.getContentSize().height / 2);
				// flashOut 메소드가 안되는 부분이 있어 막아뒀음.(remove node) 대신 다른것 넣어서
				// 테스트중...
				Utility.getInstance().flashOut(overlay, startDelay, 0.5f);
				break;
			}
		}
		// Log.e("Game / clickEffect", "end");
	}

	/*****************************************************/
	/**
	 * 문제지점
	 * 
	 * 애니매이션 아이템 발동 애니
	 * 
	 * 아이템 피해 애니는 GameMinimap.java 의 receivePlayData method 확인
	 * 
	 * 
	 * @return
	 */
	public void clicked(final Object button) {
		// public void clicked(CCMenuItem button) {
		// Log.e("clicked", "clicked");
		if (!Config.getInstance().isDisableButton()) {
			// Log.e("clicked", "Button Enable");
			String effectName = "";
			switch (((CCMenuItem) button).getTag()) {
			case Game.kButtonMinimap:
				// Log.e("button pressed", "kButtonMinimap");
				String a = GameConfig.share().isMinimapPanelOn() ? "true"
						: "false";
				Config.getInstance().setDisableButton(true);
				// this.addChild(GameMinimap.getInstance().tileon(),
				// GameConfig.share().kDepthPopup);
				mGameMinimap.setVisible(true);
				// Log.e("minimap flag is", a);
				break;
			case Game.kButtonFire:
				// Log.e("button pressed", "kButtonFire");
				effectName = "불마법";
				break;
			case Game.kButtonWind:
				// Log.e("button pressed", "kButtonWind");
				effectName = "바람마법";
				break;
			case Game.kButtonCloud:
				// Log.e("button pressed", "kButtonCloud");
				effectName = "구름마법";
				break;
			case Game.kButtonDivine:
				// Log.e("button pressed", "kButtonDivine");
				effectName = "신성마법";
				break;
			case Game.kButtonEarth:
				// Log.e("button pressed", "kButtonEarth");
				effectName = "대지마법";
				break;
			case Game.kButtonMirror:
				// Log.e("button pressed", "kButtonMirror");
				effectName = "반사마법";
				break;

			default:
				// Log.e("button pressed", "default");
				effectName = "마법지정 오류";
				break;
			}
			final String alertText = effectName;
			//
			// 수정구아이템 버튼 클릭 공통
			if (((CCMenuItem) button).getTag() >= 1
					&& ((CCMenuItem) button).getTag() <= 6) {
				//
				// 마법사 액션
				// - 애니메이션을 위에 올렸닥 끝나면 지워버린다.
				// - 본래 캐릭터는 가렸다가 애니메이션이 끝난 후에 (0.4초 이후) 다시 보이도록 한다.
				Utility.getInstance().animationMagicianAction(this);
				magician.setVisible(false);
				final CCNode layer = this;
				
				schedule(new UpdateCallback() {
					@Override
					public void update(float d) {
						magician.setVisible(true);
						layer.removeChildByTag(888, true);
						//
						// 아이템수를 감소시키고
						// 라벨 디스플레이를 업데이트 시키고
						// 버튼 클릭 효과

						// 디펜스와 어택 구분 필요 이유????
						if (GameData.share().isMultiGame) {
							try {
								NetworkController.getInstance().sendPlayDataMagicAttack(((CCMenuItem) button).getTag());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						GameData.share().decreaseItemByType(((CCMenuItem) button).getTag());
						updateSphereItemNumber();
						clickEffect(((CCMenuItem) button).getTag());
						
						unschedule(this);
					}
				}, 0.4f);

			}
		}
	}

	/*****************************************************/

	public void showMagician() {
		magician.setVisible(true);
	}

	/*****************************************************/
	/**
	 * 문제지점
	 * 
	 * 애니매이션 게임 종료 애니 GameEnding.java
	 * 
	 * @return
	 */
	public boolean isGameOver = false; // 게임 종료 메시지를 서버로 무한 보내는것을 방지함.

	public void gameOver() {
		Log.e("Game / HudLayer / gameOver", "gameEnding - gogo");
		Config.getInstance().setDisableButton(true);
		
		// 나중에 GameData 전부 합치기
		if (com.aga.mine.mains.GameData.share().isGuestMode) {
			CCScene scene = Home2.scene();
			CCDirector.sharedDirector().replaceScene(scene);
		} else if (GameData.share().isMultiGame) {
			// 게임 종료 메시지를 서버로 무한 보내는것을 방지함.
			if (!isGameOver) {
				try {
					isGameOver = !isGameOver;
					NetworkController.getInstance().sendRequestGameOver(
							123456);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		mGameEnding = new GameEnding();
		addChild(mGameEnding, GameConfig.share().kDepthPopup, 1234);
		mGameEnding.setIsTouchEnabled(true);
		GameData.share().isMultiGame = true;
		// GameEnding ending = GameEnding.share(this.mContext);
		// this.addChild(ending, GameConfig.share().kDepthPopup);
	}

	/*****************************************************/

	// others

	// 위쪽 캐릭터 얼굴만있는 프로그레스바에 캐릭 얼굴 움직이게 해줌
	public void updateProgress() {
		int gameDifficulty = GameData.share().getGameDifficulty();
		int maxMine = GameData.share().getMaxMineNumber(gameDifficulty);
		int remainedMine = GameData.share().getMineNumber();
		float progress = (float) remainedMine / (float) maxMine;
		progress = 1 - progress;
		mGameProgressBar.progress(progress, GameProgressBar.kTagIndicatorMe);
	}

	// 지뢰갯수 숫자로 표시하는 메서드
	public void updateMineNumber(int remainedMineNumber) {
		statusMine.setString("" + remainedMineNumber);
	}

	// 수정구를 활성화시키면 +1씩 라벨이 붙게함
	public void updateSphereItemNumber() {
		//
		// 전체 라벨 제거
		for (int i = 0; i < 6; i++) {
			this.removeChildByTag(999, true);
		}

		for (int i = 0; i < 6; i++) {
			//
			// 새 라벨 추가
			int sphereType = i + 1;
			int itemNumber = GameData.share().getItemNumberByType(
					sphereType);
			String string = itemNumber > 0 ? "+" + itemNumber : "  ";
			// CCLabel l = CCLabel.makeLabel(string, "Arial", 12);
			// 아이템별 수량을 표시하는 글자
			CCLabel l = CCLabel.makeLabel(string, "Arial-Bold", 24);
			l.setTag(999);
			this.addChild(l);
			l.setPosition(i * 50f * 2 + 50f * 2,
					l.getContentSize().height * 1.2f);

			//
			// 버튼 상태 설정
			this.setSphereItemState(sphereType, itemNumber > 0 ? kButtonOn
					: kButtonOff);
		}
		// Log.e("Game / updateSphereItemNumber", "end");
	}

	// 원래 코드에서 자동이 하도 많아서
	// 지금 작성한게 돌아가도 잘 돌아가는건지 모르겠다.
	public void setSphereItemState(int sphereType, int state) {

		List<CCMenuItemToggle> item = new ArrayList<CCMenuItemToggle>();
		for (int k = 0; k < itemMenu.getChildren().size(); k++) {
			item.add((CCMenuItemToggle) itemMenu.getChildren().get(k));
		}
		if (item != null && item.size() != 0) {
			for (int k = 0; k < item.size(); k++) {
				if (item.get(k).getTag() == sphereType) {
					item.get(k).setSelectedIndex(state);
					break;
				}
			}

		}

	}

	public void abc(int cell_ID) {
		testText.setString("message type : " + cell_ID);
	}

	// public static void abc(int messageType){
	public void abc(byte[] messageType) {
		// final int aaa = messageType;
		byte[] aaa = messageType;
		Log.e("Game / abc", "in");
		String aaaa = "";
		for (byte b : aaa) {
			aaaa = aaaa + b + ",";
		}
		final String result = aaaa;
		
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				if (result != null) {
					testText.setString("message type : " + result);
				}
				unschedule(this);
			}
		});

		Log.e("Game / abc", "out");
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		return true;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}

	// CCLabel point1 = CCLabel.makeLabel("point1 : ", "Arial", (30*
	// tilePixelSize) / 128);
	// CCLabel point2 = CCLabel.makeLabel("point2 : ", "Arial", (30*
	// tilePixelSize) / 128);
	// CCLabel point3 = CCLabel.makeLabel("point3 : ", "Arial", (30*
	// tilePixelSize) / 128);
	// CCLabel point4 = CCLabel.makeLabel("point4 : ", "Arial", (30*
	// tilePixelSize) / 128);
}
