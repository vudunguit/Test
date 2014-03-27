package com.aga.mine.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXLayer;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.CCFormatter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;
import com.aga.mine.mains.Utility;

public class Game extends CCLayer implements MineCell2.MineCellDelegate{
	
	String folder = "60game/";
	String fileExtension = ".png";
	
	CCLayer theLayer = this;
	
	/********************/
	
	public static Context mContext;
	GameProgressBar progress;
	CGSize winSize;
	
	ArrayList<MineCell2> cells;
	ArrayList<MineCell2> sphereBaseCells;

	final static int kButtonFire = 1;
	final static int kButtonWind = 2;
	final static int kButtonCloud = 3;
	final static int kButtonDivine = 4;
	final static int kButtonEarth = 5;
	final static int kButtonMirror = 6;
	final static int kButtonMinimap = 11;
	final static int kButtonEmoticon = 12;
	
	final int kSphereTypeNone = -1;
	final int kSphereTypeEmpty = 0;
	final int kSphereTypeFire = 1;
	final int kSphereTypeWind = 2;
	final int kSphereTypeCloud = 3;
	final int kSphereTypeDivine = 4;
	final int kSphereTypeEarth = 5;
	final int kSphereTypeMirror = 6;
	// ----------------------- Game.m --------------------------//
	private CCTMXTiledMap tileMap;
	CCTMXLayer bg;
	CCTMXLayer meta;
	CCTMXLayer fg;
	CCTMXLayer mineLayer;
	CCTMXLayer itemLayer;
	CCTMXLayer flagLayer;
	CCTMXLayer earthLayer;
	CCSprite player;
	
	HudLayer hud;
	
	float currentLayerX;
	float currentLayerY;
	float currentScale;

	CCLabel win2;
	float Xscale;
	float Yscale;
	CCLabel center3;
	
	static int unopenedTile;
	
	public CCTMXTiledMap getTileMap() {
		return tileMap;
	}
//
//	private static Game game;
//	
//	public static synchronized Game getInstance() {
//		if (game == null) {
//			game = new Game();
//			Log.e("** Game **", "make Single Instance");
//		}
//		return game;
//	}
//	
//	public void dealloc(){
//		Log.e("** Game **", "dealloc");
//		game = null;
//	}
//	
	
	// gamedata에서 수정하였음.
	// 지뢰수 수정하여 테스트중 현재 3개로 수정
	// 수정구 1개로 수정
	/** Game
	 * @param context
	 */
	Game() {
		Log.e("** Game **", "Instance");
		GameMinimap.getInstance().dealloc();
		 hud.isGameOver = false;
		unopenedTile = 0;
		// 기본 초기화
		mContext = CCDirector.sharedDirector().getActivity().getApplicationContext();
//		winSize = CCDirector.sharedDirector().winSize();
		winSize = CCDirector.sharedDirector().winSize();

		//
		if (GameData.share().isMultiGame) {
			Config.getInstance().setDisableButton(true);
		} else {
			Config.getInstance().setDisableButton(false);
		}

		//난이도 ( 0~2 초,중,상급)
		GameData.share().setGameDifficulty(1);
		// 데이터를 들어있는 숫자 깃발 초기화
		GameData.share().resetMineNumber();
		// 호박에 나타나있는 숫자 깃발 초기화
		GameData.share().setMineNumber(GameData.share().maxMineNumber);
		//GameData.share().setMineNumber(GameData.share().getGameDifficulty());
		Log.e("Game / game ", "난이도 : " + GameData.share().getGameDifficulty());
		// 생명수 초기화
		GameData.share().setHeartNumber(3); 		
		// 아이템 초기화
		GameData.share().resetItem();
//		// 게임시간 초기화
//		GameData.share().setSeconds(900); // gameStart로 이동

		//
		// 탭 제스쳐 등록
		// 안드로이드 다른방식이라 현재로서는 cocos2D와 어려움.
		// selector 메소드 확인할것
		//CCTouchDispatcher a = CCTouchDispatcher.
		// ?????
		ArrayList<View> a = new ArrayList<View>();
		a.add(CCDirector.sharedDirector().getOpenGLView());
		CCDirector.sharedDirector().getOpenGLView().addTouchables(a);
		
		 //
		 // 사운드 (로드)
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_open2); // 이펙트 (효과음) // (타일)pickup
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_pumpkin); // 이펙트 (효과음) // (호박)hit
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_mushroom); // 이펙트 (효과음) // (버섯)move
		
		//
		// 타일맵 로드
		this.tileMap = CCTMXTiledMap.tiledMap(GameData.share().gameMap);
		 
		//
		// 맵 올리고 기본 크기 지정
		this.addChild(this.tileMap, -1);

		//texture.setAntiAliasTexParameters();
		//texture.setAliasTexParameters();
		/*
		CCTexture2D texture = new CCTexture2D();
		for (CCNode child : tileMap.getChildren()) {
			((CCTMXLayer)child).setTexture(texture);
		}
		*/
		// 64 pixel
		tileSize = CGSize.make(tileMap.getTileSize().width, tileMap.getTileSize().height);
		mapSize = CGSize.make(tileMap.getMapSize().width * tileSize.width, tileMap.getMapSize().height * tileSize.height);

		 //
		 // 타일맵 레이어 등록
		/*
		this.bg = this.tileMap.layerNamed("Background"); // Layer Name in Tiled
		this.meta = this.tileMap.layerNamed("Meta");
		this.meta.setVisible(false);
		this.fg = this.tileMap.layerNamed("Foreground");
		this.mineLayer = this.tileMap.layerNamed("MineLayer");		 // 지뢰 및 아이템 뿌릴 레이어
		this.itemLayer = this.tileMap.layerNamed("ItemLayer");			 // 지뢰 및 아이템, 깃발 가져오는 레이어
		this.flagLayer = this.tileMap.layerNamed("FlagLayer");			 // 깃발 꽂을 레이어
		this.earthLayer = this.tileMap.layerNamed("CrackedEarth");	 // 갈라진대지 레이어
	*/
		this.itemLayer = this.tileMap.layerNamed("ItemLayer");			 // 지뢰 및 아이템, 깃발 가져오는 레이어
		this.earthLayer = this.tileMap.layerNamed("CrackedEarth");	 // 갈라진대지 레이어(아이템 이펙트)
		this.bg = this.tileMap.layerNamed("Background");					 // Layer Name in Tiled
		this.mineLayer = this.tileMap.layerNamed("MineLayer");		 // 지뢰(호박) 및 아이템 뿌릴 레이어
		this.fg = this.tileMap.layerNamed("Foreground");					 // 잔디
		this.flagLayer = this.tileMap.layerNamed("FlagLayer");			 // 깃발(버섯) 꽂을 레이어
		this.meta = this.tileMap.layerNamed("Meta");							 // 선택 불가 영역
		this.meta.setVisible(false);
	
		
		theLayer.setScale(GameConfig.share().kDefaultScale * 128 / tileMap.getTileSize().width);
		theLayer.setPosition(	(-mapSize.width/2 * theLayer.getScale() + winSize.width /2), 0);
		//theLayer.setPosition(getMapCenterPosition().x, getMapCenterPosition().y - getMapDeltaSize().height);
		currentScale = this.getScale(); // 처음 스케일 저장		
		 currentLayerX = this.getPosition().x; // 처음 화면의 x좌표 저장
		 currentLayerY = this.getPosition().y; // 처음 화면의 y좌표 저장
//
//		 progress = new GameProgressBar(mContext);
//		 progress.delegate = (GameProgressBarDelegate) this;
//		 
		// 전체 타일(셀) 등록
		cells = new ArrayList<MineCell2>();
		sphereBaseCells = new ArrayList<MineCell2>();
		int count = 0;

		 float wid = tileMap.getMapSize().width;
		 float hei =  tileMap.getMapSize().height;
	
		
		 for (int x = 0; x < (int)tileMap.getMapSize().width; x++) {
			 for (int y = 0; y < (int)tileMap.getMapSize().height; y++) {
				 MineCell2 cell = new MineCell2();
		
				 cell.delegate = this;
				 cell.setTileCoord(CGPoint.make(x, y));
				 cell.setCell_ID(count);
				 cell.setTilePosition(CGPoint.ccp(
						 x * tileSize.width + tileSize.height /2, 
						 mapSize.height - (y * tileSize.height + tileSize.height / 2)));
				 cells.add(cell);
//				 
//					// test code // 수정구가 숨겨진 타일에 표시한 라벨
//					CCLabel label = CCLabel.makeLabel(""+ cell.getCell_ID(), "Arial", (30 * tileSize.width) / 128);
//					this.addChild(label);
//					label.setColor(ccColor3B.ccWHITE);
//					label.setPosition(cell.getTilePosition());
//				 
				 if (!this.isCollidable(CGPoint.make(x, y)) && !this.isPreOpened(CGPoint.make(x, y))) {
					// 열리지 않은 타일수
					 unopenedTile ++;
				}
				 //
				 //isCollidable은 비활성  셀을 의미한다.
				 if (this.isCollidable(CGPoint.make(x, y))) 
					cell.setCollidable(true);
				 
				//
				// isPreOpened는 처음부터 열려있는 셀들을 열린셀로 등록한다.
				if (this.isPreOpened(CGPoint.make(x, y)))
					cell.setOpened(true);

				count++;
				
			}
		}
		 Log.e("Game / game ", "unopenedTile : " + unopenedTile);
		
		//
		// 주변타일 등록(선택지점)
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			/***************************/
			for (int k = -1; k < 2; k++) {
				for (int m = -1; m < 2; m++) {
					//
					// 맵 바깥 셀
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) continue;
					if (cellsTemp.get(i).getTileCoord().x + m < 0 || cellsTemp.get(i).getTileCoord().x + m >= this.tileMap.getMapSize().height) continue;

					//
					// 주변 셀
					MineCell2 cellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));

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
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}
					if (cellsTemp.get(i).getTileCoord().x + m < 0 || cellsTemp.get(i).getTileCoord().x + m >= this.tileMap.getMapSize().height) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}

					//
					// 수정구 위치 셀
					MineCell2 sphereCell = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));

					//
					// 비활성 셀					
					if (sphereCell != null && sphereCell.isCollidable()){
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
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) continue;
					if (cellsTemp.get(i).getTileCoord().y + m < 0 || cellsTemp.get(i).getTileCoord().y + m >= this.tileMap.getMapSize().height) continue;

					//
					// 스피어 (본체) 위치 셀 - (본체가 있는 셀은 sphereRoundCell 등록에서 제외)
					if (k == 0 ||  k == 1) {
						if (m == 0 || m == 1)
							continue;
					}
					
					//
					// 주변셀 (본체가 위치한 셀을 제외한)
					MineCell2 sphereCellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));
					
					//
					// 비활성 셀 - 은 등록하지 않는다. (null이 나올수 없다.)
					if (sphereCellRound.isCollidable()) continue;
					cellsTemp.get(i).addSphereRoundCell(sphereCellRound); // 여기는 이상 없는듯
				}
			}
			
		}
		//주변타일 등록 for(i) end
		
		//
		// debug
		cellsTemp = cells;
		size = cellsTemp.size();
		for (int i = 0; i < size; i++) {

			// coord
			// isSphereBasePossible
		}
		// debug end

