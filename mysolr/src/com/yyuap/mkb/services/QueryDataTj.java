package com.yyuap.mkb.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.QAManager;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/QueryDataTj")
public class QueryDataTj extends HttpServlet {

	 /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryDataTj() {
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

        String day = request.getParameter("day");
       

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
        	String[] arr = null;
            QAManager qam = new QAManager();
            Map<String,String> map = qam.queryDataTj(day, tenant);
            if("0".equals(day)){//今天
				arr = getTimeByHour(24,0);
				String arrStr = JSON.toJSONString(getTimeByHour1(24));
				ro.setResponseKV("xAxis", JSONArray.toJSON(arrStr));
			}else if("-1".equals(day)){//昨天
				arr = getTimeByHour(24,-1);
				String arrStr = JSON.toJSONString(getTimeByHour1(24));
				ro.setResponseKV("xAxis", JSONArray.toJSON(arrStr));
			}else{
				arr = getXValue(new Integer(Math.abs(new Integer(day).intValue())).intValue());
				String arrStr = JSON.toJSONString(getXValue(new Integer(Math.abs(new Integer(day).intValue())).intValue()));
				ro.setResponseKV("xAxis", JSONArray.toJSON(arrStr));
			}
//            if(map!=null&&map.size()>0){
	    		String[] dataArr = new String[arr.length];
    			for(int i=0;i<arr.length;i++){
    				if(!map.containsKey(arr[i])){
    	    			dataArr[i] = "0";
    	    		}else{
    	    			dataArr[i] = new BigDecimal(map.get(arr[i])).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    	    		}
    			}
    			String dataArrStr = JSON.toJSONString(dataArr);
    			ro.setResponseKV("key", JSONArray.toJSON(dataArrStr));//
//	    	}
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
                e.printStackTrace();
            }
        }
        response.getWriter().write(ro.toString());

    }
    
    public static String[] getTimeByHour(int hour,int day) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH");
		String[] arr = new String[hour];
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
    	calendar.setTime(date);
    	calendar.add(calendar.DATE,day);//把日期往后增加一天.整数往后推,负数往前移动
    	calendar.set(Calendar.HOUR_OF_DAY, 23);
    	calendar.set(Calendar.MINUTE, 59);
    	calendar.set(Calendar.SECOND, 59);
	    for(int i=1;i<=hour;i++){
	    	if(i==1){
	    		calendar.add(Calendar.HOUR, 0);
	        	arr[hour-i]=sf.format(calendar.getTime());
			}else{
				calendar.add(Calendar.HOUR, -1);
	        	arr[hour-i]=sf.format(calendar.getTime());
			}
	    	
	    }
	
	    return arr;
	
	}
	public static String[] getTimeByHour1(int hour) {
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		String[] arr = new String[hour];
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 23);
    	calendar.set(Calendar.MINUTE, 59);
    	calendar.set(Calendar.SECOND, 59);
	    for(int i=1;i<=hour;i++){
	    	if(i==1){
	    		calendar.add(Calendar.HOUR, 0);
	        	arr[hour-i]=sf.format(calendar.getTime());
			}else{
				calendar.add(Calendar.HOUR, -1);
	        	arr[hour-i]=sf.format(calendar.getTime());
			}
	    	
	    }
	
	    return arr;
	
	}

	 private static String[] getXValue(int days){
	    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	    	String[] arr = new String[days];
	    	Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
	    	for(int i=1;i<=days;i++){
	    		if(i==1){
	    			calendar.add(Calendar.DAY_OF_MONTH, 0);
	        		date = calendar.getTime();
	        		arr[days-i]=sf.format(date);
	    		}else{
	    			
	    			calendar.add(Calendar.DAY_OF_MONTH, -1);
	    			date = calendar.getTime();
	    			arr[days-i]=sf.format(date);
	    		}
	    	}
	    	return arr;
	    }
}
