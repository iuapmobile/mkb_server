package com.yyuap.mkb.services;

import java.io.BufferedReader;
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
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;

/**
 * Servlet implementation class UpdateQA
 */
@WebServlet("/UpdateQA")
public class UpdateQAQS extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateQAQS() {
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

        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();

        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {
            requestParam = this.readJSON4JSON(request);
        } else {
            requestParam = this.readJSON4Form_urlencoded(request);
        }
        String id = requestParam.getString("id");
        String q = requestParam.getString("question");
        String a = requestParam.getString("answer");
        String strqs = requestParam.getString("qs");
        JSONArray qs = JSONArray.parseArray(strqs);
        String apiKey = requestParam.getString("apiKey");
        String url = requestParam.getString("url");
        String qtype = requestParam.getString("qtype");

        if (apiKey == null || "".equals(apiKey)) {
            ResultObjectFactory rof = new ResultObjectFactory();
            ResultObject ro = rof.create(0);
            ro.setReason("apiKey为空 ");
            ro.setStatus(-1);
            response.getWriter().write(ro.toString());
            return;

        }
        
        if (id == null || id.equals("")) {
            response.getWriter().append("Served at: ").append(request.getContextPath());
            return;
        }

        if(null == url || "".equals(url)){
       	 if (q != null && !q.equals("") && a != null && !a.equals("")) {

            } else {
                response.getWriter().append("Served at: ").append(request.getContextPath());
                return;
            }
       }else{
       	 if (q != null && !q.equals("") && url != null && !url.equals("")) {

            } else {
                response.getWriter().append("Served at: ").append(request.getContextPath());
                return;
            }
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
        if (tenant == null) {
            return;
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();
        try {
            String editId = qam.updateQAQS(id, q, a, qs,url,qtype, tenant);
            if (editId != null && editId.equals("")) {
                ro.setStatus(0);
                ro.setResponseKV("id", editId);
            } else {
                ro.setStatus(1175);
                ro.setReason("更新失败!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ro.setStatus(1169);
            ro.setReason(e.toString());
        }

        response.getWriter().write(ro.toString());
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
            str = java.net.URLDecoder.decode(str, "UTF-8");
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

        String id = request.getParameter("id");
        param.put("id", id);

        String q = request.getParameter("question");
        param.put("question", q);

        String a = request.getParameter("answer");
        param.put("answer", a);

        String qs = request.getParameter("qs");
        param.put("qs", qs);

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);
        
        String url = request.getParameter("url");
        param.put("url", url);
        
        String qtype = request.getParameter("qtype");
        param.put("qtype", qtype);

        return param;
    }
}
