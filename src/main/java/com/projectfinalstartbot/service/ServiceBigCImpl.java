package com.projectfinalstartbot.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.projectfinalstartbot.function.CategoryFilter;
import com.projectfinalstartbot.function.DateTimes;
import com.projectfinalstartbot.function.Elasticsearch;
import com.projectfinalstartbot.function.OtherFunc;

@Service
public class ServiceBigCImpl implements ServiceBigC{
	public String bigcUrl = "https://www.bigc.co.th/api/categories/mainCategory?_store=2";
	@Autowired
	private Elasticsearch els;
	
    @Autowired
    private CategoryFilter categoryFilter;
    
    @Autowired
    private DateTimes dateTimes;
    
    @Autowired
    private OtherFunc otherFunc;

	@Override
	public void run(JSONObject obj) {
		List<String>listCategort = classifyCategoryUrl(obj);
		for(String item : listCategort) {
			bigc(item);
		}		
	}

	public List<String> classifyCategoryUrl(JSONObject obj) {
		List<String>list = new ArrayList<>();		
		try {
			String bigcValue = null;
			// เชื่อมต่อ bigc api
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post(bigcUrl)
        	  .header("Cookie", "__cfduid=d9a40b9d9b244c28a4c0760f1fca09b871608172638")
        	  .asString();
        	bigcValue = response.getBody(); //รับค่า
            
            // แกะข้อมูลที่ได้จาก bigc api
	        JSONObject objCategory = new JSONObject(bigcValue);
	        JSONArray arrCategory= objCategory.getJSONArray("result");
	        for (int i = 0; i < arrCategory.length(); i++) {       
	            String categoryName = arrCategory.getJSONObject(i).getString("name");
	            
	            System.out.println("category ==> "+categoryName);
            	// ตัด category เหล่านี้ออกไป
            	if(categoryFilter.bigcFilter(categoryName)) {
            		//get cate_id
            		int cateId = arrCategory.getJSONObject(i).getInt("entity_id");
            		String newCategory = els.getCategory(categoryName); 
            		
            		System.out.println("new category ==> "+newCategory);
            		
            		obj.put("category", newCategory);
            		obj.put("cateId", String.valueOf(cateId));
                	list.add(obj.toString());//จัดเก็บลง redis เพื่อหา detail ต่อ
                	
    	            System.out.println(dateTimes.thaiDateTime() +" fetch bigC ==> "+cateId);  
            	}
	        } 
	             	 			
		}catch(Exception e) {
			System.out.println("error classifyCategoryUrl => "+e.getMessage());
		}
		return list;
	}

	public void bigc(String objStr) {
		String baseUrl = "https://www.bigc.co.th/";
		try {
			JSONObject jsonEls = new JSONObject();
			JSONObject obj = new JSONObject(objStr);
			String category = obj.getString("category");
			String cateId = obj.getString("cateId");
        	//call bigCapi
        	String elasValue = els.bigCApi(cateId, "1");
        	//get last page
            int lastPage = otherFunc.lastPage(elasValue);
            System.out.println("category => "+category);
            System.out.println("lastPage => "+lastPage);
            // วนหา pagination ของ page นั้นๆ
            for(int j = 1; j <= lastPage; j++) {
            	String bigCValue = els.bigCApi(cateId, Integer.toString(j));
            	
            	// ดึงข้อมูล
    			JSONObject json = new JSONObject(bigCValue);
    			JSONObject result = json.getJSONObject("result");
    			JSONArray arrItems = result.getJSONArray("items");
    			for (int k = 0; k < arrItems.length(); k++) {
    				JSONObject objItems = arrItems.getJSONObject(k);
    				double price = objItems.getDouble("final_price_incl_tax");
    				double originalPrice = objItems.getDouble("price_incl_tax");

    				// เก็บเฉพาะที่มีส่วนลด
    				if(price != originalPrice) {
        				String image = objItems.getString("image");
        				String name = objItems.getString("name");
        				String productUrl = baseUrl + objItems.getString("url_key");

        				double discount = (((originalPrice - price) / originalPrice) * 100);
			            DecimalFormat df = new DecimalFormat("#"); // #.# แปลงทศนิยม 1 ตำแหน่ง
			            discount = Double.parseDouble(df.format(discount));
        			
	            		jsonEls.put("image",image);  
	            		jsonEls.put("name",name);  
	                    jsonEls.put("category",category);  
	                    jsonEls.put("productUrl",productUrl);  
	                    jsonEls.put("icon",obj.getString("icon_url"));
	                    jsonEls.put("price",price); 
	                    jsonEls.put("originalPrice",originalPrice);  
	                    jsonEls.put("discount",discount);  
	                    jsonEls.put("webName",obj.getString("web_name"));  
	                    jsonEls.put("review","-");
	                    jsonEls.put("ratingScore","-");
						
			            els.inputElasticsearch(jsonEls.toString(), obj.getString("database"));			            
			            System.out.println(dateTimes.thaiDateTime() +" web scrapping ==> "+productUrl); 
    				}
    			}
            }
		}catch(Exception e) {
			System.out.println("error bigc => "+e.getMessage());
		}		
	}

}
