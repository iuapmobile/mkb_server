package com.yyuap.mkb.processor;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.pl.DBManager;

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

    public boolean addQA(String question, String answer, String[] questions, Tenant tenant) {
        boolean success = false;
        KBQA qa = new KBQA();
        qa.setId(UUID.randomUUID().toString());
        qa.setQuestion(question);
        qa.setAnswer(answer);

        if (questions != null && questions.length > 0) {
            qa.setQuestions(questions);
        }

        DBManager dbmgr = new DBManager();
        try {
            success = dbmgr.insertQA(qa, tenant);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return success;
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

    public boolean updateQA(String id, String q, String a, String[] qs, Tenant tenant) {
        // TODO Auto-generated method stub
        KBQA kbqa = new KBQA();
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        // kbqa.setQtype(t);

        kbqa.setQuestions(qs);

        DBManager dbmgr = new DBManager();

        // success = dbmgr.insertQA(qa, tenant);
        return false;
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

    public JSONArray topN(String topn, Tenant tenant) {
        // TODO Auto-generated method stub
        JSONArray array = new JSONArray();

        DBManager dbmgr = new DBManager();
        array = dbmgr.query_tj(tenant);
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
}
