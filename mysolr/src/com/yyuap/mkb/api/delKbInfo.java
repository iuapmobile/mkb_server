package com.yyuap.mkb.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.ResultObject;
import com.yyuap.mkb.services.ResultObjectFactory;

/**
 * 删除文档（kbindexinfo）
 */
@WebServlet("/delKbInfo")
public class delKbInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public delKbInfo() {
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
        
        String[] ids = request.getParameterValues("ids");//删除  id  数组
   	 	if (ids != null && ids.length!=0) {

        } else {
        	 ResultObjectFactory rof = new ResultObjectFactory();
             ResultObject ro = rof.create(-1);
             ro.setReason("ids不能为空");
             response.getWriter().write(ro.toString());
             return;
        }
   	 	String apiKey = request.getParameter("apiKey");
   	 	if(apiKey != null && !apiKey.equals("")){
	 		
	 	}else{
	   	 	ResultObjectFactory rof = new ResultObjectFactory();
	        ResultObject ro = rof.create(-3);
	        ro.setReason("apiKey不能为空");
	        response.getWriter().write(ro.toString());
	        return;
	 	}

        

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = null;

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
        	ro = rof.create(-3);
            ro.setReason(e1.getMessage());
            response.getWriter().write(ro.toString());
            return;
        }

        try {

            QAManager qam = new QAManager();
            boolean flag = qam.delKbInfoBat(ids, tenant);
            if(flag){
            	ro = rof.create(0);
            	ro.setReason("删除成功");
            	ro.setResponseKV("ids", ids);
            }else{
            	ro = rof.create(-4);
            	ro.setReason("删除失败");
            	ro.setResponseKV("ids", ids);
            }
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ex = (KBDuplicateSQLException) e;
                ro = rof.create(-5);
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
                e.printStackTrace();
            }
        }
        response.getWriter().write(ro.toString());

    }

}
