package com.aga.mine.pages2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.CCFormatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;

// for문 돌리면 서 size 또는 length로 얻어온 객체 있으면 별도로 size 변수 만들어서 그값으로 할것
// 잘못하면 큰일남 ㅇㅇ
//그냥 이해 안되도 적당히 포트중... 나중에 안돌아갈것같음.
//너무 심각해서 어디가 문제인지 못찾을까봐 심히 걱정됨
//this.delegate.removeTile(this.tileCoord);
public class MineCell extends CCLayer{
	
	volatile public static int mCount;
	

	// 지뢰
	private boolean isMine;
	// 열어본 맵의 Tile
	private boolean isOpened;
//	private boolean isMarked = false;
	// 지뢰가 있을것 같은 곳 체크
	private boolean isMarked = false;
	// 구슬
	private boolean isSphere;
	// 선택 못하는 영역
	private boolean isCollidable;
	
	private int Cell_ID;
	private int sphereCell_ID;

	private  CGPoint tilePosition;
	private  boolean isSphereBasePossible;
	private  int sphereType;
	
	private CGPoint tileCoord;
	//int plusMine;
	
	Context mContext;

	
	ArrayList<MineCell> sphereCells;
	ArrayList<MineCell> roundCells  = new ArrayList<MineCell>();
	ArrayList<MineCell> sphereRoundCells;
	
	public int numberOfArroundMine; //자기 주변의 지뢰 숫자
	
	//
	//	MineCell(Context context) {
	MineCell() {
		//mContext = context;
		//delegate = new MineCellDelegate(context);
		//roundCells = new ArrayList<MineCell>();
		isMine = false;
		isOpened = false;
		//isMarked = false;
		isSphere = false;
		isCollidable = false;
		sphereCells =new ArrayList<MineCell>();
		sphereRoundCells = new ArrayList<MineCell>();	
		isSphereBasePossible = true;
		sphereType = -1; // none
		

	}
	
	private Game mGame;
	public MineCell(Game game) {
		this();
		mGame = game;
	}
	
	public void addRoundCell(MineCell cell) {
		roundCells.add(cell);
	}
	
	public void addSphereCell(MineCell cell) {
		sphereCells.add(cell);
	}
	
	public void addSphereRoundCell(MineCell cell) {
		sphereRoundCells.add(cell);
	}
	
	public ArrayList<MineCell> getRoundCells() {
		return roundCells;
	}
	
	public ArrayList<MineCell> getSphereCells() {
		return sphereCells;
	}
	
	public ArrayList<MineCell> getSphereRoundCells() {
		return sphereRoundCells;
	}

	// 선택된 지점의 주변 셀의 마크?? 된것의 갯수를 검사하여 검사된 값을 돌려준다.(Flag 지뢰 체크)
	public int getNumberOfMushroomAndPumpkinAround() {
		//Log.e("지뢰 주변 숫자","" + isMine());
		// 호박은 -1을 넣었다.
		if (isMarked()) return -1;
		
		int count = 0;
		/*
		if (getRoundCells() == null) {
			Log.e("MineCell / getNumberOfMushroomAround","주변에 셀이 없음. / getRoundCells() == null");
		} else if(getRoundCells().size() == 0){
			Log.e("MineCell / getNumberOfMushroomAround","주변에 셀의 갯수가 0개 / getRoundCells().size() == 0");
		}
		*/
		if (getRoundCells() != null && getRoundCells().size() != 0) {
			ArrayList<MineCell> cellArray = getRoundCells();
			int size = getRoundCells().size();
			for (int k = 0; k < size; k++) {

				// cellsTemp.get(k) 이 null이라 에러 발생
				// cellArray.get(k)이 null이 나오면 roundcell로 등록시 문제가 있는것
				if (cellArray.get(k) != null && (
						(cellArray.get(k).isMarked() || (cellArray.get(k).isMine() && cellArray.get(k).isOpened())
								))) 
					count++;
			}	
		}
		return count;
	}
	
