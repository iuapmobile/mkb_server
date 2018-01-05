package com.yyuap.mkb.cbo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CBOManager {

    public Tenant getTenantInfo(String apiKey) throws SQLException {
        if (apiKey == null || apiKey.equals(""))
            return null;
        Tenant tenant = null;

        String sql = CommonSQL.SELECT_TENANT_BY_APIKEY_SQL;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
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
                tenant.settname(rs.getString("tname"));
                tenant.settusername(rs.getString("tusername"));
                tenant.setTpassword(rs.getString("tpassword"));
                tenant.setAPIKey(rs.getString("apiKey"));
                tenant.setdbip(rs.getString("dbip"));
                tenant.setdbport(rs.getString("dbport"));
                tenant.setdbname(rs.getString("dbname"));
                tenant.setdbusername(rs.getString("dbusername"));
                tenant.setdbpassword(rs.getString("dbpassword"));

                tenant.settkbcore(rs.getString("tkbcore"));
                tenant.settqakbcore(rs.getString("tqakbcore"));

                tenant.setbotKey(rs.getString("botKey"));
                tenant.setSimscore(rs.getFloat("simscore"));
                tenant.setRecommended(rs.getBoolean("recommended"));

                tenant.setSolr_qf(rs.getString("solr_qf"));
                tenant.setSolr_sort(rs.getString("solr_sort"));

                tenant.setUseSynonym(rs.getBoolean("useSynonym"));
                tenant.setSorl_useFilterQueries(rs.getBoolean("solr_useFilterQueries"));
                tenant.setBotSkillConfig(rs.getString("botSkillConfig"));

                tenant.setBot_social_chatBot_enabled(rs.getBoolean("bot_social_chatBot_enabled"));
                tenant.setBot_social_chatBot_v(rs.getInt("bot_social_chatBot_v"));
                tenant.setBot_kb_simSearch_num(rs.getInt("bot_kb_simSearch_num"));
                
                tenant.setBot_api_ia_conf(rs.getString("bot_api_ia_conf"));
            }

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
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

    public TenantInfo getTenant(String apiKey) throws SQLException {

        TenantInfo tenant = null;

        String sql = CommonSQL.SELECT_TENANT_BY_APIKEY_SQL;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setString(1, apiKey);

            rs = ps.executeQuery();

            int num = 0;
            while (rs.next()) {
                num++;
                if (num > 1) {
                    throw new Exception("Exception: find [" + num + "] tenants  by APIKey = '" + apiKey + "'");
                }
                tenant = new TenantInfo();
                tenant.setId(rs.getString("id"));
                tenant.setTname(rs.getString("tname"));
                tenant.setTdescript(rs.getString("tdescript"));
                tenant.setTusername(rs.getString("tusername"));
                tenant.setTpassword(rs.getString("tpassword"));
                tenant.setApiKey(rs.getString("apiKey"));
                tenant.setDbip(rs.getString("dbip"));
                tenant.setDbport(rs.getString("dbport"));
                tenant.setDbname(rs.getString("dbname"));
                tenant.setDbusername(rs.getString("dbusername"));
                tenant.setDbpassword(rs.getString("dbpassword"));

                tenant.setTkbcore(rs.getString("tkbcore"));
                tenant.setTqakbcore(rs.getString("tqakbcore"));

                tenant.setBotKey(rs.getString("botKey"));
                tenant.setSimscore(rs.getFloat("simscore"));
                tenant.setRecommended(rs.getBoolean("recommended"));

                tenant.setSolr_qf(rs.getString("solr_qf"));
                tenant.setSolr_sort(rs.getString("solr_sort"));

                tenant.setUseSynonym(rs.getBoolean("useSynonym"));
                tenant.setSorl_useFilterQueries(rs.getBoolean("solr_useFilterQueries"));
                tenant.setBotSkillConfig(rs.getString("botSkillConfig"));

            }

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
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

    /**
     * 查询 全部 租户
     * 
     * @return
     * @throws SQLException
     */
    public List<TenantInfo> getTenantInfo() throws SQLException {

        TenantInfo tenant = null;

        String sql = "select * from mkb.u_tenant";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TenantInfo> list = new ArrayList<TenantInfo>();
        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                tenant = new TenantInfo();
                tenant.setId(rs.getString("id"));
                tenant.setTname(rs.getString("tname"));
                tenant.setTdescript(rs.getString("tdescript"));
                tenant.setTusername(rs.getString("tusername"));
                tenant.setTpassword(rs.getString("tpassword"));
                tenant.setApiKey(rs.getString("apiKey"));
                tenant.setDbip(rs.getString("dbip"));
                tenant.setDbport(rs.getString("dbport"));
                tenant.setDbname(rs.getString("dbname"));
                tenant.setDbusername(rs.getString("dbusername"));
                tenant.setDbpassword(rs.getString("dbpassword"));

                tenant.setTkbcore(rs.getString("tkbcore"));
                tenant.setTqakbcore(rs.getString("tqakbcore"));

                tenant.setBotKey(rs.getString("botKey"));
                tenant.setSimscore(rs.getFloat("simscore"));
                tenant.setRecommended(rs.getBoolean("recommended"));

                tenant.setSolr_qf(rs.getString("solr_qf"));
                tenant.setSolr_sort(rs.getString("solr_sort"));

                tenant.setUseSynonym(rs.getBoolean("useSynonym"));
                tenant.setSorl_useFilterQueries(rs.getBoolean("solr_useFilterQueries"));
                tenant.setBotSkillConfig(rs.getString("botSkillConfig"));

                list.add(tenant);

            }

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
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

    /**
     * 更新
     * 
     * @return
     * @throws SQLException
     */
    public boolean updateTenantInfo(TenantInfo tenant) throws SQLException {

        String sql = "update mkb.u_tenant set tname=?,tdescript=?,tusername=?,tpassword=?,simscore=?,recommended=?,solr_qf=?,solr_sort=?,useSynonym=?,solr_useFilterQueries=? where id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flag = false;
        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setString(1, tenant.getTname());
            ps.setString(2, tenant.getTdescript());
            ps.setString(3, tenant.getTusername());
            ps.setString(4, tenant.getTpassword());
            ps.setFloat(5, tenant.getSimscore());
            ps.setBoolean(6, tenant.getRecommended());
            ps.setString(7, tenant.getSolr_qf());
            ps.setString(8, tenant.getSolr_sort());
            ps.setBoolean(9, tenant.getUseSynonym());
            ps.setBoolean(10, tenant.getSorl_useFilterQueries());
            ps.setString(11, tenant.getId());

            flag = ps.execute();

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return flag;
    }

    /**
     * 更新
     * 
     * @return
     * @throws SQLException
     */
    public boolean updateBotInfo(TenantInfo tenant) throws SQLException {

        String sql = "update mkb.u_tenant set simscore=?,recommended=?,solr_qf=?,solr_sort=?,useSynonym=?,solr_useFilterQueries=? where id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flag = false;
        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setFloat(1, tenant.getSimscore());
            ps.setBoolean(2, tenant.getRecommended());
            ps.setString(3, tenant.getSolr_qf());
            ps.setString(4, tenant.getSolr_sort());
            ps.setBoolean(5, tenant.getUseSynonym());
            ps.setBoolean(6, tenant.getSorl_useFilterQueries());
            ps.setString(7, tenant.getId());

            flag = ps.execute();

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return flag;
    }

    /**
     * 更新
     * 
     * @return
     * @throws SQLException
     */
    public boolean updateBotSettings(String apiKey, String key, boolean value) throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;
        boolean flag = false;
        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);

            String sql = "update mkb.u_tenant set ?=? where apiKey=?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, key);
            ps.setBoolean(2, value);
            ps.setString(3, apiKey);

            flag = ps.execute();

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return flag;
    }

    /**
     * 删除
     * 
     * @return
     * @throws SQLException
     */
    public boolean delTenantInfo(String id) throws SQLException {

        Tenant tenant = null;

        String sql = "select * from u_tenant where id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success1 = false;
        boolean success2 = false;
        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setString(1, id);

            rs = ps.executeQuery();

            int num = 0;
            String dbname = "";
            while (rs.next()) {
                dbname = rs.getString("dbname");
            }
            String delsql = "delete from u_tenant where id='" + id + "'";
            ps = conn.prepareStatement(delsql);
            success1 = ps.execute();
            // String dropsql = "drop database "+dbname+"";
            // ps = conn.prepareStatement(dropsql);
            // success2 = ps.execute();

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
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

        return success1;
    }

    public TenantInfo getTenantInfoForLogin(String username, String password) throws SQLException {

        TenantInfo tenantinfo = null;

        String sql = "select * from mkb.u_tenant where tusername = ? and tpassword = ? ";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(CommonSQL.DRIVER);
            conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
            ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            int num = 0;
            while (rs.next()) {
                num++;
                if (num > 1) {
                    throw new Exception("Exception: find [" + num + "] tenants  by tusername = '" + username
                            + "' and tpassword = '" + password + "'");
                }
                tenantinfo = new TenantInfo();
                tenantinfo.setTid(rs.getString("tid"));
                tenantinfo.setTname(rs.getString("tname"));
                tenantinfo.setApiKey(rs.getString("apiKey"));
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

        return tenantinfo;
    }
}
