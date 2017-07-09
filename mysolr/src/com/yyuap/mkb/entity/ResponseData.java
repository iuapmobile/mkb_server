package com.yyuap.mkb.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResponseData {
    private JSONObject responseHeader = new JSONObject();
    private JSONObject response = new JSONObject();
    private JSONArray docs = null;
    private String status;

    public ResponseData() {
        this.responseHeader = new JSONObject();
        this.response = new JSONObject();
        this.docs = new JSONArray();

        this.responseHeader.put("status", 0);
        this.response.put("docs", this.docs);
    }

    public void setStatus(int val) {
        responseHeader.put("status", val);
    }

    public String getStatus() {
        return this.status;
    }

    public String toString() {
        JSONObject ret = new JSONObject();
        ret.put("response", this.response);
        ret.put("responseHeader", this.responseHeader);
        return ret.toString();
    }
}
