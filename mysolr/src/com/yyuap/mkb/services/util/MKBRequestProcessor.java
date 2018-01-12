package com.yyuap.mkb.services.util;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.log.MKBLogger;

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
            MKBLogger.info(e.toString());
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
        // 加个 q 不等于空的判断
        if (q != null && !"".equals(q)) {
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

        String isObserver = request.getParameter("isObserver");
        param.put("isObserver", isObserver);

        String apiKey = request.getParameter("apiKey");
        param.put("apiKey", apiKey);

        String bot = request.getParameter("bot");
        param.put("bot", bot);

        String buserid = request.getParameter("buserid");
        param.put("buserid", buserid);

        String userid = request.getParameter("userid");
        param.put("userid", userid);

        String rows = request.getParameter("rows");
        param.put("rows", rows);

        String start = request.getParameter("start");
        param.put("start", start);

        // String dailog = request.getParameter("dailog");
        // param.put("dailog", dailog);
        //
        // String dialog = request.getParameter("dialog");
        // param.put("dialog", dialog);
        // if (dialog != null) {
        // param.put("dailog", dialog);
        // }

        String dailogid = request.getParameter("dailogid");
        param.put("dailogid", dailogid);
        param.put("dialogid", dailogid);

        // dialogid优先级高于dailogid
        String dialogid = request.getParameter("dialogid");
        if (dialogid != null) {
            param.put("dailogid", dialogid);
            param.put("dialogid", dialogid);
        }
        // 部门
        String dept = request.getParameter("dept");
        param.put("dept", dept);

        // 起始时间
        String startDate = request.getParameter("startDate");
        param.put("startDate", startDate);

        // 结束时间
        String endDate = request.getParameter("endDate");
        param.put("endDate", endDate);

        // 人员
        String people = request.getParameter("people");
        param.put("people", people);

        // debug flag
        String __debug = request.getParameter("__debug");
        param.put("__debug", __debug);

        return param;
    }

}

