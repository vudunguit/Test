package com.aga.mine.pages2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCBezierBy;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXLayer;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.CCFormatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.MainApplication;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;

public class Game extends CCLayer {

	String folder = "60game/";
	String fileExtension = ".png";

	CCLayer theLayer = this;

	/********************/

	private Context mContext;
	GameProgressBar progress;
	CGSize winSize;

	ArrayList<MineCell> cells;
	ArrayList<MineCell> sphereBaseCells;

	public final static int kButtonFire = 1;
	public final static int kButtonWind = 2;
	public final static int kButtonCloud = 3;
	public final static int kButtonDivine = 4;
	public final static int kButtonEarth = 5;
	public final static int kButtonMirror = 6;
	public final static int kButtonMinimap = 11;
	public final static int kButtonEmoticon = 12;

	final int kSphereTypeNone = -1;
	final int kSphereTypeEmpty = 0;
	final int kSphereTypeFire = 1;
	final int kSphereTypeWind = 2;
	final int kSphereTypeCloud = 3;
	final int kSphereTypeDivine = 4;
	final int kSphereTypeEarth = 5;
	final int kSphereTypeMirror = 6;
	final int kSphereTypeGetMagic = 7;
	// ----------------------- Game.m --------------------------//
	public CCTMXTiledMap tileMap;
	CCTMXLayer tmxBg;
	CCTMXLayer tmxMeta;
	CCTMXLayer tmxFg;
	CCTMXLayer tmxMineLayer;
	CCTMXLayer tmxItemLayer;
	CCTMXLayer tmxFlagLayer;
	CCTMXLayer tmxEarthLayer;
	CCSprite player;

	public static HudLayer mHud;

	float currentLayerX;
	float currentLayerY;
	float currentScale;

	CCLabel win2;
	float Xscale;
	float Yscale;
	CCLabel center3;

	private int unopenedTile;
	
	private CCAnimation mEarthBomb;
	private CCAnimation mBottle;
	private CCAnimation mMagma;
	private CCAnimation mMagmaFire;
	
	//Tile animation
	public Bitmap mBitmap;
	public CCScaleTo mScaleAction;
	public CCAnimate mOpenAction;
	public CCAnimate mPumpkinBomb;
	public CCAnimation cloudDefense;
	
	final int offenceDefaultTime = 10; // 공격마법만 기본 10초, 방어는 0초입니다.
	int UserLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelCharacter")) - 1; // 1레벨때는 추가 시간 0초입니다.
	
	int offence_FireLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelFire"));
	int offence_WindLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelWind"));
	int offence_CloudLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelCloud"));
	
	int defence_FireLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelDivine"));
	int defence_WindLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelEarth"));
	int defence_CloudLevel = Integer.valueOf(FacebookData.getinstance().getDBData("LevelMirror"));
			
	//마법 공격 및 피해 변수
	public long mFireAttackTime = UserLevel +  offence_FireLevel + offenceDefaultTime; //불공공격 지속 시간, 단위는 second
	public long mWindAttackTime = UserLevel +  offence_WindLevel + offenceDefaultTime;
	public long mCloudAttackTime = UserLevel +  offence_CloudLevel + offenceDefaultTime;
	public long mStartTimeOfAttack; //공격 시작 시간(단위는 ms)
	public long mFireDefenseTime = defence_FireLevel; //불방어 지속 시간(상대방공격시간)
	public long mWindDefenseTime = defence_WindLevel;
	public long mCloudDefenseTime = defence_CloudLevel;
	
	ArrayList<Integer> mDeleteTags; //삭제하기위해 태그를 저장하는 컬렉션
	ArrayList<Integer> mNumberTags; //애니메이션시 셀에 있는 숫자를 저장하는 컬렉션
	
	//대지마법이 사용중인지를 체크하는 변수
	public boolean mIsClickedEarth;
	public CCSprite mEarthGuide;

	private int mineNumber;
	public CCTMXTiledMap getTileMap() {
		return tileMap;
	}
	
