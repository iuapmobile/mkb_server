package com.yyuap.mkb.api;

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
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.cbo.TenantInfo;
import com.yyuap.mkb.services.ResultObject;
import com.yyuap.mkb.services.ResultObjectFactory;

/**
 * Servlet implementation class AddStore
 */
@WebServlet("/GetIsRecommended")
public class GetBotIsRecommended extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetBotIsRecommended() {
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

        // 让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 设置servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");

        String apiKey = request.getParameter("apiKey");

        // 1、获取租户信息
        if (null == apiKey || "".equals(apiKey)) {
            ResultObjectFactory rof = new ResultObjectFactory();
            ResultObject ro = rof.create(-1);
            ro.setReason("没有必要的参数apiKey！");
            response.getWriter().write(ro.toString());
            return;
        }
        CBOManager api = new CBOManager();
        TenantInfo tenant = null;
        try {
            tenant = api.getTenant(apiKey);
        } catch (SQLException e1) {

            ResultObjectFactory rof = new ResultObjectFactory();
            ResultObject ro = rof.create(-1);
            ro.setReason("未能正确获取apiKey[" + apiKey + "]的租户信息！");
            response.getWriter().write(ro.toString());
            e1.printStackTrace();
            return;
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        JSONObject obj = new JSONObject();

        obj.put("isRecommended", tenant.getRecommended());
        obj.put("id", tenant.getId());
        obj.put("apiKey", tenant.getId());

        ro.setResponseData(obj);
        response.getWriter().write(ro.toString());

    }

}
