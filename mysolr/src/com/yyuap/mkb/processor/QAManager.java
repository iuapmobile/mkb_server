package com.yyuap.mkb.processor;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.QaCollection;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBDuplicateSQLException;

public class QAManager {
    public QAManager() {

    }

    public JSONObject getUniqueAnswer(String q, Tenant tenant) {
        // TODO Auto-generated method stub
        JSONObject ret = null;

        DBManager dbmgr = new DBManager();
        ret = dbmgr.selectUniqueAnswer(q, tenant);
        return ret;
    }

    public String addQA(String libraryPk, String question, String answer, String[] questions, Tenant tenant)
            throws SQLException {
        String id = null;
        KBQA qa = new KBQA();
        qa.setId(UUID.randomUUID().toString());
        qa.setLibraryPk(libraryPk);
        qa.setQuestion(question);
        qa.setAnswer(answer);

        if (questions != null && questions.length > 0) {
            qa.setQuestions(questions);
        }

        DBManager dbmgr = new DBManager();

        id = dbmgr.insertQA(qa, tenant);

        return id;
    }

    public JSONObject query(Tenant tenant) {
        JSONObject ret = new JSONObject();

        JSONObject res = new JSONObject();
        JSONArray docs = new JSONArray();
        res.put("docs", docs);
        ret.put("response", res);

        JSONObject resHeader = new JSONObject();
        ret.put("responseHeader", resHeader);

        DBManager dbmgr = new DBManager();
        List list = dbmgr.selectALLQA(tenant);
        for (int i = 0, len = list.size(); i < len; i++) {
            KBQA qa = (KBQA) list.get(i);
            docs.add(qa.toJSON());
        }
        return ret;
    }

    public boolean updateQA(String id, String q, String a, String[] qs, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        // kbqa.setQtype(t);

        kbqa.setQuestions(qs);

        DBManager dbmgr = new DBManager();

        boolean success = dbmgr.updateQA(kbqa, tenant);
        return success;
    }

    public JSONObject queryById(Tenant tenant, String id) {
        // TODO Auto-generated method stub
        JSONObject jsonQA = new JSONObject();
        DBManager dbmgr = new DBManager();
        JSONObject json = dbmgr.selectQAById(tenant, id);
        JSONArray array = dbmgr.selectSimilarQByQid(tenant, id);
        json.put("qs", array);
        return json;
    }

    public String addTongji(String q, String a, Tenant tenant) {
        // TODO Auto-generated method stub
        String id = "";
        KBQA qa = new KBQA();
        qa.setQuestion(q);
        qa.setAnswer(a);
        DBManager dbmgr = new DBManager();
        id = dbmgr.insertQA_tj(qa, tenant);
        return id;
    }

    public JSONArray topN(int topn, Tenant tenant) {
        // TODO Auto-generated method stub
        JSONArray array = new JSONArray();

        DBManager dbmgr = new DBManager();
        array = dbmgr.query_tj(topn, tenant);
        return array;
    }

    public String addFeedback(String id, String score, Tenant tenant) {
        DBManager dbmgr = new DBManager();
        KBQAFeedback fb = new KBQAFeedback();
        fb.setId(id);
        fb.setScore(score);
        String fb_id = dbmgr.addKBQAFeedback(fb, tenant);
        return fb_id;
    }

    public boolean delQA(String id, Tenant tenant) throws SQLException {
        DBManager dbmgr = new DBManager();
        boolean success = dbmgr.delQA(id, tenant);
        return success;
    }

    public boolean updateQAQS(String id, String q, String a, JSONArray qs, Tenant tenant) throws SQLException {
        // 根据数据构建Entity
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        // kbqa.setQtype(t);

        for (int i = 0, len = qs.size(); i < len; i++) {
            JSONObject json = qs.getJSONObject(i);
            KBQS kbqs = new KBQS();
            kbqs.setId(json.getString("id"));
            kbqs.setQuestion(json.getString("question"));
            kbqs.setQid(kbqa.getId());
            kbqs.setStatus(json.getString("status"));
            kbqa.getQS().add(kbqs);
        }

        DBManager dbmgr = new DBManager();

        boolean success = dbmgr.updateQAQS(kbqa, tenant);
        return success;
    }

    public boolean delQABat(String[] ids, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
        for (int i = 0, len = ids.length; i < len; i++) {
            String id = ids[i];
            this.delQA(id, tenant);
        }
        return true;
    }
    
    /**
     * pengjf 2017年7月13日17:58:03
     * 保存收藏
     * @param tenantid  租户
     * @param userid 用户
     * @param kbindexid kbindexinfo表中id
     * @param title
     * @param descript
     * @param url
     * @param qid 目前没用到
     * @param qsid 目前没用到
     * @param question 目前没用到
     * @param answer 目前没用到
     * @param tenant 目前没用到
     * @return
     * @throws SQLException
     * 
     */
    public String addStore(QaCollection qac ,Tenant tenant)
            throws SQLException {
        String id = null;
        qac.setId(UUID.randomUUID().toString());
        DBManager dbmgr = new DBManager();

        id = dbmgr.insertQACollection(qac, tenant);

        return id;
    }
    
    public JSONObject queryQaCollectionByUserid(Tenant tenant,  String userid) {
        // TODO Auto-generated method stub
    	JSONObject json = new JSONObject();
        DBManager dbmgr = new DBManager();
        JSONArray array = dbmgr.selectQaCoolectionByUserid(tenant, userid);
        json.put("qacollection", array);
        return json;
    }
    
}
