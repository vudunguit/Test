package com.aga.mine.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aga.mine.mains.R;
import com.androidquery.AQuery;

public class BroomstickListAdapter extends BaseAdapter {
	private ArrayList<Broomstick> mBroomstickList;
	private AQuery mAq;
	private Context mContext;
	
	public BroomstickListAdapter(Context context, ArrayList<Broomstick> broomstickList) {
		mBroomstickList = broomstickList;
		mContext = context;
		mAq = new AQuery(mContext);
	}

	@Override
	public int getCount() {
		return mBroomstickList.size();
	}

	@Override
	public Broomstick getItem(int position) {
		return mBroomstickList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Viewholder holder;
		if(convertView == null) {
			holder = new Viewholder();
			convertView = View.inflate(mContext, R.layout.list_broomstick, null);
			holder.profile = (ImageView) convertView.findViewById(R.id.broomstickProfile);
			holder.text = (TextView) convertView.findViewById(R.id.broomstickText);
			holder.image = (ImageView) convertView.findViewById(R.id.broomstickImage);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		//set facebook profile image
		AQuery aq = mAq.recycle(convertView);
		String url = "https://graph.facebook.com/" + mBroomstickList.get(position).sender_id +"/picture";
		if(aq.shouldDelay(position, convertView, parent, url)){

		}else{
			aq.id(holder.profile).image(url, true, true);
		}
		
		//set text
		holder.text.setText("빗자루 " + mBroomstickList.get(0).quantity + "개를 받았습니다.");
		
		return convertView;
	}
	
	class Viewholder {
		ImageView profile;
		TextView text;
		ImageView image;
	}

}
