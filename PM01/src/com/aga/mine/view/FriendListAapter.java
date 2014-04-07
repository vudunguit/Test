package com.aga.mine.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.GameScore;
import com.aga.mine.mains.R;
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
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		//1-3��: ������ ������, 4��~ : no image ó��
		switch(position) {
		case 0: 
			holder.imgNumber.setImageResource(R.drawable.home_circlegold_hd);
			break;
		case 1:
			holder.imgNumber.setImageResource(R.drawable.home_circlesilver_hd);
			break;
		case 2:
			holder.imgNumber.setImageResource(R.drawable.home_circlebronze_hd);
			break;
		default:
			holder.imgNumber.setVisibility(View.INVISIBLE);
			break;
		}
		
		if (gameScore.get(position).id.equals(user.getId())) {
			holder.imgGift.setVisibility(View.INVISIBLE);
			holder.imgBroomstick.setVisibility(View.INVISIBLE);
		}
		
		holder.tvRanking.setText(String.valueOf(position+1));
		holder.tvFBId.setText(gameScore.get(position).name);
		holder.tvScore.setText(String.valueOf(gameScore.get(position).score));
		
		//�������� Ŭ�� �̺�Ʈ ó��
		holder.imgGift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//To Do
			}
		});
		
		//���ڷ� Ŭ�� �̺�Ʈ ó��
		holder.imgBroomstick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//To Do
			}
		});
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + gameScore.get(position).id +"/picture";

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
