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
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

public class OpenAPI {

    public OpenAPI() {

    }

    public static float simnet(String text_1, String text_2) {

        //BaiAdapter b = new BaiAdapter();
        float score = BaiAdapter.simnet(text_1, text_2);
        return score;

    }
    
  //词法分析接口
    public static JSONObject lexer(String text) {

        //BaiAdapter b = new BaiAdapter();
        JSONObject score = BaiAdapter.lexer(text);
        return score;

    }
    
    //依存句法分析接口
    public static JSONObject depparser(String text) {

        //BaiAdapter b = new BaiAdapter();
        JSONObject score = BaiAdapter.depparser(text);
        return score;

    }
  

}