//		//
//		//지뢰 설치
//		this.scatterMines();
		
		// 원래 순서는 수정구부터 
		//
		// 수정구 설치
		this.scatterSpheres();
		

		//
		// 지뢰 설치
		this.scatterMines();
		
		//
		// 사전에 열려진 셀 주변에 지뢰가 설치되었으면 지뢰수 표시를 한다.
		cellsTemp = cells;
		size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			if(cellsTemp.get(i).isOpened()) {
				int numberOfMine = cellsTemp.get(i).getNumberOfMineAround();
				if (numberOfMine > 0)
					this.displayMineNumber(numberOfMine, cellsTemp.get(i).getTilePosition(), cellsTemp.get(i).getCell_ID());
			}
		}

		//
		// 갈라진 대지 레이어(this.earthLayer)에 타일을 랜덤하게 채운다.
		cellsTemp = cells;
		size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			int gid = this.itemLayer.tileGIDAt(CGPoint.make((float)(Math.random() * 4), 8));
			gid = CCFormatter.swapIntToLittleEndian(gid);
			this.earthLayer.setTileGID(gid, cellsTemp.get(i).getTileCoord());
		}
		
		///////////////////////////////////////
		//Log.e("Game / game", "this.getAnchorPoint : " + this.getAnchorPoint() + " / this.getPosition : " + (int)this.getPosition().x + ", " +(int)this.getPosition().y );
		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
		this.setIsTouchEnabled(true); 

