package com.yyuap.mkb.bot;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

public class KBBotManager {

    String url = "http://www.tuling123.com/v1/setting/importfaq";

    // {
    // "apikey":"图灵apikey（32位）",
    // "data":[
    // {"question":"测试问题1","answer":"测试答案1"},
    // {"question":"测试问题2","answer":"测试答案2"}
    // ]
    // }

    private MKBHttpClient getMKBHttpClientInstance() {
        MKBHttpClient httpclient = new MKBHttpClient();
        return httpclient;
    }

    public void addKB(String q, String a, String apiKey, JSONObject param) {
        // MKBHttpClient httpclient = this.getMKBHttpClientInstance();
        //
        // Map<String, String> createMap = new HashMap<String, String>();
        // createMap.put("apiKey", apiKey);
        //
        // List<NameValuePair> list = new ArrayList<NameValuePair>();
        // list.add(new BasicNameValuePair("aaa","xxx") );
        // createMap.put("data", list);
        //
        // String charset = "utf-8";

        JSONObject json = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("apiKey", apiKey);
        JSONObject obj = new JSONObject();
        obj.put("question", q);
        obj.put("answer", a);
        JSONArray array = new JSONArray();
        array.add(obj);
        jsonParam.put("data", array);
String d = "";
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        //httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8"); 
        StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        //entity.setContentType("text/json");
        //entity.setContentType("application/x-www-form-urlencoded;charset=utf-8");
        
        httpPost.setEntity(entity);
        try {
            HttpResponse result = httpClient.execute(httpPost);

            // 请求结束，返回结果
            // 示例的返回为：{"code":0,"data":"2"}
            String resData = EntityUtils.toString(result.getEntity());
            JSONObject resJson = JSONObject.parseObject(resData);
            String code = resJson.getString("code"); // 返回状态码数据：0成功 1失败
            String data = resJson.getString("data"); // 成功：数据添加成功条数 失败：失败原因的描述
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
