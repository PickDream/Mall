package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Objects;

/**
 * 使用JodaTime来保证线程安全
 * @author Maoxin
 * @date 2/12/2019
 */
public final class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String time,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(time);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String formatStr){
        if (Objects.isNull(date)){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String date){
        return strToDate(date,STANDARD_FORMAT);
    }

    public static String dateToStr(Date date){
        return dateToStr(date,STANDARD_FORMAT);
    }
}
