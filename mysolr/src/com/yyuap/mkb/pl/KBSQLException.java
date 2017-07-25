package com.yyuap.mkb.pl;

import java.sql.SQLException;

import com.alibaba.fastjson.JSONObject;

public class KBSQLException extends SQLException {
    JSONObject extData = new JSONObject();

    public KBSQLException(String reason) {
        // TODO Auto-generated constructor stub
        super(reason);
    }

    protected int kbExceptionCode = 1000;

    public int getKBExceptionCode() {
        return this.kbExceptionCode;
    }

    public void put(String key, String value) {
        extData.put(key, value);
    }

    public Object get(String key) {
        return extData.get(key);
    }

    public String getString(String key) {
        return extData.getString(key);
    }

    public JSONObject getExtData() {
        return this.extData;
    }

    public void setExtData(JSONObject json) {
        this.extData = json;
    }

}
