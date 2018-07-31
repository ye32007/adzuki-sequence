/**
*Copyright © 2015 <copyright holders>
*
*Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”),
*to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/
*or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
*WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
*COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
*ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.adzuki.sequence.biz.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public final class NetWorkUtil {


    public static  String   ipAddress="127.0.0.1";
    public static  String   macAddress="00-00-00-00-00-00";
    /**
     * 使用static类获取本地ip地址
     */
   static{
		
		Collection<InetAddress> addresses = new ArrayList<InetAddress>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces
						.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterface
						.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					addresses.add(inetAddress);
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		for (InetAddress address : addresses) {
			if (address.isSiteLocalAddress()) {
				ipAddress=address.getHostAddress();
				try {
					macAddress = getMACAddress(address);
					
				} catch (SocketException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		//当都没获取到并且,ipAddress仍然为127.0.0.1时返回错误
		 if("127.0.0.1".equals(ipAddress) || "00-00-00-00-00-00".equals(macAddress))
		 {
			 throw new RuntimeException("请为本机配置IP地址,当前获取到的IP地址为127.0.0.1,无法对外提供序列号服务!");
		 }
	}
   
 //获取MAC地址的方法  
   private static String getMACAddress(InetAddress ia) throws SocketException{  
       //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。  
       byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();  
         
       //下面代码是把mac地址拼装成String  
       StringBuffer sb = new StringBuffer();  
         
       for(int i=0;i<mac.length;i++){  
           if(i!=0){  
               sb.append("-");  
           }  
           //mac[i] & 0xFF 是为了把byte转化为正整数  
           String s = Integer.toHexString(mac[i] & 0xFF);  
           sb.append(s.length()==1?0+s:s);  
       }  
         
       //把字符串所有小写字母改为大写成为正规的mac地址并返回  
       return sb.toString().toUpperCase();  
   }  
	
}
