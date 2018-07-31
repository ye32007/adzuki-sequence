package com.adzuki.sequence.biz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {

	/**
	 * 按照yyMMddHHmm格式获取当前时间,共10位字符串
	 * @return yyMMddHHmm
	 */
	public static String getCurrentDateTimeFormatStr() {
		// 设置基础时间为格林威治时间
		TimeZone gmtTz = TimeZone.getTimeZone("GMT");
		// 设置目标时间为中国标准时
		TimeZone desTz = TimeZone.getTimeZone("Asia/Shanghai");
		GregorianCalendar rightNow = new GregorianCalendar(gmtTz);
		Date mydate = rightNow.getTime();
		// 设置时间字符串格式
		SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmm");
		// 设置目的时间时区
		df.setTimeZone(desTz);
		return df.format(mydate);
	}

	/**
	 * 获取当前时间从凌晨00:00分开始的秒数
	 * @throws ParseException
	 */
	public static long getCurrentSecondInDay() throws ParseException {
		// 设置基础时间为格林威治时间
		TimeZone gmtTz = TimeZone.getTimeZone("GMT");
		// 设置目标时间为中国标准时
		TimeZone desTz = TimeZone.getTimeZone("Asia/Shanghai");
		GregorianCalendar rightNow = new GregorianCalendar(gmtTz);
		Date mydate = rightNow.getTime();
		// 设置时间字符串格式
		SimpleDateFormat beginDf = new SimpleDateFormat("yyyyMMdd000000");
		SimpleDateFormat currentDf = new SimpleDateFormat("yyyyMMddHHmmss");
		// 设置目的时间时区
		beginDf.setTimeZone(desTz);
		currentDf.setTimeZone(desTz);
		String beginDfStr=beginDf.format(mydate);
		String currentDfStr=(currentDf.format(mydate));
		Date beginDate=new SimpleDateFormat("yyyyMMddHHmmss").parse(beginDfStr);
		Date currentDate=new SimpleDateFormat("yyyyMMddHHmmss").parse(currentDfStr);
		return ((currentDate.getTime()-beginDate.getTime())/1000);
	}
	
	
	/**
	 * 获取当前时间从1970年00:00:00开始的分钟数转化为毫秒数
	 * @throws ParseException
	 */
	public static long getCurrentMinenusInDay() throws ParseException {
		// 设置基础时间为格林威治时间
		TimeZone gmtTz = TimeZone.getTimeZone("GMT");
		// 设置目标时间为中国标准时
		TimeZone desTz = TimeZone.getTimeZone("Asia/Shanghai");
		GregorianCalendar rightNow = new GregorianCalendar(gmtTz);
		Date mydate = rightNow.getTime();
		// 设置时间字符串格式
		SimpleDateFormat beginDf = new SimpleDateFormat("yyyyMMddHHmm00");
		SimpleDateFormat currentDf = new SimpleDateFormat("yyyyMMddHHmm00");
		// 设置目的时间时区
		beginDf.setTimeZone(desTz);
		currentDf.setTimeZone(desTz);
		String beginDfStr="19700101000000";
		String currentDfStr=(currentDf.format(mydate));
		Date beginDate=new SimpleDateFormat("yyyyMMddHHmmss").parse(beginDfStr);
		Date currentDate=new SimpleDateFormat("yyyyMMddHHmmss").parse(currentDfStr);
		return ((currentDate.getTime()-beginDate.getTime())/1000/60);
	}
	
	/**
	 * 获取当前时间从1970年00:00:00开始的分钟数
	 * @throws ParseException
	 */
	public static long getCurrentMinenusFrom1970() throws ParseException {
		return System.currentTimeMillis()/1000/60;
	}
	
	public static int getCurrentSecondOfDay(){
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.HOUR_OF_DAY)*60*60 + now.get(Calendar.MINUTE)*60 + now.get(Calendar.SECOND);
	}
	
	public static long getCurrentDate(){
		Calendar now = Calendar.getInstance();
		String result = (now.get(Calendar.YEAR)-2000) + NumberUtil.format((now.get(Calendar.MONTH)+1), 2) + NumberUtil.format(now.get(Calendar.DAY_OF_MONTH), 2);
		return Long.parseLong(result);
	}
	
	public static long getCurrentHour(){
		Calendar now = Calendar.getInstance();
		String result = (now.get(Calendar.YEAR)-2000) + NumberUtil.format((now.get(Calendar.MONTH)+1), 2) + NumberUtil.format(now.get(Calendar.DAY_OF_MONTH), 2) + NumberUtil.format(now.get(Calendar.HOUR_OF_DAY), 2);
		return Long.parseLong(result);
	}
	
	public static long getCurrentMinute(){
		Calendar now = Calendar.getInstance();
		String result = (now.get(Calendar.YEAR)-2000) + NumberUtil.format((now.get(Calendar.MONTH)+1), 2) + NumberUtil.format(now.get(Calendar.DAY_OF_MONTH), 2) + NumberUtil.format(now.get(Calendar.HOUR_OF_DAY), 2) + NumberUtil.format(now.get(Calendar.MINUTE), 2);
		return Long.parseLong(result);
	}
	
	
	public static String getCurrentSecond(){
		Calendar now = Calendar.getInstance();
		String result = (now.get(Calendar.YEAR)-2000) + NumberUtil.format((now.get(Calendar.MONTH)+1), 2) + NumberUtil.format(now.get(Calendar.DAY_OF_MONTH), 2) + NumberUtil.format(now.get(Calendar.HOUR_OF_DAY), 2) + NumberUtil.format(now.get(Calendar.MINUTE), 2) + NumberUtil.format(now.get(Calendar.SECOND), 2);
		return result;
	}
	
	public static boolean isToDay(long millis){
		Calendar now = Calendar.getInstance();
		Calendar other = Calendar.getInstance();
		other.setTimeInMillis(millis);
		return now.get(Calendar.YEAR)==other.get(Calendar.YEAR) && now.get(Calendar.MONTH)==other.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH)==other.get(Calendar.DAY_OF_MONTH);
	}
	
	public static boolean isToHour(long millis){
		Calendar now = Calendar.getInstance();
		Calendar other = Calendar.getInstance();
		other.setTimeInMillis(millis);
		return now.get(Calendar.YEAR)==other.get(Calendar.YEAR) && now.get(Calendar.MONTH)==other.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH)==other.get(Calendar.DAY_OF_MONTH) && now.get(Calendar.HOUR_OF_DAY)==other.get(Calendar.HOUR_OF_DAY);
	}
	
	public static boolean isToMinute(long millis){
		Calendar now = Calendar.getInstance();
		Calendar other = Calendar.getInstance();
		other.setTimeInMillis(millis);
		return now.get(Calendar.YEAR)==other.get(Calendar.YEAR) && now.get(Calendar.MONTH)==other.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH)==other.get(Calendar.DAY_OF_MONTH) && now.get(Calendar.HOUR_OF_DAY)==other.get(Calendar.HOUR_OF_DAY) && now.get(Calendar.MINUTE)==other.get(Calendar.MINUTE);
	}
	
	

	/**
	 * 获取当前时间从凌晨00:00分开始的秒数
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
//		try {
//			long now = System.currentTimeMillis();
//			
//			for(int i=0; i<100;i++){
//				Thread a= new Thread(){
//					public void run(){
//						for(int i=0; i<1000;i++){
//							long a = System.currentTimeMillis()/1000/60/60/24;
//						}
//					}
//				};
//				a.start();
//				a.join();
//			}
//			long end = System.currentTimeMillis();
//			System.out.println("======"+(end-now));
//			return;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		Calendar now = Calendar.getInstance();
		System.out.println(""+(now.get(Calendar.YEAR)-2000));
		System.out.println(""+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)+now.get(Calendar.SECOND));
		System.out.println((now.get(Calendar.HOUR_OF_DAY)+1)*(now.get(Calendar.MINUTE)+1)*(now.get(Calendar.SECOND)+1));
		
		System.out.println(getCurrentDate());
	}
	
}
