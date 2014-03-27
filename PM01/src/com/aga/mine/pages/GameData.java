package com.aga.mine.pages;

import java.util.Currency;
import java.util.HashMap;

import org.cocos2d.types.CGPoint;

import android.util.Log;

	// �ϴ� ����� ���� �Ϸ�
public class GameData {

	//
	// ���� ������ �Ϲ� (�����)
	final int kMaxHeartNumber = 3;
	
	// ������
	final int kNumberOfSphere = 3; // �⺻3��

	//
	// ������ Ȯ��
	final int kFireChance = 14;
	final int kWindChance = 21;
	final int kCloudChance = 35;

	final int kDivineChance = 15;
	final int kEarthChance = 9;
	final int kMirrorChance = 6;

	//
	// ���� ���̵�
	final int kGameDifficultyEasy = 0;
	final int kGameDifficultyNormal = 1;
	final int kGameDifficultyHard = 2;

	// ���� ������
	HashMap<String, Integer> data;
	int currentMine;
	
	//
	// ���̵��� ���� ����
	final int kMaxMineNumberEasy    =  30;
	final int kMaxMineNumberNormal  =  60;
	final int kMaxMineNumberHard     = 90;
	
	
	String mapFolder =  "80map/";
	//
	// ���� ��
	//String gameMap = "tileMap.tmx";
	//String gameMap = "tilemap02.tmx";
	//
	String gameMap = mapFolder + "map001.tmx";
	//String gameMap = "map002.tmx";
	//String gameMap = "map003.tmx";
	//"orthogonal-test1.tmx";
	//"orthogonal-test3.tmx";
	//"attendance2.tmx";
	
	public boolean isGuestMode = true;
	public boolean isMultiGame = true;
	//---------------------------------------------------------------//
	
	private static GameData gameData;

	private GameData() {
		this.initGameData();
	}

	// #pragma mark - Singleton Methods
	public static synchronized GameData share() {
		if (gameData == null) {
			gameData = new GameData();
		}
		return gameData;
	}
	
	//---------------------------------------------------------------//

    //
    // ���Ӽ��� �������彬Ʈ�� �о���δ�.
   final int maxMineNumber = getMaxMineNumber(getGameDifficulty());
   
	private void initGameData() {
	
		    
		    //
		    // �ʱⰪ���� �����Ѵ�.
		   this.data = new HashMap<String, Integer>();
		   
		   // value �������� ����ȯ ����ߵ�.
		   data.put("HeartNumber", 3);	 // �����
		   data.put("MineNumber", maxMineNumber);	 // �����ִ� ���ڼ�
		   data.put("Seconds", 900); 		// 15 min, ���� ���� ����ð� // Game���� �ٽ� ���� �־ ���ǹ� �ϴ�.
		   data.put("ItemFire", 0);				// ȹ���� ������, ��	
		   data.put("ItemWind", 0);			// ȹ���� ������, �ٶ�	
		   data.put("ItemCloud", 0);			// ȹ���� ������, ����
		   data.put("ItemDivine", 0);			// ȹ���� ������, �ż�
		   data.put("ItemEarth", 0);			// ȹ���� ������, ����
		   data.put("ItemMirror", 0);			// ȹ���� ������, �ݻ�
		   data.put("GameDifficulty", 1);	// ���� ���̵� // �ӽ÷� 1
		   
		   data.put("GameMode", 1);		// ���� ��� // �ӽ÷� 1
		   
		   currentMine = 0;
	}

	// #pragma mark - data methods
	public int getMaxMineNumber(int gameDifficulty) {
		int value = -1;

		// �׽�Ʈ�� ���� ���ڼ� �ӽ� ����
		switch (gameDifficulty) {
		case kGameDifficultyEasy:
			Log.e("GameData / getMaxMineNumber", "gameDifficulty --> MineNumber : " + 30);
			value = 30; // �⺻ 30��
			break;

		case kGameDifficultyNormal:
			Log.e("GameData / getMaxMineNumber", "gameDifficulty --> MineNumber : " + 60);
			value = 60;
			break;

		case kGameDifficultyHard:
			Log.e("GameData / getMaxMineNumber", "gameDifficulty --> MineNumber : " + 90);
			value = 90;
			break;
		}

		return value;
	}

	//HeartNumber
	//return���� int���� hashmap�� integer�� ����Ǿ� �ڵ�����ȯ�� �ȵɽ� ����ȯ �ʿ�
	public int getHeartNumber() {
		return getGameData("HeartNumber");
	}

	public void setHeartNumber(int number) {
		this.setGameData("HeartNumber", number);
	}

