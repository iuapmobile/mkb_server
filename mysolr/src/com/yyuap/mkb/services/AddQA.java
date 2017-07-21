package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class addQA
 */
@WebServlet("/AddQA")
public class AddQA extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddQA() {
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

        String q = request.getParameter("q");
        String a = request.getParameter("a");
        String istop = request.getParameter("istop");//是否置顶
        if(null == istop || "".equals(istop)){
        	istop = "0";
        }
        String libraryPk = request.getParameter("libraryPk");
        String[] qs = request.getParameterValues("qs");

        if (q != null && !q.equals("") && a != null && !a.equals("")) {

        } else {
            response.getWriter().append("Served at: ").append(request.getContextPath());
            return;
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
        if (tenant == null) {
            return;
        }

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();
        String id;
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        try {
            id = qam.addQA(libraryPk, q, a, qs, tenant,istop);
            ro.getResponse().put("id", id);
        } catch (SQLException e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ee = (KBDuplicateSQLException) e;

                ro.getResponse().put("id", ee.getId());
                ro.getResponse().put("reason", ee.getMessage());
                ro.setStatus(ee.getKBExceptionCode());
            } else {
                ro.setStatus(1000);
                e.printStackTrace();
            }
        }
        response.getWriter().write(ro.toString());

    }

}
