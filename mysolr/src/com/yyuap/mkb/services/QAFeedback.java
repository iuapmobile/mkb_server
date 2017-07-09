package com.yyuap.mkb.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
 * Servlet implementation class Feedback
 */
@WebServlet("/QAFeedback")
public class QAFeedback extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QAFeedback() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        
        
        // response.setContentType("application/json");
//        String content_type = request.getContentType();
//        JSONObject requestParam = new JSONObject();
        // MKBRequestProcessor rp = new MKBRequestProcessor();
        // if (content_type != null &&
        // content_type.toLowerCase().indexOf("application/json") >= 0) {
        // requestParam = rp.readJSON4JSON(request);
        // } else {
        // requestParam = rp.readJSON4Form_urlencoded(request);
        // }
        String id = request.getParameter("id");
        String score = request.getParameter("score");
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

        if (tenant == null) {
            return;
        }

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();
        String successId = qam.addFeedback(id, score, tenant);

        ResultObjectFactory rof = new ResultObjectFactory();
        int status = successId.equalsIgnoreCase(id) ? 0 : 1;
        ResultObject ro = rof.create(status);
        response.getWriter().write(ro.toString());
    }

}
