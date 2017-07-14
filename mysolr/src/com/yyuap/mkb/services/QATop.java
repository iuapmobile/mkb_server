package com.yyuap.mkb.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class QATop
 */
@WebServlet("/QATop")
public class QATop extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public QATop() {
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
        // TODO Auto-generated method stub

        request.setCharacterEncoding("UTF-8");
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        // response.setContentType("application/json");
        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();
        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {

            requestParam = this.readJSON4JSON(request);

        } else {
            requestParam = this.readJSON4Form_urlencoded(request);
        }

        String apiKey = request.getParameter("apiKey");
        apiKey = requestParam.getString("apiKey");

        String top = request.getParameter("top");
        top = requestParam.getString("top");
        int topn = 5;
        try {
            topn = Integer.parseInt(top);
        } catch (Exception e) {
            // 记录错误
        }
        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        QAManager qamgr = new QAManager();
        JSONArray array = qamgr.topN(topn, tenant);

        ResultObject ro = (new ResultObjectFactory()).create(0);
        JSONObject res = ro.getResponse();
        res.put("docs", array);
        String result = ro.toString();
        PrintWriter out = response.getWriter();
        // out.write(result);
        out.print(result);
        out.flush();
        out.close();
    }

    private JSONObject readJSON4JSON(HttpServletRequest request) {
        JSONObject param = new JSONObject();
        StringBuffer json = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        String str = json.toString();
        try {
            JSONObject obj = JSONObject.parseObject(str);
            return obj;
        } catch (Exception e) {
            try {
                str = java.net.URLDecoder.decode(str, "UTF-8");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String[] strs = str.split("&");
            for (String s : strs) {
                String[] kv = s.split("=");
                param.put(kv[0], kv[1]);
            }
            return param;
        }
    }

    private JSONObject readJSON4Form_urlencoded(HttpServletRequest request) {
        JSONObject param = new JSONObject();

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);

        String top = request.getParameter("top");
        param.put("top", top);

        return param;
    }

}
