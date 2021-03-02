package com.projectfinalstartbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.projectfinalstartbot.function.CronExpression;

@SpringBootApplication
@EnableScheduling  //เปิดใช้งาน
public class ProjectFinalStartBotApplication {
    @Autowired
    private CronExpression cron;
	

	public static void main(String[] args) {
		SpringApplication.run(ProjectFinalStartBotApplication.class, args);
	}
	
	// ดึงข้อมูลมาเมื่อ service เริ่มทำงาน
    @Bean
    public String cronExpression_1() {
        return cron.cronExpressionTask_1();
    }

}
