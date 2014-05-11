package com.aga.mine.pages;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXLayer;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.CCFormatter;

import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.Utility;
import com.aga.mine.pages2.GameData;

public class GameMinimap extends CCLayer{
	
	String hudLayerFolder =  "61hud/";
	String mapFolder =  "80map/";

	final int kButtonBase = 0;
	final int kButtonSend = 1;
	final int kButtonExit = 9;
	final int kTagMinimap = 9;

	CCTMXTiledMap tileMap = null;
	static CCSprite base  = null;
	//배경
	CCTMXLayer bg = null;
	CCTMXLayer meta = null;
	//전경?
	CCTMXLayer fg = null;
	CCTMXLayer mineLayer = null;
	CCTMXLayer itemLayer = null;
	CCTMXLayer flagLayer = null;
	
	CGSize mapSize = null;
	CGSize tileSize = null;
	
	ArrayList<MineCell> cells  = null;
	ArrayList<MineCell> sphereBaseCells = null;
	
	private static GameMinimap gameMinimap;
	
	public static synchronized GameMinimap getInstance() {
		if (gameMinimap == null) {
			gameMinimap = new GameMinimap();
		}
		return gameMinimap;
	}
	
	public void dealloc() {
		gameMinimap = null;
		
	}
	
	private GameMinimap() {
//		if (!GameConfig.share().isMinimapPanelOn() && !GameConfig.share().isEmoticonPanelOn()) {
			this.layout();
//			Config.getInstance().setDisableButton(true);
//		}
	}

	public void layout() {
		CGSize winSize = CCDirector.sharedDirector().winSize();
		GameConfig.share().setMinimapPanelOn(true);
		Utility.getInstance().dimScreen(this);

		//
		// 베이스
		CCSprite base = CCSprite.sprite(hudLayerFolder + "minimap-base-hd.png");
		this.addChild(base);
		base.setPosition(winSize.width/2, winSize.height/2);
		base.setTag(kTagMinimap);
		
		//
		// 닫기버튼
		CCMenuItem itemExit = CCMenuItemImage.item(
				hudLayerFolder + "minimap-buttonExit-normal-hd.png", 
				hudLayerFolder + "minimap-buttonExit-select-hd.png", 
				this, "clicked");
		itemExit.setTag(kButtonExit);
		CCMenu buttonExit = CCMenu.menu(itemExit);
		base.addChild(buttonExit);
		buttonExit.setPosition(
				base.getContentSize().width - itemExit.getContentSize().width, 
				base.getContentSize().height - itemExit.getContentSize().height * 0.7f);
		
		//
		// 미니맵
		
		// 타일맵 로드
		this.tileMap = CCTMXTiledMap.tiledMap(GameData.share().gameMap);
		//this.tileMap = new Game().getTileMap();
		
		// 타일맵 레이어 등록
		this.itemLayer = this.tileMap.layerNamed("ItemLayer");			 // 지뢰 및 아이템, 깃발 가져오는 레이어
		this.bg = this.tileMap.layerNamed("Background");					 // Layer Name in Tiled
		this.mineLayer = this.tileMap.layerNamed("MineLayer");		 // 지뢰(호박) 및 아이템 뿌릴 레이어
		this.fg = this.tileMap.layerNamed("Foreground");					 // 잔디
		this.flagLayer = this.tileMap.layerNamed("FlagLayer");			 // 깃발(버섯) 꽂을 레이어
		this.meta = this.tileMap.layerNamed("Meta");							 // 선택 불가 영역
		this.meta.setVisible(false);
		
		// 맵 올리고 기본 크기 지정, 프레임 뒤로 올린다.
		base.addChild(this.tileMap, -1);
//		this.setVisible(false);
		this.tileMap.setScale(0.167f);
		this.tileMap.setScale(this.tileMap.getScale() * 128 / tileMap.getTileSize().width);
		this.tileMap.setAnchorPoint(0.5f, 0.5f);
		this.tileMap.setPosition(
				base.getContentSize().width / 2, 
				base.getContentSize().height / 2 - itemExit.getContentSize().height / 2);
		
		// 64 pixel
		tileSize = CGSize.make(tileMap.getTileSize().width, tileMap.getTileSize().height);
		mapSize = CGSize.make(tileMap.getMapSize().width * tileSize.width, tileMap.getMapSize().height * tileSize.height);
		 
		// 전체 타일(셀) 등록
		cells = new ArrayList<MineCell>();
		sphereBaseCells = new ArrayList<MineCell>();
		int count = 0;
		
		 for (int x = 0; x < (int)tileMap.getMapSize().width; x++) {
			 for (int y = 0; y < (int)tileMap.getMapSize().height; y++) {
				 MineCell cell = new MineCell();
		
				 cell.setTileCoord(CGPoint.make(x, y));
				 cell.setCell_ID(count);
				 cells.add(cell);
				count++;
				
			}
		}
		 
	}
	// layout() end
//	
//	public static void minimapOn() {
//		this.
//	}
//	public static void minimapOff() {
//		this.setVisible(false);
//	}
	
