package com.yyuap.mkb.services;

import java.io.IOException;
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
import com.yyuap.mkb.turbot.MKBHttpClientUtil;

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
        if (tenant != null) {

            // 2、查询答案
            QAManager qamgr = new QAManager();
            ret = qamgr.query(tenant,content);
        }
        response.getWriter().write(ret.toString());
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
