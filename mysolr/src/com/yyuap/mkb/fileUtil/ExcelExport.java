package com.yyuap.mkb.fileUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.processor.QAManager;

/**
 * 
 * @author pengjf
 * 
 * 导出 QA
 *
 */
public class ExcelExport {

	public boolean exportQAForExcel(Tenant tenant){
		boolean flag = true;
		OutputStream outXlsx = null;
		SXSSFWorkbook wb = createXSSFWorkbook();
		SXSSFSheet sheet = createSheet(wb,"QA");
		
		try {
			outXlsx = new FileOutputStream("C://问答知识库.xlsx");
			//在sheet里创建第一行，参数为行索引(excel的行)  
			SXSSFRow row0 = (SXSSFRow) sheet.createRow(0);
			//创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个  
			SXSSFCell cell=(SXSSFCell) row0.createCell(0); 
		    //设置单元格内容  
			cell.setCellValue("#####################################################");  
			//合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列  
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,3)); 
			JSONArray ret = new JSONArray();
	        if (tenant != null) {
	            QAManager qamgr = new QAManager();
	            ret = qamgr.exportQA(tenant);
	        }
	        if(ret.size()>0){
	        	for(int i =0;i<ret.size();i++){
	        		SXSSFRow rown=(SXSSFRow) sheet.createRow(i+1);  
	        		Map<String,String> map = (Map<String,String>)ret.get(i);
	        		String question = map.get("question");
	        		String answer = map.get("answer");
	        		String q = map.get("q");
	        		String id = map.get("id");
	        		SXSSFCell cell0=(SXSSFCell) rown.createCell(0);
	        		cell0.setCellValue(question);
	        		SXSSFCell cell1=(SXSSFCell) rown.createCell(1);
	        		cell1.setCellValue(answer);
	        		String[] qArr = q.split(",");
	        		for(int j = 0;j < qArr.length;j++){
	        			SXSSFCell celln=(SXSSFCell) rown.createCell(j+2);
	        			celln.setCellValue(qArr[j]);
	        		}
	        	}
	        }
	        wb.write(outXlsx);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				if (outXlsx != null) {
                	outXlsx.close();
                }
				wb.close();
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
		}
        return flag;
	}
	
	/**
	 * 创建HSSFWorkbook对象(excel的文档对象) 
	 * @return
	 */
	public SXSSFWorkbook createXSSFWorkbook(){
		SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
	    return workbook;
	}
	
	/**
	 * 建立新的sheet对象（excel的表单）
	 * @return
	 */
	public SXSSFSheet createSheet(SXSSFWorkbook wb,String name){
		// 生成一个(带标题)表格
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet();
        return sheet;
	}
	
	public static void main(String[] args) {
		String s = "";
		System.out.println(s.split(","));
	}
	
}
