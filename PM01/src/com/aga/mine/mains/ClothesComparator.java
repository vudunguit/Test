package com.aga.mine.mains;

import java.util.Comparator;

public class ClothesComparator implements Comparator {
	public int compare(Object str1, Object str2) {
		String str1Clothes = ((String[])str1)[2];
		String str2Clothes = ((String[])str2)[2];

		return str1Clothes.compareTo(str2Clothes);

	}
} // End class.