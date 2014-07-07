package org.project.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DateUtils {
	
	public static String date2String(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	public static Date string2Date(String dateString, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return date;
	}

	public static Date obtainIntervalDate(Date date, int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, interval);
		return calendar.getTime();
	}
	
	public static Date obtainRandomHourDate() {
		Calendar calendar = Calendar.getInstance();
		Random random = new Random();
		calendar.set(Calendar.HOUR, random.nextInt(24));
		return calendar.getTime();
	}
}
