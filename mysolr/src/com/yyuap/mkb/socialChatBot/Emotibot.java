package com.yyuap.mkb.socialChatBot;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Emotibot {

    public String chat(String url, Map<String, String> mapParms) {
        String ret = "";
        String botRes = openapi(url, mapParms, null, null);
        ret = botRes;
        JSONObject obj = JSONObject.parseObject(botRes);
        String status = obj.getString("return");
        if (status.equals("0")) {
            JSONArray data = obj.getJSONArray("data");
            if (data.size() > 0) {
                String answer = data.getJSONObject(0).getString("value");
                // text = "您是要问我知识库以外的问题？好吧，我想说的是" + text;
                System.out.println("zhujian answer:" + answer);
                // return obj;
                
                
            }
        }
        return ret;
    }

    public String openapi(String url, Map<String, String> mapParms, String charset, String ContentType) {
        /*
         * http 请求方式: POST URL: http://idc.emotibot.com/api/ApiKey/openapi.php
         * POST 数据格式：以表单的方式提交 POST数据例子：array( "cmd" => "chat", "appid" =>
         * "用户AppId", "userid" => "用户编号", "text" => "用户问句", "location" =>
         * "获取访问当前的地址", "iformat" => "向服务发送的文件类型", "oformat" => "期望服务回复的文件类型" )
         */

        try {
            // 如果是机器人请求
            MKBHttpClient mkbHttpClient = new MKBHttpClient();
            //
            // Map<String, String> createMap = new HashMap<String, String>();
            // createMap.put("cmd", "chat");
            // createMap.put("appid", "ac374de14a6ae3c15126430f6cf539c9 ");
            // createMap.put("userid", "10110");
            // createMap.put("text", text);
            // createMap.put("location", "");
            // createMap.put("iformat", "");
            // createMap.put("oformat", "");

            if (ContentType == null || ContentType.equals("")) {
                ContentType = "application/x-www-form-urlencoded";
            }
            if (charset == null || charset.equals("")) {
                charset = "utf-8";
            }

            String botRes = mkbHttpClient.doPost(url, mapParms, charset, ContentType);
            return botRes;

        } catch (Exception e) {
            throw e;
        }
    }
}
