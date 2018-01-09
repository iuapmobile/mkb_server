package com.yyuap.mkb.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultObject {

    private final String RESPONSE = "response";
    private final String RESPONSEHEADER = "responseHeader";

    private final String RESPONSEHEADER_REASON = "reason";
    private final String RESPONSEHEADER_STATUS = "status";
    private final String RESPONSE_BOTRESPONSE = "botResponse";
    private final String RESPONSE_NUMFOUND = "numFound";
    private final String RESPONSE_START = "start";

    JSONObject _ret = new JSONObject();
    JSONObject _response = new JSONObject();
    JSONObject _resHeader = new JSONObject();
    JSONObject _botResponse = new JSONObject();

    JSONArray docs = new JSONArray();
    JSONObject param = new JSONObject();

    public ResultObject() {
        _resHeader.put("status", "0");
        _resHeader.put("param", param);
        _ret.put("responseHeader", _resHeader);

        _response.put("docs", docs);
        _response.put(this.RESPONSE_BOTRESPONSE, _botResponse);
        _ret.put("response", _response);
    }

    public void set(JSONObject ret) {
        // 相当于重新构建ResultObject对象
        this._ret = ret;

        this._response = ret.getJSONObject(this.RESPONSE);

        this._resHeader = ret.getJSONObject(this.RESPONSEHEADER);

        this._botResponse = this._response.getJSONObject(this.RESPONSE_BOTRESPONSE);
        this.init();
    }

    private void init() {
        if (this._ret == null) {
            this._ret = new JSONObject();
        }
        if (this._response == null) {
            this._response = new JSONObject();
            this._ret.put(this.RESPONSE, this._response);
        }

        if (this._resHeader == null) {
            this._resHeader = new JSONObject();
            this._ret.put(this.RESPONSEHEADER, this._resHeader);
        }
        if (this._botResponse == null) {
            this._botResponse = new JSONObject();
            this._response.put(this.RESPONSE_BOTRESPONSE, this._botResponse);
        }
    }

    public JSONObject get() {
        return this._ret;
    }

    // public JSONObject getResponse() {
    // return this._response;
    // }
    //
    // public void setResponse(JSONObject value) {
    // this._response = value;
    // }

    public JSONObject getBotResponse() {
        return this._botResponse;
    }

    public void setBotResponse(JSONObject value) {
        this._botResponse = value;
        this._response.put(this.RESPONSE_BOTRESPONSE, this._botResponse);
    }

    public JSONObject getResponseHeader() {
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

    public void setBotResponseKV(String key, String value) {
        this._botResponse.put(key, value);
    }

    public void setStatus(int val) {
        _resHeader.put(this.RESPONSEHEADER_STATUS, val);
    }

    public void setReason(String reason) {
        this._resHeader.put(this.RESPONSEHEADER_REASON, reason);
    }

    public void setResponseKV(String key, Object value) {
        if (key == null) {
            return;
        }
        if (key.equals(this.RESPONSE_BOTRESPONSE)) {
            this._botResponse = (JSONObject) value;
        } else {
            this._response.put(key, value);
        }
    }

    public void setResponseHeaderKV(String key, Object value) {
        this._resHeader.put(key, value);
    }

    public String getResutlString() {
        return _ret.toString();
    }

    public void setDocs(JSONArray jsonArray) {
        this._response.put("docs", jsonArray);

    }

    public void getResponseData() {
        this._response.getJSONObject("data");
    }

    public void setResponseData(JSONObject obj) {
        this._response.put("data", obj);
    }

    public String toString() {
        return this._ret.toString();

    }
}
