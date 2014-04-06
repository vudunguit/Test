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
import com.aga.mine.mains.R;
import com.androidquery.AQuery;
import com.sromku.simple.fb.entities.Profile;

public class FriendListAapter extends BaseAdapter {
	private Context mContext;
	private List<Profile> friends;
	private AQuery mAq;
	
	public FriendListAapter(Context context) {
		mContext = context;
		friends = FacebookData.getinstance().getFriendsInfo();
		Log.d("LDK", "adpater, friends size:" + friends.size());
		mAq = new AQuery(mContext);
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Viewholder holder;
		if(convertView == null) {
			holder = new Viewholder();
			convertView = View.inflate(mContext, R.layout.list_friends, null);
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.imgProfile);
			holder.tvFBId = (TextView) convertView.findViewById(R.id.tvFBId);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		holder.tvFBId.setText(friends.get(position).getId());
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + friends.get(position).getId() +"/picture";
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
		TextView tvFBId;
	}

}
