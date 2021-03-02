package com.projectfinalstartbot.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.springframework.stereotype.Service;

@Service
public class DateTimes {

	private static final DateTimeFormatter ts = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");//yyyy/MM/dd HH:mm:ss
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  //yyyy/MM/dd HH:mm:ss

    public String timestamp() {
    	return ZonedDateTime.now().format(ts);
    }
    
    public String datetime() {
    	return ZonedDateTime.now().format(dt);
    }
    
    public String interDateTime() {  //คศ + เวลา
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public String thaiDateTime() {
        java.util.Date date = new java.util.Date();  //พศ + เวลา
        return df.format(date);
    }

    public String interDate() {  //คศ 
        LocalDate localDate = LocalDate.now();
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate);
    }

    public String newInterDate() {  //คศ 
        LocalDate localDate = LocalDate.now();
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
    }
    
    public String timeNow() {
        java.util.Date time = new java.util.Date(System.currentTimeMillis());
        return new SimpleDateFormat("HH:mm:ss").format(time);
    }

    public String getThaiYesterdayDate() { // พศ
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(cal.getTime());
    }

    public String getInterYesterdayDate() { // คศ
        LocalDate yesterday = LocalDate.now().minusDays(1L);
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(yesterday);
    }
    
    public String ddmmyyyyToyyyymmdd(String inputDate) {
        String day = inputDate.substring(0, 2);
        String month = inputDate.substring(3, 5);
        String year = inputDate.substring(6, 10);
        String mix = year + "-" + month + "-" + day;
        return mix;
    }
    public String getInterDate(String inputDate) {
        String[] splitStr = inputDate.split("\\s+");
        //วันที่ 
        String day = splitStr[1];
        if ("1".equals(day)) {
            day = "01";
        } else if ("2".equals(day)) {
            day = "02";
        } else if ("3".equals(day)) {
            day = "03";
        } else if ("4".equals(day)) {
            day = "04";
        } else if ("5".equals(day)) {
            day = "05";
        } else if ("6".equals(day)) {
            day = "06";
        } else if ("7".equals(day)) {
            day = "07";
        } else if ("8".equals(day)) {
            day = "08";
        } else if ("9".equals(day)) {
            day = "09";
        }
        //เดือน
        String month = splitStr[2];
        if ("มกราคม".equals(month)) {
            month = "01";
        } else if ("กุมภาพันธ์".equals(month)) {
            month = "02";
        } else if ("มีนาคม".equals(month)) {
            month = "03";
        } else if ("เมษายน".equals(month)) {
            month = "04";
        } else if ("พฤษภาคม".equals(month)) {
            month = "05";
        } else if ("มิถุนายน".equals(month)) {
            month = "06";
        } else if ("กรกฎาคม".equals(month)) {
            month = "07";
        } else if ("สิงหาคม".equals(month)) {
            month = "08";
        } else if ("กันยายน".equals(month)) {
            month = "09";
        } else if ("ตุลาคม".equals(month)) {
            month = "10";
        } else if ("พฤศจิกายน".equals(month)) {
            month = "11";
        } else if ("ธันวาคม".equals(month)) {
            month = "12";
        }
        //ปี
        int y = Integer.parseInt(splitStr[3]) - 543;//แปลงเป็น คศ
        String year = Integer.toString(y);
        return year + "-" + month + "-" + day;
    }
}
