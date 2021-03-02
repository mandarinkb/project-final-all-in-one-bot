package com.projectfinalstartbot.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projectfinalstartbot.function.DateTimes;
import com.projectfinalstartbot.function.Log;
import com.projectfinalstartbot.service.ServiceBigC;
import com.projectfinalstartbot.service.ServiceMakroclick;
import com.projectfinalstartbot.service.ServiceTescolotus;
import com.projectfinalstartbot.service.ServiceWeb;

@Component
public class Controller {
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
        }
        log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "stop bot", "stop all in one bot");
    }
}
