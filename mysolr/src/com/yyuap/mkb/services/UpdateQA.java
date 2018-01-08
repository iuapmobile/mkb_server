package com.yyuap.mkb.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class UpdateQA
 */
@WebServlet("/UpdateQA")
public class UpdateQA extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateQA() {
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
        String q = requestParam.getString("q");
        String a = requestParam.getString("a");
        String istop = requestParam.getString("istop");// 是否置顶
        String[] qs = (String[]) requestParam.get("qs");

        String ktype = requestParam.getString("ktype");
        if (null == ktype || "".equals(ktype)) {
            ktype = "qa";
        }

        String ext_scope = requestParam.getString("tag");// 可见范围
        String domain = requestParam.getString("domain");// 领域
        String product = requestParam.getString("product");// 产品
        String subproduct = requestParam.getString("subproduct");// 子产品
        if (null != ext_scope && !"personinside".equals(ext_scope)) {
            ext_scope = null;
        }
        if (id == null || id.equals("")) {
            response.getWriter().append("Served at: ").append(request.getContextPath());
            return;
        }

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
            MKBLogger.error("Exception:" + e1.toString());
        }
        if (tenant == null) {
            return;
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        // 2、根据租户调用QAManager
        QAManager qam = new QAManager();
        try {
            JSONArray array = qam.queryFieldForTableTenant("qa", tenant);
            JSONObject json = new JSONObject();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    json.put(obj.getString("field_name"), request.getParameter(obj.getString("field_name")));
                }
            }
            boolean success = qam.updateQA(id, q, a, qs, tenant, istop, ext_scope, domain, product, subproduct, json);
            if (success) {
                ro.setStatus(0);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
            ro.setStatus(1169);
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
            MKBLogger.error("UpdateQA.java readJSON4JSON() Exception: " + e.toString());
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
                MKBLogger.error("Exception:" + e1.toString());
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

        String q = request.getParameter("q");
        param.put("q", q);

        String a = request.getParameter("a");
        param.put("a", a);

        String[] qs = request.getParameterValues("qs");
        param.put("qs", qs);

        String kbid = request.getParameter("kbid");
        param.put("kbid", kbid);

        String ktype = request.getParameter("ktype");
        param.put("ktype", ktype);

        String qtype = request.getParameter("qtype");
        param.put("qtype", qtype);

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);

        String tag = request.getParameter("tag");
        param.put("tag", tag);

        String domain = request.getParameter("domain");// 领域
        param.put("domain", domain);
        String product = request.getParameter("product");// 产品
        param.put("product", product);
        String subproduct = request.getParameter("subproduct");// 子产品
        param.put("subproduct", subproduct);

        return param;
    }
}
