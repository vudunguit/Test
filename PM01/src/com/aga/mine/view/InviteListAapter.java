package com.aga.mine.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.provider.Telephony.Mms.Addr;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.GameScore;
import com.aga.mine.mains.MainActivity;
import com.aga.mine.mains.MainApplication;
import com.aga.mine.mains.R;
import com.aga.mine.util.Util;
import com.androidquery.AQuery;
import com.sromku.simple.fb.entities.Profile;

public class InviteListAapter extends BaseAdapter {
	private Context mContext;
	private List<Profile> notAPlayers; // GameScore로 변환
	private List<GameScore> friends;
	private AQuery mAq;
	
	public InviteListAapter(Context context) {
//		List<GameScore> adfa = new ArrayList<GameScore>(); // 수정중
		mContext = context;
		List<Profile> friends = FacebookData.getinstance().getFriendsInfo();
		List<GameScore> gameScore = FacebookData.getinstance().getGameScore();
		notAPlayers = friends;
		
//		GameScore game; // 수정중
//		for (Profile friend : friends) {
//			game = new GameScore();
//			game.setId(friend.getId());
//			game.setName(friend.getName());
//			adfa.add(game);
//		}
		
		for (GameScore player : gameScore) {
			for (int i = 0; i < friends.size(); i++) {
				if (friends.get(i).getId().equals(player.id)) {
					notAPlayers.remove(i);
					break;
				}
			}
		}
		Log.d("LDK", "adpater, notAPlayers size:" + notAPlayers.size());
		mAq = new AQuery(mContext);
	}
	
	public InviteListAapter(MainActivity mainActivity, ArrayList<GameScore> matchList) {
		friends = matchList;
	}

	@Override
	public int getCount() {
		// friends.getid()에서 gameScore.id를 뺀후 남는 친구만 리스트에 넣습니다.
		return notAPlayers.size();
	}

	@Override
	public String getItem(int position) {
		return notAPlayers.get(position).getId();
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
			convertView = View.inflate(mContext, R.layout.list_invite, null);
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.inviteProfile);
			holder.imgInviteBtn = (ImageView) convertView.findViewById(R.id.inviteButton); // 다른 이미지로 변경
			holder.tvFBId = (TextView) convertView.findViewById(R.id.inviteId);
			
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		holder.tvFBId.setText(notAPlayers.get(position).getName());
		
		//초대버튼 disable 처리
		if(!Util.canInvite(notAPlayers.get(position).getId())) {
			holder.imgInviteBtn.setImageResource(R.drawable.invite_button2ko);
			holder.imgInviteBtn.setEnabled(false);
		}
		
		//초대버튼 클릭 이벤트 처리
		holder.imgInviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    MainActivity mMainActivity = MainApplication.getInstance().getActivity();
				String friend = notAPlayers.get(position).getId();
				//	mMainActivity.mSimpleFacebook.invite(friend, "I invite you to use this app", onInviteListener, "secret data");
				// 메시지가 정확하게 전달 되는지는 모르겠네요.
				
				// 로그인 안되어 있을 경우 '취소'처리(이런경우가 없는데...) 
				// 
				Log.e("InviteListAapter", "Callback_3 - imgInviteBtn.setOnClickListener()");
				mMainActivity.sendInvite(friend, "이(가) 귀하를 초대합니다. _test", null);
				// 이(가) 귀하를 초대합니다.
				// 이(가) 빗자루 하나를 보냈습니다.
				// 이(가) xxxx 골드를 보냈습니다.
			}
		});
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + notAPlayers.get(position).getId() +"/picture";
		
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
