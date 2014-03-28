package com.aga.mine.mains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

import android.util.Log;

import com.facebook.model.GraphUser;


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
	
//	public static long weeklyLeftTimeFilter(String RequestModeGetWeeklyLeftTime) {
//		return Calendar.getInstance().getTimeInMillis() + (1000 * Long.parseLong(RequestModeGetWeeklyLeftTime));
//	}


	
//	int count = 0;
//	String[] idLvScore = {"ID : ","Lv ","Score "};
//	String[] scores = DataFilter.scoreFilter(getFriendsScore(), "***").split("\\&");
//	for (String string : scores) {
////		Log.e("Home", "getFriendsScore [" + string + "]");
//		for (String string2 : string.split("-")) {
//			Log.e("Home", idLvScore[count] + string2);
//			count ++;
//		}
//		count = 0;
//	}
	
	
	
	public static String[] mailFilter(String source) {
		return headerEraser(source, "data=").split(",");
	}
	
	public static String[] userDBFilter(String source) {
		return headerEraser(source, "***").split("-");
	}
	
	public static String[][] scoreFilter(String source) {
//        Log.e("Print",  "source : " + source);
		String[] array = headerEraser(source, "***").split("\\&");
		ArrayList<String[]> sourceList = new ArrayList<String[]>(); 
		for (String strings : array) {
			sourceList.add(strings.split("-"));
		}
		
//		  Collections.sort(sourceList, new ClothesComparator());
//		  
//		  for (String[] item : sourceList) {
//		        Log.e("Print", item[0] + "    " + item[1] + "   " + item[2]);
//		}
		  
//			for (ColorClothes item : list) {
//		//        System.out.println(item.record + "    " + item.color + "   " + item.clothes);
//		        Log.e("Print", item.record + "    " + item.color + "   " + item.clothes);
//		    }
		
		
		/******************************************************/
		
		String[][] stringArray = new String[sourceList.size()][];
		int count = 0;
		for (String[] strings : sourceList) {
			stringArray[count] = sourceList.get(count);
			count++;
		}
		
		//OK!
		Arrays.sort(stringArray, new Comparator<String[]>() {
			@Override
			public int compare(String[] lhs, String[] rhs) {
				String score1 = lhs[2];
				String score2 = rhs[2];
				return score2.compareTo(score1); // 큰수 부터
//				return score1.compareTo(score2); // 작은수 부터
			}
		});

//		Arrays.sort(stringArray, new Comparator<Object>() {
//			@Override
//			public int compare(Object lhs, Object rhs) {
//				String score1 = ((String[])lhs)[2];
//				String score2 = ((String[])rhs)[2];
//				return score2.compareTo(score1);
//			}
//		});

	/******************************************************/

		
		
//		Log.e("DataFilter", "mailFilter - sort : " + Arrays.toString(stringArray));
		
//		for (String[] strings : stringArray) {
//			Log.e("scoreFilter", "id");
//			for (String string : strings) {
//				Log.e("scoreFilter", "" + string);
//			}
//		}
		
////				) {
////			@Override
////			public int compare(final String[] lhs, final String[] rhs) {
////				final String score1 = lhs[2];
////				final String score2 = rhs[2];
////				return score2.compareTo(score1);
////			}
////		});
//		for (String strings : array) {
//			Log.e("DataFilter", "scoreFilter : " + strings);
//		}
//		
//		
//		String[][] result = new String[array.length][];
//		int count =0;
//		for (String string : array) {
//				result[count] = string.split("-"); 
//			count ++;
//		}
////		scoreFilter(source);
//		return result;
		return stringArray;
	}
	
	private ArrayList<String> scoreFilter(String[][] source) {
		String[][] strArray1 = source;
		String[][] temp = new String[strArray1.length][];
		Arrays.sort(strArray1);
		for (String[] strings : strArray1) {
			Log.e("", "");
		}
		return null;
	}
	
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

//	
//	private static String[] userDataCutter1(String source) {
//		return source.split("-");
//	}
//	
//	private static String[] userDataCutter2(String source) {
//		return source.split("\\&");
//	}
	
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
		GraphUser user = FacebookData.getinstance().getUserInfo();		
		List<GraphUser> friends = FacebookData.getinstance().getFriendsInfo();
		String[][] score2Array = getRanking(user, friends);
		return score2Array;
	}
	
	public static String[][] getRanking(GraphUser user, List<GraphUser> friends) {
		String facebookIDs = "(" + user.getId();		
		for (GraphUser graphUser : friends) {
			facebookIDs += "," + graphUser.getId();
		}
		facebookIDs += ")";
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