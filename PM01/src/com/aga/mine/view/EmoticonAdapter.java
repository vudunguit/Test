package com.aga.mine.view;

import java.util.ArrayList;

import com.aga.mine.mains.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmoticonAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Item> mItemList = new ArrayList<Item>();
	
	public EmoticonAdapter(Context context) {
		mContext = context;
		
		for(int i=0; i<48; i++) {
			Item item = new Item();
			item.emoticon = R.drawable.emoticon_01 + i;
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
	public View getView(int position, View convertView, ViewGroup parent) {
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
		
		return convertView;
	}
	
	class Item {
		int emoticon;
	}
	
	class Viewholder {
		ImageView emoticon;
		ImageView checked;
		ImageView selected;
	}
}