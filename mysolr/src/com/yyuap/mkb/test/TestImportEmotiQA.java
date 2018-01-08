package com.yyuap.mkb.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

public class TestImportEmotiQA {
    private static final String BOTURL = "http://idc.emotibot.com/api/ApiKey/openapi.php";
    private static final String URL_GETQA = "https://api.emotibot.com/bf/ubt/v1/ssm/sqs";
    private static final String URL = "https://api.emotibot.com/bf/ubt/v1/ssm/sqs";

    public static void getQA() {
        /*
         * http 请求方式: POST URL: http://idc.emotibot.com/api/ApiKey/openapi.php
         * POST 数据格式：以表单的方式提交 POST数据例子：array( "cmd" => "chat", "appid" =>
         * "用户AppId", "userid" => "用户编号", "text" => "用户问句", "location" =>
         * "获取访问当前的地址", "iformat" => "向服务发送的文件类型", "oformat" => "期望服务回复的文件类型" )
         */

        try {
            // 如果是机器人请求

            // String ContentType = "application/x-www-form-urlencoded";

            String charset = "utf-8";

            Header h1 = new Header();
            h1.setName("Authorization");
            h1.setValue("");

            Header h2 = new Header();
            h2.setName("AppId");
            h2.setValue("ac374de14a6ae3c15126430f6cf539c9");

            ArrayList<Header> headers = new ArrayList<Header>();
            headers.add(h1);
            headers.add(h2);

            String botRes = doHttpGet(URL, headers, null, charset);

            JSONObject obj = JSONObject.parseObject(botRes);
            String status = obj.getString("return");
            if (status.equals("0")) {
                JSONArray data = obj.getJSONArray("data");
                if (data.size() > 0) {
                    String answer = data.getJSONObject(0).getString("value");
                    // text = "您是要问我知识库以外的问题？好吧，我想说的是" + text;
                    MKBLogger.info("answer:" + answer);
                    // return obj;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static String doHttpGet(String url, ArrayList<Header> headers, Map<String, String> map, String charset) {
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpGet = new HttpGet(url);

            // 处理HEADER
            for (Header h : headers) {
                httpGet.setHeader(h.getName(), h.getValue());
            }



            // 设置参数
//            List<NameValuePair> list = new ArrayList<NameValuePair>();
//            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Entry<String, String> item = (Entry<String, String>) iterator.next();
//                list.add(new BasicNameValuePair(item.getKey(), item.getValue()));
//            }
//            if (list.size() > 0) {
//                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
//                httpGet.setParams(arg0);(entity);
//            }

            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void add() {
        /*
         * http 请求方式: POST URL: http://idc.emotibot.com/api/ApiKey/openapi.php
         * POST 数据格式：以表单的方式提交 POST数据例子：array( "cmd" => "chat", "appid" =>
         * "用户AppId", "userid" => "用户编号", "text" => "用户问句", "location" =>
         * "获取访问当前的地址", "iformat" => "向服务发送的文件类型", "oformat" => "期望服务回复的文件类型" )
         */

        try {
            // 如果是机器人请求
            MKBHttpClient httpclient = new MKBHttpClient();

            Map<String, String> createMap = new HashMap<String, String>();
            createMap.put("cmd", "chat");
            createMap.put("appid", "ac374de14a6ae3c15126430f6cf539c9 ");
            createMap.put("userid", "10110");
            createMap.put("text", "你叫什么");
            // createMap.put("location", "");
            // createMap.put("iformat", "");
            // createMap.put("oformat", "");

            String ContentType = "application/x-www-form-urlencoded";

            String charset = "utf-8";

            String botRes = httpclient.doPost(BOTURL, createMap, charset, ContentType);

            JSONObject obj = JSONObject.parseObject(botRes);
            String status = obj.getString("return");
            if (status.equals("0")) {
                JSONArray data = obj.getJSONArray("data");
                if (data.size() > 0) {
                    String answer = data.getJSONObject(0).getString("value");
                    // text = "您是要问我知识库以外的问题？好吧，我想说的是" + text;
                    MKBLogger.info("answer:" + answer);
                    // return obj;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
