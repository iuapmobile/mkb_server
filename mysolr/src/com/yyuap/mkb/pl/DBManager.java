/**
 * 
 */
package com.yyuap.mkb.pl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.fileUtil.ExcelReader;
import com.yyuap.mkb.fileUtil.ExcelXReader;

/**
 * @author gct
 * @created 2017-5-20
 */
public class DBManager {

    @SuppressWarnings({ "rawtypes" })
    public void saveKB(String path, KBINDEXTYPE type) throws IOException, SQLException {

        List<KBIndex> list = new ArrayList<KBIndex>();
        if (path.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx(path, type);
        } else {
            ExcelReader xlsMain = new ExcelReader();
            list = xlsMain.readXls(path, type);
        }

        addKBIndexList(list);
    }

    public void saveQA(String path, String type) throws IOException, SQLException {

        List<KBIndex> list = new ArrayList<KBIndex>();
        if (path.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx(path, KBINDEXTYPE.KBINDEX);
        } else {
            ExcelReader xlsMain = new ExcelReader();
            list = xlsMain.readXls(path, KBINDEXTYPE.KBINDEX);
        }

        addKBIndexList(list);
    }

    public void addKBIndexList(List<KBIndex> list) throws SQLException {
        KBIndex kbIndex = new KBIndex();
        for (int i = 0; i < list.size(); i++) {
            kbIndex = list.get(i);
            if (kbIndex.getTitle().equals("")) {
                continue;
            }
            addKBIndex(kbIndex);
        }
    }

    public boolean addKBIndex(KBIndex kbIndex) throws SQLException {
        List list = DbUtil.selectOne(Common.SELECT_KBINDEXINFO_SQL, kbIndex);
        if (!list.contains(1)) {
            DbUtil.insert(Common.INSERT_KBINDEXINFO_SQL, kbIndex);
            return false;
        } else {
            System.out.println("The Record was Exist : title. = " + kbIndex.getTitle() + " , url = " + kbIndex.getUrl()
                    + ", descript = " + kbIndex.getDescript() + ", and has been throw away!");
            return false;
        }
    }

    public boolean updateKBIndex(KBIndex kbindex) throws SQLException {
        DbUtil.update(Common.UPDATE_KBINDEXINFO_SQL, kbindex);
        return false;
    }

    public void insertQAFromExcel(String path, Tenant tenant) throws IOException, SQLException {
        // TODO Auto-generated method stub
        List<KBQA> list = new ArrayList<KBQA>();
        if (path.endsWith("xlsx")) {
            ExcelXReader xlsxMain = new ExcelXReader();
            list = xlsxMain.readXlsx4QA(path);
        } else {

        }
        for (int i = 0, len = list.size(); i < len; i++) {
            KBQA qa = list.get(i);
            this.insertQA(qa, tenant);
        }

    }

    public String insertQA(KBQA qa, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        if (qa == null) {
            return null;
        }

        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        ArrayList<KBQA> list = DbUtil.selectOne(Common.SELECT_QA_SQL, qa, dbconf);
        if (list.size() == 0) {
            // 2.1 问答
            String id = DbUtil.insertQA(Common.INSERT_QA_SQL, qa, dbconf);

            // 2.2 相似问法
            if (qa.getQuestions() != null) {
                DbUtil.insertQA_SIMILAR(Common.INSERT_QA_SIMILAR_SQL, qa, id, dbconf);
            }
            return id;
        } else {
            System.out.println("The Record was Exist : question. = " + qa.getQuestion() + " , answer = "
                    + qa.getAnswer() + ", and has been throw away!");
            String id = list.get(0).getId();
            throw new SQLException(
                    "存在重复的记录id[" + id + "]，q=" + qa.getQuestion() + ", a=" + qa.getAnswer() + ", 未能持久化成功改次操作");
            // return id;
        }
    }

    public JSONObject selectUniqueAnswer(String q, Tenant tenant) {
        // TODO Auto-generated method stub
        JSONObject ret = null;
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        try {
            list = DbUtil.selectAnswer(Common.SELECT_ANSWER_SQL, q, dbconf);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (list.size() == 1) {
            ret = list.get(0);
        }

        return ret;
    }

    private DBConfig getDBConfigByTenant(Tenant tenant) {
        DBConfig dbc = new DBConfig();
        dbc.setIp(tenant.getdbip());
        dbc.setPort(tenant.getdbport());
        dbc.setDbName(tenant.getdbname());

        dbc.setUsername(tenant.getdbusername());
        dbc.setPassword(tenant.getdbpassword());

        return dbc;
    }

    public List selectALLQA(Tenant tenant) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        List list = DbUtil.selectALLQA(Common.SELECT_ALL_QA_SQL, dbconf);

        return list;
    }

    public JSONObject selectQAById(Tenant tenant, String id) {
        // TODO Auto-generated method stub
        JSONObject json = null;
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        String[] arrayParams = { id };

        // 2、
        json = DbUtil.selectQAById(Common.SELECT_QA_BY_ID_SQL, arrayParams, dbconf);

        return json;
    }

    public JSONArray selectSimilarQByQid(Tenant tenant, String id) {
        // TODO Auto-generated method stub
        JSONArray array = null;

        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、
        array = DbUtil.selectSimilarQByQid(Common.SELECT_QA_SIMILAR_BY_ID_SQL, new String[] { id }, dbconf);

        return array;
    }

    public String insertQA_tj(KBQA qa, Tenant tenant) {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        String id = "";
        try {
            id = DbUtil.insertQA_TJ(Common.INSERT_QA_TJ_SQL, qa, dbconf);
            return id;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return id;
    }

    public JSONArray query_tj(Tenant tenant) {
        // TODO Auto-generated method stub
        // 1、根据租户获取DBconfig
        DBConfig dbconf = this.getDBConfigByTenant(tenant);

        // 2、检查是否已经存在相同的q和a
        JSONArray list = null;
        try {
            list = DbUtil.selectQA_top5(Common.QA_Top5, dbconf);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public String addKBQAFeedback(KBQAFeedback fb, Tenant tenant) {
        DBConfig dbconf = this.getDBConfigByTenant(tenant);
        String id = "";
        try {
            id = DbUtil.updateQAFeedback(Common.UPDATE_QA_FEEDBACK_SQL, fb, dbconf);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return id;
    }

}