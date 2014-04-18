package com.aga.mine.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

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
	
}
