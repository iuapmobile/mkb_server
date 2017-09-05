package com.yyuap.mkb.processor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.QaCollection;
import com.yyuap.mkb.nlp.BaiAdapter;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBDuplicateSQLException;

public class QAManager {
    public QAManager() {

    }

    public JSONObject getUniqueAnswer(String q, Tenant tenant) throws SolrServerException, IOException {
        // TODO Auto-generated method stub
        JSONObject ret = null;

        DBManager dbmgr = new DBManager();
        ret = dbmgr.selectUniqueAnswer(q, tenant);
        if (ret == null || ret.equals("")) {
            // 1、通过solr查询出前x条，进行相似度处理

            String corename = tenant.gettqakbcore();

            SolrManager solr = new SolrManager(corename);
            JSONObject requestParam = new JSONObject();
            requestParam.put("q", q);
            requestParam.put("num", 3);
            JSONObject topQ = solr.queryQuestion(requestParam);
            JSONArray array = topQ.getJSONObject("response").getJSONArray("docs");

            Float scoreMax = 0f;
            for (int i = 0, len = array.size(); i < len; i = i+100) {
                String _q = array.getJSONObject(i).getString("question");
                String _a = array.getJSONObject(i).getString("answer");
                String _url = array.getJSONObject(i).getString("url");

                float simscoreDef = 0f;
                try {
                    simscoreDef = tenant.getSimscore();
                } catch (Exception e) {
                    simscoreDef = 0.618f;
                }
                float score = 0f;

                if (_q == null || _q.equals("") || q == null || q.equals("")) {

                } else if (_q.toLowerCase().indexOf(q.toLowerCase()) >= 0) {
                    score = 2;
                } else {
                    BaiAdapter bai = new BaiAdapter();
                    score = bai.simnet(_q, q);
                }

                if (score > simscoreDef) {
                    if (scoreMax < score) {
                        scoreMax = score;
                        JSONObject botRes = new JSONObject();
                        botRes.put("request_q", q);
                        botRes.put("kb_q", _q);
                        botRes.put("a", _a);
                        botRes.put("url", _url);
                        botRes.put("simscore", score);
                        botRes.put(q, _a);
                        ret = botRes;
                    }
                } else {
                    // 知识库中没有答案
                }
            }

        }
        return ret;
    }

    public String addQA(JSONObject json, Tenant tenant) throws Exception {
        String id = null;
        KBQA qa = new KBQA();
        qa.setId(UUID.randomUUID().toString());
        qa.setLibraryPk(json.getString("libraryPk"));
        qa.setQuestion(json.getString("q"));
        qa.setAnswer(json.getString("a"));
        qa.setIstop(json.getString("istop"));// 是否置顶
        qa.setUrl(json.getString("url"));// 是否置顶
        qa.setKbid(json.getString("kbid"));// 是否置顶
        qa.setQtype(json.getString("qtype"));

        String[] questions = (String[]) json.get("qs");
        if (questions != null && questions.length > 0) {
            qa.setQuestions(questions);
        }

        DBManager dbmgr = new DBManager();

        return dbmgr.insertQA(qa, tenant);
    }

    // public String addQA(KBQA kbqa, Tenant tenant) throws Exception {
    // String id = null;
    // KBQA qa = new KBQA();
    // qa.setId(UUID.randomUUID().toString());
    // qa.setLibraryPk(libraryPk);
    // qa.setQuestion(question);
    // qa.setAnswer(answer);
    // qa.setIstop(istop);// 是否置顶
    //
    // //qa.setUrl(url);
    //
    // if (questions != null && questions.length > 0) {
    // qa.setQuestions(questions);
    // }
    //
    // DBManager dbmgr = new DBManager();
    //
    // return dbmgr.insertQA(qa, tenant);
    // }

