package com.yyuap.mkb.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.services.ResultObject;
import com.yyuap.mkb.services.ResultObjectFactory;

/**
 * 全局维度统计QA请求次数排名
 */
@WebServlet("/QueryQaTopTj")
public class QueryQaTopTj extends HttpServlet {

	 /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryQaTopTj() {
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

        String top = request.getParameter("topn");
        String istop = request.getParameter("istop");//死否置顶
        int topn = 5;
        try {
            topn = Integer.parseInt(top);
        } catch (Exception e) {
            // 记录错误
        }
        String field = request.getParameter("field");//字段 用哪个字段去做维度统计
        String fieldValue = request.getParameter("fieldValue");//字段value

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
        	if(null == field || "all".equals(fieldValue)){
            	Map<String,String> map = qam.queryQaTopTj(topn,istop, tenant);
            	String[] arr = new String[map.size()];
            	String[] dataArr = new String[map.size()];
                if(map.size()>0){
        			int i = 0 ;
        			for (Map.Entry<String, String> entry : map.entrySet()) {
        				arr[i] = entry.getKey();
        				dataArr[i] = entry.getValue();
        				i++;
    		        }
                }
                String arrStr = JSON.toJSONString(arr);
                ro.setResponseKV("xAxis", JSONArray.toJSON(arrStr));
                String dataArrStr = JSON.toJSONString(dataArr);
                ro.setResponseKV("key", JSONArray.toJSON(dataArrStr));
        	}else{
        		Map<String,String> map = qam.queryQaTopTjForDimension(topn,istop,field,fieldValue, tenant);
            	String[] arr = new String[map.size()];
            	String[] dataArr = new String[map.size()];
                if(map!=null && map.size()>0){
        			int i = 0 ;
        			for (Map.Entry<String, String> entry : map.entrySet()) {
        				arr[i] = entry.getKey();
        				dataArr[i] = entry.getValue();
        				i++;
    		        }
                }
                String arrStr = JSON.toJSONString(arr);
                ro.setResponseKV("xAxis", JSONArray.toJSON(arrStr));
                String dataArrStr = JSON.toJSONString(dataArr);
                ro.setResponseKV("key", JSONArray.toJSON(dataArrStr));
        	}
        	
            
           
        } catch (Exception e) {
            if (e instanceof KBDuplicateSQLException) {

                KBDuplicateSQLException ex = (KBDuplicateSQLException) e;

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
