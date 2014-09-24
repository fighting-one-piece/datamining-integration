package org.project.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.project.common.enums.SimpleDateFormatEnum;

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

	public static String getTime(long time, SimpleDateFormat format) {
		java.util.Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time);

		return format.format(calendar.getTime());
	}

	public static String getToday() {
		return SimpleDateFormatEnum.dateFormat.get().format(new Date());
	}

	public static String getYesterday() {
		java.util.Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(5, -1);
		return SimpleDateFormatEnum.dateFormat.get().format(calendar.getTime());
	}

	public static boolean runMonthlyJob(String date) {
		java.util.Calendar today = java.util.Calendar.getInstance();
		if ((null != date) && (!"".equals(date))) {
			try {
				today.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));
				today.add(5, 1);
			} catch (ParseException e) {
				return false;
			}
		}
		int d = today.get(5);
		return d == 1;
	}

	public static boolean runQuarterlyJob(String date) {
		java.util.Calendar today = java.util.Calendar.getInstance();
		if ((null != date) && (!"".equals(date))) {
			try {
				today.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));
				today.add(5, 1);
			} catch (ParseException e) {
				return false;
			}
		}
		int m = today.get(2);
		int d = today.get(5);
		return (d == 1) && (m % 3 == 0);
	}

	public static List<String> getLatestDateList(String date, int n)
			throws ParseException {
		List<String> resultList = new ArrayList<String>();
		Date toDate = SimpleDateFormatEnum.dateFormat.get().parse(date);
		java.util.Calendar toGc = GregorianCalendar.getInstance();
		toGc.setTime(toDate);
		java.util.Calendar gc = GregorianCalendar.getInstance();
		gc.setTime(toDate);
		int count = 0;
		for (;;) {
			count++;
			if (count > n)
				break;
			gc.add(5, -1);
			Date temp = gc.getTime();
			resultList.add(SimpleDateFormatEnum.dateFormat.get().format(temp));
		}
		return resultList;
	}

	public static String getTheDayBefore(String date, int n)
			throws ParseException {
		Date inDate = SimpleDateFormatEnum.dateFormat.get().parse(date);
		java.util.Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(inDate);
		calendar.add(5, -n);
		return SimpleDateFormatEnum.dateFormat.get().format(calendar.getTime());
	}

	public static List<String> getDateList(String from, String to)
			throws ParseException {
		List<String> resultList = new ArrayList<String>();
		System.out.println("from=" + from + "\nto=" + to);
		Date fromDate = SimpleDateFormatEnum.dateFormat.get().parse(from);
		Date toDate = SimpleDateFormatEnum.dateFormat.get().parse(to);
		java.util.Calendar gc = GregorianCalendar.getInstance();
		gc.setTime(toDate);
		java.util.Calendar fromGc = GregorianCalendar.getInstance();
		fromGc.setTime(fromDate);
		while ((gc.after(fromGc)) || (gc.equals(fromGc))) {
			Date temp = gc.getTime();
			resultList.add(SimpleDateFormatEnum.dateFormat.get().format(temp));
			gc.add(5, -1);
		}
		return resultList;
	}

	public static List<String> getMonthDateList(String date)
			throws ParseException {
		Date dateTemp = SimpleDateFormatEnum.dateFormat.get().parse(date);
		java.util.Calendar from = GregorianCalendar.getInstance();
		from.setTime(dateTemp);
		from.set(5, 1);

		java.util.Calendar to = GregorianCalendar.getInstance();
		to.setTime(dateTemp);
		int lastDay = to.getActualMaximum(5);
		to.set(5, lastDay);

		return getDateList(
				SimpleDateFormatEnum.dateFormat.get().format(from.getTime()),
				SimpleDateFormatEnum.dateFormat.get().format(to.getTime()));
	}

	public static List<String> getQuarterDateList(String date)
			throws ParseException {
		java.util.Calendar from = GregorianCalendar.getInstance();
		from.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));
		java.util.Calendar to = GregorianCalendar.getInstance();
		to.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));

		int month = from.get(2);
		int quarter = month / 3;

		from.set(2, quarter * 3);
		from.set(5, 1);
		to.set(2, quarter * 3 + 2);
		int lastDay = to.getActualMaximum(5);
		to.set(5, lastDay);

		return getDateList(
				SimpleDateFormatEnum.dateFormat.get().format(from.getTime()),
				SimpleDateFormatEnum.dateFormat.get().format(to.getTime()));
	}

	public static long getMonthBeginTime(String date) throws ParseException {
		Date dateTemp = SimpleDateFormatEnum.dateFormat.get().parse(date);

		java.util.Calendar c = GregorianCalendar.getInstance();
		c.setTime(dateTemp);
		c.set(5, 1);
		c.set(10, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);

		return c.getTimeInMillis();
	}

	public static long getMonthEndTime(String date) throws ParseException {
		Date dateTemp = SimpleDateFormatEnum.dateFormat.get().parse(date);

		java.util.Calendar c = GregorianCalendar.getInstance();
		c.setTime(dateTemp);
		c.add(2, 1);
		c.set(5, 1);
		c.set(10, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);

		return c.getTimeInMillis();
	}

	public static long getQuarterBeginTime(String date) throws ParseException {
		java.util.Calendar c = GregorianCalendar.getInstance();
		c.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));

		int month = c.get(2);
		int quarter = month / 3;

		c.set(2, quarter * 3);
		c.set(5, 1);
		c.set(10, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);

		return c.getTimeInMillis();
	}

	public static long getQuarterEndTime(String date) throws ParseException {
		java.util.Calendar c = GregorianCalendar.getInstance();
		c.setTime(SimpleDateFormatEnum.dateFormat.get().parse(date));

		int month = c.get(2);
		int quarter = month / 3;

		c.set(2, quarter * 3);
		c.add(2, 3);
		c.set(5, 1);
		c.set(10, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);

		return c.getTimeInMillis();
	}

	@SuppressWarnings("deprecation")
	public static Date[] getLastMonthStartEndDate() {
		java.util.Calendar cc = java.util.Calendar.getInstance();
		return getMonthStartEndDate(cc.getTime().getYear() + 1900 + "", cc
				.getTime().getMonth() + "");
	}

	@SuppressWarnings("deprecation")
	public static Date[] getLastQuartStartEndDate() {
		java.util.Calendar cc = java.util.Calendar.getInstance();
		String index = Math.round(Math.ceil((cc.getTime().getMonth() + 1) / 3))
				+ "";
		return getQuartStartEndDate(cc.getTime().getYear() + 1900 + "", index);
	}

	@SuppressWarnings("deprecation")
	public static Date[] getStartEndDate(String defDate) {
		if (defDate.indexOf("M") == 0) {
			if (defDate.indexOf("-") == 1) {
				String temp = defDate.substring(2, defDate.length());
				String[] array = temp.split("-");
				if ((array != null) && (array.length == 2)) {
					return getMonthStartEndDate(array[0], array[1]);
				}
				return null;
			}
			if (defDate.toLowerCase().indexOf("last") == 1) {
				java.util.Calendar cc = java.util.Calendar.getInstance();
				return getMonthStartEndDate(cc.getTime().getYear() + 1900 + "",
						cc.getTime().getMonth() + "");
			}
			return null;
		}
		if (defDate.indexOf("Q") == 0) {
			if (defDate.indexOf("-") == 1) {
				String temp = defDate.substring(2, defDate.length());
				String[] array = temp.split("-");
				if ((array != null) && (array.length == 2)) {
					return getQuartStartEndDate(array[0], array[1]);
				}
				return null;
			}
			if (defDate.toLowerCase().indexOf("last") == 1) {
				java.util.Calendar cc = java.util.Calendar.getInstance();
				String index = Math.round(Math
						.ceil((cc.getTime().getMonth() + 1) / 3)) + "";
				return getQuartStartEndDate(cc.getTime().getYear() + 1900 + "",
						index);
			}
			return null;
		}
		return null;
	}

	private static Date[] getQuartStartEndDate(String year, String quart) {
		try {
			String[] startEndMonth = getQuartStartEndDate(quart);
			if ((startEndMonth == null) || (startEndMonth.length != 2)) {
				return null;
			}
			Date startDate = getMonthStartDate(year, startEndMonth[0]);
			if (startDate == null) {
				return null;
			}
			Date endDate = getMonthEndDate(year, startEndMonth[1]);
			if (endDate != null) {
				return new Date[] { startDate, endDate };
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private static Date[] getMonthStartEndDate(String year, String month) {
		try {
			Date startDate = getMonthStartDate(year, month);
			if (startDate == null) {
				return null;
			}
			Date endDate = getMonthEndDate(year, month);
			if (endDate != null) {
				return new Date[] { startDate, endDate };
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private static String[] getQuartStartEndDate(String quart) {
		try {
			int quarter = Integer.parseInt(quart);
			if ((quarter < 1) || (quarter > 4)) {
				return null;
			}
			return new String[] { quarter * 3 - 2 + "", quarter * 3 + "" };
		} catch (Exception e) {
		}
		return null;
	}

	private static Date getMonthStartDate(String year, String month) {
		try {
			java.util.Calendar calendar = new GregorianCalendar();
			calendar.set(1, Integer.parseInt(year));
			calendar.set(2, Integer.parseInt(month) - 1);
			calendar.set(5, 1);
			calendar.set(11, 0);
			calendar.set(12, 0);
			calendar.set(13, 0);
			return calendar.getTime();
		} catch (Exception e) {
		}
		return null;
	}

	private static Date getMonthEndDate(String year, String month) {
		try {
			java.util.Calendar calendar = new GregorianCalendar();
			calendar.set(1, Integer.parseInt(year));
			calendar.set(2, Integer.parseInt(month));
			calendar.set(5, 0);
			calendar.set(11, 23);
			calendar.set(12, 59);
			calendar.set(13, 59);
			return calendar.getTime();
		} catch (Exception e) {
		}
		return null;
	}

	public static long getDayStartTime(String date) {
		try {
			Date dateTemp = SimpleDateFormatEnum.dateFormat.get().parse(date);

			java.util.Calendar c = GregorianCalendar.getInstance();
			c.setTime(dateTemp);
			c.set(11, 0);
			c.set(12, 0);
			c.set(13, 0);

			return c.getTimeInMillis();
		} catch (Exception e) {
		}
		return 0L;
	}

	public static long getDayEndTime(String date) {
		try {
			Date dateTemp = SimpleDateFormatEnum.dateFormat.get().parse(date);

			java.util.Calendar c = GregorianCalendar.getInstance();
			c.setTime(dateTemp);
			c.set(11, 23);
			c.set(12, 59);
			c.set(13, 59);
			return c.getTimeInMillis();
		} catch (Exception e) {
		}
		return 0L;
	}

}

