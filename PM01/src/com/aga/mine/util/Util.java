package com.aga.mine.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aga.mine.mains.Constant;
import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.GameInvite;
import com.aga.mine.mains.ImageDownloader;
import com.aga.mine.mains.ImageDownloader.ImageLoaderListener;
import com.aga.mine.mains.MainApplication;
import com.aga.mine.pages.Game;
import com.badlogic.gdx.utils.Array;

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
	
	public static void setBroomstickTime() {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine", 0);
		SharedPreferences.Editor edit = pref.edit();

		edit.putLong("BroomstickTime", System.currentTimeMillis());
		edit.commit();
	}
	
	public static void setBroomstickTime(long leftTime) {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine", 0);
		SharedPreferences.Editor edit = pref.edit();

		edit.putLong("BroomstickTime", System.currentTimeMillis() - leftTime);
		edit.commit();
	}
	
	public static long getBroomstickTime() {
		Context context = MainApplication.getInstance().getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences("mine",0);
		
		long BroomstickTime = pref.getLong("BroomstickTime", 0);
		long nowTime = System.currentTimeMillis();
		
		if(BroomstickTime == 0) {
			return 0;
		} else {
			return nowTime - BroomstickTime;
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
	

/*******************************************************************/
	private static ImageDownloader mDownloader;
	
	// 대전하는 사람 이미지 및 이름 설정 (random, invite match용)
	// 이렇게 써먹기는 범용성이 떨어짐..
	public static void setEntry(String id, String name, boolean owner, CCSprite backboard) {
		Log.e("Util", "setEntry");
		if (backboard.getChildren() == null || backboard.getChildren().size() < 1) {
			Log.e("Util", "if문 : " + backboard.getChildren().size());
			CCScene scene = GameInvite.scene(null, null, false);
			MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
			CCDirector.sharedDirector().replaceScene(scene);
		} else {
			Log.e("Util", "if문 pass");
		}
		
		for (CCNode panel2 : backboard.getChildren()) {
			Log.e("Util", "panel : " + panel2.toString());
			Log.e("Util", "panel.child(102) : " + panel2.getChildByTag(102));
			Log.e("Util", "panel.child(103) : " + panel2.getChildByTag(103));
		}
		
		boolean _owner = owner;
		for (final CCNode panel : backboard.getChildren()) {
			if (_owner) {
				Log.e("Util", "owner true");
//				((CCLabel)panel.getChildByTag(103)).setString(FacebookData.getinstance().getUserInfo().getName());
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
					Log.e("Util", "owner false");
//					((CCLabel)panel.getChildByTag(103)).setString(name);
					String etUrl = "https://graph.facebook.com/" + id +"/picture";
					mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
						@Override
						public void onImageDownloaded(CCSprite profile) {
							((CCSprite)panel.getChildByTag(102)).setTexture(profile.getTexture());
						}
					});
					mDownloader.execute();
					count(backboard);
				}
			}
			_owner = !_owner;
			
		}

	}
	

	final static  String randomfolder = "52random/";
	 static CCSprite counter = null;
	 static int count  = 5;
	
	private static void count(CCSprite parentSprite){
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCSprite tornado = CCSprite.sprite(randomfolder + "Tornado.png");
//		tornado.setAnchorPoint(0.5f, 0.5f);
		tornado.setPosition(parentSprite.getContentSize().width/2, parentSprite.getContentSize().height * 0.28f);
		parentSprite.addChild(tornado);
		CCRepeatForever repeat = CCRepeatForever.action(CCRotateBy.action(16, 360));
		tornado.runAction(repeat);
		
		counter = CCSprite.sprite(randomfolder + "n05.png");
		counter.setPosition(tornado.getPosition());
		parentSprite.addChild(counter);

		MainApplication.getInstance().mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {countdown(1000);
			}
		}, 1000);
		
	}
	
	public static  void countdown(long time) {
		try {
			boolean isLoop = true;
			while (isLoop) {
				Thread.sleep(time);
				count--;
				if (count > 0) {
					CCSprite counterNumber = CCSprite.sprite(randomfolder + "n0" + count + ".png");
					counter.setTexture(counterNumber.getTexture());
				} else {
					isLoop = false;
				}
			}
			// 작업하지않는 다른 패키지 이지만 잘 붙는지만 확인하는 것입니다.
			CCDirector.sharedDirector().replaceScene(Game.scene());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
