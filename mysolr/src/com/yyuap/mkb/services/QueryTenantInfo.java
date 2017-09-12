package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.cbo.TenantInfo;

/**
 * Servlet implementation class AddStore
 */
@WebServlet("/QueryTenantInfo")
public class QueryTenantInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryTenantInfo() {
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


        String apiKey = request.getParameter("apiKey");
        // 1、获取租户信�?
        if(null == apiKey || "".equals(apiKey)){
        	 ResultObjectFactory rof = new ResultObjectFactory();
             ResultObject ro = rof.create(-1);
             ro.setReason("apiKey不能为空！");
             response.getWriter().write(ro.toString());
             return;
        }
    	CBOManager api = new CBOManager();
    	TenantInfo tenant = null;
        try {
        	tenant = api.getTenant(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        
        
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        ro.setResponseKV("isRecommended", tenant.getRecommended());
        ro.setResponseKV("id", tenant.getId());
        response.getWriter().write(ro.toString());

    }

}
