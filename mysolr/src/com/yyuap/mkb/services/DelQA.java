package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class deleteDoc
 */
@WebServlet("/delQA")
public class DelQA extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DelQA() {
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

        String id = request.getParameter("id");
        if (id == null || id.equals("")) {
            return;
        }

        String apiKey = request.getParameter("apiKey");
        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            return;
        }
        if (tenant == null) {
            return;
        }

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        boolean success = false;
        try {
            success = qam.delQA(id, tenant);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }
        if (success) {
            ro.setResponseKV("id", id);
        } else {
            ro.setStatus(1175);
        }
        response.getWriter().write(ro.toString());

    }

}
