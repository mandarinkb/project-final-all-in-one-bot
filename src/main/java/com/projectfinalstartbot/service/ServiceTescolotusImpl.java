package com.projectfinalstartbot.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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


@Service
public class ServiceTescolotusImpl implements ServiceTescolotus{
	@Autowired
	private Elasticsearch els;
	
    @Autowired
    private CategoryFilter categoryFilter;
    
    @Autowired
    private DateTimes dateTimes;


	@Override
	public void run(JSONObject obj) {
		List<String>listCategort = classifyCategoryUrl(obj);
		for(String item : listCategort) {
			categoryUrlDetail(item);
		}
	}

	//แยกประเภท category url
	public List<String> classifyCategoryUrl(JSONObject obj) {
		List<String>list = new ArrayList<>();
		String url = obj.getString("url");		
    	try {
    		Document doc = Jsoup.connect(url)
		                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
		                        .timeout(60000)
		                        .maxBodySize(0)
		                        .get();//
            Elements eles = doc.select(".list-item.list-item-large");
            for (Element ele : eles) {
            	String category = ele.select(".name").html();
            	System.out.println("category ==> "+category);
            	// กรองหมวดหมู่
            	if(categoryFilter.tescolotusFilter(category)) {
                    Element eleTitle = ele.select("a").first();                   
                    String strUrl = eleTitle.absUrl("href");
                    String categoryUrl = strUrl;
                    String newCategory = els.getCategory(category); // แปลง category ใหม่
                    System.out.println("new category ==> "+newCategory);
                    
                    obj.put("category",newCategory);
                    obj.put("url",categoryUrl);
                    list.add(obj.toString());//จัดเก็บลง redis เพื่อหา detail ต่อ
            	}
            }
    	}catch(Exception e) {
    		System.out.println("error classifyCategoryUrl => "+e.getMessage());
    		//redis.rpush("startUrl", obj); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}
		return list;
	}

	//วน pagination ของแต่ละหน้า
	public void categoryUrlDetail(String obj) {
		JSONObject json = new JSONObject(obj);
		String url = json.getString("url");
    	try {     	
        	boolean checkNextPage = true;       	
        	while(checkNextPage) {  
            	Document doc = Jsoup.connect(url)
			                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
			                        .timeout(60000)
			                        .maxBodySize(0)
			                        .get();//
            	//urlDetail
            	Elements elesUrlDetail = doc.select(".tile-content");
                for (Element ele : elesUrlDetail) {
                    Element eleUrl = ele.select("a").first();
                    String urlDetail = eleUrl.absUrl("href");
                    json.put("url",urlDetail);
                    // หา detail ของหน้าเว็บต่อเลย
                    tescolotus(json.toString());
            	}  		
            	//next page
            	Elements elesNextPage = doc.select(".pagination--page-selector-wrapper");
        		Element eleNextPage = elesNextPage.select(".pagination-btn-holder").last();
                Element eleA = eleNextPage.select("a").first();
                //String urlNextPage = eleA.attr("href");
                String urlNextPage = eleA.absUrl("href");    
                if(urlNextPage == "") {
                	checkNextPage = false;
                }
                url = urlNextPage;
                System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+url);    
        	}
    	}catch(Exception e) {
    		System.out.println("error categoryUrlDetail => "+e.getMessage());
    		//redis.rpush("categorytUrl", obj); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}
		// return list;
	}
	//หารายละเอียดของสินค้าลดราคา
	public void tescolotus(String obj) {
		JSONObject json = new JSONObject(obj);
		JSONObject jsonEls = new JSONObject();
		String url = json.getString("url");
    	try {
        	Document doc = Jsoup.connect(url)
		                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
		                        .timeout(60000)
		                        .maxBodySize(0)
		                        .get();//
        	Elements elesUrlDetail = doc.select(".product-image__container");
        	//find image
            for (Element ele : elesUrlDetail) {
                Element eleUrl = ele.select("img").first();
                String image = eleUrl.attr("src");      
                jsonEls.put("image",image);    
        	}
            //find name
            String name = doc.select(".product-details-tile__title").html();
            jsonEls.put("name",name);
            
            //find price
            String priceAll = doc.select(".offer-text").first().html();
            String[] parts = priceAll.split("บาท");
            String priceStr = parts[0].replace("ราคาพิเศษ ", "");
            String originalPriceStr = parts[1].replace(" จากราคาปกติ ", "");
            //String save = parts[2].replace(" ประหยัด ", "");
            double price = Double.parseDouble(priceStr);
            double originalPrice = Double.parseDouble(originalPriceStr);
            double discountFull = (((originalPrice - price) / originalPrice) * 100);  // หา % ของส่วนลด
            DecimalFormat df = new DecimalFormat("#"); // #.# แปลงทศนิยม 1 ตำแหน่ง
            double discount = Double.parseDouble(df.format(discountFull));
            
            jsonEls.put("category",json.getString("category"));
            jsonEls.put("productUrl",url);
            jsonEls.put("icon",json.getString("icon_url"));
            jsonEls.put("price",price);
            jsonEls.put("originalPrice",originalPrice);
            jsonEls.put("discountFull",discountFull);
            jsonEls.put("discount",discount);
            jsonEls.put("webName",json.getString("web_name"));
            jsonEls.put("review","-");
            jsonEls.put("ratingScore","-");
            
            els.inputElasticsearch(jsonEls.toString(), json.getString("database"));
            System.out.println(dateTimes.thaiDateTime() +" web scrapping ==> "+url); 
    	}catch(Exception e) {
    		System.out.println("error tescolotus => "+e.getMessage());
    		//redis.rpush("detailUrl", obj); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}	
	}

}