//		mapLabel = CCLabel.makeLabel("map", "Arial", (30 * tileSize.width) / 128);
//		this.addChild(mapLabel);
//		mapLabel.setPosition(getPosition());
		if (!GameData.share().isGuestMode){
			
			if (GameData.share().isMultiGame) {
				aaaaa();
			}
			
		} else {
			GameData.share().isMultiGame = false;
			bbbbb();
		}
			
		// 게임시간 초기화
		GameData.share().setSeconds(900);
		UserData.share(mContext).myBroomstick();
	}
	// 생성자Game end
	
	private void aaaaa() {
			Log.e("Game / game", "I'm Ready!");
			try {
				NetworkController.getInstance().sendGameReady();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void bbbbb() {
		// 게임시간 초기화
		GameData.share().setSeconds(900);
		Config.getInstance().setDisableButton(false);
//		Game.HudLayer.testText.setString("게임 시작! testText"); // 느린 기기에서는 뻗어버림...
	}
	
	static GameEnding ending = null;
//
//	public static boolean getEnding() {
//		return ending.getVisible();
//	}
//
//	public static void setEnding(boolean visible) {
//		ending.setVisible(visible);
//	}
//	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//
/********************************************************/
/**
 * 문제지점
 * 애니메이션
 * 
 * 이모티콘 애니는 위치가 정해지지 않았습니다.
 * Game 또는 Hud중 무난한 곳에 넣어주시면 됩니다.
 **/
	static CCScene scene = null;
//	public CCScene scene() {
	public static CCScene scene() {
		// 기존에는 static으로 멤버 변수로 선언하였었음. 이상시 원래 대로 바꿀것
		scene = CCScene.node();
		
		// 게임 레이어
//		Game game = Game.getInstance();		
		Game game = new Game();		
		scene.addChild((CCLayer)game);
		game.setAnchorPoint(0.0f, 0.0f); /*** 중요 ***/
		
		// 헤드 업 디스플레이 레이어
		HudLayer hudLayer = new HudLayer();
		scene.addChild((CCLayer)hudLayer);
		game.hud = hudLayer;
		
//		ending = GameEnding.share();
		//scene.addChild(ending, GameConfig.share().kDepthPopup);
//		hudLayer.addChild(ending, GameConfig.share().kDepthPopup, 1234);
//		ending.setVisible(false);
		//game.hud.gameEnding = ending;
		//game.hud.controlHudLayer = hudLayer;
		/*
		GameEmoticon gameEmoticon = new GameEmoticon();
		//hudLayer.addChild(gameEmoticon);
		scene.addChild(gameEmoticon);
		*/
		
		return scene;
	}	
/********************************************************/

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	// map utility
	// 현재 화면의 position을 지정
	public CGPoint getMapCurrentPosition() {
		// 화면을 줌아웃하면 여기를 계속탐.
		// 화면을 멈췄을때 currentLayerX,currentLayerY, currentScale 값을
		//터치무브 (allTouches count == 1)에서 저장한후  그 값으로 이곳에서 계산해줌
		//CGPoint centerPosition = CGPoint.ccp(currentLayerX / currentScale, currentLayerY / currentScale);
		// game Layer의 anchor point값을 얻어 스케일값과 연산

		Log.e("Game / getMapCurrentPosition", "cLayerX : " + (int)currentLayerX + ", cLayerY : " +(int)currentLayerY + ", cScale : " + currentScale);		
		//CGPoint centerPosition = CGPoint.ccp((winSize.width + currentLayerX) / currentScale, (winSize.height + currentLayerY) / currentScale);
		CGPoint centerPosition = CGPoint.ccp(currentLayerX / currentScale, currentLayerY / currentScale);
		Log.e("Game / getMapCurrentPosition", "centerPosition1 : "  + centerPosition);
		centerPosition = CGPoint.ccpMult(centerPosition, this.getScale());
		Log.e("Game / getMapCurrentPosition", "centerPosition2 : "  + centerPosition);
		return centerPosition;
	}
		
	
	// cell utility
	public MineCell2 cellFromCoord(CGPoint coord) {
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			
			if(CGPoint.equalToPoint(cellsTemp.get(k).getTileCoord(), coord))
				return cellsTemp.get(k);
		}
		return null;
	}
	
	public MineCell2 cellFromCellId(int	unsingedCellId) {
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			if(cellsTemp.get(k).getCell_ID() == unsingedCellId)
				return cellsTemp.get(k);
		}
		return null;
	}
	
	//
	// 난이도 설정 (난이도에 따라 지뢰수가 많아짐)
	public void scatterMines() {
		 //(void)scatterMines:(NSMutableArray *)cells onLayer:(CCTMXLayer *)layer{ 수정된 것이라는데...
		//
		// 난이도에 따라
		//GameData.share().getMineNumber(gameData.share.getGameDifficulty);
		final int maxMineNumber = GameData.share().getMineNumber();
		//Log.e("Game / scatterMines", "getMineNumber : " + GameData.share().getMineNumber());
		
		// unsigned
		// 작은값으로 변환
		int mineGid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(0f, 0f))); // mind gid
		//Log.e("Game / scatterMines", "mineGid : " + mineGid);
		for (int i = 0; i < maxMineNumber; i++) {
			boolean isBoolean = true;
			while (isBoolean) {
				//
				// 무작위 셀 하나 추출
				//cells들 갯수중에 임의의수 발생???
				int rand = (int)(Math.random() * cells.size());
				MineCell2 cell = cells.get(rand);
				//Log.e("Game / scatterMines", i + "/" + maxMineNumber);
				
				//
				// 현재 무작위 추출 셀이 오픈된 셀이 지뢰가 아니고 셀 이면 지뢰로 설정한다.
				//if (cell.isMine() || cell.isOpened() || cell.isCollidable() || cell.isSphere()) {
				if (!cell.isMine() && !cell.isOpened() && !cell.isCollidable() && !cell.isSphere() && !this.isDontSetMine(cell.getTileCoord())) {
					//if(cell.isMine == NO && cell.isOpened == NO && cell.isCollidable == NO && cell.isSphere == NO && [self isDontSetMine:cell.tileCoord] == NO)
					
					cell.setMine(true);
					this.mineLayer.setTileGID(mineGid, cell.getTileCoord()); // 레이어가 비어있으면 에러남!
					//this.fg.setTileGID(mineGid, cell.tileCoord); // for test
					isBoolean = false;
				}
			}
		}
	}

	public void scatterSpheres() {
		for (int i = 0; i < GameData.share().kNumberOfSphere; i++) {
			//
			// 정해진 확률에 맞춰 수정구의 종류를 결정한다.
			// unsigned 없음
			final int fireChange = GameData.share().kFireChance;
			final int windChange = GameData.share().kWindChance;
			final int cloudChange = GameData.share().kCloudChance;
			final int mirrorChange = GameData.share().kMirrorChance;
			final int divineChange = GameData.share().kDivineChance;
			final int earthChange = GameData.share().kEarthChance;
			
			// unsigned 없음
			int location = 0;
			
			//NSRange ????
			int[] fireRange = {location, fireChange}; location += fireChange;
			int[] windRange = {location, windChange}; location += windChange;
			int[] cloudRange = {location, cloudChange}; location += cloudChange;
			int[] mirrorRange = {location, mirrorChange}; location += mirrorChange;
			int[] divineRange = {location, divineChange}; location += divineChange;
			
			int randomPick = (int) (Math.random() * 100);
			
			int sphereType;
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
			
			boolean isBoolean = true;
			while (isBoolean) {
				
				//
				// 무작위 셀 하나 추출
				int rand = (int) (Math.random() * cells.size());
				MineCell2 cell = cells.get(rand);
				
				//
				// ** (1) 현재 무작위 추출 셀의 수정구 자리 4개의 셀이
				//		- 1) 오픈된 셀이 아니고
				//		- 2) 지뢰가 아니고
				//		- 3) 유효한 셀
				//		- 4) 이미 수정구가 설치된 것이 아니면 수정구 아이템 영역으로 설정한다.
				
				//
				// ** (2) 기반셀이 가능한 셀이어야 한다.
				//isBoolean = false; // 임시용. isSphereBasePossible가 현재 무조건 false이다.
				if(cell.isSphereCellsClear() && cell.isSphereBasePossible()) {
					//
					// 수정구 설치된 기반셀 등록하여 오픈 체크에 사용
					sphereBaseCells.add(cell);
					cell.setSphereType(sphereType);
					cell.setToSphereCells(sphereType);
					this.addSphereTo(this.mineLayer, sphereType, cell);
					isBoolean = false;
					//Log.e("Game / scatterSpheres - isBoolean", "false");
				} else {
					//Log.e("Game / scatterSpheres - isBoolean", "true");	
				}
			}
		}
	}
	
	public int[] nsRange(int loc, int len) {
		 int[] range = {loc,len};
		return range;
	}
	
	public boolean nsLocationInRange(int loc, int[] range) {
		if (loc - range[0] < range[1])
			return true;
		return false;
	}

	public void addSphereTo(CCTMXLayer layer, int sphereType, MineCell2 baseCell) {
		
		// 0은 user가 아이템을 획득 0이 아니면 최초 맵에 수정구 설치
		if (GameData.share().isMultiGame) {
			try {
				if (sphereType == 0) {
					// NetworkController.getInstance().sendPlayDatasphereTake(baseCell.getUnSignedSphereCellId());
//					hud.testText.setString("" + baseCell.getUnSignedCellId());
//					Log.e("", "baseCell.getUnSignedCellId()" + baseCell.getUnSignedCellId());
					NetworkController.getInstance().sendPlayDatasphereTake(baseCell.getCell_ID() + (sphereType * 10000));
				} else {
//					hud.testText.setString("" + baseCell.getUnSignedCellId());
					Log.e("", "baseCell.getCell_ID()" + baseCell.getCell_ID());
					
					NetworkController.getInstance().sendPlayDataSphere(baseCell.getCell_ID() + (sphereType * 10000));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int counter = 0;
		for (MineCell2 cell : baseCell.getSphereCells()) {
			cell.setSphere(true);
		
			if (sphereType == 0)
				sphereType = 7;
			//Log.e("Game / addSphereTo", "sphereType check1 : " + counter + "," + sphereType);
			//sphereType = sphereType == 0 ? 7 : sphereType; // tilemap의 7번째 타일(0:7~3:7)에  빈 수정구가 있어서
			
			int gid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(counter, sphereType)));
			//layer.setTileGID(gid, cell.getTileCoord());
			layer.setTileGID(gid, cell.getTileCoord());
			// test code // 수정구 볼수 잇게 해줌
			this.fg.setTileGID(gid, cell.getTileCoord()); // 임시용 차후 문제시 정식으로 고칠것
			
			//
			// test code // 수정구가 숨겨진 타일에 표시한 라벨
//			CCLabel label = CCLabel.makeLabel(""+ cell.getUnSignedCellId(), "Arial", (30 * tileSize.width) / 128);
//			this.addChild(label, 1000);
//			label.setColor(ccColor3B.ccc3(240, 0, 240));
//			label.setPosition(cell.getTilePosition());
			counter++;
			/*
			// 수정구 오픈시 마지막칸 오른쪽에 tile 한칸더 추가
			if (counter == 4 && sphereType == 7) {
				gid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(counter, sphereType)));
				layer.setTileGID(gid, CGPoint.ccp(cell.getTileCoord().x+1, cell.getTileCoord().y));
				this.fg.setTileGID(gid, CGPoint.ccp(cell.getTileCoord().x+1, cell.getTileCoord().y)); // 임시용 차후 문제시 정식으로 고칠것
			}
			*/
		}
		
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
			if (a.x-errorRange < b.x && a.x+errorRange > b.x  && a.y-errorRange < b.y && a.y+errorRange > b.y )
				isBoolean = true;
		return isBoolean;
	}
	
	CGPoint currentTouchLocation  = null;
	CGPoint previousTouchLocation = null;
    
	/********************************************/
		
	/*************************/
	CGPoint currentLocation1 = null;
	CGPoint currentLocation2 = null;
	CGPoint previousLocation1 = null;
	CGPoint previousLocation2 = null;
	CGSize mapSize = null;
	CGSize tileSize = null;
	
//	CCLabel mapLabel;

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
		return (float) Math.sqrt(
				Math.pow(point1.x - point2.x, 2.0f) + 
				Math.pow(point1.y - point2.y, 2.0f));
	}

	//
	//position을 기기 화면 중심으로 지정해줌.
	public CGPoint getMapCenterPosition() {
		// -mapSize	 : x: -864.0, y: -1280.0 
		//   winSize	 : x:  160.0, y:   284.0 
		//   winSize	 : x:  640.0, y:   1052.0 <-- 1920 x1080 navigation
		
		CGPoint centerPosition = CGPoint.ccp(
				-mapSize.width/2 + winSize.width /2, 
				-mapSize.height/2 + winSize.height /2);
		Log.e("Game / getMapCenterPosition", "centerPositionScale:" + centerPosition);
		Log.e("Game / getMapCenterPosition", "getMapCenter:" + mapSize + "getMapCenter:" + winSize );
		centerPosition.ccpMult(centerPosition, this.getScale());
		Log.e("Game / getMapCenterPosition", "centerPositionScale:" + centerPosition);
		
		
		// example
		//return ccp(v.x*s, v.y*s); this.setScale() = 0.23f, centerPositionX = -704, centerPositionY = -996
		//this.getScale() = 0.23 ~ 1.0;
		//centerPosition.x = centerPosition.x * this.getScale();
		//centerPosition.y = centerPosition.y * this.getScale();
		

		return centerPosition;
	}
	
	public CGSize getMapDeltaSize() {
		// 변화되는 scale에 따라 map의 사이즈를 변경 (1:1, 3456:5120)
		CGSize scaleMapSize = CGSize.make(mapSize.width * this.getScale(), mapSize.height * this.getScale());
		// (-1408 -2034)
		CGSize deltaSize =  CGSize.make(winSize.width / 2 - scaleMapSize.width / 2 , winSize.height / 2 - scaleMapSize.height / 2);
		Log.e("Game / getMapDeltaSize", "MapDelta : " + deltaSize);
		
		return deltaSize;
	}
	
	public CGPoint getMapMinPosition() {
		return CGPoint.ccp(0,0);
	}
	
	public CGPoint getMapMaxPosition() {
		return CGPoint.ccp(
				-this.tileMap.getContentSize().width * getScale() + winSize.width,
				-this.tileMap.getContentSize().height * getScale() + winSize.height
				);
	}
	
	
	//
	private void layerMove(MotionEvent event) {
		
		if (currentLocation1 != null) {
//			if (currentLocation1.x > previousLocation1.x + 3 || currentLocation1.x < previousLocation1.x - 3 ||
	//			currentLocation1.y > previousLocation1.y + 3 || currentLocation1.y < previousLocation1.y - 3)
				previousLocation1 = currentLocation1;
			}
		if (previousLocation1 == null)
			previousLocation1 = getConvertToGL(event, 0);
		
		currentLocation1 = getConvertToGL(event, 0);
		//Log.e("Game / layerMove", "cuLoc1:" + (int)currentLocation1.x + "," + (int)currentLocation1.y);
		CGPoint deltaLocation = CGPoint.ccpSub(currentLocation1, previousLocation1);
		Log.e("Game / layerMove", "deltaLocation : " + deltaLocation);
		
		//if (deltaLocation.x >= 3 || deltaLocation.y >= 3) {
			theLayer.setPosition(CGPoint.ccpAdd(theLayer.getPosition(), deltaLocation));
			
			// scale 사용시 이상 현상 발생
			theLayer.setPosition(CGPoint.ccp(
					CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
					CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
			);
						
//			mapLabel.setString("mapLabel.getPosition() : " + mapLabel.getPosition());
//			mapLabel.setPosition(mapLabel.getPosition());
		/*	
			longPressTime = 0;
			isLongTap = false;
			isDoubleTap = false;
			
		} else if (longPressTime + 300 > System.currentTimeMillis()) {
				handleLongPress(event);
		} else {
			isDoubleTap = true;
		}
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
		
		Log.e("Game / layerScale", event.getPointerCount() + 
				", cuLoc1:" + (int)currentLocation1.x + "," + (int)currentLocation1.y + 
				", cuLoc2:" + (int)currentLocation2.x + "," + (int)currentLocation2.y);
	
		float currentDistance = getDistance(currentLocation1, currentLocation2);
		float previousDistance = getDistance(previousLocation1, previousLocation2);
		float deltaDistance = currentDistance - previousDistance;
		
		// Log.e("deltaDistance", "current:" +currentDistance+", previous:" +previousDistance+ ", Distance:"+deltaDistance);
		CGPoint pinchCenter = this.convertToNodeSpace(CGPoint.ccpMidpoint(currentLocation1, currentLocation2));
	
		//
		// 최소/최대 확대율 범위에서 레이어 확대 축소를 핀치 거리에 따라 한다.
		float newScale = theLayer.getScale() + (deltaDistance * GameConfig.share().kPinchZoomMultiplier);
		
		// clampf 그냥 범위 안에 들어가는지 확인후 리턴(128 pixel 기준)
		newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale * 128 / tileSize.width, GameConfig.share().kMaxScale * 128 / tileSize.width);
		
		
		Log.e("Game / layerScale", "scale:" + ((int)(theLayer.getScale()*100)/100f) +", new scale:"+ ((int)(newScale*100)/100f) +", delta:" + (int)deltaDistance);
		float positionScale = newScale - theLayer.getScale();
		theLayer.setScale(newScale); // 줌아웃을 하게해줌
		Log.e("Game / layerScale", "theLayer position" + theLayer.getPosition());
		Log.e("Game / layerScale", "theLayer getAnchorPoint" + theLayer.getAnchorPoint());
		theLayer.setPosition(CGPoint.ccpSub(theLayer.getPosition(), 
				CGPoint.make(pinchCenter.x * positionScale, pinchCenter.y * positionScale) )); // 선택한 화면에서 줌아웃을 함
		
		// 여기서 화면 밀림 발생
		theLayer.setPosition(CGPoint.ccp(
				CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
				CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
		);
		
//		mapLabel.setString("mapLabel.getPosition() : " + mapLabel.getPosition());
//		mapLabel.setPosition(mapLabel.getPosition());
	}

	// handle touches
		 
		//
		// 롱터치 : 깃발 꽂기
		public void handleLongPress(MotionEvent event) {
			
			//Log.e("Game / handleLongPress", "click : " + event.getAction());
				CGPoint location = CGPoint.ccp(event.getRawX(), event.getRawY());
				// 4사분면을 1사분면으로 변환
				location = CCDirector.sharedDirector().convertToGL(location);
				// GameLayer 좌측하단으로부터 터치 위치까지의 거리값(x,y)
				location = this.convertToNodeSpace(location);
				CGPoint location2 = this.convertToNodeSpace(location);
				// 타일의 어느 좌표인지 확인하여 값 불러오기
				CGPoint coord = this.tileCoordForPosition(location);
				//Log.e("Game / handleLongPress", "coord : " + coord); // 좌측상단 0,0 우측하단 26, 39
				
	
				ArrayList<MineCell2> cellArray = cells;
				int size = cellArray.size();
				for (int k = 0; k < size; k++) {
					
					//Log.e("Game / handleLongPress", "(" + k + ") : " + cellArray.get(k).isMarked() + "," + cellArray.get(k).isCollidable() + "," + cellArray.get(k).isMine() + "," + cellArray.get(k).isSphere() + ",");
					int numberOfMine = cellArray.get(k).getNumberOfMineAround();
					if (CGPoint.equalToPoint(cellArray.get(k).getTileCoord(), coord) && !cellArray.get(k).isCollidable()) {
						//Log.e("Game", "long press recognized if");
	
						// effect sound play
						SoundEngine.sharedEngine().playSound(this.mContext, R.raw.game_mushroom, false);
	
						// 꽂아져있는 버섯(깃발)을 취소할때 버섯(깃발)을 없애줌
						if (cellArray.get(k).isMarked()) {
							if (numberOfMine == -1) {
								// 지뢰가 있는 자리에 버섯(깃발)이 꽂혀있는걸 취소할때
								// 기존에 +1 연산해준값을 취소한다.(다시 -1)
								int aaa = GameData.share().previousMineNumber();
								//Log.e("previousMineNumber : ", " " + aaa);
							}
							cellArray.get(k).setMarked(false);
//							try {
//								hud.testText.setString("" + cellArray.get(k).getUnSignedCellId());
								Log.e("", "cellArray.get(k).getCell_ID()" + cellArray.get(k).getCell_ID());
//								NetworkController.getInstance().sendPlayDataMushroomOff(cellArray.get(k).getCell_ID());
								this.removeFlag(coord);
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
							
						} else {
							// 오픈안된 셀에 버섯(깃발)꽂기
							if (!cellArray.get(k).isOpened()) {
								// 지뢰가 있는 자리에 깃발을 꽂을 경우만 탐
								if (numberOfMine == -1) {
									//
								//	int mine = GameData.share().currentMineNumber();
									// 지뢰있는 곳에 호박 (깃발) 을 꽂는 수와 난이도 지뢰수가 같으면 게임오버
									// gamedata.share.getgamedifficulty는 난이도 설정하는
									// 창에서 설정되서 들어옴
									/**************************************/
									Log.e("Game / handleLongPress", "unopenedTile : " + unopenedTile + " / "+ GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty()));
									if (unopenedTile  == GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty())) {
										Log.e("Game / handleLongPress", "handleLongPress Game Over");
										this.gameOver();
										//Log.e("Game / handleLongPress", "mine:" + mine + ", difficulty : " + GameData.share().getGameDifficulty());
									}
									/**************************************/
								}
									cellArray.get(k).setMarked(true);
