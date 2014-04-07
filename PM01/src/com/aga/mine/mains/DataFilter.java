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
	
	public static void dailyFilter(CCDirector director, String RequestModeDailyCheck) {
		CCScene scene = CCScene.node();
		if (RequestModeDailyCheck.equals("not once a day")) {
			scene = Home.scene();
		} else if (0 < Integer.parseInt(RequestModeDailyCheck) % 31) {
//		RequestModeDailyCheck = "1"; // �׽�Ʈ��
			scene.addChild(Daily.scene(Integer.parseInt(RequestModeDailyCheck) % 31));
		} else {
//			new Process();
//			Process.killProcess(Process.myPid()); // ���� �ֱ� �޽��� ��� �� ����
			director.getActivity().finish(); // ���� �ֱ� �޽��� ��� �� ����
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
	
	public static String getUserDBData(String facebookID) {
		try {
			String getData = new DataController().execute("0,RequestModeRead*1," + facebookID).get();
			if (!DataFilter.readFilter(getData)) { // �´°�
//			if (DataFilter.readFilter(getData)) { // ������ ���� test��
				Log.e("DataFilter", "getUserDBData() ���ο� ���̵� ����");
				DailyBeckoner.setUserDBData(facebookID);
				getData = new DataController().execute("0,RequestModeRead*1," + facebookID).get();
			}

			Log.e("DataFilter", "getUserDBData : " + getData);
			return getData;
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
	
	public static ArrayList<GameScore> getRanking() {
		// ���̽��� ���̵� �ޱ�
		Profile user = FacebookData.getinstance().getUserInfo();		
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
	
		//DB��ȸ�� ���� ���̵�� ��ġ�� (��, ģ����)
		String facebookIDs = "(" + user.getId();		
		for (Profile profile : friends) {
			facebookIDs += "," + profile.getId();
		}
		facebookIDs += ")";
		Log.e("DataFilter","facebookIDs : " + facebookIDs );
	
		try {
			// DB���� ���� ���ھ� ��ȸ
			String rank = new DataController().execute("0,RequestModeGetInDBUserList*23," + facebookIDs).get();
			Log.e("DataFilter", "getRanking : " + rank);

			// ����� ������ ������ �ڸ���
			String[] array = headerEraser(rank, "***").split("\\&");
			
			ArrayList<GameScore> sourceList = new ArrayList<GameScore>(); 
			
			GameScore gameScoreModel;
			
			// �� ������ ������ �� �ڸ���
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
			
			// ���ھ� �������� ����
			Collections.sort(sourceList, new ClothesComparator());
			
			// ������ Ȯ��
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
	
}
