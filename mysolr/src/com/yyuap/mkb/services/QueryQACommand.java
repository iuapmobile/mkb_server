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
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.QAManager;


/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/QueryQACommand")
public class QueryQACommand extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryQACommand() {
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

        String userid = request.getParameter("userid");
        String apiKey = request.getParameter("apiKey");

        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e1.toString());
        }
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        
        JSONArray jsonArray = new JSONArray();
        if (tenant != null) {
            // 2、查询答案
            QAManager qamgr = new QAManager();
            JSONObject ret = qamgr.queryQACommandByTenant(tenant);
            
            ro.setDocs(ret.getJSONArray("docs"));
        }else {
            String _rea = "找不到aipKey[" + apiKey + "]对应的租户";
            ro.setStatus(110110);
            ro.setReason(_rea);
        }
        //response.getWriter().write(ret.toString());
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
