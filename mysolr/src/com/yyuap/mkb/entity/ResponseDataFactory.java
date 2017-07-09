package com.yyuap.mkb.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResponseDataFactory {
    private JSONObject responseHeader = new JSONObject();
    private JSONObject response = new JSONObject();
    private JSONArray docs = null;
    private String status;

    public ResponseDataFactory() {
        this.responseHeader = new JSONObject();
        this.response = new JSONObject();
        this.docs = new JSONArray();

        responseHeader.put("status", 0);
    }

    public void setStatus(int val) {
        responseHeader.put("status", val);
    }

    public String getStatus() {
        return this.status;
    }

    public ResponseData CreateResponseData(int i) {
        if (i == 0) {
            return new ResponseData();
        } else if (i == 2) {
            return null;
        }
        return null;
    }
}