	// gamedata에서 수정하였음.
	// 지뢰수 수정하여 테스트중 현재 3개로 수정
	// 수정구 1개로 수정
	/**
	 * Game
	 * 
	 * @param context
	 */
	private Game() {
		
		Log.e("** Game **", "Instance");
		unopenedTile = 0;
		// 기본 초기화
		mContext = CCDirector.sharedDirector().getActivity().getApplicationContext();
		winSize = CCDirector.sharedDirector().winSize();

		//
		if (GameData.share().isMultiGame) {
			Config.getInstance().setDisableButton(true);
		} else {
			Config.getInstance().setDisableButton(false);
		}

		// 난이도 ( 0~2 초,중,상급)
//		GameData.share().setGameDifficulty(1);
//		// 데이터를 들어있는 숫자 깃발 초기화
		GameData.share().resetMineNumber();
		// 호박에 나타나있는 숫자 깃발 초기화
//		GameData.share().setMineNumber(GameData.share().maxMineNumber);
		// GameData.share().setMineNumber(GameData.share().getGameDifficulty());
		Log.e("Game / game ", "난이도 : " + GameData.share().getGameDifficulty());
		// 생명수 초기화
		GameData.share().setHeartNumber(3);
		// 아이템 초기화
		GameData.share().resetItem();
		// // 게임시간 초기화
		// GameData.share().setSeconds(900); // gameStart로 이동

		//
		// 탭 제스쳐 등록
		// 안드로이드 다른방식이라 현재로서는 cocos2D와 어려움.
		// selector 메소드 확인할것
		// CCTouchDispatcher a = CCTouchDispatcher.
		// ?????
		ArrayList<View> a = new ArrayList<View>();
		a.add(CCDirector.sharedDirector().getOpenGLView());
		CCDirector.sharedDirector().getOpenGLView().addTouchables(a);

		//
		// 사운드 (로드)
		for (int i = 0; i < 17; i++) {
			SoundEngine.sharedEngine().preloadEffect(mContext, R.raw.landopen_01 + i); // 이펙트 (효과음) // (타일)pickup	
		}
		SoundEngine.sharedEngine().preloadEffect(mContext, R.raw.pumpkin); // 이펙트 (효과음) // (호박)hit
		SoundEngine.sharedEngine().preloadEffect(mContext, R.raw.mushroom); // 이펙트 (효과음) // (버섯)move
		//
		// 타일맵 로드
		if (!GameData.share().isMultiGame)
			GameData.share().setMap((byte) 0); // 인자값은 무의미
		this.tileMap = CCTMXTiledMap.tiledMap(GameData.share().gameMap);
		

		//
		// 맵 올리고 기본 크기 지정
		this.addChild(this.tileMap, -1);

		// texture.setAntiAliasTexParameters();
		// texture.setAliasTexParameters();
		/*
		 * CCTexture2D texture = new CCTexture2D(); for (CCNode child :
		 * tileMap.getChildren()) { ((CCTMXLayer)child).setTexture(texture); }
		 */
		// 64 pixel
		tileSize = CGSize.make(tileMap.getTileSize().width,
				tileMap.getTileSize().height);
		mapSize = CGSize.make(tileMap.getMapSize().width * tileSize.width,
				tileMap.getMapSize().height * tileSize.height);

		//
		// 타일맵 레이어 등록
		/*
		 * this.bg = this.tileMap.layerNamed("Background"); // Layer Name in
		 * Tiled this.meta = this.tileMap.layerNamed("Meta");
		 * this.meta.setVisible(false); this.fg =
		 * this.tileMap.layerNamed("Foreground"); this.mineLayer =
		 * this.tileMap.layerNamed("MineLayer"); // 지뢰 및 아이템 뿌릴 레이어
		 * this.itemLayer = this.tileMap.layerNamed("ItemLayer"); // 지뢰 및 아이템,
		 * 깃발 가져오는 레이어 this.flagLayer = this.tileMap.layerNamed("FlagLayer"); //
		 * 깃발 꽂을 레이어 this.earthLayer = this.tileMap.layerNamed("CrackedEarth");
		 * // 갈라진대지 레이어
		 */
		this.tmxBg = this.tileMap.layerNamed("Background"); // Layer Name in Tiled
		this.tmxMeta = this.tileMap.layerNamed("Meta"); // 선택 불가 영역
		this.tmxMeta.setVisible(false);
		
		this.tmxFg = this.tileMap.layerNamed("Foreground"); // 잔디
		this.tmxMineLayer = this.tileMap.layerNamed("MineLayer"); // 지뢰(호박) 및 아이템 뿌릴 레이어
		this.tmxItemLayer = this.tileMap.layerNamed("ItemLayer"); // 지뢰 및 아이템, 깃발 가져오는 레이어
		this.tmxFlagLayer = this.tileMap.layerNamed("FlagLayer"); // 깃발(버섯) 꽂을 레이어
		this.tmxEarthLayer = this.tileMap.layerNamed("CrackedEarth"); // 갈라진대지 레이어(아이템 이펙트)
		
		theLayer.setScale(GameConfig.share().kDefaultScale * 128
				/ tileMap.getTileSize().width);
		theLayer.setPosition(
				(-mapSize.width / 2 * theLayer.getScale() + winSize.width / 2),
				0);
		// theLayer.setPosition(getMapCenterPosition().x,
		// getMapCenterPosition().y - getMapDeltaSize().height);
		currentScale = this.getScale(); // 처음 스케일 저장
		currentLayerX = this.getPosition().x; // 처음 화면의 x좌표 저장
		currentLayerY = this.getPosition().y; // 처음 화면의 y좌표 저장
		//
		// progress = new GameProgressBar(mContext);
		// progress.delegate = (GameProgressBarDelegate) this;
		//
		// 전체 타일(셀) 등록
		cells = new ArrayList<MineCell>();
		sphereBaseCells = new ArrayList<MineCell>();
		int count = 0;

		float wid = tileMap.getMapSize().width;
		float hei = tileMap.getMapSize().height;

		for (int x = 0; x < (int) tileMap.getMapSize().width; x++) {
			for (int y = 0; y < (int) tileMap.getMapSize().height; y++) {
				MineCell cell = new MineCell(this);

				//cell.delegate = (MineCellDelegate) this;
				cell.setTileCoord(CGPoint.make(x, y));
				cell.setCell_ID(count);
				cell.setTilePosition(CGPoint.ccp(x * tileSize.width
						+ tileSize.height / 2, mapSize.height
						- (y * tileSize.height + tileSize.height / 2)));
				cells.add(cell);
				//
				// // test code // 수정구가 숨겨진 타일에 표시한 라벨
				// CCLabel label = CCLabel.makeLabel(""+ cell.getCell_ID(),
				// "Arial", (30 * tileSize.width) / 128);
				// this.addChild(label);
				// label.setColor(ccColor3B.ccWHITE);
				// label.setPosition(cell.getTilePosition());
				//
				if (!this.isCollidable(CGPoint.make(x, y)) && !this.isPreOpened(CGPoint.make(x, y))) {
					// 열리지 않은 타일수
					unopenedTile++;
				}
				//
				// isCollidable은 비활성 셀을 의미한다.
				if (this.isCollidable(CGPoint.make(x, y)))
					cell.setCollidable(true);

				//
				// isPreOpened는 처음부터 열려있는 셀들을 열린셀로 등록한다.
				if (this.isPreOpened(CGPoint.make(x, y)))
					cell.setOpened(true);

				count++;

			}
		}
		Log.e("Game", "unopenedTile : " + getClosedCell());
		GameData.share().setMineNumber(getClosedCell());
		mineNumber = GameData.share().getMineNumber();
		Log.e("Game", "mineNumber : " + mineNumber);
		
		// 주변타일 등록(선택지점)
		ArrayList<MineCell> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			/***************************/
			for (int k = -1; k < 2; k++) {
				for (int m = -1; m < 2; m++) {
					//
					// 맵 바깥 셀
					if (cellsTemp.get(i).getTileCoord().x + k < 0
							|| cellsTemp.get(i).getTileCoord().x + k >= this.tileMap
									.getMapSize().width)
						continue;
					if (cellsTemp.get(i).getTileCoord().x + m < 0
							|| cellsTemp.get(i).getTileCoord().x + m >= this.tileMap
									.getMapSize().height)
						continue;

					//
					// 주변 셀
					MineCell cellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k, cellsTemp
									.get(i).getTileCoord().y + m));

					//
					// 비활성 셀
					if (cellRound != null && cellRound.isCollidable())
						continue;

					//
					// 유효한 주변셀 등록(현재 셀 포함)
					cellsTemp.get(i).addRoundCell(cellRound);

				}
			}
			/***************************/

			/***************************/
			// 수정구 위치셀 등록
			// * 2
			// 3 4
			for (int m = 0; m < 2; m++) {
				for (int k = 0; k < 2; k++) {
					//
					// 맵 바깥 셀이면 불가능 셀로 지정하고 빠져 나간다.
					if (cellsTemp.get(i).getTileCoord().x + k < 0
							|| cellsTemp.get(i).getTileCoord().x + k >= this.tileMap
									.getMapSize().width) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}
					if (cellsTemp.get(i).getTileCoord().x + m < 0
							|| cellsTemp.get(i).getTileCoord().x + m >= this.tileMap
									.getMapSize().height) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}

					//
					// 수정구 위치 셀
					MineCell sphereCell = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k, cellsTemp
									.get(i).getTileCoord().y + m));

					//
					// 비활성 셀
					if (sphereCell != null && sphereCell.isCollidable()) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}

					cellsTemp.get(i).addSphereCell(sphereCell);

				}
			}
			/***************************/

			/***************************/
			// 수정구 바깥 주변셀 12개 등록
			// 1 5 7 9
			// 2 * # A
			// 3 # # B
			// 4 6 8 C
			for (int k = -1; k <= 2; k++) {
				for (int m = -1; m <= 2; m++) {
					//
					// 맵 바깥 셀
					if (cellsTemp.get(i).getTileCoord().x + k < 0
							|| cellsTemp.get(i).getTileCoord().x + k >= this.tileMap
									.getMapSize().width)
						continue;
					if (cellsTemp.get(i).getTileCoord().y + m < 0
							|| cellsTemp.get(i).getTileCoord().y + m >= this.tileMap
									.getMapSize().height)
						continue;

					//
					// 스피어 (본체) 위치 셀 - (본체가 있는 셀은 sphereRoundCell 등록에서 제외)
					if (k == 0 || k == 1) {
						if (m == 0 || m == 1)
							continue;
					}

					//
					// 주변셀 (본체가 위치한 셀을 제외한)
					MineCell sphereCellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k, cellsTemp
									.get(i).getTileCoord().y + m));

					//
					// 비활성 셀 - 은 등록하지 않는다. (null이 나올수 없다.)
					if (sphereCellRound.isCollidable())
						continue;
					cellsTemp.get(i).addSphereRoundCell(sphereCellRound);
				}
			}

		}
		// 주변타일 등록 for(i) end

		// 원래 순서는 수정구부터
		//
		// 수정구 설치 (전체 셀, foreground TileMap, Ai)
		scatterSpheres(cells, tmxMineLayer, false);

		//
		// 지뢰 설치
		this.scatterMines();
		
		//전체 셀중에서 벗길수 있는 셀만 등록한다.
		ArrayList<MineCell> tempCells = new ArrayList<MineCell>();
		for(MineCell cell : cells) {
			if(cell.isCollidable()) {
				tempCells.add(cell);
			}
		}
		cells.removeAll(tempCells);

		//
		// 사전에 열려진 셀 주변에 지뢰가 설치되었으면 지뢰수 표시를 한다.
		cellsTemp = cells;
		size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			if (cellsTemp.get(i).isOpened()) {
				int numberOfMine = cellsTemp.get(i).getNumberOfMineAround();
				if (numberOfMine > 0)
					cellsTemp.get(i).displayMineNumber(numberOfMine, cellsTemp.get(i)
							.getTilePosition(), cellsTemp.get(i).getCell_ID());
			}
		}

		//
		// 갈라진 대지 레이어(this.earthLayer)에 타일을 랜덤하게 채운다.
		cellsTemp = cells;
		size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			int gid = this.tmxItemLayer.tileGIDAt(CGPoint.make(
					(float) (Math.random() * 4), 8));
			gid = CCFormatter.swapIntToLittleEndian(gid);
			this.tmxEarthLayer.setTileGID(gid, cellsTemp.get(i).getTileCoord());
		}

		// /////////////////////////////////////
		// Log.e("Game / game", "this.getAnchorPoint : " + this.getAnchorPoint()
		// + " / this.getPosition : " + (int)this.getPosition().x + ", "
		// +(int)this.getPosition().y );
		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
		this.setIsTouchEnabled(true);

		// mapLabel = CCLabel.makeLabel("map", "Arial", (30 * tileSize.width) /
		// 128);
		// this.addChild(mapLabel);
		// mapLabel.setPosition(getPosition());

		
		//주변의 지뢰 갯수를 미리 구한다.
		for(MineCell cell : cells) {
			if (cell.isMine()) {
				cell.numberOfArroundMine = -1;
				continue;
			}

			int countOfMine = 0;
			for (MineCell arroundCell : cell.getRoundCells()) {
				if (arroundCell != null && arroundCell.isMine())
					countOfMine++;
			}
			cell.numberOfArroundMine = countOfMine;
			Log.d("LDK", "arround mine number:" + cell.numberOfArroundMine);
		}

		// 게임시간 초기화
		GameData.share().setSeconds(900);
		UserData.share(mContext).myBroomstick();
		
		//타일 오픈 애니메이션 초기화
		InputStream is;
		try {
			is = CCDirector.theApp.getAssets().open("60game/01.png");
			mBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mScaleAction = CCScaleTo.action(0.1f, 1.8f);
		CCAnimation animation = CCAnimation.animation("dance");
		for( int i=1;i<=7;i++) {
			animation.addFrame(String.format("60game/%02d.png", i));
		}
		mOpenAction = CCAnimate.action(0.2f, animation, false);
		
		//호박폭발 애니메이션
		CCAnimation pumpkin = CCAnimation.animation("pumpkin");
		for( int i=1;i<=7;i++) {
			pumpkin.addFrame(String.format("60game/pumpkinbomb_%02d.png", i));
		}
		mPumpkinBomb = CCAnimate.action(0.5f, pumpkin, false);
		
		//대지마법 애니메이션 초기화
		mEarthBomb = CCAnimation.animation("EarthBomb");
		for(int i=1; i<=12; i++) {
    		CCSprite ebframe = CCSprite.sprite(String.format("61hud/earth-bomb%02d.png", i));
    		mEarthBomb.addFrame(ebframe.getTexture());
		}
		
		//정령석 병 여는 애니메이션
		mBottle = CCAnimation.animation("bottle");
		for(int i=1; i<=10; i++) {
    		CCSprite bottle = CCSprite.sprite(String.format("61hud/bottle_%02d.png", i));
    		mBottle.addFrame(bottle.getTexture());
		}
		
		//불피해시 용암 애니
		mMagma = CCAnimation.animation("magma");
		for(int i=1; i<=9; i++) {
    		CCSprite magma = CCSprite.sprite(String.format("60game/magma_%02d.png", i));
    		mMagma.addFrame(magma.getTexture());
		}
		
		//불피해시 불기동 애니
		mMagmaFire = CCAnimation.animation("magmafire");
		for(int i=1; i<=4; i++) {
    		CCSprite magmafire = CCSprite.sprite(String.format("60game/magmafire_%02d.png", i));
    		mMagmaFire.addFrame(magmafire.getTexture());
		}
		
		cloudDefense = CCAnimation.animation("cloudDefense");
		for(int i=1; i<=4; i++) {
    		CCSprite cloudframe = CCSprite.sprite(String.format("61hud/fx-cloud%d.png", i));
    		cloudDefense.addFrame(cloudframe.getTexture());
		}
		
		mDeleteTags = new ArrayList<Integer>();
		mNumberTags = new ArrayList<Integer>();
		
		//대지마법 9칸 가이드
		mEarthGuide = CCSprite.sprite("61hud/earth_guide.png");
		
		//이모티콘 test : 실제로는 NetworkController에서 전송된 이모티콘 id를 던져준다.
		//mHud.startEmoticonAni(5);
		
//		if (GameData.share().isMultiGame) {
//			schedule(new UpdateCallback() {
//				@Override
//				public void update(float d) {
//					unschedule(this);
//					gameReady();
//				}
//			}, 5);
////			gameReady();
//		} else 
			if (!GameData.share().isMultiGame) {
			gameStart();
		}
		//SoundEngine.sharedEngine().playSound(mContext, R.raw.bgm, true);
	}

