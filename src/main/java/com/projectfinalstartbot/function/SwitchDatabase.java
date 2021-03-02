package com.projectfinalstartbot.function;

import java.sql.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.projectfinalstartbot.dao.Database;

@Component
public class SwitchDatabase {
    @Autowired
    private Database db;
    
    @Value("${db_1}")
    private String db_1;
    
    @Value("${db_2}")
    private String db_2;
    
    
	public void switchdb() {
	    String sql = "update SWITCH_DATABASE\n" + 
	    		     "set DATABASE_STATUS = \n" + 
	    		     "case when DATABASE_STATUS = '1' then '0' \n" + 
	    		     "     when DATABASE_STATUS = '0' then '1' \n" + 
	    		     "end\n" + 
	    		     "where DATABASE_NAME in ('"+db_1+"','"+db_2+"')";
	    Connection conn = db.connectDatase();
	    db.executeQuery(conn, sql);
	    db.closeConnect(conn);
	}

}