//									try {
//										hud.testText.setString("" + cellArray.get(k).getUnSignedCellId());
										Log.e("", "cellArray.get(k).getCell_ID()" + cellArray.get(k).getCell_ID());
//										NetworkController.getInstance().sendPlayDataMushroomOn(cellArray.get(k).getCell_ID());
										this.markFlag(coord);
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
								}
								//Log.e("Game / handleLongPress", "지뢰 없음");
							}
							this.hud.updateProgress();
							break;

					}
				}
				// end for
		}

	//
	// 더블터치 : 셀 오픈
	public void handleDoubleTap(MotionEvent event) {
		Log.e("Game / handleDoubleTap", "마인 갯수 : " + GameData.share().getMineNumber());
	
		if (!Config.getInstance().isDisableButton()) {
			//Log.e("Game / handleDoubleTap", "enableButton");
	
			CGPoint location = null;
			CGPoint coord = null;
			
			// GL 좌표
			location = CGPoint.ccp(event.getRawX(), event.getRawY());
			// cocos2d 좌표로 변환
			location = CCDirector.sharedDirector().convertToGL(location);
			// layer 좌측 하단으로 부터  좌표까지의 거리
			location = this.convertToNodeSpace(location);
			// 타일의 어느 좌표인지 확인하여 값 불러오기
			coord = this.tileCoordForPosition(location);
			
			for (final MineCell2 cell : cells) {
					int numberOfMine = cell.getNumberOfMushroomAndPumpkinAround();
					
					// 전체 타일을 검색하여 터치한 위치와 타일의 위치값이 일치할시
					if (CGPoint.equalToPoint(cell.getTileCoord(), coord) && !cell.isCollidable()) {
						// 오픈된 셀 누르면
						if (cell.isOpened()) {
							// 셀 저장된 주변 마인수를 얻어내 1이상이면
							if (numberOfMine > 0) {
								cell.roundOpen();
							}
							// 닫혀있는 셀 누르면
						} else {
							//지정된 셀을 열어준다.(tile의 fg를 제거)
							
							CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
								public void run() {
									android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
									try {
										Thread.sleep(300);
										cell.preOpens(false);			
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								
								}
							});

	
						}
						break;
						//k = size;
					}
				}

				
				//
				// 모두 열린 수정구가 있는지 확인한다.
				float startDelay = 0;
				for (MineCell2 cell : sphereBaseCells) {
		
					//
					// -1 : none
					// 0 : empty
					// > 0 : sphereType
					int sphereType = cell.getSphereItem();
					if (sphereType > 0) {
						
						// 새로 열려진 셀들에 수정구가 열렸다.
						// 빈 수정구로 지정하고,
						cell.setSphereType(kSphereTypeEmpty);
		
						//Log.e("Game / handleDoubleTap", "check 1 / 아이템 타일 변경");
						// 빈 수정구 타일로 바꾼다.
						this.addSphereTo(this.mineLayer, kSphereTypeEmpty, cell);
						
						// 수정구 획득수를 타입별로 하나 증가시킨다.
						GameData.share().increaseItemByType(sphereType);
						
						// UI 업데이트를 한다.
						// 수정구를 클릭해서 활성화시키면 +1 씩 라벨에 넣어준다.
						this.hud.updateSphereItemNumber();
						
						// 버튼에 클릭효과를 넣는다.
						this.hud.clickEffect(sphereType, startDelay);
						startDelay += 0.2f;
					}
				}
				// end 모두 열린 수정구가 있는지 확인한다. 
				Log.e("Game / handleDoubleTap", 
						"unopenedTile : " + unopenedTile +  
						", Max Mine : " + GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty()));
				
				if (unopenedTile == GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty())) {
					Log.e("Game / handleDoubleTap", "handleDoubleTap Game Over");
					this.gameOver();
				}
				
			}
			// end	Config.getInstance().isDisableButton()
		}
	

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		// device 좌표를 읽어온다. (openGL x)
		currentTouchLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
