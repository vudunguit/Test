package com.aga.mine.mains;

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
		if (RequestModeRead == null || RequestModeRead.equals("") || RequestModeRead.indexOf("query error") >= 0 )
			return false;
		return true;
	}
	
	public static void dailyFilter(CCDirector director, String RequestModeDailyCheck) {
		CCScene scene = CCScene.node();
		if (RequestModeDailyCheck.equals("not once a day")) {
			scene = Home.scene();
		} else if (0 < Integer.parseInt(RequestModeDailyCheck) % 31) {
//		RequestModeDailyCheck = "1"; // 테스트용
			scene.addChild(Daily.scene(Integer.parseInt(RequestModeDailyCheck) % 31));
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
	
	public static String[][] scoreFilter(String source) {
		String[] array = headerEraser(source, "***").split("\\&");
		
		String[][] stringArray = new String[array.length][];
		int count = 0;
		for (String strings : array) {
			stringArray[count] = strings.split("-");
			count++;
		}
		
		for (String[] strings : stringArray) {
			Log.e("DataFilter", "0 : " + strings[0] + ", 1 : " + strings[1] + ", 2: " + strings[2]);
		}
		
		return stringArray;
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
	
	public static String getUserDBData(String facebookID) {
		try {
			return new DataController().execute(
					"0,RequestModeRead*1," + facebookID).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;

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
	
	public static String[][] getRanking() {
		Profile user = FacebookData.getinstance().getUserInfo();		
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
		String[][] score2Array = getRanking(user, friends);
		return score2Array;
	}
	
	public static String[][] getRanking(Profile user, List<Profile> friends) {
		String facebookIDs = "(" + user.getId();		
		for (Profile profile : friends) {
			facebookIDs += "," + profile.getId();
		}
		facebookIDs += ")";
		Log.e("DataFilter","facebookIDs : " + facebookIDs );
		String[][] score2Array = getRanking(facebookIDs);
		return score2Array;
	}
	
	public static String[][] getRanking(String facebookIDs) {
		try {
			String rank = new DataController().execute("0,RequestModeGetInDBUserList*23," + facebookIDs).get();
			Log.e("DataFilter", "getRanking : " + rank);
			return scoreFilter(rank);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
