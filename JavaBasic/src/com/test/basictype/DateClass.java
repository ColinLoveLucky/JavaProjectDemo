package com.test.basictype;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateClass {
	public void testDate() {
		Date date = new Date();
		System.out.println("date is Show:"+date.toString());
		System.out.println("date is Show3:"+date.toInstant());
		System.out.println("date timeSpan:"+date.getTime());
		Instant instant=Instant.now();
		System.out.println("now time is:"+instant);
		SimpleDateFormat simpleDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		System.out.println("date format is:"+simpleDate.format(new Date()));
		System.out.println("date format is timespan:"+simpleDate.format(new Date(date.getTime())));
	}
}