//		previousTouchLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		
		isLongTap = true;
		
//		// effect sound load
//		SoundEngine.sharedEngine().preloadSound(this.mContext, R.raw.ding); // 무슨 사운드이지??
		
		// 현재 찍힌 좌표 계산
		CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		double tempTime = System.currentTimeMillis();
		
		boolean abc = locationMatchingTest(touchLocation, convertedLocation, 20); // default : 20
		
		// 더블탭 (지연시간 0.3초)
		if (abc && doubleTap + 500 > tempTime && event.getPointerCount()  == 1) {
		//if (abc && doubleTap + 500 > tempTime) {
			handleDoubleTap(event);
			isLongTap = false;
			touchLocation = convertedLocation; // 좌표가 없을시 대입
			doubleTap = 0;
			//handleDoubleTap(event);
			
		// 싱글탭	
		} else
			{ // 같은 위치가 선택 되었을시
			touchLocation = convertedLocation; // 좌표가 없을시 대입
			doubleTap = tempTime;
		}
		return super.ccTouchesBegan(event);
	}

	// 화면이동 과 줌인 아웃시
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		 if (!Config.getInstance().isDisableButton()) {

		//
		// 조절 할 레이어 지정
		CCLayer theLayer = this;
		boolean isMove = true;

		//
		// 일반

		/*************************************/
		// 현재 찍힌 좌표 계산
		// convert event location to CCPoint
		CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		double tempTime = System.currentTimeMillis();
		boolean abc = locationMatchingTest(touchLocation, convertedLocation, 10); // default : 10
		
		//롱클릭 (문제점 저해상도에서는 터치 무브가 일어나지 않는다.)
		if (event.getPointerCount() == 1 && isLongTap) {
			if (abc) {
				if (doubleTap + 300 < tempTime) {
					handleLongPress(event);
					convertedLocation = this.convertToNodeSpace(convertedLocation);
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
		    	previousLocation1  =  CCDirector.sharedDirector().convertToGL(
		    			CGPoint.ccp(event.getX(pointerIndex1), event.getY(pointerIndex1)));
			 else
				previousLocation1  = currentLocation1;
				
		    if (previousLocation2 == null) 
		    	previousLocation2  = CCDirector.sharedDirector().convertToGL(
		    			CGPoint.ccp(event.getX(pointerIndex2), event.getY(pointerIndex2)));
		     else
		    	previousLocation2  = currentLocation2;
			
			currentLocation1  = CCDirector.sharedDirector().convertToGL(
					CGPoint.ccp(event.getX(pointerIndex1), event.getY(pointerIndex1)));
			currentLocation2  = CCDirector.sharedDirector().convertToGL(
					CGPoint.ccp(event.getX(pointerIndex2), event.getY(pointerIndex2)));
			
			
			// sqrt = 제곱근 , pow = 몇 승
			float currentDistance = (float) Math.sqrt(
					Math.pow(currentLocation1.x - currentLocation2.x, 2.0f) + 
					Math.pow(currentLocation1.y - currentLocation2.y, 2.0f));
			
			float previousDistance = (float) Math.sqrt(
					Math.pow(previousLocation1.x - previousLocation2.x, 2.0f) + 
					Math.pow(previousLocation1.y - previousLocation2.y, 2.0f));
			
			float deltaDistance = currentDistance - previousDistance;
			CGPoint pinchCenter = CGPoint.ccpMidpoint(currentLocation1, currentLocation2);
			pinchCenter = this.convertToNodeSpace(pinchCenter);
			
			//
			// 최소/최대 확대율 범위에서 레이어 확대 축소를 핀치 거리에 따라 한다.
			float newScale = theLayer.getScale() + (deltaDistance * GameConfig.share().kPinchZoomMultiplier);
			// clampf 그냥 범위 안에 들어가는지 확인후 리턴 (128pixel 기준)
			newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale * 128 / tileSize.width, GameConfig.share().kMaxScale * 128 / tileSize.width);
			float positionScale = newScale - theLayer.getScale();
			theLayer.setScale(newScale); // 줌아웃을 하게해줌
			Log.e("Game / game",
					"cLocation1 : (" + (int)currentLocation1.x + ", " +  (int)currentLocation1.y +"), pLocation1 : (" + (int)previousLocation1.x + ", " +  (int)previousLocation1.y);		
			Log.e("Game / game",
					"cLocation2 : (" + (int)currentLocation2.x + ", " +  (int)currentLocation2.y +"), pLocation2 : (" + (int)previousLocation2.x + ", " +  (int)previousLocation2.y);		
			Log.e("Game / game", "MapCurrentPosition : " + this.getMapCurrentPosition());		
//			theLayer.setPosition(this.getMapCurrentPosition()); // 선택한 화면에서 줌아웃을 함
			theLayer.setPosition(CGPoint.ccpSub(theLayer.getPosition(), 
					CGPoint.make(pinchCenter.x * positionScale, pinchCenter.y * positionScale) )); // 선택한 화면에서 줌아웃을 함
			
			
			// scale 사용시 이상 현상 발생
			theLayer.setPosition(CGPoint.ccp(
					CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
					CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
			);
			// 외곽 영역 설정(현재 위치값이 틀려 버그임.)
			// 줌아웃할때 화면안에서만 줌 인 되게 해줌

//				mapLabel.setPosition(CGPoint.ccp(this.getPosition().x / 2 * Xscale, this.getPosition().y / 2 * Yscale));
//				mapLabel.setString("mapPosition :"+ (int)this.getPosition().x + ", " +(int)this.getPosition().y);
			
			//Log.e("Game / game", "this.getAnchorPoint : " + this.getAnchorPoint() + " / this.getPosition : " + (int)this.getPosition().x + ", " +(int)this.getPosition().y );			

			// debug label
			//this.hud.updateDebugLabel(theLayer.getScale(), theLayer.getPosition());
			isMove = false;
		} 
		/*************************************/
		if (isMove) {
			//Log.e("Game / ccTouchesMoved", "touch : 1");
			
		    //int pointerIndex = event.findPointerIndex(event.getPointerId(0));

		    // 뭐지??
				if (previousTouchLocation == null) {
					currentTouchLocation = CCDirector.sharedDirector().convertToGL(
							CGPoint.ccp(event.getRawX(), event.getRawY()));
					previousTouchLocation = currentTouchLocation;
				} else if (!isMove2) {
					currentTouchLocation = CCDirector.sharedDirector().convertToGL(
							CGPoint.ccp(event.getRawX(), event.getRawY()));
				} else //if (currentTouchLocation != null) 
					{
					previousTouchLocation = currentTouchLocation;
					currentTouchLocation = CCDirector.sharedDirector().convertToGL(
							CGPoint.ccp(event.getRawX(), event.getRawY()));
				} 

			CGPoint deltaLocation = null;
			if (currentTouchLocation == null || previousTouchLocation == null) {
				deltaLocation = CGPoint.ccp(0f,0f);				
			} else {
				deltaLocation = CGPoint.ccpSub(currentTouchLocation, previousTouchLocation);
			}
			Log.e("Game / Moved", "move2 : " +  isMove2+ ", deltaLocation : " + deltaLocation );
			Log.e("Game / Moved", "previousTouchLocation : " +  previousTouchLocation);
			int area = 5;
			//adasdas
			if (deltaLocation.x < -1 * area || deltaLocation.x > area || deltaLocation.y < -1*area || deltaLocation.y > area || isMove2) {
//			if (deltaLocation.x < -120 || deltaLocation.x > 120 || deltaLocation.y < -120 || deltaLocation.y > 120) {
			isMove2= true;	
			theLayer.setPosition(CGPoint.ccpAdd(theLayer.getPosition(), deltaLocation));
			// scale 사용시 이상 현상 발생
			theLayer.setPosition(CGPoint.ccp(
					CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
					CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
			);
			//Log.e("Game / Moved", "1touch theLayer1 : " + theLayer.getPosition());
			// ccpAdd : return ccp(v1.x + v2.x,v1.y + v2.y);
			// 백그라운드 이미지 밖으로 deviceView 영역이 못 벗어나게 함
			// (없으면 화면이 끝나는곳에서 멈추는게 아니라 끝까지 움직임)
			
			// 외곽 영역 설정(현재 위치값이 틀려 버그임.)
			
//			mapLabel.setPosition(CGPoint.ccp(this.getPosition().x/2 * Xscale,this.getPosition().y/2 * Yscale));
//			mapLabel.setString("mapPosition :"+ (int)this.getPosition().x + ", " +(int)this.getPosition().y);
		
			//Log.e("Game / game", "this.getAnchorPoint : " + this.getAnchorPoint() + " / this.getPosition : " + (int)this.getPosition().x + ", " +(int)this.getPosition().y );			
			
			// scale
			currentScale = this.getScale(); // 움직인 현재 스케일을 저장
			currentLayerX = theLayer.getPosition().x; // 현재 화면의 x 값을 저장 
			currentLayerY = theLayer.getPosition().y; // 현재 화면의 y 값을 저장
					 
			// 이동방향에 맞추어 마법사 스프라이트 텍스처를 바꾸어 준다.
			int direction = this.getTouchMoveDirection(deltaLocation);
			this.hud.setMagicianTo(direction);
		}
			}
			/*************************************/	
		}
		return CCTouchDispatcher.kEventHandled;
	}
	
	boolean isMove2 = false;
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		//Log.e("Game Touch", "Ended");
		currentTouchLocation  = null;
		previousTouchLocation = null;
		currentLocation1  = null;
		previousLocation1  = null;
		currentLocation2  = null;
		previousLocation2 = null;
		this.hud.setMagicianTo(kDirectionDown);
		isMove2= false;	
		return CCTouchDispatcher.kEventHandled;
	}

	// handle touches
		 
	/*****************************/
	private CGPoint displayLimit() {
		//화면 중간 좌표
		float wid = 	winSize.width/2;
		float hei = winSize.height/2;  
		// layer부터 화면 중간까지의 좌표(GL --> cocos2D --> nodeSpace)
		return this.convertToNodeSpace(CCDirector.sharedDirector().convertToGL(CGPoint.make(wid, hei)));
	}
	
	// current layer position + (변한 좌표 - 기존 좌표) * (before scale - (after 좌표 / before 좌표))
	private CGPoint setLayerPosition(CCLayer currentLayer, CGPoint displayBeforePosition, CGPoint displayAfterPosition, float beforeScale, float afterScale) {
		float x = currentLayer.getPosition().x + (displayAfterPosition.x - displayBeforePosition.x) * (beforeScale - (displayAfterPosition.x / displayBeforePosition.x));
		float y = currentLayer.getPosition().y + (displayAfterPosition.y - displayBeforePosition.y) * (beforeScale - (displayAfterPosition.y / displayBeforePosition.y));
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

		int flagGid = this.itemLayer.tileGIDAt(CGPoint.ccp(1, 0)); // 빨간깃발(방장)
		flagGid = CCFormatter.swapIntToLittleEndian(flagGid);
		this.flagLayer.setTileGID(flagGid, tileCoord);
		
		//
		// 지뢰수 하나 감소시킬시 디스플레이 업데이트 시킨다.
		this.hud.updateMineNumber(GameData.share().decreaseMineNumber());
	}

	//
	// effects
	public void rotateAllMineNumberLabel() {
		//
		// 현재 보이는 번호들을 2바퀴 2초간 돌리고 멈춘다.
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			//rotateBy rot = ~~~~~
			//지속시간, 회전각도
			CCRotateBy rot = CCRotateBy.action(2f, 360f);
			CCLabel label = (CCLabel) this.getChildByTag(cellsTemp.get(k).getCell_ID()); // 될랑가 몰라??
			label.runAction(CCSequence.actions(rot, rot, rot));
		}
	}

	public void stopRotationOfMineNumberLabel() {
		//Log.e("stop", "rot");
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			CCRotateBy rot = CCRotateBy.action(5f, 360f);
			CCLabel label = (CCLabel) this.getChildByTag(cellsTemp.get(k).getCell_ID());
			label.runAction(rot);
		}
	}

	public void showAllMineNumberLabel() {
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			CCLabel label = (CCLabel) this.getChildByTag(cellsTemp.get(k).getCell_ID());
			label.setVisible(true);
		}
	}

	public void hideAllMineNumberLabel() {
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			CCLabel label = (CCLabel) this.getChildByTag(cellsTemp.get(k).getCell_ID());
			label.setVisible(false);
		}
	}
	
