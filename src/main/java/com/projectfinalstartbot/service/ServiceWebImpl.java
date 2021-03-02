package com.projectfinalstartbot.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalstartbot.dao.Database;
import com.projectfinalstartbot.function.DateTimes;
import com.projectfinalstartbot.function.Elasticsearch;
import com.projectfinalstartbot.function.Log;
import com.projectfinalstartbot.function.Query;
import com.projectfinalstartbot.function.SwitchDatabase;

@Service
public class ServiceWebImpl implements ServiceWeb {
	@Autowired
	private Log log;
	
    @Autowired
    private DateTimes dateTimes;
    
	@Autowired
	private Query q;
	
	@Autowired
	private Elasticsearch elas;
	
    @Autowired
    private SwitchDatabase swdb;
	
    @Autowired
    private Database db;   

    // start bot
    @Override
    public List<JSONObject> start() {
        //switch database
        swdb.switchdb();
        log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "switch database","switch database in elasticsearch");
        
        // select database working and clear data in elasticsearch
        // เลือกเก็บข้อมูลลงที่ database ที่มีสถานะเป็น 0
        String dbName = q.StrExcuteQuery("select DATABASE_NAME from SWITCH_DATABASE where DATABASE_STATUS = '0' ");
        elas.deleteIndex(dbName);
        log.createLog(dateTimes.datetime(),dateTimes.timestamp(), "system", "clear data","clear data in elasticsearch database");
    	
    	List<JSONObject>list = new ArrayList<>();
        JSONObject json ;
        String sql = "select * from WEB where WEB_STATUS = 1";
        try {
            Connection conn = db.connectDatase();
            ResultSet result = db.getResultSet(conn, sql);
            while (result.next()) {
            	json = new JSONObject();
                json.put("web_id", result.getInt("Web_ID"));
                json.put("web_name", result.getString("WEB_NAME"));
                json.put("url", result.getString("WEB_URL"));
                json.put("web_status", result.getString("WEB_STATUS"));
                json.put("icon_url", result.getString("ICON_URL"));
                json.put("database", dbName);
                list.add(json);
            }
            conn.close();
        } catch (SQLException | JSONException e) {
            e.getMessage();
        }  
        return list;
    }
}
