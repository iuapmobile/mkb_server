package com.yyuap.mkb.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.solr.client.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
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
import com.yyuap.mkb.nlp.SAConfig;
import com.yyuap.mkb.nlp.SemanticAnalysis;
import com.yyuap.mkb.pl.DBConfig;
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

    public SolrManager() {

    }

    public SolrManager(String kbcore) {
        SOLR_URL = "http://127.0.0.1:8080/kb/" + kbcore;
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

    public void addDoc(KBIndex kbindex) throws Exception {
        String id = kbindex.getId();
        String title = kbindex.getTitle();
        String descript = kbindex.getDescript();
        String descriptImg = kbindex.getDescriptImg();
        String keywords = kbindex.getKeywords();
        String url = kbindex.getUrl();
        String text = kbindex.getText();

        // 1.创建链接
        @SuppressWarnings("deprecation")
        SolrClient solr = this.getHttpSolrClient();

        // 2.创建一文档对象
        SolrInputDocument document = new SolrInputDocument();

        // 3.向文档对象中添加域 （先定义后使用）
        document.addField("id", id);
        document.addField("title", title);
        document.addField("descript", descript);
        document.addField("descriptImg", descriptImg);
        document.addField("keywords", keywords);
        document.addField("url", url);
        document.addField("text", text);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long times = System.currentTimeMillis();
        System.out.println(times);
        Date date = new Date(times);
        String tim = sdf.format(date);
        System.out.println(tim);

        document.addField("createTime", tim);
        document.addField("updateTime", tim);

        // 4.提交文档到索引库
        solr.add(document);
        // 5.提交
        solr.commit();
    }

    public JSONObject query(JSONObject requestParam) throws Exception {

        HttpSolrClient solrServer = this.getHttpSolrClient();
        SolrQuery query = new SolrQuery();
        // 1、设置solr查询参数
        String _q = requestParam.getString("q");

        int num = 10;
        String strNum = requestParam.getString("num");
        if (strNum == null || strNum.equals("")) {
            num = 10;
        } else {
            try {
                num = Integer.parseInt(strNum);
            } catch (Exception e) {
                num = 10;
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

        if (q == null) {
            q = "*:*";
        } else if (q.equals("")) {
            q = "*:*";
        }
        query.set("q", q);// 相关查询，比如某条数据某个字段含有周、星、驰三个字 将会查询出来 ，这个作用适用于联想查询

        // 2、处理权重
        String qf = requestParam.getString("qf");
        query.set("defType", "edismax");
        if (qf == null || qf.equals("")) {
            qf = "keywords^1000 question^200 answer^100 title^50 descript^30 text^10";
        }
        query.set("qf", qf);

        // 参数fq, 给query增加过滤查询条件
        // query.addFilterQuery("id:[0 TO 9]");//id为0-4

        // 给query增加布尔过滤条件
        // query.addFilterQuery("description:演员"); //description字段中含有“演员”两字的数据

        // 参数df,给query设置默认搜索域
        // query.set("df", "title");
        // 参数sort,设置返回结果的排序规则
        // query.setSort("id",SolrQuery.ORDER.desc);

        // 设置分页参数
        query.setStart(0);
        query.setRows(num);// 每一页多少值

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
        System.out.println("通过文档集合获取查询的结果");
        System.out.println("查询结果的总数量：" + solrDocumentList.getNumFound());

        JSONObject json = new JSONObject();

        JSONObject resHeader = new JSONObject();
        resHeader.put("status", 0);
        resHeader.put("QTime", 0);
        JSONObject param = new JSONObject();
        param.put("q", q);
        param.put("qid", requestParam.get("qid"));
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

    public void indexTikaFile(String path, KBIndex kbIndex)
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
        if (save.addKBIndex(kbIndex)) {
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

    public boolean saveExcelData2DB(String path, String type) throws IOException {
        DBManager saveData2DB = new DBManager();
        try {
            if (type.equalsIgnoreCase("kbindex")) {
                saveData2DB.saveKB(path, KBINDEXTYPE.KBINDEX);
            } else if (type.equalsIgnoreCase("qa")) {
                saveData2DB.saveKB(path, KBINDEXTYPE.QA);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public boolean importQA(String path, Tenant tenant) {
        // TODO Auto-generated method stub
        DBManager dbMgr = new DBManager();

        try {
            dbMgr.insertQAFromExcel(path, tenant);
        } catch (IOException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

}
