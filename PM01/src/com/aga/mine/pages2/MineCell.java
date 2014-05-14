﻿package com.aga.mine.pages2;

import java.io.IOException;
import java.util.ArrayList;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.types.CGPoint;

import android.content.Context;
import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.NetworkController;

// for문 돌리면 서 size 또는 length로 얻어온 객체 있으면 별도로 size 변수 만들어서 그값으로 할것
// 잘못하면 큰일남 ㅇㅇ
//그냥 이해 안되도 적당히 포트중... 나중에 안돌아갈것같음.
//너무 심각해서 어디가 문제인지 못찾을까봐 심히 걱정됨
//this.delegate.removeTile(this.tileCoord);
public class MineCell extends CCLayer{
	
	// 잘 될려낭????
	MineCellDelegate delegate ;
	
	//class MineCellDelegate{
	public interface MineCellDelegate{
		boolean updateHeart();
		void displayMineNumber(int a, CGPoint b, int c);
		void removeTile(CGPoint tileCoord, int depth);
		void gameOver();
		
		//public void removeTile(CGPoint tileCoord) {}
		//public void displayMineNumber(int numberOfMine, CGPoint position, int tag) {}
		//public void gameOver() {}
		//public void updateHeart() {}
/*	class MineCellDelegate extends Game{
		MineCellDelegate() {
			super();
		}
*/			
	//	MineCellDelegate(Context context) {
	//		super(context);
	//	}	
	}
	
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
			if (cell.isSphere())
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
	
	//unsigned 문제
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
	
	/*****************************************************/
	/** 문제지점 
	 * 
	 * Thread
	 * 
	 * 타일open시 UI 멈춤 
	 * 많은 타일open후에도 UI 성능 저하
	 * 
	 * 
	 * 애니매이션
	 * 수정구 획득 & 타일 오픈 애니
	 * 
	 * 
	 * @return
	 */
	
	public int open() {
		return open (1);
	}
	
	public int open(int depth) {
		// 셀주변의 지뢰 갯수를 구한다.
		//int numberOfMine = this.numberOfArroundMine;
		// numberOfMine : 타일에 적히는 숫자
		//Log.e("MineCell / open", "numberOfMine: " + numberOfMine);
		/*
		//지뢰면
		if (numberOfMine == -1) {
			Log.e("MineCell / open", "지뢰 밟음 ㅠㅠ 생명력 감소");
		}
		*/
		//
		// 이미 오픈되었거나 깃발꽂은 셀이면 그냥 빠져 나온다.
		if(isOpened() || isMarked())
			return numberOfArroundMine;
		
		// 오픈으로 데이터 설정
		this.setOpened(true);
		GameData.share().addOpenedCell();
		
		//
		// TMX에서 현재 타일 제거 및 총 열리지 않은 총 타일수량 1개 감소
		this.delegate.removeTile(this.tileCoord, depth);
		mGame.removeCell();
		
		if (GameData.share().isMultiGame) {
			try {
				NetworkController.getInstance().sendPlayDataCellOpen(getCell_ID());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		// 지뢰는 -1로 지정했음
		int pumpkinMine = -1;
		if (numberOfArroundMine > pumpkinMine) { // 주변의 지뢰 수량을 표시
			if (numberOfArroundMine != 0)
				showAroundMine();
			// 프로그레스 하나 증가			
			mGame.mHud.updateProgress();
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
				sendRequestGameOver(1000);
			} else {
				gameOverType = singleCompleted;
				gameOver(1000);
			}
		}
//		if (numberOfArroundMine > 0) { // 주변의 지뢰 수량을 표시
//			showAroundMine();
//		} else if (numberOfArroundMine == pumpkinMine) { // 지뢰 밟음
//			touchMine();
//		}
		
		//Log.e("MineCell / open", "쭉~~~~~~~~~~~~~~~~~~");
		//
		// 현재셀 주변에 지뢰나 수정구가 없다면 , 주변 셀을 모두 연다.
		// 지뢰가 없는 곳만 열기 또는 지뢰와 아이템이 없는 곳만 열기
		int numberOfSphere = this.getNumberOfSphereAround();
		// 지뢰 없는 곳만 열어주기
		if(numberOfArroundMine  + numberOfSphere == 0){
		// 지뢰 없는 곳과 수정구까지 열어주기
		//if (numberOfMine == 0) {
			for (final MineCell cell : getRoundCells()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cell.open(++depth);
			}
		}
		return numberOfArroundMine;
	}
	
	private void showAroundMine() {
		this.delegate.displayMineNumber(numberOfArroundMine, tilePosition, Cell_ID);
		Log.e("MineCell / open", "주변 마인 갯수 : " + numberOfArroundMine);
	}
	
	private void touchMine() {
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
			Log.e("Game", "markedMine() 값이 의문스러움. 아이폰에 물어볼 것 : " + foundMine);
			Log.e("Game", "MineCell.java에도 같은 값 존재");
			float maxMine = GameData.share().getMineNumber(); // 테스트중
			float heart = GameData.share().getHeartNumber();
			float remainTime = GameData.share().getSeconds(); // 소요 시간

			if (heart > 0) {
				myScore = (int) ((((foundMine + heart) * maxMine) + remainTime) * maxMine * 0.006f);
			}
			
			Log.e("MineCell", "myScore : " + myScore + ", openedCell : " + openedCell + ", foundMine : " + foundMine + ", maxMine : " + maxMine + ", heart : " + heart + ", remainTime : " + remainTime);
			
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
		Log.e("MineCell / open", "updateHeart : " + this.delegate.updateHeart());
		this.delegate.updateHeart();

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
				if (cell.isOpened() || cell.isOpened())
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
