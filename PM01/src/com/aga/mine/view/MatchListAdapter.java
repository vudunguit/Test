package com.aga.mine.view;

import java.io.IOException;
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
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;
import com.aga.mine.util.Util;
import com.androidquery.AQuery;

public class MatchListAdapter extends BaseAdapter {
	private Context mContext;
	private List<GameScore> mGameScore;
	private AQuery mAq;
   
	public MatchListAdapter(Context context, List<GameScore> gameScore) {
		mContext = context;
		mGameScore = 	gameScore;
		final String myID = FacebookData.getinstance().getUserInfo().getId();
//		Log.e("MatchListAdapter", "mGameScore.size() before1 : " + mGameScore.size());
		for (int i = 0; i < mGameScore.size(); i++) {
//			Log.e("MatchListAdapter", "mGameScore.size() before2 : " + mGameScore.size());
			if (mGameScore.get(i).getId().equals(myID)) {
//				Log.e("MatchListAdapter", "mGameScore.size() after : " + mGameScore.size());
				mGameScore.remove(i);
				i--;
			} else {
				try {
//					Log.e("MatchListAdapter", "count i : " + i);
					NetworkController.getInstance().sendRequestIsPlayerConnected(mGameScore.get(i).getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		mAq = new AQuery(mContext);
	}
	
	@Override
	public int getCount() {
		return mGameScore.size();
	}

	@Override
	public String getItem(int position) {
		return mGameScore.get(position).getId();
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
		
		holder.tvFBId.setText(mGameScore.get(position).getName());
		
		// 초대버튼 disable 처리
		// 서버에서 데이터 받는 시간보다 화면 그리는 것이 더 빠르면 못받아오는 경우가 있음. 
		if(!Util.getJoin(mGameScore.get(position).getId())) {
			holder.imgInviteBtn.setImageResource(R.drawable.invite_button2ko);
			holder.imgInviteBtn.setEnabled(false);
		}
		
		//초대버튼 클릭 이벤트 처리
		holder.imgInviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		AQuery aq = mAq.recycle(convertView);
		
		String url = "https://graph.facebook.com/" + mGameScore.get(position).getId() +"/picture";
		
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
