package com.projectfinalstartbot.function;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Component
public class Elasticsearch {
    @Value("${elasticsearch_ip}")
    private String elasticsearch_ip;
    
    @Value("${index_web_scrapping_category}")
    private String index_category;

    public void inputElasticsearch(String body,String index) {
        try {
            HttpResponse<String> response = Unirest.post(elasticsearch_ip+index+"/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void deleteIndex(String index) {
        try {
            HttpResponse<String> response = Unirest.delete(elasticsearch_ip+index)
                    .header("Accept", "*/*")
                    .header("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String findAll(String index) {
        String elsValue = null;
        try {
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post(elasticsearch_ip+index+"/_search")
        	  .header("Content-Type", "application/json")
        	  .body("{ \"query\": {\"match_all\": {}}}")
        	  .asString();

            elsValue = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        JSONObject objResultsValue = new JSONObject(elsValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONObject objTotal = objHits.getJSONObject("total");
    	return String.valueOf(objTotal.getInt("value"));	
    }
    
    // จัดหมวดหมู่
    public String getCategory(String category) {
        String elsValue = null;
        try {
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post(elasticsearch_ip+index_category+"/_search")
        	  .header("Content-Type", "application/json")
        	  .body("{\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"tag\": \""+category+"\"}}}}}")
        	  .asString();

            elsValue = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        JSONObject objResultsValue = new JSONObject(elsValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");

        String newCategory = null;
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject objSource = arrHits.getJSONObject(i).getJSONObject("_source");	            
            newCategory = objSource.getString("category");
        }
    	return newCategory;	
    }
    public String bigCApi(String cateId ,String page) {
    	String elsValue = null;
    	try {
    		Unirest.setTimeouts(0, 0);
    		HttpResponse<String> response = Unirest.post("https://www.bigc.co.th/api/categories/getproductListBycateId?_store=2&stock_id=1")
    		  .header("Content-Type", "application/json")
    		  .header("Cookie", "__cfduid=d2a52d4a4656e2822d5fa36c91c494f2c1590843646; __cfruid=2cc422a0cc7785dccdce2bc2e0546577f16d6f00-1591341055")
    		  .body("{\"cate_id\": \""+cateId+"\","
    		  		+ "\"ignore_items\": \"\","
    		  		+ "\"page_no\": "+page+","
    		  		+ "\"page_size\": 36,"
    		  		+ "\"selected_categories\": \"\","
    		  		+ "\"selected_brands\": \"\","
    		  		+ "\"sort_by\": \"bestsellers:desc\","
    		  		+ "\"price_from\": \"\","
    		  		+ "\"price_to\": \"\","
    		  		+ "\"filter\": [],"
    		  		+ "\"stock_id\": 1}")
    		  .asString();

    		elsValue = response.getBody();
    	}catch (Exception ex) {
        	System.out.println(ex.getMessage());
        }
    	
    	return elsValue;
    }
    public String makroApi(String menuId ,String page) {
        String elsValue = null;
        try {
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post("https://ocs-prod-api.makroclick.com/next-product/public/api/product/search")
        	  .header("Content-Type", "application/json")
        	  .body("{\"locale\":\"th_TH\","
        	  		+ "\"minPrice\":null,"
        	  		+ "\"maxPrice\":null,"
        	  		+ "\"menuId\":"+menuId+","
        	  		+ "\"hierarchies\":[],"
        	  		+ "\"customerType\":\"MKC\","
        	  		+ "\"page\":"+page+","
        	  		+ "\"pageSize\":32,"
        	  		+ "\"sorting\":\"SORTING_LAST_UPDATE\","
        	  		+ "\"reloadPrice\":true}")
        	  .asString();

            elsValue = response.getBody();
        } catch (UnirestException ex) {
        	System.out.println(ex.getMessage());
        }

    	return elsValue;	
    }
}
