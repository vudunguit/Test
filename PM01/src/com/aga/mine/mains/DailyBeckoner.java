package com.aga.mine.mains;

import java.util.concurrent.ExecutionException;

import org.cocos2d.nodes.CCDirector;

import android.util.Log;

import com.facebook.model.GraphUser;
import com.sromku.simple.fb.SimpleFacebook;

public class DailyBeckoner {
	
	CCDirector director = CCDirector.sharedDirector();
	
	public DailyBeckoner() {
		// facebookHelper�� ���� facebook ����ڿ� ģ���� ��ü�� �ҷ��;� ������
		// facebookHelper�� ��ġ�� �ʾ� null pointer exception �߻�
		
		// �Ʒ����� ������ ���� �߻��Ҽ� ����. (datafilter ����)
		//String facebookID = "" + (long)(Math.random() * 100003270261971L); // �ӽ÷� �־����ϴ�.				
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