    public JSONObject query(Tenant tenant, String content) {
        JSONObject ret = new JSONObject();

        JSONObject res = new JSONObject();
        JSONArray docs = new JSONArray();
        res.put("docs", docs);
        ret.put("response", res);

        JSONObject resHeader = new JSONObject();
        ret.put("responseHeader", resHeader);

        DBManager dbmgr = new DBManager();
        if (content == null || "".equals(content)) {
            List list = dbmgr.selectALLQA(tenant);
            for (int i = 0, len = list.size(); i < len; i++) {
                KBQA qa = (KBQA) list.get(i);
                docs.add(qa.toJSON());
            }
        } else {
            List list = dbmgr.selectQA(tenant, content);
            for (int i = 0, len = list.size(); i < len; i++) {
                KBQA qa = (KBQA) list.get(i);
                docs.add(qa.toJSON());
            }
        }

        return ret;
    }

    public boolean updateQA(String id, String q, String a, String[] qs, Tenant tenant, String istop)
            throws SQLException {
        // TODO Auto-generated method stub
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        kbqa.setIstop(istop);// 是否置顶
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

    public String updateQAQS(String id, String q, String a, JSONArray qs,String url,String qtype, Tenant tenant) throws SQLException {
        // 根据数据构建Entity
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        kbqa.setUrl(url);
        kbqa.setQtype(qtype);
        // kbqa.setQtype(t);
        if(qs!=null){
        	for (int i = 0, len = qs.size(); i < len; i++) {
                JSONObject json = qs.getJSONObject(i);
                KBQS kbqs = new KBQS();
                kbqs.setId(json.getString("id"));
                kbqs.setQuestion(json.getString("question"));
                kbqs.setQid(kbqa.getId());
                kbqs.setStatus(json.getString("status"));
                kbqa.getQS().add(kbqs);
            }
        }
        

        DBManager dbmgr = new DBManager();

        String qid = dbmgr.updateQAQS(kbqa, tenant);
        return qid;
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
     * pengjf 2017年7月13日17:58:03 保存收藏
     * 
     * @param tenantid
     *            租户
     * @param userid
     *            用户
     * @param kbindexid
     *            kbindexinfo表中id
     * @param title
     * @param descript
     * @param url
     * @param qid
     *            目前没用到
     * @param qsid
     *            目前没用到
     * @param question
     *            目前没用到
     * @param answer
     *            目前没用到
     * @param tenant
     *            目前没用到
     * @return
     * @throws SQLException
     * 
     */
    public String addStore(QaCollection qac, Tenant tenant) throws SQLException {
        String id = null;
        qac.setId(UUID.randomUUID().toString());
        DBManager dbmgr = new DBManager();

        id = dbmgr.insertQACollection(qac, tenant);

        return id;
    }

    public JSONObject queryQaCollectionByUserid(Tenant tenant, String userid) {
        // TODO Auto-generated method stub
        JSONObject json = new JSONObject();
        DBManager dbmgr = new DBManager();
        JSONArray array = dbmgr.selectQaCoolectionByUserid(tenant, userid);
        json.put("qacollection", array);
        return json;
    }

    /**
     * 取消收藏
     * 
     * @param id
     * @param tenant
     * @return
     * @throws SQLException
     */
    public boolean deleteCollect(String id, Tenant tenant) throws SQLException {
        DBManager dbmgr = new DBManager();

        boolean flag = dbmgr.deleteCollect(id, tenant);

        return flag;
    }

    public JSONArray exportQA(Tenant tenant) {
        // TODO Auto-generated method stub
        DBManager dbmgr = new DBManager();
        JSONArray array = dbmgr.exportExcelQA(tenant);
        return array;
    }

    public boolean setIsTop(String qaid, String istop, Tenant tenant) throws SQLException {
        DBManager dbmgr = new DBManager();

        boolean flag = dbmgr.setIsTop(qaid, istop, tenant);

        return flag;
    }
    
    
    public Map<String,String> queryDataTj(String day, Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryDataTj(day, tenant);
    }
    
    public String queryBotServicesTj(Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryBotServicesTj(tenant);
    }

}
