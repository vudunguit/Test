package com.aga.mine.view;

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
import com.aga.mine.mains.ShopBroomstick2;
import com.aga.mine.mains.ShopGold2;
import com.androidquery.AQuery;
import com.sromku.simple.fb.entities.Profile;

public class InviteListAapter extends BaseAdapter {
	private Context mContext;
	private Profile user;
	private List<Profile> friends;
	private List<GameScore> gameScore;
	private AQuery mAq;
	
	public InviteListAapter(Context context) {
		mContext = context;
		user = FacebookData.getinstance().getUserInfo();
		friends = FacebookData.getinstance().getFriendsInfo();
		gameScore = FacebookData.getinstance().getGameScore();
		Log.d("LDK", "adpater, friends size:" + friends.size());
		mAq = new AQuery(mContext);
	}

	@Override
	public int getCount() {
		// friends.getid()에서 gameScore.id를 뺀후 남는 친구만 리스트에 넣습니다.
		return friends.size();
	}

	@Override
	public String getItem(int position) {
		return friends.get(position).getId();
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
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.imgProfile);
			holder.imgInviteBtn = (ImageView) convertView.findViewById(R.id.imgBroomstick); // 다른 이미지로 변경
			holder.tvFBId = (TextView) convertView.findViewById(R.id.tvFBId);
			
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		holder.tvFBId.setText(friends.get(position).getName());
		
		//초대버튼 클릭 이벤트 처리
		holder.imgInviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    MainActivity mMainActivity = MainApplication.getInstance().getActivity();
				String friend = friends.get(position).getId();
				//	mMainActivity.mSimpleFacebook.invite(friend, "I invite you to use this app", onInviteListener, "secret data");
				// 메시지가 정확하게 전달 되는지는 모르겠네요.
				
				// 로그인 안되어 있을 경우 '취소'처리(이런경우가 없는데...) 
				// 
				mMainActivity.sendInvite(friend, "이(가) 귀하를 초대합니다.(test)", null);
				// 이(가) 귀하를 초대합니다.
				// 이(가) 빗자루 하나를 보냈습니다.
				// 이(가) xxxx 골드를 보냈습니다.
			}
		});
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + friends.get(position).getId() +"/picture";
		
		if(aq.shouldDelay(position, convertView, parent, url)){
		}else{
			aq.id(holder.imgProfile).image(url, true, true);
		}
		
		return convertView;
	}
	
	class Viewholder {
		ImageView imgProfile;
		ImageView imgInviteBtn;
		TextView tvFBId;
	}

}
