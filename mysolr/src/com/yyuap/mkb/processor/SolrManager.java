package com.yyuap.mkb.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.entity.KBQS;
import com.yyuap.mkb.fileUtil.KnowlegeType;
import com.yyuap.mkb.nlp.SAConfig;
import com.yyuap.mkb.nlp.SemanticAnalysis;
import com.yyuap.mkb.pl.DBManager;

public class SolrManager {
    private String SOLR_URL = "http://127.0.0.1:8080/kb/kbcore1";
    // private final static String SOLR_URL =
    // "http://127.0.0.1:8001/kb/kbcore1";
    // private final static String SOLR_URL =
    // "http://127.0.0.1:8002/kb/kbcore1";

    private static CoreContainer initializer = null;
    private static CoreContainer coreContainer = null;
    private static EmbeddedSolrServer server = null;
    HttpSolrClient solr = null;

    public SolrManager(String kbcore) {
        String x = "ddd";
        if (x == "ddd") {
            SOLR_URL = "http://127.0.0.1:8080/kb/" + kbcore;
        }
    }

    public SolrManager(String ip, String port, String kb, String kbcore) {
        SOLR_URL = "http://" + ip + ":" + port + "/" + kb + "/" + kbcore + "";
    }

    private HttpSolrClient getHttpSolrClient() {
        try {

            if (solr == null) {
                solr = new HttpSolrClient(SOLR_URL);

                solr.setConnectionTimeout(1000);
                solr.setDefaultMaxConnectionsPerHost(100);
                solr.setMaxTotalConnections(100);
            }
            return solr;
        } catch (Exception e) {
            System.out.println("请检查tomcat服务器或端口是否开启!");
            e.printStackTrace();
        }
        return null;
    }
    /*
     * static { try { System.setProperty("solr.solr.home", SOLR_CORE);
     * initializer = new CoreContainer.Initializer(); coreContainer = new
     * CoreContainer();
     * 
     * server = new EmbeddedSolrServer(coreContainer, ""); } catch (Exception e)
     * { e.printStackTrace(); } }
     */

    public void query2() throws Exception {

        try {
            SolrQuery q = new SolrQuery();
            q.setQuery("*:*");
            q.setStart(0);
            q.setRows(20);
            SolrDocumentList list = server.query(q).getResults();
            System.out.println(list.getNumFound());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            coreContainer.shutdown();
        }
    }

    public void deleteAllDoc() throws Exception {
        try {
            server.deleteByQuery("*:*");
            server.commit();
            query2();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            coreContainer.shutdown();
        }
    }

