package com.projectfinalstartbot.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalstartbot.function.CategoryFilter;
import com.projectfinalstartbot.function.DateTimes;
import com.projectfinalstartbot.function.Elasticsearch;
import com.projectfinalstartbot.function.OtherFunc;




@Service
public class ServiceMakroclickImpl implements ServiceMakroclick{
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
			makroclick(item);
		}
	}

	public List<String> classifyCategoryUrl(JSONObject obj) {
		List<String>list = new ArrayList<>();
		String url = obj.getString("url");
		try {
    		Document doc = Jsoup.connect(url)
		                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
		                        .timeout(60000)
		                        .maxBodySize(0)
		                        .get();//
            Elements eles = doc.select(".MenuCategoryPopOver__MenuListView-sc-77t7qb-2"); 
            for (Element ele : eles) {
	            String category = ele.select("p").html();
	            System.out.println("category ==> "+category);
	            
	            if(categoryFilter.makroFilter(category)) {
		            String menuId = categoryFilter.getMenuId(category);
		            String newCategory = els.getCategory(category);
		            System.out.println("new category ==> "+newCategory);
		            
		            obj.put("category", newCategory);
		            obj.put("menuId", menuId);
		            list.add(obj.toString());//จัดเก็บลง redis เพื่อหา detail ต่อ
		            System.out.println(dateTimes.thaiDateTime() +" fetch makro ==> "+menuId);
	            }
            }				
		}catch(Exception e) {
			System.out.println("error classifyCategoryUrl => "+e.getMessage());
		}	
		return list;
	}

	public void makroclick(String objStr) {
	    String urlDetail = "https://www.makroclick.com/th/products/";
		try {
			JSONObject jsonEls = new JSONObject();
			JSONObject obj = new JSONObject(objStr);
			String category = obj.getString("category");
			String menuId = obj.getString("menuId");
			String elas = els.makroApi(menuId, "1");
			int total = otherFunc.totalPage(elas); // หา page ทั้งหมดก่อน
            System.out.println("category => "+category);
            System.out.println("lastPage => "+total);
            
			// วนหา pagination ของ page นั้นๆ
			for(int j = 1; j <= total; j++) {
				String elasValue = els.makroApi(menuId, Integer.toString(j));
				JSONObject objValue = new JSONObject(elasValue);
				JSONArray arrContent = objValue.getJSONArray("content");
				for (int k = 0; k < arrContent.length(); k++) {
					JSONObject objItems = arrContent.getJSONObject(k);
					Double originalPrice = objItems.getDouble("inVatPrice");
					Double price = objItems.getDouble("inVatSpecialPrice");
					
					//เก็บเฉพาะที่มีส่วนลด
					if(!originalPrice.equals(price)) {
						String image = objItems.getString("image");
						String name = objItems.getString("productName");
						String productUrl = urlDetail + objItems.getString("productCode");
						
			            double discount = (((originalPrice - price) / originalPrice) * 100);  // หา % ของส่วนลด
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
			System.out.println("error makroclick => "+e.getMessage());
		}
		
	}

}
