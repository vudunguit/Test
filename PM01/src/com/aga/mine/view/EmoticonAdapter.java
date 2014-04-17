package com.aga.mine.view;

import java.util.ArrayList;

import com.aga.mine.mains.FacebookData;
import com.aga.mine.mains.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmoticonAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Item> mItemList = new ArrayList<Item>();
	
	public EmoticonAdapter(Context context) {
		mContext = context;
		// 이모티콘 ","로 구분된 String을 잘라서 String배열에 저장
		String[] emoticons = FacebookData.getinstance().getDBData("Emoticons").split(",");
		ArrayList<String> emoticonList = new ArrayList<String>();
		for(String emoticonData : emoticons) {
			emoticonList.add(emoticonData);
			Log.d("LDK", "checked emoticon: " + emoticonData);
		}
		
		for(int i=0; i<48; i++) {
			Item item = new Item();
			item.emoticon = R.drawable.emoticon_01 + i;
			item.checked = emoticonList.contains(String.valueOf(i+1)) ? true : false;
			mItemList.add(item);
		}
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
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
			convertView = View.inflate(mContext, R.layout.grid_emoticon, null);
			holder.emoticon = (ImageView) convertView.findViewById(R.id.imgEmoticon);
			holder.checked = (ImageView) convertView.findViewById(R.id.imgChecked);
			holder.selected = (ImageView) convertView.findViewById(R.id.imgSelected);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}

		holder.emoticon.setImageResource(mItemList.get(position).emoticon);
		
		if(mItemList.get(position).checked) {
			holder.checked.setVisibility(View.VISIBLE);
			holder.emoticon.setEnabled(false);
		} else {
			holder.checked.setVisibility(View.INVISIBLE);
			holder.emoticon.setEnabled(true);
		}
		
		holder.emoticon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 
				String emoticons = FacebookData.getinstance().getDBData("Emoticons");
				Log.e("EmoticonAdapter", "emoticons [" +emoticons + "] + " + (position+1));
				emoticons += "," + (position+1);
				Log.e("EmoticonAdapter", "emoticons : " + emoticons);
				FacebookData.getinstance().modDBData("Emoticons", emoticons);
			}
		});
		
		return convertView;
	}
	
	class Item {
		int emoticon;
		boolean checked;
	}
	
	class Viewholder {
		ImageView emoticon;
		ImageView checked;
		ImageView selected;
	}
}