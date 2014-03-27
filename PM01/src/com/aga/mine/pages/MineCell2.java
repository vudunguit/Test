package com.aga.mine.pages;

import java.io.IOException;
import java.util.ArrayList;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;

import android.content.Context;
import android.util.Log;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;

// for�� ������ �� size �Ǵ� length�� ���� ��ü ������ ������ size ���� ���� �װ����� �Ұ�
// �߸��ϸ� ū�ϳ� ����
//�׳� ���� �ȵǵ� ������ ��Ʈ��... ���߿� �ȵ��ư��Ͱ���.
//�ʹ� �ɰ��ؼ� ��� �������� ��ã����� ���� ������
//this.delegate.removeTile(this.tileCoord);
public class MineCell2 extends CCLayer{
	
	/************** inner class *****************/
	public interface MineCellDelegate{
		void removeTile(CGPoint tileCoord, boolean isAi, boolean isMine);
		void displayMineNumber(int numberOfMine, CGPoint position, int tag);
		void gameOver(int point, int otherPoint);
		void updateHeart();
		int unOpenedCell();
		int markedMine();
		int difficultyMine();		
		void updateProgressOwner();
		void updateProgressOther();
		void updateMineNumber();
	}
	/***************************************/
	
	// ����
	private boolean isMine;
	// ��� ���� Tile
	private boolean isOpened;
//	private boolean isMarked = false;
	// ���ڰ� ������ ���� �� üũ
	private boolean isMarked = false;
	// ����
	private boolean isSphere;
	// ���� ���ϴ� ����
	private boolean isCollidable;
	
	private int Cell_ID;
	private int sphereCell_ID;

	private  CGPoint tilePosition;
	private  boolean isSphereBasePossible;
	private  int sphereType;
	
	private CGPoint tileCoord;
	//int plusMine;
	
	Context mContext;
	// �� �ɷ���????
	MineCellDelegate delegate ;
	
	ArrayList<MineCell2> sphereCells;
	ArrayList<MineCell2> roundCells  = new ArrayList<MineCell2>();
	ArrayList<MineCell2> sphereRoundCells;
	
	//
	//	MineCell(Context context) {
	MineCell2() {
		//mContext = context;
		//delegate = new MineCellDelegate(context);
		//roundCells = new ArrayList<MineCell>();
		isMine = false;
		isOpened = false;
		//isMarked = false;
		isSphere = false;
		isCollidable = false;
		sphereCells =new ArrayList<MineCell2>();
		sphereRoundCells = new ArrayList<MineCell2>();	
		isSphereBasePossible = true;
		sphereType = -1; // none
	}
	
	public void addRoundCell(MineCell2 cell) {
		roundCells.add(cell);
	}
	
	public void addSphereCell(MineCell2 cell) {
		sphereCells.add(cell);
	}
	
	public void addSphereRoundCell(MineCell2 cell) {
		sphereRoundCells.add(cell);
	}
	
	public ArrayList<MineCell2> getRoundCells() {
		return roundCells;
	}
	
	public ArrayList<MineCell2> getSphereCells() {
		return sphereCells;
	}
	
	public ArrayList<MineCell2> getSphereRoundCells() {
		return sphereRoundCells;
	}

	// ���õ� ������ �ֺ� ���� ��ũ?? �Ȱ��� ������ �˻��Ͽ� �˻�� ���� �����ش�.(Flag ���� üũ)
	public int getNumberOfMushroomAndPumpkinAround() {
		//Log.e("���� �ֺ� ����","" + isMine());
		// ȣ���� -1�� �־���.
		if (isMarked()) return -1;
		
		int count = 0;
		/*
		if (getRoundCells() == null) {
			Log.e("MineCell / getNumberOfMushroomAround","�ֺ��� ���� ����. / getRoundCells() == null");
		} else if(getRoundCells().size() == 0){
			Log.e("MineCell / getNumberOfMushroomAround","�ֺ��� ���� ������ 0�� / getRoundCells().size() == 0");
		}
		*/
		if (getRoundCells() != null && getRoundCells().size() != 0) {
			ArrayList<MineCell2> cellArray = getRoundCells();
			int size = getRoundCells().size();
			for (int k = 0; k < size; k++) {

				// cellsTemp.get(k) �� null�̶� ���� �߻�
				// cellArray.get(k)�� null�� ������ roundcell�� ��Ͻ� ������ �ִ°�
				if (cellArray.get(k) != null && (
						(cellArray.get(k).isMarked() || (cellArray.get(k).isMine() && cellArray.get(k).isOpened())
								))) 
					count++;
			}	
		}
		return count;
	}
	
