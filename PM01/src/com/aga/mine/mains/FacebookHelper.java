package com.aga.mine.mains;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookHelper extends Activity {
//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {

				if (session.isOpened()) {
					
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
							FacebookData.getinstance().setUserInfo(user);
							Log.e("FacebookHelper", "setUserInfo");
							nextCallback(FacebookData.getinstance().facebookReady(1));
					      

						}
					}).executeAsync();
					
					Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
								@Override
								public void onCompleted(List<GraphUser> users, Response response) {
									FacebookData.getinstance().setFriendsInfo(users);
									Log.e("FacebookHelper", "setFriendsInfo");
									nextCallback(FacebookData.getinstance().facebookReady(100));
									
								}
							}).executeAsync();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private void nextCallback(boolean facebookReady) {
		Log.e("FacebookHelper", "nextCallback");
		if (facebookReady) {
			
			HomeScroll.getInstance().setData(
					DataFilter.getRanking(FacebookData.getinstance().getUserInfo(),FacebookData.getinstance().getFriendsInfo())
			);
			startActivity(new Intent(FacebookHelper.this, Daily.class));
//	    	finish();	
		}
	}
	
}
