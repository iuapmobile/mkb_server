package com.yyuap.mkb.processor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQAFeedback;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.entity.QaCollection;
import com.yyuap.mkb.fileUtil.KnowlegeType;
import com.yyuap.mkb.nlp.BaiAdapter;
import com.yyuap.mkb.pl.DBManager;

public class QAManager {
    public QAManager() {

    }

    public JSONObject getUniqueAnswer(String q, Tenant tenant,String[] tag) throws SolrServerException, IOException {
        // TODO Auto-generated method stub
        JSONObject ret = null;

        DBManager dbmgr = new DBManager();
        ret = dbmgr.selectUniqueAnswer(q, tenant,tag);
        if (ret == null || ret.equals("")) {
            // 1、通过solr查询出前x条，进行相似度处理

            String corename = tenant.gettqakbcore();

            SolrManager solr = new SolrManager(corename);
            JSONObject requestParam = new JSONObject();
            requestParam.put("q", q);
            requestParam.put("num", 3);
            JSONObject topQ = solr.queryQuestion(requestParam,tag);
            JSONArray array = topQ.getJSONObject("response").getJSONArray("docs");

            Float scoreMax = 0f;
            for (int i = 0, len = array.size(); i < len; i = i+100) {
                String _q = array.getJSONObject(i).getString("question");
                String _a = array.getJSONObject(i).getString("answer");
                String _url = array.getJSONObject(i).getString("url");
                String _qtype = array.getJSONObject(i).getString("qtype");
                String id = array.getJSONObject(i).getString("id");

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
                        botRes.put("qtype", _qtype);
                        botRes.put("id", id);
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
        qa.setKtype(json.getString("ktype"));//该问答的类型
        qa.setExt_scope(json.getString("ext_scope"));//可见范围
        qa.setDomain(json.getString("domain"));
        qa.setProduct(json.getString("product"));
        qa.setSubproduct(json.getString("subproduct"));
        qa.setExtend0(json.getString("extend0"));
        qa.setExtend1(json.getString("extend1"));
        qa.setExtend2(json.getString("extend2"));
        qa.setExtend3(json.getString("extend3"));
        qa.setExtend4(json.getString("extend4"));
        qa.setExtend5(json.getString("extend5"));
        qa.setExtend6(json.getString("extend6"));
        qa.setExtend7(json.getString("extend7"));
        qa.setExtend8(json.getString("extend8"));
        qa.setExtend9(json.getString("extend9"));
        qa.setExtend10(json.getString("extend10"));
        qa.setExtend11(json.getString("extend11"));
        qa.setExtend12(json.getString("extend12"));
        qa.setExtend13(json.getString("extend13"));
        qa.setExtend14(json.getString("extend14"));
        qa.setExtend15(json.getString("extend15"));
        qa.setExtend16(json.getString("extend16"));
        qa.setExtend17(json.getString("extend17"));
        qa.setExtend17(json.getString("extend18"));
        qa.setExtend19(json.getString("extend19"));
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

    public boolean updateQA(String id, String q, String a, String[] qs, Tenant tenant, String istop,String ext_scope,String domain,String product,String subproduct,JSONObject json)
            throws SQLException {
        // TODO Auto-generated method stub
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        kbqa.setIstop(istop);// 是否置顶
        // kbqa.setQtype(t);
        kbqa.setExt_scope(ext_scope);
        kbqa.setDomain(domain);
        kbqa.setProduct(product);
        kbqa.setSubproduct(subproduct);
        kbqa.setExtend0(json.getString("extend0"));
        kbqa.setExtend1(json.getString("extend1"));
        kbqa.setExtend2(json.getString("extend2"));
        kbqa.setExtend3(json.getString("extend3"));
        kbqa.setExtend4(json.getString("extend4"));
        kbqa.setExtend5(json.getString("extend5"));
        kbqa.setExtend6(json.getString("extend6"));
        kbqa.setExtend7(json.getString("extend7"));
        kbqa.setExtend8(json.getString("extend8"));
        kbqa.setExtend9(json.getString("extend9"));
        kbqa.setExtend10(json.getString("extend10"));
        kbqa.setExtend11(json.getString("extend11"));
        kbqa.setExtend12(json.getString("extend12"));
        kbqa.setExtend13(json.getString("extend13"));
        kbqa.setExtend14(json.getString("extend14"));
        kbqa.setExtend15(json.getString("extend15"));
        kbqa.setExtend16(json.getString("extend16"));
        kbqa.setExtend17(json.getString("extend17"));
        kbqa.setExtend17(json.getString("extend18"));
        kbqa.setExtend19(json.getString("extend19"));
        
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

    public String addTongji(String q, String a,String qid, Tenant tenant) {
        // TODO Auto-generated method stub
        String id = "";
        KBQA qa = new KBQA();
        qa.setQuestion(q);
        qa.setAnswer(a);
        qa.setId(qid);
        DBManager dbmgr = new DBManager();
        id = dbmgr.insertQA_tj(qa, tenant);
        return id;
    }

    public JSONArray topN(int topn, Tenant tenant,String tag) {
        // TODO Auto-generated method stub
        JSONArray array = new JSONArray();

        DBManager dbmgr = new DBManager();
        array = dbmgr.query_tj(topn, tenant,tag);
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


    public String updateQAQS(String id, String q, String a, JSONArray qs,String url,String qtype, Tenant tenant,String ext_scope,String domain,String product,String subproduct,JSONObject extendjson) throws SQLException {

        // 根据数据构建Entity
        KBQA kbqa = new KBQA();
        kbqa.setId(id);
        kbqa.setQuestion(q);
        kbqa.setAnswer(a);
        kbqa.setUrl(url);

        kbqa.setQtype(qtype);

        kbqa.setExt_scope(ext_scope);
        kbqa.setDomain(domain);
        kbqa.setProduct(product);
        kbqa.setSubproduct(subproduct);
        kbqa.setExtend0(extendjson.getString("extend0"));
        kbqa.setExtend1(extendjson.getString("extend1"));
        kbqa.setExtend2(extendjson.getString("extend2"));
        kbqa.setExtend3(extendjson.getString("extend3"));
        kbqa.setExtend4(extendjson.getString("extend4"));
        kbqa.setExtend5(extendjson.getString("extend5"));
        kbqa.setExtend6(extendjson.getString("extend6"));
        kbqa.setExtend7(extendjson.getString("extend7"));
        kbqa.setExtend8(extendjson.getString("extend8"));
        kbqa.setExtend9(extendjson.getString("extend9"));
        kbqa.setExtend10(extendjson.getString("extend10"));
        kbqa.setExtend11(extendjson.getString("extend11"));
        kbqa.setExtend12(extendjson.getString("extend12"));
        kbqa.setExtend13(extendjson.getString("extend13"));
        kbqa.setExtend14(extendjson.getString("extend14"));
        kbqa.setExtend15(extendjson.getString("extend15"));
        kbqa.setExtend16(extendjson.getString("extend16"));
        kbqa.setExtend17(extendjson.getString("extend17"));
        kbqa.setExtend17(extendjson.getString("extend18"));
        kbqa.setExtend19(extendjson.getString("extend19"));

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
    
    public String addKbInfo(JSONObject json, Tenant tenant) throws Exception {
    	String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        KBIndex vo = new KBIndex();
        vo.setId(UUID.randomUUID().toString());
        vo.setTitle(json.getString("title"));
        vo.setDescript(json.getString("descript"));
        vo.setDescriptImg(json.getString("descriptImg"));
        vo.setText(json.getString("text"));
        vo.setUrl(json.getString("url"));
        vo.setAuthor(json.getString("author"));
        if(null == json.getString("createTime") || "".equals(json.getString("createTime"))){
        	 vo.setCreateTime(datetime);
        }else{
        	 vo.setCreateTime(json.getString("createTime"));
        }
        if(null == json.getString("updateTime") || "".equals(json.getString("updateTime"))){
        	vo.setUpdateTime(datetime);
        }else{
        	vo.setUpdateTime(json.getString("updateTime"));
        }
        vo.setWeight(json.getString("weight"));
        vo.setKeywords(json.getString("keywords"));
        vo.setTag(json.getString("label"));
        vo.setContent(json.getString("content"));
        vo.setCategory(json.getString("category"));
        vo.setGrade(json.getString("grade"));
        vo.setDomain(json.getString("domain"));
        vo.setProduct(json.getString("product"));
        vo.setSubproduct(json.getString("subproduct"));
        vo.setS_top(json.getString("s_top"));
        vo.setS_kbsrc(json.getString("s_kbsrc"));
        vo.setS_kbcategory(json.getString("s_kbcategory"));
        vo.setS_hot(json.getString("s_hot"));
        vo.setKbid(json.getString("kbid"));
        vo.setExt_supportsys(json.getString("ext_supportsys"));
        vo.setExt_resourcetype(json.getString("ext_resourcetype"));
        vo.setExt_scope(json.getString("ext_scope"));
        //下面两个 实在添加solr时用到
        vo.setQuestion(json.getString("title"));
        vo.setAnser(json.getString("descript"));
        if(json.getString("resourcetype")==null){
        	vo.setKtype(KnowlegeType.KBINDEXINFO_KTYPE);
        }else{
        	vo.setKtype(json.getString("resourcetype"));
        }
        


        DBManager dbmgr = new DBManager();

        return dbmgr.insertKbInfo(vo, tenant);
    }
    
    public boolean updateKbInfo(JSONObject json, Tenant tenant)
            throws Exception {
    	String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    	KBIndex vo = new KBIndex();
        vo.setId(json.getString("id"));
        vo.setTitle(json.getString("title"));
        vo.setDescript(json.getString("descript"));
        vo.setDescriptImg(json.getString("descriptImg"));
        vo.setText(json.getString("text"));
        vo.setUrl(json.getString("url"));
        vo.setAuthor(json.getString("author"));
        vo.setCreateTime(json.getString("createTime"));
    	vo.setUpdateTime(datetime);
        vo.setWeight(json.getString("weight"));
        vo.setKeywords(json.getString("keywords"));
        vo.setTag(json.getString("label"));
        vo.setContent(json.getString("content"));
        vo.setCategory(json.getString("category"));
        vo.setGrade(json.getString("grade"));
        vo.setDomain(json.getString("domain"));
        vo.setProduct(json.getString("product"));
        vo.setSubproduct(json.getString("subproduct"));
        vo.setS_top(json.getString("s_top"));
        vo.setS_kbsrc(json.getString("s_kbsrc"));
        vo.setS_kbcategory(json.getString("s_kbcategory"));
        vo.setS_hot(json.getString("s_hot"));
        vo.setKbid(json.getString("kbid"));
        vo.setExt_supportsys(json.getString("ext_supportsys"));
        vo.setExt_resourcetype(json.getString("ext_resourcetype"));
        vo.setExt_scope(json.getString("ext_scope"));
        //下面两个 实在添加solr时用到
        vo.setQuestion(json.getString("title"));
        vo.setAnser(json.getString("descript"));
        if(json.getString("resourcetype")==null){
        	vo.setKtype(KnowlegeType.KBINDEXINFO_KTYPE);
        }else{
        	vo.setKtype(json.getString("resourcetype"));
        }
        DBManager dbmgr = new DBManager();

        boolean success = dbmgr.updateKbInfo(vo, tenant);
        return success;
    }
    
    public boolean delKbInfoBat(String[] ids, Tenant tenant) throws SQLException {
        // TODO Auto-generated method stub
    	DBManager dbmgr = new DBManager();
        boolean success = dbmgr.delKbInfo(ids, tenant);
        return success;
    }
    
    /**
     *  根据表名，查询表中字段 全部
     * @param tenant
     * @param tableName 表名
     * @return
     */
    public JSONArray queryFieldForTable(String tableName,Tenant tenant) {
        // TODO Auto-generated method stub
        JSONObject jsonQA = new JSONObject();
        DBManager dbmgr = new DBManager();
        JSONArray json = dbmgr.queryFieldForTable(tenant, tableName);
        return json;
    }
    
    /**
     *  根据表名，查询表中字段 每个租户已定义的
     * @param tenant
     * @param tableName 表名
     * @return
     */
    public JSONArray queryFieldForTableTenant(String tableName,Tenant tenant) {
        // TODO Auto-generated method stub
        JSONObject jsonQA = new JSONObject();
        DBManager dbmgr = new DBManager();
        JSONArray json = dbmgr.queryFieldForTableTenant(tenant, tableName);
        return json;
    }
    
    /**
     *  根据表名，保存表中字段
     * @param tenant
     * @param tableName 表名
     * @return
     * @throws Exception 
     */
    public boolean saveFieldForTable(JSONArray paramArr,Tenant tenant) throws Exception {
        // TODO Auto-generated method stub
        JSONObject jsonQA = new JSONObject();
        DBManager dbmgr = new DBManager();
        boolean ret = dbmgr.saveFieldForTable(tenant, paramArr);
        return ret;
    }
    
    public Map<String,String> queryDimensionTj(String field, Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryDimensionTj(field, tenant);
    }
    public Map<String,String> queryQaTopTj(int topn,String istop,  Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryQaTopTj(topn,istop, tenant);
    }
    
    public JSONArray queryDimensionData(String field, Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryDimensionData(field, tenant);
    }
    
    public Map<String,String> queryQaTopTjForDimension(int topn,String istop,String field,String fieldValue,  Tenant tenant) throws Exception {

        DBManager dbmgr = new DBManager();

        return dbmgr.queryQaTopTjForDimension(topn,istop,field,fieldValue, tenant);
    }

    public JSONObject queryQACommandByTenant(Tenant tenant) {
        JSONObject json = new JSONObject();
        
        DBManager dbmgr = new DBManager();
        JSONArray array = dbmgr.selectQACommandByTenant(tenant);
        
        json.put("docs", array);
        return json;
    }
    
}
