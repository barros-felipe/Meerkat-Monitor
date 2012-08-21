/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2011 Merkat-Monitor
 * mailto: contact AT meerkat-monitor DOT org
 * 
 * Meerkat Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Meerkat Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Meerkat Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meerkat.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

public class DateUtil {
	private static Logger log = Logger.getLogger(DateUtil.class);

	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_FORMAT_RSS_ITEM = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private int year, month, day, hour, minute, second;

	/**
	 * now
	 * 
	 * @return
	 */
	public final String now() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();

		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		// Use HOUR_OF_DAY to get 24H clock
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		second = cal.get(Calendar.SECOND);

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		try {
			date = sdf.parse(year + "-" + month + "-" + day + " " + hour + ":" 
					+ minute + ":" + second);
		} catch (ParseException e) {
			log.error("Parsing date ", e);
		}

		return sdf.format(date);
	}

	/**
	 * getFormatedDate
	 * 
	 * @param date
	 *            in format yyyy, MM, dd, hh, mm
	 * @return
	 */
	public final String getFormatedDateGWT(String date) {
		// GWT receives data in format: "yyyy, M, dd, HH, mm"

		// Convert the string to date
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
		Date converted = null;
		try {
			converted = df.parse(date);
		} catch (ParseException e) {
			log.error("Parsing date "+date, e);
		}

		// String convertedDateGWT = converted.toString();
		Calendar calGWT = Calendar.getInstance();
		calGWT.setTime(converted);

		year = calGWT.get(Calendar.YEAR);
		month = calGWT.get(Calendar.MONTH);
		day = calGWT.get(Calendar.DAY_OF_MONTH);
		// Use HOUR_OF_DAY to get 24H clock
		hour = calGWT.get(Calendar.HOUR_OF_DAY);
		minute = calGWT.get(Calendar.MINUTE);

		return year + ", " + month + ", " + day + ", " + hour + ", " + minute;
	}

	/**
	 * getFormatedDate
	 * 
	 * @param Date
	 *            date
	 * @return formated date: yyyy-MM-dd HH:mm:ss
	 */
	public final Date getFormatedDate(String date) {
		// Convert the string to date
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
		Date converted = null;
		try {
			converted = df.parse(date);
		} catch (ParseException e) {
			log.error("Parsing date ", e);

		}
		return converted;
	}

	/**
	 * getFormatedDateRSS
	 * 
	 * @param date
	 * @return
	 */
	public final String getFormatedDateRSS() {
		// RSS data in format: "Tue, 11 May 2010 14:32:23 -0500"
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_RSS_ITEM,
				Locale.getDefault());
		String newDate = formatter.format(new Date());

		return newDate;

	}

}
