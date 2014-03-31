package com.aga.mine.mains;

import java.util.concurrent.ExecutionException;

import org.cocos2d.nodes.CCDirector;

import android.util.Log;

import com.facebook.model.GraphUser;
import com.sromku.simple.fb.SimpleFacebook;

public class DailyBeckoner {
	
	CCDirector director = CCDirector.sharedDirector();
	
	public DailyBeckoner() {
		// facebookHelper를 통해 facebook 사용자와 친구들 객체를 불러와야 하지만
		// facebookHelper를 거치지 않아 null pointer exception 발생
		
		// 아래에서 스레드 문제 발생할수 있음. (datafilter 사용시)
		//String facebookID = "" + (long)(Math.random() * 100003270261971L); // 임시로 넣었습니다.				
		String facebookID = FacebookData.getinstance().getUserInfo().getId();
		if (!DataFilter.readFilter(getUserDBData(facebookID))) {
			Log.e("Daily", "setUserDBData");
			setUserDBData(facebookID);
		}
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

	private void setUserDBData(String facebookID) {
		new DataController()
				.execute("0,RequestModeUpdate*1,"
						+ facebookID
						+ "*2,1*3,2*4,3*5,4*6,5*7,6*8,7*9,8*10,9*11,10*12,11*13,12*14,13*15,14*16,15*17,16");
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
