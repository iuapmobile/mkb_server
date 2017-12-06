package com.yyuap.mkb.fileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.processor.QAManager;

/**
 * 
 * @author gct
 * 
 *         导出 QA
 *
 */
public class ExcelXWriter {

    public boolean writeXlsx(JSONArray dataSource, String filePath, KBINDEXTYPE type) throws IOException {
        if (dataSource == null || dataSource.size() == 0) {
            return false;
        }
        boolean flag = true;
        OutputStream outXlsx = null;
        SXSSFWorkbook wb = createXSSFWorkbook();
        SXSSFSheet sheet = createSheet(wb, type == KBINDEXTYPE.QA ? "QA" : "KBINDEX");
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                // 先得到文件的上级目录，并创建上级目录，在创建文件
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            outXlsx = new FileOutputStream(filePath);

            // 在sheet里创建第一行，参数为行索引(excel的行)
            SXSSFRow row0 = (SXSSFRow) sheet.createRow(0);

            // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
            SXSSFCell cell = (SXSSFCell) row0.createCell(0);

            // 设置单元格内容
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            String now = df.format(new Date());// new Date()为获取当前系统时间
            cell.setCellValue("##### 这里是到处的内容，导出时间" + now + " #####");

            // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            for (int i = 0; i < dataSource.size(); i++) {
                SXSSFRow rown = (SXSSFRow) sheet.createRow(i + 1);
                JSONObject json = (JSONObject) dataSource.get(i);
                String question = json.getString("q");
                String answer = json.getString("a");
                SXSSFCell cell0 = (SXSSFCell) rown.createCell(0);
                cell0.setCellValue(question);
                SXSSFCell cell1 = (SXSSFCell) rown.createCell(1);
                cell1.setCellValue(answer);

                // 处理剩下的
                Iterator<String> sIterator = json.keySet().iterator();
                int j = 0;
                while (sIterator.hasNext()) {
                    // 获得key
                    String key = sIterator.next();
                    if (key.equals("q") || key.equals("a")) {
                        continue;
                    }
                    String value = json.getString(key);
                    SXSSFCell celln = (SXSSFCell) rown.createCell(j + 2);
                    celln.setCellValue(value);
                    j++;
                }
            }
            wb.write(outXlsx);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        } finally {
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
     * 
     * @return
     */
    public SXSSFWorkbook createXSSFWorkbook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);// 缓存
        workbook.setCompressTempFiles(true);
        return workbook;
    }

    /**
     * 建立新的sheet对象（excel的表单）
     * 
     * @return
     */
    public SXSSFSheet createSheet(SXSSFWorkbook wb, String name) {
        // 生成一个(带标题)表格
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet();
        return sheet;
    }

    public static void main(String[] args) {
        String s = null;
        System.out.println(s.split(",")[0]);
    }

}
