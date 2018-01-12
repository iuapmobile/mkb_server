package com.yyuap.mkb.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

class BaiAdapter {
    // 文档http://ai.baidu.com/docs#/Auth/top
    public static final String tokenURL = "https://aip.baidubce.com/oauth/2.0/token";
    public static final String grant_type = "client_credentials";
    public static final String client_id = "ah7yAv2YGjEyYNZEUdsrVNfG";
    public static final String client_secret = "ct5N3lbEnqGgkrZBRbB0TbOKhrgQ2eXj";

    public static final String simnet_url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet";

    public BaiAdapter() {

    }

    public static float simnet(String text_1, String text_2) {
        float score = 0;
        try {
            MKBHttpClient httpclient = new MKBHttpClient();
            Map<String, String> createMap = new HashMap<String, String>();
            createMap.put("grant_type", grant_type);
            createMap.put("client_id", client_id);
            createMap.put("client_secret", client_secret);
            String charset = "gbk";

            String botRes = httpclient.doPost(tokenURL, createMap, charset, null);
            JSONObject obj = JSONObject.parseObject(botRes);
            String access_token = obj.getString("access_token");
            String expires_in = obj.getString("expires_in");
            MKBLogger.info(
                    "ai.baidu.com >>>get token finish! access_token: " + access_token + ", expires_in:" + expires_in);

            String url = simnet_url + "?access_token=" + access_token;

            PostMethod post = new PostMethod(url);
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
            score = jsonRet.getFloatValue("score");
            String log = String.format("+++++[MKBBOT] com.yyuap.mkb.nlpBaiAdapter simnet >>>q=%s，_q=%s，结果：%s", text_2,
                    text_1, dataStr);
            MKBLogger.info(log);

            return score;

        } catch (UnsupportedEncodingException e) {
            // TODO 自动生成的 catch 块
        	MKBLogger.info("BaiAdapter simnet Exception:" + e.toString());
        } catch (HttpException e) {
            // TODO 自动生成的 catch 块
            MKBLogger.error("Exception:" + e.toString());
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
        	MKBLogger.info("BaiAdapter simnet Exception:" + e.toString());
        } catch (Exception e) {
            MKBLogger.info("BaiAdapter simnet Exception:" + e.toString());
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

    public static JSONObject lexer(String text) {
        JSONObject ret = new JSONObject();
        return ret;
    }

    public static JSONObject depparser(String text) {

        JSONObject ret = new JSONObject();
        return ret;
    }

}
