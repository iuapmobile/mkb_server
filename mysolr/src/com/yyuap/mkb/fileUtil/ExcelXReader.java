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

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;

import javafx.animation.KeyValue.Type;

/**
 * @author gct
 * @created 2017-5-20
 */
public class ExcelXReader {

    public List<KBIndex> readXlsx(String path, KBINDEXTYPE type) throws IOException {
        // getServletContext().getRealPath("/");
        if (path == null || path.equals("")) {
            return new ArrayList<KBIndex>();
        }
        // InputStream is = new FileInputStream(Common.EXCEL_PATH);
        InputStream is = new FileInputStream(path);

        // HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);

        KBIndex kbIndex = null;
        List<KBIndex> list = new ArrayList<KBIndex>();
        // foreach Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // foreach Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    kbIndex = new KBIndex();
                    // 对应
                    if (type == KBINDEXTYPE.KBINDEX) {
                        XSSFCell id_cell = hssfRow.getCell(0);// 编号
                        XSSFCell title_cell = hssfRow.getCell(1);// 标题 tile
                        XSSFCell descript_cell = hssfRow.getCell(2);// descript
                                                                    // 简介
                        XSSFCell domain_cell = hssfRow.getCell(3);// lingyu
                        XSSFCell keywords_cell = hssfRow.getCell(4);// keyword（，隔开）
                        XSSFCell label_cell = hssfRow.getCell(5);// 标签（，隔开）
                        XSSFCell category_cell = hssfRow.getCell(6);// 类别
                        XSSFCell grade_cell = hssfRow.getCell(7);// 分级
                        XSSFCell descriptImg_cell = hssfRow.getCell(8);// 简介图片
                        XSSFCell url_cell = hssfRow.getCell(9);// url
                        XSSFCell author_cell = hssfRow.getCell(10);// 作者
                        XSSFCell createTime_cell = hssfRow.getCell(11);// 创建时间
                        XSSFCell updateTime_cell = hssfRow.getCell(12);// 更新时间

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
                        XSSFCell q_cell = hssfRow.getCell(0);// 问题
                        XSSFCell a_cell = hssfRow.getCell(1);// 答案

                        String uuid = UUID.randomUUID().toString();
                        kbIndex.setId(uuid);
                        kbIndex.setTitle(getValue(q_cell));
                        kbIndex.setDescript(getValue(a_cell));
                        kbIndex.setKeywords(getValue(q_cell));
                    }
                    if (kbIndex.getTitle().equals("") && kbIndex.getDescript().equals("") && kbIndex.getUrl().equals("")
                            && kbIndex.getText().equals("")) {
                        System.out.println("numSheet : " + hssfSheet.getSheetName() + " , rowNum : " + rowNum
                                + " title is empty!");
                    } else {
                        list.add(kbIndex);
                    }
                }
            }
        }
        return list;
    }

    public List<KBIndex> readFileListFromExcel(String path, String rootPath) throws IOException {
        // getServletContext().getRealPath("/");
        List<KBIndex> list = new ArrayList<KBIndex>();
        if (path == null || path.equals("")) {
            return list;
        }

        // InputStream is = new FileInputStream(Common.EXCEL_PATH);
        InputStream is = new FileInputStream(path);

        // HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);

        // foreach Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // foreach Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    KBIndex kbIndex = new KBIndex();
                    // 约定格式如下
                    XSSFCell title_cell = hssfRow.getCell(0);// 文档名称，作为title
                    XSSFCell filepath_cell = hssfRow.getCell(1);// 文档路径，作为filePath
                    XSSFCell keywords_cell = hssfRow.getCell(2);// 关键字
                    XSSFCell label_cell = hssfRow.getCell(3);// 标签
                    XSSFCell descript_cell = hssfRow.getCell(4);// 资源描述，作为descript
                    XSSFCell grade_cell = hssfRow.getCell(5);// 分级
                    XSSFCell url_cell = hssfRow.getCell(6);// url
                    XSSFCell author_cell = hssfRow.getCell(7);// author

                    String uuid = UUID.randomUUID().toString();
                    kbIndex.setId(uuid);

                    String _title = getValue(title_cell);
                    if (_title.endsWith(".pdf")) {
                        _title = _title.substring(0, _title.lastIndexOf("."));
                    }
                    kbIndex.setTitle(_title);// 0

                    kbIndex.setFilePath(getValue(filepath_cell));// 1
                    kbIndex.setKeywords(getValue(keywords_cell));// 2
                    kbIndex.setTag(getValue(label_cell));// 3
                    kbIndex.setDescript(getValue(descript_cell));// 4
                    kbIndex.setGrade(getValue(grade_cell));// 5
                    kbIndex.setUrl(getValue(url_cell));// 6
                    kbIndex.setAuthor(getValue(author_cell));// 7

                    kbIndex.setDomain(hssfSheet.getSheetName());
                    if (kbIndex.getTitle().equals("") && kbIndex.getDescript().equals("") && kbIndex.getUrl().equals("")
                            && kbIndex.getText().equals("")) {
                        System.out.println("numSheet : " + hssfSheet.getSheetName() + " , rowNum : " + rowNum
                                + " title is empty!");
                    } else {
                        list.add(kbIndex);
                    }
                }
            }
        }
        return list;
    }

    public List<KBQA> readXlsx4QA(String path) throws IOException {
        // getServletContext().getRealPath("/");
        if (path == null || path.equals("")) {
            return new ArrayList<KBQA>();
        }
        // InputStream is = new FileInputStream(Common.EXCEL_PATH);
        InputStream is = new FileInputStream(path);

        // HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);

        KBQA kbqa = null;
        List<KBQA> list = new ArrayList<KBQA>();
        // foreach Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // foreach Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    kbqa = new KBQA();
                    // 对应
                    XSSFCell num_cell = hssfRow.getCell(0);// 问题
                    XSSFCell q_cell = hssfRow.getCell(1);// 问题
                    XSSFCell a_cell = hssfRow.getCell(2);// 答案

                    String uuid = UUID.randomUUID().toString();
                    kbqa.setId(uuid);
                    kbqa.setQuestion(getValue(q_cell));
                    kbqa.setAnswer(getValue(a_cell));

                    if (kbqa.getQuestion().equals("") || kbqa.getAnswer().equals("")) {
                        System.out.println("numSheet : " + hssfSheet.getSheetName() + " , rowNum : " + rowNum
                                + " title is empty!");
                    } else {
                        list.add(kbqa);
                    }
                }
            }
        }
        return list;
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell hssfCell) {
        if (hssfCell == null)
            return "";

        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}