//	public boolean updateHeart() {
//		return this.hud.updateHeart();
//	}

	// 주변 지뢰 수
	public void displayMineNumber(int numberOfMine, CGPoint position, int tag) {
			if (this.isCollidable(this.tileCoordForPosition(position))) {
				return;
			}
				// 글자 크기 기존의 2/3로 감소(사유 : 수정구 바닦에 숫자가 겹쳐야 되서 수정구에 의해 숫자가 가려짐. - 박정렬 팀장님)
				CCLabel label = CCLabel.makeLabel("" + numberOfMine + " ", "Arial-Bold", (int)((70 * (2 / 3.0) * tileSize.width) / 128));
				this.addChild(label, 100000, tag);
				label.setAnchorPoint(0.5f, 0.5f);
				label.setPosition(position);
				label.setColor(ccColor3B.ccc3((int)(75/255f), (int)(51/255f), (int)(9/255f)));
		}

	//
	// MineCell2 Delegate
//	@Override
	public void removeTile(CGPoint tileCoord) {
		// Global ID // Globally unique IDentifier
		int tileGid = this.meta.tileGIDAt(tileCoord); 
		tileGid = CCFormatter.swapIntToLittleEndian(tileGid); // 뭔지 아직 모르겠음.
		// 0 : 일시 타일값 없음
		
		if(tileGid > 0){
			HashMap<String , String> properties = this.tileMap.propertiesForGID(tileGid);
	
			//Log.e("Game / removeTile", "properties:" + properties);
			if (properties != null && properties.size() != 0) {
				String collisionValue = properties.get("Collidable");
				
				if (collisionValue != null && collisionValue.equals("true")  ) {
					SoundEngine.sharedEngine().playEffect(this.mContext, R.raw.game_pumpkin); // hit
				} else {
					SoundEngine.sharedEngine().playEffect(this.mContext, R.raw.game_open2); // pickup
					//Log.e("Game / removeTile", "getFg : " + getFg());
					//Log.e("Game / removeTile", "tileCoord : " + tileCoord);
					// 자주 문제 발생 stack over flow Error (thread 오버)
					this.getFg().removeTileAt(tileCoord);
				}
	
			} else {
				// 폭탄은 없는데 타일이 안없어지는 에러
				//Log.e("Game / removeTile", "properties - boom empty");
			}
		} else {
			//Log.e("Game / removeTile", "properties - tile empty");
			SoundEngine.sharedEngine().playEffect(this.mContext,R.raw.game_open2); // pickup
			// 에러 stack over flow error
			this.getFg().removeTileAt(tileCoord);
		}
	}
	
