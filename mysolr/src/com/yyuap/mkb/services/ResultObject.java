package com.yyuap.mkb.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultObject {

    private static final String RESPONSEHEADER_REASON = "reason";
    private static final String RESPONSEHEADER_STATUS = "status";
    private static final String RESPONSE_BOTRESPONSE = "botResponse";
    private static final String RESPONSE_NUMFOUND = "numFound";
    private static final String RESPONSE_START = "start";
    JSONObject _ret = new JSONObject();
    JSONObject _response = new JSONObject();
    JSONObject _resHeader = new JSONObject();
    JSONArray docs = new JSONArray();
    JSONObject param = new JSONObject();

    public ResultObject() {
        _resHeader.put("status", "0");
        _resHeader.put("param", param);
        _ret.put("responseHeader", _resHeader);

        _response.put("docs", docs);
        _ret.put("response", _response);
    }

    private JSONObject getResponse() {
        return this._response;
    }

    private JSONObject getResponseHeader() {
        return this._resHeader;
    }

    public JSONObject getResult() {
        return this._ret;
    }

    public void setNumFound(int value) {
        this._response.put(this.RESPONSE_NUMFOUND, value);
    }

    public void setStart(int value) {
        this._response.put(this.RESPONSE_START, value);
    }

    public void setBotResponse(JSONObject value) {
        this._response.put(this.RESPONSE_BOTRESPONSE, value);
    }

    public void setStatus(int val) {
        _resHeader.put(this.RESPONSEHEADER_STATUS, val);
    }

    public void setReason(String reason) {
        this._resHeader.put(this.RESPONSEHEADER_REASON, reason);
    }

    public void setResponseKV(String key, Object value) {
        this._response.put(key, value);
    }

    public void setResponseHeader(String key, Object value) {
        this._resHeader.put(key, value);
    }

    public String toString() {
        return _ret.toString();
    }

    public void setDocs(JSONArray jsonArray) {
        this._response.put("docs", jsonArray);

    }
}
