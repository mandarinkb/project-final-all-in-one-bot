package com.projectfinalstartbot.controller;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projectfinalstartbot.function.DateTimes;
import com.projectfinalstartbot.function.Elasticsearch;
import com.projectfinalstartbot.function.Log;
import com.projectfinalstartbot.service.ServiceBigC;
import com.projectfinalstartbot.service.ServiceMakroclick;
import com.projectfinalstartbot.service.ServiceTescolotus;
import com.projectfinalstartbot.service.ServiceWeb;



@Component
public class Controller {
	@Autowired
	private Elasticsearch elas;
	
	@Autowired
	private Log log;

    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private ServiceWeb serviceWeb;
    
    @Autowired
    private ServiceBigC bigC;
    
    @Autowired
    private ServiceMakroclick makroclick;
    
    @Autowired
    private ServiceTescolotus tescolotus;
    
    // ดึงข้อมูล cron expression จาก ProjectFinalStartBotApplication Class
    @Scheduled(cron = "#{@cronExpression_1}") 
    public void start() {   
    	String index = null;
    	System.out.println(dateTimes.interDateTime() + " : start all in one bot"); 
    	log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "start bot", "start all in one bot");  
    	// ดึงรายชื่อเว็บทั้งหมดมาก่อน
        for(JSONObject item : serviceWeb.start()) {
            switch(item.get("web_name").toString()) 
            { 
                case "tescolotus": 
                	System.out.println("tescolotus");
                	tescolotus.run(item);
                    break; 
                case "makroclick": 
                	System.out.println("makroclick");
                	makroclick.run(item);
                    break; 
                case "bigc": 
                	System.out.println("bigc");
                	bigC.run(item);
                    break;
                default: 
                    System.out.println("no match web name"); 
            } 
            index = item.get("database").toString();
        }
        log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "stop bot", "stop all in one bot");
        System.out.println(dateTimes.interDateTime() + " : stop all in one bot"); 
        // เมื่อทำเสร็จให้แสดงจำนวนข้อมูลที่กวาดมาได้ลง log
        try {
        	TimeUnit.SECONDS.sleep(30); // หน่วง 30วิ
            String value = elas.findAll(index);
            log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "match all", index+" total "+value);
            System.out.println(dateTimes.interDateTime() + " "+index+" total "+value);
        }catch(Exception e) {
        	System.out.println(e.getMessage());
        } 
    }
}
