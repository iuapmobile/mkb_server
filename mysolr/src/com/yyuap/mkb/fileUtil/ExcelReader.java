/**
 * 
 */
package com.yyuap.mkb.fileUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.log.MKBLogger;

import javafx.animation.KeyValue.Type;

/**
 * @author gct
 * @created 2017-5-20
 */
public class ExcelReader {

    public List<KBIndex> readXls(String path, KBINDEXTYPE type) throws IOException {
        // getServletContext().getRealPath("/");
        if (path == null || path.equals("")) {
            return new ArrayList<KBIndex>();
        }
        // InputStream is = new FileInputStream(Common.EXCEL_PATH);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

        KBIndex kbIndex = null;
        List<KBIndex> list = new ArrayList<KBIndex>();
        // ѭ��������Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // ѭ����Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    kbIndex = new KBIndex();
                    if (type == KBINDEXTYPE.KBINDEX) {
                        // 对应
                        HSSFCell id_cell = hssfRow.getCell(0);// 编号
                        HSSFCell title_cell = hssfRow.getCell(1);// 标题 tile
                        HSSFCell descript_cell = hssfRow.getCell(2);// descript
                                                                    // 简介
                        HSSFCell domain_cell = hssfRow.getCell(3);// lingyu
                        HSSFCell keywords_cell = hssfRow.getCell(4);// keyword（，隔开）
                        HSSFCell label_cell = hssfRow.getCell(5);// 标签（，隔开）
                        HSSFCell category_cell = hssfRow.getCell(6);// 类别
                        HSSFCell grade_cell = hssfRow.getCell(7);// 分级
                        HSSFCell descriptImg_cell = hssfRow.getCell(8);// 简介图片
                        HSSFCell url_cell = hssfRow.getCell(9);// url
                        HSSFCell author_cell = hssfRow.getCell(10);// 作者
                        HSSFCell createTime_cell = hssfRow.getCell(11);// 创建时间
                        HSSFCell updateTime_cell = hssfRow.getCell(12);// 更新时间

                        String uuid = UUID.randomUUID().toString();
                        // kbIndex.setId(getValue(id_cell));
                        kbIndex.setId(uuid);
                        kbIndex.setTitle(getValue(title_cell));
                        kbIndex.setDescript(getValue(descript_cell));
                        kbIndex.setDomain(getValue(domain_cell));
                        kbIndex.setKeywords(getValue(keywords_cell));

                        kbIndex.setTag(getValue(label_cell));
                        kbIndex.setCategory(getValue(category_cell));
                        kbIndex.setGrade(getValue(grade_cell));
                        kbIndex.setDescriptImg(getValue(descriptImg_cell));
                        kbIndex.setUrl(getValue(url_cell));

                        kbIndex.setAuthor(getValue(author_cell));
                        kbIndex.setCreateTime(getValue(createTime_cell));
                        kbIndex.setUpdateTime(getValue(updateTime_cell));
                    } else if (type == KBINDEXTYPE.QA) {
                        HSSFCell q_cell = hssfRow.getCell(0);// 问题
                        HSSFCell a_cell = hssfRow.getCell(1);// 答案

                        String uuid = UUID.randomUUID().toString();
                        kbIndex.setId(uuid);
                        kbIndex.setTitle(getValue(q_cell));
                        kbIndex.setDescript(getValue(a_cell));
                        kbIndex.setKeywords(getValue(q_cell));
                    }

                    if (kbIndex.getTitle().equals("") && kbIndex.getDescript().equals("") && kbIndex.getUrl().equals("")
                            && kbIndex.getText().equals("")) {
                        MKBLogger.info("numSheet : " + hssfSheet.getSheetName() + " , rowNum : " + rowNum
                                + " title is empty!");
                    } else {
                        list.add(kbIndex);
                    }
                }
            }
        }
        return list;
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell == null) {
            return "";
        }
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}