//	@Override
	public void gameOver() {
//		this.hud.gameOver();
		this.hud.gameOver(globalIndex, globalIndex);
	}

	
	// HudLayer inner class end

	public CGPoint tileCoordForPosition(CGPoint position) {
		CGSize mapSize = this.mapSize;
		//640 / 27
		// x /27
		int x = (int)(position.x / tileSize.width);
		int y = (int)((mapSize.height - position.y) / tileSize.height); // for flip from bottom to top
		return CGPoint.ccp(x, y);
	}

		// metaLayer (작업 불가능 영역)
		public boolean isMetaChecked(CGPoint tileCoord, String metaString) {
			// tile이 있는 곳은 0이 아닌 값을 가진다.
			int gid = CCFormatter.swapIntToLittleEndian(this.meta.tileGIDAt(tileCoord));
			// tile이 깔린곳
			if (gid > 0) {
				// tileMap의 GID를 key로 넣어 HashMap으로 변환하여 Properties로 저장한다.
				HashMap<String, String> Properties = this.tileMap.propertiesForGID(gid);
				// <--- 문제 지점은 아마 키값이 안들어 있어서 인것같다 확인 필요.
				if (Properties !=null && Properties.size() != 0) {
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
		this.flagLayer.removeTileAt(tileCoord);
		
		//
		// 지뢰수 하나 증가시키고 디스플레이 업데이트 시킨다.
		this.hud.updateMineNumber(GameData.share().increaseMineNumber());
	}

	public CCTMXLayer getFg() {
		return fg;
	}

	public void setFg(CCTMXLayer fg) {
		this.fg = fg;
	}

	// 뭔가 다름...
	public CCSprite animationMagma() {
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(folder + "effectMagma-hd.plist");
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 9; i++) {
			String frameName = "magma" + i + ".png";
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(frameName));
		}
		// 음...  name String이 다르다
		CCAnimation animation = CCAnimation.animation("", 0.1f, frames);
		CCAnimate animate = CCAnimate.action(animation);
		CCSprite sprite = CCSprite.sprite("magma1.png");
		CCSequence sequence = CCSequence.actions(animate);
		CCRepeatForever repeat = CCRepeatForever.action(sequence);
		sprite.runAction(repeat);
		return sprite;
	}

	public void effectMagmaOn() {
		this.bg.setVisible(false);
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			CCSprite sprite = this.animationMagma();
			sprite.setTag(cellsTemp.get(k).getCell_ID() * 10);
			this.addChild(sprite, -5);
			sprite.setPosition(cellsTemp.get(k).getTilePosition());
		}
	}

	public void effectMagmaOff() {
		this.bg.setVisible(true);
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			this.removeChildByTag(cellsTemp.get(k).getCell_ID() * 10, true);
		}
	}
	
	//
	final int kDirectionDown 			= 0;			/***  ↓   ***/ 
	final int kDirectionLeftDown 	= 45;		/*** ↙  ***/
	final int kDirectionLeft 				= 90;		/*** ←  ***/
	final int kDirectionLeftUp 			= 135;		/*** ↖  ***/
	final int kDirectionUp 				= 180;		/*** ↑    ***/
	final int kDirectionRightUp 		= 225;		/*** ↗  ***/
	final int kDirectionRight 			= 270;		/*** →  ***/
	final int kDirectionRightDown = 315;		/*** ↘  ***/
	
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
		
			//Log.e("Direction", "Vector:" +dv + ", gx:" +((int)(gx*10))/10f + ", gy:" +((int)(gy*10))/10f);

		if (isDiagonal) {
			if (dv.x > 0 && dv.y > 0) {
				//Log.e("Direction", "↗");
				return kDirectionRightUp;
			} else if (dv.x > 0 && dv.y < 0) {
				//Log.e("Direction", "↘");
				return kDirectionRightDown;
			} else if (dv.x < 0 && dv.y > 0) {
				//Log.e("Direction", "↖");
				return kDirectionLeftUp;
			} else if (dv.x < 0 && dv.y < 0) {
				//Log.e("Direction", "↙");
				return kDirectionLeftDown;
			}
		}

		if (isVertical) {
			if (dv.y > 0) {
				//Log.e("Direction", "↑");
				return kDirectionUp;
			} else {
				//Log.e("Direction", "↓");
				return kDirectionDown;
			}
		}

		if (isHorizontal) {
			if (dv.x > 0) {
				//Log.e("Direction", "→");
				return kDirectionRight;
			} else {
				//Log.e("Direction", "←");
				return kDirectionLeft;
			}
		}
		//Log.e("Direction", "return 0");
		return 0;
	}
	/**************************************/

	public static class HudLayer extends CCLayer {

		String hudLayerFolder =  "61hud/";
		int maxMineNumber;
		
		CGSize winSize;
		
		CCLabel label;
		public static CCLabel testText;
		//CCLabel debugLabel;
		//CCLabel debugLabelLine2;
		static CCLabel statusMine;

		CCSprite magician;
		
		CCMenu itemMenu;
		CCSequence magicianAction;
		
		Context mContext;
		
		float tilePixelSize = CCTMXTiledMap.tiledMap(GameData.share().gameMap).getTileSize().width;
		static GameProgressBar gameProgressBar;

		// (생명) 태그 넘버
		static final int kTagHeart = 9999; // final x
		static final int kButtonOff = 0; // final x
		static final int kButtonOn = 1; // final x

		GameEnding gameEnding;
		HudLayer controlHudLayer;
		
		public HudLayer() {
			this.winSize = CCDirector.sharedDirector().winSize();
			this.mContext = CCDirector.sharedDirector().getActivity().getApplicationContext();

			// 터치가 안될시 아래 명령을 비활성화 할 것
			//CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
			//this.setIsTouchEnabled(true);
			//this.isTouchEnabled_ = true; // cclayer 멤버
			
			int margin = 5;
			label = CCLabel.makeLabel("0", "verdana-Bold", (18 * tilePixelSize) / 128);
			label.setColor(ccColor3B.ccBLACK);
			label.setPosition(
					winSize.width - (label.getContentSize().width / 2) - margin, 
					(label.getContentSize().height / 2) - margin);
			this.addChild(label);
	
			int testTextSize = 72;
			testText = CCLabel.makeLabel("시간이 초기화 되면 시작", "Arial", (testTextSize * tilePixelSize) / 128);
			testText.setColor(ccColor3B.ccWHITE);
			testText.setPosition(winSize.width/2, winSize.height / 2);
					this.addChild(testText);
					//
					// 상단 중앙 게임진행시간 및 진행 막대기
					gameProgressBar = new GameProgressBar(this.mContext);
					this.addChild(gameProgressBar);
					//gameProgressBar.startTime(gameEnding);
					//Log.e("HudLayer / ", "minimap setting complete");adsa
					//gameProgressBar.startTime(controlHudLayer);
					gameProgressBar.startTime();

					
					//
					// 좌상단 찾은 지뢰 갯수 및 생명 표시
					CCSprite statusBase = CCSprite.sprite(hudLayerFolder + "game-statusBase-hd.png");
					statusBase.setPosition(
							statusBase.getContentSize().width / 2 + margin, 
							winSize.height - statusBase.getContentSize().height / 2 - margin);
					this.addChild(statusBase);
					
					//  현재 맵에서 찾아야되는 지뢰(호박)의 갯수 글자
					statusMine = CCLabel.makeLabel(
//							GameData.share().getMineNumber() + " ", "AvenirNextCondensed-Bold", 11);
							GameData.share().getMineNumber() + " ", "Arial-Bold", 30);
					statusMine.setPosition(
							statusBase.getContentSize().width / 2,
							statusBase.getContentSize().height / 4);
					statusBase.addChild(statusMine);
					this.updateHeart(); // 생명 표시 초기화
		
					//
					// 우상단 미니맵 버튼
					CCMenuItem item = CCMenuItemImage.item(
							hudLayerFolder + "game-buttonMinimap-normal-hd.png",
							hudLayerFolder + "game-buttonMinimap-select-hd.png", 
							this, "clicked");
					
					item.setTag(kButtonMinimap);
					CCMenu minimap = CCMenu.menu(item);
					minimap.setPosition(
							winSize.width - item.getContentSize().width / 2,
							winSize.height - item.getContentSize().height / 2);
					minimap.setPosition(CGPoint.ccpSub(minimap.getPosition(), CGPoint.ccp(4f, 4f)));
					//Log.e("HudLayer", "minimap setting complete");
					this.addChild(minimap);
					//Log.e("HudLayer", "minimap loaded");
		
					//
					// 하단 수정구 아이템
					CCSprite itemBase = CCSprite.sprite(hudLayerFolder + "game-itemBase-hd.png");
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
						String fOn = hudLayerFolder + "game-item" + fileNames[i] + "On-hd.png";
						String fOff = hudLayerFolder + "game-item" + fileNames[i] + "Off-hd.png";
						//Log.e("fOn", fOn);
						//Log.e("fOff", fOff);
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
					
					// 추가하면 터치가 안됨.  확인할 것
					
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
					CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(hudLayerFolder + "magician.plist");
					magician = CCSprite.sprite(hudLayerFolder + "R-WizardAngle-0.png");
					this.addChild(magician, -1);
					magician.setPosition(winSize.width * 0.57f, winSize.height * 0.3f);
					
					
//					this.addChild(point1);
//					point1.setPosition(winSize.width/2,winSize.height/2 + 100);
//					this.addChild(point2);
//					point2.setPosition(winSize.width/2,winSize.height/2 + 50);
//					this.addChild(point3);
//					point3.setPosition(winSize.width/2,winSize.height/2 - 50);
//					this.addChild(point4);
//					point4.setPosition(winSize.width/2,winSize.height/2 - 100);
					
					this.addChild(GameMinimap.getInstance(), GameConfig.share().kDepthPopup);
					GameMinimap.getInstance().setVisible(false);
					
				}
		// HudLayer() end

		//
		public boolean updateHeart() {
			//Log.e("HudLayer", "HudLayer --------- updateHeart --------- HudLayer");
			// value는 3
			int value = GameData.share().getHeartNumber();

			for (int i = 0; i < GameData.share().kMaxHeartNumber * 2; i++) {
				this.removeChildByTag(kTagHeart, true);
			}

			// 하트 위치
			CGPoint position = CGPoint.make(
					winSize.width / 20, winSize.height - winSize.width / 4.3f);
			int z = 0;

			// add flag
			for (int i = 0; i < GameData.share().kMaxHeartNumber; i++) {
				z = (i < value) ? 10 : 0;

				CCSprite heartOn = CCSprite.sprite(hudLayerFolder + "game-heartOn-hd.png");
				CCSprite heartOff = CCSprite.sprite(hudLayerFolder + "game-heartOff-hd.png");

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
				position = CGPoint.make(
						position.x + heartOff.getContentSize().width * 1.1f, position.y);
			}
			
			return true;
		}
		// updateHeart() end

		public void setMagicianTo(int direction) {
			//Log.e("direction", " " + direction);
			String frameName = hudLayerFolder + "R-WizardAngle-" + direction + ".png";
			//Log.e("frameName", frameName);
			//CCSpriteFrame frame = CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(frameName);
			
			// 저사양 기기에서는 버벅거림.
			CCTexture2D t2d = CCTextureCache.sharedTextureCache().addImage(frameName); 
			CCSpriteFrame frame = CCSpriteFrame.frame(t2d,CGRect.make(0,0,301,277),CGPoint.ccp(0,0));
			
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
			CCSprite overlay = CCSprite.sprite(hudLayerFolder + "game-itemOver-hd.png");

			for (CCNode itemNode : itemMenu.getChildren()) {
				CCMenuItemToggle item = (CCMenuItemToggle)itemNode;
				if (item.getTag() == sphereType && item.selectedIndex() == kButtonOn) {
					item.addChild(overlay);
					overlay.setPosition(item.getContentSize().width / 2, item.getContentSize().height / 2);
					// flashOut 메소드가 안되는 부분이 있어 막아뒀음.(remove node) 대신 다른것 넣어서 테스트중...
					Utility.getInstance().flashOut(overlay, startDelay, 0.5f);
					break;
				}
			}
			//Log.e("Game / clickEffect", "end"); 
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*****************************************************/
		/** 문제지점
		 *  
		 * 애니매이션
		 * 아이템 발동 애니
		 * 
		 * 아이템 피해 애니는 GameMinimap.java 의 receivePlayData method 확인
		 * 
		 * 
		 * @return
		 */
		public void clicked(final Object button) {
		//public void clicked(CCMenuItem button) {
			//Log.e("clicked", "clicked");
			if (!Config.getInstance().isDisableButton()) {
				//Log.e("clicked", "Button Enable");
				String effectName = "";
				switch (((CCMenuItem) button).getTag()) {
				case kButtonMinimap:
					//Log.e("button pressed", "kButtonMinimap");
					String a = GameConfig.share().isMinimapPanelOn() ? "true" : "false";
					Config.getInstance().setDisableButton(true);
					//this.addChild(GameMinimap.getInstance().tileon(), GameConfig.share().kDepthPopup);
					GameMinimap.getInstance().setVisible(true);
					//Log.e("minimap flag is", a);
					break;
				case kButtonFire:
					//Log.e("button pressed", "kButtonFire");
					effectName = "불마법";
					break;
				case kButtonWind:
					//Log.e("button pressed", "kButtonWind");
					effectName = "바람마법";
					break;
				case kButtonCloud:
					//Log.e("button pressed", "kButtonCloud");
					effectName = "구름마법";
					break;
				case kButtonDivine:
					//Log.e("button pressed", "kButtonDivine");
					effectName = "신성마법";
					break;
				case kButtonEarth:
					//Log.e("button pressed", "kButtonEarth");
					effectName = "대지마법";
					break;
				case kButtonMirror:
					//Log.e("button pressed", "kButtonMirror");
					effectName = "반사마법";
					break;

				default:
					//Log.e("button pressed", "default");
					effectName = "마법지정 오류";
					break;
				}
				final String alertText  = effectName;
				//
				// 수정구아이템 버튼 클릭 공통
				if (((CCMenuItem) button).getTag() >= 1 && ((CCMenuItem) button).getTag() <= 6) {
					//
					// 마법사 액션
					// - 애니메이션을 위에 올렸닥 끝나면 지워버린다.
					// - 본래 캐릭터는 가렸다가 애니메이션이 끝난 후에 (0.4초 이후) 다시 보이도록 한다.
					Utility.getInstance().animationMagicianAction(this);
					magician.setVisible(false);
					final CCNode layer = this;
					CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
						public void run() {
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							try {
								Thread.sleep(400);
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
/*
								// 발동 이펙트 (기본 확인 취소 경고창)
								AlertDialog.Builder builder = 
										new AlertDialog.Builder(CCDirector.sharedDirector().getActivity());
								builder
								//.setMessage(alertText + " 발동")
								.setTitle(alertText + " 발동")
										.setPositiveButton(
												"확인",new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,
															int id) {
														// your Code here
														dialog.cancel(); 
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
								*/
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						}
					});

	/**************/
				}
			}
		}
		/*****************************************************/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		public void showMagician() {
			magician.setVisible(true);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*****************************************************/
		/** 문제지점
		 *  
		 * 애니매이션
		 * 게임 종료 애니 GameEnding.java
		 * 
		 * @return
		 */
		static boolean isGameOver = false; // 게임 종료 메시지를 서버로 무한 보내는것을 방지함.
		
//		public static void gameOver() {
		public static void gameOver(int myPoint, int otherPoint) {
			Log.e("Game / HudLayer / gameOver", "gameEnding - gogo");
			Config.getInstance().setDisableButton(true);
			
			if (GameData.share().isMultiGame) {
				 // 게임 종료 메시지를 서버로 무한 보내는것을 방지함.
				if (!isGameOver) {
					try {
						isGameOver = !isGameOver;
						NetworkController.getInstance().sendRequestGameOver(123456);					
					} catch (IOException e) {
						e.printStackTrace();
					}			
				}
			}

			Game.scene.addChild(GameEnding.layer());
//			Game.ending.setVisible(true);
//			GameData.share().isMultiGame = true;
//			GameEnding ending = GameEnding.share(this.mContext);
//			this.addChild(ending, GameConfig.share().kDepthPopup);
		}
		/*****************************************************/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//others
		
		// 위쪽 캐릭터 얼굴만있는 프로그레스바에 캐릭 얼굴 움직이게 해줌
		public static void updateProgress() {
			int gameDifficulty  = GameData.share().getGameDifficulty();
			int maxMine = GameData.share().getMaxMineNumber(gameDifficulty);
			int remainedMine = GameData.share().getMineNumber();
			float progress = (float)remainedMine / (float)maxMine;
			progress = 1- progress;
			gameProgressBar.progress(progress, gameProgressBar.kTagIndicatorMe);
		}
		
		// 지뢰갯수 숫자로 표시하는 메서드
		public static void updateMineNumber(int remainedMineNumber) {
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
				int itemNumber = GameData.share().getItemNumberByType(sphereType);
				String string = itemNumber > 0 ? "+" + itemNumber : "  " ;
				//CCLabel l = CCLabel.makeLabel(string, "Arial", 12);
				// 아이템별 수량을 표시하는 글자
				CCLabel l = CCLabel.makeLabel(string, "Arial-Bold", 24);
				l.setTag(999);
				this.addChild(l);
				l.setPosition(i * 50f*2 + 50f*2, l.getContentSize().height * 1.2f);

				//
				// 버튼 상태 설정
				this.setSphereItemState(sphereType, itemNumber > 0 ?
						kButtonOn : kButtonOff);
			}
			//Log.e("Game / updateSphereItemNumber", "end");
		}

		// 원래 코드에서 자동이 하도 많아서 
		//지금 작성한게 돌아가도 잘 돌아가는건지 모르겠다.
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

		public static void abc(int cell_ID){
			testText.setString("message type : " + cell_ID);		
		}
		
		//public static void abc(int messageType){
		public static void abc(byte[] messageType){
//			final int aaa = messageType;
			 byte[] aaa = messageType;
			Log.e("Game / abc", "in");
			String aaaa = "";
			for (byte b : aaa) {
				aaaa = aaaa + b + ",";
			}
			final String result = aaaa;
			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
			public void run() {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				if (result != null) {
					testText.setString("message type : " + result);					
				}
				
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

//		CCLabel point1 = CCLabel.makeLabel("point1 : ", "Arial", (30* tilePixelSize) / 128);
//		CCLabel point2 = CCLabel.makeLabel("point2 : ", "Arial", (30* tilePixelSize) / 128);
//		CCLabel point3 = CCLabel.makeLabel("point3 : ", "Arial", (30* tilePixelSize) / 128);
//		CCLabel point4 = CCLabel.makeLabel("point4 : ", "Arial", (30* tilePixelSize) / 128);
	}
	/**************************** 변경 ****************************/
	int globalIndex;
	int myPoint;
	int otherPoint;
	
	
	@Override
	public void removeTile(CGPoint tileCoord, boolean isAi, boolean isMine) {
		if (!isAi) {
			if (isMine) {
				Effect.share().pumpkinExplosion(this, this.cellFromCoord(tileCoord).getTilePosition());
			} else {
				Effect.share().cellOpen(this, this.cellFromCoord(tileCoord).getTilePosition(), globalIndex);
			}
			this.fg.removeTileAt(tileCoord);
		}
	}

	@Override
	public void gameOver(int point, int otherPoint) {
		this.myPoint = point;
		this.otherPoint = otherPoint;
		this.endingZoomOutAndBlastFX();
//		this.performSelector(endingZoomOut, null, 4); // 쓰레드슬립 (메서드명, 인자, 딜레이 타임)
	}
	
	private void endingZoomOut() {
		this.hud.gameOver(myPoint, otherPoint);
	}
	
	private void endingZoomOutAndBlastFX() {
		this.schedule("endingZoomOut", 0.1f);
		this.setIsTouchEnabled(false);
		this.hud.setIsTouchEnabled(false);
	}
	public void endingZoomOut(float dt) {
		Config.getInstance().setDisableButton(true);
		CCNode theLayer = this;
		float newScale = theLayer.getScale() - 0.05f;
		newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale, GameConfig.share().kMaxScale);
		theLayer.setScale(newScale); // 줌 아웃
		theLayer.setPosition(this.getMapCurrentPosition()); // 현재 화면에서 줌 아웃함
		
		// 줌 아웃시 화면 안에서만 줌 아웃 되게함.
		theLayer.setPosition(CGPoint.ccp(
				CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
				CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y)
				));
		if (newScale <= GameConfig.share().kMinScale) {
			this.unschedule("endingZoomOut");
			this.blastAllPumpkin();
		}
	}

	
	private void blastAllPumpkin() {
		for (MineCell2 c : cells) {
			if (c.isMine()) {
//				this.performSelector(blastPumpkin(), c, ((int)(Math.random() * 200)	+ 1) /200);
			}
		}	
	}
	
	private void blastPumpkin(MineCell2 c) {
		this.fg.removeTileAt(c.getTileCoord());
		Effect.share().pumpkinExplosion(this, c.getTilePosition());

	}
	@Override
	public void updateHeart() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int unOpenedCell() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int markedMine() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int difficultyMine() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void updateProgressOwner() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateProgressOther() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateMineNumber() {
		// TODO Auto-generated method stub
		
	}
	
	
}
// Game class end
