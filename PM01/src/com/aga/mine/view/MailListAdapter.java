package com.aga.mine.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aga.mine.mains.Constant;
import com.aga.mine.mains.DataFilter;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Viewholder holder;
		if(convertView == null) {
			holder = new Viewholder();
			convertView = View.inflate(mContext, R.layout.list_mail, null);
			holder.profile = (ImageView) convertView.findViewById(R.id.mailProfile);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.text = (TextView) convertView.findViewById(R.id.mailMessage);
			holder.image = (ImageView) convertView.findViewById(R.id.mailImage);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		
		//아이템 받기 클릭 이벤트 처리
		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 일반 버튼같이 down상태에서는 이미지 변경이 가능할까요?
				String serialNumber = mMailItemList.get(position).serial_number;
				if (!serialNumber.equals("")) {
					DataFilter.deleteMail(serialNumber);
					if (mTab == Constant.MAIL_TAB_BROOM) {
						// 빗자루 화면 갱신  
						holder.image.setImageResource(R.drawable.mail_broomstickbutton2);
						mMailItemList.remove(position);
						MailListAdapter.this.notifyDataSetChanged();
					} else {
						// 골드 화면 갱신
						holder.image.setImageResource(R.drawable.mail_giftbutton2);
						mMailItemList.remove(position);
						MailListAdapter.this.notifyDataSetChanged();
					}
				}
			}
		});
		
		String senderID = mMailItemList.get(position).sender_id;
		final int pumpkinMine = 1; 

		holder.tvDate.setText(mMailItemList.get(position).date);
		
		//set facebook profile image
		AQuery aq = mAq.recycle(convertView);
		
		// id대신 name을 얻어야 되네요.(친구가 아닐 수 있습니다.)
		if (senderID.equals(String.valueOf(pumpkinMine))) {
			holder.tvName.setText("Pumpkin Mine");	
			aq.id(holder.profile).image(R.drawable.mail_pumkin);
		} else {
			holder.tvName.setText(mMailItemList.get(position).sender_id);
			String url = "https://graph.facebook.com/" + senderID +"/picture";
			if(aq.shouldDelay(position, convertView, parent, url)){
	
			}else{
				aq.id(holder.profile).image(url, true, true);
			}
		
		}
		
		//set text and icon
		if(mTab == 1) {
			holder.text.setText("빗자루 " + mMailItemList.get(position).quantity + "개를 받았습니다.");
			holder.image.setImageResource(R.drawable.mail_broomstickbutton1);
		} else {
			holder.text.setText("" + mMailItemList.get(position).quantity + "골드를 받았습니다.");
			holder.image.setImageResource(R.drawable.mail_giftbutton1);
		}
		
		return convertView;
	}
	
	class Viewholder {
		ImageView profile;
		TextView tvName;
		TextView tvDate;
		TextView text;
		ImageView image;
	}

}
