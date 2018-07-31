package com.adzuki.sequence.biz.utils;


public class NumberUtil {
	
	public static String format(int cur, int length){
		String str = ""+cur;
		int q = length - str.length();
		switch (q) {
		case 0:
			break;
		case 1:
			str = "0" + str;
			break;
		case 2:
			str = "00"+str;
			break;
		case 3:
			str = "000"+str;
			break;
		case 4:
			str = "0000"+str;
			break;
		case 5:
			str = "00000"+str;
			break;
		case 6:
			str = "000000"+str;
			break;
		case 7:
			str = "0000000"+str;
			break;
		case 8:
			str = "00000000"+str;
			break;
		case 9:
			str = "000000000"+str;
			break;
		default:
			break;
		}
//		return df.format(cur);
		return str;
	}
	
	
	
	public static String format(long cur, int length){
		String str = ""+cur;
		int q = length - str.length();
		StringBuilder fileBuilder = new StringBuilder();
		for(int i =0 ; i<q ; i++){
			fileBuilder.append("0");
		}
		fileBuilder.append(str);
//		return df.format(cur);
		return fileBuilder.toString();
	}
}
