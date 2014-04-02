package com.aga.mine.mains;

import java.util.Comparator;

public class ClothesComparator implements Comparator {
	public int compare(Object str1, Object str2) {
//		String clothes1 = ((String[])str1)[2];
//		String clothes2 = ((String[])str2)[2];
		Integer clothes1 = Integer.parseInt(((String[])str1)[2]);
		Integer clothes2 = Integer.parseInt(((String[])str2)[2]);
//		return clothes1.compareTo(clothes2);
		return clothes2.compareTo(clothes1);
	}
} // End class.