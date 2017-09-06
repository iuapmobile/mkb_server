package com.yyuap.mkb.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/QueryBotServicesTj")
public class QueryBotServicesTj extends HttpServlet {

	 /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryBotServicesTj() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");

        String apiKey = request.getParameter("apiKey");

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
            if (tenant == null) {
                String _rea = "找不到aipKey[" + apiKey + "]对应的租户";
                throw new KBSQLException(_rea);
            }
        } catch (Exception e1) {
            ro.setStatus(1000);
            ro.setReason(e1.getMessage());
            response.getWriter().write(ro.toString());
            return;
        }

        try {
            QAManager qam = new QAManager();
            String count = qam.queryBotServicesTj(tenant);
            ro.setResponseKV("count", count);//
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ex = (KBDuplicateSQLException) e;

                ro.setReason(ex.getMessage());
                ro.setStatus(ex.getKBExceptionCode());
            } else if (e instanceof KBInsertSQLException) {
                KBInsertSQLException ex = (KBInsertSQLException) e;

                ro.setReason(ex.getMessage());
                ro.setStatus(ex.getKBExceptionCode());
            } else {
                ro.setStatus(1000);
                ro.setReason(e.toString());
                e.printStackTrace();
            }
        }
        response.getWriter().write(ro.toString());

    }
    
}
