package com.aga.mine.pages;

import java.util.Currency;
import java.util.HashMap;

import org.cocos2d.types.CGPoint;

import android.util.Log;

	// 일단 물어볼것 빼고 완료
public class GameData {

	//
	// 게임 데이터 일반 (생명력)
	final int kMaxHeartNumber = 3;
	
	// 수정구
	final int kNumberOfSphere = 3; // 기본3개

	//
	// 수정구 확률
	final int kFireChance = 14;
	final int kWindChance = 21;
	final int kCloudChance = 35;

	final int kDivineChance = 15;
	final int kEarthChance = 9;
	final int kMirrorChance = 6;

	//
	// 게임 난이도
	final int kGameDifficultyEasy = 0;
	final int kGameDifficultyNormal = 1;
	final int kGameDifficultyHard = 2;

	// 게임 데이터
	HashMap<String, Integer> data;
	int currentMine;
	
	//
	// 난이도별 지뢰 갯수
	final int kMaxMineNumberEasy    =  30;
	final int kMaxMineNumberNormal  =  60;
	final int kMaxMineNumberHard     = 90;
	
	
	String mapFolder =  "80map/";
	//
	// 게임 맵
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
    // 게임설정 스프레드쉬트를 읽어들인다.
   final int maxMineNumber = getMaxMineNumber(getGameDifficulty());
   
	private void initGameData() {
	
		    
		    //
		    // 초기값으로 지정한다.
		   this.data = new HashMap<String, Integer>();
		   
		   // value 꺼낼때는 형변환 해줘야됨.
		   data.put("HeartNumber", 3);	 // 생명수
		   data.put("MineNumber", maxMineNumber);	 // 남아있는 지뢰수
		   data.put("Seconds", 900); 		// 15 min, 남은 게임 진행시간 // Game에서 다시 값을 넣어서 무의미 하다.
		   data.put("ItemFire", 0);				// 획득한 수정구, 불	
		   data.put("ItemWind", 0);			// 획득한 수정구, 바람	
		   data.put("ItemCloud", 0);			// 획득한 수정구, 구름
		   data.put("ItemDivine", 0);			// 획득한 수정구, 신성
		   data.put("ItemEarth", 0);			// 획득한 수정구, 대지
		   data.put("ItemMirror", 0);			// 획득한 수정구, 반사
		   data.put("GameDifficulty", 1);	// 게임 난이도 // 임시로 1
		   
		   data.put("GameMode", 1);		// 게임 모드 // 임시로 1
		   
		   currentMine = 0;
	}

	// #pragma mark - data methods
	public int getMaxMineNumber(int gameDifficulty) {
		int value = -1;

		// 테스트를 위해 지뢰수 임시 수정
		switch (gameDifficulty) {
		case kGameDifficultyEasy:
			Log.e("GameData / getMaxMineNumber", "gameDifficulty --> MineNumber : " + 30);
			value = 30; // 기본 30개
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
	
	public void setMineNumber(int number) {
		this.setGameData("MineNumber", number);
	}
	
	// 버섯(깃발)을 꽂았을때 꽂은 자리에 지뢰가 있으면 +1
	public int currentMineNumber() {
		int a = currentMine +=1;
		Log.e("gameData currentMineNumber", "currentMine : " + a);  // log로 확인
		return a;
	}
	
	public int previousMineNumber() {
		int a = currentMine -=1;
		Log.e("gameData previousMineNumber", "currentMine : " + a);  // log로 확인
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
		//
		// sphereType from 1 to 6
		//","로 구분하여 스트링 배열로 만든다?
		String keyString = "ItemFire,ItemWind,ItemCloud,ItemDivine,ItemEarth,ItemMirror";
		String[] keyArray = keyString.split(",",6);
		//Log.e("GameData", "구슬 종류:" + (sphereType) + ":" + keyArray[sphereType-1]);
		//Log.e("GameData", "getGameData:" + getGameData(keyArray[sphereType-1]));
		return getGameData(keyArray[sphereType-1]);
	}

	public int getGameData(String key) {
		// 임시로
		//
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
	
	// 난이도
	public int getGameDifficulty(){
		//Log.e("get", ""+getGameData("GameDifficulty"));  // log로 확인
		return 0; // 임시로 expert
		//return getGameData("GameDifficulty");
	}
	
	public void setGameDifficulty(int difficulty) {
		Log.e("GameData / setGameDifficulty", "Difficulty : "+ getGameData("GameDifficulty"));  // log로 확인
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
	/**************************** 변경 ****************************/
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
