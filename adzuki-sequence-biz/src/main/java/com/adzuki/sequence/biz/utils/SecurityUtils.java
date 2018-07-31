package com.adzuki.sequence.biz.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {
	
	//获取MD5之后的16进制字符串
	public static String getMD5(String orgStr) {
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(orgStr.getBytes(Charset.defaultCharset()));
					byte[] smd = md.digest();
					return bytesToHexString(smd);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				return null;
	}
	
	//将byte数组转换成16进制字符串
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  

}
