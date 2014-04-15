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
	private String[] emoticons; 
	public EmoticonAdapter(Context context) {
		mContext = context;
		// 이모티콘 ","로 구분된 String을 잘라서 String배열에 저장
		emoticons = FacebookData.getinstance().getDBData("Emoticons").split(",");
		
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
		
		Log.e("EmoticonAdapter", "position[" + position + "]");
		
		// 자신의 이모티콘 목록을 현재 포지션과 비교(몇번째 배열에 저장되어있는지 몰라서 for문 돌립니다.)
		// list로 키값 같이 넣어서 돌리는건 어떨까요? (없는것은 키는 순서대로, 값은 0으로)
		for (String emoticonData : emoticons) {
			// 하나하나 비교. (비교 방식이 좋지 않은것 같네요.. ㅠㅠ)
			if (Integer.parseInt(emoticonData) == position + 1) {
				holder.checked.setVisibility(View.VISIBLE);
				break;
			} else {
				holder.checked.setVisibility(View.INVISIBLE);
			}
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