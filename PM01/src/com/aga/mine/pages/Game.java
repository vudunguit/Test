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
	
	// gamedata���� �����Ͽ���.
	// ���ڼ� �����Ͽ� �׽�Ʈ�� ���� 3���� ����
	// ������ 1���� ����
	/** Game
	 * @param context
	 */
	Game() {
		Log.e("** Game **", "Instance");
		GameMinimap.getInstance().dealloc();
		 hud.isGameOver = false;
		unopenedTile = 0;
		// �⺻ �ʱ�ȭ
		mContext = CCDirector.sharedDirector().getActivity().getApplicationContext();
//		winSize = CCDirector.sharedDirector().winSize();
		winSize = CCDirector.sharedDirector().winSize();

		//
		if (GameData.share().isMultiGame) {
			Config.getInstance().setDisableButton(true);
		} else {
			Config.getInstance().setDisableButton(false);
		}

		//���̵� ( 0~2 ��,��,���)
		GameData.share().setGameDifficulty(1);
		// �����͸� ����ִ� ���� ��� �ʱ�ȭ
		GameData.share().resetMineNumber();
		// ȣ�ڿ� ��Ÿ���ִ� ���� ��� �ʱ�ȭ
		GameData.share().setMineNumber(GameData.share().maxMineNumber);
		//GameData.share().setMineNumber(GameData.share().getGameDifficulty());
		Log.e("Game / game ", "���̵� : " + GameData.share().getGameDifficulty());
		// ����� �ʱ�ȭ
		GameData.share().setHeartNumber(3); 		
		// ������ �ʱ�ȭ
		GameData.share().resetItem();
//		// ���ӽð� �ʱ�ȭ
//		GameData.share().setSeconds(900); // gameStart�� �̵�

		//
		// �� ������ ���
		// �ȵ���̵� �ٸ�����̶� ����μ��� cocos2D�� �����.
		// selector �޼ҵ� Ȯ���Ұ�
		//CCTouchDispatcher a = CCTouchDispatcher.
		// ?????
		ArrayList<View> a = new ArrayList<View>();
		a.add(CCDirector.sharedDirector().getOpenGLView());
		CCDirector.sharedDirector().getOpenGLView().addTouchables(a);
		
		 //
		 // ���� (�ε�)
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_open2); // ����Ʈ (ȿ����) // (Ÿ��)pickup
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_pumpkin); // ����Ʈ (ȿ����) // (ȣ��)hit
		 SoundEngine.sharedEngine().preloadEffect(
				 this.mContext, R.raw.game_mushroom); // ����Ʈ (ȿ����) // (����)move
		
		//
		// Ÿ�ϸ� �ε�
		this.tileMap = CCTMXTiledMap.tiledMap(GameData.share().gameMap);
		 
		//
		// �� �ø��� �⺻ ũ�� ����
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
		 // Ÿ�ϸ� ���̾� ���
		/*
		this.bg = this.tileMap.layerNamed("Background"); // Layer Name in Tiled
		this.meta = this.tileMap.layerNamed("Meta");
		this.meta.setVisible(false);
		this.fg = this.tileMap.layerNamed("Foreground");
		this.mineLayer = this.tileMap.layerNamed("MineLayer");		 // ���� �� ������ �Ѹ� ���̾�
		this.itemLayer = this.tileMap.layerNamed("ItemLayer");			 // ���� �� ������, ��� �������� ���̾�
		this.flagLayer = this.tileMap.layerNamed("FlagLayer");			 // ��� ���� ���̾�
		this.earthLayer = this.tileMap.layerNamed("CrackedEarth");	 // ���������� ���̾�
	*/
		this.itemLayer = this.tileMap.layerNamed("ItemLayer");			 // ���� �� ������, ��� �������� ���̾�
		this.earthLayer = this.tileMap.layerNamed("CrackedEarth");	 // ���������� ���̾�(������ ����Ʈ)
		this.bg = this.tileMap.layerNamed("Background");					 // Layer Name in Tiled
		this.mineLayer = this.tileMap.layerNamed("MineLayer");		 // ����(ȣ��) �� ������ �Ѹ� ���̾�
		this.fg = this.tileMap.layerNamed("Foreground");					 // �ܵ�
		this.flagLayer = this.tileMap.layerNamed("FlagLayer");			 // ���(����) ���� ���̾�
		this.meta = this.tileMap.layerNamed("Meta");							 // ���� �Ұ� ����
		this.meta.setVisible(false);
	
		
		theLayer.setScale(GameConfig.share().kDefaultScale * 128 / tileMap.getTileSize().width);
		theLayer.setPosition(	(-mapSize.width/2 * theLayer.getScale() + winSize.width /2), 0);
		//theLayer.setPosition(getMapCenterPosition().x, getMapCenterPosition().y - getMapDeltaSize().height);
		currentScale = this.getScale(); // ó�� ������ ����		
		 currentLayerX = this.getPosition().x; // ó�� ȭ���� x��ǥ ����
		 currentLayerY = this.getPosition().y; // ó�� ȭ���� y��ǥ ����
