﻿package com.aga.mine.pages2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cocos2d.types.CGPoint;

import android.util.Log;

	// 일단 물어볼것 빼고 완료
public class GameData {
	
	public class MessageData {
		byte type;
		int value;
	}
	
	List<MessageData> playData = new ArrayList<MessageData>();
	
	public void setplayData(byte key, int value) {
		MessageData data = new MessageData();
		data.type = key;
		data.value = value;
		playData.add(data);
//		playData.put(key, value);
	}
	
	public boolean createdMinimap = false;
	public List getplayData() {
		if (!createdMinimap) {
			createdMinimap = !createdMinimap;
		}
		return playData;
	}
	//
	// 게임 데이터 일반 (생명력)
	public final int kMaxHeartNumber = 3;
	
	// 수정구
	public final int kNumberOfSphere = 3; // 기본3개

	//
	// 수정구 확률
	public final int kFireChance = 14;
	public final int kWindChance = 21;
	public final int kCloudChance = 35;

	public final int kDivineChance = 15;
	public final int kEarthChance = 9;
	public final int kMirrorChance = 6;
	
	public final int kSingleEarthChance = 20;
	public final int kSingleEmptyChance = 80;

	//
	// 게임 난이도
	public final int kGameDifficultyEasy = 1;
	public final int kGameDifficultyNormal = 2;
	public final int kGameDifficultyHard = 3;

	// 게임 데이터
	HashMap<String, Integer> data;
	int currentMine;
	
	//
	// 난이도별 지뢰 갯수 (단위 : 퍼센트)
	public final int kMaxMineNumberEasy    =  12;
	public final int kMaxMineNumberNormal  =  15;
	public final int kMaxMineNumberHard     = 18;
	
	
	String mapFolder =  "80map/";
	//
	// 게임 맵
	//String gameMap = "tileMap.tmx";
	//String gameMap = "tilemap02.tmx";
	//
	public String gameMap = mapFolder + "map003.tmx";
	//String gameMap = "map002.tmx";
	//String gameMap = "map003.tmx";
	//"orthogonal-test1.tmx";
	//"orthogonal-test3.tmx";
	//"attendance2.tmx";
	
	public boolean isGuestMode = false;
	public boolean isMultiGame = false;
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
    // 게임설정 스프레드쉬트를 읽어들인다.
//   public final int maxMineNumber = getMaxMineNumber(getGameDifficulty());
   
	private void initGameData() {
	
		    
		    //
		    // 초기값으로 지정한다.
		   this.data = new HashMap<String, Integer>();
		   
		   // value 꺼낼때는 형변환 해줘야됨.
		   data.put("HeartNumber", 3);	 // 생명수
		   data.put("MineNumber", 0);	 // 남아있는 지뢰수
		   data.put("Seconds", 900); 		// 15 min, 남은 게임 진행시간 // Game에서 다시 값을 넣어서 무의미 하다.
		   data.put("ItemFire", 50);				// 획득한 수정구, 불	
		   data.put("ItemWind", 50);			// 획득한 수정구, 바람	
		   data.put("ItemCloud", 50);			// 획득한 수정구, 구름
		   data.put("ItemDivine", 50);			// 획득한 수정구, 신성
		   data.put("ItemEarth", 50);			// 획득한 수정구, 대지
		   data.put("ItemMirror", 50);			// 획득한 수정구, 반사
		   data.put("GameDifficulty", 1);	// 게임 난이도 // 임시로 1
		   
		   data.put("GameMode", 1);		// 게임 모드 // 임시로 1
		   
		   currentMine = 0;
	}

//	// #pragma mark - data methods
//	int getMaxMineNumber(int gameDifficulty) {
//		int value = -1;
//		String difficulty = null;
//		
//		// 맵마다 지뢰 수량 다른듯...
//		// 맵 구석 지뢰 생김..(난이도 상승에 따른 제거 였다가 다시 올린듯. tilemap 수정필요)
//		switch (gameDifficulty) {
//		case kGameDifficultyEasy:
//			value = 30;
//			difficulty = "Easy";
//			break;
//
//		case kGameDifficultyNormal:
//			value = 48;
//			difficulty = "Normal";
//			break;
//
//		case kGameDifficultyHard:
//			value = 65;
//			difficulty = "Hard";
//			break;
//		}
//		Log.e("GameData", "gameDifficulty " + difficulty + ", MineNumber : " + value);
//		return value;
//	}

