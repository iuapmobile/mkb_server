package com.yyuap.mkb.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.turbot.MKBHttpClient;

public class BaiAdapter {
    public BaiAdapter(){
        
    }
    public  float simnet(String text_1,String text_2)  {
        float score =0;
        MKBHttpClient httpclient = new MKBHttpClient();
        Map<String, String> createMap = new HashMap<String, String>();
        createMap.put("grant_type", "client_credentials");
        createMap.put("client_id", "ah7yAv2YGjEyYNZEUdsrVNfG");
        createMap.put("client_secret", "ct5N3lbEnqGgkrZBRbB0TbOKhrgQ2eXj");
        String charset = "gbk";
        String tokenURL = "https://aip.baidubce.com/oauth/2.0/token";

        String botRes = httpclient.doPost(tokenURL, createMap, charset, null);
        JSONObject obj = JSONObject.parseObject(botRes);
        String access_token = obj.getString("access_token");
        System.out.println("access_token: " + access_token);

       
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet?access_token="+ access_token;
       
        
        try {
           
            
                org.apache.commons.httpclient.methods.PostMethod post = new PostMethod(url);
                RequestEntity re;
                
                JSONObject json = new JSONObject();
                json.put("text_1", text_1);
                json.put("text_2", text_2);
                
                String str = json.toString();
                re = new StringRequestEntity(str, "application/json", "gbk");

                post.setRequestEntity(re);

                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
                client.getHttpConnectionManager().getParams().setSoTimeout(10000);
                client.executeMethod(post);

                String dataStr = getRespMsgByBodyStream(post.getResponseBodyAsStream());
                
                JSONObject jsonRet = JSONObject.parseObject(dataStr);
               score =  jsonRet.getFloatValue("score");
             
                System.out.println(dataStr);
                
                return score;
                
                
                
            
        } catch (UnsupportedEncodingException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

        return score;

    }

    public static String getRespMsgByBodyStream(InputStream in) {
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(in, "GBK");
            br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // EMLogger.info("http response is: " + sb.toString());
            return sb.toString();
        } catch (IOException e) {
            // EMLogger.info("io exception when send http." + e);
        } finally {
            // FileUtil.closeStream(in);
            // FileUtil.closeStream(isr);
            // FileUtil.closeStream(br);
        }

        return "";
    }

}