	// 숫자 주변의 마인 수 (ㄷㄷㄷ)
	//ArrayList는 쓰레드 세이프하지 않기 때문에 동기화문제로 타일 오픈시 indexOutOfBound 초래함.
	//또한 계산할때 부하도 많기 때문에 Game 생성자에서 미리 계산하여 numberOfArroundMine 변수에 저장하여 사용한다.
	public int getNumberOfMineAround() {
		//Log.e("지뢰 주변 숫자","" + isMine());
		if (isMine()) return -1;

		int count = 0;
		for (MineCell cell : getRoundCells()) {
			if (cell != null && cell.isMine())
				count++;
		}

		return count;
	}

	public int getNumberOfSphereAround() {
		if (isMine()) return -1;

		int count = 0;
		for (MineCell cell : getRoundCells()) {
//			if (cell.isSphere())
			if (cell.isSphere() || cell.isOpened() || cell.isMine())
				count++;
		}
		return count;
	}

	public boolean isSphereCellsClear() {
		for (MineCell mineCell : getSphereCells()) {
			if (mineCell != null) { // null 값이 나와서는 안된다. (임시)
				if (!mineCell.isMine() && !mineCell.isOpened() && !mineCell.isCollidable() && !mineCell.isSphere()) {
					// return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
		
	/*
		if (getSphereCells() != null && getSphereCells().size() != 0) {
			ArrayList<MineCell> cellArray = getSphereCells();
			int size = cellArray.size();
			for (int k = 0; k < size; k++) {
				if (cellArray.get(k) != null) { // null 값이 나와서는 안된다. 
					if (!cellArray.get(k).isMine() && !cellArray.get(k).isOpened() && !cellArray.get(k).isCollidable() && !cellArray.get(k).isSphere()) {
						//	return true;
						} else {
							// 하나라도 true이면 false 반환
							return false;
					}
//				} else {
		//			return false;
	//			}
			}
			//getSphereCells 이 null이거나 데이터가 없으면 어떻게 해야되지???
			//Log.e("MineCell isSphereCellsClear", "null or size 0");
			//return true; // 임시
		}
		return true;
	}*/
	
	public void setToSphereCells(int sphereType) {
		int counter = 1;
		if (getSphereRoundCells() != null && getSphereRoundCells().size() != 0) {
			ArrayList<MineCell> cells = getSphereRoundCells();
			//Log.e("setToSphereCells ", " " + cells);
			int size = cells.size();
			for (int i = 0; i < size; i++) {
				// 없을시 그 값만 패스~
				if (cells.get(i) != null) {
				cells.get(i).setSphere(true); // 툭하면 에러나는 부분
				cells.get(i).setSphereCell_ID(sphereType * 10 + counter); // 1*10+1,1*10+2,1*10+3,1*10+4
				}
			}
		}
	}
	
	// sdfsdf
	public int roundOpen() {
		int numberOfMine = this.numberOfArroundMine;
		int getNumberOfMushroomAround = this.getNumberOfMushroomAndPumpkinAround();
		//Log.e("폭탄 노출됨", "" + numberOfMine + "," + plusMine + ",");
/*		int boom = 0;
		int mushroomSum = 0;*/
		if (numberOfMine == getNumberOfMushroomAround) {
			// 더블탭을 했을때 더블 탭한곳의 숫자가 있으면 탐??
			ArrayList<MineCell> cells = getRoundCells();
			int size = cells.size();
			for (int i = 0; i < size; i++) {
				if (!cells.get(i).isOpened() && !cells.get(i).isMarked()) {
					cells.get(i).open();
				}
		}

/*			//Log.e("mineCell For", "" + cells.get(i));
			
			if(cells.get(i).isOpened()) {
				// 오픈 한 셀을 누를때 셀주변에 노출된 지뢰가 있으면 그 지뢰 만큼 + 해준다.
				if(cells.get(i).getNumberOfMineAround() == -1) {
					boom ++;
				}
				//Log.e("폭탄 노출됨 ", " ");
			}
			mushroomSum = getNumberOfMushroomAround + boom;
			//Log.e("getNumber", mushroomSum + ", boom :" + boom);
			if (numberOfMine == getNumberOfMushroomAround) {
				cells.get(i).open();
			} else if (numberOfMine == mushroomSum) {
				//Log.e("왜 안타지 ", "?????????????????????????");
				cells.get(i).open();
			}
			Log.e("MineCell / roundOpen", "폭탄 노출됨");
			
			*/
		}
		return numberOfMine;
	}
	
	public int open() {
		return open (1);
	}
	
	public int open(int depth) {
		// 이미 오픈되었거나 깃발꽂은 셀이면 그냥 빠져 나온다.
		if(isOpened() || isMarked())
			return numberOfArroundMine;
		
		// 오픈으로 데이터 설정
		setOpened(true);
		// TMX에서 현재 타일 제거
		removeTile(this.tileCoord, depth);
		
		if (GameData.share().isMultiGame) {
			try {
				NetworkController.getInstance().sendPlayDataCellOpen(getCell_ID());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		// 아래 if문에서 타일오픈 소리 또는 호박터지는 소리가 결정 되야 합니다.
		// showAroundMine(); or touchMine()
		
		// 지뢰는 -1로 지정했음
		int pumpkinMine = -1;
		if (numberOfArroundMine > pumpkinMine) { 
			startOpenTile(depth);
			// 프로그레스 하나 증가			
			mGame.mHud.updateProgress();
			
			// 현재셀 주변에 지뢰나 수정구가 없다면 , 주변 셀을 모두 연다.
			// 지뢰가 없는 곳만 열기 또는 지뢰와 아이템이 없는 곳만 열기
			//int numberOfSphere = this.getNumberOfSphereAround();
			//Log.e("MineCell", "open _ getNumberOfSphereAround : " + numberOfSphere);
			// 지뢰 없는 곳만 열어주기
			if(numberOfArroundMine  == 0){
//			if(numberOfArroundMine  + numberOfSphere == 0){
			// 지뢰 없는 곳과 수정구까지 열어주기
			//if (numberOfMine == 0) {
				//ArrayList 동기화 문제로 죽지 않도록 셀을 카피해서 사용
				ArrayList<MineCell> cells = new ArrayList<MineCell>();
				cells.addAll(getRoundCells());
				
				for (final MineCell cell : cells) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					cell.open(++depth);
				}
			}
		} else { // 지뢰 밟음
			touchMine();
		}
		
		Log.e("MineCell", "markedMine : " + GameData.share().getCurrentMine());
		Log.e("MineCell", "getClosedCell : " + mGame.getClosedCell());
		Log.e("MineCell", "getMineNumber() : " + GameData.share().getMineNumber());
		
//		int foundMines = GameData.share().getCurrentMine();
		int closedTiles = mGame.getClosedCell();
		if (closedTiles <= GameData.share().getMineNumber()) {
			Log.e("MineCell", "new gameover : score 1000");
			if (GameData.share().isMultiGame) {
				gameOverType = continueGame;
				sendRequestGameOver(mGame.sumScore());
			} else {
				gameOverType = singleCompleted;
				gameOver(mGame.sumScore());
			}
		}
		
		return numberOfArroundMine;
	}
	
	public void removeTile(CGPoint tileCoord, int depth) {
		if(depth == 1) {
			mCount = 1;
		} else {
			++mCount;
		}

		// Global ID // Globally unique IDentifier
		int tileGid = mGame.tmxMeta.tileGIDAt(tileCoord);
		tileGid = CCFormatter.swapIntToLittleEndian(tileGid);
		// 0 : 일시 타일값 없음
		
		//메타레이어가 isCollidable, preOpened 일경우는 무시. isDontSetMine인 경우는 타일을 벗겨준다
		if (tileGid > 0) {
			HashMap<String, String> properties = mGame.tileMap.propertiesForGID(tileGid);

			// Log.e("Game / removeTile", "properties:" + properties);
			if (properties != null && properties.size() != 0) {
				String isDontSetMine = properties.get("isDontSetMine");
				
				if (isDontSetMine != null && isDontSetMine.equals("YES")) {
					//SoundEngine.sharedEngine().playEffect(mContext, R.raw.landopen_01); // pickup // 뭐에 쓰는 거지?
					mGame.getFg().removeTileAt(tileCoord);
					GameData.share().addOpenedCell(); // 오픈된 셀 수량 누적
					mGame.removeCell(); // 남은 총 cell 수량 감소
				}

			} 
		} else {
			mGame.getFg().removeTileAt(tileCoord);
			GameData.share().addOpenedCell(); // 오픈된 셀 수량 누적
			mGame.removeCell(); // 남은 총 cell 수량 감소
		}
	}
	
	private void startOpenTile(int depth) {
		Log.w("LDK", "showAroundMine:"  + ", mcount:" + mCount);
		if(depth == 1) {
			SoundEngine.sharedEngine().playEffect(mContext, R.raw.landopen_01);
			Log.e("LDK", "mcount:" + mCount);
		} else {
			if(mCount%2 == 0) {
				int effect = mCount/2;
				if ((effect/17)%2 == 0) {
					effect = effect%17;
				} else {
					effect = 16 - effect%17;
				}
				SoundEngine.sharedEngine().playEffect(mContext, R.raw.landopen_01 + effect);
				Log.e("LDK", "effect:" + effect + ", mcount:" + mCount);
			}
		}
		
		//타일 오픈 애니메이션
		CCSprite tile = CCSprite.sprite(mGame.mBitmap, "01");
		mGame.addChild(tile, 5);
		tile.setPosition(CGPoint.ccp(tileCoord.x * mGame.tileSize.width + mGame.tileSize.width / 2, 
				 mGame.mapSize.height - (tileCoord.y *  mGame.tileSize.height +  mGame.tileSize.height / 2)));
		
		tile.runAction(CCSequence.actions(mGame.mScaleAction, mGame.mOpenAction, CCCallFuncND.action(this, "removeTileAni", tileCoord)));
	}
	
	public void removeTileAni(Object sender, Object coord) {
		CCSprite obj = (CCSprite)sender;
		obj.removeFromParentAndCleanup(false);
		
		if(numberOfArroundMine>0) {
			displayMineNumber(numberOfArroundMine, tilePosition, Cell_ID);
			Log.e("MineCell / open", "주변 마인 갯수 : " + numberOfArroundMine);
		}
	}
	
	// 주변 지뢰 수
	public void displayMineNumber(int numberOfMine, CGPoint position, int tag) {
		if (mGame.isCollidable(mGame.tileCoordForPosition(position))) {
			return;
		}
		// 글자 크기 기존의 2/3로 감소(사유 : 수정구 바닦에 숫자가 겹쳐야 되서 수정구에 의해 숫자가 가려짐. - 박정렬 팀장님)
		CCLabel label = CCLabel.makeLabel("" + numberOfMine + " ",
				"Arial-Bold", (int) ((70 * (2 / 3.0) * mGame.tileSize.width) / 128));
		mGame.addChild(label, 5, tag);
		label.setAnchorPoint(0.5f, 0.5f);
		label.setPosition(position);
		label.setColor(ccColor3B.ccc3((int) (75 / 255f), (int) (51 / 255f),
				(int) (9 / 255f)));
	}
	
	private void touchMine() {
		//호박폭탄 애니메이션
		startPumpkinBomb();
		
		// 주변 지뢰수를 숫자로 표현했으며, 주변에 지뢰가 없는 땅은 0으로 표현
		//Log.e("MineCell / open", "plusMine : " + plusMine);
		
		// 지뢰로 표시
		this.setMine(true);
		
		// 땅오픈
		this.setOpened(true);
		
		// 지뢰를 누르면 지뢰갯수 (화면좌측위 지뢰 숫자) 하나를 없애준다.
		int mineNumber = mGame.decreaseMineNumber();
		
		// 화면에 지뢰 갯수 갱신
		mGame.mHud.updateMineNumber(mineNumber);
					
//		try {
//			mGame.mHud.showMessage(this.getCell_ID());
//			if (GameData.share().isMultiGame) {
//				NetworkController.getInstance().sendPlayDataMine(this.getCell_ID()); // 사용 안함
//			}				
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// 지뢰를 누르면 실제 지뢰갯수(currentMineNumber)에 +1를 해줌.
		// +1을 안해주면~  안알랴줌 (노출지뢰가 실제 지뢰에 포함이 안됨)
		//int currentMine = GameData.share().currentMineNumber() + 1;
		GameData.share().markMine();
		int currentMine = GameData.share().getCurrentMine(); // 로그용.
		Log.e("MineCell / open", "최대 지뢰 수 : " + GameData.share().getMineNumber());
		Log.e("MineCell / open", "open3 : currentMineNumber" + currentMine + ", decreaseMineNumber : " + mineNumber);

		//최대 지뢰 갯수와 currentMine의 수가 같으면 게임 종료 <<---- 밖으로 빼야 할 것 같음. 수상함. 좀 더 테스트
		Log.e("MineCell / open", "currentMine : " + currentMine + ", MaxMineNumber : " +  GameData.share().getMineNumber());
		
		if (GameData.share().getCurrentMine() == GameData.share().getMineNumber()) {
			Log.e("MineCell / open", "delegate - gameOver *** mission complete ***");
//			this.delegate.gameOver();
			
//			float openedCell = GameData.share().getOpenedCell();
////			float foundMine = mGame.getFoundMine();
//			float mine = GameData.share().getCurrentMine();
//			Log.e("MineCell", "markedMine() 값이 의문스러움. 아이폰에 물어볼 것 : " + mine);
//			Log.e("MineCell", "Game.java에도 같은 값 존재");
//			float mushroom = mGame.getMineNumber(); // 잘못된 데이터
//			float heart = GameData.share().getHeartNumber();
//			float remainTime = GameData.share().getSeconds();
//			
//			int myScore = 0;

			int myScore = 0;
			
			float openedCell = GameData.share().getOpenedCell();
//			float foundMine = mGame.getFoundMine();
			float foundMine = GameData.share().getCurrentMine(); // 올바르게 버섯이 심겨진 지뢰만(찾은 호박)
			float maxMine = GameData.share().getMineNumber(); // 테스트중
			float heart = GameData.share().getHeartNumber();
			float spentTime = 900 - GameData.share().getSeconds(); // 소요 시간

			if (heart > 0) {
				myScore = (int) ((((foundMine + heart) * maxMine) + spentTime) * maxMine * 0.006f);
			}
			
			Log.e("MineCell", "myScore : " + myScore + ", openedCell : " + openedCell + ", foundMine : " + foundMine + ", maxMine : " + maxMine + ", heart : " + heart + ", spentTime : " + spentTime);
			
			if (GameData.share().isMultiGame) {
				gameOverType = continueGame;
				sendRequestGameOver(myScore);
			} else if (GameData.share().isGuestMode) {
				gameOverType = singleCompleted;
				gameOver(myScore);
			}
			
		}
		
		// 지뢰를 누르면 생명 하나 감소 시킨다.(현재셀은 이미 오픈되었음)
		// 지뢰를 밟을시 생명 감소 (-1)
		GameData.share().decreaseHeartNumber();
		
		// 생명수를 업데이트 시킨다.
		Log.e("MineCell / open", "updateHeart : " + updateHeart());
		updateHeart();

		// 생명수 다없어지면 게임오버
		//if (true) {
		if (GameData.share().isHeartOut()) {
			Log.e("MineCell / open", "delegate - gameOver *** mission failed ***");
			if (GameData.share().isMultiGame) {
				sendRequestGameOver(0); // 대전이므로 서버로 내점수 0점 보내기
			} else {
				gameOverType = singleFailed;
				gameOver(-1); // 대전이 아니므로 서버로 점수 안보내기
			}
			
		}
	}
	
	public void startPumpkinBomb() {
		CCSprite bomb = CCSprite.sprite("60game/pumpkinbomb_01.png");
		//붙이는 위치, 크기 조정해야 함.
		bomb.setPosition(tilePosition);
		mGame.addChild(bomb, 100);
		
		CCCallFuncN remove = CCCallFuncN.action(this, "cbRemovePumpkinBomb");
		bomb.runAction(CCSequence.actions(mGame.mPumpkinBomb, remove));
		
		SoundEngine.sharedEngine().playEffect(mContext, R.raw.pumpkin);
	}
	
	public void cbRemovePumpkinBomb(Object sender) {
		CCSprite sprite = (CCSprite)sender;
		sprite.removeFromParentAndCleanup(true);
	}
	
	public boolean updateHeart() {
		return mGame.mHud.updateHeart();
	}
	
	/*****************************************************/
	
	// 대전했을시 게임오버 점수
	private void sendRequestGameOver(int myScore) {
		Log.e("MineCell", "myScore : " + myScore);
//		String str = String.valueOf(point);
//		int pp 
		try {
			NetworkController.getInstance().sendRequestGameOver(myScore);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	int gameOverType = 0; //
	final int singleFailed = 1;
	final int singleCompleted = 2;
	final int continueGame = 3;
	
	// 싱글 및 게스트일때 게임오버 점수
	private void gameOver(int myScore) {
		Log.e("MineCell", "myScore : " + myScore);
		if (gameOverType == singleFailed) {
			Log.e("MineCell", "싱글 - 생명 전부 소모로 종료 (type) : " + gameOverType);
			Config.getInstance().setVs(Config.getInstance().vsLose);
			mGame.mHud.gameOver(myScore, 0);
		} else if (gameOverType == singleCompleted){
			Log.e("MineCell", "싱글 - 전부 찾고 종료 (type) : " + gameOverType);
			Config.getInstance().setVs(Config.getInstance().vsWin);
			myScore = (int) (myScore*0.2);
			mGame.mHud.gameOver(myScore, 0);
		} else if (gameOverType == continueGame){
			Log.e("MineCell", "멀티 - 계속 하기 (type) : " + gameOverType);
		}
	}

	
	public int getSphereItem() {
		
		for (MineCell cell : getSphereCells()) {
			if(!cell.isOpened()) return -1;
		}
		
		for (MineCell cell : getSphereRoundCells()) {
				if (cell.isOpened() || cell.isMarked())
					continue;
				else
					return -1;
		}
		
		/*
		if (getSphereCells() != null && getSphereCells().size() > 0) {
			ArrayList<MineCell> cellArray = getSphereCells();
			int size2 = getSphereCells().size();
			for (int k = 0; k < size2; k++) {
				if(!cellArray.get(k).isOpened())
					return -1;
			}
		}
		
		if (getSphereRoundCells() != null && getSphereRoundCells().size() > 0) {
			ArrayList<MineCell> cellArray = getSphereRoundCells();
			int size = getSphereRoundCells().size();
			for (int k = 0; k < size; k++) {
				if (cellArray.get(k).isOpened() || cellArray.get(k).isOpened())
					continue;
				else
					return -1;
			}
		}
		*/
		return getSphereType();
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isOpened() {
		return isOpened;
	}

	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	public boolean isMarked() {
		return isMarked;
	}

	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}

	public boolean isCollidable() {
		return isCollidable;
	}

	public void setCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}

	public int getCell_ID() {
		return Cell_ID;
	}

	public void setCell_ID(int cell_ID) {
		this.Cell_ID = cell_ID;
	}

	public CGPoint getTilePosition() {
		return tilePosition;
	}

	public void setTilePosition(CGPoint tilePosition) {
		this.tilePosition = tilePosition;
	}

	public boolean isSphere() {
		return isSphere;
	}

	public void setSphere(boolean isSphere) {
		this.isSphere = isSphere;
	}

	public int getSphereCell_ID() {
		return sphereCell_ID;
	}

	public void setSphereCell_ID(int sphereCell_ID) {
		this.sphereCell_ID = sphereCell_ID;
	}

	public boolean isSphereBasePossible() {
		return isSphereBasePossible;
	}

	public void setSphereBasePossible(boolean isSphereBasePossible) {
		this.isSphereBasePossible = isSphereBasePossible;
	}

	public int getSphereType() {
		return sphereType;
	}

	public void setSphereType(int sphereType) {
		this.sphereType = sphereType;
	}

	public CGPoint getTileCoord() {
		return tileCoord;
	}

	public void setTileCoord(CGPoint tileCoord) {
		this.tileCoord = tileCoord;
	}
	
	
	/*		
	//unsigned
	private int unsigned2(int myByte) {
		System.out.println(unsigned32(-1) );   // 4294967295
		return 0;
	}
	
	private int unsigned(int myByte) {
		int val = myByte < 0 ? ( Byte.MAX_VALUE + 1 ) * 2 + myByte : myByte;
		return val;
	}
	*/
	
}