//
//		 progress = new GameProgressBar(mContext);
//		 progress.delegate = (GameProgressBarDelegate) this;
//		 
		// ��ü Ÿ��(��) ���
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
//					// test code // �������� ������ Ÿ�Ͽ� ǥ���� ��
//					CCLabel label = CCLabel.makeLabel(""+ cell.getCell_ID(), "Arial", (30 * tileSize.width) / 128);
//					this.addChild(label);
//					label.setColor(ccColor3B.ccWHITE);
//					label.setPosition(cell.getTilePosition());
//				 
				 if (!this.isCollidable(CGPoint.make(x, y)) && !this.isPreOpened(CGPoint.make(x, y))) {
					// ������ ���� Ÿ�ϼ�
					 unopenedTile ++;
				}
				 //
				 //isCollidable�� ��Ȱ��  ���� �ǹ��Ѵ�.
				 if (this.isCollidable(CGPoint.make(x, y))) 
					cell.setCollidable(true);
				 
				//
				// isPreOpened�� ó������ �����ִ� ������ �������� ����Ѵ�.
				if (this.isPreOpened(CGPoint.make(x, y)))
					cell.setOpened(true);

				count++;
				
			}
		}
		 Log.e("Game / game ", "unopenedTile : " + unopenedTile);
		
		//
		// �ֺ�Ÿ�� ���(��������)
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int i = 0; i < size; i++) {
			/***************************/
			for (int k = -1; k < 2; k++) {
				for (int m = -1; m < 2; m++) {
					//
					// �� �ٱ� ��
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) continue;
					if (cellsTemp.get(i).getTileCoord().x + m < 0 || cellsTemp.get(i).getTileCoord().x + m >= this.tileMap.getMapSize().height) continue;

					//
					// �ֺ� ��
					MineCell2 cellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));

					//
					// ��Ȱ�� ��
					if (cellRound != null && cellRound.isCollidable())
						continue;

					//
					// ��ȿ�� �ֺ��� ���(���� �� ����)
					cellsTemp.get(i).addRoundCell(cellRound);
					
				}
			}
			/***************************/
			
			/***************************/
			// ������ ��ġ�� ���
			// * 2
			// 3 4
			for (int m = 0; m < 2; m++) {
				for (int k = 0; k < 2; k++) {
					//
					// �� �ٱ� ���̸� �Ұ��� ���� �����ϰ� ���� ������.
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}
					if (cellsTemp.get(i).getTileCoord().x + m < 0 || cellsTemp.get(i).getTileCoord().x + m >= this.tileMap.getMapSize().height) {
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}

					//
					// ������ ��ġ ��
					MineCell2 sphereCell = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));

					//
					// ��Ȱ�� ��					
					if (sphereCell != null && sphereCell.isCollidable()){
						cellsTemp.get(i).setSphereBasePossible(false);
						break;
					}
					
					cellsTemp.get(i).addSphereCell(sphereCell);
					
					
				}
			}
			/***************************/
			
			/***************************/
			// ������ �ٱ� �ֺ��� 12�� ���
			// 1 5 7 9
			// 2 * # A
			// 3 # # B
			// 4 6 8 C
			for (int k = -1; k <= 2; k++) {
				for (int m = -1; m <= 2; m++) {
					//
					// �� �ٱ� ��
					if (cellsTemp.get(i).getTileCoord().x + k < 0 || cellsTemp.get(i).getTileCoord().x + k >= this.tileMap.getMapSize().width) continue;
					if (cellsTemp.get(i).getTileCoord().y + m < 0 || cellsTemp.get(i).getTileCoord().y + m >= this.tileMap.getMapSize().height) continue;

					//
					// ���Ǿ� (��ü) ��ġ �� - (��ü�� �ִ� ���� sphereRoundCell ��Ͽ��� ����)
					if (k == 0 ||  k == 1) {
						if (m == 0 || m == 1)
							continue;
					}
					
					//
					// �ֺ��� (��ü�� ��ġ�� ���� ������)
					MineCell2 sphereCellRound = this.cellFromCoord(CGPoint.ccp(
							cellsTemp.get(i).getTileCoord().x + k,
							cellsTemp.get(i).getTileCoord().y + m));
					
					//
					// ��Ȱ�� �� - �� ������� �ʴ´�. (null�� ���ü� ����.)
					if (sphereCellRound.isCollidable()) continue;
					cellsTemp.get(i).addSphereRoundCell(sphereCellRound); // ����� �̻� ���µ�
				}
			}
			
		}
		//�ֺ�Ÿ�� ��� for(i) end
		
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
//		//���� ��ġ
//		this.scatterMines();
		
		// ���� ������ ���������� 
		//
		// ������ ��ġ
		this.scatterSpheres();
		

		//
		// ���� ��ġ
		this.scatterMines();
		
		//
		// ������ ������ �� �ֺ��� ���ڰ� ��ġ�Ǿ����� ���ڼ� ǥ�ø� �Ѵ�.
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
		// ������ ���� ���̾�(this.earthLayer)�� Ÿ���� �����ϰ� ä���.
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
			
		// ���ӽð� �ʱ�ȭ
		GameData.share().setSeconds(900);
		UserData.share(mContext).myBroomstick();
	}
	// ������Game end
	
	private void aaaaa() {
			Log.e("Game / game", "I'm Ready!");
			try {
				NetworkController.getInstance().sendGameReady();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void bbbbb() {
		// ���ӽð� �ʱ�ȭ
		GameData.share().setSeconds(900);
		Config.getInstance().setDisableButton(false);
//		Game.HudLayer.testText.setString("���� ����! testText"); // ���� ��⿡���� �������...
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
 * ��������
 * �ִϸ��̼�
 * 
 * �̸�Ƽ�� �ִϴ� ��ġ�� �������� �ʾҽ��ϴ�.
 * Game �Ǵ� Hud�� ������ ���� �־��ֽø� �˴ϴ�.
 **/
	static CCScene scene = null;
//	public CCScene scene() {
	public static CCScene scene() {
		// �������� static���� ��� ������ �����Ͽ�����. �̻�� ���� ��� �ٲܰ�
		scene = CCScene.node();
		
		// ���� ���̾�
//		Game game = Game.getInstance();		
		Game game = new Game();		
		scene.addChild((CCLayer)game);
		game.setAnchorPoint(0.0f, 0.0f); /*** �߿� ***/
		
		// ��� �� ���÷��� ���̾�
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
	// ���� ȭ���� position�� ����
	public CGPoint getMapCurrentPosition() {
		// ȭ���� �ܾƿ��ϸ� ���⸦ ���Ž.
		// ȭ���� �������� currentLayerX,currentLayerY, currentScale ����
		//��ġ���� (allTouches count == 1)���� ��������  �� ������ �̰����� �������
		//CGPoint centerPosition = CGPoint.ccp(currentLayerX / currentScale, currentLayerY / currentScale);
		// game Layer�� anchor point���� ��� �����ϰ��� ����

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
	// ���̵� ���� (���̵��� ���� ���ڼ��� ������)
	public void scatterMines() {
		 //(void)scatterMines:(NSMutableArray *)cells onLayer:(CCTMXLayer *)layer{ ������ ���̶�µ�...
		//
		// ���̵��� ����
		//GameData.share().getMineNumber(gameData.share.getGameDifficulty);
		final int maxMineNumber = GameData.share().getMineNumber();
		//Log.e("Game / scatterMines", "getMineNumber : " + GameData.share().getMineNumber());
		
		// unsigned
		// ���������� ��ȯ
		int mineGid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(0f, 0f))); // mind gid
		//Log.e("Game / scatterMines", "mineGid : " + mineGid);
		for (int i = 0; i < maxMineNumber; i++) {
			boolean isBoolean = true;
			while (isBoolean) {
				//
				// ������ �� �ϳ� ����
				//cells�� �����߿� �����Ǽ� �߻�???
				int rand = (int)(Math.random() * cells.size());
				MineCell2 cell = cells.get(rand);
				//Log.e("Game / scatterMines", i + "/" + maxMineNumber);
				
				//
				// ���� ������ ���� ���� ���µ� ���� ���ڰ� �ƴϰ� �� �̸� ���ڷ� �����Ѵ�.
				//if (cell.isMine() || cell.isOpened() || cell.isCollidable() || cell.isSphere()) {
				if (!cell.isMine() && !cell.isOpened() && !cell.isCollidable() && !cell.isSphere() && !this.isDontSetMine(cell.getTileCoord())) {
					//if(cell.isMine == NO && cell.isOpened == NO && cell.isCollidable == NO && cell.isSphere == NO && [self isDontSetMine:cell.tileCoord] == NO)
					
					cell.setMine(true);
					this.mineLayer.setTileGID(mineGid, cell.getTileCoord()); // ���̾ ��������� ������!
					//this.fg.setTileGID(mineGid, cell.tileCoord); // for test
					isBoolean = false;
				}
			}
		}
	}

	public void scatterSpheres() {
		for (int i = 0; i < GameData.share().kNumberOfSphere; i++) {
			//
			// ������ Ȯ���� ���� �������� ������ �����Ѵ�.
			// unsigned ����
			final int fireChange = GameData.share().kFireChance;
			final int windChange = GameData.share().kWindChance;
			final int cloudChange = GameData.share().kCloudChance;
			final int mirrorChange = GameData.share().kMirrorChance;
			final int divineChange = GameData.share().kDivineChance;
			final int earthChange = GameData.share().kEarthChance;
			
			// unsigned ����
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
				// ������ �� �ϳ� ����
				int rand = (int) (Math.random() * cells.size());
				MineCell2 cell = cells.get(rand);
				
				//
				// ** (1) ���� ������ ���� ���� ������ �ڸ� 4���� ����
				//		- 1) ���µ� ���� �ƴϰ�
				//		- 2) ���ڰ� �ƴϰ�
				//		- 3) ��ȿ�� ��
				//		- 4) �̹� �������� ��ġ�� ���� �ƴϸ� ������ ������ �������� �����Ѵ�.
				
				//
				// ** (2) ��ݼ��� ������ ���̾�� �Ѵ�.
				//isBoolean = false; // �ӽÿ�. isSphereBasePossible�� ���� ������ false�̴�.
				if(cell.isSphereCellsClear() && cell.isSphereBasePossible()) {
					//
					// ������ ��ġ�� ��ݼ� ����Ͽ� ���� üũ�� ���
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
		
		// 0�� user�� �������� ȹ�� 0�� �ƴϸ� ���� �ʿ� ������ ��ġ
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
			//sphereType = sphereType == 0 ? 7 : sphereType; // tilemap�� 7��° Ÿ��(0:7~3:7)��  �� �������� �־
			
			int gid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(counter, sphereType)));
			//layer.setTileGID(gid, cell.getTileCoord());
			layer.setTileGID(gid, cell.getTileCoord());
			// test code // ������ ���� �հ� ����
			this.fg.setTileGID(gid, cell.getTileCoord()); // �ӽÿ� ���� ������ �������� ��ĥ��
			
			//
			// test code // �������� ������ Ÿ�Ͽ� ǥ���� ��
//			CCLabel label = CCLabel.makeLabel(""+ cell.getUnSignedCellId(), "Arial", (30 * tileSize.width) / 128);
//			this.addChild(label, 1000);
//			label.setColor(ccColor3B.ccc3(240, 0, 240));
//			label.setPosition(cell.getTilePosition());
			counter++;
			/*
			// ������ ���½� ������ĭ �����ʿ� tile ��ĭ�� �߰�
			if (counter == 4 && sphereType == 7) {
				gid = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(counter, sphereType)));
				layer.setTileGID(gid, CGPoint.ccp(cell.getTileCoord().x+1, cell.getTileCoord().y));
				this.fg.setTileGID(gid, CGPoint.ccp(cell.getTileCoord().x+1, cell.getTileCoord().y)); // �ӽÿ� ���� ������ �������� ��ĥ��
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
	
	// ���� ��ǥ�� ���� ��ġ�� Ŭ���ߴ��� Ȯ���ϴ� �޼ҵ�
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
		// sqrt = ������ , pow = �� ��
		return (float) Math.sqrt(
				Math.pow(point1.x - point2.x, 2.0f) + 
				Math.pow(point1.y - point2.y, 2.0f));
	}

	//
	//position�� ��� ȭ�� �߽����� ��������.
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
		// ��ȭ�Ǵ� scale�� ���� map�� ����� ���� (1:1, 3456:5120)
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
			
			// scale ���� �̻� ���� �߻�
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

	// previousLocation�� ���� �ִµ� �̺�Ʈ �޾ƿö� �� �����Ұ�
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
		// �ּ�/�ִ� Ȯ���� �������� ���̾� Ȯ�� ��Ҹ� ��ġ �Ÿ��� ���� �Ѵ�.
		float newScale = theLayer.getScale() + (deltaDistance * GameConfig.share().kPinchZoomMultiplier);
		
		// clampf �׳� ���� �ȿ� ������ Ȯ���� ����(128 pixel ����)
		newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale * 128 / tileSize.width, GameConfig.share().kMaxScale * 128 / tileSize.width);
		
		
		Log.e("Game / layerScale", "scale:" + ((int)(theLayer.getScale()*100)/100f) +", new scale:"+ ((int)(newScale*100)/100f) +", delta:" + (int)deltaDistance);
		float positionScale = newScale - theLayer.getScale();
		theLayer.setScale(newScale); // �ܾƿ��� �ϰ�����
		Log.e("Game / layerScale", "theLayer position" + theLayer.getPosition());
		Log.e("Game / layerScale", "theLayer getAnchorPoint" + theLayer.getAnchorPoint());
		theLayer.setPosition(CGPoint.ccpSub(theLayer.getPosition(), 
				CGPoint.make(pinchCenter.x * positionScale, pinchCenter.y * positionScale) )); // ������ ȭ�鿡�� �ܾƿ��� ��
		
		// ���⼭ ȭ�� �и� �߻�
		theLayer.setPosition(CGPoint.ccp(
				CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
				CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
		);
		
//		mapLabel.setString("mapLabel.getPosition() : " + mapLabel.getPosition());
//		mapLabel.setPosition(mapLabel.getPosition());
	}

	// handle touches
		 
		//
		// ����ġ : ��� �ȱ�
		public void handleLongPress(MotionEvent event) {
			
			//Log.e("Game / handleLongPress", "click : " + event.getAction());
				CGPoint location = CGPoint.ccp(event.getRawX(), event.getRawY());
				// 4��и��� 1��и����� ��ȯ
				location = CCDirector.sharedDirector().convertToGL(location);
				// GameLayer �����ϴ����κ��� ��ġ ��ġ������ �Ÿ���(x,y)
				location = this.convertToNodeSpace(location);
				CGPoint location2 = this.convertToNodeSpace(location);
				// Ÿ���� ��� ��ǥ���� Ȯ���Ͽ� �� �ҷ�����
				CGPoint coord = this.tileCoordForPosition(location);
				//Log.e("Game / handleLongPress", "coord : " + coord); // ������� 0,0 �����ϴ� 26, 39
				
	
				ArrayList<MineCell2> cellArray = cells;
				int size = cellArray.size();
				for (int k = 0; k < size; k++) {
					
					//Log.e("Game / handleLongPress", "(" + k + ") : " + cellArray.get(k).isMarked() + "," + cellArray.get(k).isCollidable() + "," + cellArray.get(k).isMine() + "," + cellArray.get(k).isSphere() + ",");
					int numberOfMine = cellArray.get(k).getNumberOfMineAround();
					if (CGPoint.equalToPoint(cellArray.get(k).getTileCoord(), coord) && !cellArray.get(k).isCollidable()) {
						//Log.e("Game", "long press recognized if");
	
						// effect sound play
						SoundEngine.sharedEngine().playSound(this.mContext, R.raw.game_mushroom, false);
	
						// �Ⱦ����ִ� ����(���)�� ����Ҷ� ����(���)�� ������
						if (cellArray.get(k).isMarked()) {
							if (numberOfMine == -1) {
								// ���ڰ� �ִ� �ڸ��� ����(���)�� �����ִ°� ����Ҷ�
								// ������ +1 �������ذ��� ����Ѵ�.(�ٽ� -1)
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
							// ���¾ȵ� ���� ����(���)�ȱ�
							if (!cellArray.get(k).isOpened()) {
								// ���ڰ� �ִ� �ڸ��� ����� ���� ��츸 Ž
								if (numberOfMine == -1) {
									//
								//	int mine = GameData.share().currentMineNumber();
									// �����ִ� ���� ȣ�� (���) �� �ȴ� ���� ���̵� ���ڼ��� ������ ���ӿ���
									// gamedata.share.getgamedifficulty�� ���̵� �����ϴ�
									// â���� �����Ǽ� ����
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
								//Log.e("Game / handleLongPress", "���� ����");
							}
							this.hud.updateProgress();
							break;

					}
				}
				// end for
		}

	//
	// ������ġ : �� ����
	public void handleDoubleTap(MotionEvent event) {
		Log.e("Game / handleDoubleTap", "���� ���� : " + GameData.share().getMineNumber());
	
		if (!Config.getInstance().isDisableButton()) {
			//Log.e("Game / handleDoubleTap", "enableButton");
	
			CGPoint location = null;
			CGPoint coord = null;
			
			// GL ��ǥ
			location = CGPoint.ccp(event.getRawX(), event.getRawY());
			// cocos2d ��ǥ�� ��ȯ
			location = CCDirector.sharedDirector().convertToGL(location);
			// layer ���� �ϴ����� ����  ��ǥ������ �Ÿ�
			location = this.convertToNodeSpace(location);
			// Ÿ���� ��� ��ǥ���� Ȯ���Ͽ� �� �ҷ�����
			coord = this.tileCoordForPosition(location);
			
			for (final MineCell2 cell : cells) {
					int numberOfMine = cell.getNumberOfMushroomAndPumpkinAround();
					
					// ��ü Ÿ���� �˻��Ͽ� ��ġ�� ��ġ�� Ÿ���� ��ġ���� ��ġ�ҽ�
					if (CGPoint.equalToPoint(cell.getTileCoord(), coord) && !cell.isCollidable()) {
						// ���µ� �� ������
						if (cell.isOpened()) {
							// �� ����� �ֺ� ���μ��� �� 1�̻��̸�
							if (numberOfMine > 0) {
								cell.roundOpen();
							}
							// �����ִ� �� ������
						} else {
							//������ ���� �����ش�.(tile�� fg�� ����)
							
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
				// ��� ���� �������� �ִ��� Ȯ���Ѵ�.
				float startDelay = 0;
				for (MineCell2 cell : sphereBaseCells) {
		
					//
					// -1 : none
					// 0 : empty
					// > 0 : sphereType
					int sphereType = cell.getSphereItem();
					if (sphereType > 0) {
						
						// ���� ������ ���鿡 �������� ���ȴ�.
						// �� �������� �����ϰ�,
						cell.setSphereType(kSphereTypeEmpty);
		
						//Log.e("Game / handleDoubleTap", "check 1 / ������ Ÿ�� ����");
						// �� ������ Ÿ�Ϸ� �ٲ۴�.
						this.addSphereTo(this.mineLayer, kSphereTypeEmpty, cell);
						
						// ������ ȹ����� Ÿ�Ժ��� �ϳ� ������Ų��.
						GameData.share().increaseItemByType(sphereType);
						
						// UI ������Ʈ�� �Ѵ�.
						// �������� Ŭ���ؼ� Ȱ��ȭ��Ű�� +1 �� �󺧿� �־��ش�.
						this.hud.updateSphereItemNumber();
						
						// ��ư�� Ŭ��ȿ���� �ִ´�.
						this.hud.clickEffect(sphereType, startDelay);
						startDelay += 0.2f;
					}
				}
				// end ��� ���� �������� �ִ��� Ȯ���Ѵ�. 
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
		// device ��ǥ�� �о�´�. (openGL x)
		currentTouchLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
//		previousTouchLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		
		isLongTap = true;
		
//		// effect sound load
//		SoundEngine.sharedEngine().preloadSound(this.mContext, R.raw.ding); // ���� ��������??
		
		// ���� ���� ��ǥ ���
		CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		double tempTime = System.currentTimeMillis();
		
		boolean abc = locationMatchingTest(touchLocation, convertedLocation, 20); // default : 20
		
		// ������ (�����ð� 0.3��)
		if (abc && doubleTap + 500 > tempTime && event.getPointerCount()  == 1) {
		//if (abc && doubleTap + 500 > tempTime) {
			handleDoubleTap(event);
			isLongTap = false;
			touchLocation = convertedLocation; // ��ǥ�� ������ ����
			doubleTap = 0;
			//handleDoubleTap(event);
			
		// �̱���	
		} else
			{ // ���� ��ġ�� ���� �Ǿ�����
			touchLocation = convertedLocation; // ��ǥ�� ������ ����
			doubleTap = tempTime;
		}
		return super.ccTouchesBegan(event);
	}

	// ȭ���̵� �� ���� �ƿ���
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		 if (!Config.getInstance().isDisableButton()) {

		//
		// ���� �� ���̾� ����
		CCLayer theLayer = this;
		boolean isMove = true;

		//
		// �Ϲ�

		/*************************************/
		// ���� ���� ��ǥ ���
		// convert event location to CCPoint
		CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getRawX(), event.getRawY()));
		double tempTime = System.currentTimeMillis();
		boolean abc = locationMatchingTest(touchLocation, convertedLocation, 10); // default : 10
		
		//��Ŭ�� (������ ���ػ󵵿����� ��ġ ���갡 �Ͼ�� �ʴ´�.)
		if (event.getPointerCount() == 1 && isLongTap) {
			if (abc) {
				if (doubleTap + 300 < tempTime) {
					handleLongPress(event);
					convertedLocation = this.convertToNodeSpace(convertedLocation);
					isLongTap = false;
					touchLocation = convertedLocation; // ��ǥ�� ������ ����
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
			
			
			// sqrt = ������ , pow = �� ��
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
			// �ּ�/�ִ� Ȯ���� �������� ���̾� Ȯ�� ��Ҹ� ��ġ �Ÿ��� ���� �Ѵ�.
			float newScale = theLayer.getScale() + (deltaDistance * GameConfig.share().kPinchZoomMultiplier);
			// clampf �׳� ���� �ȿ� ������ Ȯ���� ���� (128pixel ����)
			newScale = CGPoint.clampf(newScale, GameConfig.share().kMinScale * 128 / tileSize.width, GameConfig.share().kMaxScale * 128 / tileSize.width);
			float positionScale = newScale - theLayer.getScale();
			theLayer.setScale(newScale); // �ܾƿ��� �ϰ�����
			Log.e("Game / game",
					"cLocation1 : (" + (int)currentLocation1.x + ", " +  (int)currentLocation1.y +"), pLocation1 : (" + (int)previousLocation1.x + ", " +  (int)previousLocation1.y);		
			Log.e("Game / game",
					"cLocation2 : (" + (int)currentLocation2.x + ", " +  (int)currentLocation2.y +"), pLocation2 : (" + (int)previousLocation2.x + ", " +  (int)previousLocation2.y);		
			Log.e("Game / game", "MapCurrentPosition : " + this.getMapCurrentPosition());		
//			theLayer.setPosition(this.getMapCurrentPosition()); // ������ ȭ�鿡�� �ܾƿ��� ��
			theLayer.setPosition(CGPoint.ccpSub(theLayer.getPosition(), 
					CGPoint.make(pinchCenter.x * positionScale, pinchCenter.y * positionScale) )); // ������ ȭ�鿡�� �ܾƿ��� ��
			
			
			// scale ���� �̻� ���� �߻�
			theLayer.setPosition(CGPoint.ccp(
					CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
					CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
			);
			// �ܰ� ���� ����(���� ��ġ���� Ʋ�� ������.)
			// �ܾƿ��Ҷ� ȭ��ȿ����� �� �� �ǰ� ����

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

		    // ����??
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
			// scale ���� �̻� ���� �߻�
			theLayer.setPosition(CGPoint.ccp(
					CGPoint.clampf(theLayer.getPosition().x, this.getMapMinPosition().x, this.getMapMaxPosition().x),
					CGPoint.clampf(theLayer.getPosition().y, this.getMapMinPosition().y, this.getMapMaxPosition().y))
			);
			//Log.e("Game / Moved", "1touch theLayer1 : " + theLayer.getPosition());
			// ccpAdd : return ccp(v1.x + v2.x,v1.y + v2.y);
			// ��׶��� �̹��� ������ deviceView ������ �� ����� ��
			// (������ ȭ���� �����°����� ���ߴ°� �ƴ϶� ������ ������)
			
			// �ܰ� ���� ����(���� ��ġ���� Ʋ�� ������.)
			
//			mapLabel.setPosition(CGPoint.ccp(this.getPosition().x/2 * Xscale,this.getPosition().y/2 * Yscale));
//			mapLabel.setString("mapPosition :"+ (int)this.getPosition().x + ", " +(int)this.getPosition().y);
		
			//Log.e("Game / game", "this.getAnchorPoint : " + this.getAnchorPoint() + " / this.getPosition : " + (int)this.getPosition().x + ", " +(int)this.getPosition().y );			
			
			// scale
			currentScale = this.getScale(); // ������ ���� �������� ����
			currentLayerX = theLayer.getPosition().x; // ���� ȭ���� x ���� ���� 
			currentLayerY = theLayer.getPosition().y; // ���� ȭ���� y ���� ����
					 
			// �̵����⿡ ���߾� ������ ��������Ʈ �ؽ�ó�� �ٲپ� �ش�.
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
		//ȭ�� �߰� ��ǥ
		float wid = 	winSize.width/2;
		float hei = winSize.height/2;  
		// layer���� ȭ�� �߰������� ��ǥ(GL --> cocos2D --> nodeSpace)
		return this.convertToNodeSpace(CCDirector.sharedDirector().convertToGL(CGPoint.make(wid, hei)));
	}
	
	// current layer position + (���� ��ǥ - ���� ��ǥ) * (before scale - (after ��ǥ / before ��ǥ))
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

		int flagGid = this.itemLayer.tileGIDAt(CGPoint.ccp(1, 0)); // �������(����)
		flagGid = CCFormatter.swapIntToLittleEndian(flagGid);
		this.flagLayer.setTileGID(flagGid, tileCoord);
		
		//
		// ���ڼ� �ϳ� ���ҽ�ų�� ���÷��� ������Ʈ ��Ų��.
		this.hud.updateMineNumber(GameData.share().decreaseMineNumber());
	}

	//
	// effects
	public void rotateAllMineNumberLabel() {
		//
		// ���� ���̴� ��ȣ���� 2���� 2�ʰ� ������ �����.
		ArrayList<MineCell2> cellsTemp = cells;
		int size = cellsTemp.size();
		for (int k = 0; k < size; k++) {
			//rotateBy rot = ~~~~~
			//���ӽð�, ȸ������
			CCRotateBy rot = CCRotateBy.action(2f, 360f);
			CCLabel label = (CCLabel) this.getChildByTag(cellsTemp.get(k).getCell_ID()); // �ɶ��� ����??
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

	// �ֺ� ���� ��
	public void displayMineNumber(int numberOfMine, CGPoint position, int tag) {
			if (this.isCollidable(this.tileCoordForPosition(position))) {
				return;
			}
				// ���� ũ�� ������ 2/3�� ����(���� : ������ �ٴۿ� ���ڰ� ���ľ� �Ǽ� �������� ���� ���ڰ� ������. - ������ �����)
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
		tileGid = CCFormatter.swapIntToLittleEndian(tileGid); // ���� ���� �𸣰���.
		// 0 : �Ͻ� Ÿ�ϰ� ����
		
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
					// ���� ���� �߻� stack over flow Error (thread ����)
					this.getFg().removeTileAt(tileCoord);
				}
	
			} else {
				// ��ź�� ���µ� Ÿ���� �Ⱦ������� ����
				//Log.e("Game / removeTile", "properties - boom empty");
			}
		} else {
			//Log.e("Game / removeTile", "properties - tile empty");
			SoundEngine.sharedEngine().playEffect(this.mContext,R.raw.game_open2); // pickup
			// ���� stack over flow error
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

		// metaLayer (�۾� �Ұ��� ����)
		public boolean isMetaChecked(CGPoint tileCoord, String metaString) {
			// tile�� �ִ� ���� 0�� �ƴ� ���� ������.
			int gid = CCFormatter.swapIntToLittleEndian(this.meta.tileGIDAt(tileCoord));
			// tile�� �򸰰�
			if (gid > 0) {
				// tileMap�� GID�� key�� �־� HashMap���� ��ȯ�Ͽ� Properties�� �����Ѵ�.
				HashMap<String, String> Properties = this.tileMap.propertiesForGID(gid);
				// <--- ���� ������ �Ƹ� Ű���� �ȵ�� �־ �ΰͰ��� Ȯ�� �ʿ�.
				if (Properties !=null && Properties.size() != 0) {
					// tile�� ���鶧 tile�� �̸��� �־��� �Ͱ� ���� �̸��� Ÿ���� ã�´�.
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
	// ���� �׽�Ʈ Ÿ�ϸʿ��� isPreOpenedŸ���� ��ŸŸ�� �� �ٷ� ������ ���� Ÿ���� �̿��ϰ� ����????
	public boolean isPreOpened(CGPoint tileCoord) {
		return this.isMetaChecked(tileCoord, "isPreOpened");
	}
	
	public boolean isCollidable(CGPoint tileCoord) {
		return this.isMetaChecked(tileCoord, "isCollidable");
	}
	
	public void removeFlag(CGPoint tileCoord) {
		this.flagLayer.removeTileAt(tileCoord);
		
		//
		// ���ڼ� �ϳ� ������Ű�� ���÷��� ������Ʈ ��Ų��.
		this.hud.updateMineNumber(GameData.share().increaseMineNumber());
	}

	public CCTMXLayer getFg() {
		return fg;
	}

	public void setFg(CCTMXLayer fg) {
		this.fg = fg;
	}

	// ���� �ٸ�...
	public CCSprite animationMagma() {
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(folder + "effectMagma-hd.plist");
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 9; i++) {
			String frameName = "magma" + i + ".png";
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(frameName));
		}
		// ��...  name String�� �ٸ���
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
	final int kDirectionDown 			= 0;			/***  ��   ***/ 
	final int kDirectionLeftDown 	= 45;		/*** ��  ***/
	final int kDirectionLeft 				= 90;		/*** ��  ***/
	final int kDirectionLeftUp 			= 135;		/*** ��  ***/
	final int kDirectionUp 				= 180;		/*** ��    ***/
	final int kDirectionRightUp 		= 225;		/*** ��  ***/
	final int kDirectionRight 			= 270;		/*** ��  ***/
	final int kDirectionRightDown = 315;		/*** ��  ***/
	
	//
	// ȭ�� �����϶�
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
				//Log.e("Direction", "��");
				return kDirectionRightUp;
			} else if (dv.x > 0 && dv.y < 0) {
				//Log.e("Direction", "��");
				return kDirectionRightDown;
			} else if (dv.x < 0 && dv.y > 0) {
				//Log.e("Direction", "��");
				return kDirectionLeftUp;
			} else if (dv.x < 0 && dv.y < 0) {
				//Log.e("Direction", "��");
				return kDirectionLeftDown;
			}
		}

		if (isVertical) {
			if (dv.y > 0) {
				//Log.e("Direction", "��");
				return kDirectionUp;
			} else {
				//Log.e("Direction", "��");
				return kDirectionDown;
			}
		}

		if (isHorizontal) {
			if (dv.x > 0) {
				//Log.e("Direction", "��");
				return kDirectionRight;
			} else {
				//Log.e("Direction", "��");
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

		// (����) �±� �ѹ�
		static final int kTagHeart = 9999; // final x
		static final int kButtonOff = 0; // final x
		static final int kButtonOn = 1; // final x

		GameEnding gameEnding;
		HudLayer controlHudLayer;
		
		public HudLayer() {
			this.winSize = CCDirector.sharedDirector().winSize();
			this.mContext = CCDirector.sharedDirector().getActivity().getApplicationContext();

			// ��ġ�� �ȵɽ� �Ʒ� ����� ��Ȱ��ȭ �� ��
			//CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
			//this.setIsTouchEnabled(true);
			//this.isTouchEnabled_ = true; // cclayer ���
			
			int margin = 5;
			label = CCLabel.makeLabel("0", "verdana-Bold", (18 * tilePixelSize) / 128);
			label.setColor(ccColor3B.ccBLACK);
			label.setPosition(
					winSize.width - (label.getContentSize().width / 2) - margin, 
					(label.getContentSize().height / 2) - margin);
			this.addChild(label);
	
			int testTextSize = 72;
			testText = CCLabel.makeLabel("�ð��� �ʱ�ȭ �Ǹ� ����", "Arial", (testTextSize * tilePixelSize) / 128);
			testText.setColor(ccColor3B.ccWHITE);
			testText.setPosition(winSize.width/2, winSize.height / 2);
					this.addChild(testText);
					//
					// ��� �߾� ��������ð� �� ���� �����
					gameProgressBar = new GameProgressBar(this.mContext);
					this.addChild(gameProgressBar);
					//gameProgressBar.startTime(gameEnding);
					//Log.e("HudLayer / ", "minimap setting complete");adsa
					//gameProgressBar.startTime(controlHudLayer);
					gameProgressBar.startTime();

					
					//
					// �»�� ã�� ���� ���� �� ���� ǥ��
					CCSprite statusBase = CCSprite.sprite(hudLayerFolder + "game-statusBase-hd.png");
					statusBase.setPosition(
							statusBase.getContentSize().width / 2 + margin, 
							winSize.height - statusBase.getContentSize().height / 2 - margin);
					this.addChild(statusBase);
					
					//  ���� �ʿ��� ã�ƾߵǴ� ����(ȣ��)�� ���� ����
					statusMine = CCLabel.makeLabel(
//							GameData.share().getMineNumber() + " ", "AvenirNextCondensed-Bold", 11);
							GameData.share().getMineNumber() + " ", "Arial-Bold", 30);
					statusMine.setPosition(
							statusBase.getContentSize().width / 2,
							statusBase.getContentSize().height / 4);
					statusBase.addChild(statusMine);
					this.updateHeart(); // ���� ǥ�� �ʱ�ȭ
		
					//
					// ���� �̴ϸ� ��ư
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
					// �ϴ� ������ ������
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
					
					// �߰��ϸ� ��ġ�� �ȵ�.  Ȯ���� ��
					
					//
					// �̸�Ƽ��
					GameEmoticon gameEmoticon = new GameEmoticon();
					this.addChild(gameEmoticon);
					
					//
					// test
					// this.schedule("tick", 0.1f);
					this.updateSphereItemNumber();
		
					//
					// test code - ������ ���۵Ǹ� ���ӿ��� ȭ���� �ٷζ�
					// GameEnding ending = GameEnding.share(this.mContext);
					// this.addChild(ending, GameConfig.share().kDepthPopup);
		
					//
					// test code
					// GameLoading loading = GameLoading.share(this.mContext);
					// this.addChild(loading, GameConfig.share().kDepthPopup);
		
					//
					// test code
					// game�� hudlayer�� mcontext�Ѵ� ����.
					UserData userData = UserData.share(mContext);
		
					//
					// ������
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
			// value�� 3
			int value = GameData.share().getHeartNumber();

			for (int i = 0; i < GameData.share().kMaxHeartNumber * 2; i++) {
				this.removeChildByTag(kTagHeart, true);
			}

			// ��Ʈ ��ġ
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
			
			// ����� ��⿡���� �����Ÿ�.
			CCTexture2D t2d = CCTextureCache.sharedTextureCache().addImage(frameName); 
			CCSpriteFrame frame = CCSpriteFrame.frame(t2d,CGRect.make(0,0,301,277),CGPoint.ccp(0,0));
			
			magician.setDisplayFrame(frame);
		}

		//
		// ������������ ��ư Ȱ��ȭ ����
		// @ sphereType : ������ ����
		// @ state : Ȱ��ȭ ����, kBUttonOn, kButtonOff;


		// button callback
		public void clickEffect(int sphereType) {
			this.clickEffect(sphereType, 0.0f);
		}

		// �ȵɼ��� �ִ� �κ�
		public void clickEffect(int sphereType, float startDelay) {
			//
			// Ȱ��ȭ�� �����۹�ư�� Ŭ�� ȿ�� �������̸� �����ð� �״� ����.
			CCSprite overlay = CCSprite.sprite(hudLayerFolder + "game-itemOver-hd.png");

			for (CCNode itemNode : itemMenu.getChildren()) {
				CCMenuItemToggle item = (CCMenuItemToggle)itemNode;
				if (item.getTag() == sphereType && item.selectedIndex() == kButtonOn) {
					item.addChild(overlay);
					overlay.setPosition(item.getContentSize().width / 2, item.getContentSize().height / 2);
					// flashOut �޼ҵ尡 �ȵǴ� �κ��� �־� ���Ƶ���.(remove node) ��� �ٸ��� �־ �׽�Ʈ��...
					Utility.getInstance().flashOut(overlay, startDelay, 0.5f);
					break;
				}
			}
			//Log.e("Game / clickEffect", "end"); 
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*****************************************************/
		/** ��������
		 *  
		 * �ִϸ��̼�
		 * ������ �ߵ� �ִ�
		 * 
		 * ������ ���� �ִϴ� GameMinimap.java �� receivePlayData method Ȯ��
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
					effectName = "�Ҹ���";
					break;
				case kButtonWind:
					//Log.e("button pressed", "kButtonWind");
					effectName = "�ٶ�����";
					break;
				case kButtonCloud:
					//Log.e("button pressed", "kButtonCloud");
					effectName = "��������";
					break;
				case kButtonDivine:
					//Log.e("button pressed", "kButtonDivine");
					effectName = "�ż�����";
					break;
				case kButtonEarth:
					//Log.e("button pressed", "kButtonEarth");
					effectName = "��������";
					break;
				case kButtonMirror:
					//Log.e("button pressed", "kButtonMirror");
					effectName = "�ݻ縶��";
					break;

				default:
					//Log.e("button pressed", "default");
					effectName = "�������� ����";
					break;
				}
				final String alertText  = effectName;
				//
				// ������������ ��ư Ŭ�� ����
				if (((CCMenuItem) button).getTag() >= 1 && ((CCMenuItem) button).getTag() <= 6) {
					//
					// ������ �׼�
					// - �ִϸ��̼��� ���� �÷ȴ� ������ ����������.
					// - ���� ĳ���ʹ� ���ȴٰ� �ִϸ��̼��� ���� �Ŀ� (0.4�� ����) �ٽ� ���̵��� �Ѵ�.
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
								// �����ۼ��� ���ҽ�Ű��
								// �� ���÷��̸� ������Ʈ ��Ű��
								// ��ư Ŭ�� ȿ��

								// ���潺�� ���� ���� �ʿ� ����????
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
								// �ߵ� ����Ʈ (�⺻ Ȯ�� ��� ���â)
								AlertDialog.Builder builder = 
										new AlertDialog.Builder(CCDirector.sharedDirector().getActivity());
								builder
								//.setMessage(alertText + " �ߵ�")
								.setTitle(alertText + " �ߵ�")
										.setPositiveButton(
												"Ȯ��",new DialogInterface.OnClickListener() {
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
		/** ��������
		 *  
		 * �ִϸ��̼�
		 * ���� ���� �ִ� GameEnding.java
		 * 
		 * @return
		 */
		static boolean isGameOver = false; // ���� ���� �޽����� ������ ���� �����°��� ������.
		
//		public static void gameOver() {
		public static void gameOver(int myPoint, int otherPoint) {
			Log.e("Game / HudLayer / gameOver", "gameEnding - gogo");
			Config.getInstance().setDisableButton(true);
			
			if (GameData.share().isMultiGame) {
				 // ���� ���� �޽����� ������ ���� �����°��� ������.
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
		
		// ���� ĳ���� �󱼸��ִ� ���α׷����ٿ� ĳ�� �� �����̰� ����
		public static void updateProgress() {
			int gameDifficulty  = GameData.share().getGameDifficulty();
			int maxMine = GameData.share().getMaxMineNumber(gameDifficulty);
			int remainedMine = GameData.share().getMineNumber();
			float progress = (float)remainedMine / (float)maxMine;
			progress = 1- progress;
			gameProgressBar.progress(progress, gameProgressBar.kTagIndicatorMe);
		}
		
		// ���ڰ��� ���ڷ� ǥ���ϴ� �޼���
		public static void updateMineNumber(int remainedMineNumber) {
			statusMine.setString("" + remainedMineNumber);
		}

		// �������� Ȱ��ȭ��Ű�� +1�� ���� �ٰ���
		public void updateSphereItemNumber() {
			//
			// ��ü �� ����
			for (int i = 0; i < 6; i++) {
				this.removeChildByTag(999, true);
			}

			for (int i = 0; i < 6; i++) {
				//
				// �� �� �߰�
				int sphereType = i + 1;
				int itemNumber = GameData.share().getItemNumberByType(sphereType);
				String string = itemNumber > 0 ? "+" + itemNumber : "  " ;
				//CCLabel l = CCLabel.makeLabel(string, "Arial", 12);
				// �����ۺ� ������ ǥ���ϴ� ����
				CCLabel l = CCLabel.makeLabel(string, "Arial-Bold", 24);
				l.setTag(999);
				this.addChild(l);
				l.setPosition(i * 50f*2 + 50f*2, l.getContentSize().height * 1.2f);

				//
				// ��ư ���� ����
				this.setSphereItemState(sphereType, itemNumber > 0 ?
						kButtonOn : kButtonOff);
			}
			//Log.e("Game / updateSphereItemNumber", "end");
		}

		// ���� �ڵ忡�� �ڵ��� �ϵ� ���Ƽ� 
		//���� �ۼ��Ѱ� ���ư��� �� ���ư��°��� �𸣰ڴ�.
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
	/**************************** ���� ****************************/
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
//		this.performSelector(endingZoomOut, null, 4); // �����彽�� (�޼����, ����, ������ Ÿ��)
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
		theLayer.setScale(newScale); // �� �ƿ�
		theLayer.setPosition(this.getMapCurrentPosition()); // ���� ȭ�鿡�� �� �ƿ���
		
		// �� �ƿ��� ȭ�� �ȿ����� �� �ƿ� �ǰ���.
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