//	private void gameReady() {
//		Log.e("Game", "I'm Ready!");
//		try {
//			NetworkController.getInstance().sendGameReady();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public void gameStart() {
		// 게임시간 초기화
		GameData.share().setSeconds(900);
		Config.getInstance().setDisableButton(false);
		// Game.HudLayer.testText.setString("게임 시작! testText"); // 느린 기기에서는
		// 뻗어버림...
	}
	
	// public CCScene scene() {
	public static CCScene scene() {
		// 기존에는 static으로 멤버 변수로 선언하였었음. 이상시 원래 대로 바꿀것
		CCScene scene = CCScene.node();

		// 게임 레이어
		// Game game = Game.getInstance();
		//Game game = Game.getInstance();
		Game game = new Game();
		if (GameData.share().isMultiGame) {
			NetworkController.getInstance().setGame(game);	
		}
		scene.addChild(game);
		game.setAnchorPoint(0.0f, 0.0f);
		/*** 중요 ***/

		// 헤드 업 디스플레이 레이어
		mHud = new HudLayer(game);
		scene.addChild(mHud);
		
		return scene;
	}
	/********************************************************/
	


	// map utility
	// 현재 화면의 position을 지정
	public CGPoint getMapCurrentPosition() {
		// 화면을 줌아웃하면 여기를 계속탐.
		// 화면을 멈췄을때 currentLayerX,currentLayerY, currentScale 값을
		// 터치무브 (allTouches count == 1)에서 저장한후 그 값으로 이곳에서 계산해줌
		// CGPoint centerPosition = CGPoint.ccp(currentLayerX / currentScale,
		// currentLayerY / currentScale);
		// game Layer의 anchor point값을 얻어 스케일값과 연산

		Log.e("Game / getMapCurrentPosition", "cLayerX : "
				+ (int) currentLayerX + ", cLayerY : " + (int) currentLayerY
				+ ", cScale : " + currentScale);
		// CGPoint centerPosition = CGPoint.ccp((winSize.width + currentLayerX)
		// / currentScale, (winSize.height + currentLayerY) / currentScale);
		CGPoint centerPosition = CGPoint.ccp(currentLayerX / currentScale,
				currentLayerY / currentScale);
		Log.e("Game / getMapCurrentPosition", "centerPosition1 : "
				+ centerPosition);
		centerPosition = CGPoint.ccpMult(centerPosition, this.getScale());
		Log.e("Game / getMapCurrentPosition", "centerPosition2 : "
				+ centerPosition);
		return centerPosition;
	}

	// cell utility
	public MineCell cellFromCoord(CGPoint coord) {
		ArrayList<MineCell> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {

			if (CGPoint.equalToPoint(cellsTemp.get(k).getTileCoord(), coord))
				return cellsTemp.get(k);
		}
		return null;
	}

	public MineCell cellFromCellId(int unsingedCellId) {
		ArrayList<MineCell> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			if (cellsTemp.get(k).getCell_ID() == unsingedCellId)
				return cellsTemp.get(k);
		}
		return null;
	}

	//
	// 난이도 설정 (난이도에 따라 지뢰수가 많아짐)
	public void scatterMines() {
		// (void)scatterMines:(NSMutableArray *)cells onLayer:(CCTMXLayer
		// *)layer{ 수정된 것이라는데...
		//
		// 난이도에 따라
		// GameData.share().getMineNumber(gameData.share.getGameDifficulty);
		final int maxMineNumber = getMineNumber();
		// Log.e("Game / scatterMines", "getMineNumber : " +
		// GameData.share().getMineNumber());

		// unsigned
		// 작은값으로 변환
		int mineGid = CCFormatter.swapIntToLittleEndian(this.tmxItemLayer
				.tileGIDAt(CGPoint.make(0f, 0f))); // mind gid
		// Log.e("Game / scatterMines", "mineGid : " + mineGid);
		for (int i = 0; i < maxMineNumber; i++) {
			boolean isBoolean = true;
			while (isBoolean) {
				//
				// 무작위 셀 하나 추출
				// cells들 갯수중에 임의의수 발생???
				int rand = (int) (Math.random() * cells.size());
				MineCell cell = cells.get(rand);
				// Log.e("Game / scatterMines", i + "/" + maxMineNumber);

				//
				// 현재 무작위 추출 셀이 오픈된 셀이 지뢰가 아니고 셀 이면 지뢰로 설정한다.
				// if (cell.isMine() || cell.isOpened() || cell.isCollidable()
				// || cell.isSphere()) {
				if (!cell.isMine() && !cell.isOpened() && !cell.isCollidable()
						&& !cell.isSphere()
						&& !this.isDontSetMine(cell.getTileCoord())) {
					// if(cell.isMine == NO && cell.isOpened == NO &&
					// cell.isCollidable == NO && cell.isSphere == NO && [self
					// isDontSetMine:cell.tileCoord] == NO)

					cell.setMine(true);
					this.tmxMineLayer.setTileGID(mineGid, cell.getTileCoord()); // 레이어가
																				// 비어있으면
																				// 에러남!
					// this.fg.setTileGID(mineGid, cell.tileCoord); // for test
					isBoolean = false;
				}
			}
		}
	}

	// 인자 추가
	public void scatterSpheres(ArrayList<MineCell> cells, CCTMXLayer tmx, boolean isAi) {
		
		int numberOfSphere = GameData.share().kNumberOfSphere;
		if (!GameData.share().isGuestMode)
			numberOfSphere = Integer.parseInt(FacebookData.getinstance().getDBData("SphereNumber"));
		
		for (int i = 0; i < numberOfSphere; i++) {
			int location = 0;
			int randomPick = (int) (Math.random() * 100);
			int sphereType = kSphereTypeEmpty;
			
			if (!GameData.share().isMultiGame) {
				final int earthChance = GameData.share().kSingleEarthChance;
				final int emptyChance = GameData.share().kSingleEmptyChance;
				
				int[] earthRange = {location, earthChance};
				location += earthChance;
				int[] emptyRange = {location, emptyChance};
				location += emptyChance;
				
				if (nsLocationInRange(randomPick, earthRange)) {
					sphereType = kSphereTypeEarth;
				}
				else if (nsLocationInRange(randomPick, emptyRange)) {
					sphereType = kSphereTypeEmpty;
				}
				
			} else {
				
				// 정해진 확률에 맞춰 수정구의 종류를 결정한다.
				final int fireChance = GameData.share().kFireChance;
				final int windChance = GameData.share().kWindChance;
				final int cloudChance = GameData.share().kCloudChance;
				final int mirrorChance = GameData.share().kMirrorChance;
				final int divineChance = GameData.share().kDivineChance;
				final int earthChance = GameData.share().kEarthChance;
				
				// 각 속성별 획득 범위 설정
				int[] fireRange = { location, fireChance };
				location += fireChance;
				int[] windRange = { location, windChance };
				location += windChance;
				int[] cloudRange = { location, cloudChance };
				location += cloudChance;
				int[] mirrorRange = { location, mirrorChance };
				location += mirrorChance;
				int[] divineRange = { location, divineChance };
				location += divineChance;
				
				if (nsLocationInRange(randomPick, fireRange)) {
					sphereType = kSphereTypeFire;
				} else if (nsLocationInRange(randomPick, windRange)) {
					sphereType = kSphereTypeWind;
				} else if (nsLocationInRange(randomPick, cloudRange)) {
					sphereType = kSphereTypeCloud;
				} else if (nsLocationInRange(randomPick, mirrorRange)) {
					sphereType = kSphereTypeMirror;
				} else if (nsLocationInRange(randomPick, divineRange)) {
					sphereType = kSphereTypeDivine;
				} else {
					sphereType = kSphereTypeEarth;
				}

			}
			
			boolean isBoolean = true;
			while (isBoolean) {
				// 무작위 셀 하나 추출
				int rand = (int) (Math.random() * cells.size());
				MineCell cell = cells.get(rand);

				//
				// ** (1) 현재 무작위 추출 셀의 수정구 자리 4개의 셀이
				// - 1) 오픈된 셀이 아니고
				// - 2) 지뢰가 아니고
				// - 3) 유효한 셀
				// - 4) 이미 수정구가 설치된 것이 아니면 수정구 아이템 영역으로 설정한다.

				//
				// ** (2) 기반셀이 가능한 셀이어야 한다.
				// isBoolean = false; // 임시용. isSphereBasePossible가 현재 무조건
				// false이다.
				if (cell.isSphereCellsClear() && cell.isSphereBasePossible()) {
					// 수정구 설치된 기반셀 등록하여 오픈 체크에 사용
					if (isAi) {
						Log.e("Game", "scatterSpheres - isAi");
					} else {
						sphereBaseCells.add(cell);
					}
					cell.setSphereType(sphereType);
//					cell.setToSphereCells(sphereType); // 수정구 필드에 심기
					
					Log.e("Game", "수정구 생성(타입) : " + sphereType);
					createSphere = true;
					this.addSphereTo(tmx, sphereType, cell, isAi); // 획득전 수정구는 마인레이어에 심기
					isBoolean = false;
				}
				// Log.e("Game", "scatterSpheres" + isBoolean);
			}
		}
	}

	boolean createSphere = false;
	
	public int[] nsRange(int loc, int len) {
		int[] range = { loc, len };
		return range;
	}

	public boolean nsLocationInRange(int loc, int[] range) {
		if (loc - range[0] < range[1])
			return true;
		return false;
	}

	// 인자 isAi 추가
	public void addSphereTo(CCTMXLayer tmx, int sphereType, MineCell baseCell, boolean isAi) {
		int counter = 0;
		
		if (sphereType == kSphereTypeEmpty)
			sphereType = kSphereTypeGetMagic; // 빈 수정구값은 0이지만 TMX에 있는 이미지는 7번에 있기때문에 새로 대입
		
		if(!isAi) {
			if (GameData.share().isMultiGame) {
				try {
					if (sphereType == kSphereTypeGetMagic) {
						NetworkController.getInstance().sendPlayDatasphereTake(baseCell.getCell_ID() + (sphereType * 10000));
					} else {
						NetworkController.getInstance().sendPlayDataSphere(baseCell.getCell_ID() + (sphereType * 10000));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (MineCell cell : baseCell.getSphereCells()) {
			cell.setSphere(true);

//			if (sphereType == kSphereTypeEmpty)
//				sphereType = 7; // 빈 수정구값은 0이지만 TMX에 있는 이미지는 7번에 있기때문에 새로 대입

			CCTMXLayer itemLayer = null;
			if(isAi) // ai일시 자신의 맵을 미니맵에 표현한듯.
				itemLayer = mHud.mGameMinimap.itemLayer;
			else
				itemLayer = tmxItemLayer;
			// 아이폰과 다른점 : gid를 얻을시 반드시 CCFormatter.swapIntToLittleEndian(gid)에 gid를 넣을것 
			int gid = CCFormatter.swapIntToLittleEndian(itemLayer.tileGIDAt(CGPoint.make(counter, sphereType))); // 아이템 레이어에서 가지고 오기
			if (!createSphere)
				tmx.removeTileAt(cell.getTileCoord());
			tmx.setTileGID(gid, cell.getTileCoord());
			this.getFg().removeTileAt(cell.getTileCoord());
			cell.setOpened(true); // 수정구가 있는 좌표는 Fg오픈하기
			removeCell();
			if (createSphere && GameData.share().isMultiGame) {
				try {
					NetworkController.getInstance().sendPlayDataCellOpen(cell.getCell_ID());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			counter++;
		}
		createSphere = false;
	}

	boolean toggleFlag;
	// handle touches

	// touch
	CGPoint touchLocation;
	CCSprite yourSprite;

	boolean isLongTap = false;
	boolean isDoubleTap = false;
	double doubleTap = 0;
	double longPressTime = 0;
	double doubleTouchTime = 0;

	// 기존 좌표와 같은 위치를 클릭했는지 확인하는 메소드
	public boolean locationMatchingTest(CGPoint a, CGPoint b, float errorRange) {
		boolean isBoolean = false;
		if (a != null && b != null)
			if (a.x - errorRange < b.x && a.x + errorRange > b.x
					&& a.y - errorRange < b.y && a.y + errorRange > b.y)
				isBoolean = true;
		return isBoolean;
	}

	CGPoint currentTouchLocation = null;
	CGPoint previousTouchLocation = null;

	/********************************************/

	/*************************/
	CGPoint currentLocation1 = null;
	CGPoint currentLocation2 = null;
	CGPoint previousLocation1 = null;
	CGPoint previousLocation2 = null;
	public CGSize mapSize = null;
	public CGSize tileSize = null;

	// CCLabel mapLabel;

	//
	private CGPoint getConvertToGL(MotionEvent event) {
		return getConvertToGL(event, 0);
	}

	//
	private CGPoint getConvertToGL(MotionEvent event, int pointerID) {
		int touch = event.findPointerIndex(event.getPointerId(pointerID));
		return CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(touch), event.getY(touch)));
	}

	//
	private float getDistance(CGPoint point1, CGPoint point2) {

		Log.e("Game / getDistance", "point1:" + point1 + ",point2:" + point2);
		// sqrt = 제곱근 , pow = 몇 승
		return (float) Math.sqrt(Math.pow(point1.x - point2.x, 2.0f)
				+ Math.pow(point1.y - point2.y, 2.0f));
	}

	//
	// position을 기기 화면 중심으로 지정해줌.
	public CGPoint getMapCenterPosition() {
		// -mapSize : x: -864.0, y: -1280.0
		// winSize : x: 160.0, y: 284.0
		// winSize : x: 640.0, y: 1052.0 <-- 1920 x1080 navigation

		CGPoint centerPosition = CGPoint.ccp(-mapSize.width / 2 + winSize.width
				/ 2, -mapSize.height / 2 + winSize.height / 2);
		Log.e("Game / getMapCenterPosition", "centerPositionScale:"
				+ centerPosition);
		Log.e("Game / getMapCenterPosition", "getMapCenter:" + mapSize
				+ "getMapCenter:" + winSize);
		centerPosition.ccpMult(centerPosition, this.getScale());
		Log.e("Game / getMapCenterPosition", "centerPositionScale:"
				+ centerPosition);

		// example
		// return ccp(v.x*s, v.y*s); this.setScale() = 0.23f, centerPositionX =
		// -704, centerPositionY = -996
		// this.getScale() = 0.23 ~ 1.0;
		// centerPosition.x = centerPosition.x * this.getScale();
		// centerPosition.y = centerPosition.y * this.getScale();

		return centerPosition;
	}

	public CGSize getMapDeltaSize() {
		// 변화되는 scale에 따라 map의 사이즈를 변경 (1:1, 3456:5120)
		CGSize scaleMapSize = CGSize.make(mapSize.width * this.getScale(),
				mapSize.height * this.getScale());
		// (-1408 -2034)
		CGSize deltaSize = CGSize.make(winSize.width / 2 - scaleMapSize.width
				/ 2, winSize.height / 2 - scaleMapSize.height / 2);
		Log.e("Game / getMapDeltaSize", "MapDelta : " + deltaSize);

		return deltaSize;
	}

	public CGPoint getMapMinPosition() {
		return CGPoint.ccp(0, 0);
	}

	public CGPoint getMapMaxPosition() {
		return CGPoint.ccp(-this.tileMap.getContentSize().width * getScale()
				+ winSize.width, -this.tileMap.getContentSize().height
				* getScale() + winSize.height);
	}

	//
	private void layerMove(MotionEvent event) {

		if (currentLocation1 != null) {
			// if (currentLocation1.x > previousLocation1.x + 3 ||
			// currentLocation1.x < previousLocation1.x - 3 ||
			// currentLocation1.y > previousLocation1.y + 3 ||
			// currentLocation1.y < previousLocation1.y - 3)
			previousLocation1 = currentLocation1;
		}
		if (previousLocation1 == null)
			previousLocation1 = getConvertToGL(event, 0);

		currentLocation1 = getConvertToGL(event, 0);
		// Log.e("Game / layerMove", "cuLoc1:" + (int)currentLocation1.x + "," +
		// (int)currentLocation1.y);
		CGPoint deltaLocation = CGPoint.ccpSub(currentLocation1,
				previousLocation1);
		Log.e("Game / layerMove", "deltaLocation : " + deltaLocation);

		// if (deltaLocation.x >= 3 || deltaLocation.y >= 3) {
		theLayer.setPosition(CGPoint.ccpAdd(theLayer.getPosition(),
				deltaLocation));

		// scale 사용시 이상 현상 발생
		theLayer.setPosition(CGPoint.ccp(
				CGPoint.clampf(theLayer.getPosition().x,
						this.getMapMinPosition().x, this.getMapMaxPosition().x),
				CGPoint.clampf(theLayer.getPosition().y,
						this.getMapMinPosition().y, this.getMapMaxPosition().y)));

		// mapLabel.setString("mapLabel.getPosition() : " +
		// mapLabel.getPosition());
		// mapLabel.setPosition(mapLabel.getPosition());
		/*
		 * longPressTime = 0; isLongTap = false; isDoubleTap = false;
		 * 
		 * } else if (longPressTime + 300 > System.currentTimeMillis()) {
		 * handleLongPress(event); } else { isDoubleTap = true; }
		 */
	}

	// previousLocation에 문제 있는듯 이벤트 받아올때 잘 정리할것
	private void layerScale(MotionEvent event) {

		if (currentLocation1 != null)
			previousLocation1 = currentLocation1;

		if (currentLocation2 != null)
			previousLocation2 = currentLocation2;

		currentLocation1 = getConvertToGL(event, 0);
		currentLocation2 = getConvertToGL(event, 1);

		Log.e("Game / layerScale", event.getPointerCount() + ", cuLoc1:"
				+ (int) currentLocation1.x + "," + (int) currentLocation1.y
				+ ", cuLoc2:" + (int) currentLocation2.x + ","
				+ (int) currentLocation2.y);

		float currentDistance = getDistance(currentLocation1, currentLocation2);
		float previousDistance = getDistance(previousLocation1,
				previousLocation2);
		float deltaDistance = currentDistance - previousDistance;

		// Log.e("deltaDistance", "current:" +currentDistance+", previous:"
		// +previousDistance+ ", Distance:"+deltaDistance);
		CGPoint pinchCenter = this.convertToNodeSpace(CGPoint.ccpMidpoint(
				currentLocation1, currentLocation2));

		//
		// 최소/최대 확대율 범위에서 레이어 확대 축소를 핀치 거리에 따라 한다.
		float newScale = theLayer.getScale()
				+ (deltaDistance * GameConfig.share().kPinchZoomMultiplier);

		// clampf 그냥 범위 안에 들어가는지 확인후 리턴(128 pixel 기준)
		newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale * 128
				/ tileSize.width, GameConfig.share().kMaxScale * 128
				/ tileSize.width);

		Log.e("Game / layerScale", "scale:"
				+ ((int) (theLayer.getScale() * 100) / 100f) + ", new scale:"
				+ ((int) (newScale * 100) / 100f) + ", delta:"
				+ (int) deltaDistance);
		float positionScale = newScale - theLayer.getScale();
		theLayer.setScale(newScale); // 줌아웃을 하게해줌
		Log.e("Game / layerScale", "theLayer position" + theLayer.getPosition());
		Log.e("Game / layerScale",
				"theLayer getAnchorPoint" + theLayer.getAnchorPoint());
		theLayer.setPosition(CGPoint.ccpSub(
				theLayer.getPosition(),
				CGPoint.make(pinchCenter.x * positionScale, pinchCenter.y
						* positionScale))); // 선택한 화면에서 줌아웃을 함

		// 여기서 화면 밀림 발생
		theLayer.setPosition(CGPoint.ccp(
				CGPoint.clampf(theLayer.getPosition().x,
						this.getMapMinPosition().x, this.getMapMaxPosition().x),
				CGPoint.clampf(theLayer.getPosition().y,
						this.getMapMinPosition().y, this.getMapMaxPosition().y)));

		// mapLabel.setString("mapLabel.getPosition() : " +
		// mapLabel.getPosition());
		// mapLabel.setPosition(mapLabel.getPosition());
	}

	// handle touches

	//
	// 롱터치 : 깃발 꽂기
	public void handleLongPress(MotionEvent event) {
		CGPoint coord = setCoord(event);
		for (MineCell mineCell : cells) {
			/***************테스트 코드**********************/
			Log.e("Game_LongPress", "mineCell.isCollidable() : " + mineCell.isCollidable());
			Log.e("Game_LongPress", "mineCell.isMarked() : " + mineCell.isMarked());
			Log.e("Game_LongPress", "mineCell.isMine() : " + mineCell.isMine());
			Log.e("Game_LongPress", "mineCell.isOpened() : " + mineCell.isOpened());
			Log.e("Game_LongPress", "mineCell.isSphere() : " + mineCell.isSphere());
			Log.e("Game_LongPress", "mineCell.isSphereBasePossible() : " + mineCell.isSphereBasePossible());
			Log.e("Game_LongPress", "mineCell.isSphereCellsClear() : " + mineCell.isSphereCellsClear());
			/*************************************/
			
			// 오픈안된 셀에 버섯(깃발)꽂기
			if (!mineCell.isOpened() && !mineCell.isCollidable() && CGPoint.equalToPoint(mineCell.getTileCoord(), coord)) {
				// effect sound play
				SoundEngine.sharedEngine().playEffect(mContext, R.raw.mushroom);
				// 버섯 설치음 대신 진동으로 변경입니다. (일단 둘다 열어둡니다.)
				MainApplication.getInstance().getActivity().vibe();
				
				int isMine = -1;
				
				// 꽂아져있는 버섯(깃발)을 취소할때 버섯(깃발)을 없애줌
				if (mineCell.isMarked())
					unmarkMushroom(mineCell, coord, isMine);
				else
					markMushroom(mineCell, coord, isMine);
				break;
			}
		}
	}

	private CGPoint setCoord(MotionEvent event) {
		// Log.e("Game / handleLongPress", "click : " + event.getAction());
		CGPoint location = CGPoint.ccp(event.getRawX(), event.getRawY());
		location = CCDirector.sharedDirector().convertToGL(location); // 4사분면을 1사분면으로 변환
		location = this.convertToNodeSpace(location); // GameLayer 좌측하단으로부터 터치 위치까지의 거리값(x,y)
//		CGPoint location2 = this.convertToNodeSpace(location);
		
		CGPoint coord = this.tileCoordForPosition(location); // 타일의 어느 좌표인지 확인하여 값 불러오기
		// Log.e("Game / handleLongPress", "coord : " + coord); // 좌측상단 0,0 우측하단
		// 26, 39
		return coord;
	}
	
	// 버섯 표시
	private void markMushroom(MineCell mineCell, CGPoint coord, int isMine) {
		if (isMine == mineCell.getNumberOfMineAround()) {
			GameData.share().markMine();
			Log.e("Game", "markedMine : " + GameData.share().getCurrentMine());
			Log.e("Game", "getClosedCell : " + getClosedCell());
			Log.e("Game", "getMineNumber() : " + GameData.share().getMineNumber());
			// 게임 종료, 이게 가능한가???
			if (getClosedCell() <= GameData.share().getMineNumber()) {
				Log.e("Game", "handleLongPress Game Over");
				// 오픈이 안된 셀 11개, 지뢰 1개 ==> 지뢰에 버섯을 꽂아도 타일은 오픈되지 않으므로x
				// 오픈이 안된 셀 10개, 지뢰 0개 ==> 이미 더블탭에서 종료가 됐을것이기에 x
				mHud.gameOver(sumScore(), -1);
			}
		}
		
		mineCell.setMarked(true);
		this.markFlag(coord);
		if (GameData.share().isMultiGame) {
			try {
					NetworkController.getInstance().sendPlayDataMushroomOn(mineCell.getCell_ID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Log.e("Game / handleLongPress", "지뢰 없음");
	}
	
	// 버섯 제거
	private void unmarkMushroom(MineCell mineCell, CGPoint coord, int isMine) {
		if (isMine == mineCell.getNumberOfMineAround()) {
			GameData.share().unmarkMine();
			Log.e("Game", "markedMine : " + GameData.share().getCurrentMine());
		}
		
		mineCell.setMarked(false);
		this.removeFlag(coord); // tmx에 적용
		if (GameData.share().isMultiGame) {
			try {
					NetworkController.getInstance().sendPlayDataMushroomOff(mineCell.getCell_ID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//
	// 더블터치 : 셀 오픈
	public void handleDoubleTap(MotionEvent event) {
		mHud.StartAniCloudDefense(10);
		Log.e("Game / handleDoubleTap", "마인 갯수 : " + getMineNumber());
		if (Config.getInstance().isDisableButton())
			return;

		CGPoint location = null;
		CGPoint coord = null;

		// GL 좌표
		location = CGPoint.ccp(event.getRawX(), event.getRawY());
		// cocos2d 좌표로 변환
		CGPoint glLocation = CCDirector.sharedDirector().convertToGL(location);

		// layer 좌측 하단으로 부터 좌표까지의 거리
		location = this.convertToNodeSpace(glLocation);
		// 타일의 어느 좌표인지 확인하여 값 불러오기
		coord = this.tileCoordForPosition(location);

		CopyOnWriteArrayList<MineCell> copyCells = new CopyOnWriteArrayList<MineCell>();
		copyCells.addAll(cells);

		for (final MineCell cell : copyCells) {
			// 전체 타일을 검색하여 터치한 위치와 타일의 위치값이 일치할시
			if (CGPoint.equalToPoint(cell.getTileCoord(), coord)
					&& !cell.isCollidable()) {
				int numberOfMine = cell.getNumberOfMushroomAndPumpkinAround();
				
				// 오픈된 셀 누르면
				if (cell.isOpened()) {
					// 셀 저장된 주변 마인수를 얻어내 1이상이면
					if (numberOfMine > 0) {
						cell.roundOpen();
					}
					// 닫혀있는 셀 누르면
				} else {
					// 지정된 셀을 열어준다.(tile의 fg를 제거)
					new TileOpenTask() {
						@Override
						public void run() {
							cell.open();
						}
					}.execute();
				}
				break;
				// k = size;
			}
		}

		// 모두 열린 수정구가 있는지 확인한다.
		for (MineCell cell : sphereBaseCells) {
			//
			// -1 : none
			// 0 : empty
			// > 0 : sphereType
			int sphereType = cell.getSphereItem();
			if (sphereType > 0) {

				// 새로 열려진 셀들에 수정구가 열렸다.
				// 빈 수정구로 지정하고,
				cell.setSphereType(kSphereTypeEmpty);

				// Log.e("Game / handleDoubleTap", "check 1 / 아이템 타일 변경");
				// 빈 수정구 타일로 바꾼다.
				Log.e("Game", "수정구 획득");
				this.addSphereTo(tmxMineLayer, kSphereTypeGetMagic, cell, false); // 빈
																					// 수정구는
																					// 백그라운드에
																					// 심기

				// 유리병 터지는 애니메이션
				if (!createSphere) { // 수정구 생성하는게 아님.(수정구 획득)
					startOpenBottle(cell.getTilePosition());
				}

				// 정령석 아래 HUD로 이동하는 애니메이션
				mHud.startMoveSpirit(sphereType, this.convertToWorldSpace(cell.getTilePosition().x, cell.getTilePosition().y));
			}
		}
		// end 모두 열린 수정구가 있는지 확인한다.
		if (getClosedCell() <= GameData.share().getMineNumber()) {
			Log.e("Game / handleDoubleTap", "handleDoubleTap Game Over");
			Log.e("Game", "unopenedTile : " + getClosedCell() + ", Max Mine : "
					+ GameData.share().getMineNumber());
			mHud.gameOver(sumScore(), -1);
		}
		// end Config.getInstance().isDisableButton()
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint location = null;
		CGPoint coord = null;

		// GL 좌표
		location = CGPoint.ccp(event.getRawX(), event.getRawY());
		CGPoint glLocation = CCDirector.sharedDirector().convertToGL(location);
		location = this.convertToNodeSpace(glLocation);
		coord = this.tileCoordForPosition(location);
		
		Log.d("LDK", "coord: " + coord.x + "," + coord.y + "     misclickedEarth:" + mIsClickedEarth);
		
		if(mIsClickedEarth) {
			ArrayList<MineCell> copyCells = new ArrayList<MineCell>();
			copyCells.addAll(cells);
			for (MineCell cell : copyCells) {
				// 전체 타일을 검색하여 터치한 위치와 타일의 위치값이 일치할시
				if (CGPoint.equalToPoint(cell.getTileCoord(), coord)) {
					mEarthGuide.setAnchorPoint(0.5f, 0.5f);
					addChild(mEarthGuide, 10);
					mEarthGuide.setPosition(cell.getTilePosition());
					Log.e("LDK", "cell: " + cell.getTilePosition().x + "," + cell.getTilePosition().y + "     ");
					return true;
				}
			}
		}
		
		// device 좌표를 읽어온다. (openGL x)
		currentTouchLocation = CCDirector.sharedDirector().convertToGL(
				CGPoint.make(event.getRawX(), event.getRawY()));
		// previousTouchLocation =
		// CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(),
		// event.getRawY()));

		isLongTap = true;

		// 현재 찍힌 좌표 계산
		CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(
				CGPoint.make(event.getRawX(), event.getRawY()));
		
		double tempTime = System.currentTimeMillis();

		boolean abc = locationMatchingTest(touchLocation, convertedLocation, 20); // default
																					// :
																					// 20

		// 더블탭 (지연시간 0.3초)
		if (abc && doubleTap + 500 > tempTime && event.getPointerCount() == 1) {
			// if (abc && doubleTap + 500 > tempTime) {
			handleDoubleTap(event);
			isLongTap = false;
			touchLocation = convertedLocation; // 좌표가 없을시 대입
			doubleTap = 0;
			// handleDoubleTap(event);

			// 싱글탭
		} else { // 같은 위치가 선택 되었을시
			touchLocation = convertedLocation; // 좌표가 없을시 대입
			doubleTap = tempTime;
		}
		return super.ccTouchesBegan(event);
	}

	// 화면이동 과 줌인 아웃시
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		
		if (!Config.getInstance().isDisableButton()) {
			
			if(mIsClickedEarth) {
				CGPoint glLocation = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getRawX(), event.getRawY()));
				CGPoint coord = this.tileCoordForPosition(convertToNodeSpace(glLocation));
				
				ArrayList<MineCell> copyCells = new ArrayList<MineCell>();
				copyCells.addAll(cells);
				for (MineCell cell : copyCells) {
					// 전체 타일을 검색하여 터치한 위치와 타일의 위치값이 일치할시
					if (CGPoint.equalToPoint(cell.getTileCoord(), coord)) {
						mEarthGuide.setPosition(cell.getTilePosition());
						Log.e("LDK", "cell: " + cell.getTilePosition().x + "," + cell.getTilePosition().y + "     ");
						return true;
					}
				}
			}

			//
			// 조절 할 레이어 지정
			CCLayer theLayer = this;
			boolean isMove = true;

			//
			// 일반

			/*************************************/
			// 현재 찍힌 좌표 계산
			// convert event location to CCPoint
			CGPoint convertedLocation = CCDirector
					.sharedDirector()
					.convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
			double tempTime = System.currentTimeMillis();
			boolean abc = locationMatchingTest(touchLocation,
					convertedLocation, 10); // default : 10

			// 롱클릭 (문제점 저해상도에서는 터치 무브가 일어나지 않는다.)
			if (event.getPointerCount() == 1 && isLongTap) {
				if (abc) {
					if (doubleTap + 300 < tempTime) {
						handleLongPress(event);
						convertedLocation = this
								.convertToNodeSpace(convertedLocation);
						isLongTap = false;
						touchLocation = convertedLocation; // 좌표가 없을시 대입
						doubleTap = 0;

						isMove = false;
					}
				}
			} else
			/*************************************/

			if (event.getPointerCount() == 2) {
				isLongTap = false;
				int touch1 = event.getPointerId(0);
				int touch2 = event.getPointerId(1);

				int pointerIndex1 = event.findPointerIndex(touch1);
				int pointerIndex2 = event.findPointerIndex(touch2);

				if (previousLocation1 == null)
					previousLocation1 = CCDirector.sharedDirector()
							.convertToGL(
									CGPoint.ccp(event.getX(pointerIndex1),
											event.getY(pointerIndex1)));
				else
					previousLocation1 = currentLocation1;

				if (previousLocation2 == null)
					previousLocation2 = CCDirector.sharedDirector()
							.convertToGL(
									CGPoint.ccp(event.getX(pointerIndex2),
											event.getY(pointerIndex2)));
				else
					previousLocation2 = currentLocation2;

				currentLocation1 = CCDirector.sharedDirector().convertToGL(
						CGPoint.ccp(event.getX(pointerIndex1),
								event.getY(pointerIndex1)));
				currentLocation2 = CCDirector.sharedDirector().convertToGL(
						CGPoint.ccp(event.getX(pointerIndex2),
								event.getY(pointerIndex2)));

				// sqrt = 제곱근 , pow = 몇 승
				float currentDistance = (float) Math.sqrt(Math.pow(
						currentLocation1.x - currentLocation2.x, 2.0f)
						+ Math.pow(currentLocation1.y - currentLocation2.y,
								2.0f));

				float previousDistance = (float) Math.sqrt(Math.pow(
						previousLocation1.x - previousLocation2.x, 2.0f)
						+ Math.pow(previousLocation1.y - previousLocation2.y,
								2.0f));

				float deltaDistance = currentDistance - previousDistance;
				CGPoint pinchCenter = CGPoint.ccpMidpoint(currentLocation1,
						currentLocation2);
				pinchCenter = this.convertToNodeSpace(pinchCenter);

				//
				// 최소/최대 확대율 범위에서 레이어 확대 축소를 핀치 거리에 따라 한다.
				float newScale = theLayer.getScale()
						+ (deltaDistance * GameConfig.share().kPinchZoomMultiplier);
				// clampf 그냥 범위 안에 들어가는지 확인후 리턴 (128pixel 기준)
				newScale = CGPoint.clampf(newScale,
						GameConfig.share().kMinScale * 128 / tileSize.width,
						GameConfig.share().kMaxScale * 128 / tileSize.width);
				float positionScale = newScale - theLayer.getScale();
				theLayer.setScale(newScale); // 줌아웃을 하게해줌
				Log.e("Game / game", "cLocation1 : ("
						+ (int) currentLocation1.x + ", "
						+ (int) currentLocation1.y + "), pLocation1 : ("
						+ (int) previousLocation1.x + ", "
						+ (int) previousLocation1.y);
				Log.e("Game / game", "cLocation2 : ("
						+ (int) currentLocation2.x + ", "
						+ (int) currentLocation2.y + "), pLocation2 : ("
						+ (int) previousLocation2.x + ", "
						+ (int) previousLocation2.y);
				Log.e("Game / game",
						"MapCurrentPosition : " + this.getMapCurrentPosition());
				// theLayer.setPosition(this.getMapCurrentPosition()); // 선택한
				// 화면에서 줌아웃을 함
				theLayer.setPosition(CGPoint.ccpSub(theLayer.getPosition(),
						CGPoint.make(pinchCenter.x * positionScale,
								pinchCenter.y * positionScale))); // 선택한 화면에서
																	// 줌아웃을 함

				// scale 사용시 이상 현상 발생
				theLayer.setPosition(CGPoint.ccp(
						CGPoint.clampf(theLayer.getPosition().x,
								this.getMapMinPosition().x,
								this.getMapMaxPosition().x),
						CGPoint.clampf(theLayer.getPosition().y,
								this.getMapMinPosition().y,
								this.getMapMaxPosition().y)));
				// 외곽 영역 설정(현재 위치값이 틀려 버그임.)
				// 줌아웃할때 화면안에서만 줌 인 되게 해줌

				// mapLabel.setPosition(CGPoint.ccp(this.getPosition().x / 2 *
				// Xscale, this.getPosition().y / 2 * Yscale));
				// mapLabel.setString("mapPosition :"+ (int)this.getPosition().x
				// + ", " +(int)this.getPosition().y);

				// Log.e("Game / game", "this.getAnchorPoint : " +
				// this.getAnchorPoint() + " / this.getPosition : " +
				// (int)this.getPosition().x + ", " +(int)this.getPosition().y
				// );

				// debug label
				// mHud.updateDebugLabel(theLayer.getScale(),
				// theLayer.getPosition());
				isMove = false;
			}
			/*************************************/
			if (isMove) {
				// Log.e("Game / ccTouchesMoved", "touch : 1");

				// int pointerIndex =
				// event.findPointerIndex(event.getPointerId(0));

				// 뭐지??
				if (previousTouchLocation == null) {
					currentTouchLocation = CCDirector.sharedDirector()
							.convertToGL(
									CGPoint.ccp(event.getRawX(),
											event.getRawY()));
					previousTouchLocation = currentTouchLocation;
				} else if (!isMove2) {
					currentTouchLocation = CCDirector.sharedDirector()
							.convertToGL(
									CGPoint.ccp(event.getRawX(),
											event.getRawY()));
				} else // if (currentTouchLocation != null)
				{
					previousTouchLocation = currentTouchLocation;
					currentTouchLocation = CCDirector.sharedDirector()
							.convertToGL(
									CGPoint.ccp(event.getRawX(),
											event.getRawY()));
				}

				CGPoint deltaLocation = null;
				if (currentTouchLocation == null
						|| previousTouchLocation == null) {
					deltaLocation = CGPoint.ccp(0f, 0f);
				} else {
					deltaLocation = CGPoint.ccpSub(currentTouchLocation,
							previousTouchLocation);
				}
				Log.e("Game / Moved", "move2 : " + isMove2
						+ ", deltaLocation : " + deltaLocation);
				Log.e("Game / Moved", "previousTouchLocation : "
						+ previousTouchLocation);
				int area = 5;
				// adasdas
				if (deltaLocation.x < -1 * area || deltaLocation.x > area
						|| deltaLocation.y < -1 * area
						|| deltaLocation.y > area || isMove2) {
					// if (deltaLocation.x < -120 || deltaLocation.x > 120 ||
					// deltaLocation.y < -120 || deltaLocation.y > 120) {
					isMove2 = true;
					theLayer.setPosition(CGPoint.ccpAdd(theLayer.getPosition(),
							deltaLocation));
					// scale 사용시 이상 현상 발생
					theLayer.setPosition(CGPoint.ccp(
							CGPoint.clampf(theLayer.getPosition().x,
									this.getMapMinPosition().x,
									this.getMapMaxPosition().x),
							CGPoint.clampf(theLayer.getPosition().y,
									this.getMapMinPosition().y,
									this.getMapMaxPosition().y)));
					// Log.e("Game / Moved", "1touch theLayer1 : " +
					// theLayer.getPosition());
					// ccpAdd : return ccp(v1.x + v2.x,v1.y + v2.y);
					// 백그라운드 이미지 밖으로 deviceView 영역이 못 벗어나게 함
					// (없으면 화면이 끝나는곳에서 멈추는게 아니라 끝까지 움직임)

					// 외곽 영역 설정(현재 위치값이 틀려 버그임.)

					// mapLabel.setPosition(CGPoint.ccp(this.getPosition().x/2 *
					// Xscale,this.getPosition().y/2 * Yscale));
					// mapLabel.setString("mapPosition :"+
					// (int)this.getPosition().x + ", "
					// +(int)this.getPosition().y);

					// Log.e("Game / game", "this.getAnchorPoint : " +
					// this.getAnchorPoint() + " / this.getPosition : " +
					// (int)this.getPosition().x + ", "
					// +(int)this.getPosition().y );

					// scale
					currentScale = this.getScale(); // 움직인 현재 스케일을 저장
					currentLayerX = theLayer.getPosition().x; // 현재 화면의 x 값을 저장
					currentLayerY = theLayer.getPosition().y; // 현재 화면의 y 값을 저장

					// 이동방향에 맞추어 마법사 스프라이트 텍스처를 바꾸어 준다.
					int direction = this.getTouchMoveDirection(deltaLocation);
					mHud.setMagicianTo(direction);
				}
			}
			/*************************************/
		}
		return CCTouchDispatcher.kEventHandled;
	}

	boolean isMove2 = false;

	@Override
	public boolean ccTouchesEnded(final MotionEvent event) {
		if(mIsClickedEarth) {
			mIsClickedEarth = false;
			
			mHud.StartAniRune(Game.kButtonEarth);
			
			schedule(new UpdateCallback() {
				@Override
				public void update(float d) {
					unschedule(this);
					startEarth(event);
				}
			}, 2.4f);
			
			return true;
		}
		
		// Log.e("Game Touch", "Ended");
		currentTouchLocation = null;
		previousTouchLocation = null;
		currentLocation1 = null;
		previousLocation1 = null;
		currentLocation2 = null;
		previousLocation2 = null;
		mHud.setMagicianTo(kDirectionDown);
		isMove2 = false;
		return CCTouchDispatcher.kEventHandled;
	}
	
	public void startEarth(MotionEvent event) {
		removeChild(mEarthGuide, true);
		
		CGPoint glLocation = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getRawX(), event.getRawY()));
		CGPoint coord = this.tileCoordForPosition(convertToNodeSpace(glLocation));
		
		ArrayList<MineCell> copyCells = new ArrayList<MineCell>();
		copyCells.addAll(cells);
		for (MineCell cell : copyCells) {
			// 전체 타일을 검색하여 터치한 위치와 타일의 위치값이 일치할시
			if (CGPoint.equalToPoint(cell.getTileCoord(), coord)) {
				final ArrayList<MineCell> cells = new ArrayList<MineCell>();
				cells.add(cell);
				cells.addAll(cell.getRoundCells());
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						for(MineCell c : cells) {
							if(!c.isCollidable() && !c.isOpened()) {
								if(c.isMine()) {
									markMushroom(c, c.getTileCoord(), -1);
								} else {
									c.open();
								}
							}
						}
					}
				}).start();
			}
		}
	}

	// handle touches

	/*****************************/
	private CGPoint displayLimit() {
		// 화면 중간 좌표
		float wid = winSize.width / 2;
		float hei = winSize.height / 2;
		// layer부터 화면 중간까지의 좌표(GL --> cocos2D --> nodeSpace)
		return this.convertToNodeSpace(CCDirector.sharedDirector().convertToGL(
				CGPoint.make(wid, hei)));
	}

	// current layer position + (변한 좌표 - 기존 좌표) * (before scale - (after 좌표 /
	// before 좌표))
	private CGPoint setLayerPosition(CCLayer currentLayer,
			CGPoint displayBeforePosition, CGPoint displayAfterPosition,
			float beforeScale, float afterScale) {
		float x = currentLayer.getPosition().x
				+ (displayAfterPosition.x - displayBeforePosition.x)
				* (beforeScale - (displayAfterPosition.x / displayBeforePosition.x));
		float y = currentLayer.getPosition().y
				+ (displayAfterPosition.y - displayBeforePosition.y)
				* (beforeScale - (displayAfterPosition.y / displayBeforePosition.y));
		return CGPoint.make(x, y);
	}

	public CGPoint tileCenterPosition(CGPoint position) {
		CGPoint tileCoord = this.tileCoordForPosition(position);

		float x = tileCoord.x * tileSize.width;
		float y = tileCoord.y * tileSize.height;

		return CGPoint.make(x, y);
	}

	//
	// tile methods
	public void markFlag(CGPoint tileCoord) {

		int flagGid = this.tmxItemLayer.tileGIDAt(CGPoint.ccp(1, 0)); // 빨간깃발(방장)
		flagGid = CCFormatter.swapIntToLittleEndian(flagGid);
		this.tmxFlagLayer.setTileGID(flagGid, tileCoord);

		//
		// 지뢰수 하나 감소시킬시 디스플레이 업데이트 시킨다.
		mHud.updateMineNumber(decreaseMineNumber());
	}


	public CGPoint tileCoordForPosition(CGPoint position) {
		CGSize mapSize = this.mapSize;
		// 640 / 27
		// x /27
		int x = (int) (position.x / tileSize.width);
		int y = (int) ((mapSize.height - position.y) / tileSize.height); // for
																			// flip
																			// from
																			// bottom
																			// to
																			// top
		return CGPoint.ccp(x, y);
	}

	// metaLayer (작업 불가능 영역)
	public boolean isMetaChecked(CGPoint tileCoord, String metaString) {
		// tile이 있는 곳은 0이 아닌 값을 가진다.
		int gid = CCFormatter.swapIntToLittleEndian(tmxMeta.tileGIDAt(tileCoord));
		// tile이 깔린곳
		if (gid > 0) {
			// tileMap의 GID를 key로 넣어 HashMap으로 변환하여 Properties로 저장한다.
			HashMap<String, String> Properties = this.tileMap.propertiesForGID(gid);
			// <--- 문제 지점은 아마 키값이 안들어 있어서 인것같다 확인 필요.
			if (Properties != null && Properties.size() != 0) {
				// tile을 만들때 tile에 이름을 넣었던 것과 같은 이름의 타일을 찾는다.
				String collisionValue = Properties.get(metaString);
				if (collisionValue != null && collisionValue.equals("YES")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDontSetMine(CGPoint tileCoord) {
		return this.isMetaChecked(tileCoord, "isDontSetMine");
	}

	//
	// 현재 테스트 타일맵에서 isPreOpened타일은 메타타일 락 바로 오른쪽 투명 타일을 이용하고 있음????
	public boolean isPreOpened(CGPoint tileCoord) {
		return this.isMetaChecked(tileCoord, "isPreOpened");
	}

	public boolean isCollidable(CGPoint tileCoord) {
		return this.isMetaChecked(tileCoord, "isCollidable");
	}

	public void removeFlag(CGPoint tileCoord) {
		this.tmxFlagLayer.removeTileAt(tileCoord);

		//
		// 지뢰수 하나 증가시키고 디스플레이 업데이트 시킨다.
		mHud.updateMineNumber(increaseMineNumber());
	}

	public CCTMXLayer getFg() {
		return tmxFg;
	}

	public void setFg(CCTMXLayer fg) {
		this.tmxFg = fg;
	}

	// 뭔가 다름...
	public CCSprite animationMagma() {
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
				folder + "effectMagma-hd.plist");
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 9; i++) {
			String frameName = "magma" + i + ".png";
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache()
					.spriteFrameByName(frameName));
		}
		// 음... name String이 다르다
		CCAnimation animation = CCAnimation.animation("", 0.1f, frames);
		CCAnimate animate = CCAnimate.action(animation);
		CCSprite sprite = CCSprite.sprite("magma1.png");
		CCSequence sequence = CCSequence.actions(animate);
		CCRepeatForever repeat = CCRepeatForever.action(sequence);
		sprite.runAction(repeat);
		return sprite;
	}

	public void effectMagmaOn() {
		this.tmxBg.setVisible(false);
		ArrayList<MineCell> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			CCSprite sprite = this.animationMagma();
			sprite.setTag(cellsTemp.get(k).getCell_ID() * 10);
			this.addChild(sprite, -5);
			sprite.setPosition(cellsTemp.get(k).getTilePosition());
		}
	}

	public void effectMagmaOff() {
		this.tmxBg.setVisible(true);
		ArrayList<MineCell> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			this.removeChildByTag(cellsTemp.get(k).getCell_ID() * 10, true);
		}
	}

	//
	final int kDirectionDown = 0;
	/*** ↓ ***/
	final int kDirectionLeftDown = 45;
	/*** ↙ ***/
	final int kDirectionLeft = 90;
	/*** ← ***/
	final int kDirectionLeftUp = 135;
	/*** ↖ ***/
	final int kDirectionUp = 180;
	/*** ↑ ***/
	final int kDirectionRightUp = 225;
	/*** ↗ ***/
	final int kDirectionRight = 270;
	/*** → ***/
	final int kDirectionRightDown = 315;
	/*** ↘ ***/

	//
	// 화면 움직일때
	public int getTouchMoveDirection(CGPoint deltaVector) {

		CGPoint dv = deltaVector; // delta vector
		float gx = Math.abs(dv.x) / Math.abs(dv.y); // x grade
		float gy = Math.abs(dv.y) / Math.abs(dv.x); // y grade
		float tg = 2.0f; // trigger grade

		boolean isDiagonal = (gx > 1 / tg && gx < tg) ? true : false;
		boolean isVertical = (gy > tg) ? true : false;
		boolean isHorizontal = (gx > tg) ? true : false;

		// Log.e("Direction", "Vector:" +dv + ", gx:" +((int)(gx*10))/10f +
		// ", gy:" +((int)(gy*10))/10f);

		if (isDiagonal) {
			if (dv.x > 0 && dv.y > 0) {
				// Log.e("Direction", "↗");
				return kDirectionRightUp;
			} else if (dv.x > 0 && dv.y < 0) {
				// Log.e("Direction", "↘");
				return kDirectionRightDown;
			} else if (dv.x < 0 && dv.y > 0) {
				// Log.e("Direction", "↖");
				return kDirectionLeftUp;
			} else if (dv.x < 0 && dv.y < 0) {
				// Log.e("Direction", "↙");
				return kDirectionLeftDown;
			}
		}

		if (isVertical) {
			if (dv.y > 0) {
				// Log.e("Direction", "↑");
				return kDirectionUp;
			} else {
				// Log.e("Direction", "↓");
				return kDirectionDown;
			}
		}

		if (isHorizontal) {
			if (dv.x > 0) {
				// Log.e("Direction", "→");
				return kDirectionRight;
			} else {
				// Log.e("Direction", "←");
				return kDirectionLeft;
			}
		}
		// Log.e("Direction", "return 0");
		return 0;
	}
	
	// 다시 체크 해볼 것 
	public int  sumScore() {
		int myScore = 0;
		
		float openedCell = GameData.share().getOpenedCell();
//		float foundMine = mGame.getFoundMine();
		float foundMine = GameData.share().getCurrentMine(); // 올바르게 버섯이 심겨진 지뢰만(찾은 호박)
		float maxMine = GameData.share().getMineNumber(); // 테스트중
		float heart = GameData.share().getHeartNumber();
		float spentTime = 900 - GameData.share().getSeconds(); // 소요 시간
		
		if (heart > 0) {
			myScore = (int) ((((foundMine + heart) * maxMine) + spentTime) * maxMine * 0.006f);
		}
		
		Log.e("MineCell", "myScore : " + myScore + ", openedCell : " + openedCell + ", foundMine : " + foundMine + ", heart : " + heart + ", maxMine : " + maxMine + ", spentTime : " + spentTime);
		
		return myScore;
	}
	
	public void messageReceived(int messageType, Object obj) {
		mHud.mGameProgressBar.stopTime();
		Log.e("Game", "kmessageRequestScore = 15, kmessageGameOver = 6, kmessageOpponentConnectionLost = 9");
		Log.e("Game", "messageReceived - messageType : " + messageType);
		final int kmessageRequestScore = 15;
		final int kmessageGameOver = 6;
		final int kmessageOpponentConnectionLost = 9;
		
		int myScore = 0;
		int otherScore = -1;
		if (obj != null)
			otherScore = (Integer) obj;		
		
		myScore = sumScore();

		if (myScore < otherScore)
			Config.getInstance().setVs(Config.getInstance().vsLose);
		else 
			Config.getInstance().setVs(Config.getInstance().vsWin);
		
		switch (messageType) {
		case kmessageRequestScore:
			if (GameData.share().isMultiGame) { // 막을필요는 없지만 의외로 실행 되는것을 방지하기위해 막아놓음
				try {
					NetworkController.getInstance().sendRequestGameOver(myScore);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
			
		case kmessageGameOver:
			mHud.gameOver(myScore, otherScore);
			break;
			
		case kmessageOpponentConnectionLost:
			mHud.gameOver(myScore, 0);
			break;
		}
	}

	int foundMine = 0;
//	public void setFoundMine(int value) {
//		foundMine += value;
//	}
	
	public int getFoundMine() {
		return GameData.share().currentMine;
	}
	
//	public int markedMine() {
//		int _markedMine = markedMine + previousMarkedMine; 
//		return _markedMine;
//	}
	
	public int getClosedCell() {
		return unopenedTile;
	}
	
	public void removeCell() {
		unopenedTile--;
		Log.e("Game", "타일 제거됨. : " + getClosedCell()); // 테스트용
	}
	
	public int getMineNumber() {
		return mineNumber;
	}
	
	public int increaseMineNumber() {
		mineNumber ++;
		return mineNumber;
	}

	public int decreaseMineNumber() {
		mineNumber --;
		return mineNumber;
	}
	
//	private boolean _soundPlaying = false;
//	private boolean _soundPaused = false;
//	private boolean _resumeSound = false;
//	
//	public void bgMusicClicked(View button)
//	{
//	    // If we haven't started playing the sound - play it!
//	    if (!_soundPlaying)
//	    {
//	        SoundEngine.sharedEngine().playSound(mContext, R.raw.bgm, true);
//	        _soundPlaying = true;
//	    }
//	    else
//	    {
//	        // We've loaded the sound, now it's just a case of pausing / resuming
//	        if (!_soundPaused)
//	        {
//	            SoundEngine.sharedEngine().pauseSound();
//	            _soundPaused = true;
//	        }
//	        else
//	        {
//	            SoundEngine.sharedEngine().resumeSound();
//	            _soundPaused = false;
//	        }
//	    }
//	}
	
	//정령석 유리병 여는 애니메이션
	public void startOpenBottle(CGPoint pos) {
		CCSprite bottle = CCSprite.sprite("61hud/bottle_01.png");
		
		pos = CGPoint.ccp(pos.x + tileSize.width/2, pos.y - tileSize.height/2);
		
		bottle.setPosition(pos);
		// 기본 z값은 -1이며 지뢰숫자는 100000으로 설정되어있어 애니가 숫자보다 밑에 보이는겁니다.
		addChild(bottle); 
//		addChild(bottle, mineNumberLayer + 1); // 지뢰숫자보다 높게 설정함. //원래대로 돌려 놨습니다. 
		
		CCAnimate action = CCAnimate.action(1.0f, mBottle, false);
		CCCallFuncN remove = CCCallFuncN.action(this, "cbRemoveSprite"); // 익셉션 발생에 따른 막아둠.
		bottle.runAction(CCSequence.actions(action, remove));
		bottle.runAction(CCSequence.actions(action));
	}
	
	public void cbRemoveSprite(Object sender) {
		CCSprite sprite = (CCSprite)sender;
		sprite.removeFromParentAndCleanup(true);
	}
	
	
	public void startEarthBomb() {
		CCSprite bomb = CCSprite.sprite("61hud/earth-bomb01.png");
		//붙이는 위치, 크기 조정해야 함.
		bomb.setPosition(winSize.width*0.5f, winSize.height*0.5f);
		addChild(bomb, 100);
		
		CCAnimate action = CCAnimate.action(1.2f, mEarthBomb, false);
		CCCallFuncN remove = CCCallFuncN.action(this, "cbRemoveBomb");
		
		bomb.runAction(CCSequence.actions(action, remove));
	}
	
	public void cbRemoveBomb(Object sender) {
		CCSprite sprite = (CCSprite)sender;
		sprite.removeFromParentAndCleanup(true);
		
		//9칸 타일 벗기기
	}
	
	//gameOver 애니메이션 : map zoomout 후 폭탄 터지는 애니메이션
	public void gameOverAnimation() {
		//zoom out
		CCScaleTo scale = CCScaleTo.action(2.0f, 0.43f); 
		CCMoveTo move = CCMoveTo.action(2.0f, CGPoint.ccp(0, 0));
		
		this.runAction(move);
		this.runAction(scale);
		
		CCAnimation pumpkin = CCAnimation.animation("pumpkin");
		for( int i=1;i<=7;i++) {
			pumpkin.addFrame(String.format("60game/pumpkinbomb_%02d.png", i));
		}
		final CCAnimate pumpkinBomb = CCAnimate.action(1.2f, pumpkin, true);
		
		//폭탄 터지기 : 타일 오픈후 pumpkin 애니메이션
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				unschedule(this);
				for(MineCell cell : cells) {
					if(cell.isMine()) {
						getFg().removeTileAt(cell.getTileCoord());
						
						CCSprite bomb = CCSprite.sprite("60game/pumpkinbomb_01.png");
						bomb.setPosition(cell.getTilePosition());
						theLayer.addChild(bomb, 100);
						
						bomb.runAction(pumpkinBomb.copy());
					}
				}
			}
		}, 2.0f);
		
	}
	
	//불 피해 애니 : 열려진 땅을 cracklayer로 변환
	public void startFire(int attackTime) {
		//int crackGID = tmxEarthLayer.tileGIDAt(CGPoint.ccp(0, 0));
		//crackGID = CCFormatter.swapIntToLittleEndian(crackGID);
		
		CCAnimate magma = CCAnimate.action(1.5f, mMagma, false);
		CCRepeatForever magmaAni = CCRepeatForever.action(magma);
		
		CCAnimate magmafire = CCAnimate.action(1.0f, mMagmaFire, false);
		CCRepeatForever magmafireAni = CCRepeatForever.action(magmafire);
		
		for(MineCell cell : cells) {
			if(cell.isOpened() && !cell.isSphere()) {
				//셀위에 숫자가 있다면 감춘다.
				if(cell.numberOfArroundMine > 0) {
					this.getChildByTag(cell.getCell_ID()).setVisible(false);
					mNumberTags.add(cell.getCell_ID());
				}

				//애니가 멈출때 애니를 삭제하기 위해 태그 할당
				int tag = 10000 + cell.getCell_ID();

				Random rand = new Random();
				int r = rand.nextInt(100);
				if(r < 40) { //30%
					//깨진 대지 타일 세팅
					//this.tmxFlagLayer.setTileGID(crackGID, cell.getTileCoord());
					CCSprite crack = CCSprite.sprite("60game/crackearth.png");
					this.addChild(crack, 20, tag);
					crack.setPosition(cell.getTilePosition());
					crack.setAnchorPoint(0.5f, 0.5f);
					Log.d("LDK", "crack earth position:" + cell.getPosition().x + "," + cell.getPosition().y);
					
					//용암 애니
					CCSprite sprite = CCSprite.sprite("60game/magma_01.png");
					sprite.setScale(0.5f);
					crack.addChild(sprite, -1);
					sprite.setPosition(crack.getContentSize().width/2, crack.getContentSize().height/2);
					sprite.runAction(magmaAni.copy());
					
					//불기둥 애니
					CCSprite sprite2 = CCSprite.sprite("60game/magmafire_01.png");
					crack.addChild(sprite2, 1);
					sprite2.setPosition(crack.getContentSize().width/2, crack.getContentSize().height/2);
					sprite2.setAnchorPoint(0.5f, 0.2f);
					sprite2.runAction(magmafireAni.copy());
					
					//삭제하기 위해 태그를 저장
					mDeleteTags.add(tag);
				}
			}
		}
		
		long spanTime = (attackTime - mFireDefenseTime - UserLevel);
		spanTime = spanTime > 0 ? spanTime : 0;
		
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				stopAttack();
				unschedule(this);
			}
		}, spanTime);
	}
	
	//바람 피해 애니===========================================================
	public void startWind(int attackTime) {
		
		for(MineCell cell : cells) {
			if(cell.isOpened()) {
				if(cell.numberOfArroundMine > 0) {
					//셀위에 숫자가 있다면 감춘다.
					this.getChildByTag(cell.getCell_ID()).setVisible(false);
					mNumberTags.add(cell.getCell_ID());
					
					//셀에 숫자를 생성하고 애니메이션 시킨다.
					int tag = 10000 + cell.getCell_ID();
					
					//라벨 스프라이트 생성
					Random rand = new Random();
					int r = rand.nextInt(8);
					final CCLabel label = CCLabel.makeLabel(String.valueOf(r+1), "Arial-Bold", (int) ((70 * (2 / 3.0) * tileSize.width) / 128));
					addChild(label, 10, tag);
					label.setAnchorPoint(0.5f, 0.5f);
					label.setPosition(cell.getTilePosition());
					label.setColor(ccColor3B.ccc3((int) (75 / 255f), (int) (51 / 255f), (int) (9 / 255f)));
					
					//회전하는 속도를 랜덤하게 조정 0.1f, 18f는 0.1초당 18도회전이 기본 + 랜덤 18도
					CCRotateBy rot = CCRotateBy.action(0.1f, 18f + new Random().nextInt(36));
					CCRepeatForever repeat = CCRepeatForever.action(rot.copy());
					label.runAction(repeat.copy());
					mDeleteTags.add(tag);
				}
			}
		}
		
		schedule("increaseNumber", 0.1f); //숫자 증가시키는 콜백
		
		long spanTime = (attackTime - mWindDefenseTime - UserLevel);
		spanTime = spanTime > 0 ? spanTime : 0;
		
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				stopAttack();
				unschedule(this);
				unschedule("increaseNumber");
			}
		}, spanTime);
	}
	
	public float mIncreaseTime = 0;
	public void increaseNumber(float dt) {
		mIncreaseTime += dt;
		
		while (mIncreaseTime >= 0.5f) {
			for(Integer i : mDeleteTags) {
				CCLabel label = (CCLabel) getChildByTag(i);
				int value = Integer.parseInt(label.getString());
				label.setString(String.valueOf(value%8 + 1));
			}
			mIncreaseTime -= 0.5f;
		}
	} //------------------------------
	
	//구름 피해 애니
	public void startCloud(int attackTime) {
		ArrayList<CGPoint> clouds = new ArrayList<CGPoint>();
		CGPoint location = CGPoint.ccp(winSize.width * 0.5f, winSize.height * 0.2f);
		CGPoint pos = this.convertToNodeSpace(location);
		clouds.add(pos);
		clouds.add(CGPoint.ccpAdd(pos, CGPoint.ccp(0, tileSize.height * 8)));
		clouds.add(CGPoint.ccpAdd(pos, CGPoint.ccp(tileSize.width * -4, tileSize.height*4)));
		clouds.add(CGPoint.ccpAdd(pos, CGPoint.ccp(tileSize.width * 4, tileSize.height*4)));
		
		Log.d("LDK","CGPoint:" + winSize.width * 0.5f + "," + winSize.height * 0.2f);
		Log.d("LDK","pos:" + pos.x + "," + pos.y);
		Log.d("LDK","this.position:" + this.getPosition().x + "," + this.getPosition().y);

		//CCFadeOut out = CCFadeOut.action(0.01f);
		//CCFadeIn in = CCFadeIn.action(2f);
		
//		CCAnimate action = CCAnimate.action(1f, cloudDefense, false);
//		CCAction repeat = CCRepeatForever.action(action);
		CCScaleTo scale1 = CCScaleTo.action(0.1f, 0.98f);
		CCScaleTo scale2 = CCScaleTo.action(0.1f, 1.0f);
		CCAction repeat = CCRepeatForever.action(CCSequence.actions(scale1, scale2));
		
		for(int k=0; k<4; k++) {
			int tag = 10000 + k;

			CCSprite cloud = CCSprite.sprite("61hud/fx-cloud1.png");
			cloud.setOpacity(128 + new Random().nextInt(128));
			cloud.setPosition(clouds.get(k)); //구름 생성 위치
			cloud.setAnchorPoint(CGPoint.ccp(0.5f, 0.5f));
			addChild(cloud, 8, tag);
			mDeleteTags.add(tag);
	
			cloud.runAction(repeat.copy());
			
			CCDelayTime delay = CCDelayTime.action(k*0.15f);
			cloud.runAction(delay);
		}
		
		long spanTime = (attackTime - mCloudDefenseTime - UserLevel);
		spanTime = spanTime > 0 ? spanTime : 0;
		
		schedule(new UpdateCallback() {
			@Override
			public void update(float d) {
				stopAttack();
				unschedule(this);
			}
		}, spanTime);
	}
	
	//마법 피해 애니 종료============================
	public void stopAttack() {
		//애니메이션 삭제
		for(Integer i : mDeleteTags) {
			removeChildByTag(i, true);
		}
		//삭제후 컬렉션 초기화
		mDeleteTags.clear();
		
		//셀 숫자 복원
		for(Integer i : mNumberTags) {
			getChildByTag(i).setVisible(true);
		}
		mNumberTags.clear();
		
		//마법사 감전 애니 정지
		mHud.stopShockAni();
		
		unschedule("increaseNumber");
	}

	abstract class TileOpenTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			run();
			return null;
		}
		
		public abstract void run();
		
	}



}
// Game class end