	// ���� �ֺ��� ���� �� (������)
	public int getNumberOfMineAround() {
		//Log.e("���� �ֺ� ����","" + isMine());
		if (isMine()) return -1;

		int count = 0;
		for (MineCell2 cell : getRoundCells()) {
			if (cell != null && cell.isMine())
				count++;
		}

		return count;
	}

	public int getNumberOfSphereAround() {
		if (isMine()) return -1;

		int count = 0;
		for (MineCell2 cell : getRoundCells()) {
			if (cell.isSphere())
				count++;
		}
		return count;
	}

	public boolean isSphereCellsClear() {
		for (MineCell2 mineCell : getSphereCells()) {
			if (mineCell != null) { // null ���� ���ͼ��� �ȵȴ�. (�ӽ�)
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
				if (cellArray.get(k) != null) { // null ���� ���ͼ��� �ȵȴ�. 
					if (!cellArray.get(k).isMine() && !cellArray.get(k).isOpened() && !cellArray.get(k).isCollidable() && !cellArray.get(k).isSphere()) {
						//	return true;
						} else {
							// �ϳ��� true�̸� false ��ȯ
							return false;
					}
//				} else {
		//			return false;
	//			}
			}
			//getSphereCells �� null�̰ų� �����Ͱ� ������ ��� �ؾߵ���???
			//Log.e("MineCell isSphereCellsClear", "null or size 0");
			//return true; // �ӽ�
		}
		return true;
	}*/
	
	//unsigned ����
	public void setToSphereCells(int sphereType) {
		int counter = 1;
		if (getSphereRoundCells() != null && getSphereRoundCells().size() != 0) {
			ArrayList<MineCell2> cells = getSphereRoundCells();
			//Log.e("setToSphereCells ", " " + cells);
			int size = cells.size();
			for (int i = 0; i < size; i++) {
				// ������ �� ���� �н�~
				if (cells.get(i) != null) {
				cells.get(i).setSphere(true); // ���ϸ� �������� �κ�
				cells.get(i).setSphereCell_ID(sphereType * 10 + counter); // 1*10+1,1*10+2,1*10+3,1*10+4
				}
			}
		}
	}
	
	// sdfsdf
	public int roundOpen() {
		int numberOfMine = this.getNumberOfMineAround();
		int getNumberOfMushroomAround = this.getNumberOfMushroomAndPumpkinAround();
		//Log.e("��ź �����", "" + numberOfMine + "," + plusMine + ",");
/*		int boom = 0;
		int mushroomSum = 0;*/
		if (numberOfMine == getNumberOfMushroomAround) {
			// �������� ������ ���� ���Ѱ��� ���ڰ� ������ Ž??
			ArrayList<MineCell2> cells = getRoundCells();
			int size = cells.size();
			for (int i = 0; i < size; i++) {
				if (!cells.get(i).isOpened() && !cells.get(i).isMarked()) {
					cells.get(i).open();
				}
		}

/*			//Log.e("mineCell For", "" + cells.get(i));
			
			if(cells.get(i).isOpened()) {
				// ���� �� ���� ������ ���ֺ��� ����� ���ڰ� ������ �� ���� ��ŭ + ���ش�.
				if(cells.get(i).getNumberOfMineAround() == -1) {
					boom ++;
				}
				//Log.e("��ź ����� ", " ");
			}
			mushroomSum = getNumberOfMushroomAround + boom;
			//Log.e("getNumber", mushroomSum + ", boom :" + boom);
			if (numberOfMine == getNumberOfMushroomAround) {
				cells.get(i).open();
			} else if (numberOfMine == mushroomSum) {
				//Log.e("�� ��Ÿ�� ", "?????????????????????????");
				cells.get(i).open();
			}
			Log.e("MineCell / roundOpen", "��ź �����");
			
			*/
		}
		return numberOfMine;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*****************************************************/
	/** �������� 
	 * 
	 * Thread
	 * 
	 * Ÿ��open�� UI ���� 
	 * ���� Ÿ��open�Ŀ��� UI ���� ����
	 * 
	 * 
	 * �ִϸ��̼�
	 * ������ ȹ�� & Ÿ�� ���� �ִ�
	 * 
	 * 
	 * @return
	 */
	public int open() {
		
//		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
//			public void run() {
//				
//			}
//		});
		
		
		// ���ֺ��� ���� ������ ���Ѵ�.
		int numberOfMine = this.getNumberOfMineAround();
		// numberOfMine : Ÿ�Ͽ� ������ ����
		//Log.e("MineCell / open", "numberOfMine: " + numberOfMine);
		/*
		//���ڸ�
		if (numberOfMine == -1) {
			Log.e("MineCell / open", "���� ���� �Ф� ����� ����");
		}
		*/
		//
		// �̹� ���µǾ��ų� ��߲��� ���̸� �׳� ���� ���´�.
		if(isOpened() || isMarked()) return numberOfMine;
		this.setOpened(true);
		
		if (GameData.share().isMultiGame) {
			try {
	//			Game.HudLayer.abc(this.getUnSignedCellId());
	//			Log.e("", "this.getUnSignedCellId()" + this.getUnSignedCellId());
				NetworkController.getInstance().sendPlayDataCellOpen(this.getCell_ID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//
		// ���� Ÿ�� ����
		/*** �׳� ���� �ȵǵ� ������ ��Ʈ��... ���߿� �ȵ��ư��Ͱ���. ***/
		/*** �ʹ� �ɰ��ؼ� ��� �������� ��ã����� ���� ������ ***/
		/*** �ϴ� ���� ������ ***/
//		this.delegate.removeTile(this.tileCoord);
		this.delegate.removeTile(this.tileCoord, isCollidable, isCollidable);
		//NetworkController.getInstance().send
		Game.unopenedTile --;
		
		if (numberOfMine > 0) {
			//
			// ������ ��(Ÿ��)�� �ֺ��� ���� ������ ǥ���Ѵ�.
			Log.e("MineCell / open", "�ֺ� ���� ���� : " + numberOfMine);
			this.delegate.displayMineNumber(numberOfMine, tilePosition, Cell_ID);
		}
		
		// ���ڴ� -1�� �������� ���ڰ� ���°Ŵ� 0, �����ִ°� �׼��ڴ��
		else if (numberOfMine == -1){
			//Log.e("MineCell / open", "plusMine : " + plusMine);
			// ���ڷ� ǥ��
			this.setMine(true);
			this.setOpened(true);
//			try {
//				Game.HudLayer.abc(this.getUnSignedCellId());
//				NetworkController.getInstance().sendPlayDataMine(this.getCell_ID());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			
			// ���ڸ� ������ ���ڰ��� (ȭ�������� ���� ����) �ϳ��� �����ش�.
			int mineNumber = GameData.share().decreaseMineNumber();
			
			// ȭ�鿡 ���� ���� ����
			Game.HudLayer.updateMineNumber(mineNumber);
			
			// ���α׷��� �ϳ� ����			
			Game.HudLayer.updateProgress();
			
			// ���ڸ� ������ ���� ���ڰ���(currentMineNumber)�� +1�� ����.
			// +1�� �����ָ�~  �Ⱦ˷��� (�������ڰ� ���� ���ڿ� ������ �ȵ�)
			//int currentMine = GameData.share().currentMineNumber() + 1;
			int currentMine = GameData.share().currentMineNumber();
			Log.e("MineCell / open", "�ִ� ���� �� : " + GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty()));
			Log.e("MineCell / open", "open3 : currentMineNumber" + currentMine + ", decreaseMineNumber : " + mineNumber);

			//�ִ� ���� ������ currentMine�� ���� ������ ���� ����
			Log.e("MineCell / open", "currentMine : " + currentMine + ", MaxMineNumber : " +  GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty()));
			if (currentMine == GameData.share().getMaxMineNumber(GameData.share().getGameDifficulty())) {
			Log.e("MineCell / open", "delegate - gameOver *** mission complete ***");
//				this.delegate.gameOver();
				this.delegate.gameOver(currentMine, currentMine);
			}
			
			// ���ڸ� ������ ���� �ϳ� ���� ��Ų��.(���缿�� �̹� ���µǾ���)
			// ���ڸ� ������ ���� ���� (-1)
			GameData.share().decreaseHeartNumber();
			
			// ������� ������Ʈ ��Ų��.
//			Log.e("MineCell / open", "updateHeart : " + this.delegate.updateHeart());
			this.delegate.updateHeart();

			// ����� �پ������� ���ӿ���
			//if (true) {
			if (GameData.share().isHeartOut()) {
				Log.e("MineCell / open", "delegate - gameOver *** mission failed ***");
//				this.delegate.gameOver();
				this.delegate.gameOver(currentMine, currentMine);
				GameProgressBar progress = new GameProgressBar(CCDirector.sharedDirector().getActivity());
				progress.stopTime();
			}
		}
		
		//Log.e("MineCell / open", "��~~~~~~~~~~~~~~~~~~");
		//
		// ���缿 �ֺ��� ���ڳ� �������� ���ٸ� , �ֺ� ���� ��� ����.
		// ���ڰ� ���� ���� ���� �Ǵ� ���ڿ� �������� ���� ���� ����
		int numberOfSphere = this.getNumberOfSphereAround();
		// ���� ���� ���� �����ֱ�
		if(numberOfMine  + numberOfSphere == 0){
		// ���� ���� ���� ���������� �����ֱ�
		//if (numberOfMine == 0) {
			for (final MineCell2 cell : getRoundCells()) {
//				CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
//					public void run() {
//						android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//						try {
//							Thread.sleep(30);
							cell.open();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//				
//					}
//				});
			}
		}
		return numberOfMine;
	}
	/*****************************************************/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int getSphereItem() {
		
		for (MineCell2 cell : getSphereCells()) {
			if(!cell.isOpened()) return -1;
		}
		
		for (MineCell2 cell : getSphereRoundCells()) {
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
	
	
	/**************************** ���� ****************************/
	public void preOpens(boolean isAi) {
		this.preOpened(0);
		this.open(isAi);
	}

	private void preOpened(int preOpenTileSetting) {
		int preTileOpenCount = this.getNumberOfCellToOpenIsRoundOpen(true) -  preOpenTileSetting;
		if (preTileOpenCount > 0)
			this.openCount(preTileOpenCount);
		Log.e("MineCell2", "preTileOpenCount : " + preTileOpenCount);
	}
	
	private int getNumberOfCellToOpenIsRoundOpen(boolean isRoundOpen) {
		GameConfig.share().resetPreOpenedCells();
		this.preOpen(isRoundOpen);
		int count = GameConfig.share().countOfPreOpenedCells();
		GameConfig.share().restorePreOpenedCells();
		return count;
	}

	private void preOpen(boolean isRoundOpen) {
		if (isOpened() || isMarked())
			return;
		setOpened(true);
		GameConfig.share().addCellToPreOpenedCells(this);
		int target = isRoundOpen ? this.getNumberOfMushroomAround() : 0;
		if (this.getNumberOfMineAround() == target) {
			for (MineCell2 cell : roundCells) {
				cell.preOpen(false);
			}
		}
	}

	private int getNumberOfMushroomAround() {
		if (isMarked())
			return -1;
		int count = 0;
		for (MineCell2 cell : roundCells) {
			if (cell.isMarked() || (cell.isOpened() && cell.isMine())) {
				count ++;
			}
		}
		return count;
	}

	private void openCount(int count) {
		int tileCount = 10;
		boolean isLoop = false;
		int effectCount = R.raw.game_open2; // landOpen_01.mp3�� �����Ұ�
		
		if (count > 1) {
			tileCount += 20;
			effectCount ++;
			isLoop = true;
		} else {
			Log.e("MineCell2", "openCount : " + count + ", effectCount : " + effectCount );
		}
		
		while (isLoop) {
			if (count > tileCount) {
				tileCount += 20;
				effectCount ++;
			} else {
				isLoop = false;
//				if (effectCount < 10)
//					String landOpenSound = "landOpen_" + ".mp3";	
				Log.e("MineCell2", "openCount : " + count + ", effectCount : " + effectCount );
			}
		}
	}
	
	private int open(boolean isAi) {
		// �� �ֺ��� ���� ������ ���Ѵ�.
		int numberOfMine = this.getNumberOfMineAround();
/*		if (numberOfMine == -1)
			Log.e("MineCell2", "open : " + "����?");*/
		// Ÿ���� �����Ҽ� ���� ���
		if (isOpened() || isMarked() || isSphere())
			return numberOfMine;
		
		setOpened(true);
		
		// ���� Ÿ�� ����
//		this.delegate.removeTile(this.tileCoord);
		this.delegate.removeTile(this.tileCoord, isAi, isAi);
		GameConfig.share().setDelayTime(GameConfig.share().getDelayTime());
		
		// ���������� Thread�� ������ �ð����� �����ϴ� �� (�ȵ���̵�� Thread���� ���ؾߵɵ�. )
//		this.performSelector(removeTile, isAi, GameConfig.share().getDelayTime()); // ����?
		
		GameConfig.share().setDelayTime(GameConfig.share().getDelayTime() + 0.03f);
		

		if (numberOfMine > 0 && !isAi) {
			// ������ ���� �ֺ��� ���� ������ ǥ���Ѵ�.
//			this.performSelector(displayMineNumber, numberOfMine, GameConfig.share().getDelayTime() + 0.02f); // ����?
		}
		
		// ���ڴ� -1
		else if(numberOfMine == -1) {
			this.setMine(true);
			this.setOpened(true);
			// ���� ����
			GameData.share().decreaseHeartNumber();
			// ���� �ֱ�
			// ���� ������Ʈ
			this.delegate.updateHeart();
			// ���� ���� ����
			this.delegate.updateMineNumber();
			// ������� 0�� �Ǹ� ���� ����
			if (GameData.share().isHeartOut()) {
				if (Config.getInstance().isGuest() || Config.getInstance().getModeChoise() == kModeSingle ) {
//					this.performSelector(gameover, 0, 1); // ����?
					guestSingleMatchScore = 1; // �̱� �Ǵ� �Խ�Ʈ��� �Ͻ� ���� ��
				} else {
//					this.performSelector(sendRequestGameOver, 0, 0.5f); // ����?
					Log.e("MineCell2", "open : " + "GameOver");
				}
				GameProgressBar progressBar = new GameProgressBar(mContext);
				progressBar.stopTime();
			}
		}
		// ���� �Ǵ� �������� �ƴ� ������ ���� ���̸� �������� count�� +1 ���ش�.
		// count�� ���� Ŭ�������� �޾ƿ� unOpenedCell�� ���ڰ� ������ ���ڰ� �ƴ� ��� ���� ������ ������ �����ϰ� ������ �����Ѵ�.
		// (���¾ȵ� ��ü �� - ������ (4�� ��) * ����) - ���̵��� ���� ��
		if (numberOfMine > -1) {
			int openCell = GameData.share().getOpenedCell(); // ���µ� �� ��
			int pumpkin = this.delegate.difficultyMine(); // ���̵��� ȣ�� ��
			int mine = this.delegate.markedMine(); // ã�� ȣ�� ��
			int heart = GameData.share().getHeart(); // ���� �����
			int time = 900 - GameData.share().getSecond(); // ���� �ð�
//			int heart = GameData.share().getHeartNumber(); // ���� �����
//			int time = 900 - GameData.share().getSeconds(); // �ҿ� �ð�
			
			int myPoint = (int)(((pumpkin + heart) * mine + time) * (pumpkin * 0.006f)); // ��� ���� �Ҹ�ô� ������ ������� �ʴ´�.
			Log.e("MineCell2", "myPoint : " + myPoint);
			
			// ���ӿ���
			if (this.delegate.unOpenedCell() - this.delegate.difficultyMine() == openCell) {
				
				// �̱� or  �Խ�Ʈ
				if (Config.getInstance().isGuest() || Config.getInstance().getModeChoise() == kModeSingle) {
					guestSingleMatchScore = 2; // �̱� �Ǵ� �Խ�Ʈ��� �Ͻ� ���� ��
//					this.performSelector(gameover, myPoint, 1); // ����?
				} else { // ����
					guestSingleMatchScore = 3; // �̱� �Ǵ� �Խ�Ʈ��� �Ͻ� ���� ��
//					this.performSelector(sendRequestGameOver, myPoint, 0.5f); // ����?
				}
				
			}
		}
		
		// ���缿 �ֺ��� ���ڰ� ���ٸ� �ֺ� ��� ���� ����.
		if (numberOfMine == 0) {
			for (MineCell2 cell : roundCells) {
				cell.open(isAi);
			}
		}
		return numberOfMine;
	}
	
	int guestSingleMatchScore = 0;
	final int kModeSingle = 1;
	final int kModeVersus = 2;
	final int kModeInvite = 3;
	
	// �̱� ���� ����� ����
	private void gameOver(int point) {
		if (guestSingleMatchScore == 2) {
			this.delegate.gameOver(point, 0);
			Log.e("MineCell2", "gameSet : " + point);
			return;
		}
		Log.e("MineCell2", "gameOver : " + 0);
		this.delegate.gameOver(0, 0);
	}
	

	// ���� ���� ����� ����
	private void sendRequestGameOver(int point) {
		try {
			NetworkController.getInstance().sendRequestGameOver(point);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
