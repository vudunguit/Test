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
		
		if (gameScore.get(position).id.equals(user.getId())) {
			holder.imgGift.setVisibility(View.GONE);
			holder.imgBroomstick.setVisibility(View.GONE);
		}
		holder.tvRanking.setText(String.valueOf(position+1));
		holder.tvFBId.setText(gameScore.get(position).name);
//		holder.tvFBId.setText(friends.get(position).getName());
		holder.tvScore.setText(String.valueOf(gameScore.get(position).score));
		
		AQuery aq = mAq.recycle(convertView);
		
//		String url = "https://graph.facebook.com/" + friends.get(position).getId() +"/picture";
		String url = "https://graph.facebook.com/" + gameScore.get(position).id +"/picture";
//		Bitmap placeholder = null;
//		try {
//			placeholder = BitmapFactory.decodeStream( mContext.getAssets().open(""));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		if(aq.shouldDelay(position, convertView, parent, url)){
			//aq.id(holder.imgProfile).image(placeholder);
		}else{
			//aq.id(holder.imgProfile).image(url, true, true, 0, 0, placeholder, AQuery.FADE_IN_NETWORK, 0);
			aq.id(holder.imgProfile).image(url, true, true);
		}
		
		return convertView;
	}
	
	class Viewholder {
		ImageView imgProfile;
		ImageView imgGift;
		ImageView imgBroomstick;
		TextView tvRanking;
		TextView tvFBId;
		TextView tvScore;
	}

}
