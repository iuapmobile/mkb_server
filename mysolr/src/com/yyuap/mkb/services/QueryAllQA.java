package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/QueryAllQA")
public class QueryAllQA extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BOTURL = "http://www.tuling123.com/openapi/api";
    private static final String BOTAPIKEY = "f08c391260304acc81f8fdb27de44832";// 雪儿
    // private static final String BOTAPIKEY=
    // "301c3b5d56934e7b9248f7142fbad15d";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryAllQA() {
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

        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        
        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();

        MKBRequestProcessor rp = new MKBRequestProcessor();
        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {
            requestParam = rp.readJSON4JSON(request);
        } else {
            requestParam = rp.readJSON4Form_urlencoded(request);
        }

        String content = request.getParameter("content");
        String bot = request.getParameter("bot");
        if (bot == null) {
            bot = "true";
        }
        String apiKey = request.getParameter("apiKey");

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
        
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        if (tenant != null) {
        	// 1、是否推荐，启用搜索引擎进行推荐
            boolean recommended = tenant.getRecommended();
            // 2、查询答案
//            QAManager qamgr = new QAManager();
//            ret = qamgr.query(tenant,content);
            if (recommended) {
                try {
                    String corename = tenant.gettkbcore();
                    SolrManager solrmng = new SolrManager(corename);
                    JSONObject _ret = solrmng.queryAllAndByContent(requestParam, tenant);// 获取查询结果,，一个新的对象
                   
                    JSONObject respnse = _ret.getJSONObject("response");
                    QAManager qam = new QAManager();
                    JSONArray array = qam.queryFieldForTable("qa", tenant);
                    respnse.put("extend", array);
                    
                    ro.set(_ret);// 导致botResponse需要重新赋值
                    
                } catch (Exception e) {
                }
            }
        }
        response.getWriter().write(ro.toString());
    }

    /**
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
