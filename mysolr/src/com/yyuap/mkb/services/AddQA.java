package com.yyuap.mkb.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
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
        String url = request.getParameter("url");
        String qtype = request.getParameter("qtype");
        String kbid = request.getParameter("kbid");// 该问答属于那一个知识库，一个用户可以有多个知识库
        if (null == kbid || "".equals(kbid)) {
            kbid = "1";
        }
        String istop = request.getParameter("istop");// 是否置顶
        if (null == istop || "".equals(istop)) {
            istop = "0";
        }
        String ext_scope = request.getParameter("ext_scope");//tag: "personinside" //内部为personinside 其余为空
        if(null!=ext_scope && !"personinside".equals(ext_scope)){
        	ext_scope = null;
        }
        String domain = request.getParameter("domain");//领域
        String product = request.getParameter("product");//产品
        String subproduct = request.getParameter("subproduct");//子产品
        String libraryPk = request.getParameter("libraryPk");
        String ktype = request.getParameter("ktype");//表示该问答是那种类型，command、doc、qa、kb
        if (null == ktype || "".equals(ktype)) {
            ktype = "qa";
        }
        String[] qs = request.getParameterValues("qs");
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
       

        String apiKey = request.getParameter("apiKey");

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
            if (tenant == null) {
                String _rea = "找不到aipKey[" + apiKey + "]对应的租户";
                throw new KBSQLException(_rea);
            }
        } catch (Exception e1) {
            ro.setStatus(1000);
            ro.setReason(e1.getMessage());
            response.getWriter().write(ro.toString());
            return;
        }
       
        try {
        	QAManager qam = new QAManager();
            // 2、插入数据库
            JSONObject json = new JSONObject();
            json.put("q", q);
            json.put("a", a);
            json.put("qs", qs);
            json.put("url", url);
            json.put("qtype", qtype);
            json.put("kbid", kbid);
            json.put("istop", istop);
            json.put("libraryPk", libraryPk);
            json.put("ktype", ktype);
            json.put("ext_scope", ext_scope);
            json.put("domain", domain);
            json.put("product", product);
            json.put("subproduct", subproduct);
            JSONArray array = qam.queryFieldForTableTenant("qa",tenant);
            if(array!=null&&array.size()>0){
            	for(int i=0;i<array.size();i++){
            		JSONObject obj = array.getJSONObject(i);
            		json.put(obj.getString("field_name"), request.getParameter(obj.getString("field_name")));
            	}
            }
            String id = qam.addQA(json, tenant);
            ro.setResponseKV("id", id);
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ex = (KBDuplicateSQLException) e;

                ro.setResponseKV("id", ex.getId());

                ro.setReason(ex.getMessage());
                ro.setStatus(ex.getKBExceptionCode());
            } else if (e instanceof KBInsertSQLException) {
                KBInsertSQLException ex = (KBInsertSQLException) e;

                ro.setReason(ex.getMessage());
                ro.setStatus(ex.getKBExceptionCode());
            } else {
                ro.setStatus(1000);
                ro.setReason(e.toString());
                MKBLogger.error("Exception:" + e.toString());
            }
        }
        response.getWriter().write(ro.toString());

    }


    
}