    /**
     * pengjf
     * 批量删除solr 根据id
     * @param ids
     */
    public void delBatById(List<String> ids) {
        try {
            HttpSolrClient client = this.getHttpSolrClient();
            client.deleteById(ids);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void delDocById(String id) {
        try {
            HttpSolrClient client = this.getHttpSolrClient();
            client.deleteById(id);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delDocByQid(String id) {
        try {
            HttpSolrClient client = this.getHttpSolrClient();
            client.deleteByQuery("qid:" + id);
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDoc(KBIndex kbindex) throws SolrServerException, IOException {
        String id = kbindex.getId();
        String title = kbindex.getTitle();
        String descript = kbindex.getDescript();
        String question = kbindex.getQuestion();
        String answer = kbindex.getAnswer();
        String descriptImg = kbindex.getDescriptImg();
        String keywords = kbindex.getKeywords();
        String url = kbindex.getUrl();
        String text = kbindex.getText();
        String qid = kbindex.getQid();
        String kbid = kbindex.getKbid();
        String qtype = kbindex.getQtype();
        String createTime = kbindex.getCreateTime();
        String updateTime = kbindex.getUpdateTime();
        String ext_scope = kbindex.getExt_scope();//可见范围
        String ktype = kbindex.getKtype();//知识类型
        String domain = kbindex.getDomain();//领域
        String product = kbindex.getProduct();//产品
        String subproduct = kbindex.getSubproduct();//子产品
        String tag = kbindex.getTag();//标签
        String author = kbindex.getAuthor();//作者
        String extend0 = kbindex.getExtend0();
        String extend1 = kbindex.getExtend1();
        String extend2 = kbindex.getExtend2();
        String extend3 = kbindex.getExtend3();
        String extend4 = kbindex.getExtend4();
        String extend5 = kbindex.getExtend5();
        String extend6 = kbindex.getExtend6();
        String extend7 = kbindex.getExtend7();
        String extend8 = kbindex.getExtend8();
        String extend9 = kbindex.getExtend9();
        String extend10 = kbindex.getExtend10();
        String extend11 = kbindex.getExtend11();
        String extend12 = kbindex.getExtend12();
        String extend13 = kbindex.getExtend13();
        String extend14 = kbindex.getExtend14();
        String extend15 = kbindex.getExtend15();
        String extend16 = kbindex.getExtend16();
        String extend17 = kbindex.getExtend17();
        String extend18 = kbindex.getExtend18();
        String extend19 = kbindex.getExtend19();
        
        // 1.创建链接
        @SuppressWarnings("deprecation")
        SolrClient solr = this.getHttpSolrClient();

        // 2.创建一文档对象
        SolrInputDocument document = new SolrInputDocument();

        // 3.向文档对象中添加域 （先定义后使用）
        document.addField("id", id);
        document.addField("title", title);
        document.addField("descript", descript);
        document.addField("question", question);
        document.addField("answer", answer);
        document.addField("descriptImg", descriptImg);
        document.addField("keywords", keywords);
        document.addField("url", url);
        document.addField("text", text);
        document.addField("qid", qid);
        document.addField("kbid", kbid);
        document.addField("qtype", qtype);
        document.addField("createTime", createTime);
        document.addField("updateTime", updateTime);
        document.addField("ktype", ktype);
        document.addField("domain", domain);
        document.addField("product", product);
        document.addField("subproduct", subproduct);
        document.addField("tag", tag);
        document.addField("author", author);
        document.addField("extend0", extend0);
        document.addField("extend1", extend1);
        document.addField("extend2", extend2);
        document.addField("extend3", extend3);
        document.addField("extend4", extend4);
        document.addField("extend5", extend5);
        document.addField("extend6", extend6);
        document.addField("extend7", extend7);
        document.addField("extend8", extend8);
        document.addField("extend9", extend9);
        document.addField("extend10", extend10);
        document.addField("extend11", extend11);
        document.addField("extend12", extend12);
        document.addField("extend13", extend13);
        document.addField("extend14", extend14);
        document.addField("extend15", extend15);
        document.addField("extend16", extend16);
        document.addField("extend17", extend17);
        document.addField("extend18", extend18);
        document.addField("extend19", extend19);
        
        if(null != ext_scope){
        	document.addField("ext_scope", ext_scope);
        }
        

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long times = System.currentTimeMillis();
        System.out.println(times);
        Date date = new Date(times);
        String tim = sdf.format(date);
        System.out.println(tim);

        // document.addField("createTime", tim);
        // document.addField("updateTime", tim);

        // 4.提交文档到索引库
        solr.add(document);
        // 5.提交
        solr.commit();
    }

    public void updateDoc(KBIndex kbindex) throws SolrServerException, IOException {

        String id = kbindex.getId();
        String title = kbindex.getTitle();
        String descript = kbindex.getDescript();
        String question = kbindex.getQuestion();
        String answer = kbindex.getAnswer();
        String descriptImg = kbindex.getDescriptImg();
        String keywords = kbindex.getKeywords();
        String url = kbindex.getUrl();
        String text = kbindex.getText();
        String qid = kbindex.getQid();
        String kbid = kbindex.getKbid();
        String qtype = kbindex.getQtype();
        String createTime = kbindex.getCreateTime();
        String updateTime = kbindex.getUpdateTime();
        String ext_scope = kbindex.getExt_scope();//可见范围
        String ktype = kbindex.getKtype();//知识类型
        String domain = kbindex.getDomain();//领域
        String product = kbindex.getProduct();//产品
        String subproduct = kbindex.getSubproduct();//子产品
        String tag = kbindex.getTag();//标签
        String author = kbindex.getAuthor();//作者
        String extend0 = kbindex.getExtend0();
        String extend1 = kbindex.getExtend1();
        String extend2 = kbindex.getExtend2();
        String extend3 = kbindex.getExtend3();
        String extend4 = kbindex.getExtend4();
        String extend5 = kbindex.getExtend5();
        String extend6 = kbindex.getExtend6();
        String extend7 = kbindex.getExtend7();
        String extend8 = kbindex.getExtend8();
        String extend9 = kbindex.getExtend9();
        String extend10 = kbindex.getExtend10();
        String extend11 = kbindex.getExtend11();
        String extend12 = kbindex.getExtend12();
        String extend13 = kbindex.getExtend13();
        String extend14 = kbindex.getExtend14();
        String extend15 = kbindex.getExtend15();
        String extend16 = kbindex.getExtend16();
        String extend17 = kbindex.getExtend17();
        String extend18 = kbindex.getExtend18();
        String extend19 = kbindex.getExtend19();
        // 1.创建链接
        @SuppressWarnings("deprecation")
        SolrClient solr = this.getHttpSolrClient();

        // 2.创建一文档对象
        SolrInputDocument document = new SolrInputDocument();

        // 3.向文档对象中添加域 （先定义后使用）
        document.addField("id", id);
        document.addField("title", title);
        document.addField("descript", descript);
        document.addField("question", question);
        document.addField("answer", answer);
        document.addField("descriptImg", descriptImg);
        document.addField("keywords", keywords);
        document.addField("url", url);
        document.addField("text", text);
        document.addField("qid", qid);
        document.addField("kbid", kbid);
        document.addField("qtype", qtype);
        document.addField("createTime", createTime);
        document.addField("updateTime", updateTime);
        if(null != ext_scope){
        	document.addField("ext_scope", ext_scope);
        }
        document.addField("ktype", ktype);
        document.addField("domain", domain);
        document.addField("product", product);
        document.addField("subproduct", subproduct);
        document.addField("tag", tag);
        document.addField("author", author);
        document.addField("extend0", extend0);
        document.addField("extend1", extend1);
        document.addField("extend2", extend2);
        document.addField("extend3", extend3);
        document.addField("extend4", extend4);
        document.addField("extend5", extend5);
        document.addField("extend6", extend6);
        document.addField("extend7", extend7);
        document.addField("extend8", extend8);
        document.addField("extend9", extend9);
        document.addField("extend10", extend10);
        document.addField("extend11", extend11);
        document.addField("extend12", extend12);
        document.addField("extend13", extend13);
        document.addField("extend14", extend14);
        document.addField("extend15", extend15);
        document.addField("extend16", extend16);
        document.addField("extend17", extend17);
        document.addField("extend18", extend18);
        document.addField("extend19", extend19);
      
        document.addField("_version_", 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long times = System.currentTimeMillis();
        System.out.println(times);
        Date date = new Date(times);
        String tim = sdf.format(date);
        System.out.println(tim);

        // document.addField("createTime", tim);
        // document.addField("updateTime", tim);

        // 4.提交文档到索引库
        solr.add(document);
        // 5.提交
        solr.commit();
    }

    public void addQADoc(KBQA kbqa) throws SolrServerException, IOException {
        KBIndex kbindex = new KBIndex();

        kbindex.setId(kbqa.getId());// 必须和数据库种对id相同，以便删除
        kbindex.setTitle(kbqa.getQuestion());
        kbindex.setDescript(kbqa.getAnswer());
        kbindex.setQuestion(kbqa.getQuestion());
        kbindex.setAnser(kbqa.getAnswer());
        kbindex.setUrl(kbqa.getUrl());
        kbindex.setKbid(kbqa.getKbid());
        kbindex.setCreateTime(kbqa.getCreateTime());
        kbindex.setUpdateTime(kbqa.getUpdateTime());
        kbindex.setQtype(kbqa.getQtype());
        kbindex.setExt_scope(kbqa.getExt_scope());//可见范围
        kbindex.setKtype(KnowlegeType.QA_KTYPE);//知识类型
        kbindex.setDomain(kbqa.getDomain());//领域
        kbindex.setProduct(kbqa.getProduct());//产品
        kbindex.setSubproduct(kbqa.getSubproduct());//子产品
        kbindex.setExtend0(kbqa.getExtend0());
        kbindex.setExtend1(kbqa.getExtend1());
        kbindex.setExtend2(kbqa.getExtend2());
        kbindex.setExtend3(kbqa.getExtend3());
        kbindex.setExtend4(kbqa.getExtend4());
        kbindex.setExtend5(kbqa.getExtend5());
        kbindex.setExtend6(kbqa.getExtend6());
        kbindex.setExtend7(kbqa.getExtend7());
        kbindex.setExtend8(kbqa.getExtend8());
        kbindex.setExtend9(kbqa.getExtend9());
        kbindex.setExtend10(kbqa.getExtend10());
        kbindex.setExtend11(kbqa.getExtend11());
        kbindex.setExtend12(kbqa.getExtend12());
        kbindex.setExtend13(kbqa.getExtend13());
        kbindex.setExtend14(kbqa.getExtend14());
        kbindex.setExtend15(kbqa.getExtend15());
        kbindex.setExtend16(kbqa.getExtend16());
        kbindex.setExtend17(kbqa.getExtend17());
        kbindex.setExtend18(kbqa.getExtend18());
        kbindex.setExtend19(kbqa.getExtend19());
        

        this.addDoc(kbindex);
    }

    public void addQASimilarDoc(KBQA kbqa) throws SolrServerException, IOException {
        // String[] qs = kbqa.getQuestions();
        ArrayList<KBQS> kbqsList = kbqa.getQS();

        for (int i = 0, len = kbqsList.size(); i < len; i++) {
            KBQS kbqs = kbqsList.get(i);
            if (kbqs == null)
                continue;
            if (kbqs.getId() == null)
                continue;
            String qid = kbqa.getId();
            String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
            // String q = kbqa.getQuestion();
            String a = kbqa.getAnswer();
            String qs = kbqs.getQuestion();
            String url = kbqa.getUrl();
            String kbid = kbqa.getKbid();
            String updateTime = kbqs.getUpdateTime();
            String createTime = kbqs.getCreateTime();

            KBIndex kbindex = new KBIndex();
            kbindex.setId(id);
            kbindex.setTitle(qs);
            kbindex.setDescript(a);
            kbindex.setQuestion(qs);
            kbindex.setAnser(a);
            kbindex.setUrl(url);
            kbindex.setQid(qid);
            kbindex.setKbid(kbid);
            kbindex.setUpdateTime(updateTime);
            kbindex.setCreateTime(createTime);

            this.addDoc(kbindex);
        }

    }

    public void addQASimilarDoc(KBQA kbqa, KBQS kbqs) throws SolrServerException, IOException {
        String qid = kbqa.getId();
        String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
        // String q = kbqa.getQuestion();
        String a = kbqa.getAnswer();
        String qs = kbqs.getQuestion();
        String url = kbqa.getUrl();
        String kbid = kbqa.getKbid();
        String updateTime = kbqs.getUpdateTime();
        String createTime = kbqs.getCreateTime();

        KBIndex kbindex = new KBIndex();
        kbindex.setId(id);
        kbindex.setTitle(qs);
        kbindex.setDescript(a);
        kbindex.setQuestion(qs);
        kbindex.setAnser(a);
        kbindex.setUrl(url);
        kbindex.setQid(qid);
        kbindex.setKbid(kbid);
        kbindex.setUpdateTime(updateTime);
        kbindex.setCreateTime(createTime);

        this.addDoc(kbindex);

    }

    public void updateQASimilarDoc(KBQA kbqa) throws SolrServerException, IOException {
        // String[] qs = kbqa.getQuestions();
        ArrayList<KBQS> kbqsList = kbqa.getQS();

        for (int i = 0, len = kbqsList.size(); i < len; i++) {
            KBQS kbqs = kbqsList.get(i);
            if (kbqs == null)
                continue;
            if (kbqs.getStatus() == null) {
                continue;
            }
            String qid = kbqa.getId();
            String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
            // String q = kbqa.getQuestion();
            String a = kbqa.getAnswer();
            String qs = kbqs.getQuestion();
            String url = kbqa.getUrl();
            String kbid = kbqa.getKbid();
            String qtype = kbqa.getQtype();
            String updateTime = kbqs.getUpdateTime();
            String createTime = kbqs.getCreateTime();

            KBIndex kbindex = new KBIndex();
            kbindex.setId(id);
            kbindex.setTitle(qs);
            kbindex.setDescript(a);
            kbindex.setQuestion(qs);
            kbindex.setAnser(a);
            kbindex.setUrl(url);
            kbindex.setQid(qid);
            kbindex.setKbid(kbid);
            kbindex.setQtype(qtype);
            kbindex.setUpdateTime(updateTime);
            kbindex.setCreateTime(createTime);

            this.updateDoc(kbindex);
        }

    }

    public void updateQASimilarDoc(KBQA kbqa, KBQS kbqs) throws SolrServerException, IOException {
        // String[] qs = kbqa.getQuestions();
        String qid = kbqa.getId();
        String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
        // String q = kbqa.getQuestion();
        String a = kbqa.getAnswer();
        String qs = kbqs.getQuestion();
        String url = kbqa.getUrl();
        String kbid = kbqa.getKbid();
        String qtype = kbqa.getQtype();
        String updateTime = kbqs.getUpdateTime();
        String createTime = kbqs.getCreateTime();

        KBIndex kbindex = new KBIndex();
        kbindex.setId(id);
        kbindex.setTitle(qs);
        kbindex.setDescript(a);
        kbindex.setQuestion(qs);
        kbindex.setAnser(a);
        kbindex.setUrl(url);
        kbindex.setQid(qid);
        kbindex.setKbid(kbid);
        kbindex.setQtype(qtype);
        kbindex.setUpdateTime(updateTime);
        kbindex.setCreateTime(createTime);

        this.updateDoc(kbindex);

    }

    public void delQASimilarDoc(KBQA kbqa, KBQS kbqs) throws SolrServerException, IOException {
        String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
        this.delDocById(id);

    }

    public void delQASimilarDoc(KBQA kbqa) throws SolrServerException, IOException {
        // String[] qs = kbqa.getQuestions();
        ArrayList<KBQS> kbqsList = kbqa.getQS();

        for (int i = 0, len = kbqsList.size(); i < len; i++) {
            KBQS kbqs = kbqsList.get(i);
            if (kbqs == null)
                continue;
            if (kbqs.getStatus() == null) {
                continue;
            }
            String id = kbqs.getId();// 必须和数据库种对id相同，以便删除
            this.delDocById(id);
        }

    }

    public JSONObject query(JSONObject requestParam, Tenant tenant,String id) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();
        // 1、设置solr查询参数
        String _q = requestParam.getString("q");
        
        // 可见范围
        String[] tag = (String[])requestParam.get("tag");

        int rows = 10;
        int start = 0;
        String strNum = requestParam.getString("rows");
        String strStart = requestParam.getString("start");
        if (strNum == null || strNum.equals("")) {
            rows = 10;
        } else {
            try {
                rows = Integer.parseInt(strNum);
            } catch (Exception e) {
                rows = 10;
            }
        }
        if (strStart == null || strStart.equals("")) {
            start = 0;
        } else {
            try {
                start = Integer.parseInt(strStart);
            } catch (Exception e) {
                start = 0;
            }
        }
        String q = _q;
        String sa = requestParam.getString("sa"); // semantic analysis => sa
        if (sa != null && sa.equalsIgnoreCase("true") && !q.equals("")) {
            // 进行语义分析
            SemanticAnalysis sap = new SemanticAnalysis();
            SAConfig conf = new SAConfig();
            conf.httpArg = ("s=" + _q);
            String _keywords = sap.getKeywords(conf);
            q = this.process(_keywords);
        }

        // 2、处理权重

        query.set("defType", "edismax");

        // query.set("op", "AND");

        solrQueryRules rules = new solrQueryRules();

        String qf = requestParam.getString("qf");
        rules.setQF(query, q, qf, tenant);

        String new_q = rules.addFilterQuery(query, q, tenant);
        
       
        if(null == tag){
        	if(null !=id){
        		query.addFilterQuery(" -id:\""+id+"\" AND -qid:[\"\" TO *]  AND -ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围	
        	}else{
        		query.addFilterQuery("-qid:[\"\" TO *]  AND -ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围		
        	}
        	 
        }else if("personinside".equals(tag[0])){//如果 为personinside 内部员工，查询全部
        	if(null !=id){
        		query.addFilterQuery(" -id:\""+id+"\" AND -qid:[\"\" TO *]");  // q中含有subproduct，则fq限定范围	
        	}else{
        		query.addFilterQuery("-qid:[\"\" TO *]");  // q中含有subproduct，则fq限定范围	
        	}
        	 
        }else{
        	if(null !=id){
        		query.addFilterQuery(" -id:\""+id+"\" AND -qid:[\"\" TO *]  AND -ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围	
        	}else{
        		query.addFilterQuery("-qid:[\"\" TO *]  AND -ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围	
        	}
        	 
        }
        // if (q == null || q.equals("")) {
        // q = "*:*";
        // }
        // query.set("q", q);

        if (new_q == null || new_q.trim().equals("")) {
            new_q = "*:*";
        }
        if(null == tag){
        	if(null !=id){
        		new_q = new_q + " AND -id:\""+id+"\" AND -qid:[\"\" TO *] AND -ext_scope:[\"\" TO *]";//*  在solr当中  应该代表 有值  -取反// q中含有subproduct，则fq限定范围	
        	}else{
        		new_q = new_q + " AND -qid:[\"\" TO *] AND -ext_scope:[\"\" TO *]";//*  在solr当中  应该代表 有值  -取反// q中含有subproduct，则fq限定范围	
        	}
        	
        }else if("personinside".equals(tag[0])){//如果 为personinside 内部员工，查询全部
        	if(null !=id){
        		new_q = new_q + "  AND -id:\""+id+"\"  AND -qid:[\"\" TO *]";//*  在solr当中  应该代表 有值  -取反// q中含有subproduct，则fq限定范围	
        	}else{
        		new_q = new_q + " AND -qid:[\"\" TO *]";//*  在solr当中  应该代表 有值  -取反// q中含有subproduct，则fq限定范围	
        	}
        	
        }else{
        	if(null !=id){
        		new_q = new_q + "  AND -id:\""+id+"\"  AND -qid:[\"\" TO *] AND -ext_scope:[\"\" TO *]";
        	}else{
        		new_q = new_q + " AND -qid:[\"\" TO *] AND -ext_scope:[\"\" TO *]";
        	}
        	
        }
        
        query.set("q", new_q);

        rules.addSort(query, q, tenant);

        // 参数fq, 给query增加过滤查询条件
        // query.addFilterQuery("id:[0 TO 9]");//id为0-4

        // 给query增加布尔过滤条件
        // query.addFilterQuery("description:演员"); //description字段中含有“演员”两字的数据

        // 参数df,给query设置默认搜索域
        // query.set("df", "title");
        // 参数sort,设置返回结果的排序规则
        // query.setSort("id",SolrQuery.ORDER.desc);

        // 设置分页参数
        query.setStart(start);

        query.setRows(rows);// 每一页多少值

        /*
         * //参数hl,设置高亮 query.setParam("hl", "true");
         * query.set("hl.highlightMultiTerm","true");//启用多字段高亮
         * //query.setHighlight(true); //设置高亮的字段
         * query.addHighlightField("title,descript");
         * //query.addHighlightField("title");// 高亮字段 query.setParam("hl.q", q);
         * query.setParam("hl.fl", "title");
         * 
         * //设置高亮的样式 query.setHighlightSimplePre("<font color=\"red\">");
         * query.setHighlightSimplePost("</font>");
         * query.setHighlightSnippets(3);//结果分片数，默认为1
         * query.setHighlightFragsize(1000);//每个分片的最大长度，默认为100
         * query.setParam("f.content.hl.fragsize", "200");
         */
        // 获取查询结果
        QueryResponse response = solrServer.query(query);
        // 两种结果获取：得到文档集合或者实体对象

        /*
         * // 查询得到文档的集合 Map<String, Map<String, List<String>>> highlightresult =
         * response.getHighlighting();
         */

        SolrDocumentList solrDocumentList = response.getResults();
        // System.out.println("通过文档集合获取查询的结果");
        System.out.println("搜索结果的总数量：" + solrDocumentList.getNumFound());

        JSONObject json = new JSONObject();

        JSONObject resHeader = new JSONObject();
        resHeader.put("status", 0);
        resHeader.put("QTime", 0);
        JSONObject param = new JSONObject();
        param.put("q", q);

        param.put("indent", "on");
        param.put("wt", "json");
        resHeader.put("param", param);
        resHeader.put("status", 0);
        json.put("responseHeader", resHeader);

        JSONObject res = new JSONObject();
        res.put("numFound", solrDocumentList.getNumFound());
        res.put("start", 0);

        JSONArray docs = new JSONArray();
        // 遍历列表
        for (SolrDocument doc : solrDocumentList) {
            // System.out.println("id:"+doc.get("id")+" name:"+doc.get("name")+"
            // description:"+doc.get("description"));
            /*
             * List<String> hl_title =
             * highlightresult.get(doc.getFieldValue("id")).get("title");
             * doc.setField("title", hl_title.toString());
             */
            JSONObject obj = new JSONObject();
            obj.put("id", doc.get("id"));
            obj.put("title", doc.get("title"));
            obj.put("descript", doc.get("descript"));
            obj.put("descriptImg", doc.get("descriptImg"));
            obj.put("url", doc.get("url"));
            obj.put("qtype", doc.get("qtype"));
            obj.put("updateTime", doc.get("updateTime"));
            obj.put("createTime", doc.get("createTime"));
            obj.put("author", doc.get("author"));
            // obj.put("_version", doc.get("_version"));

            docs.add(obj);

        }
        res.put("docs", docs);

        json.put("response", res);
        return json;
        /*
         * //得到实体对象 List<Person> tmpLists = response.getBeans(Person.class);
         * if(tmpLists!=null && tmpLists.size()>0){
         * System.out.println("通过文档集合获取查询的结果"); for(Person per:tmpLists){
         * System.out.println("id:"+per.getId()+"   name:"+per.getName()
         * +"    description:"+per.getDescription()); } }
         */

    }

    public JSONObject queryQuestion(JSONObject requestParam,String[] tag) throws SolrServerException, IOException {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();
        // 1、参数q处理：设置solr查询参数q
        String q = requestParam.getString("q");

        String sa = requestParam.getString("sa"); // semantic analysis => sa
        if (sa != null && sa.equalsIgnoreCase("true") && !q.equals("")) {
            // 进行语义分析
            SemanticAnalysis sap = new SemanticAnalysis();
            SAConfig conf = new SAConfig();
            conf.httpArg = ("s=" + q);
            String _keywords = sap.getKeywords(conf);
            q = this.process(_keywords);
        }
        if(null == tag){
       	 	query.addFilterQuery("-ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围	
        }else if("personinside".equals(tag[0])){//如果 为personinside 内部员工，查询全部
        }else{
       		query.addFilterQuery("-ext_scope:[\"\" TO *]");// q中含有subproduct，则fq限定范围	
        }
        // 非空处理
        if (q == null || q.equals("")) {
            q = "*:*";
        }

        query.set("q", q);
        // 2、参数qf处理：设置权重
        String qf = requestParam.getString("qf");
        query.set("defType", "edismax");
        if (qf == null || qf.equals("")) {
            qf = "question";
        }
        query.set("qf", qf);

        // 参数Num处理：设置分页参数
        int num = 10;
        String strNum = requestParam.getString("num");
        if (strNum == null || strNum.equals("")) {
            num = 3;
        } else {
            try {
                num = Integer.parseInt(strNum);
            } catch (Exception e) {
                System.out.println("查询参数num为[" + strNum + "]，其值不是一个有效的整数，已默认处理为取前10条");
                num = 3;
            }
        }
        query.setStart(0);
        query.setRows(num);// 每一页多少值

        // 获取查询结果
        QueryResponse qResponse = solrServer.query(query);
        SolrDocumentList solrDocumentList = qResponse.getResults();

        System.out.println("搜索结果的总数量：" + solrDocumentList.getNumFound());

        JSONObject jsonRet = new JSONObject();

        JSONObject resHeader = new JSONObject();
        resHeader.put("status", 0);
        resHeader.put("QTime", 0);
        JSONObject param = new JSONObject();
        param.put("q", q);
        param.put("indent", "on");
        param.put("wt", "json");
        resHeader.put("param", param);
        resHeader.put("status", 0);
        jsonRet.put("responseHeader", resHeader);

        JSONObject response = new JSONObject();
        response.put("numFound", solrDocumentList.getNumFound());
        response.put("start", 0);

        JSONArray docs = new JSONArray();
        for (SolrDocument doc : solrDocumentList) {
            JSONObject obj = new JSONObject();
            obj.put("id", doc.get("id"));
            obj.put("question", doc.get("question"));
            obj.put("answer", doc.get("answer"));
            obj.put("url", doc.get("url"));
            obj.put("qtype", doc.get("qtype"));
            docs.add(obj);
        }
        response.put("docs", docs);

        jsonRet.put("response", response);
        return jsonRet;
    }

    private String process(String src) {
        String ret = "";
        JSONObject json = JSONObject.parseObject(src);
        ((JSONObject) ((JSONArray) json.get("data")).get(0)).get("keyword");

        JSONArray array = (JSONArray) json.get("data");
        for (int i = 0, len = array.size(); i < len; i++) {
            JSONObject item = (JSONObject) array.get(i);
            String aa = item.getString("keyword");
            ret += (aa + " ");
        }

        ret = ret.trim();
        return ret;
    }

    public void indexFilesSolrCell(String fileName, String solrId) throws IOException, SolrServerException {
        HttpSolrClient client = new HttpSolrClient(SOLR_URL);
        // ContentStreamUpdateRequest 是专门用来提交文件的
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
        String contentType = "application/pdf";

        up.addFile(new File(fileName), contentType);
        // literal.xxx 文件以外的字段，xxx将直接映射到schema.xml中的同名字段
        up.setParam("literal.id", solrId);
        // up.setParam("uprefix", "attr_");
        up.setParam("fmap.content", "text");
        up.setParam("fmap.content", "title");

        up.setAction(org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION.COMMIT, true, true);
        client.request(up);

        // client.commit();
        QueryResponse rsp = client.query(new SolrQuery("*:*"));
        System.out.println(rsp);
    }

    public void indexTikaFile(String path, KBIndex kbIndex, Tenant tenant)
            throws IOException, SAXException, TikaException, SQLException {
        String text = "";
        if (path.endsWith(".mp4")) {

        } else {
            if (!path.endsWith(".pdf")) {
                path = path + ".pdf";
            }
            File file = new File(path);
            InputStream stream = new FileInputStream(file);
            ContentHandler handler = new BodyContentHandler(-1);
            AutoDetectParser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            try {
                parser.parse(stream, handler, metadata);
            } finally {
                stream.close();
            }
            text = handler.toString();
        }

        kbIndex.setId(UUID.randomUUID().toString());
        if (kbIndex.getTitle().equals("")) {
            String[] arr = path.split("/");
            String fileName = arr[arr.length - 1];
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            kbIndex.setTitle(title);
        }
        if (kbIndex.getDescript().equals("")) {
            if (text.length() > 100) {
                kbIndex.setDescript(text.substring(0, 100) + "...");
            } else {
                kbIndex.setDescript(text);
            }
        }
        kbIndex.setText(text);

        // 下一步，考虑DB事务，异常时回滚
        DBManager save = new DBManager();
        if (save.addKBIndex(kbIndex, tenant)) {
            try {
                this.addDoc(kbIndex);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String indexHTML(String url) {
        if (url == null || url.equals("")) {
            url = "/Users/gct/work/test.html";
            // url =
            // "http://www.yyuap.com/page/service/book/platform3/index.html#/platform3/articles/iuap-develop/1-/readme.html";
        }
        File file = new File(url);
        Tika tika = new Tika();
        try {
            Reader red = tika.parse(file);
            org.apache.lucene.document.Document document = new Document();
            TextField tf = new TextField("content", (new Tika().parse(file)));
            String xxx = tf.toString();

            document.add(tf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tika.toString();
    }

    private String fileToTxt(File file) {
        org.apache.tika.parser.Parser parser = new AutoDetectParser();
        try {
            InputStream inputStream = new FileInputStream(file);
            DefaultHandler handler = new BodyContentHandler();
            // BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            parseContext.set(Parser.class, parser);
            parser.parse(inputStream, handler, metadata, parseContext);
            for (String string : metadata.names()) {
                System.out.println(string + ":" + metadata.get(string));
            }
            inputStream.close();
            return handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveExcelData2DB(String path, String type, Tenant tenant) throws IOException {
        DBManager saveData2DB = new DBManager();
        try {
            if (type.equalsIgnoreCase("kbindex")) {
                saveData2DB.saveKB(path, KBINDEXTYPE.KBINDEX, tenant);
            } else if (type.equalsIgnoreCase("qa")) {
                saveData2DB.saveKB(path, KBINDEXTYPE.QA, tenant);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public int importQA(InputStream is, Tenant tenant,String fileName) throws Exception {
        // TODO Auto-generated method stub
        DBManager dbMgr = new DBManager();
        int num = 0;
        num = dbMgr.insertQAFromExcel(is, tenant,fileName);

        return num;
    }

    public JSONArray selectSimilarQByQid(Tenant tenant, String id) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();

        query.setQuery("qid:" + id);// 相关查询，比如某条数据某个字段含有周、星、驰三个字 将会查询出来
                                    // ，这个作用适用于联想查询
        QueryResponse response = solrServer.query(query);

        SolrDocumentList solrDocumentList = response.getResults();
        // System.out.println("通过文档集合获取查询的结果");

        JSONArray array = new JSONArray();
        // 遍历列表
        for (SolrDocument doc : solrDocumentList) {
            JSONObject json = new JSONObject();
            json.put("id", doc.get("id"));
            json.put("question", doc.get("question"));
            json.put("qid", doc.get("qid"));
            array.add(json);
        }
        return array;
        /*
         * //得到实体对象 List<Person> tmpLists = response.getBeans(Person.class);
         * if(tmpLists!=null && tmpLists.size()>0){
         * System.out.println("通过文档集合获取查询的结果"); for(Person per:tmpLists){
         * System.out.println("id:"+per.getId()+"   name:"+per.getName()
         * +"    description:"+per.getDescription()); } }
         */

    }

    public JSONObject selectQAById(Tenant tenant, String id) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();

        query.setQuery("id:" + id);// 相关查询，比如某条数据某个字段含有周、星、驰三个字 将会查询出来
                                   // ，这个作用适用于联想查询
        QueryResponse response = solrServer.query(query);

        SolrDocumentList solrDocumentList = response.getResults();
        // System.out.println("通过文档集合获取查询的结果");

        JSONObject json = new JSONObject();

        // 遍历列表
        for (SolrDocument doc : solrDocumentList) {
            json.put("id", doc.get("id"));
            json.put("question", doc.get("question"));
            json.put("answer", doc.get("answer"));
            json.put("url", doc.get("url"));

            json.put("qtype", doc.get("qtype"));

        }
        return json;
        /*
         * //得到实体对象 List<Person> tmpLists = response.getBeans(Person.class);
         * if(tmpLists!=null && tmpLists.size()>0){
         * System.out.println("通过文档集合获取查询的结果"); for(Person per:tmpLists){
         * System.out.println("id:"+per.getId()+"   name:"+per.getName()
         * +"    description:"+per.getDescription()); } }
         */

    }

    public JSONObject queryAllAndByContent(JSONObject requestParam, Tenant tenant) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();
        // 1、设置solr查询参数
        String _q = requestParam.getString("q");

        int rows = 10;
        int start = 0;
        String strNum = requestParam.getString("rows");
        String strStart = requestParam.getString("start");
        if (strNum == null || strNum.equals("")) {
            rows = 10;
        } else {
            try {
                rows = Integer.parseInt(strNum);
            } catch (Exception e) {
                rows = 10;
            }
        }
        if (strStart == null || strStart.equals("")) {
            start = 0;
        } else {
            try {
                start = Integer.parseInt(strStart);
            } catch (Exception e) {
                start = 0;
            }
        }
        String q = _q;
        String sa = requestParam.getString("sa"); // semantic analysis => sa
        if (sa != null && sa.equalsIgnoreCase("true") && !q.equals("")) {
            // 进行语义分析
            SemanticAnalysis sap = new SemanticAnalysis();
            SAConfig conf = new SAConfig();
            conf.httpArg = ("s=" + _q);
            String _keywords = sap.getKeywords(conf);
            q = this.process(_keywords);
        }
        //(question:嘟嘟  AND  qid:"") OR (question:嘟嘟  AND  -qid:*)
        if (q == null) {
            q = "*:* ";
        } else if (q.equals("")) {
        	q = "*:* ";
        }
        q = q + " AND -qid:[\"\" TO *] ";//*  在solr当中  应该代表 有值  -取反
        query.set("q", q);// 相关查询，比如某条数据某个字段含有周、星、驰三个字 将会查询出来 ，这个作用适用于联想查询

        // 2、处理权重
        String qf = "question^1 answer^0.1";
        query.set("defType", "edismax");
        query.set("qf", qf);
        // query.set("op", "AND");

        query.addSort("updateTime", ORDER.desc);

        // 参数fq, 给query增加过滤查询条件
        // query.addFilterQuery("id:[0 TO 9]");//id为0-4

        // 给query增加布尔过滤条件
        // query.addFilterQuery("description:演员"); //description字段中含有“演员”两字的数据

        // 参数df,给query设置默认搜索域
        // query.set("df", "title");
        // 参数sort,设置返回结果的排序规则
        // query.setSort("id",SolrQuery.ORDER.desc);

        // 设置分页参数
        query.setStart(start);
        query.setRows(rows);// 每一页多少值

        /*
         * //参数hl,设置高亮 query.setParam("hl", "true");
         * query.set("hl.highlightMultiTerm","true");//启用多字段高亮
         * //query.setHighlight(true); //设置高亮的字段
         * query.addHighlightField("title,descript");
         * //query.addHighlightField("title");// 高亮字段 query.setParam("hl.q", q);
         * query.setParam("hl.fl", "title");
         * 
         * //设置高亮的样式 query.setHighlightSimplePre("<font color=\"red\">");
         * query.setHighlightSimplePost("</font>");
         * query.setHighlightSnippets(3);//结果分片数，默认为1
         * query.setHighlightFragsize(1000);//每个分片的最大长度，默认为100
         * query.setParam("f.content.hl.fragsize", "200");
         */
        // 获取查询结果
        QueryResponse response = solrServer.query(query);
        // 两种结果获取：得到文档集合或者实体对象

        /*
         * // 查询得到文档的集合 Map<String, Map<String, List<String>>> highlightresult =
         * response.getHighlighting();
         */

        SolrDocumentList solrDocumentList = response.getResults();
        // System.out.println("通过文档集合获取查询的结果");
        System.out.println("搜索结果的总数量：" + solrDocumentList.getNumFound());

        JSONObject json = new JSONObject();

        JSONObject resHeader = new JSONObject();
        resHeader.put("status", 0);
        resHeader.put("QTime", 0);
        JSONObject param = new JSONObject();
        param.put("q", q);

        param.put("indent", "on");
        param.put("wt", "json");
        resHeader.put("param", param);
        resHeader.put("status", 0);
        json.put("responseHeader", resHeader);

        JSONObject res = new JSONObject();
        res.put("numFound", solrDocumentList.getNumFound());
        res.put("start", 0);

        JSONArray docs = new JSONArray();
        // 遍历列表
        for (SolrDocument doc : solrDocumentList) {
            // System.out.println("id:"+doc.get("id")+" name:"+doc.get("name")+"
            // description:"+doc.get("description"));
            /*
             * List<String> hl_title =
             * highlightresult.get(doc.getFieldValue("id")).get("title");
             * doc.setField("title", hl_title.toString());
             */
            JSONObject obj = new JSONObject();
            obj.put("id", doc.get("id"));
            obj.put("question", doc.get("question"));
            obj.put("answer", doc.get("answer"));
            obj.put("updateTime", doc.get("updateTime"));
            obj.put("createTime", doc.get("createTime"));
            obj.put("url", doc.get("url"));
            obj.put("qtype", doc.get("qtype"));


            obj.put("extend0", doc.get("extend0"));
            obj.put("extend1", doc.get("extend1"));
            obj.put("extend2", doc.get("extend2"));
            obj.put("extend3", doc.get("extend3"));
            obj.put("extend4", doc.get("extend4"));
            obj.put("extend5", doc.get("extend5"));
            obj.put("extend6", doc.get("extend6"));
            obj.put("extend7", doc.get("extend7"));
            obj.put("extend8", doc.get("extend8"));
            obj.put("extend9", doc.get("extend9"));
            obj.put("extend10", doc.get("extend10"));
            obj.put("extend11", doc.get("extend11"));
            obj.put("extend12", doc.get("extend12"));
            obj.put("extend13", doc.get("extend13"));
            obj.put("extend14", doc.get("extend14"));
            obj.put("extend15", doc.get("extend15"));
            obj.put("extend16", doc.get("extend16"));
            obj.put("extend17", doc.get("extend17"));
            obj.put("extend18", doc.get("extend18"));
            obj.put("extend19", doc.get("extend19"));


            docs.add(obj);

        }
        res.put("docs", docs);

        json.put("response", res);
        return json;
        /*
         * //得到实体对象 List<Person> tmpLists = response.getBeans(Person.class);
         * if(tmpLists!=null && tmpLists.size()>0){
         * System.out.println("通过文档集合获取查询的结果"); for(Person per:tmpLists){
         * System.out.println("id:"+per.getId()+"   name:"+per.getName()
         * +"    description:"+per.getDescription()); } }
         */

    }
    
    public JSONObject queryAllKbIndexInfo(JSONObject requestParam, Tenant tenant) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();
        // 1、设置solr查询参数
        String _q = requestParam.getString("q");

        int rows = 10;
        int start = 0;
        String strNum = requestParam.getString("rows");
        String strStart = requestParam.getString("start");
        if (strNum == null || strNum.equals("")) {
            rows = 10;
        } else {
            try {
                rows = Integer.parseInt(strNum);
            } catch (Exception e) {
                rows = 10;
            }
        }
        if (strStart == null || strStart.equals("")) {
            start = 0;
        } else {
            try {
                start = Integer.parseInt(strStart);
            } catch (Exception e) {
                start = 0;
            }
        }
        String q = _q;
        String sa = requestParam.getString("sa"); // semantic analysis => sa
        if (sa != null && sa.equalsIgnoreCase("true") && !q.equals("")) {
            // 进行语义分析
            SemanticAnalysis sap = new SemanticAnalysis();
            SAConfig conf = new SAConfig();
            conf.httpArg = ("s=" + _q);
            String _keywords = sap.getKeywords(conf);
            q = this.process(_keywords);
        }
        //(question:嘟嘟  AND  qid:"") OR (question:嘟嘟  AND  -qid:*)
        if (q == null) {
            q = "*:* ";
        } else if (q.equals("")) {
        	q = "*:* ";
        }
        q = q + " AND ktype:kb ";//类型，kb 文档，qa 问答
        query.set("q", q);// 相关查询，比如某条数据某个字段含有周、星、驰三个字 将会查询出来 ，这个作用适用于联想查询

        // 2、处理权重
        String qf = "question^1 answer^0.1";
        query.set("defType", "edismax");
        query.set("qf", qf);

        query.addSort("updateTime", ORDER.desc);


        // 设置分页参数
        query.setStart(start);
        query.setRows(rows);// 每一页多少值

        // 获取查询结果
        QueryResponse response = solrServer.query(query);

        SolrDocumentList solrDocumentList = response.getResults();
        // System.out.println("通过文档集合获取查询的结果");
        System.out.println("搜索结果的总数量：" + solrDocumentList.getNumFound());

        JSONObject json = new JSONObject();

        JSONObject resHeader = new JSONObject();
        resHeader.put("status", 0);
        resHeader.put("QTime", 0);
        JSONObject param = new JSONObject();
        param.put("q", q);

        param.put("indent", "on");
        param.put("wt", "json");
        resHeader.put("param", param);
        resHeader.put("status", 0);
        json.put("responseHeader", resHeader);

        JSONObject res = new JSONObject();
        res.put("numFound", solrDocumentList.getNumFound());
        res.put("start", 0);

        JSONArray docs = new JSONArray();
        // 遍历列表
        for (SolrDocument doc : solrDocumentList) {
            JSONObject obj = new JSONObject();
            obj.put("id", doc.get("id")==null?"":doc.get("id"));
            obj.put("title", doc.get("title")==null?"":doc.get("title"));
            obj.put("question", doc.get("question")==null?"":doc.get("question"));
            obj.put("descript", doc.get("descript")==null?"":doc.get("descript"));
            obj.put("answer", doc.get("answer")==null?"":doc.get("answer"));
            obj.put("updateTime", doc.get("updateTime")==null?"":doc.get("updateTime"));
            obj.put("createTime", doc.get("createTime")==null?"":doc.get("createTime"));
            obj.put("url", doc.get("url")==null?"":doc.get("url"));
            obj.put("ext_scope", doc.get("ext_scope")==null?"":doc.get("ext_scope"));
            obj.put("domain", doc.get("domain")==null?"":doc.get("domain"));
            obj.put("product", doc.get("product")==null?"":doc.get("product"));
            obj.put("subproduct", doc.get("subproduct")==null?"":doc.get("subproduct"));
            obj.put("descriptImg", doc.get("descriptImg")==null?"":doc.get("descriptImg"));
            obj.put("author", doc.get("author")==null?"":doc.get("author"));
            obj.put("keywords", doc.get("keywords")==null?"":doc.get("keywords"));
            obj.put("tag", doc.get("tag")==null?"":doc.get("tag"));
            obj.put("extend0", doc.get("extend0"));
            obj.put("extend1", doc.get("extend1"));
            obj.put("extend2", doc.get("extend2"));
            obj.put("extend3", doc.get("extend3"));
            obj.put("extend4", doc.get("extend4"));
            obj.put("extend5", doc.get("extend5"));
            obj.put("extend6", doc.get("extend6"));
            obj.put("extend7", doc.get("extend7"));
            obj.put("extend8", doc.get("extend8"));
            obj.put("extend9", doc.get("extend9"));
            obj.put("extend10", doc.get("extend10"));
            obj.put("extend11", doc.get("extend11"));
            obj.put("extend12", doc.get("extend12"));
            obj.put("extend13", doc.get("extend13"));
            obj.put("extend14", doc.get("extend14"));
            obj.put("extend15", doc.get("extend15"));
            obj.put("extend16", doc.get("extend16"));
            obj.put("extend17", doc.get("extend17"));
            obj.put("extend18", doc.get("extend18"));
            obj.put("extend19", doc.get("extend19"));

            docs.add(obj);

        }
        res.put("docs", docs);

        json.put("response", res);
        return json;

    }
    

    ////// pengjf 下面测试调用solrj 删除文档测试代码 begin
    /**
     * 根据id从索引库删除文档
     */
    public static void deleteDocumentById() throws Exception {
        // // 选择具体的某一个solr core
        // HttpSolrClient server = new
        // HttpSolrClient("http://127.0.0.1:8080/solr/" + "mycore");
        // // 删除文档
        // server.deleteById("222");
        // // 删除所有的索引
        // // solr.deleteByQuery("*:*");
        // // 提交修改
        // server.commit();
        // server.close();

        try {
            HttpSolrClient client = new HttpSolrClient("http://127.0.0.1:8080/kb/" + "yycloudkbcore");
            client.deleteByQuery("qid:" + "715eea41-b712-4592-af0c-bcb34b6c1d69");
            client.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id从索引库查询文档
     */
    public static void queryDocumentById() throws Exception {
        // 选择具体的某一个solr core
        HttpSolrClient server = new HttpSolrClient("http://127.0.0.1:8080/kb/" + "yycloudkbcore");
        SolrQuery query = new SolrQuery();


        query.set("q", "(*:*  AND  qid:\"\") OR (*:*  AND  -qid:*)");// AND -qid:* 

        query.set("q", "*");// AND -qid:* 
        query.setFilterQueries("ktype:tj");
        query.addStatsFieldFacets("question", "answer");

        //query.setQuery("question:pjf OR answer:嘟嘟");

        QueryResponse response = server.query(query);
        SolrDocumentList solrDocumentList = response.getResults();
    }

    /**
     * 根据id从索引库更新文档
     */
    public static void updateDocumentById() throws Exception {
        // 选择具体的某一个solr core
        HttpSolrClient server = new HttpSolrClient("http://127.0.0.1:8080/kb/" + "yycloudkbcore");
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "715eea41-b712-4592-af0c-bcb34b6c1d69");
        doc.addField("question", "pjf5");
        doc.addField("title", "pjf4");
        doc.addField("answer", "pjf4");
        doc.addField("descript", "pjf4");
        doc.addField("_version_", 1);
        server.add(doc);
        server.commit();
    }

    public static void main(String[] args) {
        try {
            queryDocumentById();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    ////// pengjf 下面测试调用solrj 删除文档测试代码 end

}
