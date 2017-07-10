package com.yyuap.mkb.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;
import com.yyuap.mkb.turbot.MKBHttpClientUtil;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/mkbQuery")
public class Query extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BOTURL = "http://www.tuling123.com/openapi/api";
    private static final String BOTAPIKEY = "f08c391260304acc81f8fdb27de44832";// 雪儿
    // private static final String BOTAPIKEY=
    // "301c3b5d56934e7b9248f7142fbad15d";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Query() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        // response.setContentType("application/json");
        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();

        MKBRequestProcessor rp = new MKBRequestProcessor();
        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {
            requestParam = rp.readJSON4JSON(request);
        } else {
            requestParam = rp.readJSON4Form_urlencoded(request);
        }

        String q = requestParam.getString("q");
        String bot = requestParam.getString("bot");
        String apiKey = requestParam.getString("apiKey");
        String buserid = requestParam.getString("buserid");

         String a="";
        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JSONObject ret = new JSONObject();
        JSONObject res = new JSONObject();
        ret.put("response", res);
        if (tenant != null) {
            QAManager qamgr = new QAManager();

            // 2、查询唯一答案
            JSONObject uniqueQA = qamgr.getUniqueAnswer(q, tenant);
            if (uniqueQA != null && !uniqueQA.getString(q).equals("")) {
                JSONObject botRes = new JSONObject();
                botRes.put("code", "100000");
                a = uniqueQA.getString(q);
                botRes.put("text", a);
                res.put("botResponse", botRes);
            } else {
                // 3、启用搜索引擎
                SolrManager solrmng = new SolrManager(tenant.gettkbcore());

                try {

                    ret = solrmng.query(requestParam);// 获取查询结果

                    // 4、机器人应答
                    JSONObject resObjj = ((JSONObject) ret.get("response"));
                    // if(resObj.getInteger("numFound") == 0){
                    if (bot == null || !bot.equalsIgnoreCase("false")) {
                        // 如果是机器人请求

                        MKBHttpClientUtil httpclient = new MKBHttpClientUtil();

                        Map<String, String> createMap = new HashMap<String, String>();
                        createMap.put("key", tenant.getbotKey());
                        createMap.put("info", q);
                        if (buserid != null && !buserid.equals("")) {
                            createMap.put("userid", buserid);
                        }
                        String charset = "utf-8";

                        String botRes = httpclient.doPost(BOTURL, createMap, charset);
                        JSONObject obj = JSONObject.parseObject(botRes);
                        a = obj.getString("text");
                        resObjj.put("botResponse", obj);

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            JSONObject resHeader = new JSONObject();
            resHeader.put("status", 0);
            resHeader.put("QTime", 0);
            JSONObject param = new JSONObject();
            param.put("q", q);
            param.put("indent", "on");
            param.put("wt", "json");
            resHeader.put("param", param);
            resHeader.put("status", 0);
            ret.put("responseHeader", resHeader);

            res.put("numFound", 0);
            res.put("start", 0);
        }

        // 5、添加q的统计
        QAManager qamgr = new QAManager();
        String q_tj_id = qamgr.addTongji(q, a, tenant);
        res = ret.getJSONObject("responseHeader");
        JSONObject param = res.getJSONObject("param");
        param.put("qid", q_tj_id);

        String result = ret.toString();

        PrintWriter out = response.getWriter();
        // out.write(result);
        out.print(result);
        out.flush();
        out.close();
    }

    /**、
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
