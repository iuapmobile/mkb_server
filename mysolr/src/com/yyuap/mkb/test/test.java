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
import java.util.regex.Pattern;

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
import com.yyuap.mkb.nlp.BaiAdapter;
import com.yyuap.mkb.turbot.MKBHttpClient;

//develop
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
        //test();
        //相似度测试
        simnet.test();
    }

    public static void test() throws IOException {

        // String reg = "((?<=(什么|啥))[^的？\\?]+)";
        // Pattern pattern = Pattern.compile(reg);
        // java.util.regex.Matcher m = pattern.matcher("这个是什么牌子的？");
        // while (m.find()) {
        // System.out.println(m.group());// 输出“牌子”
        // String xxxx1 = m.group();
        // String xxxx2 = m.group(0);
        // }
        //
        // String reg2 = "((?<=(给))[^拨打|打电话\\?]+)";
        // Pattern pattern2 = Pattern.compile(reg2);
        // java.util.regex.Matcher m2 = pattern2.matcher("给姚磊打电话？");
        //

        ArrayList<String> list = new ArrayList<String>();
        list.add("云打印");
        list.add("云端打电话挺好");
        list.add("呼叫");
        list.add("呼");
        list.add("呼啊");
        list.add("云打印姚磊");
        list.add("通过嘟嘟打电话给姚磊一直挺方便的");
        list.add("云端打电话给姚磊挺好");
        list.add("云端打电话姚磊挺好");
        list.add("云端打姚磊电话挺好");
       
        list.add("呼叫下姚磊啊。");
        list.add("呼叫一下姚磊啊。");
        list.add("呼叫,姚磊啊~");
        list.add("呼叫,姚磊。");
        list.add("呼叫姚磊。");
        list.add("呼叫姚磊         ");
        list.add("呼叫姚磊         ");
        list.add("呼叫,姚磊！");
        list.add("呼叫,姚磊～");
        list.add("呼叫,姚磊 ");
        list.add("呼叫，姚磊呀...");
        list.add("呼叫姚磊呗！");
        list.add("呼叫！姚磊哦");
        list.add("呼叫姚！磊！。");
        list.add("呼叫姚磊打个电话");
        list.add("呼[姚磊]个电话");
        list.add("呼叫姚磊电话");
        list.add("呼叫姚磊吧");
        list.add("呼叫姚磊");

        list.add("给姚磊打个电话");
        list.add("给姚磊打电话");
        list.add("给姚磊打手机");
        list.add("给姚磊打一个电话");
        list.add("给姚磊打个移动电话");

        list.add("打电话给姚磊挺方便");
        list.add("打电话给姚磊");
        list.add("打电话给姚磊吧");
        list.add("给姚磊的手机打个电话");
        list.add("给姚磊手机打个电话");
        list.add("给姚磊手机打电话");
        list.add("拨打电话给姚磊");
        list.add("拨电话给姚磊");

        list.add("打姚磊的手机");
        list.add("打姚磊手机");
        list.add("打姚磊电话");
        list.add("拨姚磊的电话");
        list.add("拨姚磊电话");
        list.add("打姚磊电话");
        list.add("请拨姚磊电话");
        list.add("拨打姚磊的电话");
        list.add("拨打姚磊电话");

        for (int i = 0, len = list.size(); i < len; i++) {
            String text = list.get(i);
            System.out.println(i + ":");// 输出
         
            predictIntent(text);
        }
        System.out.println("-----------------end");// 输出
    }

    public static void predictIntent(String text) {

        // try {
        // JSONObject obj = fenci(text);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        boolean ok = false;
        if ((text.contains("呼") && !text.endsWith("呼") && !text.endsWith("呼叫"))
                || (text.contains("呼叫") && !text.endsWith("呼叫"))) {
            ok = true;
        } else if (text.contains("打电话")) {
            ok = true;
        } else if (text.contains("打手机")) {
            ok = true;
        } else if (text.contains("拨")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        } else if (text.contains("打")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        } else if (text.contains("call")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        }
        if (!ok) {
            System.out.println(text + "*****************非打电话场景");
            System.out.println("");
            return;
        }
        ArrayList<String> regList = new ArrayList<String>();
        regList.add("((?<=(呼叫[一]?下))[^(吧|啊|呀|呗|哦)\\?(打)\\?(个)\\?电话]+)");
        regList.add("((?<=(呼叫))[^(吧|啊|呀|呗|哦)\\?(打)\\?(个)\\?电话]+)");
        regList.add("((?<=(呼))[^个电话]+)");
        // regList.add("((?<=(呼叫))[^(吧|啊|呀)\\?]+)");
        regList.add("((?<=(呼叫))[个\\?|打\\?]+)");
        regList.add("((?<=(呼叫))[^吧|电话]+)");
        regList.add("((?<=(呼叫))[^个电话\\?]+)");
        regList.add("((?<=(呼叫))[^(的手机)?打|拨打.*电话\\?]+)");

        regList.add("((?<=(给))[^(的手机)\\?打|拨打.*电话\\?(吧)\\?]+)");
        // regList.add("((?<=(拨打))[^(的电话|的手机|手机|电话)\\+]\\+)");
        regList.add("((?<=(拨打))[^(电话|手机|的手机|的电话)\\?]+)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+(给)\\?)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+)");

        regList.add("((?<=(呼|打))[^(电话|手机|的手机|的电话)\\?]?)");
        int index = 0;
        for (String reg : regList) {
            Pattern pattern = Pattern.compile(reg);
            String newtext = characterFormat(text);
            java.util.regex.Matcher m = pattern.matcher(newtext);
            boolean has = false;
            while (m.find()) {

                String name = m.group();
                if (name.length() == 0) {
                    System.out.println(text + "*****************非打电话场景");
                    System.out.println("");
                    continue;
                }
                System.out.println((name.equals("姚磊") ? "" : "**********Err:") + text + "====>" + name
                        + "                 reg" + index + ":" + reg);// 输出
                System.out.println();
                has = true;
            }
            if (has) {
                break;
            }

            index++;
        }

    }

    public static ArrayList<String> ik(String text) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        StringReader sr = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        while ((lex = ik.next()) != null) {
            System.out.print(lex.getLexemeText() + "|");
            ret.add(lex.getLexemeText());
        }
        System.out.println("");
        return ret;
    }

    public static String characterFormat(String s) {
        String str = s.replaceAll("[`～~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    public static void test2() throws IOException {
        String reg = "((?<=(什么|啥))[^的？\\?]+)";
        Pattern pattern = Pattern.compile(reg);
        java.util.regex.Matcher m = pattern.matcher("这个是什么牌子的？");
        while (m.find()) {
            System.out.println(m.group());// 输出“牌子”
            String xxxx1 = m.group();
            String xxxx2 = m.group(0);
        }

        String reg2 = "((?<=(给))[^打电话？\\?]+)";
        Pattern pattern2 = Pattern.compile(reg);
        java.util.regex.Matcher m2 = pattern.matcher("给姚磊打电话？");
        while (m2.find()) {
            System.out.println(m2.group());// 输出“牌子”
            String xxxx1 = m2.group();
            String xxxx2 = m2.group(0);
        }

        // if (q.contains("打") && q.contains("电话")) {
        //
        // }

        ArrayList<String> list = new ArrayList<String>();
        list.add("给姚磊打个电话");
        list.add("打电话给姚磊");
        list.add("给姚磊拨个电话");
        list.add("拨电话给姚磊");
        // list.add("用户如何填已有的表单？PC端");
        // list.add("用户如何填已有的表单？PC端.");
        // list.add("你好,用户怎么填已有的表单？PC端。");
        // list.add("用户怎么填已有的表单？PC端");

        // list.add("你好,用户如何填已有的表单，PC端");
        // list.add("你好,用户如何填PC端已有的表单");
        // list.add("你好,用户怎么在PC端填写已有的表单");
        // list.add("你好,用户怎样在PC端填报已有的表单");
        // list.add("你好,用户怎么在PC端填入已有的表单");
        // list.add("你好,用户怎么在PC端填已有表单");

        // list.add("今天天气怎么样");
        // list.add("我有一张单据如何保存");
        // list.add("填写报销单的时候我怎么才能保证填写正确");
        // list.add("怎么在PC端用表单来填写已有的表单");
        // list.add("怎么在PC端用填已有的单据");
        // list.add("怎么在PC端用户填写已有的单子");
        // list.add("在PC端用户怎么填写已有的表单");
        // list.add("用户在PC端怎么填已有的单据");
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

        // String url =
        // "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_sim?access_token="
        // + access_token;
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet?access_token=" + access_token;
        url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?access_token=" + access_token;

        try {

            for (int i = 0; i < list.size(); i = i + 1) {
                org.apache.commons.httpclient.methods.PostMethod post = new PostMethod(url);
                RequestEntity re;

                JSONObject json = new JSONObject();
                json.put("text_1", "你好,用户如何填已有的表单？PC端");
                json.put("text_2", list.get(i));
                // json.put("text_1", "浙富股份");
                // json.put("text_2", "万事通自考网");
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

    public static String getBaiDuAccess_Token() {

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
        return access_token;
    }

    public static JSONObject fenci(String text) throws HttpException, IOException {
        String token = getBaiDuAccess_Token();
        return bdPost(text, token);
    }

    public static JSONObject bdPost(String text, String access_token) throws HttpException, IOException {
        // String url =
        // "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet?access_token=" +
        // access_token;
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?access_token=" + access_token;

        org.apache.commons.httpclient.methods.PostMethod post = new PostMethod(url);
        RequestEntity re;

        JSONObject json = new JSONObject();
        json.put("text", text);
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
        return jsonRet;
    }
    
    
   

}


