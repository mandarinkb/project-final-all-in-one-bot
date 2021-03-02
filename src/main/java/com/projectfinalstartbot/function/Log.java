package com.projectfinalstartbot.function;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Component
public class Log {
    @Value("${elasticsearch_ip}")
    private String elasticsearch_ip;

	public void createLog(String datetime,String timestamp ,String username, String type, String message ) {
		JSONObject json = new JSONObject();
		json.put("datetime", datetime);
		json.put("timestamp", timestamp);
		json.put("username", username);
		json.put("type",type);
		json.put("message", message);
        try {
            HttpResponse<String> response = Unirest.post(elasticsearch_ip+"web_scrapping_log/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(json.toString())
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }			
	}

}
