package com.aga.mine.pages2;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Clock extends CCLayer{
	
	int val=0;
    public static String aaa ;
    
    public Clock() {
	}
    
	public void startTime(int value) {
		val = value;
		this.schedule("tick", 1.0f);
	}

	void stopTime() {
		this.unschedule("tick");
	}

	public void tick(float dt) {
		val--;
		
		if (val < 1)
			this.stopTime();
	}
	
	public int getVal() {
		return val;
	}
}