	//HeartNumber
	//return값이 int지만 hashmap에 integer로 저장되어 자동형변환이 안될시 형변환 필요
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
	
//	public void setMineNumber(int number) {
//		this.setGameData("MineNumber", number);
//	}
	
	public void setMineNumber(int TileNumber) {
		int tile = (int) (TileNumber * ((9.0f + (getGameDifficulty() * 3)) / 100));
		Log.e("GameData", "setMineNumber : " + tile);
		setGameData("MineNumber", tile);
	}

//	public int increaseMineNumber() {
//		setMineNumber(getMineNumber() + 1);
//		return getMineNumber();
//	}
//	
//	public int decreaseMineNumber() {
//		setMineNumber(getMineNumber() - 1);
//		return getMineNumber();
//	}
	
	
	public int getCurrentMine() {
		return currentMine;
	}
	
	public int resetMineNumber() {
		return currentMine = 0 ;
	}
	
	// 버섯(깃발)을 꽂았을때 꽂은 자리에 지뢰가 있으면 +1
	public void markMine() {
		currentMine += 1;
		Log.e("gameData currentMineNumber", "currentMine : " + currentMine);  // log로 확인
	}
	
	public void unmarkMine() {
		currentMine -= 1;
		Log.e("gameData previousMineNumber", "currentMine : " + currentMine);  // log로 확인
	}
	
	public int getSeconds() {
		// 최초설정시간에서 1초씩 감소함.
		int seconds = getGameData("Seconds");
		Log.e("GameData", "getSeconds : " + seconds);
		return seconds;
	}
	
	public void setSeconds(int seconds) {
		seconds = (int)CGPoint.clampf((float)seconds,	0, (float)seconds); //뭐에 쓰는 물건인고?
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
		String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
		String[] keyArray = keyString.split(",",6);
		return getGameData(keyArray[sphereType-1]);
	}

	private int getGameData(String key) {
		return data.get(key);
	}

	public void setItemNumberByType(int sphereType, int number){
	//","로 구분하여 스트링 배열로 만든다?	
	String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
	String[] keyArray = keyString.split(",", 6);

	Log.e("Game / setItemNumberByType", "keyArray : "+ keyArray[sphereType-1]);
	this.setGameData(keyArray[sphereType-1], number);
	}

	public void increaseItemByType(int sphereType){
//		Log.e("Game / increaseItemBy", "itemType before : "+ sphereType + " / " + getItemNumberByType(sphereType));
		this.setItemNumberByType(sphereType, getItemNumberByType(sphereType) + 1);
//		Log.e("Game / increaseItemBy", "itemType after : "+ sphereType + " / " + getItemNumberByType(sphereType));
	}
	
	public void decreaseItemByType(int sphereType){
		if (getItemNumberByType(sphereType) > 0) {
			int n = this.getItemNumberByType(sphereType) - 1;
			n = (int) CGPoint.clampf(n, 0, n);
			this.setItemNumberByType(sphereType, n);
		}
	}
	
	// 난이도
	public int getGameDifficulty(){
		return getGameData("GameDifficulty");
	}
	
	public void setGameDifficulty(int difficulty) {
//		Log.e("GameData / setGameDifficulty", "Difficulty : "+ getGameData("GameDifficulty"));  // log로 확인
		this.setGameData("GameDifficulty", difficulty);
//		setMineNumber(getMaxMineNumber(getGameDifficulty()));
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
	
//	public void setGameData(String key, int value) {
//		data.put(key, value);
//	}
	
	
	/**************************** 변경 ****************************/
	private int openCell = 0;
	
	public void addOpenedCell() {
		openCell++;
	}
	
	public int getOpenedCell() {
		return openCell;
	}

	public int getHeart() {
		return getGameData("HeartNumber");
	}

	public int getSecond() {
		return getGameData("Seconds");
	}
	
	public void setMap(byte mapType) {
		if (isMultiGame)
			gameMap = mapFolder + "map00"+ mapType + ".tmx";
		else {
			int map = (int) ((Math.random() * 3) + 1);
			gameMap = String.format(mapFolder + "map%03d.tmx", map);
		}
	}

//	public String getMap() {
//		if (isMultiGame)
//			return gameMap;
//		int map = (int) ((Math.random() * 3) + 1);
//		return String.format(mapFolder + "map%03d.tmx", map);
//	}
}
// end
