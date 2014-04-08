package com.aga.mine.mains;

import java.util.concurrent.ExecutionException;

import org.cocos2d.nodes.CCDirector;

import android.util.Log;

public class DailyBeckoner {
	
	
	final static int level = 1;
	final static int sphere = 2;
	final static int exp = 0;
	final static int gold = 0;
	final static int gameScore = 0;
	final static int win = 0;
	final static int lose = 0;
	final static int broomstick = 10;
	final static int fire = 1;
	final static int wind = 1;
	final static int cloud = 1;
	final static int divine = 0;
	final static int earth = 0;
	final static int mirror = 0;
	final static String emoticon = "1,2,3";
	final static int invite = 0; 
	
	CCDirector director = CCDirector.sharedDirector();
	
	public DailyBeckoner() {
		// facebookHelper를 통해 facebook 사용자와 친구들 객체를 불러와야 하지만
		// facebookHelper를 거치지 않아 null pointer exception 발생
		
		// 아래에서 스레드 문제 발생할수 있음. (datafilter 사용시)
		String facebookID = FacebookData.getinstance().getUserInfo().getId();
		Log.e("Daily", "facebookID : " + facebookID);
		if (!DataFilter.readFilter(getUserDBData(facebookID)))
			setUserDBData(facebookID);
		DataFilter.dailyFilter(director, getDailyData(facebookID));
	}
	


	private String getUserDBData(String facebookID) {
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
	
	// 일단 임시로 열어놨습니다.
	public static void setUserDBData(String facebookID) {
		Log.e("Daily", "setUserDBData");
		try {
			String asd = new DataController()
					.execute("0,RequestModeUpdate" +
							"*1,"  + facebookID + 
							"*2," +level +
							"*3," +sphere +
							"*4," +exp +
							"*5," +gold +
							"*6," + gameScore +
							"*7," +win +
							"*8," +lose +
							"*9," +broomstick +
							"*10," +fire +
							"*11," +wind +
							"*12," +cloud +
							"*13," +divine +
							"*14," +earth +
							"*15," +mirror +
							"*16," +emoticon +
							"*17," +invite).get();
			Log.e("Daily", "setUserDBData : " + asd);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private String getDailyData(String facebookID) {
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
}
