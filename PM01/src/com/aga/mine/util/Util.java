package com.aga.mine.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.ImageDownloader;
import com.aga.mine.mains.ImageDownloader.ImageLoaderListener;
import com.aga.mine.mains.MainApplication;

public final class Util {

	public static void setBroom(String id) {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine", 0);
		SharedPreferences.Editor edit = pref.edit();

		//key : Broom:id,
		//value : timestamp(ms)
		edit.putLong("Broom" + ":" + id, System.currentTimeMillis());

		edit.commit();
	}
	
	public static boolean canSendBroom(String id) {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine",0);
		
		long sendTime = pref.getLong("Broom" + ":" + id, 0);
		long nowTime = System.currentTimeMillis();
		
		if(nowTime - sendTime > 3 * 60 * 60 * 1000) { //3 hour
			return true;
		} else {
			return false;
		}
	}
	
	public static void setInvite(String id) {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine", 0);
		SharedPreferences.Editor edit = pref.edit();

		//key : Invite:id,
		//value : "20140415"
		Calendar cal = Calendar.getInstance();
		String date = String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
		edit.putString("Invite" + ":" + id, date);

		edit.commit();
	}
	
	public static boolean canInvite(String id) {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine",0);
		
		Calendar cal = Calendar.getInstance();
		String sendDate = pref.getString("Invite" + ":" + id, "");
		String nowDate = String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
		
		if(nowDate.equals(sendDate)) { 
			return false;
		} else {
			return true;
		}
	}

	
	static Map<String, Boolean> join = new HashMap<String, Boolean>();
	// 게임 접속자들 체크
	public static void setJoin(String id, byte joinValue) {
		Log.e("Util", "set id : " + id + ", " + joinValue);
		if (joinValue > 0)
			join.put(id, true);
		else
			join.put(id, false);
	}
	
	public static boolean getJoin(String id) {
		Log.e("Util", "get id : " + id);
		if (id == null || join.get(id) == null)
			return false;
		return join.get(id);
	}
	


	public static ImageDownloader mDownloader;
	
	// 대전하는 사람 이미지 및 이름 설정 (random, invite match용)
	// 이렇게 써먹기는 범용성이 떨어짐..
	public static void setEntry(String id, String name, boolean owner, List<CCNode> matchingPanel) {
		boolean _owner = owner;
		for (final CCNode panel : matchingPanel) {

			if (_owner) {
				((CCLabel)panel.getChildByTag(103)).setString(FacebookData.getinstance().getUserInfo().getName());
				String etUrl = "https://graph.facebook.com/" + FacebookData.getinstance().getUserInfo().getId() +"/picture";
				mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
					@Override
					public void onImageDownloaded(CCSprite profile) {
						((CCSprite)panel.getChildByTag(102)).setTexture(profile.getTexture());
					}
				});
				mDownloader.execute();

			} else {
				if (id != null && name != null) {
					((CCLabel)panel.getChildByTag(103)).setString(name);
					String etUrl = "https://graph.facebook.com/" + id +"/picture";
					mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
						@Override
						public void onImageDownloaded(CCSprite profile) {
							((CCSprite)panel.getChildByTag(102)).setTexture(profile.getTexture());
						}
					});
					mDownloader.execute();
					
				}
			}
			_owner = !_owner;
			
		}
	}
	
}
