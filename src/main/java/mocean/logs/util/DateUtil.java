package mocean.logs.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	
	private DateUtil() {}
	
	public static String getTransTimeByTimeStamp(long minisecond) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTime(new Date(minisecond));
		String time = dateFormat.format(calendar.getTime());
		return time;
	}
	
	public static String getTransTime(String currentFormat,String nextFormat,String date) {
		SimpleDateFormat currentdf = new SimpleDateFormat(currentFormat,Locale.ENGLISH);
		SimpleDateFormat nextdf = new SimpleDateFormat(nextFormat,Locale.ENGLISH);
		Date tempTime = new Date();
		try {
			tempTime = currentdf.parse(date);
		} catch (ParseException e) {
			logger.error("输入的时间格式与输入的时间不匹配！！！");
		}
		return nextdf.format(tempTime);
	}

	public static String getQueryDay(int i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
		calendar.setTime(new Date());
		if(i!=0) {
			calendar.add(Calendar.DAY_OF_MONTH, -i);
		}
		return sdf2.format(calendar.getTime());
	}
	
	public static double getCurrentUnixTimeStamp() {
		long time = System.currentTimeMillis();
		double nowTimestamp = Double.valueOf(time)/1000;
		return nowTimestamp;
	}
	
	public static String getLastHourTimeStamp() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		return dateFormat.format(calendar.getTime());
	}
	
	public static double getTwoMinuteUnixTimeStamp() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 2);
		long time = calendar.getTimeInMillis();
		double timestamp = Double.valueOf(time)/1000;
		return timestamp;
	}
	
	public static double getTwelveMinuteUnixTimeStamp() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 12);
		long time = calendar.getTimeInMillis();
		double timestamp = Double.valueOf(time)/1000;
		return timestamp;
	}
	
	public static int getNowHour() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getNowDay() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static long getTimeStampByString(String formatDate,String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);  
		Date date = dateFormat.parse(formatDate,pos);
		return date.getTime();
	}
	
	public static String getHourStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
	public static String getDayStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
	public static String getYesterDayStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
	public static String getYesterDayEnd(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	

	public static String getWeekStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
	    int day_of_week = calendar.get(Calendar. DAY_OF_WEEK) - 1;
	    if (day_of_week == 0 ) {
	        day_of_week = 7 ;
	    }
	    calendar.add(Calendar.DATE , -day_of_week + 1 );
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
	public static String getMonthStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
	public static String getYearStart(long i) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = new Date(i);
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		return getTransTimeByTimeStamp(calendar.getTimeInMillis());
	}
	
}
