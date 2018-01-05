package com.yyuap.mkb.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.pl.Common;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.DbUtil;

public class searchStaff {
    public static void test() {
        String q = "姚";
        JSONArray ret = searchStaff.selectFromDB(q);
        System.out.println(ret.toJSONString());
    }

    public static JSONArray selectFromDB(String q) {
        // TODO Auto-generated method stub
        JSONArray list = new JSONArray();

        DBConfig dbconf = new DBConfig();
        dbconf.setIp("127.0.0.1");
        dbconf.setPort("3306");
        dbconf.setDbName("mkb");

        dbconf.setUsername("root");
        dbconf.setPassword("1qazZAQ!");

        String sql1 = "select * from staff ";

        String xxx = "";
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("name");
        cols.add("mobile");
        cols.add("email");
        for (int i = 0, len = cols.size(); i < len; i++) {
            if (i == 0) {
                xxx += "where " + cols.get(i) + " like ?";
            } else {
                xxx += " or " + cols.get(i) + " like ?";
            }
        }
        String SQL_SELECT = sql1 + xxx;

        try {
            list = selectAnswer(SQL_SELECT, cols, q, dbconf);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    public static JSONArray selectAnswer(String sql, ArrayList<String> cols, String q, DBConfig dbconf)
            throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray list = new JSONArray();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(dbconf.getUlr(), dbconf.getUsername(), dbconf.getPassword());
            ps = conn.prepareStatement(sql);
            for (int i = 0, len = cols.size(); i < len; i++) {
                ps.setString(i + 1, "%" + q + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getString("id"));

                for (int i = 0, len = cols.size(); i < len; i++) {
                    obj.put(cols.get(i), rs.getString(cols.get(i)));
                }

                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

}
