package com.aga.mine.mains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

import android.util.Log;

import com.sromku.simple.fb.entities.Profile;


public class DataFilter {
	
	
	private static String headerEraser(String source, String filterStr) {
		if (source.indexOf(filterStr) < 0) {
			return source;
		}
		return source.substring(source.indexOf(filterStr) + filterStr.length(), source.length());
	}
	
	public static boolean readFilter(String RequestModeRead) {
		Log.e("DataFilter", "RequestModeRead response [" + RequestModeRead +"]");
		if (RequestModeRead == null || RequestModeRead.equals("") || RequestModeRead.indexOf("query error") >= 0) {
			Log.e("DataFilter", "false");
			return false;
		}
		Log.e("DataFilter", "true");
		return true;
	}
	
	public static void dailyFilter(CCDirector director, String facebookID) {
		String requestModeDailyCheck = getDailyData(facebookID);
		CCScene scene = CCScene.node();
		if (requestModeDailyCheck.equals("not once a day")) {
			scene = Home.scene();
		} else if (0 < Integer.parseInt(requestModeDailyCheck) % 31) {
//		RequestModeDailyCheck = "1"; // 테스트용
			scene.addChild(Daily.scene(Integer.parseInt(requestModeDailyCheck) % 31));
		} else {
//			new Process();
//			Process.killProcess(Process.myPid()); // 문의 넣기 메시지 출력 후 종료
			director.getActivity().finish(); // 문의 넣기 메시지 출력 후 종료
		}
		director.runWithScene(scene);
	}
	
	public static String[] mailFilter(String source) {
		return headerEraser(source, "data=").split(",");
	}
	
	public static String[] userDBFilter(String source) {
		return headerEraser(source, "***").split("-");
	}
	
	//	private ArrayList<String> scoreFilter(String[][] source) {
//		String[][] strArray1 = source;
//		String[][] temp = new String[strArray1.length][];
//		Arrays.sort(strArray1);
//		for (String[] strings : strArray1) {
//			Log.e("", "");
//		}
//		return null;
//	}
	
	public static String userScoreFilter(String source) {
		if (!headerEraser(source, "isQuerySuccess=YES").equals(source)) {
			return "complete";
		};
		return "false";
	}
	
	public static boolean sendMailFilter(String source) {
		Log.e("DataFilter", "sendMailFilter : " + Integer.parseInt(headerEraser(source, "Rows=")));
		if (Integer.parseInt(headerEraser(source, "Rows=")) == 1)
			return true;
		return false;
	}

	public static String itemEraser(String requestIDs) {
		try {
			return new DataController().execute("0,RequestModeMailBoxDelete*22,"+ requestIDs).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String addGameScore(String score) {
		try {
			return new DataController().execute(
					"0,RequestModeAddScore" +
					"*1," + FacebookData.getinstance().getUserInfo().getId() +
					"*18,"+ score).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<GameScore> getRanking() {
		// 페이스북 아이디 받기
		Profile user = FacebookData.getinstance().getUserInfo();		
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
	
		//DB조회를 위해 아이디들 합치기 (나, 친구들)
		String facebookIDs = "(" + user.getId();		
		for (Profile profile : friends) {
			facebookIDs += "," + profile.getId();
		}
		facebookIDs += ")";
		Log.e("DataFilter","facebookIDs : " + facebookIDs );
	
		try {
			// DB에서 게임 스코어 조회
			String rank = new DataController().execute("0,RequestModeGetInDBUserList*23," + facebookIDs).get();
			Log.e("DataFilter", "getRanking : " + rank);

			// 결과값 유저별 데이터 자르기
			String[] array = headerEraser(rank, "***").split("\\&");
			
			ArrayList<GameScore> sourceList = new ArrayList<GameScore>(); 
			
			GameScore gameScoreModel;
			
			// 각 유저별 데이터 상세 자르기
			int count = 1;
			for (String strings : array) {
				String[] userScoreData = strings.split("-");
				gameScoreModel = new GameScore();
				gameScoreModel.id = userScoreData[0];

				if (user.getId().equals(gameScoreModel.id))
					gameScoreModel.name = user.getName();
				else
					for (Profile facebook : friends) {
						if (facebook.getId().equals(gameScoreModel.id)) {
							gameScoreModel.name = facebook.getName();
							break;
						}
					}
				
				gameScoreModel.level = Integer.parseInt(userScoreData[1]);
				gameScoreModel.score = Integer.parseInt(userScoreData[2]);
				sourceList.add(gameScoreModel);
				count ++;
			}
			
			// 스코어 내림차순 정렬
			Collections.sort(sourceList, new ClothesComparator());
			
			// 데이터 확인
			for (GameScore strings : sourceList) {
				Log.e("DataFilter", "score : " + strings.name + "," + strings.id + "," + strings.level + "," + strings.score);
			}

			return sourceList;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static String getGameVersionData() {
			try {
				return new DataController().execute("0,RequestModeIsServerOk*24,0").get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	public static String getUserDBData1(String facebookID) {
		try {
			return new DataController().execute("0,RequestModeRead*1," + facebookID).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String getUserDBData(String facebookID) {
		String getData = getUserDBData1(facebookID);
		if (!readFilter(getData)) { // 정상작동은 false ,해당 ID 데이터 초기화시 true (test용)
			Log.e("DataFilter", "getUserDBData() 새로운 아이디 생성 또는 기존 아이디 초기화");
			setUserDBData(facebookID);
			getData = getUserDBData1(facebookID);
		}
		Log.e("DataFilter", "getUserDBData : " + getData);
		return getData;
	}

	
	public static void setUserDBData(String facebookID) {
		Log.e("Daily", "setUserDBData");
		try {
			String userDataCreate = new DataController()
					.execute("0,RequestModeUpdate" +
							"*1,"  + facebookID + 
							"*2," +DBuser.level +
							"*3," +DBuser.sphere +
							"*4," +DBuser.exp +
							"*5," +DBuser.gold +
							"*6," +DBuser.gameScore +
							"*7," +DBuser.win +
							"*8," +DBuser.lose +
							"*9," +DBuser.broomstick +
							"*10," +DBuser.fireLevel +
							"*11," +DBuser.windLevel +
							"*12," +DBuser.cloudLevel +
							"*13," +DBuser.divineLevel +
							"*14," +DBuser.earthLevel +
							"*15," +DBuser.mirrorLevel +
							"*16," +DBuser.emoticons +
							"*17," +DBuser.invite).get();
			Log.e("Daily", "setUserDBData : " + userDataCreate);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	public static String getDailyData(String facebookID) {
		try {
			return new DataController().execute(
					"0,RequestModeDailyCheck*1," + facebookID).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	
	class DBuser {
		final static int level = 1;
		final static int sphere = 2;
		final static int exp = 0;
		final static int gold = 0;
		final static int gameScore = 0;
		final static int win = 0;
		final static int lose = 0;
		final static int broomstick = 10;
		final static int fireLevel = 1;
		final static int windLevel = 1;
		final static int cloudLevel = 1;
		final static int divineLevel = 0;
		final static int earthLevel = 0;
		final static int mirrorLevel = 0;
		final static String emoticons = "1,2,3";
		final static int invite = 0; 
	}
}
