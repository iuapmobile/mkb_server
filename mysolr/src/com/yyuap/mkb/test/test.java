package com.yyuap.mkb.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.turbot.MKBHttpClient;

public class test {
    // public static void main1(String[] args) throws IOException {
    //
    //
    // String text="用户如何填已有的表单？PC端";
    // //创建分词对象
    // Analyzer anal=new IKAnalyzer(true);
    // StringReader reader=new StringReader(text);
    // //分词
    // TokenStream ts=anal.tokenStream("", reader);
    // CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
    // //遍历分词数据
    // while(ts.incrementToken()){
    // System.out.print(term.toString()+"|");
    // }
    // reader.close();
    // System.out.println();
    // }

    public static void main(String[] args) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        list.add("用户如何填已有的表单？PC端");
        list.add("用户如何填已有的表单？PC端.");
        list.add("你好,用户怎么填已有的表单？PC端。");
        list.add("用户怎么填已有的表单？PC端");
//        list.add("你好,用户如何填已有的表单，PC端");
//        list.add("你好,用户如何填PC端已有的表单");
//        list.add("你好,用户怎么在PC端填写已有的表单");
//        list.add("你好,用户怎样在PC端填报已有的表单");
//        list.add("你好,用户怎么在PC端填入已有的表单");
//        list.add("你好,用户怎么在PC端填已有表单");
        list.add("今天天气怎么样");
        list.add("我有一张单据如何保存");
        list.add("填写报销单的时候我怎么才能保证填写正确");
        list.add("怎么在PC端用表单来填写已有的表单");
        list.add("怎么在PC端用填已有的单据");
        list.add("怎么在PC端用户填写已有的单子");
        list.add("在PC端用户怎么填写已有的表单");
        list.add("用户在PC端怎么填已有的单据");
        for (String text : list) {

            StringReader sr = new StringReader(text);
            IKSegmenter ik = new IKSegmenter(sr, true);
            Lexeme lex = null;
            while ((lex = ik.next()) != null) {
                System.out.print(lex.getLexemeText() + "|");
            }
            System.out.println("");
        }

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

        //String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_sim?access_token=" + access_token;
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet?access_token="+ access_token;
        url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?access_token="+ access_token;

        
        
        try {
           
            for (int i = 0; i < list.size(); i=i+1) {
                org.apache.commons.httpclient.methods.PostMethod post = new PostMethod(url);
                RequestEntity re;
                
                JSONObject json = new JSONObject();
                json.put("text_1", "你好,用户如何填已有的表单？PC端");
                json.put("text_2", list.get(i));
//                json.put("text_1", "浙富股份");
//                json.put("text_2", "万事通自考网");
                String str = json.toString();
                re = new StringRequestEntity(str, "application/json", "gbk");

                post.setRequestEntity(re);

                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
                client.getHttpConnectionManager().getParams().setSoTimeout(10000);
                client.executeMethod(post);

                String dataStr = getRespMsgByBodyStream(post.getResponseBodyAsStream());
                
                JSONObject jsonRet = JSONObject.parseObject(dataStr);
               
                System.out.println(dataStr);
                
                
                
                
                
            }
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

        //
        // createMap = new HashMap<String, String>();
        // //createMap.put("access_token", access_token);
        // createMap.put("word_1", "填");
        // createMap.put("word_2", "填写");
        // charset = "gbk";
        // String url =
        // "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_sim?access_token=24.3d1e2de7d9d6939bcd7453eaef8e8c84.2592000.1503817075.282335-9763964";
        //
        // String content_type="application/json";
        // String botRes1 = httpclient.doPost(url, createMap,
        // charset,content_type);
        // JSONObject rettt = JSONObject.parseObject(botRes1);

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
