package com.yyuap.mkb.socialChatBot;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TulingBot {

    public JSONObject chat(String botURL, Map<String, String> mapParms, String charset) {
        try {

            MKBHttpClient httpclient = new MKBHttpClient();
            if (charset == null || charset.equals("")) {
                charset = "utf-8";
            }

            String botRes = httpclient.doPost(botURL, mapParms, charset);

            JSONObject obj = JSONObject.parseObject(botRes);

            return obj;
        } catch (Exception e) {
            throw e;
        }
    }
}
