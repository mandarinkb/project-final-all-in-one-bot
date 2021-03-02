package com.projectfinalstartbot.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CronExpression {
    @Autowired
    private Query query;
        
    public String cronExpressionTask_1(){
       return query.StrExcuteQuery("select CRON_EXPRESSION from SCHEDULE where PROJECT_NAME = 'project-final-start-bot' and METHOD_NAME = 'start'"); 
    }
}
