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
import com.yyuap.mkb.cbo.TenantInfo;
import com.yyuap.mkb.log.MKBLogger;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // 登陆获取租户信�?
        TenantInfo tenantinfo = null;
        CBOManager api = new CBOManager();
        try {
        	tenantinfo = api.getTenantInfoForLogin(username,password);
        } catch (SQLException e1) {
            MKBLogger.error("Exception:" + e1.toString());
        }
        if (tenantinfo == null) {
        	JSONObject ret = new JSONObject();
        	ret.put("status", "500");
        	ret.put("reason", "登陆失败，没有用户信息");
        	response.getWriter().write(ret.toString());
            return;
        }
        JSONObject ret = new JSONObject();
        ret.put("status", "0");
        ret.put("tenantinfo", tenantinfo);
        response.getWriter().write(ret.toString());
    }

}
