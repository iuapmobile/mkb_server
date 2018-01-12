package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;

/**
 * Servlet implementation class deleteDoc
 */
@WebServlet("/DelQABat")
public class DelQABat extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DelQABat() {
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

        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();

        MKBRequestProcessor rp = new MKBRequestProcessor();
        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {
            requestParam = rp.readJSON4JSON(request);
        } else {
            requestParam = this.readJSON4Form_urlencoded(request);
        }

        //Object _ids = requestParam.get("ids");
        String[] _ids = request.getParameterValues("ids");
        if (_ids == null) {
            return;
        }
        String[] ids = (String[]) _ids;

        // 1、获取租户信息
        String apiKey = requestParam.getString("apiKey");
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
            success = qam.delQABat(ids, tenant);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }
        if (success) {
            ro.setStatus(0);
            ro.setResponseKV("ids", ids);
        } else {
            ro.setStatus(1175);
        }
        response.getWriter().write(ro.toString());

    }

    public JSONObject readJSON4Form_urlencoded(HttpServletRequest request) {
        JSONObject param = new JSONObject();

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);

        String[] ids = request.getParameterValues("ids");
        param.put("ids", ids);
        return param;
    }
}
