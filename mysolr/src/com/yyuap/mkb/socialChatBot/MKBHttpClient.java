package com.yyuap.mkb.socialChatBot;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.yyuap.mkb.log.MKBLogger;

//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;

public class MKBHttpClient {
    public String doPost(String url, Map<String, String> map, String charset) {
        return this.doPost(url, map, charset, null);
    }

    public String doPost(String url, Map<String, String> map, String charset, String ContentType) {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            if (ContentType != null && !ContentType.equals("")) {
                httpPost.setHeader("Content-Type", ContentType);
            }
            // 设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> item = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
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
    
    public String doHttpPost(String url, Map<String, String> map, String charset, String ContentType,String developKey) {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            if (ContentType != null && !ContentType.equals("")) {
                httpPost.setHeader("Content-Type", ContentType);
            }
            httpPost.setHeader("DEVELOP-KEY", developKey);
            // 设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> item = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
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
    
    @SuppressWarnings("finally")
	public String doHttpGet(String url, Object... get_param) {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String coururl = MessageFormat.format(url, get_param);
		HttpGet httpGet = new HttpGet(coururl);
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				result = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			MKBLogger.error("Exception:" + e.toString());
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				MKBLogger.error("Exception:" + e.toString());
			}
			return result;
		}
	}   

}
