package com.projectfinalstartbot.function;

import org.springframework.stereotype.Component;

@Component
public class CategoryFilter {
	public boolean tescolotusFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.matches("ดูทั้งหมด") || 
		   category.matches("สินค้าอื่นๆ") ||
		   category.matches("เทศกาลปีใหม่")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean makroFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.matches("สมาร์ทและไลฟ์สไตล์") ||
		   category.matches("Own Brand")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean bigcFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.equals("พร้อมรับมือ โควิด-19") ||
		   category.equals("สินค้าส่งด่วน 1 ชม.") ||
		   category.equals("บ้านและไลฟ์สไตล์") ||
           category.equals("ร้านเพรียวฟาร์มาซี") ||
           category.equals("ร้านค้าส่ง") ||
           category.equals("สินค้าแบรนด์เบสิโค")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean lazadaFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.equals("อุปกรณ์เสริม อิเล็กทรอนิกส์") ||	
		   category.equals("กีฬาและ การเดินทาง") ||
           category.equals("ยานยนต์ และอุปกรณ์")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}		
	}
	
    //for makro
    public String getMenuId(String category) {
    	String menuId = null ;
        switch(category) 
        { 
            case "ผักและผลไม้": 
            	menuId = "3874";
                break; 
            case "เนื้อสัตว์": 
            	menuId = "3896"; //
                break; 
            case "ปลาและอาหารทะเล": 
            	menuId = "4147";
                break; 
            case "ไข่ นม เนย ชีส": //
            	menuId = "3353";
                break; 
            case "ผลิตภัณฑ์แปรรูปแช่เย็น": //
            	menuId = "82";
                break;
            case "ผลิตภัณฑ์เนื้อสัตว์แปรรูป": //
            	menuId = "4227";
                break;
            case "อาหารแช่แข็ง": 
            	menuId = "3932";
                break; 
            case "เบเกอรีและวัตถุดิบสำหรับทำเบเกอรี": 
            	menuId = "3803";
                break; 
            case "อาหารแห้ง": 
            	menuId = "2465";
                break; 
            case "เครื่องดื่มและขนมขบเคี้ยว": 
            	menuId = "2462";
                break; 
            case "อุปกรณ์และของใช้ในครัวเรือน": 
            	menuId = "2460";
                break; 
            case "ผลิตภัณฑ์ทำความสะอาด": 
            	menuId = "4112";
                break; 
            case "เครื่องเขียนและอุปกรณ์สำนักงาน": 
            	menuId = "2464";
                break; 
            case "เครื่องใช้ไฟฟ้า": 
            	menuId = "2461";
                break; 
            case "สุขภาพและความงาม": 
            	menuId = "2466";
                break; 
            case "แม่และเด็ก": 
            	menuId = "2467";
                break; 
            case "ผลิตภัณฑ์สำหรับสัตว์เลี้ยง": 
            	menuId = "2468";
                break; 
            default: 
                System.out.println("no match"); 
        } 
    	return menuId;
    }   
}
