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
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class AddStore
 */
@WebServlet("/SetIsTop")
public class SetIsTop extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetIsTop() {
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

        // 这句话的意�?�，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意�?�，是告诉servlet用UTF-8转码，�?�不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");

        String qaid = request.getParameter("id");
        String istop = request.getParameter("istop");

        if ((qaid == null || "".equals(qaid)) || (istop == null || "".equals(istop))) {
        	 ResultObjectFactory rof = new ResultObjectFactory();
             ResultObject ro = rof.create(0);
             ro.setReason("qa的ID为空或者是否置顶istop为空，请检查. ");
             ro.setStatus(1000);
             response.getWriter().write(ro.toString());
             return;
        } else {
           
        }

       

        String apiKey = request.getParameter("apiKey");

        // 1、获取租户信�?
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e1.toString());
        }
        if (tenant == null) {
            return;
        }

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        try {
        	boolean flag = qam.setIsTop(qaid,istop, tenant);
        } catch (SQLException e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ee = (KBDuplicateSQLException) e;

                ro.setResponseKV("id", ee.getId());
                ro.setReason(ee.getMessage());
                ro.setStatus(ee.getKBExceptionCode());
            } else {
                ro.setStatus(1000);
                MKBLogger.error("Exception:" + e.toString());
            }
        }
        response.getWriter().write(ro.toString());

    }

}
