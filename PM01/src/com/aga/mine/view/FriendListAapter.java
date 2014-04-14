﻿package com.aga.mine.view;

import java.util.List;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aga.mine.mains.Constant;
import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.GameScore;
import com.aga.mine.mains.MainActivity;
import com.aga.mine.mains.MainApplication;
import com.aga.mine.mains.R;
import com.aga.mine.mains.ShopGold2;
import com.androidquery.AQuery;
import com.sromku.simple.fb.entities.Profile;

public class FriendListAapter extends BaseAdapter {
	private Context mContext;
	private Profile user;
	private List<Profile> friends;
	private List<GameScore> gameScore;
	private AQuery mAq;
	
	public FriendListAapter(Context context) {
		mContext = context;
		user = FacebookData.getinstance().getUserInfo();
		friends = FacebookData.getinstance().getFriendsInfo();
		gameScore = FacebookData.getinstance().getGameScore();
		Log.d("LDK", "adpater, friends size:" + friends.size());
		mAq = new AQuery(mContext);
	}

	@Override
	public int getCount() {
		return gameScore.size();
	}

	@Override
	public String getItem(int position) {
		return gameScore.get(position).id;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Viewholder holder;
		if(convertView == null) {
			holder = new Viewholder();
			convertView = View.inflate(mContext, R.layout.list_friends, null);
			holder.imgNumber = (ImageView) convertView.findViewById(R.id.imgNumber);
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.imgProfile);
			holder.imgGift = (ImageView) convertView.findViewById(R.id.imgGift);
			holder.imgBroomstick = (ImageView) convertView.findViewById(R.id.imgBroomstick);
			holder.tvRanking = (TextView) convertView.findViewById(R.id.tvNumber);
			holder.tvFBId = (TextView) convertView.findViewById(R.id.tvFBId);
			holder.tvScore = (TextView) convertView.findViewById(R.id.tvScore);
			
//			Log.e("FriendListAapter", "size : " + gameScore.size());
//			Log.e("FriendListAapter", "position : " + position + ", rankingID : " +gameScore.get(position).id + ", userID : " + user.getId());
			
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		//1-3등: 금은동 아이콘, 4등~ : no image 처리
		switch(position) {
		case 0: 
//			Log.e("FriendListAapter", "Gold : " + position);
			holder.imgNumber.setImageResource(R.drawable.home_circlegold_hd);
			holder.imgNumber.setVisibility(View.VISIBLE);
			holder.tvRanking.setTextColor(Color.BLACK);
			break;
		case 1:
//			Log.e("FriendListAapter", "Silver : " + position);
			holder.imgNumber.setImageResource(R.drawable.home_circlesilver_hd);
			holder.imgNumber.setVisibility(View.VISIBLE);
			holder.tvRanking.setTextColor(Color.BLACK);
			break;
		case 2:
//			Log.e("FriendListAapter", "Bronze : " + position);
			holder.imgNumber.setImageResource(R.drawable.home_circlebronze_hd);
			holder.imgNumber.setVisibility(View.VISIBLE);
			holder.tvRanking.setTextColor(Color.WHITE);
			break;
		default:
//			Log.e("FriendListAapter", "noMedal : " + position);
			holder.imgNumber.setVisibility(View.INVISIBLE);
			holder.tvRanking.setTextColor(Color.WHITE);
			break;
		}
		
		if (gameScore.get(position).id.equals(user.getId())) {
//			Log.e("FriendListAapter", "INVISIBLE : " + position);
			holder.imgGift.setVisibility(View.INVISIBLE);
			holder.imgBroomstick.setVisibility(View.INVISIBLE);
		} else {
//			Log.e("FriendListAapter", "VISIBLE : " + position);
			holder.imgGift.setVisibility(View.VISIBLE);
			holder.imgBroomstick.setVisibility(View.VISIBLE);
		}
		//
		holder.tvRanking.setText(String.valueOf(position+1));
		holder.tvFBId.setText(gameScore.get(position).name);
		holder.tvScore.setText(String.valueOf(gameScore.get(position).score));
		
		//선물상자 클릭 이벤트 처리
		holder.imgGift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//hide scroll view
				MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
				
				FacebookData.getinstance().setRecipientID(gameScore.get(position).id);
				CCScene scene = ShopGold2.scene();
				CCDirector.sharedDirector().replaceScene(scene);
			}
		});
		
		//빗자루 클릭 이벤트 처리
		holder.imgBroomstick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity mMainActivity = MainApplication.getInstance().getActivity();
				//hide scroll view
				mMainActivity.mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
				
				// facebook sendRequest보낸 후
				//sendRequest 성공시 보낸 게시물의 id를 requestID에 담아 보냅니다.
//				long requestID = FacebookData.getinstance().getRequestID();  //test용 
//				String recipientID = FacebookData.getinstance().getRecipientID(); // 상점 이동 방식에 따른 ID 변경
//				String senderID = FacebookData.getinstance().getUserInfo().getId();
				
//				String giftItemType = "Broomstick";
//				int giftBroomStick = 1;
//				String data = 
//						"0,RequestModeMailBoxAdd" +
//						"*22," + requestID + 
//						"*1," + recipientID + 
//						"*19," + senderID + 
//						"*20," + giftItemType + 
//						"*21," + giftBloomStick;
//				FacebookData.getinstance().sendMail(data);
				
//				FacebookData.getinstance().setRecipientID(gameScore.get(position).id);
//				CCScene scene = ShopBroomstick2.scene();
//				CCDirector.sharedDirector().replaceScene(scene);
				
				mMainActivity.sendInvite(gameScore.get(position).id, gameScore.get(position).name + "이(가) 빗자루 하나를 보냈습니다", null);
			}
		});
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + gameScore.get(position).id +"/picture";
//		Log.e("FriendListAapter", "position : " + position + ", url : " + url);
		
		if(aq.shouldDelay(position, convertView, parent, url)){
			//aq.id(holder.imgProfile).image(placeholder);
		}else{
			//aq.id(holder.imgProfile).image(url, true, true, 0, 0, placeholder, AQuery.FADE_IN_NETWORK, 0);
			aq.id(holder.imgProfile).image(url, true, true);
		}
		
		return convertView;
	}
	
	class Viewholder {
		ImageView imgNumber;
		ImageView imgProfile;
		ImageView imgGift;
		ImageView imgBroomstick;
		TextView tvRanking;
		TextView tvFBId;
		TextView tvScore;
	}

}
