package com.ks0100.common.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtil {

	public static int daysBetween(DateTime startDate ,DateTime endDate) {
		DateTimeFormatter format2 = DateTimeFormat .forPattern("yyyy/MM/dd"); 
		String startDateTime = startDate.toString("yyyy/MM/dd");
		String endDateTime = endDate.toString("yyyy/MM/dd");
		DateTime start = DateTime.parse(startDateTime, format2);   
		DateTime end = DateTime.parse(endDateTime, format2);     
		int day = Days.daysBetween(start, end).getDays();  
		day = day == 0?1:day;
		return day;
	} 
}
