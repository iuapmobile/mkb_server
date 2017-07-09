package com.yyuap.mkb.cbo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CBOManager {

    public Tenant getTenantInfo(String apiKey) throws SQLException {

        Tenant tenant = null;

        String sql = CommonSQL.SELECT_TENANT_BY_TID_SQL;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setString(1, apiKey);

            rs = ps.executeQuery();

            int num = 0;
            while (rs.next()) {
                num++;
                if (num > 1) {
                    throw new Exception("Exception: find [" + num + "] tenants  by APIKey = '" + apiKey + "'");
                }
                tenant = new Tenant();
                tenant.setdbip(rs.getString("dbip"));
                tenant.setdbport(rs.getString("dbport"));
                tenant.setdbname(rs.getString("dbname"));
                tenant.setdbusername(rs.getString("dbusername"));
                tenant.setdbpassword(rs.getString("dbpassword"));

                tenant.settkbcore(rs.getString("tkbcore"));

                tenant.setbotKey(rs.getString("botKey"));
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

        return tenant;
    }
}