	public void decreaseHeartNumber() {
		int n = this.getHeartNumber() - 1;
		n = n < 0 ? 0 : n;
		this.setHeartNumber(n);
	}

	public boolean isHeartOut() {
		if (this.getHeartNumber() < 1) 
		return true;
		return false;
	}
	
	public int getMineNumber(){
		return getGameData("MineNumber");
	}
	
	public void setMineNumber(int number) {
		this.setGameData("MineNumber", number);
	}
	
	// ����(���)�� �Ⱦ����� ���� �ڸ��� ���ڰ� ������ +1
	public int currentMineNumber() {
		int a = currentMine +=1;
		Log.e("gameData currentMineNumber", "currentMine : " + a);  // log�� Ȯ��
		return a;
	}
	
	public int previousMineNumber() {
		int a = currentMine -=1;
		Log.e("gameData previousMineNumber", "currentMine : " + a);  // log�� Ȯ��
		return a;
	}
	
	public int resetMineNumber() {
		return currentMine = 0 ;
	}

	public int decreaseMineNumber() {
		setMineNumber(getMineNumber() - 1);
		return getMineNumber();
	}
	
	public int increaseMineNumber() {
		setMineNumber(getMineNumber() + 1);
		return getMineNumber();
	}

	public int getSeconds() {
		return getGameData("Seconds");
	}
	
	public void setSeconds(int seconds) {
		seconds = (int)CGPoint.clampf((float)seconds,	0, (float)seconds); //���� ���� �����ΰ�?
		this.setGameData("Seconds", seconds);
	}
	
	public boolean isTimeOut() {
		if (this.getSeconds() <= 0)
			return true;
			return false;
	}

	public boolean resetItem(){
		String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
		String[] keyArray = keyString.split(",", 6);
		for (int i = 0; i < keyArray.length; i++) {
			this.setGameData(keyArray[i], 0);
		}
		return true;
	}
	
	public int getItemNumberByType(int sphereType){
		//
		// sphereType from 1 to 6
		//","�� �����Ͽ� ��Ʈ�� �迭�� �����?
		String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
		String[] keyArray = keyString.split(",",6);
		//Log.e("GameData", "���� ����:" + (sphereType) + ":" + keyArray[sphereType-1]);
		//Log.e("GameData", "getGameData:" + getGameData(keyArray[sphereType-1]));
		return getGameData(keyArray[sphereType-1]);
	}

	public int getGameData(String key) {
		// �ӽ÷�
		//
		return data.get(key);
	}

	public void setItemNumberByType(int sphereType, int number){
	//","�� �����Ͽ� ��Ʈ�� �迭�� �����?	
	String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
	String[] keyArray = keyString.split(",", 6);

	Log.e("Game / setItemNumberByType", "keyArray : "+ keyArray[sphereType-1]);
	this.setGameData(keyArray[sphereType-1], number);
	}

	public void increaseItemByType(int sphereType){
		Log.e("Game / increaseItemBy", "itemType before : "+ sphereType + " / " + getItemNumberByType(sphereType));
		this.setItemNumberByType(sphereType, getItemNumberByType(sphereType) + 1);
		Log.e("Game / increaseItemBy", "itemType after : "+ sphereType + " / " + getItemNumberByType(sphereType));
	}
	
	public void decreaseItemByType(int sphereType){
		if (getItemNumberByType(sphereType) > 0) {
			int n = this.getItemNumberByType(sphereType) - 1;
			n = (int) CGPoint.clampf(n, 0, n);
			this.setItemNumberByType(sphereType, n);
		}
	}
	
	// ���̵�
	public int getGameDifficulty(){
		//Log.e("get", ""+getGameData("GameDifficulty"));  // log�� Ȯ��
		return 0; // �ӽ÷� expert
		//return getGameData("GameDifficulty");
	}
	
	public void setGameDifficulty(int difficulty) {
		Log.e("GameData / setGameDifficulty", "Difficulty : "+ getGameData("GameDifficulty"));  // log�� Ȯ��
		this.setGameData("GameDifficulty", difficulty);
	}

	//GameMode
	public int getGameMode(){
		return getGameData("GameMode");
	}
	
	public void setGameMode(int mode) {
		this.setGameData("GameMode", mode);
	}


	public void setGameData(String key, int value) {
		data.put(key, value);
	}
	/**************************** ���� ****************************/
	int openCell = 0;
	
	public int getOpenedCell() {
		return this.openCell += 1;
	}

	public int getHeart() {
		return getGameData("HeartNumber");
		
	}

	public int getSecond() {
		return getGameData("Seconds");
		
	}
	
}
// end
