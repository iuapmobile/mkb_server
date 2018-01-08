package com.yyuap.mkb.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.fileUtil.ExcelReader;
import com.yyuap.mkb.fileUtil.ExcelXReader;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.Common;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.DbUtil;

public class importAtomServices {

    public static void test() {
        String path = "/Users/gct/work2/xiaoyou/用友云2.0服务清单1.xls";
        try {
            ArrayList<ArrayList<String>> xx = importAtomServices.readXls(path);
            saveDB(xx);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void saveDB(ArrayList<ArrayList<String>> list) {

        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
     
        // String INSERT_SQL = "insert into staff(id, libraryPk, question,
        // answer, qtype, createTime, updateTime, createBy,
        // updateBy,istop,settoptime,url,kbid,ext_scope,domain,product,subproduct,extend0,extend1,extend2,extend3,extend4,extend5,extend6,extend7,extend8,extend9,extend10,extend11,extend12,extend13,extend14,extend15,extend16,extend17,extend18,extend19,ktype)
        // values(?, ?, ?, ?, ?, ?, ?, ?,
        // ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String sql1 = "insert into u_cbo_atomservices(";
        String sql2 = ") values (";
        String sql3 = ")";

        String xxx = "";
        String yyy = "";
        for (int i = 0, len = cols.size(); i < len; i++) {
            if (i == 0) {
                xxx += cols.get(i);
                yyy += "?";
            } else {
                xxx += "," + cols.get(i);
                yyy += ",?";
            }
        }
        String INSERT_SQL = sql1 + xxx + sql2 + yyy + sql3;
        DBConfig dbc = new DBConfig();
        dbc.setIp("127.0.0.1");
        dbc.setPort("3306");
        dbc.setDbName("mkb");

        dbc.setUsername("root");
        dbc.setPassword("1qazZAQ!");

        ArrayList<String> row = null;

        for (int i = 0; i < list.size(); i++) {
            row = list.get(i);

            try {

                insert(INSERT_SQL, row, dbc);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void insert(String sql, ArrayList<String> row, DBConfig dbconf) throws SQLException {
        // "insert into kbIndexInfo(title, decript, descriptImg, url,
        // author,keywords,tag,category,grade,domain,createTime,updateTime)
        // values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        try {

            String _username = dbconf.getUsername();
            String _psw = dbconf.getPassword();
            String _url = dbconf.getUlr();

            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(_url, _username, _psw);
            ps = conn.prepareStatement(sql);
            for (int i = 0, len = row.size(); i < len; i++) {
                String val = row.get(i);
                ps.setString(i + 1, val);
            }

            boolean flag = ps.execute();
            if (!flag) {
                MKBLogger.info("import data error: " + row.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static ArrayList<ArrayList<String>> readXls(String path) throws IOException {

        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

        ArrayList<String> row = null;
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();

        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }

            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    row = new ArrayList<String>();
                    row.add(UUID.randomUUID().toString());
                    short num = hssfRow.getLastCellNum();

                    for (int cellNum = 0, len = num; cellNum < len; cellNum++) {
                        HSSFCell cell = hssfRow.getCell(cellNum);// 编号
                        row.add(getValue(cell));
                    }

                    // 对应

                    // HSSFCell id_cell = hssfRow.getCell(0);// 编号
                    // HSSFCell title_cell = hssfRow.getCell(1);// 标题 tile
                    // HSSFCell descript_cell = hssfRow.getCell(2);// descript
                    // // 简介
                    // HSSFCell domain_cell = hssfRow.getCell(3);// lingyu
                    // HSSFCell keywords_cell = hssfRow.getCell(4);//
                    // keyword（，隔开）
                    // HSSFCell label_cell = hssfRow.getCell(5);// 标签（，隔开）
                    // HSSFCell category_cell = hssfRow.getCell(6);// 类别
                    // HSSFCell grade_cell = hssfRow.getCell(7);// 分级
                    // HSSFCell descriptImg_cell = hssfRow.getCell(8);// 简介图片
                    // HSSFCell url_cell = hssfRow.getCell(9);// url
                    // HSSFCell author_cell = hssfRow.getCell(10);// 作者
                    // HSSFCell createTime_cell = hssfRow.getCell(11);// 创建时间
                    // HSSFCell updateTime_cell = hssfRow.getCell(12);// 更新时间
                    //
                    // json.put("id", UUID.randomUUID().toString());
                    // json.put("renyuanbianma", getValue(hssfRow.getCell(0)));
                    // json.put("name", getValue(hssfRow.getCell(1)));
                    // json.put("sex", getValue(hssfRow.getCell(2)));
                    // json.put("birthDate", getValue(hssfRow.getCell(3)));
                    // json.put("zhengjianleixing",
                    // getValue(hssfRow.getCell(4)));
                    // json.put("zhengjianhao", getValue(hssfRow.getCell(5)));
                    //
                    // json.put("suoshubumen", getValue(hssfRow.getCell(5)));
                    // json.put("shangji", getValue(hssfRow.getCell(5)));
                    // json.put("mobile", getValue(hssfRow.getCell(5)));
                    // json.put("email", getValue(hssfRow.getCell(5)));
                    // json.put("ji", getValue(hssfRow.getCell(5)));
                    // json.put("zhengjianhao", getValue(hssfRow.getCell(5)));
                    // json.put("zhengjianhao", getValue(hssfRow.getCell(5)));
                    // json.put("zhengjianhao", getValue(hssfRow.getCell(5)));

                    table.add(row);

                }
            }
        }
        // MKBLogger.info(table.toJSONString());
        return table;
    }

    private static String getValue(HSSFCell hssfCell) {
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
