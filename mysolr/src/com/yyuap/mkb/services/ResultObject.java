package com.yyuap.mkb.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultObject {

    JSONObject ret = new JSONObject();
    JSONObject res = new JSONObject();
    JSONObject resHeader = new JSONObject();
    JSONArray docs = new JSONArray();

    public ResultObject() {
        resHeader.put("status", "0");
        ret.put("responseHeader", resHeader);

        res.put("docs", docs);
        ret.put("response", res);
    }

    public void setStatus(int val) {
        resHeader.put("status", val);
    }

    public JSONObject getResponse() {
        return this.res;
    }

    public JSONObject getResponseHeader() {
        return this.resHeader;
    }

    public JSONObject getResult() {
        return this.ret;
    }

    public String toString() {
        return ret.toString();
    }
}