	//
	// MineCell Delegate
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
				} else {
					this.getFg().removeTileAt(tileCoord);
				}
	
			} else {
				// 폭탄은 없는데 타일이 안없어지는 에러
				//Log.e("Game / removeTile", "properties - boom empty");
			}
		} else {
			//Log.e("Game / removeTile", "properties - tile empty");
			// 에러 stack over flow error
			this.getFg().removeTileAt(tileCoord);
		}
	}

	public void clicked(Object button) {
	//public void clicked(CCMenuItem button) {
		//button clicked
		Log.e("Minimap close", "clicked");
		
		switch (((CCMenuItem) button).getTag()) {
		case kButtonExit:
			Log.e("Minimap kButtonExit", "clicked");
			GameConfig.share().setMinimapPanelOn(false);
			Config.getInstance().setDisableButton(false);
			Utility.getInstance().undimScreen(this);
			this.setVisible(false);
//			minimapOff();
			//this.removeChildByTag(kTagMinimap, true);
			break;

		default:
			break;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*****************************************************/
	/** 문제지점
	 *  
	 * 애니매이션
	 * 아이템 피해 애니
	 * 
	 * @return
	 */
	public void receivePlayData(byte playType, int data) {
		Log.e("GameMinimap", "receivePlayData / " +playType+","+data );

		final int kPlayDataCellOpen = 0;
		final int kPlayDataMushroomOn = 1;
		final int kPlayDataMushroomOff = 2;
		final int kPlayDataMagicAttack = 3;
		final int kPlayDataMagicDefense = 4;
		final int kPlayDataGameOver = 5;
		final int kPlayDataEmoticon = 6;
		final int kPlayDataMine = 7;
		final int kPlayDataSphere = 8;
		final int kPlayDataSphereTake = 9;
		int cellGID;
		
		int dataLength = 0;
		int count = 0;
		int itemType = data / 10000;
		CGPoint cell = null;
		if (playType < 3 ||  6 <  playType) {
			cell = searchTilePosition(data % 10000);
		}

	
		switch (playType){
		case kPlayDataCellOpen:
			removeTile(data);
			break;
//			
//		case kPlayDataMushroomOn:
//			cellGID = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(1f, 0f)));
//			this.flagLayer.setTileGID(cellGID, searchTilePosition(data));
//			break;
//			
//		case kPlayDataMushroomOff:
//			this.flagLayer.removeTileAt(searchTilePosition(data));
//			break;
//			
		case kPlayDataMagicAttack:
			if (0 < data && data < 4) {
				Game.HudLayer.testText.setString("적의 공격을 받습니다. type : " + data);
			} else if (data < 7) {
				Game.HudLayer.testText.setString("적이 방어 하였습니다. type : " + data);
			} else {
				Game.HudLayer.testText.setString("알수없는 타입의 공격 type : " + data);
			}
			break;
			
		case kPlayDataMagicDefense:
			Game.HudLayer.testText.setString("적이 방어 하였습니다. kPlayDataMagicDefense");
			break;
			
//		case kPlayDataGameOver:
//			try {
//				Game.HudLayer.gameOver();
////				NetworkController.getInstance().sendPlayDataGameOver(111111);
//				NetworkController.getInstance().sendGameOver();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			break;
			
		case kPlayDataEmoticon:
			Game.HudLayer.testText.setString("이모티콘이 왔습니다. type : " + data);
			break;
//			
//		case kPlayDataMine:
//			cellGID = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(0f, 0f)));
//			this.mineLayer.setTileGID(cellGID, searchTilePosition(data));
//			removeTile(data);
//			break;
//			
		case kPlayDataSphere:
			count = 0;
			for (int m = 0; m < 2; m++) { // 세로 방향
				for (int k = 0; k < 2; k++) { // 가로 방향
					CGPoint targetTile = CGPoint.make(cell.x + k, cell.y + m);

					removeTile(targetTile);
					cellGID = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(count, itemType)));
					this.mineLayer.setTileGID(cellGID, targetTile);
					count++;
				}
			}			
			break;
			
		case kPlayDataSphereTake:
			Game.HudLayer.testText.setString("Warning!!!! 적이 아이템 획득");
//			count = 0;
//			if (itemType == 0)
//				itemType = 7;
//			for (int m = 0; m < 2; m++) { // 세로 방향
//				for (int k = 0; k < 2; k++) { // 가로 방향
//					CGPoint targetTile = CGPoint.make(cell.x + k, cell.y + m);
//					this.mineLayer.removeTileAt(targetTile);
//					cellGID = CCFormatter.swapIntToLittleEndian(this.itemLayer.tileGIDAt(CGPoint.make(count, itemType)));
//					this.mineLayer.setTileGID(cellGID, targetTile);
//					count++;
//				}
//			}	
			break;

		default:
			Log.e("GameMinimap", "알수 없는 데이타 타입 : " + playType);
			Game.HudLayer.testText.setString("알수 없는 데이타 타입. playType : " + playType);
			break;
		}
		
	}
	/*****************************************************/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void removeTile(int tileGID) {
		this.getFg().removeTileAt(searchTilePosition(tileGID));
	}
	
	public void removeTile2(CGPoint tilePoint) {
		this.getFg().removeTileAt(tilePoint);
	}
	
	public CGPoint searchTilePosition(int tileGID) {
		return cells.get(tileGID).getTileCoord();
		}

	public CCTMXTiledMap getTileMap() {
		return tileMap;
	}

	public void setTileMap(CCTMXTiledMap tileMap) {
		this.tileMap = tileMap;
	}

	public CCTMXLayer getBg() {
		return bg;
	}

	public void setBg(CCTMXLayer bg) {
		this.bg = bg;
	}

	public CCTMXLayer getMeta() {
		return meta;
	}

	public void setMeta(CCTMXLayer meta) {
		this.meta = meta;
	}

	public CCTMXLayer getFg() {
		return fg;
	}

	public void setFg(CCTMXLayer fg) {
		this.fg = fg;
	}

	public CCTMXLayer getMineLayer() {
		return mineLayer;
	}

	public void setMineLayer(CCTMXLayer mineLayer) {
		this.mineLayer = mineLayer;
	}

	public CCTMXLayer getItemLayer() {
		return itemLayer;
	}

	public void setItemLayer(CCTMXLayer itemLayer) {
		this.itemLayer = itemLayer;
	}

	public CCTMXLayer getFlagLayer() {
		return flagLayer;
	}

	public void setFlagLayer(CCTMXLayer flagLayer) {
		this.flagLayer = flagLayer;
	}
}
