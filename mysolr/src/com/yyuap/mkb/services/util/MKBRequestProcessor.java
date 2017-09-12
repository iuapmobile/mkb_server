package com.yyuap.mkb.services.util;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public class MKBRequestProcessor {

    public JSONObject readJSON4JSON(HttpServletRequest request) {
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

    public JSONObject readJSON4Form_urlencoded(HttpServletRequest request) {
        JSONObject param = new JSONObject();

        String q = request.getParameter("q");
        //加个  q  不等于空的判断
        if(q!=null && !"".equals(q)){
        	if (request.getMethod() != null && request.getMethod().toLowerCase().equals("get")) {
                try {
                    q = java.net.URLDecoder.decode(q, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        param.put("q", q);

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);

        String bot = request.getParameter("bot");
        param.put("bot", bot);

        String buserid = request.getParameter("buserid");
        param.put("buserid", buserid);

        String rows = request.getParameter("rows");
        param.put("rows", rows);

        String start = request.getParameter("start");
        param.put("start", start);
        
        String dailog = request.getParameter("dailog");
        param.put("dailog", dailog);
        
        String dailogid = request.getParameter("dailogid");
        param.put("dailogid", dailogid);

        return param;
    }

}
