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

public class MailListAdapter extends BaseAdapter {
	private ArrayList<MailItem> mMailItemList;
	private AQuery mAq;
	private Context mContext;
	private int mTab;
	
	public MailListAdapter(Context context, ArrayList<MailItem> mailItemList, int tab) {
		mContext = context;
		mAq = new AQuery(mContext);
		mTab = tab;

		//tab => 1:broomstick, 2:gold
		mMailItemList = mailItemList;
	}

	@Override
	public int getCount() {
		return mMailItemList.size();
	}

	@Override
	public MailItem getItem(int position) {
		return mMailItemList.get(position);
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
			convertView = View.inflate(mContext, R.layout.list_mail, null);
			holder.profile = (ImageView) convertView.findViewById(R.id.mailProfile);
			holder.text = (TextView) convertView.findViewById(R.id.mailText);
			holder.image = (ImageView) convertView.findViewById(R.id.mailImage);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		//set facebook profile image
		AQuery aq = mAq.recycle(convertView);
		String url = "https://graph.facebook.com/" + mMailItemList.get(position).sender_id +"/picture";
		if(aq.shouldDelay(position, convertView, parent, url)){

		}else{
			aq.id(holder.profile).image(url, true, true);
		}
		
		//set text and icon
		if(mTab == 1) {
			holder.text.setText("빗자루 " + mMailItemList.get(0).quantity + "개를 받았습니다.");
			holder.image.setImageResource(R.drawable.mail_broomstickbutton1);
		} else {
			holder.text.setText("" + mMailItemList.get(0).quantity + "골드를 받았습니다.");
			holder.image.setImageResource(R.drawable.mail_pumkin);
		}
		
		return convertView;
	}
	
	class Viewholder {
		ImageView profile;
		TextView text;
		ImageView image;
	}

}
