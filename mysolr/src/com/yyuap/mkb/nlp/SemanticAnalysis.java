package com.yyuap.mkb.nlp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.log.MKBLogger;

public class SemanticAnalysis {
    public String getKeywords(SAConfig conf){
        String httpUrl = conf.HTTPSERVICEURL;
        String httpArg = conf.httpArg;
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("DEVELOP-KEY", conf.DEVELOPKEY);
            connection.setDoOutput(true);
            connection.getOutputStream().write(httpArg.getBytes("UTF-8"));
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            
        } catch (Exception e) {
        	MKBLogger.info("SemanticAnalysis simnet Exception:" + e.toString());
        }
        MKBLogger.info(result);
       
        return result;
    }
}
