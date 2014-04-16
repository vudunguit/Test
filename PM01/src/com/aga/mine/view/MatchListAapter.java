package com.aga.mine.view;

import java.io.IOException;
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
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.R;
import com.aga.mine.util.Util;
import com.androidquery.AQuery;
import com.sromku.simple.fb.entities.Profile;

public class MatchListAapter extends BaseAdapter {
	private Context mContext;
	private List<GameScore> mGameScore;
	private AQuery mAq;
	
	public MatchListAapter(Context context, List<GameScore> gameScore) {
		mContext = context;
		mGameScore = 	gameScore;
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
		
//		//초대버튼 disable 처리
//		if(!Util.canInvite(mGameScore.get(position).getId())) {
//			holder.imgInviteBtn.setImageResource(R.drawable.invite_button2ko);
//			holder.imgInviteBtn.setEnabled(false);
//		}
		
		//초대버튼 클릭 이벤트 처리
		holder.imgInviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// 그사람을 눌러보면 접속하는지 Log로 체크할수 있음. (접속시 : 1, 미접속시 : 0)
					// 현재 아이폰은 체크가 잘 되는데 안드로이드는 안됨.
					// 버튼 누르는걸로 체크하지 않고 버튼 활성화/비활성화로 바꿔야됩니다.
					// 결과값은 networkController.java 
					//353 line 		case kMessageRequestIsPlayerConnected:에서 확인하실수 있습니다.
					NetworkController.getInstance().sendRequestIsPlayerConnected(mGameScore.get(position).getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
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
