package com.aga.mine.mains;

public class NumberComma {
	
	public NumberComma() {
	}
	
	// String¿¡ comma »ğÀÔ
	public String numberComma(final String string) {
		if (string.length() < 4) 
			return string;
		
		String value = string;
		int len = value.length();
		int position = 3;
		for (int i = 0; i < (len -1) / 3; i++) {
			value = value.substring(0, (value.length() - position)) + "," + value.substring((value.length() - position), value.length());
			position += 4;
		}
		return value;
	}
	
	public String numberComma(final int value) {
		return numberComma("" + value);
	}
	
	
	public String numberComma(final long value) {
		return numberComma("" + value);
	}
	
	
}
